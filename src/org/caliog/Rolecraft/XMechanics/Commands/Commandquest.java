package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Villagers.Quests.QManager;
import org.caliog.Rolecraft.Villagers.Quests.Quest;
import org.caliog.Rolecraft.Villagers.Quests.QuestBook;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestEditorMenu;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;

public class Commandquest extends Commands {

	@Override
	public List<Command> getCommands() {

		/*
		 * Name: quest SubName: edit
		 * 
		 * Permission: rc.quest.edit
		 * 
		 * Usage: /quest edit <name> [required_quest]
		 */
		cmds.add(new Command("quest", "rc.quest.edit", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				QuestEditorMenu menu = new QuestEditorMenu(player, args[1]);
				if (args.length >= 3) {
					String req_quest = args[2];
					Quest q = QManager.getQuest(req_quest);
					if (q == null) {
						player.sendMessage(ChatColor.RED + args[2] + " could not be found! Did you spell it wrong?");
						return;
					}
					menu.setRequiredQuest(q);
				} else
					menu.setRequiredQuest(null);
				MenuManager.openMenu(player, menu);
			}
		}, new CommandField("edit", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED),
				new CommandField("required_quest", FieldProperty.OPTIONAL)));

		/*
		 * Name: quest SubName: book
		 * 
		 * Permission: rc.quest.book
		 * 
		 * Usage: /quest book
		 */
		cmds.add(new Command("quest", "rc.quest.book", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				QuestBook book = new QuestBook(player);
				// TODO remove
				Playerface.takeItem(player, new ItemStack(Material.LOG, 1));
				for (ItemStack stack : player.getInventory())
					if (stack != null)
						if (stack.getType().equals(book.getType())) {
							if (stack.hasItemMeta()) {
								if (stack.getItemMeta().getDisplayName().equals(book.getItemMeta().getDisplayName())) {
									return;
								}
							}
						}
				Playerface.giveItem(player, book);
			}
		}, new CommandField("book", FieldProperty.IDENTIFIER)));

		return cmds;
	}

}
