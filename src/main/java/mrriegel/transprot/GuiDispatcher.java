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
	GuiButton mode, ore, meta, nbt, white, reset, mod, minus, plus;

	public GuiDispatcher(InventoryPlayer playerInv, TileDispatcher tile) {
		super(new ContainerDispatcher(playerInv, tile));
		this.playerInventory = playerInv;
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		mode = new GuiButton(0, 149 + guiLeft, 38 + guiTop, 20, 20, tile.getMode().toString());
		buttonList.add(mode);
		ore = new GuiButton(1, 85 + guiLeft, 16 + guiTop, 20, 20, "");
		buttonList.add(ore);
		meta = new GuiButton(2, 63 + guiLeft, 16 + guiTop, 20, 20, "ME");
		buttonList.add(meta);
		nbt = new GuiButton(3, 107 + guiLeft, 16 + guiTop, 20, 20, "N");
		buttonList.add(nbt);
		white = new GuiButton(4, 107 + guiLeft, 38 + guiTop, 20, 20, "");
		buttonList.add(white);
		reset = new GuiButton(5, 149 + guiLeft, 60 + guiTop, 20, 20, "R");
		buttonList.add(reset);
		mod = new GuiButton(6, 63 + guiLeft, 38 + guiTop, 20, 20, "MO");
		buttonList.add(mod);
		minus = new GuiButton(7, 63 + guiLeft, 63 + guiTop, 14, 13, "-");
		buttonList.add(minus);
		plus = new GuiButton(8, 107 + guiLeft, 63 + guiTop, 14, 13, "+");
		buttonList.add(plus);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = "Dispatcher";
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		if (mode != null)
			mode.displayString = tile.getMode().toString();

		RenderHelper.enableGUIStandardItemLighting();
		if (ore != null) {
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.GOLD_ORE), 2 + ore.xPosition - guiLeft, 2 + ore.yPosition - guiTop);
			if (!tile.isOreDict()) {
				itemRender.zLevel += 200;
				itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + ore.xPosition - guiLeft, 2 + ore.yPosition - guiTop);
				itemRender.zLevel -= 200;
			}
		}
		if (!tile.isMeta() && meta != null)
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + meta.xPosition - guiLeft, 2 + meta.yPosition - guiTop);
		if (!tile.isNbt() && nbt != null)
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + nbt.xPosition - guiLeft, 2 + nbt.yPosition - guiTop);
		if (white != null)
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.PAPER), 2 + white.xPosition - guiLeft, 2 + white.yPosition - guiTop);
		if (!tile.isWhite() && white != null)
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + white.xPosition - guiLeft, 2 + white.yPosition - guiTop);
		if (!tile.isMod() && mod != null)
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + mod.xPosition - guiLeft, 2 + mod.yPosition - guiTop);
		RenderHelper.disableStandardItemLighting();

		if (mode != null && mode.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.getMode().text), mouseX - guiLeft, mouseY - guiTop);
		else if (ore != null && ore.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isOreDict() ? "Check OreDict" : "Ignore OreDict"), mouseX - guiLeft, mouseY - guiTop);
		else if (meta != null && meta.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isMeta() ? "Check Meta" : "Ignore Meta"), mouseX - guiLeft, mouseY - guiTop);
		else if (nbt != null && nbt.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isNbt() ? "Check NBT" : "Ignore NBT"), mouseX - guiLeft, mouseY - guiTop);
		else if (white != null && white.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isWhite() ? "Whitelist" : "Blacklist"), mouseX - guiLeft, mouseY - guiTop);
		else if (reset != null && reset.isMouseOver())
			drawHoveringText(Lists.newArrayList("Reset Connections"), mouseX - guiLeft, mouseY - guiTop);
		else if (mod != null && mod.isMouseOver())
			drawHoveringText(Lists.newArrayList(tile.isMod() ? "Check Mod ID" : "Ignore Mod ID"), mouseX - guiLeft, mouseY - guiTop);
		else if (isPointInRegion(86, 65, 13, 13, mouseX, mouseY))
			drawHoveringText(Lists.newArrayList("If greater than 0 destination inventory", "will keep that amount of items."), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		drawString(fontRendererObj, "" + tile.getStockNum(), guiLeft + (92 - fontRendererObj.getStringWidth("" + tile.getStockNum()) / 2), guiTop + 65, 14737632);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		Transprot.DISPATCHER.sendToServer(new ButtonMessage(button.id, isShiftKeyDown()));
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
		case 5:
			tile.getTargets().clear();
			break;
		case 6:
			tile.setMod(!tile.isMod());
			break;
		case 7:
			tile.setStockNum(tile.getStockNum() - (isShiftKeyDown() ? 10 : 1));
			if (tile.getStockNum() < 0)
				tile.setStockNum(0);
			break;
		case 8:
			tile.setStockNum(tile.getStockNum() + (isShiftKeyDown() ? 10 : 1));
			break;
		}
	}
}
