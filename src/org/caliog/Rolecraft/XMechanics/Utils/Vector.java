package org.caliog.Rolecraft.XMechanics.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Vector {
	private static final String regex = "_";
	private String world;
	private int x;
	private int y;
	private int z;
	private boolean isNull = false;

	public Vector(Location l) {
		if (l != null) {
			this.x = l.getBlockX();
			this.y = l.getBlockY();
			this.z = l.getBlockZ();
			this.world = l.getWorld().getName();
		} else {
			this.isNull = true;
		}
	}

	public Vector(int x, int y, int z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		if (Bukkit.getWorld(world) == null)
			isNull = true;
	}

	public double distanceSquared(Vector v) {
		if (isNull || v.isNull())
			return Double.POSITIVE_INFINITY;
		if (!v.getWorld().equals(world))
			return Double.POSITIVE_INFINITY;
		int dx = v.x - this.x;
		int dy = v.y - this.y;
		int dz = v.z - this.z;
		return dx * dx + dy * dy + dz * dz;
	}

	public double distanceSquared(Location location) {
		return distanceSquared(new Vector(location));
	}

	public Location toLocation() {
		if (this.isNull) {
			return null;
		}
		World w = Bukkit.getWorld(world);
		if (w == null) {
			return null;
		}
		Location loc = new Location(w, this.x, this.y, this.z);
		return loc;
	}

	public String toString() {
		if (this.isNull) {
			return "null";
		}
		return this.x + regex + this.y + regex + this.z + regex + this.world;
	}

	public static Vector fromString(String s) {
		s = s.replaceAll(" ", "");
		try {
			if ((s == null) || (s.equals("null"))) {
				return new Vector(null);
			} else {
				String[] split = s.split(regex);
				if (split.length != 4)
					return new Vector(null);
				return new Vector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3]);
			}
		} catch (NumberFormatException e) {
			return new Vector(null);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector)
			return equals((Vector) obj);
		else
			return false;
	}

	public boolean equals(Vector v) {
		return v.distanceSquared(this) == 0.0D;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean isNull() {
		return isNull;
	}
}
