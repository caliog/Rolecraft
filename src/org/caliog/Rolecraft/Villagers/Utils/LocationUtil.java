package org.caliog.Rolecraft.Villagers.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

	public static String toString(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":"
				+ loc.getPitch();
	}

	public static Location fromString(String s) {
		String[] a = s.split(":");
		if (a.length < 6)
			return null;
		if (Bukkit.getWorld(a[0]) == null)
			return null;
		Location location = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]),
				Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
		return location;
	}

	public static String readable(Location l) {
		if (l == null)
			return "";
		else
			return "[ " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + " ]";
	}
}
