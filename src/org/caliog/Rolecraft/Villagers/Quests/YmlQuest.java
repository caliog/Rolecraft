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
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.ItemUtils;
import org.caliog.Rolecraft.Utils.QuestStatus;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.Chat.CMessage;
import org.caliog.Rolecraft.Villagers.Chat.CMessage.MessageType;
import org.caliog.Rolecraft.Villagers.Chat.ChatTask;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestEditorMenu;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestInfoMenu;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.Messages.MsgKey;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.Stoppable;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class YmlQuest extends Quest {

	protected YamlConfiguration config;

	private final boolean loaded;
	private Villager v;

	public YmlQuest(String name) {
		super(name);
		File file = new File(FilePath.quests + name + ".yml");
		if (file.exists()) {
			config = YamlConfiguration.loadConfiguration(file);
			loadQuest();
			loaded = true;
		} else
			loaded = false;
	}

	@Override
	public Location getTargetLocation(RolecraftPlayer player) {
		if (player.getQuestStatus(getName()).equals(QuestStatus.FIRST))
			if (v != null) {
				return v.getEntityLocation();
			}
		return null;
	}

	private void loadQuest() {
		loadMessages();
		loadRewards();
		loadCollects();
		loadReceives();
		loadMobs();

		Stoppable s = new Stoppable() {

			@Override
			public void run() {
				if (VManager.isLoaded()) {
					if (config.isString("target-villager")) {
						String name = config.getString("target-villager");
						Villager vl = VManager.getVillager(name);
						v = vl;
					}
					stop();
				}

			}
		};
		s.setTaskID(Manager.scheduleRepeatingTask(s, 10L, 10L, 600L));
	}

	private void loadMessages() {
		if (config.isConfigurationSection("messages"))
			for (String id : config.getConfigurationSection("messages").getKeys(false)) {
				CMessage msg = CMessage.fromString(config.getConfigurationSection("messages").getString(id), Integer.parseInt(id));
				if (msg != null) {
					if (id.equals("1")) {// id:1 is reserved for the
											// "accept-quest"
											// message; default start with id:0

						msg.setTask(new ChatTask(this) {

							@Override
							public void execute(RolecraftPlayer player, Villager villager) {
								player.newQuest(quest.getName());
								if (!config.getBoolean("target-villager-give"))
									Playerface.giveItem(player.getPlayer(), getReceives());
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
									Playerface.giveItem(player.getPlayer(), getReceives());
								}

							}

						});

					messages.put(Integer.parseInt(id), msg);
				}
			}
		else {
			CMessage msg = new CMessage(null, MessageType.END);
			msg.setTask(new ChatTask(this) {

				@Override
				public void execute(RolecraftPlayer player, Villager villager) {
					MenuManager.openMenu(player.getPlayer(), new QuestInfoMenu((YmlQuest) quest, true));
				}
			});
			messages.put(0, msg);

			msg = new CMessage(null, MessageType.END);
			msg.setTask(new ChatTask(this) {

				@Override
				public void execute(RolecraftPlayer player, Villager villager) {
					if (Playerface.hasItem(player.getPlayer(), getCollects())) {
						if (((YmlQuest) quest).getTargetVillager() != null)
							Playerface.takeItem(player.getPlayer(), getCollects());
						player.raiseQuestStatus(quest.getName());
						if (quest.couldComplete(player)) {
							player.completeQuest(quest.getName());
						} else {
							Msg.sendMessage(player.getPlayer(), MsgKey.QUEST_DELIVERED_ITEM);
						}
					} else {
						Msg.sendMessage(player.getPlayer(), MsgKey.QUEST_MISSING_COLLECT);
					}
				}

			});
			messages.put(1, msg);
		}
	}

	@Override
	public int getMessageStart(RolecraftPlayer p) {
		if (getTargetLocation(p) != null) {
			return config.getInt("target-villager-message");
		} else if (this.couldComplete(p)) {
			return config.getInt("completed-message");
		}

		return 0;// default start with id 0

	}

	private void loadRewards() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (config.isList("rewards")) {
			for (String l : config.getStringList("rewards"))
				list.add(ItemUtils.getItem(l));
		} else {
			for (int i = 0; i < 7; i++) {
				if (config.isItemStack("rewards." + (i + 1))) {
					list.add(config.getItemStack("rewards." + (i + 1)));
				}
			}
		}
		this.rewards = list;
	}

	private void loadCollects() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (config.isList("collects")) {
			for (String l : config.getStringList("collects"))
				list.add(ItemUtils.getItem(l));
		} else {
			for (int i = 0; i < 7; i++) {
				if (config.isItemStack("collects." + (i + 1))) {
					list.add(config.getItemStack("collects." + (i + 1)));
				}
			}
		}
		this.collects = list;
	}

	private void loadReceives() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (config.isList("receives")) {
			for (String l : config.getStringList("receives"))
				list.add(ItemUtils.getItem(l));
		} else {
			for (int i = 0; i < 7; i++) {
				if (config.isItemStack("receives." + (i + 1))) {
					list.add(config.getItemStack("receives." + (i + 1)));
				}
			}
		}
		this.receives = list;
	}

	private void loadMobs() {
		if (config.isConfigurationSection("mobs")) {
			ConfigurationSection sec = config.getConfigurationSection("mobs");
			for (String id : sec.getKeys(false)) {
				if (sec.isInt(id)) {
					mobs.put(id, sec.getInt(id));
				}
			}
		}
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
		return (v != null) ? QuestStatus.SECOND : QuestStatus.FIRST;
	}

	@Override
	public String getChainQuest() {
		return config.getString("required-quest");
	}

	public String getTargetVillager() {
		return config.getString("target-villager", null);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	public void editedQuest(QuestEditorMenu menu) {
		String clazz = menu.getClazz();
		String targetVillager = menu.getTargetVillager();
		int minLevel = menu.getMinLevel();
		int exp = menu.getExp();
		String requiredQuest = menu.getRequiredQuest();
		List<ItemStack> rewards = menu.getRewards();
		List<ItemStack> collects = menu.getCollects();
		List<ItemStack> receives = menu.getReceives();
		HashMap<String, Integer> mobs = menu.getMobMap();
		try {
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
			config.set("required-quest", requiredQuest);
			for (int i = 0; i < collects.size(); i++)
				config.set("collects." + (i + 1), collects.get(i));
			for (int i = 0; i < rewards.size(); i++)
				config.set("rewards." + (i + 1), rewards.get(i));
			for (int i = 0; i < receives.size(); i++)
				config.set("receives." + (i + 1), receives.get(i));
			for (String k : mobs.keySet())
				config.set("mobs." + k, mobs.get(k));

			// static
			config.set("target-villager-message", 1);
			config.set("completed-message", 1);

			config.save(f);
			QManager.addYmlQuest(new YmlQuest(this.getName()));
		} catch (IOException e) {
			Debugger.exception("YmlQuest.editedQuest threw an IOException : ", e.getMessage());
			e.printStackTrace();
		}

	}

}
