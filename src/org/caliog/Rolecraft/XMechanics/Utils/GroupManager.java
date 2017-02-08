package org.caliog.Rolecraft.XMechanics.Utils;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;

import net.milkbowl.vault.permission.Permission;

public class GroupManager {

	private static Permission permission;

	public static boolean init() {
		try {
			Class.forName("net.milkbowl.vault.permission.Permission");
			RegisteredServiceProvider<Permission> permissionProvider = Manager.plugin.getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
			}
		} catch (Exception e) {
			Manager.plugin.getLogger().warning("Could not find Vault!");
		}
		return (permission != null);
	}

	public static void updateGroup(Player player, int level) {
		if (permission == null || player == null)
			return;
		String[] ignore = RolecraftConfig.getIgnoredPlayers();
		if (ignore != null)
			for (String n : ignore)
				if (n.equals(player.getName()))
					return;

		HashMap<String, Integer> map = RolecraftConfig.getGroupMap();
		int max = -1;
		for (int i : map.values())
			if (i > max && level >= i)
				max = i;
		if (max == -1)
			return;

		for (int i = level; i >= 0; i--) {
			for (String g : permission.getGroups())
				if (map.keySet().contains(g) && map.get(g) == i) {
					String old = permission.getPrimaryGroup(player);
					permission.playerAddGroup(player, g);
					permission.playerRemoveGroup(player, old);
					return;
				}
		}

	}

	public static String getGroup(RolecraftPlayer player) {
		if (permission != null)
			return permission.getPrimaryGroup(player.getPlayer());
		return null;
	}
}
