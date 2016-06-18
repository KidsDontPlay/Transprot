package mrriegel.transprot;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class ContainerDispatcher extends Container {
	private IInventory inv;
	public TileDispatcher tile;

	public ContainerDispatcher(IInventory playerInventory, TileDispatcher tile) {
		this.tile = tile;
		this.inv = tile.getInv();

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 5; ++j) {
				this.addSlotToContainer(new SlotGhost(inv, j + i * 5, 8 + j * 18, 17 + i * 18));
			}
		}
		this.addSlotToContainer(new Slot(tile.getUpgrades(), 0, 151, 17) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack == null || stack.getItem() == null ? false : Transprot.upgrades.keySet().contains(stack.getItem());
			}
		});
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.inv.isUseableByPlayer(playerIn);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return false;
	}

	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();

			if (index < 9 + 6) {
				return null;
			} else if (slot.inventory instanceof InventoryPlayer) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null) {
						// inv.setInventorySlotContents(i,
						// ItemHandlerHelper.copyStackWithSize(itemstack1, 1));
						getSlotFromInventory(inv, i).putStack(ItemHandlerHelper.copyStackWithSize(itemstack1, 1));
						return null;
					}
				}
			}

		}

		return null;
	}

	public static class SlotGhost extends Slot {
		public SlotGhost(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			ItemStack holding = playerIn.inventory.getItemStack();

			if (holding != null) {
				holding = holding.copy();
				holding.stackSize = 1;
			}
			this.putStack(holding);
			return false;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			ItemStack copy = stack.copy();
			copy.stackSize = 1;
			this.putStack(copy);
			return false;
		}

		@Override
		public ItemStack decrStackSize(int amount) {
			this.putStack(null);
			return null;
		}
	}
}
