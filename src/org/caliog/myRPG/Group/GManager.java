package org.caliog.myRPG.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GManager {
	private static List<Group> groups = new ArrayList<Group>();
	public static HashMap<UUID, UUID> invitation = new HashMap<UUID, UUID>();

	public static Group getGroup(Player p) {
		return getGroup(p.getUniqueId());
	}

	public static Group getGroup(UUID id) {
		for (Group g : groups) {
			if (g.isMember(id)) {
				return g;
			}
		}
		return null;
	}

	public static boolean isInGroup(Player p) {
		for (Group g : groups) {
			if (g.isMember(p.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	public static boolean createGroup(Player p) {
		if (!isInGroup(p)) {
			groups.add(new Group(p.getUniqueId()));
			return true;
		}
		return false;
	}

	public static boolean removeMemeber(Player p, String name) {
		Group g = getGroup(p);
		if (g == null) {
			return false;
		}
		if (!g.isCreator(p.getUniqueId())) {
			return false;
		}
		if (Bukkit.getPlayer(name) != null) {
			return g.removeMember(Bukkit.getPlayer(name).getUniqueId());
		}
		return false;
	}

	public static void leaveGroup(Player player) {
		Group g = getGroup(player);
		if (g != null) {
			if (g.isCreator(player.getUniqueId())) {
				removeGroup(g);
			} else {
				g.removeMember(player.getUniqueId());
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			if (g.isEmpty()) {
				removeGroup(g);
			}
		}
	}

	private static void removeGroup(Group g) {
		g.clean();
		groups.remove(g);
	}

	public static boolean addMemeber(Player p, Player m) {
		Group g = getGroup(p);
		if (g == null) {
			return false;
		}
		if (!g.isCreator(p.getUniqueId())) {
			return false;
		}
		return g.addMember(m.getUniqueId());
	}

	public static void playerEarnedExp(Player player, int exp) {
		Group g = getGroup(player);
		if (g == null) {
			return;
		}
		g.playerEarnedExp(player.getUniqueId(), exp);
	}

}
