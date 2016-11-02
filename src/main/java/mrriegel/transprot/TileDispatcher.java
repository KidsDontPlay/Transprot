package mrriegel.transprot;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.transprot.Transprot.Boost;
import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TileDispatcher extends CommonTile implements ITickable {
	private Set<Transfer> transfers = Sets.newHashSet();
	private Set<Pair<BlockPos, EnumFacing>> targets = Sets.newHashSet();
	private Mode mode = Mode.NF;
	private IInventory inv = new InventoryBasic(null, false, 9);
	private boolean oreDict = false, meta = true, nbt = false, white = false, mod = false;
	private IInventory upgrades = new InventoryBasic(null, false, 1);
	private int lastInsertIndex, stockNum = 0;

	public enum Mode {
		NF("Nearest first"), FF("Farthest first"), RA("Random"), RR("Round Robin");
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
		if (oreDict && StackHelper.equalOreDict(stack1, stack2))
			return true;
		if (mod && stack1.getItem().getRegistryName().getResourceDomain().equals(stack2.getItem().getRegistryName().getResourceDomain()))
			return true;
		if (nbt && !ItemStack.areItemStackTagsEqual(stack1, stack2))
			return false;
		if (meta && stack1.getItemDamage() != stack2.getItemDamage())
			return false;
		return stack1.getItem() == stack2.getItem();
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(Transprot.instance, 0, worldObj, getX(), getY(), getZ());
		return true;
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
		inv = new InventoryBasic(null, false, 9);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			if (j >= 0 && j < inv.getSizeInventory()) {
				inv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		upgrades = new InventoryBasic(null, false, 1) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}
		};
		NBTTagList nbttaglist2 = compound.getTagList("upgrades", 10);

		for (int i = 0; i < nbttaglist2.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist2.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			if (j >= 0 && j < upgrades.getSizeInventory()) {
				upgrades.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
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
		if (compound.hasKey("mod"))
			mod = compound.getBoolean("mod");
		lastInsertIndex = compound.getInteger("index");
		stockNum = compound.getInteger("stock");
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
		compound.setTag("Items", nbttaglist);

		NBTTagList nbttaglist2 = new NBTTagList();
		for (int i = 0; i < upgrades.getSizeInventory(); ++i) {
			if (upgrades.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				upgrades.getStackInSlot(i).writeToNBT(nbttagcompound);
				nbttaglist2.appendTag(nbttagcompound);
			}
		}
		compound.setTag("upgrades", nbttaglist2);
		NBTTagList lis = new NBTTagList();
		for (Transfer t : transfers) {
			NBTTagCompound n = new NBTTagCompound();
			t.writeToNBT(n);
			lis.appendTag(n);
		}
		compound.setTag("lis", lis);
		compound.setBoolean("ore", oreDict);
		compound.setBoolean("meta", meta);
		compound.setBoolean("nbt", nbt);
		compound.setBoolean("white", white);
		compound.setBoolean("mod", mod);
		compound.setInteger("index", lastInsertIndex);
		compound.setInteger("stock", stockNum);
		return super.writeToNBT(compound);
	}

	boolean wayFree(BlockPos start, BlockPos end) {
		if (throughBlocks())
			return true;
		Vec3d p1 = new Vec3d(start).addVector(.5, .5, .5);
		Vec3d p2 = new Vec3d(end).addVector(.5, .5, .5);
		Vec3d d = new Vec3d(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());
		d = d.normalize().scale(0.25);
		Set<BlockPos> set = Sets.newHashSet();
		while (p1.distanceTo(p2) > 0.5) {
			set.add(new BlockPos(p1));
			p1 = p1.add(d);
		}
		set.remove(start);
		set.remove(end);
		for (BlockPos p : set)
			if (!worldObj.isAirBlock(p))
				return false;
		return true;
	}

	void moveItems() {
		for (Transfer tr : getTransfers()) {
			if (!tr.blocked && worldObj.getChunkFromBlockCoords(tr.rec.getLeft()).isLoaded()) {
				tr.current = tr.current.add(tr.getVec().scale(getSpeed() / tr.getVec().lengthVector()));
			}
		}
	}

	long getFrequence() {
		if (upgrades.getStackInSlot(0) == null || !(upgrades.getStackInSlot(0).getItem() instanceof ItemUpgrade))
			return Boost.defaultFrequence;
		return Transprot.upgrades.get(upgrades.getStackInSlot(0).getItemDamage()).frequence;
	}

	double getSpeed() {
		if (upgrades.getStackInSlot(0) == null || !(upgrades.getStackInSlot(0).getItem() instanceof ItemUpgrade))
			return Boost.defaultSpeed;
		return Transprot.upgrades.get(upgrades.getStackInSlot(0).getItemDamage()).speed;
	}

	int getStackSize() {
		if (upgrades.getStackInSlot(0) == null || !(upgrades.getStackInSlot(0).getItem() instanceof ItemUpgrade))
			return Boost.defaultStackSize;
		return Transprot.upgrades.get(upgrades.getStackInSlot(0).getItemDamage()).stackSize;
	}

	boolean startTransfer() {
		if (worldObj.getTotalWorldTime() % getFrequence() == 0 && !worldObj.isBlockPowered(pos)) {
			EnumFacing face = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
			if (!worldObj.getChunkFromBlockCoords(pos.offset(face)).isLoaded())
				return false;
			IItemHandler inv = InvHelper.getItemHandler(worldObj.getTileEntity(pos.offset(face)), face.getOpposite());
			if (inv == null)
				return false;
			List<Pair<BlockPos, EnumFacing>> lis = Lists.newArrayList();
			for (Pair<BlockPos, EnumFacing> pp : targets)
				if (wayFree(pos, pp.getLeft()))
					lis.add(pp);
			if (lis.isEmpty())
				return false;
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
			case RR:
				if (lastInsertIndex + 1 >= lis.size())
					lastInsertIndex = 0;
				else
					lastInsertIndex++;
				List<Pair<BlockPos, EnumFacing>> k = Lists.newArrayList();
				for (int i = 0; i < lis.size(); i++) {
					k.add(lis.get((lastInsertIndex + i) % lis.size()));
				}
				lis = Lists.newArrayList(k);
				break;
			default:
				break;
			}
			for (Pair<BlockPos, EnumFacing> pair : lis)
				for (int i = 0; i < inv.getSlots(); i++) {
					if (inv.getStackInSlot(i) == null || !canTransfer(inv.getStackInSlot(i)))
						continue;
					int max = getStackSize();
					ItemStack send = inv.extractItem(i, max, true);
					boolean blocked = false;
					for (Transfer t : transfers) {
						if (t.rec.equals(pair) && t.blocked) {
							blocked = true;
							break;
						}
					}
					if (blocked)
						continue;
					IItemHandler dest = InvHelper.getItemHandler(worldObj.getTileEntity(pair.getLeft()), pair.getRight());
					int canInsert = InvHelper.canInsert(dest, send);
					int missing = Integer.MAX_VALUE;
					if (stockNum > 0) {
						int contains = 0;
						for (int j = 0; j < dest.getSlots(); j++) {
							if (dest.getStackInSlot(j) != null && equal(dest.getStackInSlot(j), send)) {
								contains += dest.getStackInSlot(j).stackSize;
							}
						}
						for (Transfer t : transfers) {
							if (t.rec.equals(pair) && equal(t.stack, send)) {
								contains += t.stack.stackSize;
							}
						}
						missing = stockNum - contains;
					}
					if (missing <= 0 || canInsert <= 0)
						continue;
					canInsert = Math.min(canInsert, missing);
					ItemStack x = inv.extractItem(i, canInsert, true);
					if (x != null) {
						Transfer tr = new Transfer(pos, pair.getLeft(), pair.getRight(), x);
						if (!wayFree(tr.dis, tr.rec.getLeft()))
							continue;
						if (ConfigHandler.particle) {
							Vec3d vec = tr.getVec().normalize().scale(0.015);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setLong("pos", pos.toLong());
							nbt.setDouble("x", vec.xCoord);
							nbt.setDouble("y", vec.yCoord);
							nbt.setDouble("z", vec.zCoord);
							PacketHandler.sendToDimension(new ParticleMessage(nbt), worldObj.provider.getDimension());
						}
						transfers.add(tr);
						inv.extractItem(i, canInsert, false);
						return true;
					}
				}
		}
		return false;
	}

	public Color getColor() {
		return Color.getHSBColor((((pos.hashCode() * 761)) % 360l) / 360f, 1, 1);
	}

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		moveItems();
		boolean needSync = false;
		Iterator<Pair<BlockPos, EnumFacing>> ite = targets.iterator();
		while (ite.hasNext()) {
			Pair<BlockPos, EnumFacing> pa = ite.next();
			if (!InvHelper.hasItemHandler(worldObj, pa.getLeft(), pa.getRight())) {
				ite.remove();
				needSync = true;
			}
		}
		Iterator<Transfer> it = transfers.iterator();
		while (it.hasNext()) {
			Transfer tr = it.next();
			BlockPos currentPos = new BlockPos(getX() + tr.current.xCoord, getY() + tr.current.yCoord, getZ() + tr.current.zCoord);
			if (tr.rec == null || !InvHelper.hasItemHandler(worldObj, tr.rec.getLeft(), tr.rec.getRight()) || (!currentPos.equals(pos) && !currentPos.equals(tr.rec.getLeft()) && !worldObj.isAirBlock(currentPos))) {
				// InventoryHelper.spawnItemStack(worldObj, getX() +
				// tr.current.xCoord, getY() + tr.current.yCoord, getZ() +
				// tr.current.zCoord, tr.stack);
				StackHelper.spawnItemStack(worldObj, currentPos, tr.stack);
				it.remove();
				needSync = true;
				continue;
			}
			boolean received = tr.rec.getLeft().equals(currentPos);
			if (/* tr.received() */received && worldObj.getChunkFromBlockCoords(tr.rec.getLeft()).isLoaded()) {
				ItemStack rest = InvHelper.insert(worldObj.getTileEntity(tr.rec.getLeft()), tr.stack, tr.rec.getRight());
				if (rest != null) {
					tr.stack = rest;
					for (Transfer t : transfers) {
						if (t.rec.equals(tr.rec)) {
							if (!t.blocked)
								needSync = true;
							t.blocked = true;
						}
					}
				} else {
					for (Transfer t : transfers) {
						if (t.rec.equals(tr.rec))
							t.blocked = false;
					}
					it.remove();
					needSync = true;
				}
				worldObj.getTileEntity(tr.rec.getLeft()).markDirty();
			}
		}
		boolean started = startTransfer();
		if (needSync || started)
			sync();
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		if (player.openContainer instanceof ContainerDispatcher) {
			TileDispatcher tile = ((ContainerDispatcher) player.openContainer).tile;
			switch (nbt.getInteger("id")) {
			case 0:
				tile.setMode(tile.getMode().next());
				break;
			case 1:
				tile.setOreDict(!tile.isOreDict());
				break;
			case 2:
				tile.setMeta(!tile.isMeta());
				break;
			case 3:
				tile.setNbt(!tile.isNbt());
				break;
			case 4:
				tile.setWhite(!tile.isWhite());
				break;
			case 5:
				tile.getTargets().clear();
				break;
			case 6:
				tile.setMod(!tile.isMod());
				break;
			case 7:
				tile.setStockNum(tile.getStockNum() - (nbt.getBoolean("shift") ? 10 : 1));
				if (tile.getStockNum() < 0)
					tile.setStockNum(0);
				break;
			case 8:
				tile.setStockNum(tile.getStockNum() + (nbt.getBoolean("shift") ? 10 : 1));
				break;
			}
		}
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

	public IInventory getUpgrades() {
		return upgrades;
	}

	public void setUpgrades(IInventory upgrades) {
		this.upgrades = upgrades;
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

	public boolean isMod() {
		return mod;
	}

	public void setMod(boolean mod) {
		this.mod = mod;
	}

	public int getStockNum() {
		return stockNum;
	}

	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}

	boolean throughBlocks() {
		return upgrades.getStackInSlot(0) != null && upgrades.getStackInSlot(0).getItemDamage() >= 2;
	}

}
