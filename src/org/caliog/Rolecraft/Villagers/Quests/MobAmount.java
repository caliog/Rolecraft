package org.caliog.Rolecraft.Villagers.Quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.caliog.Rolecraft.Entities.EntityUtils;
import org.caliog.Rolecraft.Mobs.Mob;

public class MobAmount {

	private HashMap<String, Integer> killed = new HashMap<String, Integer>();

	public MobAmount(Mob m) {
		killed.put(m.getName(), 1);
	}

	public MobAmount() {

	}

	public void killed(Mob m) {
		int a = getKilled(m.getName());
		a++;
		killed.put(m.getName(), a);
	}

	public int getKilled(String m) {
		if (killed.containsKey(m)) {
			return killed.get(m);
		} else
			return 0;
	}

	public void delete(String m) {
		if (killed.containsKey(m)) {
			killed.remove(m);
		}

	}

	public List<String> toStringList() {
		List<String> list = new ArrayList<String>();
		for (String m : killed.keySet())
			list.add(m + "," + killed.get(m));
		return list;
	}

	public void fromStringList(List<String> list) {
		for (String l : list) {
			String c = (l.split(",")[0]);
			if (!EntityUtils.isMobClass(c))
				continue;
			killed.put(c, Integer.parseInt(l.split(",")[1]));
		}
	}
}
