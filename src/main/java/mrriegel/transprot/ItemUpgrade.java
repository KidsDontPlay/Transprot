package mrriegel.transprot;

import mrriegel.limelib.item.CommonSubtypeItem;
import net.minecraft.creativetab.CreativeTabs;

public class ItemUpgrade extends CommonSubtypeItem {

	public ItemUpgrade() {
		super("upgrade", 4);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
}
