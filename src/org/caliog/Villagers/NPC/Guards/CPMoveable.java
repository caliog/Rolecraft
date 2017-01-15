package org.caliog.Villagers.NPC.Guards;

import org.bukkit.Location;
import org.caliog.myRPG.Manager;
import org.caliog.npclib.Moveable;

public class CPMoveable extends Moveable {

	private Location location;
	private CheckpointPath cpPath = null;
	private boolean running;

	public CPMoveable(Location loc) {
		this.location = loc;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void walkPath() {
		if (this.cpPath != null && bukkitEntity != null)
			this.cpPath.walkPath(this);
	}

	public CheckpointPath getPath() {
		return this.cpPath;
	}

	public String getPathName() {
		if (this.cpPath == null)
			return "null";
		else
			return cpPath.getName();
	}

	public void setPath(CheckpointPath cpPath) {
		removePath();
		this.cpPath = cpPath;
		walkPath();
	}

	public boolean createPath(String pathName) {
		if (pathName == null || pathName.equals("null"))
			return false;
		if (pathName.length() > 0) {
			this.setPath(new CheckpointPath(pathName));
			if (!cpPath.isLoaded())
				Manager.plugin.getLogger().warning("Failed to load Path: " + pathName);
			else {
				return true;
			}
		}
		return false;

	}

	public void removePath() {
		if (this.cpPath != null)
			this.cpPath.setRun(false);
		this.cpPath = null;

	}

	@Override
	public boolean isRunning() {
		return running;

	}

	public void setRunning(boolean t) {
		this.running = t;
	}

}
