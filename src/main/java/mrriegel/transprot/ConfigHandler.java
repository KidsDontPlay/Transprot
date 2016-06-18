package mrriegel.transprot;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static int range;
	public static boolean itemsVisible, particle, freeWay;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		range = config.getInt("range", Configuration.CATEGORY_GENERAL, 24, 2, 64, "Max distance between dispatcher and inventory.");
		itemsVisible = config.getBoolean("itemsVisible", Configuration.CATEGORY_CLIENT, true, "Items are visible.");
		particle = config.getBoolean("particle", Configuration.CATEGORY_CLIENT, true, "Particles are visible.");
		freeWay = config.getBoolean("freeWay", Configuration.CATEGORY_GENERAL, true, "If enabled items won't pass blocks.");

		if (config.hasChanged()) {
			config.save();
		}
	}

}
