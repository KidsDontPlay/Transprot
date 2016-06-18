package mrriegel.transprot;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticleMessage implements IMessage {

	BlockPos pos;
	Vec3d vec;

	public ParticleMessage() {
	}

	public ParticleMessage(BlockPos pos, Vec3d vec) {
		this.pos = pos;
		this.vec = vec;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.vec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeDouble(this.vec.xCoord);
		buf.writeDouble(this.vec.yCoord);
		buf.writeDouble(this.vec.zCoord);
	}

	public static class Handler implements IMessageHandler<ParticleMessage, IMessage> {

		@Override
		public IMessage onMessage(final ParticleMessage message, final MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					double dx = message.vec.xCoord, dy = message.vec.yCoord, dz = message.vec.zCoord;
					for (int i = 0; i < 7; i++) {
						double xx = (new Random().nextDouble() - .5) / 2d;
						double yy = (new Random().nextDouble() - .5) / 2d;
						double zz = (new Random().nextDouble() - .5) / 2d;
						Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.END_ROD, message.pos.getX() + .5 + xx, message.pos.getY() + .5 + yy, message.pos.getZ() + .5 + zz, dx, dy, dz);
					}
				}
			});
			return null;
		}
	}

}
