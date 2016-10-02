package mrriegel.transprot;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.gui.slot.SlotGhost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import com.google.common.collect.Lists;

public class ContainerDispatcher extends CommonContainer {
	public TileDispatcher tile;

	public ContainerDispatcher(InventoryPlayer playerInventory, TileDispatcher tile) {
		super(playerInventory, InvEntry.of("filter", tile.getInv()), InvEntry.of("upgrade", tile.getUpgrades()));
		this.tile = tile;
	}

	@Override
	protected void initSlots() {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				this.addSlotToContainer(new SlotGhost(invs.get("filter"), j + i * 3, 8 + j * 18, 17 + i * 18) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						inventoryChanged();
					}
				});
		this.addSlotToContainer(new Slot(invs.get("upgrade"), 0, 151, 17) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack == null || stack.getItem() == null ? false : stack.getItem() instanceof ItemUpgrade;
			}

			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				inventoryChanged();
			}
		});
		initPlayerSlots(8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tile.isUseableByPlayer(playerIn);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return false;
	}

	@Override
	protected void inventoryChanged() {
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();

			IInventory inv = invs.get("filter");
			if (slot.inventory instanceof InventoryBasic) {
				return null;
			} else if (slot.inventory instanceof InventoryPlayer) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null) {
						getSlotFromInventory(inv, i).putStack(ItemHandlerHelper.copyStackWithSize(itemstack1, 1));
						return null;
					}
				}
			}

		}

		return null;
		// ItemStack s=super.transferStackInSlot(playerIn, index);
		// detectAndSendChanges();
		// return s;
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		List<Area> lis = Lists.newArrayList();
		// if (inv == invPlayer) {
		// if (stack.getItem() == Transprot.upgrade) {
		// lis.add(getAreaforEntire(invs.get("upgrade")));
		// }
		// lis.add(getAreaforEntire(invs.get("filter")));
		// } else if (inv == invs.get("upgrade") || inv == invs.get("filter")) {
		// lis.add(getAreaforEntire(invPlayer));
		// }
		return lis;
	}
}
