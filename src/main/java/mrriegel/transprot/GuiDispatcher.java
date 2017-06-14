package mrriegel.transprot;

import java.io.IOException;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonTooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import com.google.common.collect.Lists;

public class GuiDispatcher extends CommonGuiContainer {
	private final InventoryPlayer playerInventory;
	private TileDispatcher tile;
	GuiButtonTooltip mode, ore, meta, nbt, white, reset, mod;
	GuiButton minus, plus;

	public GuiDispatcher(InventoryPlayer playerInv, TileDispatcher tile) {
		super(new ContainerDispatcher(playerInv, tile));
		this.playerInventory = playerInv;
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(mode = new GuiButtonTooltip(0, 149 + guiLeft, 38 + guiTop, 20, 20, tile.getMode().toString(), null, Lists.newArrayList(tile.getMode().text)));
		buttonList.add(ore = new GuiButtonTooltip(1, 85 + guiLeft, 16 + guiTop, 20, 20, "", null, Lists.newArrayList(tile.isOreDict() ? "Check OreDict" : "Ignore OreDict")));
		buttonList.add(meta = new GuiButtonTooltip(2, 63 + guiLeft, 16 + guiTop, 20, 20, "ME", null, Lists.newArrayList(tile.isMeta() ? "Check Meta" : "Ignore Meta")));
		buttonList.add(nbt = new GuiButtonTooltip(3, 107 + guiLeft, 16 + guiTop, 20, 20, "N", null, Lists.newArrayList(tile.isNbt() ? "Check NBT" : "Ignore NBT")));
		buttonList.add(white = new GuiButtonTooltip(4, 107 + guiLeft, 38 + guiTop, 20, 20, "", null, Lists.newArrayList(tile.isWhite() ? "Whitelist" : "Blacklist")));
		buttonList.add(reset = new GuiButtonTooltip(5, 149 + guiLeft, 60 + guiTop, 20, 20, "R", null, Lists.newArrayList("Reset Connections")));
		buttonList.add(mod = new GuiButtonTooltip(6, 63 + guiLeft, 38 + guiTop, 20, 20, "MO", null, Lists.newArrayList(tile.isMod() ? "Check Mod ID" : "Ignore Mod ID")));
		buttonList.add(minus = new GuiButtonExt(7, 63 + guiLeft, 63 + guiTop, 14, 14, "-"));
		buttonList.add(plus = new GuiButtonExt(8, 107 + guiLeft, 63 + guiTop, 14, 14, "+"));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (mode != null)
			mode.displayString = tile.getMode().toString();
		mode.setTooltip(Lists.newArrayList(tile.getMode().text));
		ore.setTooltip(Lists.newArrayList(tile.isOreDict() ? "Check OreDict" : "Ignore OreDict"));
		meta.setTooltip(Lists.newArrayList(tile.isMeta() ? "Check Meta" : "Ignore Meta"));
		nbt.setTooltip(Lists.newArrayList(tile.isNbt() ? "Check NBT" : "Ignore NBT"));
		white.setTooltip(Lists.newArrayList(tile.isWhite() ? "Whitelist" : "Blacklist"));
		mod.setTooltip(Lists.newArrayList(tile.isMod() ? "Check Mod ID" : "Ignore Mod ID"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = "Dispatcher";
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);

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

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (isPointInRegion(86, 65, 13, 13, mouseX, mouseY))
			drawHoveringText(Lists.newArrayList("If greater than 0 destination inventory", "will keep that amount of items."), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(7, 83);
		drawer.drawSlots(7, 16, 3, 3);
		drawer.drawSlot(150, 16);
		drawString(fontRenderer, "" + tile.getStockNum(), guiLeft + (92 - fontRenderer.getStringWidth("" + tile.getStockNum()) / 2), guiTop + 65, 14737632);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", button.id);
		nbt.setBoolean("shift", isShiftKeyDown());
		tile.handleMessage(mc.player, nbt);
		tile.sendMessage(nbt);
	}
}
