package mrriegel.decoy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemDecoy extends Item {

	public ItemDecoy() {
		super();
		this.setRegistryName("decoy");
		this.setUnlocalizedName(getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.MISC);
	}

}
