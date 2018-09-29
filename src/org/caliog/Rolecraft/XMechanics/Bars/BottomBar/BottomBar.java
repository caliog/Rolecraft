package org.caliog.Rolecraft.XMechanics.Bars.BottomBar;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;

public class BottomBar {

	public static void display(Player player, String msg) {
		NMSMethods.sendHotBar(player, ChatColor.translateAlternateColorCodes('&', msg));
	}

	public static void display(Player player, String msg, int speed) {
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(player);
		display(list, msg, speed);
	}

	public static void display(final Collection<? extends Player> players, String m, int speed) {
		if (speed == 0) {
			for (Player p : players) {
				display(p, m);
			}
			return;
		}
		final int time = Math.round(20L / (float) speed);
		final String msg = ChatColor.translateAlternateColorCodes('&', m);
		Manager.scheduleRepeatingTask(new Runnable() {
			private String message = "";
			private int counter = 0;

			public void run() {
				this.counter += 1;
				if (this.counter <= msg.length()) {
					this.message = msg.substring(0, this.counter);
				}
				String lc = this.message.substring(this.message.length() - 1);
				while ((lc.equals(" ")) || (lc.equals("§"))) {
					this.counter += 1;
					if (this.counter <= msg.length()) {
						this.message = msg.substring(0, this.counter);
					}
					lc = this.message.substring(this.message.length() - 1);
				}
				for (Player player : players) {
					if (player.isOnline()) {
						BottomBar.display(player, this.message);
					}
				}
			}
		}, 20L, time, 20L + time * msg.length());

	}

	public static void broadcast(String message, int speed, World world) {
		Collection<? extends Player> players;
		if (world == null) {
			players = Bukkit.getOnlinePlayers();
		} else {
			players = world.getPlayers();
		}
		display(players, message, speed);
	}

	public static void broadcast(String message) {
		broadcast(message, 0, null);
	}
}
