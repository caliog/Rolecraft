package org.caliog.Rolecraft.Villagers.Quests.Utils;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.ConsoleReader;

import net.md_5.bungee.api.ChatColor;

public class QuestInventoryConsole {

	public static void chooseQuestVillager(final QuestInventory questInventory, final Player player) {
		player.closeInventory();
		ConsoleReader cr = new ConsoleReader(player) {

			@Override
			public void doWork(String lastLine) {
				// lastLine is only null if it is the first time the method is called
				if (lastLine == null)
					player.sendMessage(ChatColor.GRAY + "Enter the name of the quest villager: (q to quit)");
				else {

					Villager v;
					if ((v = VManager.getVillager(lastLine)) != null) {
						questInventory.setQuestVillager(v.getName());
						quit();
					} else {
						player.sendMessage(ChatColor.BOLD + lastLine + ChatColor.GRAY + " is not a villager.");
						player.sendMessage(ChatColor.GRAY + "Enter the name of the quest villager: (q to quit)");
					}
				}
			}

			@Override
			public void quit() {
				super.stop();
				player.openInventory(questInventory);
			}
		};
		cr.setTaskID(Manager.scheduleRepeatingTask(cr, 0L, 4L));
	}

}
