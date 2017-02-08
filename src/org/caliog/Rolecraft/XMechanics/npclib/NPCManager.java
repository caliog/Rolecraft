package org.caliog.Rolecraft.XMechanics.npclib;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class NPCManager {

	public static NPCManager npcManager;

	public abstract void despawnAll();

	public abstract NPC spawnHumanNPC(String name, Location l, String id);

	public abstract void despawnById(String valueOf);

	protected abstract JavaPlugin getPlugin();

}
