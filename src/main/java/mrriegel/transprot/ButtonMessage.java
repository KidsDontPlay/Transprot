package mrriegel.transprot;

import java.util.Collections;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ButtonMessage implements IMessage {

	int id;

	public ButtonMessage() {
	}

	public ButtonMessage(int id) {
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
	}

	public static class Handler implements IMessageHandler<ButtonMessage, IMessage> {

		@Override
		public IMessage onMessage(final ButtonMessage message, final MessageContext ctx) {
			ctx.getServerHandler().playerEntity.getServerWorld().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerDispatcher) {
						TileDispatcher tile = ((ContainerDispatcher) ctx.getServerHandler().playerEntity.openContainer).tile;
						switch (message.id) {
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
							if (tile.getStockNum() > 0)
								tile.setStockNum(tile.getStockNum() - 1);
							break;
						case 8:
							tile.setStockNum(tile.getStockNum() + 1);
							break;
						}
					}

				}
			});
			return null;
		}
	}

}
