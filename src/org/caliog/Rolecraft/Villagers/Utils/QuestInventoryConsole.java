package org.caliog.Rolecraft.Villagers.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.ConsoleStoppable;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.PlayerChatReader;

import net.md_5.bungee.api.ChatColor;

public class QuestInventoryConsole {

	public static String chooseQuestVillager(Player player) {
		PlayerChatReader reader = new PlayerChatReader(player);
		Manager.plugin.getServer().getPluginManager().registerEvents(reader, Manager.plugin);
		ConsoleStoppable cs = new ConsoleStoppable() {

			private String lastLine = "";

			@Override
			public void run() {
				if (!super.check())
					return;
				// TODO not exactly what we need
				player.sendMessage(ChatColor.GRAY + "Enter the name of the quest villager: (q to quit)");
				String cLine = reader.getLine();
				if (!cLine.equals(lastLine))
					lastLine = cLine;
				else
					return;
				if (lastLine.equals("q") || lastLine.equals("quit") || lastLine.equals("exit"))
					stop();

				Villager v;
				if ((v = VManager.getVillager(lastLine)) != null) {
					setString(v.getName());
					stop();
				} else {
					player.sendMessage(ChatColor.BLACK + lastLine + ChatColor.GRAY + " is not a villager.");
				}
			}
		};

		Manager.scheduleRepeatingTask(cs, 0L, 10L);
		String name = cs.getString();
		HandlerList.unregisterAll(reader);
		return name;
	}

}
