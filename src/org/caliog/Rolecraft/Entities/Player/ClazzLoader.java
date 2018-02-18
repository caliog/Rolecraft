package org.caliog.Rolecraft.Entities.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class ClazzLoader {

	public final static String[] ids = { "xxx", "xxo", "xox", "oxx", "xoo", "oxo", "oox", "ooo" };

	public static YamlConfiguration classes;

	public static HashMap<String, List<String>> spellMap = new HashMap<String, List<String>>();

	public static void init() {
		ClazzLoader.classes = YamlConfiguration.loadConfiguration(new File(FilePath.classes));
		for (String name : classes.getKeys(false)) {
			List<String> list = new ArrayList<String>();
			for (String id : ids)
				if (classes.isSet(name + ".spells." + id)) {
					list.add(classes.getString(name + ".spells." + id));
				}
			spellMap.put(name, list);
		}
	}

	public static RolecraftPlayer create(Player player, String c) {
		if (isClass(c)) {
			RolecraftPlayer clazz = new RolecraftPlayer(player, c);

			ConfigurationSection config = classes.getConfigurationSection(c);
			if (!clazz.isLoaded()) {
				if (!RolecraftConfig.isWorldDisabled(clazz.getPlayer().getWorld())) {
					clazz.setLevel(1);
					clazz.getPlayer().setExp(0F);
				}
				clazz.setIntelligence(config.getInt("int"));
				clazz.setVitality(config.getInt("vit"));
				clazz.setDexterity(config.getInt("dex"));
				clazz.setStrength(config.getInt("str"));
			}

			for (String id : ids) {
				if (config.isSet("spells." + id)) {
					clazz.addSpell(id.toLowerCase().replaceAll("x", "1").replaceAll("o", "0"), config.getString("spells." + id));
				}
			}
			@SuppressWarnings("deprecation")
			double maxHealth = ((Damageable) player).getMaxHealth();
			clazz.getPlayer().setHealth(maxHealth);
			return clazz;
		} else {
			String cl = RolecraftConfig.getDefaultClass();
			if (cl != null && isClass(cl)) {
				Manager.plugin.getLogger().info("Could not find " + player.getName() + "'s class: " + c);
				Manager.plugin.getLogger().info("Load class " + cl + " instead.");
				return create(player, cl);
			}
		}
		Manager.plugin.getLogger().warning("Could not find a class for " + player.getName());
		Manager.plugin.getLogger().warning(player.getName() + " will not be able to use Rolecraft safely.");
		return null;
	}

	public static boolean isClass(String name) {
		if (name == null)
			return false;
		return classes.isConfigurationSection(name);
	}

	public static String getFirstClass() {
		Set<String> keys = classes.getKeys(false);
		if (keys != null && !keys.isEmpty())
			for (String k : keys)
				return k;
		return null;
	}

	public static String getClassColor(String name) {
		return classes.getString(name + ".chat-color");
	}

	public static String[] getClassesAsArray() {
		Set<String> keys = classes.getKeys(false);
		String[] r = new String[keys.size()];
		int c = 0;
		for (String k : keys) {
			r[c] = k;
			c++;
		}
		return r;
	}

	public static List<String> getSpells(String name) {
		List<String> empty = new ArrayList<String>();
		return spellMap.containsKey(name) ? spellMap.get(name) : empty;
	}

}
