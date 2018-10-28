package org.caliog.Rolecraft.Mobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Slime;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;

public class MobSpawner {
	public static Set<MobSpawnZone> zones = new HashSet<MobSpawnZone>();
	public static HashMap<String, String> mobs = new HashMap<String, String>();

	public static void loadMobs() {
		mobs.clear();
		File m = new File(FilePath.mobs);
		for (File ff : m.listFiles()) {
			if (!ff.isDirectory() && ff.getName().endsWith(".yml")) {
				YamlConfiguration c = YamlConfiguration.loadConfiguration(ff);
				if (c.isSet("name")) {
					mobs.put(ff.getName().replace(".yml", ""), c.getString("name"));
				}
			}
		}
	}

	public static void loadZones() throws IOException {
		File f = new File(FilePath.szFile);
		if (!f.exists()) {
			return;
		}

		loadMobs();

		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] a = line.split("/");
			if (a.length == 4) {
				Vector m;

				m = Vector.fromString(a[0]);
				if (m == null || m.isNull())
					continue;
				String mob = null;
				if (isMobClass(a[1])) {
					mob = getIdentifier(a[1]);
				} else {
					Debugger.warning(LogTitle.NONE, "Skipping " + a[1] + " in mob spawn zones.");
					continue;
				}
				int radius = Integer.parseInt(a[2]);
				int amount = Integer.parseInt(a[3]);
				if ((mob != null) && (m != null)) {
					zones.add(new MobSpawnZone(m, mob, radius, amount));
				}

			}
		}
		reader.close();

	}

	public static void saveZones() throws IOException {
		File f = new File(FilePath.szFile);
		f.getParentFile().mkdir();
		f.createNewFile();
		FileWriter writer = new FileWriter(f);
		String text = "";
		for (MobSpawnZone z : zones) {
			text = text + z.getM().toString() + "/" + z.getMob() + "/" + z.getRadius() + "/" + z.getAmount() + "\r";
		}
		EntityManager.killAllMobs();
		writer.write(text);
		writer.close();

		// free mobs map
		mobs.clear();
	}

	public static boolean isNearSpawnZone(Entity e) {
		Mob m = EntityManager.getMob(e.getUniqueId());
		if (m != null) {
			for (MobSpawnZone zone : zones) {
				if (zone.getM().equals(m.getSpawnZone())) {
					if (m.getSpawnZone().distanceSquared(e.getLocation()) <= 3.5D * zone.getRadius()
							* zone.getRadius()) {
						return true;
					}
					return false;
				}
			}
			return false;
		}
		return true;
	}

	public static Runnable getTask() {
		return new Runnable() {
			public void run() {
				while (MobSpawner.zones.contains(null)) {
					MobSpawner.zones.remove(null);
				}

				Set<UUID> remove = new HashSet<UUID>();
				Set<UUID> ids = new HashSet<UUID>();
				for (World w : Manager.getWorlds()) {
					if (w == null)
						continue;
					for (MobSpawnZone z : MobSpawner.zones)
						if (z.getWorld().equals(w.getName()))
							if (Math.random() < 0.6D)
								z.askForSpawn();

					Mob m;
					for (Entity e : w.getEntities()) {
						if (((e instanceof Creature)) || ((e instanceof Slime)) || ((e instanceof Ghast))) {
							m = EntityManager.getMob(e.getUniqueId());
							if (!EntityManager.isRegistered(e.getUniqueId())) {
								if (RolecraftConfig.isNaturalSpawnDisabled(w.getName()))
									e.remove();
							} else if (m != null) {
								ids.add(e.getUniqueId());
							}
						}
					}

				}
				for (Mob mob : EntityManager.getMobs()) {
					if (!ids.contains(mob.getUniqueId())) {
						remove.add(mob.getUniqueId());
					}
				}
				for (UUID id : remove) {
					EntityManager.remove(id);
				}
			}
		};
	}

	public static boolean isMobClass(String name) {
		return mobs.containsValue(name) || mobs.containsKey(name);
	}

	public static String getIdentifier(String c) {
		if (mobs.containsKey(c))
			return c;
		for (String id : mobs.keySet()) {
			if (mobs.get(id).equals(c))
				return id;
		}
		return null;
	}

	public static Set<String> getIdentSet() {
		return mobs.keySet();
	}
}
