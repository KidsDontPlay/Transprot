package mrriegel.decoy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
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
		GameRegistry.register(Decoy.dispatcher);
		GameRegistry.register(new ItemBlock(Decoy.dispatcher).setRegistryName(Decoy.dispatcher.getRegistryName()));
		GameRegistry.registerTileEntity(TileDispatcher.class, "tile_dispatcher");
		GameRegistry.register(Decoy.linker);
	}

	public void init(FMLInitializationEvent event) {
		int id = 0;
		Decoy.DISPATCHER.registerMessage(SyncMessageS.Handler.class, SyncMessageS.class, id++, Side.SERVER);
		NetworkRegistry.INSTANCE.registerGuiHandler(Decoy.instance, this);
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileDispatcher tile = (TileDispatcher) world.getTileEntity(new BlockPos(x, y, z));
		return new ContainerDispatcher(player.inventory, tile.getInv());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileDispatcher tile = (TileDispatcher) world.getTileEntity(new BlockPos(x, y, z));
		return new GuiDispatcher(player.inventory, tile.getInv());
	}

}
