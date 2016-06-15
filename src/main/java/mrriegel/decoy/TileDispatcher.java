package mrriegel.decoy;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TileDispatcher extends TileEntity implements ITickable {
	private Set<Transfer> transfers = Sets.newHashSet();
	private Set<Pair<BlockPos, EnumFacing>> targets = Sets.newHashSet();
	private Mode mode = Mode.NF;
	private IInventory inv = new InventoryBasic(null, false, 15);

	public enum Mode {
		NF, FF, RA;

		public Mode next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		readTransfersFromNBT(compound);
		NBTTagList lis2 = compound.getTagList("lis2", 10);
		targets = Sets.newHashSet();
		for (int i = 0; i < lis2.tagCount(); i++) {
			NBTTagCompound nbt = lis2.getCompoundTagAt(i);
			targets.add(new ImmutablePair<BlockPos, EnumFacing>(BlockPos.fromLong(nbt.getLong("pos")), EnumFacing.values()[nbt.getInteger("face")]));
		}

		if (compound.hasKey("mode"))
			mode = Mode.valueOf(compound.getString("mode"));
		else
			mode = Mode.NF;
		inv = new InventoryBasic(null, false, 15);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			if (j >= 0 && j < inv.getSizeInventory()) {
				inv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		super.readFromNBT(compound);
	}

	public void readTransfersFromNBT(NBTTagCompound compound) {
		NBTTagList lis = compound.getTagList("lis", 10);
		transfers = Sets.newHashSet();
		for (int i = 0; i < lis.tagCount(); i++)
			transfers.add(Transfer.loadFromNBT(lis.getCompoundTagAt(i)));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		writeTransfersToNBT(compound);

		NBTTagList lis2 = new NBTTagList();
		for (Pair<BlockPos, EnumFacing> t : targets) {
			NBTTagCompound n = new NBTTagCompound();
			n.setLong("pos", t.getLeft().toLong());
			n.setInteger("face", t.getRight().ordinal());
			lis2.appendTag(n);
		}
		compound.setTag("lis2", lis2);

		compound.setString("mode", mode.toString());
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		compound.setTag("Items", nbttaglist);
		return super.writeToNBT(compound);
	}

	public void writeTransfersToNBT(NBTTagCompound compound) {
		NBTTagList lis = new NBTTagList();
		for (Transfer t : transfers) {
			NBTTagCompound n = new NBTTagCompound();
			t.writeToNBT(n);
			lis.appendTag(n);
		}
		compound.setTag("lis", lis);
	}

	public void deserializeTransfers(NBTTagCompound nbt) {
		this.readTransfersFromNBT(nbt);
	}

	public NBTTagCompound serializeTransfers() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeTransfersToNBT(nbt);
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	public void updateClient() {
		if (worldObj == null || worldObj.isRemote)
			return;
		WorldServer w = (WorldServer) worldObj;
		for (EntityPlayer p : w.playerEntities) {
			if (p.getPosition().getDistance(pos.getX(), pos.getY(), pos.getZ()) < 32) {
				((EntityPlayerMP) p).connection.sendPacket(getUpdatePacket());
			}
		}
	}

	void moveItems() {
		for (Transfer tr : getTransfers()) {
			tr.current = tr.current.add(tr.getVec().scale(.025 / tr.getVec().lengthVector()));
		}
	}

	void transferItems() {
		if (worldObj.getTotalWorldTime() % 30L == 0 && !worldObj.isBlockPowered(pos)) {
			EnumFacing face = worldObj.getBlockState(pos).getValue(BlockDispatcher.FACING);
			IItemHandler inv = InvHelper.getItemHandler(worldObj.getTileEntity(pos.offset(face)), face.getOpposite());
			List<Pair<BlockPos, EnumFacing>> lis = Lists.newArrayList(targets);
			switch (mode) {
			case FF:
				Collections.sort(lis, new Comparator<Pair<BlockPos, EnumFacing>>() {
					@Override
					public int compare(Pair<BlockPos, EnumFacing> o1, Pair<BlockPos, EnumFacing> o2) {
						double dis1 = pos.getDistance(o2.getLeft().getX(), o2.getLeft().getY(), o2.getLeft().getZ());
						double dis2 = pos.getDistance(o1.getLeft().getX(), o1.getLeft().getY(), o1.getLeft().getZ());
						return Double.compare(dis1, dis2);
					}
				});
				break;
			case NF:
				Collections.sort(lis, new Comparator<Pair<BlockPos, EnumFacing>>() {
					@Override
					public int compare(Pair<BlockPos, EnumFacing> o1, Pair<BlockPos, EnumFacing> o2) {
						double dis1 = pos.getDistance(o2.getLeft().getX(), o2.getLeft().getY(), o2.getLeft().getZ());
						double dis2 = pos.getDistance(o1.getLeft().getX(), o1.getLeft().getY(), o1.getLeft().getZ());
						return Double.compare(dis2, dis1);
					}
				});
				break;
			case RA:
				Collections.shuffle(lis);
				break;
			}
			for (Pair<BlockPos, EnumFacing> pair : lis)
				for (int i = 0; i < inv.getSlots(); i++) {
					if (inv.getStackInSlot(i) == null)
						continue;
					int max = 1;
					ItemStack send = inv.extractItem(i, 3, true);
					if (InvHelper.canInsert(inv, send) <= 0)
						continue;
					// System.out.println("caninsert: "+InvHelper.canInsert(inv,
					// send)+" "+send);
					ItemStack x = inv.extractItem(i, Math.min(max, InvHelper.canInsert(inv, send)), false);
					if (x != null) {
						transfers.add(new Transfer(pos, pair.getLeft(), pair.getRight(), x));
						updateClient();
						return;
					}
				}
		}
	}

	Color getColor() {
		Integer num = Integer.valueOf(pos.getX()) * 29 + Integer.valueOf(pos.getY()) * 17 + Integer.valueOf(pos.getZ()) * 67;
		return Color.getHSBColor((/* num.hashCode() */pos.toLong() % 360l) / 360f, 1, 1);
	}

	@Override
	public void update() {
		// if(worldObj.getTotalWorldTime()%20L==0)
		// System.out.println("da_ "+pos);
		boolean removed = false;
		Iterator<Pair<BlockPos, EnumFacing>> ite = targets.iterator();
		while (ite.hasNext()) {
			Pair<BlockPos, EnumFacing> pa = ite.next();
			if (!InvHelper.hasItemHandler(worldObj, pa.getLeft(), pa.getRight())) {
				ite.remove();
				removed = true;
			}
		}
		moveItems();
		if (worldObj.isRemote)
			return;

		Iterator<Transfer> it = transfers.iterator();
		while (it.hasNext()) {
			Transfer tr = it.next();
			if (tr.dis == null || !InvHelper.hasItemHandler(worldObj, tr.rec.getLeft(), tr.rec.getRight())) {
				it.remove();
				removed = true;
				continue;
			}
			if (tr.received()) {
				ItemStack rest = InvHelper.insert(worldObj.getTileEntity(tr.rec.getLeft()), tr.stack, tr.rec.getRight());
				// System.out.println("rest: "+rest);
				it.remove();
				removed = true;
			}
		}
		if (removed)
			updateClient();
		transferItems();
	}

	public Set<Transfer> getTransfers() {
		return transfers;
	}

	public void setTransfers(Set<Transfer> transfers) {
		this.transfers = transfers;
	}

	public Set<Pair<BlockPos, EnumFacing>> getTargets() {
		return targets;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public IInventory getInv() {
		return inv;
	}

	public void setInv(IInventory inv) {
		this.inv = inv;
	}

	public void setTargets(Set<Pair<BlockPos, EnumFacing>> targets) {
		this.targets = targets;
	}

}
