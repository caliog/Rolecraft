package org.caliog.myRPG.Entities;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.myConfig;
import org.caliog.myRPG.Spells.Spell;
import org.caliog.myRPG.Spells.SpellLoader;

public class ClazzLoader {

	public static YamlConfiguration classes;

	public static myClass create(Player player, String c) {
		if (isClass(c)) {
			myClass clazz = new myClass(player, c);

			ConfigurationSection config = classes.getConfigurationSection(c);
			if (!clazz.isLoaded()) {
				if (!myConfig.isWorldDisabled(clazz.getPlayer().getWorld())) {
					clazz.setLevel(1);
					clazz.getPlayer().setExp(0F);
				}
				clazz.setIntelligence(config.getInt("int"));
				clazz.setVitality(config.getInt("vit"));
				clazz.setDexterity(config.getInt("dex"));
				clazz.setStrength(config.getInt("str"));
			}

			String[] ids = { "xxx", "xxo", "xox", "oxx", "xoo", "oxo", "oox", "ooo" };
			for (String id : ids) {
				if (config.isSet("spells." + id)) {
					Spell spell = SpellLoader.load(clazz, config.getString("spells." + id));
					if (spell != null)
						clazz.addSpell(id.replaceAll("x", "1").replaceAll("o", "0"), spell);

				}
			}
			@SuppressWarnings("deprecation")
			double maxHealth = ((Damageable) player).getMaxHealth();
			clazz.getPlayer().setHealth(maxHealth);
			return clazz;
		} else {
			String cl = myConfig.getDefaultClass();
			if (cl != null && isClass(cl)) {
				Manager.plugin.getLogger().info("Could not find " + player.getName() + "'s class: " + c);
				Manager.plugin.getLogger().info("Load class " + cl + " instead.");
				return create(player, cl);
			}
		}
		Manager.plugin.getLogger().warning("Could not find a class for " + player.getName());
		Manager.plugin.getLogger().warning(player.getName() + " will not be able to use myRPG safely.");
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
}
