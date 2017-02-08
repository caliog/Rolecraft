package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.NPC.Villager.VillagerType;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;

public class Commandpriest extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: priest SubName: create
		 * 
		 * Permission: rc.priest.create
		 * 
		 * Usage: /priest create <name>
		 */
		cmds.add(new Command("priest", "rc.priest.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				String cname = ChatColor.translateAlternateColorCodes('&', args[1]);
				String name = ChatColor.stripColor(cname);
				if (!ClazzLoader.isClass(name)) {
					player.sendMessage(ChatColor.RED + name + " is not a class!");
					return;
				}
				VManager.spawnVillager(player.getLocation(), cname, VillagerType.PRIEST);
				player.sendMessage(ChatColor.GOLD + "Spawned the priest next to you!");
			}
		}, new CommandField("create", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));
		return cmds;
	}

}
