package org.caliog.Rolecraft.Villagers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Guards.Paths.CheckpointPath;
import org.caliog.Rolecraft.Villagers.NPC.Priest;
import org.caliog.Rolecraft.Villagers.NPC.Trader;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.NPC.Villager.VillagerType;
import org.caliog.Rolecraft.Villagers.Quests.QuestManager;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestStatus;
import org.caliog.Rolecraft.Villagers.Quests.Quest;
import org.caliog.Rolecraft.Villagers.Utils.Recipe;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.LocationUtil;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;
import org.caliog.Rolecraft.XMechanics.Utils.IO.DataSaver;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;

public class VManager {

	private static List<Villager> villagers = new ArrayList<Villager>();
	private static boolean loaded = false;

	public static Villager getVillager(UUID entityId) {
		for (Villager v : villagers)
			if (v.getUniqueId().equals(entityId))
				return v;

		return null;
	}

	public static Villager spawnVillager(Location location, String name, VillagerType type) {
		if (location == null)
			return null;

		World w = location.getWorld();
		Entity entity = w.spawnEntity(location, EntityType.VILLAGER);

		if (name != null && name.equals("null"))
			name = null;
		Villager villager;
		if (type.equals(VillagerType.PRIEST))
			villager = new Priest((org.bukkit.entity.Villager) entity, location, name);
		else if (type.equals(VillagerType.TRADER))
			villager = new Trader((org.bukkit.entity.Villager) entity, location, name);
		else
			villager = new Villager((org.bukkit.entity.Villager) entity, type, location, name);

		EntityManager.register(villager.getUniqueId());

		villagers.add(villager);

		// LOG
		Debugger.info(LogTitle.SPAWN, "Spawning villager (name=%s) at:", name, new Vector(location).toString());

		return villager;

	}

	public static Trader spawnTrader(Location location, String name, Recipe recipe) {
		if (location == null)
			return null;

		World w = location.getWorld();

		Entity entity = w.spawnEntity(location, EntityType.VILLAGER);

		if (name != null && name.equals("null"))
			name = null;

		Trader trader = new Trader((org.bukkit.entity.Villager) entity, location, name);

		trader.setRecipe(recipe);

		EntityManager.register(trader.getUniqueId());

		villagers.add(trader);

		// LOG
		Debugger.info(LogTitle.SPAWN, "Spawning villager (trader) (name=%s) at:", name,
				new Vector(location).toString());

		return trader;
	}

	public static Villager getClosestVillager(Location location) {
		Villager villager = null;
		double distance = 400;
		for (Villager v : villagers) {
			if (location.getWorld().equals(v.getVillager().getWorld())) {
				double d = v.getVillager().getLocation().distanceSquared(location);
				if (d < distance) {
					distance = d;
					villager = v;
				}
			}
		}
		return villager;
	}

	public static Trader getClosestTrader(Location location) {
		Trader villager = null;
		double distance = 100;
		for (Villager v : villagers) {
			if (!v.getType().equals(VillagerType.TRADER))
				continue;
			if (location.getWorld().equals(v.getVillager().getWorld())) {
				double d = v.getVillager().getLocation().distanceSquared(location);
				if (d < distance) {
					distance = d;
					villager = (Trader) v;
				}
			}
		}
		return villager;
	}

