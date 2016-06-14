package mrriegel.decoy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Decoy.MODID, name = Decoy.MODNAME, version = Decoy.VERSION)
public class Decoy {
	public static final String MODID = "decoy";
	public static final String VERSION = "1.0.0";
	public static final String MODNAME = "Decoy";

	@Instance(Decoy.MODID)
	public static Decoy instance;
	public static final SimpleNetworkWrapper DISPATCHER = new SimpleNetworkWrapper(MODID);

	public static final Item decoy = new ItemDecoy();

	@SidedProxy(clientSide = "mrriegel.decoy.ClientProxy", serverSide = "mrriegel.decoy.CommonProxy")
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