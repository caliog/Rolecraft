package org.caliog.myRPG.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;

public class Utils {
	public static String cleanString(String str) {
		char[] abc = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', ' ', '�', '�', '�' };

		String newString = "";
		for (int i = 0; i < str.length(); i++) {
			char u = str.charAt(i);
			boolean found = false;
			for (int j = 0; j < abc.length; j++) {
				if (String.valueOf(u).toLowerCase().equals(String.valueOf(abc[j]))) {
					found = true;
				}
			}
			if (found) {
				newString = newString + u;
			}
		}
		return newString;
	}

	public static Player getPlayer(UUID id) {
		return Bukkit.getPlayer(id);
	}

	public static boolean isBukkitClass(String string) {
		try {
			Manager.plugin.getClass().getClassLoader().loadClass(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static List<Player> getBukkitPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (World w : Bukkit.getWorlds())
			players.addAll(w.getPlayers());
		return players;
	}

	public static boolean isBukkitMethod(String c, String m, Class<?>... param) {
		try {
			Class<?> cl = Manager.plugin.getClass().getClassLoader().loadClass(c);
			cl.getMethod(m, param);
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static boolean isBukkitField(String c, String f) {
		try {
			Class<?> cl = Manager.plugin.getClass().getClassLoader().loadClass(c);
			cl.getField(f);
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
