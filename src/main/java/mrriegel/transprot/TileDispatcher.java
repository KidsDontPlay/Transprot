package mrriegel.transprot;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

public class TileDispatcher extends TileEntity implements ITickable {
	private Set<Transfer> transfers = Sets.newHashSet();
	private Set<Pair<BlockPos, EnumFacing>> targets = Sets.newHashSet();
	private Mode mode = Mode.NF;
	private IInventory inv = new InventoryBasic(null, false, 15);
	private boolean oreDict = false, meta = true, nbt = false, white = false;

	public enum Mode {
		NF("Nearest first"), FF("Farthest first"), RA("Random");
		String text;

		Mode(String text) {
			this.text = text;
		}

		public Mode next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
	}

	public boolean canTransfer(ItemStack stack) {
		if (stack == null)
			return false;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack s = inv.getStackInSlot(i);
			if (s != null && equal(stack, s))
				return white;
		}
		return !white;
	}

	public boolean equal(ItemStack stack1, ItemStack stack2) {
		if (oreDict && equalOre(stack1, stack2))
			return true;
		if (nbt && !ItemStack.areItemStackTagsEqual(stack1, stack2))
			return false;
		if (meta && stack1.getItemDamage() != stack2.getItemDamage())
			return false;
		return stack1.getItem() == stack2.getItem();
	}

	boolean equalOre(ItemStack stack1, ItemStack stack2) {
		for (int i : OreDictionary.getOreIDs(stack1)) {
			if (Ints.asList(OreDictionary.getOreIDs(stack2)).contains(i))
				return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList lis2 = compound.getTagList("lis2", 10);
		targets = Sets.newHashSet();
		for (int i = 0; i < lis2.tagCount(); i++) {
			NBTTagCompound nbt = lis2.getCompoundTagAt(i);
			targets.add(new ImmutablePair<BlockPos, EnumFacing>(BlockPos.fromLong(nbt.getLong("pos")), EnumFacing.values()[nbt.getInteger("face")]));
		}
		NBTTagList lis = compound.getTagList("lis", 10);
		transfers = Sets.newHashSet();
		for (int i = 0; i < lis.tagCount(); i++)
			transfers.add(Transfer.loadFromNBT(lis.getCompoundTagAt(i)));

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
		if (compound.hasKey("ore"))
			oreDict = compound.getBoolean("ore");
		if (compound.hasKey("meta"))
			meta = compound.getBoolean("meta");
		if (compound.hasKey("nbt"))
			nbt = compound.getBoolean("nbt");
		if (compound.hasKey("white"))
			white = compound.getBoolean("white");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
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
		NBTTagList lis = new NBTTagList();
		for (Transfer t : transfers) {
			NBTTagCompound n = new NBTTagCompound();
			t.writeToNBT(n);
			lis.appendTag(n);
		}
		compound.setTag("lis", lis);
		compound.setTag("Items", nbttaglist);
		compound.setBoolean("ore", oreDict);
		compound.setBoolean("meta", meta);
		compound.setBoolean("nbt", nbt);
		compound.setBoolean("white", white);
		return super.writeToNBT(compound);
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

	boolean wayFree(Transfer tr) {
		if (!ConfigHandler.freeWay)
			return true;
		Vec3d p1 = new Vec3d(tr.dis);
		p1.addVector(.5, .5, .5);
		Vec3d p2 = new Vec3d(tr.rec.getLeft());
		p2.addVector(.5, .5, .5);
		Vec3d d = new Vec3d(p1.xCoord - p2.xCoord, p1.yCoord - p2.yCoord, p1.zCoord - p2.zCoord);
		d = d.scale(-1);
		d = d.normalize().scale(0.25);
		Set<BlockPos> set = Sets.newHashSet();
		while (p1.distanceTo(p2) > 0.5) {
			set.add(new BlockPos(p1));
			p1 = p1.add(d);
		}
		set.remove(tr.dis);
		set.remove(tr.rec.getLeft());
		for (BlockPos p : set)
			if (!worldObj.isAirBlock(p))
				return false;
		return true;
	}

	void moveItems() {
		for (Transfer tr : getTransfers()) {
			if (!tr.blocked && worldObj.getChunkFromBlockCoords(tr.rec.getLeft()).isLoaded()) {
				tr.current = tr.current.add(tr.getVec().scale(.025 / tr.getVec().lengthVector()));
			}
		}
	}

	boolean startTransfer() {
		if (worldObj.getTotalWorldTime() % 30L == 0 && !worldObj.isBlockPowered(pos)) {
			EnumFacing face = worldObj.getBlockState(pos).getValue(BlockDispatcher.FACING);
			if (!worldObj.getChunkFromBlockCoords(pos.offset(face)).isLoaded())
				return false;
			IItemHandler inv = InvHelper.getItemHandler(worldObj.getTileEntity(pos.offset(face)), face.getOpposite());
			if (inv == null)
				return false;
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
					if (inv.getStackInSlot(i) == null || !canTransfer(inv.getStackInSlot(i)))
						continue;
					int max = 1;
					ItemStack send = inv.extractItem(i, max, true);
					int canInsert = InvHelper.canInsert(InvHelper.getItemHandler(worldObj.getTileEntity(pair.getLeft()), pair.getRight()), send);
					if (canInsert <= 0)
						continue;
					ItemStack x = inv.extractItem(i, canInsert, true);
					if (x != null) {
						Transfer tr = new Transfer(pos, pair.getLeft(), pair.getRight(), x);
						if (!wayFree(tr))
							continue;
						if (ConfigHandler.particle)
							Transprot.DISPATCHER.sendToDimension(new ParticleMessage(pos, tr.getVec().normalize().scale(0.018)), worldObj.provider.getDimension());
						transfers.add(tr);
						inv.extractItem(i, canInsert, false);
						markDirty();
						return true;
					}
				}
		}
		return false;
	}

	Color getColor() {
		return Color.getHSBColor(((pos.hashCode() * 761) % 360l) / 360f, 1, 1);
	}

	@Override
	public void update() {
		moveItems();
		if (worldObj.isRemote)
			return;
		boolean removed = false;
		Iterator<Pair<BlockPos, EnumFacing>> ite = targets.iterator();
		while (ite.hasNext()) {
			Pair<BlockPos, EnumFacing> pa = ite.next();
			if (!InvHelper.hasItemHandler(worldObj, pa.getLeft(), pa.getRight())) {
				ite.remove();
				removed = true;
			}
		}
		boolean changed = false;
		Iterator<Transfer> it = transfers.iterator();
		while (it.hasNext()) {
			Transfer tr = it.next();
			if (tr.rec == null || !InvHelper.hasItemHandler(worldObj, tr.rec.getLeft(), tr.rec.getRight())) {
				InventoryHelper.spawnItemStack(worldObj, pos.getX() + tr.current.xCoord, pos.getY() + tr.current.yCoord, pos.getZ() + tr.current.zCoord, tr.stack);
				it.remove();
				removed = true;
				continue;
			}
			if (tr.received() && worldObj.getChunkFromBlockCoords(tr.rec.getLeft()).isLoaded()) {
				ItemStack rest = InvHelper.insert(worldObj.getTileEntity(tr.rec.getLeft()), tr.stack, tr.rec.getRight());
				if (rest != null) {
					tr.stack = rest;
					for (Transfer t : transfers) {
						if (t.rec.equals(tr.rec)) {
							if (!t.blocked)
								changed = true;
							t.blocked = true;
						}
					}
				} else {
					for (Transfer t : transfers) {
						if (t.rec.equals(tr.rec))
							t.blocked = false;
					}
					it.remove();
					removed = true;
				}
				worldObj.getTileEntity(tr.rec.getLeft()).markDirty();
			}
		}
		boolean started = startTransfer();
		if (removed || changed || started)
			updateClient();
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

	public boolean isOreDict() {
		return oreDict;
	}

	public void setOreDict(boolean oreDict) {
		this.oreDict = oreDict;
	}

	public boolean isMeta() {
		return meta;
	}

	public void setMeta(boolean meta) {
		this.meta = meta;
	}

	public boolean isNbt() {
		return nbt;
	}

	public void setNbt(boolean nbt) {
		this.nbt = nbt;
	}

	public boolean isWhite() {
		return white;
	}

	public void setWhite(boolean white) {
		this.white = white;
	}

}
