package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Groups.GManager;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Messages.MsgKey;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

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
					Msg.sendMessage(player, MsgKey.GROUP_CREATED);
				} else {
					Msg.sendMessage(player, MsgKey.GROUP_CREATE_FAIL);
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
					Msg.sendMessage(Bukkit.getPlayer(name), MsgKey.GROUP_INVITED, Msg.PLAYER, player.getName());
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
							Msg.sendMessage(player, MsgKey.GROUP_FAIL);
						}
						GManager.invitation.remove(player.getUniqueId());
					}
				} else {
					Msg.sendMessage(player, MsgKey.GROUP_JOIN_FAIL);
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
							Msg.sendMessage(player, MsgKey.GROUP_CANNOT_KICK_PLAYER);
						}
					} else {
						player.sendMessage(ChatColor.RED + "This player is offline!");
					}
				} else {
					if (GManager.isInGroup(player)) {
						GManager.leaveGroup(player);
						Msg.sendMessage(player, MsgKey.GROUP_LEFT);
					} else {
						Msg.sendMessage(player, MsgKey.GROUP_NOT_A_MEMBER);
					}
				}
			}
		}, new CommandField("leave", FieldProperty.IDENTIFIER), new CommandField("player", FieldProperty.OPTIONAL)));

		return cmds;
	}
}
