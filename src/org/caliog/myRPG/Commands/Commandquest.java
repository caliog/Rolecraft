package org.caliog.myRPG.Commands;

import java.util.List;

import org.bukkit.entity.Player;
import org.caliog.Villagers.Utils.QuestInventory;
import org.caliog.myRPG.Commands.Utils.Command;
import org.caliog.myRPG.Commands.Utils.CommandExecutable;
import org.caliog.myRPG.Commands.Utils.CommandField;
import org.caliog.myRPG.Commands.Utils.CommandField.FieldProperty;
import org.caliog.myRPG.Commands.Utils.Commands;

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
