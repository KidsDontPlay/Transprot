package mrriegel.decoy;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemLinker extends Item {

	public ItemLinker() {
		super();
		this.setRegistryName("linker");
		this.setUnlocalizedName(getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
			return EnumActionResult.PASS;
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		if (player.isSneaking() && world.getTileEntity(pos) instanceof TileDispatcher) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setLong("pos", pos.toLong());
			return EnumActionResult.SUCCESS;
		}
		if (player.isSneaking() && InvHelper.hasItemHandler(world, pos, facing)) {
			if (stack.getTagCompound() != null && world.getTileEntity(BlockPos.fromLong(stack.getTagCompound().getLong("pos"))) instanceof TileDispatcher) {
				Pair<BlockPos, EnumFacing> pair=new ImmutablePair<BlockPos, EnumFacing>(pos, facing);
				((TileDispatcher) world.getTileEntity(BlockPos.fromLong(stack.getTagCompound().getLong("pos")))).getTargets().add(pair);
				((TileDispatcher) world.getTileEntity(BlockPos.fromLong(stack.getTagCompound().getLong("pos")))).updateClient();
				return EnumActionResult.SUCCESS;
			}
		}
		return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}
