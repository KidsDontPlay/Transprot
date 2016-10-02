package mrriegel.transprot;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class TransferRender {
	Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void ren(RenderWorldLastEvent e) {
		if (!ConfigHandler.itemsVisible)
			return;
		for (TileEntity t : mc.theWorld.loadedTileEntityList)
			if (t instanceof TileDispatcher && mc.thePlayer.getDistance(t.getPos().getX(), t.getPos().getY(), t.getPos().getZ()) < 24)
				renderTransfers((TileDispatcher) t, t.getPos().getX() - TileEntityRendererDispatcher.staticPlayerX, t.getPos().getY() - TileEntityRendererDispatcher.staticPlayerY, t.getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ);
	}

	public void renderTransfers(TileDispatcher te, double x, double y, double z) {
		for (Transfer tr : te.getTransfers()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			RenderItem itemRenderer = mc.getRenderItem();
			double factor = Minecraft.getDebugFPS() / 20d;
			if (!tr.blocked && !mc.isGamePaused() && mc.theWorld.getChunkFromBlockCoords(tr.rec.getLeft()).isLoaded()) {
				tr.current = tr.current.add(tr.getVec().scale((te.getSpeed() / factor) / tr.getVec().lengthVector()));
			}

			GlStateManager.translate(tr.current.xCoord, tr.current.yCoord, tr.current.zCoord);
			EntityItem ei = new EntityItem(mc.theWorld, 0, 0, 0, tr.stack);
			ei.hoverStart = 0;

			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();

			if (mc.gameSettings.fancyGraphics && !mc.isGamePaused()) {
				float rotation = (float) (720.0 * ((System.currentTimeMillis() + tr.turn) & 0x3FFFL) / 0x3FFFL);
				GlStateManager.rotate(rotation, 0.0F, 1.0F, 0);
			}
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.pushAttrib();
			RenderHelper.enableStandardItemLighting();
			itemRenderer.renderItem(ei.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != Transprot.linker)
			return;
		double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		for (TileEntity t : mc.theWorld.loadedTileEntityList)
			if (t instanceof TileDispatcher && !mc.isGamePaused() && mc.thePlayer.getDistance(t.getPos().getX(), t.getPos().getY(), t.getPos().getZ()) < 64) {
				Color color = ((TileDispatcher) t).getColor();
				GlStateManager.pushMatrix();

				GlStateManager.disableTexture2D();
				GlStateManager.disableLighting();
				GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

				for (Pair<BlockPos, EnumFacing> pa : ((TileDispatcher) t).getTargets()) {
					BlockPos p = pa.getLeft();
					float x = p.getX() + .5f, y = p.getY() + .5f, z = p.getZ() + .5f;
					float x2 = t.getPos().getX() + .5f, y2 = t.getPos().getY() + .5f, z2 = t.getPos().getZ() + .5f;
					// RenderHelper.enableStandardItemLighting();
					boolean free = ((TileDispatcher) t).wayFree(t.getPos(), p);
					if (!free && mc.theWorld.getTotalWorldTime() / 10 % 2 != 0)
						continue;
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer renderer = tessellator.getBuffer();
					renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
					GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
					float width = 5.0f;
					GL11.glLineWidth(width);
					GlStateManager.pushAttrib();
					if (player.isSneaking())
						GL11.glDisable(GL11.GL_DEPTH_TEST);
					renderer.pos(x, y, z).endVertex();
					renderer.pos(x2, y2, z2).endVertex();
					tessellator.draw();
					GlStateManager.popAttrib();
					// RenderHelper.disableStandardItemLighting();

				}

				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.color(1f, 1f, 1f, 1f);
				GlStateManager.popMatrix();
			}
	}
}
