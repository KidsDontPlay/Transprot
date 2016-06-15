package mrriegel.decoy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Transfer {
	public BlockPos dis, rec;
	public Vec3d current;
	public ItemStack stack;

	private Transfer() {

	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound c = compound.getCompoundTag("stack");
		stack = ItemStack.loadItemStackFromNBT(c);
		dis = BlockPos.fromLong(compound.getLong("dis"));
		rec = BlockPos.fromLong(compound.getLong("rec"));
		current = new Vec3d(compound.getDouble("xx"), compound.getDouble("yy"), compound.getDouble("zz"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag("stack", c);
		compound.setLong("dis", dis.toLong());
		compound.setLong("rec", rec.toLong());
		compound.setDouble("xx", current.xCoord);
		compound.setDouble("yy", current.yCoord);
		compound.setDouble("zz", current.zCoord);
		return c;
	}

	public Transfer(BlockPos dis, BlockPos rec, ItemStack stack) {
		this.dis = dis;
		this.rec = rec;
		this.current = new Vec3d(.5, .5, .5);
		this.stack = stack;
	}

	public boolean received() {
		return current.lengthVector() > getVec().lengthVector();
	}

	public Vec3d getVec() {
		return new Vec3d(rec.getX() - dis.getX(), rec.getY() - dis.getY(), rec.getZ() - dis.getZ());
	}

	public static Transfer loadFromNBT(NBTTagCompound nbt) {
		Transfer tr = new Transfer();
		tr.readFromNBT(nbt);
		return tr;
	}

}
