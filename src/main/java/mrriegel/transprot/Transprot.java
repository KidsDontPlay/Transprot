package mrriegel.transprot;

import java.util.Map;

import com.google.common.collect.Maps;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.item.CommonItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Transprot.MODID, name = Transprot.MODNAME, version = Transprot.VERSION, dependencies = "required-after:limelib@[1.6.0,)")
public class Transprot {
	public static final String MODID = "transprot";
	public static final String VERSION = "1.5.0";
	public static final String MODNAME = "Transprot";

	@Instance(Transprot.MODID)
	public static Transprot instance;

	public static final CommonBlock dispatcher = new BlockDispatcher();
	public static final CommonItem linker = new ItemLinker();
	public static final CommonItem upgrade = new ItemUpgrade();

	public static final Map<Integer, Boost> upgrades = Maps.newHashMap();

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
		public static final long defaultFrequence = 35l;
		public static final double defaultSpeed = .03;
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