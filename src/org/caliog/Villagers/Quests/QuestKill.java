package org.caliog.Villagers.Quests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Mobs.Mob;
import org.caliog.myRPG.Utils.FilePath;

public class QuestKill {

	private static File f = new File(FilePath.villagerDataQuestKillFile);
	private static HashMap<String, MobAmount> questKill = new HashMap<String, MobAmount>();

	public static void killed(Player p, Mob m) {
		if (questKill.containsKey(p.getName())) {
			questKill.get(p.getName()).killed(m);
		} else
			questKill.put(p.getName(), new MobAmount(m));

	}

	public static void delete(Player p, String m) {
		if (questKill.containsKey(p.getName())) {
			questKill.get(p.getName()).delete(m);
		}
	}

	public static int getKilled(Player p, String m) {
		if (questKill.containsKey(p.getName())) {
			return questKill.get(p.getName()).getKilled(m);
		} else
			return 0;
	}

	public static void save() throws IOException {
		f.delete();
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

		for (String p : questKill.keySet()) {
			config.set(p, questKill.get(p).toStringList());
		}

		config.save(f);
	}

	public static void load() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		for (String p : config.getKeys(false)) {
			MobAmount ma = new MobAmount();
			ma.fromStringList(config.getStringList(p));
			questKill.put(p, ma);
		}
	}

}
