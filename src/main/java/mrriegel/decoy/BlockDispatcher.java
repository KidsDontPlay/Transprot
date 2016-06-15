package mrriegel.decoy;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDispatcher extends BlockContainer {

	public BlockDispatcher() {
		super(Material.IRON);
		this.setRegistryName("dispatcher");
		this.setUnlocalizedName(getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDispatcher();
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileDispatcher t = (TileDispatcher) worldIn.getTileEntity(pos);
		// t.getTransfers().clear();
		t.getTransfers().add(new Transfer(pos, pos.add(next(), Math.abs(next()), next()), new ItemStack(Blocks.FURNACE)));
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	int next() {
		return new Random().nextInt(10) - 5;
	}

}
