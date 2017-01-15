package org.caliog.Villagers.Chat;

import org.caliog.Villagers.NPC.Villager;
import org.caliog.Villagers.Quests.Quest;
import org.caliog.myRPG.Entities.myClass;

public abstract class ChatTask {

	protected Quest quest;

	public ChatTask(Quest quest) {
		this.quest = quest;
	}

	public ChatTask() {
		quest = null;
	}

	public abstract void execute(myClass player, Villager villager);

}
