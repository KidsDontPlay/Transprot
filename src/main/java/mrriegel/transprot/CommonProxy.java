package mrriegel.transprot;

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
		GameRegistry.register(Transprot.linker);
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.dispatcher, 3), "e e", " g ", "iii", 'e', Items.ENDER_PEARL, 'g', Items.GOLD_INGOT, 'i', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(Transprot.linker), "i  ", " p ", "  i", 'p', Items.PAPER, 'i', Items.IRON_INGOT);
	}

	public void init(FMLInitializationEvent event) {
		int id = 0;
		Transprot.DISPATCHER.registerMessage(ButtonMessage.Handler.class, ButtonMessage.class, id++, Side.SERVER);
		NetworkRegistry.INSTANCE.registerGuiHandler(Transprot.instance, this);
		// MinecraftForge.EVENT_BUS.register(this);
	}

	public void postInit(FMLPostInitializationEvent event) {
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
