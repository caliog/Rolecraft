package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Villagers.Utils.QuestInventory;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;

public class Commandquest extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: quest SubName: create
		 * 
		 * Permission: rc.quest.create
		 * 
		 * Usage: /quest create
		 */
		cmds.add(new Command("quest", "rc.quest.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				player.openInventory(new QuestInventory(player, args[1]));
			}
		}, new CommandField("create", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		return cmds;
	}

}
