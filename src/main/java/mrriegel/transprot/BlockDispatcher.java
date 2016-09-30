package mrriegel.transprot;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDispatcher extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockDispatcher() {
		super(Material.IRON);
		this.setHardness(1.5f);
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
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta]);
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
				playerIn.openGui(Transprot.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}

			return true;
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity t = worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && t instanceof TileDispatcher) {
			TileDispatcher tile = (TileDispatcher) t;
			if (tile.getUpgrades().getStackInSlot(0) != null)
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getUpgrades().getStackInSlot(0));
			for (Transfer tr : tile.getTransfers()) {
				InventoryHelper.spawnItemStack(worldIn, pos.getX() + tr.current.xCoord, pos.getY() + tr.current.yCoord, pos.getZ() + tr.current.zCoord, tr.stack);
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

}
