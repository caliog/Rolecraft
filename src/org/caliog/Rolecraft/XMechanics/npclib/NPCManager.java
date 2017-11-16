package org.caliog.Rolecraft.XMechanics.npclib;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.caliog.Rolecraft.Guards.Guard;

public abstract class NPCManager {

	public static NPCManager npcManager;

	public abstract void despawnAll();

	public abstract NPC spawnHumanNPC(String name, Location l, String id);

	public abstract void despawnById(String valueOf);

	protected abstract JavaPlugin getPlugin();

	public abstract void addGuardToPlayerList(Player player, Guard guard);

}
