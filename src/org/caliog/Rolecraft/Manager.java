package org.caliog.Rolecraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Guards.GManager;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.Mobs.PetController;
import org.caliog.Rolecraft.Spells.SpellLoader;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.Chat.ChatManager;
import org.caliog.Rolecraft.Villagers.Quests.QManager;
import org.caliog.Rolecraft.Villagers.Quests.QuestKill;
import org.caliog.Rolecraft.Villagers.Utils.DataSaver;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Resource.DataFolder;
import org.caliog.Rolecraft.XMechanics.Utils.ChestHelper;
import org.caliog.Rolecraft.XMechanics.Utils.GroupManager;
import org.caliog.Rolecraft.XMechanics.Utils.PlayerList;
import org.caliog.Rolecraft.XMechanics.npclib.NMSUtil;
import org.caliog.Rolecraft.XMechanics.npclib.NPCManager;

import net.milkbowl.vault.economy.Economy;

public final class Manager {
	public static RolecraftPlugin plugin;
	private static long timer = 0L;
	private static List<World> worlds = new ArrayList<World>();
	public static Economy economy;

	// (no) construction
	private Manager() {

	}

	// publics
	public static Runnable getTask() {

		return new Runnable() {
			public void run() {
				try {
					Manager.timer += 1L;
					if (timer >= 72000)
						timer = 0;
					if (Manager.timer % 4 == 0L)
						GManager.doLogics(timer);
					if (Manager.timer % 5L == 0L) {
						Manager.scheduleTask(MobSpawner.getTask());
						if (Manager.timer % 20L == 0L) {
							VManager.doLogics(timer);
							PetController.controll();
						}
					}

					PlayerManager.task(timer);

				} catch (Exception e) {
					Debugger.exception("Manager task gave Exception:", e.getMessage());
					e.printStackTrace();
				}
			}
		};
	}

	public static void save() {

		try {
			MobSpawner.saveZones();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			EntityManager.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PlayerManager.save();
		Playerface.clear();

		// Chets
		ChestHelper.cleanUp();

		// Villager stuff
		try {
			VManager.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			GManager.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			QuestKill.save();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ChatManager.clear();

		DataSaver.save();

		Debugger.save();

		try {
			DataFolder.backup();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void load() {
		setupEconomy();
		ClazzLoader.init();

		loadWorlds();

		try {
			Msg.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GroupManager.init();

		// Quests
		try {
			QManager.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		QuestKill.load();

		// Villagers
		NPCManager.npcManager = NMSUtil.getNPCManager();
		try {
			VManager.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			GManager.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Spells
		try {
			SpellLoader.init();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			MobSpawner.loadZones();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			EntityManager.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		PlayerManager.load();
		PlayerList.refreshList();

		try {
			// this has to be the last thing to do
			DataSaver.clean();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// scheduler
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

	// worlds
	public static List<World> getWorlds() {
		return worlds;
	}

	public static boolean isWorldDisabled(World world) {
		return !worlds.contains(world);
	}

	// server version
	public static String getServerVersion() {
		return plugin.getServerVersion();
	}

	// privates
	private static void loadWorlds() {
		worlds.clear();
		List<World> list = Bukkit.getWorlds();
		List<String> disabled = RolecraftConfig.getDisabledWorlds();
		for (World w : list)
			if (!disabled.contains(w.getName()))
				worlds.add(w);
	}

	private static boolean setupEconomy() {
		try {
			Class.forName("net.milkbowl.vault.economy.Economy");
			RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}

			return (economy != null);
		} catch (Exception e) {
			Debugger.error(LogTitle.NONE, "Vault (Economy) setup failed. Maybe Vault is missing.");
			plugin.getLogger().warning("Vault is missing! The plugin may not work 100%.");
		}
		return false;
	}

}
