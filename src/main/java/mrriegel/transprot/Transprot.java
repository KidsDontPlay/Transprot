package mrriegel.transprot;

import java.util.Map;

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

import com.google.common.collect.Maps;

@Mod(modid = Transprot.MODID, name = Transprot.MODNAME, version = Transprot.VERSION)
public class Transprot {
	public static final String MODID = "transprot";
	public static final String VERSION = "1.0.1";
	public static final String MODNAME = "Transprot";

	@Instance(Transprot.MODID)
	public static Transprot instance;
	public static final SimpleNetworkWrapper DISPATCHER = new SimpleNetworkWrapper(MODID);

	public static final Block dispatcher = new BlockDispatcher();
	public static final Item linker = new ItemLinker();

	public static final Map<Item, Boost> upgrades = Maps.newHashMap();

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

	static class Boost {
		public static final long defaultFrequence = 40l;
		public static final double defaultSpeed = .02;
		public static final int defaultStackSize = 1;

		public final long frequence;
		public final double speed;
		public final int stackSize;

		public Boost(long frequence, double speed, int stackSize) {
			this.frequence = frequence;
			this.speed = speed;
			this.stackSize = stackSize;
		}

	}

}