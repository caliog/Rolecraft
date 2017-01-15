package org.caliog.myRPG.Entities;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.caliog.myRPG.myConfig;
import org.caliog.myRPG.Utils.FilePath;

public class PlayerManager {
	private static HashMap<UUID, myClass> players = new HashMap<UUID, myClass>();
	private static File f = new File(FilePath.players);
	public static Set<UUID> changedClass = new HashSet<UUID>();

	public static void save() throws IOException {
		f.mkdir();
		for (UUID id : players.keySet()) {
			save((myPlayer) players.get(id));
		}
	}

	public static void load() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			login(p);
		}
	}

	public static void save(myPlayer p) throws IOException {
		myClass player = (myClass) p;
		File ff = new File(f.getAbsolutePath() + "/" + "players" + ".yml");
		if (!ff.exists()) {
			ff.createNewFile();
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(ff);
		config.set(p.getName(), player.getType());
		config.save(ff);
		player.save();
	}

	public static boolean login(Player player) {
		try {
			File ff = new File(f.getAbsolutePath() + "/" + "players" + ".yml");
			if (!ff.exists()) {
				ff.createNewFile();
				return false;
			}
			YamlConfiguration config = YamlConfiguration.loadConfiguration(ff);
			String type = config.getString(player.getName());
			if (type == null)
				return false;
			myClass p = getPlayer(player, type);
			register(p);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void logout(Player player) {
		f.mkdir();
		myPlayer p = players.get(player.getUniqueId());
		if (p == null) {
			return;
		}
		try {
			save(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		players.remove(player.getUniqueId());
	}

	public static myClass getPlayer(Player player, String clazz) {
		myClass p = null;
		p = ClazzLoader.create(player, clazz);
		if (p == null) {
			return null;
		}

		return p;
	}

	private static void register(myClass p) {
		if (p == null)
			return;
		players.put(p.getPlayer().getUniqueId(), p);
	}

	public static void register(Player player, String clazz) {
		register(getPlayer(player, clazz));
	}

	public static myClass getPlayer(UUID player) {
		return players.get(player);
	}

	public static myClass getPlayer(String string) {
		for (UUID i : players.keySet()) {
			if ((players.get(i)).getName().equals(string)) {
				return players.get(i);
			}
		}
		return null;
	}

	public static void task(long time) {
		for (myClass clazz : players.values()) {
			if (myConfig.isWorldDisabled(clazz.getPlayer().getWorld()))
				continue;
			int i = clazz.getIntelligence();
			int s = 1;
			if (i < 20) {
				s = 5;
			} else if (i < 45) {
				s = 4;
			} else if (i < 60) {
				s = 3;
			} else if (i <= 80) {
				s = 2;
			}
			s *= 20;

			if (time % s == 0.0F) {
				clazz.regainFood();
			}
			if (clazz.getHealth() != 0.0D) {
				if (time % 100 == 0.0F) {
					clazz.addHealth(clazz.getMaxHealth() * 0.08D);
				}
				double d = clazz.getHealth() / clazz.getMaxHealth();
				@SuppressWarnings("deprecation")
				double maxHealth = ((Damageable) clazz.getPlayer()).getMaxHealth();
				clazz.getPlayer().setHealth(maxHealth * (d > 1.0D ? 1.0D : d));
				/*
				 * if (!GManager.isInGroup(clazz.getPlayer())) { clazz.getPlayer().setScoreboard(Bukkit.getScoreboardManager() .getNewScoreboard()); }
				 */
			}
		}
	}

	public static void changeClass(Player player, String clazz) {
		if (getPlayer(player.getUniqueId()).getType().equals(clazz))
			return;
		logout(player);
		register(player, clazz);
		if (player.getLevel() <= 0)
			player.setLevel(1);
	}

	public static void respawn(Player player) {
		myPlayer p = getPlayer(player.getUniqueId());
		if (p == null) {
			return;
		}
		Location loc = player.getBedSpawnLocation();
		if (loc != null) {
			player.teleport(loc);
		} else {
			player.teleport(player.getWorld().getSpawnLocation());
		}
	}

	public static Collection<myClass> getPlayers() {
		return players.values();
	}
}
