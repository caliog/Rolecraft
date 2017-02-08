package org.caliog.Rolecraft.Spells;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Bars.BottomBar.BottomBar;

import net.md_5.bungee.api.ChatColor;

public class SpellBarManager {

	private static final float x = 16;

	private static HashMap<UUID, HashMap<Integer, String[]>> map = new HashMap<UUID, HashMap<Integer, String[]>>();// player
																													// uuid:
																													// <Priority,
																													// spell|time>

	public static void register(final Player player, final String id, long time) {
		HashMap<Integer, String[]> innerMap = new HashMap<Integer, String[]>();
		if (map.containsKey(player.getUniqueId()))
			innerMap = map.get(player.getUniqueId());
		if (!innerMap.isEmpty())
			for (int i = innerMap.size(); i >= 1; i--)
				innerMap.put(i + 1, innerMap.get(i));
		String[] str = { id, String.valueOf(time) };
		innerMap.put(1, str);
		map.put(player.getUniqueId(), innerMap);
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				HashMap<Integer, String[]> innerMap = new HashMap<Integer, String[]>();
				innerMap = map.get(player.getUniqueId());
				int priority = 0;
				if (innerMap != null)
					for (int p : innerMap.keySet())
						if (innerMap.get(p)[0].equals(id)) {
							priority = p;
						}
				for (int i = priority; i <= innerMap.size(); i++)
					if (innerMap.containsKey(i + 1))
						innerMap.put(i, innerMap.get(i + 1));
					else
						innerMap.remove(i);
			}
		}, time);
	}

	public static void timer(final Player player, final String spell, final long time) {
		final long d = Math.round(time / x);
		register(player, spell, time);
		Manager.scheduleRepeatingTask(new Runnable() {

			private long counter = 0;
			private long tickCounter = 0;

			@Override
			public void run() {
				tickCounter++;
				if (tickCounter % d == 0 || tickCounter % 20L == 0) {
					counter = tickCounter / d;
					HashMap<Integer, String[]> innerMap = map.get(player.getUniqueId());
					if (innerMap.containsKey(1) && !innerMap.get(1)[0].equals("#castcode#"))
						if (innerMap.get(1)[0].equals(spell) && (!innerMap.containsKey(2) || innerMap.get(2)[0].equals("#castcode#")))
							BottomBar.display(player, ChatColor.GOLD + spell + " " + getBar(counter));
						else if (tickCounter % 20L == 0 && innerMap.get(1)[0].equals(spell) && innerMap.containsKey(2)) {
							String s = ChatColor.GOLD + spell + ChatColor.GRAY + " " + (time - tickCounter) / 20 + "sec";
							for (int i = 2; i <= innerMap.size() && i < 4; i++)
								s += " " + ChatColor.GOLD + innerMap.get(i)[0] + ChatColor.GRAY + " " + (Integer.valueOf(innerMap.get(i)[1]) - tickCounter) / 20
										+ "sec";
							BottomBar.display(player, s);
						}
				}

			}
		}, 0L, 1L, time);

	}

	private static String getBar(long counter) {
		String r = "";
		for (int i = 0; i < x; i++) {
			if (x > i + counter)
				r += ChatColor.BLUE + "◘";
			else
				r += ChatColor.GRAY + "◘";
		}
		return r;

	}

	public static boolean isFree(UUID player) {
		if (map.containsKey(player))
			return map.get(player).isEmpty();
		return true;
	}
}
