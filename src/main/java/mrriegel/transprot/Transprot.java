package mrriegel.transprot;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Transprot.MODID, name = Transprot.MODNAME, version = Transprot.VERSION)
public class Transprot {
	public static final String MODID = "transprot";
	public static final String VERSION = "1.0.0";
	public static final String MODNAME = "Transprot";

	@Instance(Transprot.MODID)
	public static Transprot instance;
	public static final SimpleNetworkWrapper DISPATCHER = new SimpleNetworkWrapper(MODID);

	public static final Block dispatcher = new BlockDispatcher();
	public static final Item linker = new ItemLinker();

	@SidedProxy(clientSide = "mrriegel.transprot.ClientProxy", serverSide = "mrriegel.transprot.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}