package mrriegel.decoy;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.refreshConfig(event.getSuggestedConfigurationFile());
		GameRegistry.register(Decoy.dispatcher);
		GameRegistry.register(new ItemBlock(Decoy.dispatcher).setRegistryName(Decoy.dispatcher.getRegistryName()));
		GameRegistry.registerTileEntity(TileDispatcher.class, "tile_dispatcher");
	}

	public void init(FMLInitializationEvent event) {
		int id = 0;
		Decoy.DISPATCHER.registerMessage(SyncMessageS.Handler.class, SyncMessageS.class, id++, Side.SERVER);
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

}
