package org.caliog.Rolecraft.XMechanics.npclib.v1_12_R1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.caliog.Rolecraft.Guards.Guard;
import org.caliog.Rolecraft.XMechanics.Reflection.Reflect;
import org.caliog.Rolecraft.XMechanics.npclib.NMSUtil;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.BServer;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.BWorld;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.WorldServer;

public class NPCManager extends org.caliog.Rolecraft.XMechanics.npclib.NPCManager {

	private final HashMap<String, NPC> npcs = new HashMap<>();
	private final BServer server;
	private final Map<World, BWorld> bworlds = new HashMap<>();
	private NPCNetworkManager npcNetworkManager;
	private JavaPlugin plugin;
	private Class<?> worldServerClass, entityClass;

	public NPCManager(JavaPlugin plugin) {
		server = BServer.getInstance();
		try {
			worldServerClass = Reflect.getNMSClass("WorldServer");
			entityClass = Reflect.getNMSClass("Entity");

			npcNetworkManager = new NPCNetworkManager();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		this.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);

	}

	public BWorld getBWorld(World world) {
		BWorld bworld = bworlds.get(world);
		if (bworld != null) {
			return bworld;
		}
		bworld = new BWorld(world);
		bworlds.put(world, bworld);
		return bworld;
	}

	private class SL implements Listener {
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
			}
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onPlayerJoin(PlayerJoinEvent event) {
			Player p = event.getPlayer();
			if (p == null)
				return;

			for (NPC npc : npcs.values()) {
				NMSUtil.sendPacketsTo(p, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
						new EntityPlayer[] { (EntityPlayer) npc.getEntity() }));
			}

		}

	}

	public boolean containsNPC(String name) {
		return npcs.containsKey(name);
	}

	public org.caliog.Rolecraft.XMechanics.npclib.NPC spawnHumanNPC(String name, Location l, String id) {
		if (npcs.containsKey(id)) {
			Bukkit.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return npcs.get(id);
		}
		if (name.length() > 16) { // Check and nag if name is too long, spawn
			// NPC anyway with shortened name.
			final String tmp = name.substring(0, 16);
			Bukkit.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			Bukkit.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		final BWorld world = getBWorld(l.getWorld());
		final NPC npc = new NPC(NMSUtil.getUtil().createNPCEntity(this, world, name));
		npc.moveTo(l);
		try {
			Object entityHuman = NMSUtil.getUtil().getPlayerHandle((Player) npc.getBukkitEntity());
			worldServerClass.getMethod("addEntity", entityClass).invoke(world.getWorldServer(), entityHuman);

			List<?> players = (List<?>) worldServerClass.getField("players").get(world.getWorldServer());
			players.remove(entityHuman);

			NMSUtil.sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutPlayerInfo(
					PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] { (EntityPlayer) npc.getEntity() }));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
				| NoSuchFieldException e) {
			e.printStackTrace();
		}
		npcs.put(id, npc);
		return npc;
	}

	public void despawnById(String id) {
		final NPC npc = npcs.get(id);
		if (npc != null) {
			npcs.remove(id);
			npc.removeFromWorld();
			NMSUtil.sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutPlayerInfo(
					PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] { (EntityPlayer) npc.getEntity() }));

		}
	}

	public void despawnHumanByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); // Ensure you can still despawn
		}
		final HashSet<String> toRemove = new HashSet<>();
		for (final String n : npcs.keySet()) {
			final NPC npc = npcs.get(n);
			if (npc != null && npc instanceof NPC) {
				if (((NPC) npc).getName().equals(npcName)) {
					toRemove.add(n);
					npc.removeFromWorld();
				}
			}
		}
		for (final String n : toRemove) {
			npcs.remove(n);
		}
	}

	public void despawnAll() {
		for (final NPC npc : npcs.values()) {
			if (npc != null) {
				npc.removeFromWorld();
			}
		}
		npcs.clear();
	}

	public NPC getNPC(String id) {
		return npcs.get(id);
	}

	public boolean isNPC(org.bukkit.entity.Entity e) {
		return ((CraftEntity) e).getHandle() instanceof NPCEntity;
	}

	public List<NPC> getHumanNPCByName(String name) {
		final List<NPC> ret = new ArrayList<>();
		final Collection<NPC> i = npcs.values();
		for (final NPC e : i) {
			if (e instanceof NPC) {
				if (((NPC) e).getName().equalsIgnoreCase(name)) {
					ret.add(e);
				}
			}
		}
		return ret;
	}

	public List<NPC> getNPCs() {
		return new ArrayList<>(npcs.values());
	}

	public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
		if (e instanceof HumanEntity) {
			for (final String i : npcs.keySet()) {
				if (npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) {
					return i;
				}
			}
		}
		return null;
	}

	public void rename(String id, String name) {
		if (name.length() > 16) { // Check and nag if name is too long, spawn
			// NPC anyway with shortened name.
			final String tmp = name.substring(0, 16);
			Bukkit.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			Bukkit.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		final NPC npc = (NPC) getNPC(id);
		npc.setName(name);
		final BWorld b = getBWorld(npc.getBukkitEntity().getLocation().getWorld());
		final WorldServer s = (WorldServer) b.getWorldServer();
		try {
			Method m = s.getClass().getDeclaredMethod("d", new Class[] { Entity.class });
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
			m = s.getClass().getDeclaredMethod("c", new Class[] { Entity.class });
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}

	public BServer getServer() {
		return server;
	}

	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}

	@Override
	public JavaPlugin getPlugin() {
		return plugin;
	}

	@Override
	public void addGuardToPlayerList(Player player, Guard guard) {
		NMSUtil.sendPacketsTo(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
				new EntityPlayer[] { (EntityPlayer) guard.getNpc().getEntity() }));
	}
}
