package org.caliog.Rolecraft.Villagers.Quests.Utils;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.caliog.Rolecraft.Villagers.Quests.Quest;

public class MobAmount {

	private HashMap<String, HashMap<String, Integer>> killed = new HashMap<String, HashMap<String, Integer>>(); // countdown

	@SuppressWarnings("unchecked")
	public MobAmount(Quest q) {
		killed.put(q.getName(), (HashMap<String, Integer>) q.getMobs().clone());
	}

	@SuppressWarnings("unchecked")
	public MobAmount(HashMap<String, HashMap<String, Integer>> map) {
		this.killed = (HashMap<String, HashMap<String, Integer>>) map.clone();
	}

	@SuppressWarnings("unchecked")
	public void addQuest(Quest q) {
		killed.put(q.getName(), (HashMap<String, Integer>) q.getMobs().clone());
	}

	public int getKilled(String q, String m) {
		if (killed.containsKey(q))
			if (killed.get(q).containsKey(m))
				return killed.get(q).get(m);
		return -1;
	}

	public boolean isFinished(String q) {
		if (killed.containsKey(q))
			for (String m : killed.get(q).keySet()) {
				if (killed.get(q).get(m) > 0)
					return false;
			}
		return true;
	}

	public void killed(String mob) {
		for (String quest : killed.keySet()) {
			if (killed.get(quest).containsKey(mob)) {
				int a = killed.get(quest).get(mob);
				a--;
				a = a > 0 ? a : 0;
				killed.get(quest).put(mob, a);
			}
		}
	}

	public void delete(String questName) {
		killed.remove(questName);
	}

	public void toSection(ConfigurationSection section) {
		for (String quest : killed.keySet()) {
			for (String m : killed.get(quest).keySet()) {
				section.set(quest + "." + m, killed.get(quest).get(m));
			}
		}
	}

	public static MobAmount fromSection(ConfigurationSection section) {
		HashMap<String, HashMap<String, Integer>> bigMap = new HashMap<String, HashMap<String, Integer>>();
		for (String quest : section.getKeys(false)) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			for (String m : section.getConfigurationSection(quest).getKeys(false)) {
				map.put(m, section.getConfigurationSection(quest).getInt(m));
			}
			bigMap.put(quest, map);
		}
		return new MobAmount(bigMap);
	}

}
