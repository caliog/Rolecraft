package org.caliog.Rolecraft.XMechanics.Bars.CenterBar;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class CenterBar {
	public static void display(Player player, String title, String subtitle, int time, boolean t) {
		if (title != null) {
			title = ChatColor.translateAlternateColorCodes('&', title);
		}
		if (subtitle != null) {
			subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
		}
		NMSMethods.sendBar(player, title, subtitle, t ? 15 : 0, time, t ? 15 : 0);
	}

	public static void display(Player player, String title, int time) {
		display(player, title, null, time, false);
	}

	public static void display(Player player, String title) {
		display(player, title, 60);
	}

	public static void display(Player player, String title, String subtitle, int time) {
		display(player, title, subtitle, time, false);
	}

	public static void display(Player player, String title, String subtitle) {
		display(player, title, subtitle, 60, false);
	}

	public static void broadcast(String title, String subtitle, World world, int time, boolean t) {
		Collection<? extends Player> players = new ArrayList<Player>();
		if (world != null)
			players = world.getPlayers();
		else
			players = Utils.getBukkitPlayers();

		for (Player player : players)
			display(player, title, subtitle, time, t);
	}

	public static void broadcast(String title, String subtitle, World world) {
		broadcast(title, subtitle, world, 60, false);
	}

	public static void broadcast(String title, String subtitle) {
		broadcast(title, subtitle, null);
	}
}
