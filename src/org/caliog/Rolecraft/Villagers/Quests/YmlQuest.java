package org.caliog.Rolecraft.Villagers.Quests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.ItemUtils;
import org.caliog.Rolecraft.Utils.QuestStatus;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.Chat.CMessage;
import org.caliog.Rolecraft.Villagers.Chat.ChatTask;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestInventory;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class YmlQuest extends Quest {

	protected YamlConfiguration config;

	private final boolean loaded;

	public YmlQuest(String name) {
		super(name);
		File file = new File(FilePath.quests + name + ".yml");
		if (file.exists()) {
			config = YamlConfiguration.loadConfiguration(file);
			loaded = true;
		} else
			loaded = false;
	}

	@Override
	public Location getTargetLocation(RolecraftPlayer player) {
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
						public void execute(RolecraftPlayer player, Villager villager) {
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
						public void execute(RolecraftPlayer player, Villager villager) {
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
	public int getMessageStart(RolecraftPlayer p) {
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
		} else {
			int c = 0;
			for (int i = 0; config.isItemStack("rewards." + i + 1); i++) {
				list.add(config.getItemStack("rewards." + (i + 1)));
				c++;
				if (c >= 4)
					break;
			}
		}
		return list;
	}

	@Override
	public List<ItemStack> getCollects() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (config.isList("collects")) {
			for (String l : config.getStringList("collects"))
				list.add(ItemUtils.getItem(l));
		} else {
			int c = 0;
			for (int i = 0; config.isItemStack("collects." + i + 1); i++) {
				list.add(config.getItemStack("collects." + (i + 1)));
				c++;
				if (c >= 4)
					break;
			}
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

	public boolean isLoaded() {
		return loaded;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	public void editedQuest(QuestInventory view) throws IOException {
		String clazz = view.getClazz();
		String targetVillager = view.getTargetVillager();
		int minLevel = view.getMinLevel();
		int exp = view.getExp();
		List<ItemStack> rewards = view.getRewardList();
		List<ItemStack> collect = view.getCollectList();
		ItemStack receive = view.getReceiveItem();
		HashMap<String, Integer> mobs = view.getMobMap();

		YamlConfiguration config = getConfig();
		File f = new File(FilePath.quests + getName() + ".yml");
		if (config == null) {
			if (!f.exists())
				f.createNewFile();
			config = YamlConfiguration.loadConfiguration(f);
		}

		config.set("class", clazz);
		config.set("exp-reward", exp);
		config.set("min-level", minLevel);
		config.set("target-villager", targetVillager);
		for (int i = 0; i < collect.size(); i++)
			config.set("collects." + (i + 1), collect.get(i));
		for (int i = 0; i < rewards.size(); i++)
			config.set("rewards." + (i + 1), rewards.get(i));
		config.set("receive-item", receive);
		for (String k : mobs.keySet())
			config.set("mobs." + k, mobs.get(k));

		config.save(f);

		String vName = view.getQuestVillager();
		Villager v = VManager.getVillager(vName);
		if (v != null)
			v.addQuest(getName());
	}

}
