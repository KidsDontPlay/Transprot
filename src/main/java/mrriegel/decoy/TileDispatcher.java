package mrriegel.decoy;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.google.common.collect.Sets;

public class TileDispatcher extends TileEntity implements ITickable {
	private Set<Transfer> transfers = Sets.newHashSet();
	private EnumFacing invFacing;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList lis = compound.getTagList("lis", 10);
		transfers = Sets.newHashSet();
		for (int i = 0; i < lis.tagCount(); i++)
			transfers.add(Transfer.loadFromNBT(lis.getCompoundTagAt(i)));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList lis = new NBTTagList();
		for (Transfer t : transfers) {
			NBTTagCompound n = new NBTTagCompound();
			t.writeToNBT(n);
			lis.appendTag(n);
		}
		compound.setTag("lis", lis);
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

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expand(50, 50, 50);
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

	@Override
	public void update() {
		Iterator<Transfer> it = transfers.iterator();
		boolean removed = false;
		while (it.hasNext())
			if (it.next().received()) {
				it.remove();
				removed = true;
			}
		if (removed)
			updateClient();
	}

	double next() {
		return new Random().nextBoolean() ? 0.03 : 0.04;
	}

	public Set<Transfer> getTransfers() {
		return transfers;
	}

	public void setTransfers(Set<Transfer> transfers) {
		this.transfers = transfers;
	}

	public EnumFacing getInvFacing() {
		return invFacing;
	}

	public void setInvFacing(EnumFacing invFacing) {
		this.invFacing = invFacing;
	}

}
