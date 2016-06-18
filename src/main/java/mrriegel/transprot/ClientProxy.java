package mrriegel.transprot;

import mrriegel.transprot.Transprot.Boost;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Transprot.dispatcher), 0, new ModelResourceLocation(Transprot.dispatcher.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Transprot.linker, 0, new ModelResourceLocation(Transprot.linker.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public void tooltip(ItemTooltipEvent e) {
		if (e.getEntityPlayer().openContainer instanceof ContainerDispatcher) {
			Item item = e.getItemStack().getItem();
			if (Transprot.upgrades.keySet().contains(item)) {
				Boost boost = Transprot.upgrades.get(item);
				if (boost.frequence < Boost.defaultFrequence)
					e.getToolTip().add("+ Frequence");
				else if (boost.frequence > Boost.defaultFrequence)
					e.getToolTip().add("- Frequence");
				if (boost.speed > Boost.defaultSpeed)
					e.getToolTip().add("+ Speed");
				else if (boost.speed < Boost.defaultSpeed)
					e.getToolTip().add("- Speed");
				if (boost.stackSize > Boost.defaultStackSize)
					e.getToolTip().add("+ Stack size");
				else if (boost.stackSize < Boost.defaultStackSize)
					e.getToolTip().add("- Stack size");
			}
		}
	}

}
