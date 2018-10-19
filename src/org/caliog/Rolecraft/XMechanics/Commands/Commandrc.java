package org.caliog.Rolecraft.XMechanics.Commands;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Items.Armor;
import org.caliog.Rolecraft.Items.Weapon;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Resource.DataFolder;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;

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
				Manager.plugin.updateMessage(player);
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
				Manager.plugin.updateMessage(player);
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

				for (Command cmd : Manager.plugin.cmdReg.getPermittedCommands(player)) {
					if (counter >= limit - 9 && counter < limit)
						player.sendMessage(ChatColor.GOLD + cmd.getUsage());
					counter++;
				}

			}
		}, new CommandField("help", FieldProperty.IDENTIFIER),
				new CommandField("page", "positve integer", FieldProperty.OPTIONAL)));

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
					Debugger.exception("Commandrc threw an exception. Failed to create backup! ", e.getMessage());
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
		 * Usage: /rc class
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
		/*
		 * Name: rc SubName: mobs
		 * 
		 * Permission: rc.mobs
		 * 
		 * Usage: /rc mobs [page]
		 */

		cmds.add(new Command("rc", "rc.mobs", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				int page = 1;
				if (args.length == 2)
					page = Integer.parseInt(args[1]);
				player.sendMessage(ChatColor.BLUE + "Rolecraft mobs: (Page " + page + ")");
				int counter = 0;
				int limit = 9 * page;
				Set<String> mobIdentifier = MobSpawner.getIdentSet();
				for (String id : mobIdentifier) {
					if (counter >= limit - 9 && counter < limit)
						player.sendMessage(ChatColor.GOLD + id);
					counter++;
				}
			}
		}, new CommandField("mobs", FieldProperty.IDENTIFIER),
				new CommandField("page", "positve integer", FieldProperty.OPTIONAL)));

		/*
		 * Name: rc SubName: weapons
		 * 
		 * Permission: rc.weapons
		 * 
		 * Usage: /rc mobs [page]
		 */

		cmds.add(new Command("rc", "rc.weapons", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				int page = 1;
				if (args.length == 2)
					page = Integer.parseInt(args[1]);
				player.sendMessage(ChatColor.BLUE + "Rolecraft weapons: (Page " + page + ")");
				int counter = 0;
				int limit = 9 * page;
				List<String> list = Weapon.getWeaponList();

				for (String id : list) {
					if (counter >= limit - 9 && counter < limit)
						player.sendMessage(ChatColor.GOLD + id);
					counter++;
				}
			}
		}, new CommandField("weapons", FieldProperty.IDENTIFIER),
				new CommandField("page", "positve integer", FieldProperty.OPTIONAL)));

		/*
		 * Name: rc SubName: armor
		 * 
		 * Permission: rc.armor
		 * 
		 * Usage: /rc mobs [page]
		 */

		cmds.add(new Command("rc", "rc.armor", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				int page = 1;
				if (args.length == 2)
					page = Integer.parseInt(args[1]);
				player.sendMessage(ChatColor.BLUE + "Rolecraft armor: (Page " + page + ")");
				int counter = 0;
				int limit = 9 * page;
				List<String> list = Armor.getArmorList();

				for (String id : list) {
					if (counter >= limit - 9 && counter < limit)
						player.sendMessage(ChatColor.GOLD + id);
					counter++;
				}
			}
		}, new CommandField("armor", FieldProperty.IDENTIFIER),
				new CommandField("page", "positve integer", FieldProperty.OPTIONAL)));

		return cmds;
	}
}
