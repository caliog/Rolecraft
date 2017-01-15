package org.caliog.myRPG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Villagers.Chat.ChatManager;
import org.caliog.Villagers.NPC.Guards.GManager;
import org.caliog.Villagers.Quests.QManager;
import org.caliog.Villagers.Quests.QuestKill;
import org.caliog.Villagers.Utils.DataSaver;
import org.caliog.Villagers.Utils.VManager;
import org.caliog.myRPG.Entities.ClazzLoader;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Entities.Playerface;
import org.caliog.myRPG.Entities.VolatileEntities;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Messages.Msg;
import org.caliog.myRPG.Mobs.MobSpawner;
import org.caliog.myRPG.Mobs.PetController;
import org.caliog.myRPG.Spells.SpellLoader;
import org.caliog.myRPG.Utils.ChestHelper;
import org.caliog.myRPG.Utils.DataFolder;
import org.caliog.myRPG.Utils.FilePath;
import org.caliog.myRPG.Utils.GroupManager;
import org.caliog.myRPG.Utils.PlayerList;
import org.caliog.npclib.NMS;
import org.caliog.npclib.NPCManager;

public class Manager {
	public static myPlugin plugin;
	private static long timer = 0L;

	public static myClass getPlayer(UUID id) {
		return PlayerManager.getPlayer(id);
	}

	public static Runnable getTask() {

		return new Runnable() {
			public void run() {
				Manager.timer += 1L;
				if (timer >= 72000)
					timer = 0;
				if (Manager.timer % 4 == 0L)
					GManager.doLogics();
				if (Manager.timer % 5L == 0L) {
					Manager.scheduleTask(MobSpawner.getTask());
					if (Manager.timer % 20L == 0L) {
						VManager.doLogics(timer);
						PetController.controll();
					}

				}

				PlayerManager.task(timer);

			}
		};
	}

	public static void save() {

		try {
			MobSpawner.saveZones();
			VolatileEntities.save();
			PlayerManager.save();
			Playerface.clear();

			// Chets
			ChestHelper.cleanUp();

			// Villager stuff
			VManager.save();
			GManager.save();
			QuestKill.save();
			ChatManager.clear();

			DataFolder.backup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		ClazzLoader.classes = YamlConfiguration.loadConfiguration(new File(FilePath.classes));

		try {
			Msg.init();
			GroupManager.init();

			// Quests
			QManager.init();
			QuestKill.load();

			// Villagers
			NPCManager.npcManager = NMS.getNPCManager();
			VManager.load();
			GManager.load();

			// Spells
			SpellLoader.init();

			MobSpawner.loadZones();
			VolatileEntities.load();

			PlayerManager.load();
			PlayerList.refreshList();

			DataSaver.clean();// this has to be the last thing to do
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int scheduleRepeatingTask(Runnable r, long d, long p) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(Manager.plugin, r, d, p);
	}

	public static int scheduleTask(Runnable r, long d) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(Manager.plugin, r, d);
	}

	public static int scheduleTask(Runnable r) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(Manager.plugin, r);
	}

	public static void cancelTask(Integer id) {
		Bukkit.getScheduler().cancelTask(id.intValue());
	}

	public static void cancelAllTasks() {
		Bukkit.getScheduler().cancelTasks(Manager.plugin);
	}

	public static int scheduleRepeatingTask(Runnable r, long i, long j, long l) {
		final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Manager.plugin, r, i, j);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Manager.plugin, new Runnable() {
			public void run() {
				Bukkit.getScheduler().cancelTask(taskId);
			}
		}, l + i);
		return taskId;
	}

	public static void broadcast(String string) {
		for (myClass p : PlayerManager.getPlayers())
			p.getPlayer().sendMessage(string);

	}

	public static List<World> getWorlds() {
		List<World> list = Bukkit.getWorlds();
		List<World> r = new ArrayList<World>();
		List<String> disabled = myConfig.getDisabledWorlds();
		for (World w : list)
			if (!disabled.contains(w.getName()))
				r.add(w);
		return r;
	}

}
