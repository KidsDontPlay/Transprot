package mrriegel.transprot;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.slot.SlotFilter;
import mrriegel.limelib.gui.slot.SlotGhost;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerDispatcher extends CommonContainerTile<TileDispatcher> {

	public ContainerDispatcher(InventoryPlayer playerInventory, TileDispatcher tile) {
		super(playerInventory, tile, Pair.<String, IInventory> of("filter", tile.getInv()), Pair.<String, IInventory> of("upgrade", tile.getUpgrades()));
	}

	@Override
	protected void initSlots() {
		initSlots((IInventory) invs.get("filter"), 8, 17, 3, 3, 0, SlotGhost.class);
		this.addSlotToContainer(new SlotFilter(invs.get("upgrade"), 0, 151, 17, stack -> stack.isEmpty() || stack.getItem() == Transprot.upgrade));
		initPlayerSlots(8, 84);
	}

	//	@Override
	//	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
	//		return false;
	//	}

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
