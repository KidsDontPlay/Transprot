package mrriegel.decoy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		registerItemModels();
		// ClientRegistry.bindTileEntitySpecialRenderer(TileDispatcher.class,
		// new TileDispatcherRender());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new TransferRender());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Decoy.dispatcher), 0, new ModelResourceLocation(Decoy.dispatcher.getRegistryName().toString(), "inventory"));
	}

}
