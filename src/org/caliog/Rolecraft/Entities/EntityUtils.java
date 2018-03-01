package org.caliog.Rolecraft.Entities;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Mobs.MobSpawner;

public class EntityUtils {

	public static String getBar(double h, double mh) {
		double p = h / mh;
		mh = 16.0D;
		String bar = "";
		for (double i = 1.0D; i <= mh; i += 1.0D) {
			if (i / mh <= p) {
				bar = bar + ChatColor.RED + "♥";
			} else {
				bar = bar + ChatColor.GOLD + "♥";
			}
		}
		return bar;
	}

	public static boolean isMobClass(String name) {
		return MobSpawner.isMobClass(name);
	}

	public static Entity getEntity(UUID id) {
		for (World w : Manager.getWorlds())
			getEntity(id, w);
		return null;
	}

	public static Entity getEntity(UUID id, World w) {
		if (w != null)
			for (Entity entity : w.getEntities()) {
				if ((entity.getUniqueId().equals(id))) {
					return entity;
				}
			}
		return null;
	}
}
