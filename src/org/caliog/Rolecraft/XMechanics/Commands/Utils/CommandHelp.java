package org.caliog.Rolecraft.XMechanics.Commands.Utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;

public class CommandHelp {

	public CommandHelp(String name, Set<Command> cmds, Player player) {
		work(name, cmds, player);

	}

	public CommandHelp(Command cmd, Player player) {
		Set<Command> cmds = new HashSet<Command>();
		cmds.add(cmd);
		work(cmd.getName(), cmds, player);
	}

	private void work(String name, Set<Command> cmds, Player player) {
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
