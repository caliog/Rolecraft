package org.caliog.myRPG.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Commands.Utils.Command;
import org.caliog.myRPG.Commands.Utils.CommandExecutable;
import org.caliog.myRPG.Commands.Utils.CommandField;
import org.caliog.myRPG.Commands.Utils.CommandField.FieldProperty;
import org.caliog.myRPG.Commands.Utils.Commands;
import org.caliog.myRPG.Group.GManager;
import org.caliog.myRPG.Messages.Msg;
import org.caliog.myRPG.Utils.Utils;

public class Commandgroup extends Commands {

	@Override
	public List<Command> getCommands() {

		/*
		 * Name: group SubName: create
		 * 
		 * Permission: rc.group.create
		 * 
		 * Usage: /group create
		 */
		cmds.add(new Command("group", "rc.group.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				if (!GManager.isInGroup(player)) {
					GManager.createGroup(player);
					Msg.sendMessage(player, "group-created");
				} else {
					Msg.sendMessage(player, "group-leave-create");
				}

			}
		}, new CommandField("create", FieldProperty.IDENTIFIER)));

		/*
		 * Name: group SubName: invite
		 * 
		 * Permission: rc.group.invite
		 * 
		 * Usage: /group invite <player>
		 */
		cmds.add(new Command("group", "rc.group.invite", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				final String name = args[1];
				if (Bukkit.getPlayer(name) != null) {
					GManager.invitation.put(Bukkit.getPlayer(name).getUniqueId(), player.getUniqueId());
					Manager.scheduleTask(new Runnable() {
						public void run() {
							Player player = Bukkit.getPlayer(name);
							if (player != null)
								GManager.invitation.remove(player.getUniqueId());
						}
					}, 1800L);
					Msg.sendMessage(Bukkit.getPlayer(name), "group-invited", Msg.PLAYER, player.getName());
				} else {
					player.sendMessage(ChatColor.RED + "This player is offline!");
				}
			}
		}, new CommandField("invite", FieldProperty.IDENTIFIER), new CommandField("player", FieldProperty.REQUIRED)));

		/*
		 * Name: group SubName: join
		 * 
		 * Permission: rc.group.join
		 * 
		 * Usage: /group join
		 */
		cmds.add(new Command("group", "rc.group.join", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				if (!GManager.isInGroup(player)) {
					if (GManager.invitation.containsKey(player.getUniqueId())) {
						if (!GManager.addMemeber(Utils.getPlayer(GManager.invitation.get(player.getUniqueId())), player)) {
							Msg.sendMessage(player, "group-full");
						}
						GManager.invitation.remove(player.getUniqueId());
					}
				} else {
					Msg.sendMessage(player, "group-leave-join");
				}

			}
		}, new CommandField("join", FieldProperty.IDENTIFIER)));

		/*
		 * Name: group SubName: leave
		 * 
		 * Permission: null
		 * 
		 * Usage: /group leave [player]
		 */
		cmds.add(new Command("group", null, new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				if (args.length >= 2) {
					String name = args[1];
					if (Bukkit.getPlayer(name) != null) {
						if (!GManager.removeMemeber(player, name)) {
							Msg.sendMessage(player, "group-cannot-leave-player");
						}
					} else {
						player.sendMessage(ChatColor.RED + "This player is offline!");
					}
				} else {
					if (GManager.isInGroup(player)) {
						GManager.leaveGroup(player);
						Msg.sendMessage(player, "group-left");
					} else {
						Msg.sendMessage(player, "group-no");
					}
				}
			}
		}, new CommandField("leave", FieldProperty.IDENTIFIER), new CommandField("player", FieldProperty.OPTIONAL)));

		return cmds;
	}
}
