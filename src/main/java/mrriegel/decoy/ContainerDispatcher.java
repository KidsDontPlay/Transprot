package mrriegel.decoy;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDispatcher extends Container {
	private IInventory inv;

	public ContainerDispatcher(IInventory playerInventory, IInventory inv) {
		this.inv = inv;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 5; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 3, 8 + j * 18, 17 + i * 18));
			}
		}

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.inv.isUseableByPlayer(playerIn);
	}

	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 9 + 6) {
				if (!this.mergeItemStack(itemstack1, 9 + 6, 45 + 6, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 9 + 6, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}
}
