package mrriegel.decoy;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDispatcher extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Decoy.MODID + ":textures/gui/dispatcher.png");
	private final InventoryPlayer playerInventory;
	public IInventory inv;

	public GuiDispatcher(InventoryPlayer playerInv, IInventory inv) {
		super(new ContainerDispatcher(playerInv, inv));
		this.playerInventory = playerInv;
		this.inv = inv;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = "Dispatcher";
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
