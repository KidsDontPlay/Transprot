package mrriegel.transprot;

import java.io.IOException;

import com.google.common.collect.Lists;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.CommonGuiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiDispatcher extends CommonGuiContainer {
	private final InventoryPlayer playerInventory;
	private TileDispatcher tile;
	CommonGuiButton mode, ore, meta, nbt, white, reset, mod;
	GuiButton minus, plus;

	private boolean dirty;

	public GuiDispatcher(InventoryPlayer playerInv, TileDispatcher tile) {
		super(new ContainerDispatcher(playerInv, tile));
		this.playerInventory = playerInv;
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(mode = new CommonGuiButton(0, 149 + guiLeft, 38 + guiTop, 20, 20, tile.getMode().toString()));
		buttonList.add(ore = new CommonGuiButton(1, 85 + guiLeft, 16 + guiTop, 20, 20, ""));
		buttonList.add(meta = new CommonGuiButton(2, 63 + guiLeft, 16 + guiTop, 20, 20, "ME"));
		buttonList.add(nbt = new CommonGuiButton(3, 107 + guiLeft, 16 + guiTop, 20, 20, "N"));
		buttonList.add(white = new CommonGuiButton(4, 107 + guiLeft, 38 + guiTop, 20, 20, ""));
		buttonList.add(reset = new CommonGuiButton(5, 149 + guiLeft, 60 + guiTop, 20, 20, "R"));
		buttonList.add(mod = new CommonGuiButton(6, 63 + guiLeft, 38 + guiTop, 20, 20, "MO"));
		buttonList.add(minus = new GuiButtonExt(7, 63 + guiLeft, 63 + guiTop, 14, 14, "-"));
		buttonList.add(plus = new GuiButtonExt(8, 107 + guiLeft, 63 + guiTop, 14, 14, "+"));
		dirty = true;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (dirty) {
			mode.displayString = tile.getMode().toString();
			mode.setTooltip(tile.getMode().text);
			ore.setTooltip(tile.isOreDict() ? "Check OreDict" : "Ignore OreDict");
			meta.setTooltip(tile.isMeta() ? "Check Meta" : "Ignore Meta");
			nbt.setTooltip(tile.isNbt() ? "Check NBT" : "Ignore NBT");
			white.setTooltip(tile.isWhite() ? "Whitelist" : "Blacklist");
			mod.setTooltip(tile.isMod() ? "Check Mod ID" : "Ignore Mod ID");
			reset.setTooltip("Reset");
			dirty = false;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = "Dispatcher";
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);

		RenderHelper.enableGUIStandardItemLighting();
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.GOLD_ORE), 2 + ore.x - guiLeft, 2 + ore.y - guiTop);
		if (!tile.isOreDict()) {
			itemRender.zLevel += 200;
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + ore.x - guiLeft, 2 + ore.y - guiTop);
			itemRender.zLevel -= 200;
		}
		if (!tile.isMeta())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + meta.x - guiLeft, 2 + meta.y - guiTop);
		if (!tile.isNbt())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + nbt.x - guiLeft, 2 + nbt.y - guiTop);
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.PAPER), 2 + white.x - guiLeft, 2 + white.y - guiTop);
		if (!tile.isWhite())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + white.x - guiLeft, 2 + white.y - guiTop);
		if (!tile.isMod())
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), 2 + mod.x - guiLeft, 2 + mod.y - guiTop);
		RenderHelper.disableStandardItemLighting();

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		if (isPointInRegion(86, 65, 13, 13, mouseX, mouseY))
			drawHoveringText(Lists.newArrayList("If greater than 0 destination inventory", "will keep that amount of items."), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
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
		dirty = true;
	}
}
