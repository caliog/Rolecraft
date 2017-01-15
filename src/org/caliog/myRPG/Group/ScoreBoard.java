package org.caliog.myRPG.Group;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreBoard {
	private HashMap<Integer, String> map = new HashMap<Integer, String>();
	private Objective obj;

	public void add(String str) {
		for (int i = this.map.keySet().size(); i >= 0; i--) {
			this.map.put(i + 1, this.map.get(i));
		}
		/*
		 * if (str.length() > 16) { str = str.substring(0, 16); }
		 */
		this.map.put(1, str);
	}

	public Scoreboard setScoreboard(String title, String displayName, Player player) {
		if (player == null) {
			return null;
		}
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.obj = board.registerNewObjective(title, "dummy");
		this.obj.setDisplayName(displayName);
		for (int i = this.map.keySet().size(); i > 0; i--) {
			this.obj.getScore((String) this.map.get(Integer.valueOf(i))).setScore(i);
		}
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(board);
		return board;
	}
}
