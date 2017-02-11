package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Guards.CheckpointPath;
import org.caliog.Rolecraft.Guards.PathUtil;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.Chat.CMessage;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.NPC.Villager.VillagerType;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Messages.CmdMessage;

public class Commandvg extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: vg SubName: create
		 * 
		 * Permission: rc.villager.create
		 * 
		 * Usage: /vg create <name..>
		 */
		cmds.add(new Command("vg", "rc.villager.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				String name = args[1];
				VManager.spawnVillager(player.getLocation(), name.trim(), VillagerType.VILLAGER);
			}
		}, new CommandField("create", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		/*
		 * Name: vg SubName: remove
		 * 
		 * Permission: rc.villager.remove
		 * 
		 * Usage: /vg remove
		 */
		cmds.add(new Command("vg", "rc.villager.remove", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());
				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				VManager.remove(v.getUniqueId());
				player.sendMessage(ChatColor.GOLD + "Removed this villager!");
			}
		}, new CommandField("remove", FieldProperty.IDENTIFIER)));

		/*
		 * Name: vg SubName: talk
		 * 
		 * Permission: rc.villager.talk
		 * 
		 * Usage: /vg talk <id> <message> <type> <target>
		 */
		cmds.add(new Command("vg", "rc.villager.talk", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());

				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				String text = args[2];
				try {
					CMessage.MessageType.valueOf(args[3]);
					text += "#" + args[3];
					text += "#" + (args.length >= 5 ? args[4] : (Integer.parseInt(args[1]) + 1));

					v.addText(Integer.parseInt(args[1]), text);
					player.sendMessage(ChatColor.GOLD + "You set/edited message " + args[1] + "!");
				} catch (Exception e) {
					Debugger.exception("Commandvg threw an exception: ", e.getMessage());
					player.sendMessage(ChatColor.RED + "/vg talk <id> <message> <type> [target]");
					player.sendMessage(ChatColor.RED + "Visit Rolecraft wiki to get some information about this command!");
				}
			}
		}, new CommandField("talk", FieldProperty.IDENTIFIER), new CommandField("id", "not-negative integer", FieldProperty.REQUIRED),
				new CommandField("message", FieldProperty.REQUIRED), new CommandField("type", "END|QUESTION|TEXT", FieldProperty.REQUIRED),
				new CommandField("target", "not-negative integer", FieldProperty.OPTIONAL)));

		/*
		 * Name: vg SubName: deltalk
		 * 
		 * Permission: rc.villager.talk
		 * 
		 * Usage: /vg deltalk [id]
		 */
		cmds.add(new Command("vg", "rc.villager.talk", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());
				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				if (args.length == 2) {
					v.removeText(Integer.parseInt(args[1]));
					player.sendMessage(ChatColor.GOLD + "Removed message " + args[1] + "!");
				} else {
					v.clearText();
					player.sendMessage(ChatColor.GOLD + "Removed all messages!");
				}
			}
		}, new CommandField("deltalk", FieldProperty.IDENTIFIER), new CommandField("id", "not-negative integer", FieldProperty.OPTIONAL)));

		/*
		 * Name: vg SubName: quest
		 * 
		 * Permission: rc.villager.quest
		 * 
		 * Usage: /vg quest <name>
		 */
		cmds.add(new Command("vg", "rc.villager.quest", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());
				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				if (v.addQuest(args[1]))
					player.sendMessage(ChatColor.GOLD + "Assigned the quest to the villager!");
				else
					player.sendMessage(ChatColor.GOLD + "Quest does not exist!");
			}
		}, new CommandField("quest", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		/*
		 * Name: vg SubName: delquest
		 * 
		 * Permission: rc.villager.quest
		 * 
		 * Usage: /vg delquest <name>
		 */
		cmds.add(new Command("vg", "rc.villager.quest", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());
				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				if (v.removeQuest(args[1]))
					player.sendMessage(ChatColor.GOLD + "Removed this quest!");
				else
					player.sendMessage(ChatColor.RED + "That is not an assigned quest of this villager!");
			}
		}, new CommandField("delquest", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		/*
		 * Name: vg SubName: toggle
		 * 
		 * Permission: rc.villager.toggle
		 * 
		 * Usage: /vg toggle
		 */
		cmds.add(new Command("vg", "rc.villager.toggle", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());
				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				v.toggleProfession();
			}
		}, new CommandField("toggle", FieldProperty.IDENTIFIER)));

		/*
		 * Name: vg SubName: path
		 * 
		 * Permission: rc.villager.path
		 * 
		 * Usage: /vg path <name>
		 */
		cmds.add(new Command("vg", "rc.villager.path", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Villager v = VManager.getClosestVillager(player.getLocation());
				if (v == null) {
					player.sendMessage(CmdMessage.noVillager);
					return;
				}
				if (args[1].equals("remove")) {
					v.removePath();
					player.sendMessage(ChatColor.GOLD + "The villager won't walk this path anymore!");
					return;
				}
				CheckpointPath path = PathUtil.getPath(args[1]);
				if (path != null && path.isLoaded()) {
					v.setPath(path);
					player.sendMessage(ChatColor.GOLD + "The villager will walk this line!");
				} else
					player.sendMessage(ChatColor.GOLD + "This path does not exist!");

			}
		}, new CommandField("path", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		return cmds;
	}
}
