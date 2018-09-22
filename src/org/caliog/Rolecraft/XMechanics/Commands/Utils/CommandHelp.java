package org.caliog.Rolecraft.XMechanics.Commands.Utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;

public final class CommandHelp {

	public static void sendCommandHelp(Command cmd, Player player) {
		Set<Command> cmds = new HashSet<Command>();
		cmds.add(cmd);
		sendCommandHelp(cmd.getName(), cmds, player);
	}

	public static void sendCommandHelp(String name, Set<Command> cmds, Player player) {
		if (player == null || cmds == null || cmds.isEmpty())
			return;

		for (Command cmd : cmds) {
			if (cmd == null)
				continue;
			if (cmd.getName().equalsIgnoreCase(name)) {
				ChatColor color = ChatColor.RED;
				if (PlayerManager.getPlayer(player.getUniqueId()).hasPermission(cmd.getPermission()))
					color = ChatColor.GOLD;
				player.getPlayer().sendMessage(color + cmd.getUsage());
			}
		}

	}
}
