package org.caliog.Rolecraft.Entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.Mobs.MobInstance;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;

public class EntityManager {
	private static List<Mob> mobs = new ArrayList<Mob>();
	private static Set<UUID> register = new HashSet<UUID>();

	public static void save() throws IOException {
		File f = new File(FilePath.mobsFile);
		FileWriter writer = new FileWriter(f);
		String text = "";
		for (World w : Manager.getWorlds())
			if (w != null)
				for (Entity e : w.getEntities()) {
					Mob m = getMob(e.getUniqueId());
					if (m != null) {
						text = text + m.getName() + "=" + m.getUniqueId().toString() + "=" + m.getSpawnZone().toString() + "\r";
					}
				}

		writer.write(text);
		writer.close();
		killAllMobs();
		register.clear();
	}

	public static void load() throws Exception {
		File f = new File(FilePath.mobsFile);
		if (!f.exists()) {
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String m = line.split("=")[0];
			if (EntityUtils.isMobClass(m)) {
				UUID uuid = UUID.fromString(line.split("=")[1]);

				mobs.add(new MobInstance(m, uuid, Vector.fromString(line.split("=")[2])));
				register(uuid);
				for (World w : Manager.getWorlds())
					if (w != null)
						for (Entity entity : w.getEntities()) {

							if ((entity.getUniqueId().equals(uuid)) && (!MobSpawner.isNearSpawnZone(entity))) {
								remove(uuid);
								entity.remove();
							}

						}
			}
		}
		reader.close();
		f.delete();
	}

	public static boolean remove(UUID entityId) {
		for (Mob m : mobs) {
			if (m.getUniqueId().equals(entityId)) {
				m.delete();
				mobs.remove(m);
				unregister(m.getUniqueId());
				return true;
			}
		}
		return false;
	}

	public static void register(Mob mob) {
		remove(mob.getUniqueId());
		register(mob.getUniqueId());
		mobs.add(mob);
	}

	public static Mob getMob(UUID entityId) {
		for (Mob m : mobs) {
			if (m.getUniqueId().equals(entityId)) {
				return m;
			}
		}
		return null;
	}

	public static void register(UUID id) {
		register.add(id);
	}

	public static void unregister(UUID id) {
		register.remove(id);
	}

	public static boolean isRegistered(UUID uuid) {
		return register.contains(uuid);
	}

	public static List<Mob> getMobs() {
		return mobs;
	}

	public static void setMobs(List<Mob> mobs) {
		EntityManager.mobs = mobs;
	}

	public static void killAllMobs() {
		for (Mob m : mobs) {
			m.delete();
			unregister(m.getUniqueId());
		}
		mobs.clear();

	}
}
