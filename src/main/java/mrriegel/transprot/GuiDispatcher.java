package mrriegel.transprot;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;

@SideOnly(Side.CLIENT)
public class GuiDispatcher extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Transprot.MODID + ":textures/gui/dispatcher.png");
	private final InventoryPlayer playerInventory;
	private TileDispatcher tile;
	GuiButton mode, ore, meta, nbt, white;

	public GuiDispatcher(InventoryPlayer playerInv, TileDispatcher tile) {
		super(new ContainerDispatcher(playerInv, tile));
		this.playerInventory = playerInv;
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		mode = new GuiButton(0, 105 + guiLeft, 16 + guiTop, 20, 20, tile.getMode().toString());
		buttonList.add(mode);
		ore = new GuiButton(1, 127 + guiLeft, 16 + guiTop, 20, 20, "");
		buttonList.add(ore);
		meta = new GuiButton(2, 105 + guiLeft, 38 + guiTop, 20, 20, "M");
		buttonList.add(meta);
		nbt = new GuiButton(3, 127 + guiLeft, 38 + guiTop, 20, 20, "N");
		buttonList.add(nbt);
		white = new GuiButton(4, 105 + guiLeft, 60 + guiTop, 20, 20, "");
		buttonList.add(white);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = "Dispatcher";
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		mode.displayString = tile.getMode().toString();

		RenderHelper.enableGUIStandardItemLighting();
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.GOLD_ORE), 2 + ore.xPosition - guiLeft, 2 + ore.yPosition - guiTop);
		if (!tile.isOreDict()) {
			itemRender.zLevel += 200;
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + ore.xPosition - guiLeft, 2 + ore.yPosition - guiTop);
			itemRender.zLevel -= 200;
		}
		if (!tile.isMeta())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + meta.xPosition - guiLeft, 2 + meta.yPosition - guiTop);
		if (!tile.isNbt())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + nbt.xPosition - guiLeft, 2 + nbt.yPosition - guiTop);
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.PAPER), 2 + white.xPosition - guiLeft, 2 + white.yPosition - guiTop);
		if (!tile.isWhite())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + white.xPosition - guiLeft, 2 + white.yPosition - guiTop);
		RenderHelper.disableStandardItemLighting();

		if (mode.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.getMode().text), mouseX - guiLeft, mouseY - guiTop);
		else if (ore.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isOreDict() ? "Check OreDict" : "Don't check OreDict"), mouseX - guiLeft, mouseY - guiTop);
		else if (meta.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isMeta() ? "Check Meta" : "Don't check Meta"), mouseX - guiLeft, mouseY - guiTop);
		else if (nbt.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isNbt() ? "Check NBT" : "Don't check NBT"), mouseX - guiLeft, mouseY - guiTop);
		else if (white.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isWhite() ? "Whitelist" : "Blacklist"), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		Transprot.DISPATCHER.sendToServer(new ButtonMessage(button.id));
		switch (button.id) {
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
		}
	}
}
