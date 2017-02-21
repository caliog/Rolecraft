package org.caliog.Rolecraft.XMechanics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class RolecraftConfig {

	public static YamlConfiguration config;

	@SuppressWarnings("deprecation")
	public static void init() {
		config = YamlConfiguration.loadConfiguration(new File(FilePath.config));
		InputStream stream = new FileCreator().getClass().getResourceAsStream("config.yml");
		if (stream == null)
			return;
		YamlConfiguration def = YamlConfiguration.loadConfiguration(stream);
		config.addDefaults(def);
		config.options().copyDefaults(true);

		try {
			File f = new File(FilePath.config);
			String str = config.saveToString();
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			while (str.contains("comment")) {
				str = str.replace(str.substring(str.indexOf("comment"), str.indexOf(": '#") + 3), "");
				str = str.replaceFirst("#'", "");
			}
			bf.write(str);
			bf.close();
		} catch (IOException e) {
			Debugger.exception("RolecraftConfig.init threw an exception:", e.getMessage());
			e.printStackTrace();
		}

	}

	public static String getLangCode() {
		String l = config.getString("lang", "en");
		if (l.length() != 2) {
			Debugger.info(LogTitle.NONE, "Could not identify lang code in config.yml.");
			Manager.plugin.getLogger().warning("Could not identify lang code in config.yml. en is default.");
			return "en";
		}
		return l;
	}

	public static Material getCurrency() {
		try {
			return Material.valueOf(config.getString("currency"));
		} catch (Exception e) {
			Debugger.exception("RolecraftConfig.getCurrency threw an exception:", e.getMessage());
			Manager.plugin.getLogger().warning("Could not load currency in config.yml!");
			Manager.plugin.getLogger().warning("Using default currency: Emeralds!");
			return Material.EMERALD;
		}
	}

	public static String getDefaultClass() {
		String str = config.getString("default-class");
		if (ClazzLoader.isClass(str))
			return str;
		else if ((str = ClazzLoader.getFirstClass()) != null) {
			Manager.plugin.getLogger().warning("Could not find your default class in classes.yml! Using " + str + " instead.");
			return str;
		} else {
			Manager.plugin.getLogger().log(Level.WARNING,
					"Could not find default class (config.yml) in your classes.yml! Disabling Rolecraft...");
			Manager.plugin.getServer().getPluginManager().disablePlugin(Manager.plugin);
			return null;
		}
	}

	public static List<String> getDisabledWorlds() {
		List<String> def = new ArrayList<String>();
		def.add("world_nether");
		def.add("world_the_end");
		if (!config.isSet("disable-worlds"))
			return def;
		return config.getStringList("disable-worlds");
	}

	public static boolean isWorldDisabled(World world) {
		return Manager.isWorldDisabled(world);
	}

	public static boolean isLevelLinear() {
		return config.getBoolean("linear-experience", false);
	}

	public static int getRemoveItemTime() {
		return config.getInt("remove-item-delay", 120);
	}

	public static boolean isFireworkEnabled() {
		return config.getBoolean("firework", true);
	}

	public static float getExpLoseRate() {
		int r = config.getInt("exp-loss-on-death", 0);
		return r / 100F;
	}

	public static boolean spellsEnabled() {
		return config.getBoolean("enable-spells", true);
	}

	public static boolean keepInventory() {
		return config.getBoolean("keep-inventory", true);
	}

	public static int getDefaultSpawnTime() {
		return config.getInt("mob-spawn-time", 15);
	}

	public static boolean isMICDisabled() {
		if (config == null)
			return true;
		return config.getBoolean("disable-mic", false);
	}

	public static int getMaxBackups() {
		return config.getInt("max-backups", 20);
	}

	public static long getBackupTime() {
		return config.getInt("backup-time", 60);
	}

	public static boolean isSpellCollectionEnabled() {
		if (config == null)
			return true;
		return config.getBoolean("enable-spell-collection", true);
	}

	public static HashMap<String, Integer> getGroupMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ConfigurationSection sec = config.getConfigurationSection("group-assignment");
		for (String key : sec.getKeys(false))
			if (!key.equals("ignore"))
				map.put(key, sec.getInt(key));
		return map;
	}

	public static String[] getIgnoredPlayers() {
		String s = config.getString("group-assignemt.ignore");
		String[] r;
		if (s != null && s.length() != 0)
			r = s.replaceAll(" ", "").split(",");
		else
			r = null;
		return r;
	}

	public static String getChatFormat() {
		String cf = config.getString("chat-format", null);
		if (cf == null || cf.equalsIgnoreCase("none"))
			return null;
		return cf;
	}

	public static boolean isUpdateEnabled() {
		return config.getBoolean("enable-update-check", true);
	}

	public static boolean isNaturalSpawnDisabled(String world) {
		return config.getBoolean("disable-natural-spawn." + world, false);
	}

	public static boolean isLootChestEnabled() {
		return config.getBoolean("enable-loot-chest");
	}

	public static boolean isDebugging() {
		return config.getBoolean("enable-debugging", false);
	}

}
