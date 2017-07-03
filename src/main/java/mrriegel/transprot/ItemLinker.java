package mrriegel.transprot;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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

public class ItemLinker extends CommonItem {

	public ItemLinker() {
		super("linker");
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return EnumActionResult.SUCCESS;
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			if (world.getTileEntity(pos) instanceof TileDispatcher) {
				NBTStackHelper.set(stack, "pos", pos);
				NBTStackHelper.set(stack, "dim", world.provider.getDimension());
				player.sendMessage(new TextComponentString("Bound to Dispatcher."));
				return EnumActionResult.SUCCESS;
			} else if (InvHelper.hasItemHandler(world, pos, facing) && NBTHelper.hasTag(stack.getTagCompound(), "pos")) {
				BlockPos tPos = NBTStackHelper.get(stack, "pos", BlockPos.class);
				if (world.provider.getDimension() == NBTStackHelper.get(stack, "dim", Integer.class) && world.getTileEntity(tPos) instanceof TileDispatcher) {
					TileDispatcher tile = (TileDispatcher) world.getTileEntity(tPos);
					Pair<BlockPos, EnumFacing> pair = new ImmutablePair<BlockPos, EnumFacing>(pos, facing);
					if (pos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ()) < ConfigHandler.range) {
						boolean done = tile.getTargets().add(pair);
						if (done) {
							player.sendMessage(new TextComponentString("Added " + world.getBlockState(pos).getBlock().getLocalizedName() + "."));
							tile.sync();
						} else {
							player.sendMessage(new TextComponentString("Inventory is already connected."));
						}
					} else
						player.sendMessage(new TextComponentString("Too far away."));
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}
