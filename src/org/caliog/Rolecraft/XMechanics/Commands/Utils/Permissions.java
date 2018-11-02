package org.caliog.Rolecraft.XMechanics.Commands.Utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class Permissions {

	private static Set<String> permissions = new HashSet<String>();

	public static Set<String> getPermissions(Player player) {
		Set<String> list = new HashSet<String>();

		if (player.isOp() || player.hasPermission("rc.admin") || player.hasPermission("rc.*")
				|| player.hasPermission("*") || player.hasPermission("'*'")) {
			return new HashSet<String>(permissions);
		} else {
			for (String p : permissions) {
				String[] split = p.split("\\.");
				String splitted = "";
				for (int i = 0; i < split.length; i++) {
					if (i < split.length - 1)
						splitted += split[i] + ".";
					else
						splitted += split[i];
					if (player.hasPermission(splitted + "*") || player.hasPermission(splitted + "admin")
							|| player.hasPermission(splitted)) {
						list.add(p);
					}
				}
			}
		}

		return list;
	}

	public static void add(String permission) {
		permissions.add(permission);
	}
}
