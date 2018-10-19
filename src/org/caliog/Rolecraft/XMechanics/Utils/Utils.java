package org.caliog.Rolecraft.XMechanics.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;

public abstract class Utils {

	public static String cleanString(String str) {
		char[] abc = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', 'ü', 'ä', 'ö' };

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

	public static List<Player> getBukkitPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (World w : Bukkit.getWorlds())
			players.addAll(w.getPlayers());
		return players;
	}

	public static boolean isNotNegativeInteger(String string) {
		if (isInteger(string))
			if (Integer.parseInt(string) >= 0)
				return true;
		return false;
	}

	public static boolean isPositiveInteger(String string) {
		if (isInteger(string))
			if (Integer.parseInt(string) > 0)
				return true;
		return false;
	}

	public static boolean isInteger(String string) {
		if (string == null)
			return false;
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static CommandField[] addElementToArray(CommandField[] a, CommandField e) {
		if (a == null) {
			CommandField[] array = { e };
			return array;
		} else {
			CommandField[] array = new CommandField[a.length + 1];
			System.arraycopy(a, 0, array, 0, a.length);
			array[array.length - 1] = e;
			return array;
		}
	}

	public static String[] removeNull(String[] a) {
		int counter = 0;
		for (int i = 0; i < a.length; i++)
			if (a[i] == null)
				counter++;
		String b[] = new String[a.length - counter];
		int j = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null) {
				b[j] = a[i];
				j++;
			}
		}

		return b;
	}

	public static String readable(Material mat) {
		String[] split = mat.name().split("_");
		String name = "";
		for (String s : split)
			name += s.substring(0, 1) + s.substring(1).toLowerCase() + " ";
		return name.trim();

	}
}
