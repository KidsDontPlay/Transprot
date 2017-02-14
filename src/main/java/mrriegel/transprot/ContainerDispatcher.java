package mrriegel.transprot;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.gui.slot.SlotGhost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerDispatcher extends CommonContainer {
	public TileDispatcher tile;

	public ContainerDispatcher(InventoryPlayer playerInventory, TileDispatcher tile) {
		super(playerInventory, Pair.<String, IInventory> of("filter", tile.getInv()), Pair.<String, IInventory> of("upgrade", tile.getUpgrades()));
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
				return stack.isEmpty() || stack.getItem() == null ? false : stack.getItem() instanceof ItemUpgrade;
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
		return this.tile.isUsable(playerIn);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return false;
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		List<Area> lis = Lists.newArrayList();
		if (inv == invPlayer) {
			if (stack.getItem() == Transprot.upgrade) {
				lis.add(getAreaForEntireInv((invs.get("upgrade"))));
			}
			lis.add(getAreaForEntireInv(invs.get("filter")));
		} else if (inv == invs.get("upgrade") || inv == invs.get("filter")) {
			lis.add(getAreaForEntireInv(invPlayer));
		}
		return lis;
	}
}
