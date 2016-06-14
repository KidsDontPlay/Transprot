package mrriegel.decoy;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDecoy extends Item {

	public ItemDecoy() {
		super();
		this.setRegistryName("decoy");
		this.setUnlocalizedName(getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.MISC);
	}

}
