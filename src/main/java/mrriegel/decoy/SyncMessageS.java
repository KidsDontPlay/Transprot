package mrriegel.decoy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncMessageS implements IMessage {

	NBTTagCompound nbt;
	BlockPos pos;

	public SyncMessageS() {
	}

	public SyncMessageS(TileEntity t) {
		this.nbt = t.serializeNBT();
		this.pos = t.getPos();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.nbt = ByteBufUtils.readTag(buf);
		this.pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.nbt);
		buf.writeLong(this.pos.toLong());
	}

	public static class Handler implements IMessageHandler<SyncMessageS, IMessage> {

		@Override
		public IMessage onMessage(final SyncMessageS message, final MessageContext ctx) {
			ctx.getServerHandler().playerEntity.getServerWorld().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					TileEntity t = ctx.getServerHandler().playerEntity.getServerWorld().getTileEntity(message.pos);
					t.deserializeNBT(message.nbt);
				}
			});
			return null;
		}
	}

}
