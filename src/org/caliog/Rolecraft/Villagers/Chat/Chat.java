package org.caliog.Rolecraft.Villagers.Chat;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Villagers.Chat.CMessage.MessageType;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.Quests.QManager;
import org.caliog.Rolecraft.Villagers.Quests.Quest;

public class Chat {

	private final RolecraftPlayer player;
	private final Villager villager;

	private int current = 0;
	private final HashMap<Integer, CMessage> messages;
	private final Quest q;
	private boolean ended = false;
	private int taskId;

	public Chat(Player p, Villager v) {
		player = PlayerManager.getPlayer(p.getUniqueId());
		this.villager = v;
		this.q = QManager.searchFittingQuest(player, villager);
		if (q != null) {
			messages = q.getMessages();

			current = q.getMessageStart(player) - 1;

		} else
			messages = villager.getMessages();
		if (messages.isEmpty())
			end();
	}

	public void chat() {
		current++;
		if (getCurrent() == null) {
			end();
			return;
		}
		String name = villager.getName();
		if (name == null || name.equals("null")) {
			name = "Villager";
		}

		player.getPlayer().sendMessage(ChatColor.GOLD + name + ChatColor.WHITE + ": " + ChatColor.BOLD + "" + ChatColor.GOLD + '"'
				+ getCurrent().getMessage() + ChatColor.GOLD + '"');

		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				getCurrent().execute(player, villager);

			}
		});

		if (getCurrent().getType().equals(MessageType.END))
			end();
		else if (getCurrent().getType().equals(MessageType.TEXT)) {
			Manager.scheduleTask(new Runnable() {

				@Override
				public void run() {
					chat();

				}
			}, getCurrent().getTime());

		}

		if (!ended) {
			Manager.cancelTask(taskId);
			taskId = Manager.scheduleTask(new Runnable() {

				@Override
				public void run() {
					end();
				}
			}, 20L * 10);
		}

	}

	public void answer(boolean t) {
		if (!t && getCurrent().getType().equals(MessageType.QUESTION))
			current = getCurrent().getTarget() - 1;
		chat();

	}

	public boolean isListening() {
		return getCurrent().getType().equals(MessageType.QUESTION);
	}

	public CMessage getCurrent() {
		return messages.get(current);
	}

	public boolean isEnded() {
		return ended;
	}

	private void end() {
		ended = true;
		ChatManager.end(this);
	}

}
