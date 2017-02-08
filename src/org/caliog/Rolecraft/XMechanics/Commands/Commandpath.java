package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Guards.PathUtil;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;

public class Commandpath extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: path SubName: create
		 * 
		 * Permission: rc.path.create
		 * 
		 * Usage: /path create <name> <initdelay> [CPdelay]
		 */
		cmds.add(new Command("path", "rc.path.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				int cpdelay = 0;
				if (args.length >= 4)
					cpdelay = Integer.parseInt(args[3]);
				PathUtil.createPath(args[1], Integer.parseInt(args[2]), cpdelay, player.getLocation());
				player.sendMessage(ChatColor.GOLD + "Created path!");
			}
		}, new CommandField("create", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED),
				new CommandField("initdelay", "positive integer", FieldProperty.REQUIRED),
				new CommandField("checkpointdelay", "positive integer", FieldProperty.OPTIONAL)));

		/*
		 * Name: path SubName: set
		 * 
		 * Permission: rc.path.set
		 * 
		 * Usage: /path set <name> <checkpoint>
		 */
		cmds.add(new Command("path", "rc.path.set", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				PathUtil.setPath(args[1], Integer.parseInt(args[2]), player.getLocation());
				player.sendMessage(ChatColor.GOLD + "Created the checkpoint!");
			}
		}, new CommandField("set", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED),
				new CommandField("checkpoint", "not-negative integer", FieldProperty.REQUIRED)));

		/*
		 * Name: path SubName: delete
		 * 
		 * Permission: rc.path.delete
		 * 
		 * Usage: /path delete <name>
		 */
		cmds.add(new Command("path", "rc.path.delete", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				PathUtil.removePath(args[1]);
				player.sendMessage(ChatColor.GOLD + "Deleted path!");
			}
		}, new CommandField("delete", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		return cmds;
	}
}
