package mrriegel.transprot;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgrade extends Item {

	public ItemUpgrade() {
		super();
		this.setRegistryName("upgrade");
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setUnlocalizedName(getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 4; i++) {
			list.add(new ItemStack(item, 1, i));
		}
}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack)+"_"+stack.getItemDamage();
	}

	
}
