package org.caliog.Rolecraft.Villagers.Quests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class QManager {

	private static List<Quest> quests = new ArrayList<Quest>();

	public static void init() throws IOException {
		quests.clear();
		QuestLoader.init();
		File dir = new File(FilePath.quests);
		for (String n : dir.list()) {
			n = n.replaceAll(".jar", "").replaceAll(".yml", "");
			if (QuestLoader.isJarQuest(n))
				quests.add(QuestLoader.load(n));
			else if (QuestLoader.isYmlQuest(n)) {
				quests.add(QuestLoader.loadYMLQuest(n));
			}
		}
		while (quests.contains(null))
			quests.remove(null);
	}

	public static Quest getQuest(String id) {
		for (Quest quest : quests) {
			if (quest.getName().equals(id))
				return quest;
		}
		return null;
	}

	public static Quest searchFittingQuest(RolecraftPlayer player, Villager villager) {
		if (villager == null || player == null)
			return null;
		for (String q : player.getUnCompletedQuests()) {
			Quest quest = getQuest(q);
			if (quest != null && quest.getTargetLocation(player) != null) {
				if (quest.getTargetLocation(player).distanceSquared(villager.getLocation()) < 10) {
					return quest;
				}
			}
		}

		for (String id : villager.getQuests()) {
			if (player.isCompleted(id))
				continue;
			Quest q = getQuest(id);
			if (q == null)
				continue;
			if (q.hasClazz() && !player.getType().equals(q.getClazz()))
				continue;
			if (q.hasMinLevel() && player.getLevel() < q.getMinLevel())
				continue;
			if (player.getUnCompletedQuests().contains(q.getName()) && !q.couldComplete(player))
				continue;
			if (q.isChainQuest() && !player.isCompleted(q.getChainQuest()))
				continue;
			return q;
		}

		return null;
	}

	public static List<Quest> getQuests() {
		return quests;
	}

	public static void addYmlQuest(YmlQuest quest) {
		if (quest != null) {
			for (Quest q : quests) {
				if (q.getName().equals(quest.getName())) {
					quests.remove(q);
					break;
				}
			}
			quests.add(quest);
		}
	}
}
