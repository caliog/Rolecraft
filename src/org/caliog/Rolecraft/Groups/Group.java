package org.caliog.Rolecraft.Groups;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class Group {
	private HashMap<UUID, Float> players = new HashMap<UUID, Float>();
	private int experience;
	private UUID creator;

	public Group(UUID creator) {
		this.creator = creator;
		this.players.put(creator, Float.valueOf(0.0F));
		updateScoreboard();
	}

	public boolean isCreator(UUID id) {
		return id.equals(this.creator);
	}

	public String getCreator() {
		return Bukkit.getPlayer(creator).getName();
	}

	public boolean isMember(UUID member) {
		return this.players.containsKey(member);
	}

	public boolean addMember(UUID member) {
		if (this.players.size() >= 10) {
			return false;
		}
		if (this.players.containsKey(member)) {
			return false;
		}
		this.players.put(member, 0.0F);
		updateScoreboard();
		return true;
	}

	public boolean removeMember(UUID member) {
		if (!this.players.containsKey(member)) {
			return false;
		}
		this.players.remove(member);
		Utils.getPlayer(member).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		updateScoreboard();
		return true;
	}

	public void updateScoreboard() {
		ScoreBoard board = new ScoreBoard();
		board.add(ChatColor.YELLOW + "" + this.experience + " Exp!");
		board.add(" ");
		board.add(ChatColor.BLUE + "Members:");
		for (UUID id : this.players.keySet()) {
			if (Utils.getPlayer(id) != null) {
				String str = ChatColor.GOLD + Utils.getPlayer(id).getName() + " " + ChatColor.YELLOW
						+ Math.round(((Float) this.players.get(id)).floatValue() / this.experience * 100.0F) + "%";
				if ((str.length() > 16) && (Utils.getPlayer(id).getName().length() >= 10)) {
					str = ChatColor.GOLD + Utils.getPlayer(id).getName().substring(0, 9) + ". " + ChatColor.YELLOW
							+ Math.round(((Float) this.players.get(id)).floatValue() / this.experience * 100.0F) + "%";
				}
				board.add(str);
			}
		}
		for (UUID id : this.players.keySet()) {
			board.setScoreboard("gruppe", ChatColor.GOLD + Utils.getPlayer(this.creator).getName() + "s " + ChatColor.BLUE + "group",
					Utils.getPlayer(id));
		}
	}

	public void playerEarnedExp(UUID player, int exp) {
		if (isMember(player)) {
			this.experience += exp;
			float e = ((Float) this.players.get(player)).floatValue() + exp;
			this.players.put(player, Float.valueOf(e));
			updateScoreboard();
			int experience = Math.round(exp / this.players.size() * (1.0F + this.players.size() * 0.09F));
			for (UUID id : this.players.keySet()) {
				PlayerManager.getPlayer(id).giveExp(experience);
			}
		}
	}

	public boolean isEmpty() {
		return this.players.size() == 0;
	}

	public void clean() {
		if (!isEmpty()) {
			for (UUID id : this.players.keySet()) {
				Utils.getPlayer(id).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		}
	}
}