	public static void load() throws IOException {
		File f = new File(FilePath.villagerDataVillagerFile);
		if (!f.exists())
			f.createNewFile();
		BufferedReader reader = new BufferedReader(new FileReader(f));

		String line = "";
		while ((line = reader.readLine()) != null) {
			Recipe recipe = null;
			String[] a = line.split("&");
			String name = a[0];
			Location location = LocationUtil.fromString(a[1]);
			VillagerType type = VillagerType.valueOf(a[2]);
			// remove
			List<String> list = DataSaver.getStringList(a[3]);
			List<String> quests = DataSaver.getStringList(a[4]);
			Profession prof = Profession.valueOf(a[5]);
			String path = a[6];
			Villager v = null;
			if (type.equals(VillagerType.TRADER)) {
				if (a.length > 7) {
					recipe = Recipe.fromString(a[7]);
				}
				v = spawnTrader(location, name, recipe);
				if (a.length > 8) {
					((Trader) v).loadTradeMenu(a[8]);
				}
			} else
				v = spawnVillager(location, name, type);

			if (v == null)
				continue;
			if (!type.equals(VillagerType.PRIEST))
				for (String text : list)
					v.addText(Integer.parseInt(text.split(":")[0]), text.split(":")[1]);

			for (String q : quests)
				v.addQuest(q);

			v.setProfession(prof);

			if (path != null && !path.equals("null"))
				v.setPath(new CheckpointPath(path));

		}

		reader.close();
		loaded = true;
	}

	public static void save() throws IOException {
		File f = new File(FilePath.villagerDataVillagerFile);
		FileWriter writer = new FileWriter(f);
		for (Villager v : villagers) {
			v.save(writer);
			v.despawn();
		}
		writer.close();
		villagers.clear();

	}

	public static void remove(UUID entityid) {
		for (Villager v : villagers) {
			if (v.getUniqueId().equals(entityid)) {
				villagers.remove(v);
				EntityManager.unregister(v.getUniqueId());
				v.despawn();
				break;
			}
		}
	}

	private static void searchQuests() {
		for (Villager v : villagers) {
			for (Entity e : v.getVillager().getNearbyEntities(50, 25, 50)) {
				if (e instanceof Player) {
					RolecraftPlayer player = PlayerManager.getPlayer(e.getUniqueId());
					Quest q = QuestManager.searchFittingQuest(PlayerManager.getPlayer(e.getUniqueId()), v);
					if (q != null) {
						Location l = q.getTargetLocation(PlayerManager.getPlayer(e.getUniqueId()));
						if (l == null) {
							if (player.getQuestStatus(q.getName()).equals(QuestStatus.UNACCEPTED)
									|| q.couldComplete(player))
								l = v.getEntityLocation();
							else
								continue;
						} else {
							// got target villager
							if (!player.getQuestStatus(q.getName()).equals(QuestStatus.FIRST))
								continue;
						}
						l.setY(l.getY() + 2.65);
						try {
							((Player) e).spawnParticle(Particle.VILLAGER_HAPPY, l, 7, 0.1F, 0.35F, 0.3F);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

	}

	public static void doLogics(long timer) {
		searchQuests();
		// Destroy possible bug copies
		if (timer % 100 == 0) {
			Manager.scheduleTask(new Runnable() {

				@Override
				public void run() {
					for (Villager v : villagers) {
						if (!RolecraftConfig.isNaturalSpawnDisabled(v.getBukkitEntity().getWorld().getName()))
							for (Entity e : v.getBukkitEntity().getNearbyEntities(10, 4, 10)) {
								if (e instanceof org.bukkit.entity.Villager
										&& !EntityManager.isRegistered(e.getUniqueId())) {
									if (e.getName().equals(v.getName()))
										e.remove();
								}
							}
					}

				}
			});

		}
	}

	public static synchronized void load(final Chunk chunk) {
		List<Villager> list = villagers;
		for (final Villager v : list) {
			if (v.getEntityLocation().getChunk().getX() == chunk.getX()
					&& v.getEntityLocation().getChunk().getZ() == chunk.getZ()) {
				Manager.scheduleTask(new Runnable() {

					@Override
					public void run() {
						Villager villager = null;
						if (v.getType().equals(VillagerType.TRADER)) {
							villager = spawnTrader(v.getLocation(), v.getName(), ((Trader) v).getRecipe());
						} else
							villager = spawnVillager(v.getLocation(), v.getName(), v.getType());

						villager.copy(v);

						remove(v.getUniqueId());
					}

				});
			}
		}

	}

	public static Villager getVillager(String name) {
		for (Villager v : villagers)
			if (v.getName().equals(name)) {
				return v;
			}
		return null;

	}

	public static boolean isLoaded() {
		return loaded;
	}

}
