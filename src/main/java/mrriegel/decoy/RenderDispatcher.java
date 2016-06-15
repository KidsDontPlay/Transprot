package mrriegel.decoy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderDispatcher extends TileEntitySpecialRenderer<TileDispatcher> {
	ModelDispatcher model;
	ResourceLocation tex=new ResourceLocation(Decoy.MODID+":textures/tile/dis.png");
	

	public RenderDispatcher() {
		model = new ModelDispatcher();
	}

	@Override
	public void renderTileEntityAt(TileDispatcher te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		 Minecraft.getMinecraft().renderEngine.bindTexture(tex);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushAttrib();
		RenderHelper.disableStandardItemLighting();
		model.render(null, 0, 0, 0, 0, 0, 0.0625f);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
