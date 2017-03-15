package org.caliog.Rolecraft.Villagers.Quests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.Villagers.Quests.Utils.MobAmount;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class QuestKill {

	private static File f = new File(FilePath.villagerDataQuestKillFile);
	private static HashMap<String, MobAmount> questKill = new HashMap<String, MobAmount>();

	public static void addNew(Player p, Quest q) {
		if (questKill.containsKey(p.getName())) {
			questKill.get(p.getName()).addQuest(q);
		} else
			questKill.put(p.getName(), new MobAmount(q));
	}

	public static void killed(Player p, Mob m) {
		if (questKill.containsKey(p.getName()))
			questKill.get(p.getName()).killed(m.getName());

		RolecraftPlayer rcp = PlayerManager.getPlayer(p.getUniqueId());
		if (rcp != null)
			rcp.checkQuests();
	}

	public static void delete(Player p, String questName) {
		if (questKill.containsKey(p.getName()))
			questKill.get(p.getName()).delete(questName);
	}

	public static boolean isFinished(Player p, String q) {
		if (questKill.containsKey(p.getName()))
			return questKill.get(p.getName()).isFinished(q);
		return true;
	}

	public static int getKilled(Player p, String q, String m) {
		if (questKill.containsKey(p.getName())) {
			return questKill.get(p.getName()).getKilled(q, m);
		} else
			return -1;
	}

	public static void save() throws IOException {
		f.delete();
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

		for (String p : questKill.keySet()) {
			questKill.get(p).toSection(config.createSection(p));
		}

		config.save(f);
	}

	public static void load() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		for (String p : config.getKeys(false)) {
			MobAmount ma = MobAmount.fromSection(config.getConfigurationSection(p));
			questKill.put(p, ma);
		}
	}

}
