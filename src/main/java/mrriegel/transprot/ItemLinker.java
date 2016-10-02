package mrriegel.transprot;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.item.CommonItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ItemLinker extends CommonItem {

	public ItemLinker() {
		super("linker");
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return EnumActionResult.PASS;
		if (player.isSneaking() && world.getTileEntity(pos) instanceof TileDispatcher) {
			NBTStackHelper.setLong(stack, "pos", pos.toLong());
			NBTStackHelper.setInt(stack, "dim", world.provider.getDimension());
			player.addChatMessage(new TextComponentString("Bound to Dispatcher."));
			return EnumActionResult.SUCCESS;
		}
		if (player.isSneaking() && InvHelper.hasItemHandler(world, pos, facing) && NBTHelper.hasTag(stack.getTagCompound(), "pos")) {
			BlockPos tPos = BlockPos.fromLong(stack.getTagCompound().getLong("pos"));
			if (world.provider.getDimension() == stack.getTagCompound().getInteger("dim") && world.getTileEntity(tPos) instanceof TileDispatcher) {
				Pair<BlockPos, EnumFacing> pair = new ImmutablePair<BlockPos, EnumFacing>(pos, facing);
				if (pos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ()) < ConfigHandler.range) {
					boolean done = ((TileDispatcher) world.getTileEntity(tPos)).getTargets().add(pair);
					((TileDispatcher) world.getTileEntity(tPos)).sync();
					if (done)
						player.addChatMessage(new TextComponentString("Added " + world.getBlockState(pos).getBlock().getLocalizedName() + "."));
				} else
					player.addChatMessage(new TextComponentString("Too far away."));
				return EnumActionResult.SUCCESS;
			}
		}
		return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}
