package mrriegel.decoy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TransferRender {
	Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void ren(RenderWorldLastEvent e) {
		for (TileEntity t : mc.theWorld.loadedTileEntityList)
			if (t instanceof TileDispatcher)
				renderTileEntityAt((TileDispatcher) t, t.getPos().getX() - TileEntityRendererDispatcher.staticPlayerX, t.getPos().getY() - TileEntityRendererDispatcher.staticPlayerY, t.getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ, e.getPartialTicks(), 0);
	}

	// @Override
	public void renderTileEntityAt(TileDispatcher te, double x, double y, double z, float partialTicks, int destroyStage) {

		for (Transfer tr : te.getTransfers()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			RenderItem itemRenderer = mc.getRenderItem();
			if (!tr.received() && !mc.isGamePaused())
				tr.current = tr.current.add(tr.getVec().scale(.03 / tr.getVec().lengthVector()));
			if (mc.theWorld.getTotalWorldTime() % 10 == 0)
				Decoy.DISPATCHER.sendToServer(new SyncMessageS(te));
			GlStateManager.translate(tr.current.xCoord, tr.current.yCoord, tr.current.zCoord);
			EntityItem ei = new EntityItem(mc.theWorld, 0, 0, 0, tr.stack);
			ei.hoverStart = 0;

			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();

			float rotation = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

			GlStateManager.rotate(rotation, 0.0F, 1.0F, 0);
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
}
