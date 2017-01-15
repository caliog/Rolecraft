package org.caliog.myRPG.Commands;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.myConfig;
import org.caliog.myRPG.Commands.Utils.Command;
import org.caliog.myRPG.Commands.Utils.CommandExecutable;
import org.caliog.myRPG.Commands.Utils.CommandField;
import org.caliog.myRPG.Commands.Utils.CommandField.FieldProperty;
import org.caliog.myRPG.Commands.Utils.Commands;
import org.caliog.myRPG.Entities.ClazzLoader;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Utils.DataFolder;
import org.caliog.myRPG.Utils.FilePath;

public class Commandrc extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: rc
		 * 
		 * Permission: null
		 * 
		 * Usage: /rc
		 */
		cmds.add(new Command("rc", null, new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				player.sendMessage(Manager.plugin.getDescription().getFullName());
				player.sendMessage("Type /rc help [page] for commands!");
			}
		}));

		/*
		 * Name: rc SubName: reload
		 * 
		 * Permission: rc.reload
		 * 
		 * Usage: /rc reload
		 */
		cmds.add(new Command("rc", "rc.reload", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Manager.plugin.reload();
				player.sendMessage(ChatColor.GOLD + "Reloaded " + Manager.plugin.getDescription().getFullName());
			}
		}, new CommandField("reload", FieldProperty.IDENTIFIER)));

		/*
		 * Name: rc SubName: help
		 * 
		 * Permission: rc.help
		 * 
		 * Usage: /rc help [page]
		 */
		cmds.add(new Command("rc", "rc.help", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				int page = 1;
				if (args.length == 2)
					page = Integer.parseInt(args[1]);
				player.sendMessage(ChatColor.BLUE + "All permitted Rolecraft commands: (Page " + page + ")");
				int counter = 0;
				int limit = 9 * page;
				if (Manager.plugin.cmdReg.getPermittedCommands(player).size() >= limit - 9)
					for (Command cmd : Manager.plugin.cmdReg.getPermittedCommands(player)) {
						if (counter >= limit - 9 && counter < limit)
							player.sendMessage(ChatColor.GOLD + cmd.getUsage());
						counter++;
					}
			}
		}, new CommandField("help", FieldProperty.IDENTIFIER), new CommandField("page", "positve integer", FieldProperty.OPTIONAL)));
		/*
		 * Name: rc
		 * 
		 * Permission: rc.mic
		 * 
		 * Usage: /rc mic
		 */
		cmds.add(new Command("rc", "rc.mic", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				if (myConfig.isMICDisabled())
					player.sendMessage(ChatColor.RED + "MIC is disabled in config!");
				else {
					if (!Manager.plugin.createMIC(player))
						player.sendMessage(ChatColor.RED + "Couldn't create MIC.jar, is it already existing?");
				}
			}
		}, new CommandField("mic", FieldProperty.IDENTIFIER)));

		/*
		 * Name: rc
		 * 
		 * Permission: rc.backup
		 * 
		 * Usage: /rc backup
		 */
		cmds.add(new Command("rc", "rc.backup", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				try {
					DataFolder.backup();
				} catch (IOException e) {
					player.sendMessage(ChatColor.RED + "Failed to create backup!");
				}
				player.sendMessage(ChatColor.GOLD + "Made a backup in " + FilePath.backup + "!");

			}
		}, new CommandField("backup", FieldProperty.IDENTIFIER)));

		/*
		 * Name: rc
		 * 
		 * Permission: rc.class
		 * 
		 * Usage: /rc backup
		 */
		cmds.add(new Command("rc", "rc.class", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Player p = player;
				if (args.length >= 3)
					p = Bukkit.getPlayer(args[2]);
				if (p == null) {
					player.sendMessage(ChatColor.RED + "This player is not online!");
					return;
				}
				if (!ClazzLoader.isClass(args[1])) {
					player.sendMessage(ChatColor.RED + "Could not find this class!");
					return;
				}
				PlayerManager.changeClass(p, args[1]);
				player.sendMessage(ChatColor.GOLD + "Changed the class of " + p.getName() + " to " + args[1]);

			}
		}, new CommandField("class", FieldProperty.IDENTIFIER), new CommandField("class-name", FieldProperty.REQUIRED),
				new CommandField("player", FieldProperty.OPTIONAL)));

		return cmds;
	}
}
