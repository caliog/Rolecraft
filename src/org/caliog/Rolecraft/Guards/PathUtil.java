package org.caliog.Rolecraft.Guards;

import org.bukkit.Location;

public class PathUtil {

	public static void createPath(String name, int startdelay, int cpdelay, Location loc) {
		new CheckpointPath(name, startdelay, cpdelay, loc);
	}

	public static void setPath(String name, int cp, Location loc) {
		CheckpointPath path = getPath(name);
		if (path != null && path.isLoaded())
			path.setCheckpoint(loc, cp);
	}

	public static CheckpointPath getPath(String string) {
		return new CheckpointPath(string);
	}

	public static void removePath(String string) {
		getPath(string).removePath();
	}

}
