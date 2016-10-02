package mrriegel.transprot;

import static mrriegel.transprot.Transprot.upgrades;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.transprot.Transprot.Boost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler {

	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.refreshConfig(event.getSuggestedConfigurationFile());
		GameRegistry.register(Transprot.dispatcher);
		GameRegistry.register(new ItemBlock(Transprot.dispatcher).setRegistryName(Transprot.dispatcher.getRegistryName()));
		GameRegistry.registerTileEntity(TileDispatcher.class, "tile_dispatcher");
		Transprot.linker.registerItem();
		Transprot.upgrade.registerItem();
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.dispatcher, 4), "e e", " g ", "iii", 'e', Items.ENDER_PEARL, 'g', Items.GOLD_INGOT, 'i', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.linker), "i  ", " p ", "  i", 'p', Items.PAPER, 'i', Items.IRON_INGOT);
		GameRegistry.addShapelessRecipe(new ItemStack(Transprot.upgrade, 1, 0), Items.REDSTONE, Items.GOLD_NUGGET, Items.PAPER, Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.upgrade, 1, 1), "ueu", 'u', new ItemStack(Transprot.upgrade, 1, 0), 'e', Items.GOLD_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.upgrade, 1, 2), "ueu", 'u', new ItemStack(Transprot.upgrade, 1, 1), 'e', Items.DIAMOND);
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.upgrade, 1, 3), "ueu", 'u', new ItemStack(Transprot.upgrade, 1, 2), 'e', Items.EMERALD);
	}

	public void init(FMLInitializationEvent event) {
		PacketHandler.registerMessage(ParticleMessage.class, Side.CLIENT);
		NetworkRegistry.INSTANCE.registerGuiHandler(Transprot.instance, this);
	}

	public void postInit(FMLPostInitializationEvent event) {
		long f = Boost.defaultFrequence;
		double t = Boost.defaultSpeed;
		upgrades.put(0, new Boost((long) (f / 1.5), t * 1.5, 1));
		upgrades.put(1, new Boost((long) (f / 2.5), t * 2.0, 4));
		upgrades.put(2, new Boost((long) (f / 5.0), t * 4.0, 16));
		upgrades.put(3, new Boost((long) (f / 8.0), t * 5.0, 64));
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileDispatcher tile = (TileDispatcher) world.getTileEntity(new BlockPos(x, y, z));
		return new ContainerDispatcher(player.inventory, tile);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileDispatcher tile = (TileDispatcher) world.getTileEntity(new BlockPos(x, y, z));
		return new GuiDispatcher(player.inventory, tile);
	}

}
