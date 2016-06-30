package mrriegel.transprot;

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
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new TransferRender());
		// MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Transprot.dispatcher), 0, new ModelResourceLocation(Transprot.dispatcher.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Transprot.linker, 0, new ModelResourceLocation(Transprot.linker.getRegistryName(), "inventory"));
		for (int i = 0; i < 4; i++)
			ModelLoader.setCustomModelResourceLocation(Transprot.upgrade, i, new ModelResourceLocation(Transprot.upgrade.getRegistryName() + "_" + i, "inventory"));
	}

}
