package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestInventory;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;

public class Commandquest extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: quest SubName: create
		 * 
		 * Permission: rc.quest.create
		 * 
		 * Usage: /quest
		 */
		cmds.add(new Command("quest", "rc.quest.edit", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				player.openInventory(new QuestInventory(player, args[0]));
			}
		}, new CommandField("name", FieldProperty.REQUIRED)));

		/*
		 * Name: quest SubName: edit
		 * 
		 * Permission: rc.quest.edit
		 * 
		 * Usage: /quest edit
		 */
		cmds.add(new Command("quest", "rc.quest.edit", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				player.openInventory(new QuestInventory(player, args[1]));
			}
		}, new CommandField("edit", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		return cmds;
	}

}
