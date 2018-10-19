package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.Mobs.MobCreation.MobEditMenu;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;

public class Commandmob extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: mob
		 * 
		 * Permission: rc.mob.edit
		 * 
		 * Usage: /mob edit [name]
		 */
		cmds.add(new Command("mob", "rc.mob.edit", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				MobEditMenu menu;
				if (args.length >= 2) {
					String name = args[1];
					menu = MobEditMenu.loadByName(name);
					if (menu != null) {
						MenuManager.openMenu(player, menu);
						return;
					}
				}
				menu = new MobEditMenu();
				MenuManager.openMenu(player, menu);
			}
		}, new CommandField("edit", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.OPTIONAL)));

		/*
		 * Name: mob
		 * 
		 * Permission: rc.mob.list
		 * 
		 * Usage: /mob list [page]
		 */
		cmds.add(new Command("mob", "rc.mob.list", new CommandExecutable() {

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
		}, new CommandField("list", FieldProperty.IDENTIFIER),
				new CommandField("page", "positive integer", FieldProperty.OPTIONAL)));

		return cmds;
	}
}
