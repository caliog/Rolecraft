package org.caliog.myRPG.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Commands.Utils.Command;
import org.caliog.myRPG.Commands.Utils.CommandExecutable;
import org.caliog.myRPG.Commands.Utils.CommandField;
import org.caliog.myRPG.Commands.Utils.CommandField.FieldProperty;
import org.caliog.myRPG.Commands.Utils.Commands;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Entities.myClass;

public class Commandlevel extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: level SubName: set
		 * 
		 * Permission: rc.level
		 * 
		 * Usage: /level set <player> <level>
		 */
		cmds.add(new Command("level", "rc.level", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				if (PlayerManager.getPlayer(args[1]) != null) {
					PlayerManager.getPlayer(args[1]).setLevel(Integer.parseInt(args[2]));
					player.sendMessage(ChatColor.GOLD + "Set level to " + args[2] + "!");
					return;
				}
				player.sendMessage(ChatColor.RED + args[1] + " is not online!");
				return;
			}
		}, new CommandField("set", FieldProperty.IDENTIFIER), new CommandField("player", FieldProperty.REQUIRED),
				new CommandField("level", "not-negative integer", FieldProperty.REQUIRED)));

		/*
		 * Name: level SubName: reset
		 * 
		 * Permission: rc.level
		 * 
		 * Usage: /level reset <player>
		 */
		cmds.add(new Command("level", "rc.level", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				myClass p = PlayerManager.getPlayer(player.getUniqueId());
				if (args.length > 1) {
					p = PlayerManager.getPlayer(args[1]);
					if (p == null) {
						player.sendMessage(ChatColor.RED + args[1] + " is not online!");
						return;
					}
				}
				if (p != null) {
					p.reset();
					player.sendMessage(ChatColor.GOLD + p.getName() + " reset!");
				}

			}
		}, new CommandField("reset", FieldProperty.IDENTIFIER), new CommandField("player", FieldProperty.REQUIRED)));

		return cmds;
	}
}
