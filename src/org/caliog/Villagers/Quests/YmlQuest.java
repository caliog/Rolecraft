package org.caliog.Villagers.Quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.caliog.Villagers.Chat.CMessage;
import org.caliog.Villagers.Chat.ChatTask;
import org.caliog.Villagers.NPC.Villager;
import org.caliog.Villagers.Utils.VManager;
import org.caliog.myRPG.Entities.Playerface;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Items.ItemUtils;
import org.caliog.myRPG.Utils.QuestStatus;

public class YmlQuest extends Quest {

	protected YamlConfiguration config;

	public YmlQuest(String name, YamlConfiguration config) {
		super(name);
		this.config = config;
	}

	@Override
	public Location getTargetLocation(myClass player) {
		if (player.getQuestStatus(getName()).equals(QuestStatus.FIRST))
			if (config.isString("target-villager")) {
				String name = config.getString("target-villager");
				Villager v = VManager.getVillager(name);

				if (v != null) {
					return v.getEntityLocation();
				}
			}

		return null;
	}

	@Override
	public HashMap<Integer, CMessage> getMessages() {
		HashMap<Integer, CMessage> map = new HashMap<Integer, CMessage>();

		for (String id : config.getConfigurationSection("messages").getKeys(false)) {
			CMessage msg = CMessage.fromString(config.getConfigurationSection("messages").getString(id), Integer.parseInt(id));
			if (msg != null) {
				if (id.equals("1")) {// id:1 is reserved for the "accept-quest"
										// message; default start with id:0

					msg.setTask(new ChatTask(this) {

						@Override
						public void execute(myClass player, Villager villager) {
							player.newQuest(quest.getName());
							QManager.updateQuestBook(player);
							ItemStack stack = getReceive();
							if (!config.getBoolean("target-villager-give"))
								if (stack != null)
									Playerface.giveItem(player.getPlayer(), stack);
						}

					});
				} else if (id.equals(config.getString("target-villager-message")))
					msg.setTask(new ChatTask(this) {

						@Override
						public void execute(myClass player, Villager villager) {
							player.raiseQuestStatus(this.quest.getName());
							if (config.getBoolean("target-villager-take"))
								Playerface.takeItem(player.getPlayer(), getCollects());
							else if (config.getBoolean("target-villager-give")) {
								Playerface.giveItem(player.getPlayer(), getReceive());
							}

						}
					});

				map.put(Integer.parseInt(id), msg);
			}
		}

		return map;
	}

	@Override
	public int getMessageStart(myClass p) {
		if (getTargetLocation(p) != null) {
			return config.getInt("target-villager-message");
		} else if (this.couldComplete(p)) {
			p.completeQuest(getName());
			return config.getInt("completed-message");
		} else if (!p.getQuestStatus(getName()).equals(QuestStatus.UNACCEPTED)
				&& p.getQuestStatus(this.getName()).isLowerThan(QuestStatus.COMPLETED)) {
			return config.getInt("uncompleted-message");
		} else {

			return 0;// default start with id 0
		}
	}

	@Override
	public List<ItemStack> getRewards() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (config.isList("rewards")) {
			for (String l : config.getStringList("rewards"))
				list.add(ItemUtils.getItem(l));
		}
		return list;
	}

	@Override
	public List<ItemStack> getCollects() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (config.isList("collects")) {
			for (String l : config.getStringList("collects"))
				list.add(ItemUtils.getItem(l));
		}
		return list;
	}

	@Override
	public ItemStack getReceive() {
		return ItemUtils.getItem(config.getString("receive-item"));
	}

	@Override
	public HashMap<String, Integer> getMobs() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (config.isConfigurationSection("mobs")) {
			ConfigurationSection sec = config.getConfigurationSection("mobs");
			for (String id : sec.getKeys(false)) {
				if (sec.isInt(id)) {
					map.put(id, sec.getInt(id));
				}
			}
		}
		return map;
	}

	@Override
	public int getExp() {
		String expr = config.getString("exp-reward");
		int e = 0;
		if (expr.contains("%")) {
			e = (int) (Playerface.getExpDifference(Integer.parseInt(expr.split("%")[1].split("-")[0]),
					Integer.parseInt(expr.split("%")[1].split("-")[1])) * (Integer.parseInt(expr.split("%")[0]) / 100.0F));
		} else {
			e = Integer.parseInt(expr);
		}
		return e;
	}

	@Override
	public String getClazz() {
		return config.getString("class");
	}

	@Override
	public int getMinLevel() {
		return config.getInt("min-level");
	}

	@Override
	public String getDescription() {
		String descr = config.getString("description");
		return ChatColor.translateAlternateColorCodes('&', descr);
	}

	@Override
	public QuestStatus hasToReach() {
		return QuestStatus.FIRST;
	}

	@Override
	public String getChainQuest() {
		return config.getString("required-quest");
	}

}
