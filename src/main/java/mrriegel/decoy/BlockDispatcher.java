package mrriegel.decoy;

import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockDispatcher extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockDispatcher() {
		super(Material.IRON);
		this.setRegistryName("dispatcher");
		this.setUnlocalizedName(getRegistryName().toString());
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, facing.getOpposite());
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double x1 = 0.0625, y1 = 0.0625, z1 = 0.0625, x2 = 0.9375, y2 = 0.9375, z2 = 0.9375;
		switch (state.getValue(FACING)) {
		case SOUTH:
			z1 = 0.5;
			z2 = 1;
			break;
		case DOWN:
			y2 = 0.5;
			y1 = 0;
			break;
		case EAST:
			x1 = 0.5;
			x2 = 1;
			break;
		case NORTH:
			z2 = 0.5;
			z1 = 0;
			break;
		case UP:
			y1 = 0.5;
			y2 = 1;
			break;
		case WEST:
			x2 = 0.5;
			x1 = 0;
			break;

		}
		AxisAlignedBB bb = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
		return bb;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
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
	public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileDispatcher) {
				playerIn.openGui(Decoy.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}

			return true;
		}
	}

}
