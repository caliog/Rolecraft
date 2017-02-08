package org.caliog.Rolecraft.Villagers.Chat;

import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.Quests.Quest;

public abstract class ChatTask {

	protected Quest quest;

	public ChatTask(Quest quest) {
		this.quest = quest;
	}

	public ChatTask() {
		quest = null;
	}

	public abstract void execute(RolecraftPlayer player, Villager villager);

}
