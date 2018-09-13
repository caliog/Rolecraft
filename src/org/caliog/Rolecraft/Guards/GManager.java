package org.caliog.Rolecraft.Guards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.Utils.LocationUtil;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.PlayerList;
import org.caliog.Rolecraft.XMechanics.npclib.NPCManager;

public class GManager {

	private static List<Guard> guards = new ArrayList<Guard>();
	private static File f = new File(FilePath.villagerDataNPCFile);
	private static HashMap<UUID, Set<UUID>> farAway = new HashMap<UUID, Set<UUID>>();

	public static void load() throws IOException {

		if (!f.exists())
			f.createNewFile();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String key = line.split("=")[0], values[] = line.split("=")[1].split("&");
			String name = values[0];
			Location loc = LocationUtil.fromString(values[1]);
			String attacking = values[2];
			String path = values[3];
			String eq = values[4];
			if (NPCManager.npcManager == null) {
				Manager.plugin.getLogger()
						.warning("Failed to create guard. ( " + Manager.plugin.getBukkitVersion() + ") does not support guards.");
				reader.close();
				return;
			}
			Guard guard = new Guard(name, loc, Integer.parseInt(key), eq);
			guard.setAttackings(attacking);
			guard.createPath(path);
			guards.add(guard);
		}

		reader.close();
	}

	public static void save() throws IOException {
		FileWriter writer = new FileWriter(f);
		String text = "";
		for (Guard g : guards) {
			text += g.getId() + "=" + g.getName() + "&" + LocationUtil.toString(g.getLocation()) + "&" + g.getAttackings() + "&"
					+ g.getPathName() + "&" + g.getEquipmentString() + "\r";
		}
		writer.write(text);
		writer.close();
		if (NPCManager.npcManager != null)
			NPCManager.npcManager.despawnAll();
		farAway.clear();
	}

	public static Guard getGuard(UUID entityId) {
		for (Guard guard : guards) {
			if (guard.getUniqueId() == entityId) {
				return guard;
			}
		}
		return null;
	}

	public static Guard getClosestGuard(Location location) {
		Guard guard = null;
		double distance = 400;
		for (Guard g : guards) {
			if (location.getWorld().equals(g.getLocation().getWorld())) {
				double d = g.getNpc().getBukkitEntity().getLocation().distanceSquared(location);
				if (d < distance) {
					distance = d;
					guard = g;
				}
			}
		}
		return guard;
	}

	public static void remove(Guard g) {
		if (g != null && NPCManager.npcManager != null)
			NPCManager.npcManager.despawnById(String.valueOf(g.getId()));
		guards.remove(g);

	}

	public static boolean isGuard(Entity e) {
		for (Guard guard : guards) {
			if (guard.getUniqueId() == e.getUniqueId())
				return true;
		}
		return false;
	}

	public static boolean isUsedId(int id) {
		for (Guard g : guards) {
			if (g.getId() == id)
				return true;
		}
		return false;
	}

	public static void createNewGuard(String name, Location loc) {
		int id = 1;
		while (isUsedId(id)) {
			id++;
		}
		if (NPCManager.npcManager == null) {
			Manager.plugin.getLogger()
					.warning("Failed to create guard. ( " + Manager.plugin.getBukkitVersion() + " does not support guards.");
			return;
		}
		Guard g = new Guard(name, loc, id, null);
		guards.add(g);
		PlayerList.refreshList();
	}

	public static List<Guard> getGuards() {
		return guards;
	}

	public static void doLogics(long timer) {
		GuardWatcher.run();

		// This should be a temporary solution (not compatible with v1.11.2)
		if (timer % 12 == 0) {
			Collection<? extends Player> list = Bukkit.getOnlinePlayers();
			for (Guard g : guards) {
				for (Player p : list) {
					if (p.getWorld().equals(g.getLocation().getWorld())) {
						Set<UUID> u = new HashSet<UUID>();
						if (farAway.containsKey(g.getUniqueId())) {
							u = farAway.get(g.getUniqueId());
						}
						if (p.getLocation().distanceSquared(g.getEntityLocation()) > 2500) {
							u.add(p.getUniqueId());
						} else if (u.contains(p.getUniqueId())) {
							p.hidePlayer(Manager.plugin, (Player) g.getBukkitEntity());
							p.showPlayer(Manager.plugin, (Player) g.getBukkitEntity());
							PlayerList.refreshList(p);
							u.remove(p.getUniqueId());
						}
						farAway.put(g.getUniqueId(), u);
					}
				}
			}
		}
	}

}
