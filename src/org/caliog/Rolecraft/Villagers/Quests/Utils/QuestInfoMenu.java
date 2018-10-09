package org.caliog.Rolecraft.Villagers.Quests.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Villagers.Quests.Quest;
import org.caliog.Rolecraft.Villagers.Quests.QuestKill;
import org.caliog.Rolecraft.Villagers.Quests.YmlQuest;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.VersionControll.Mat;

public class QuestInfoMenu extends Menu {

	private final YmlQuest quest;
	private final boolean accept;

	public QuestInfoMenu(YmlQuest quest) {
		this(quest, false);
	}

	public QuestInfoMenu(YmlQuest quest, boolean accept) {
		super(1, quest.getName());
		this.quest = quest;
		this.accept = accept;
		setup();
	}

	private void setup() {
		MenuItem item;
		ArrayList<String> lore = new ArrayList<String>();

		if (!quest.getMobs().isEmpty()) {
			// 0. item - mob kills
			item = new MenuItem(Msg.getMessage(Key.WORD_WANTED_MOBS), Mat.SKULL_ITEM.e());
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.openMenu(player, new MobSelectorMenu((QuestInfoMenu) getMenu(), player));
				}
			});
			this.setItem(0, item);
		}

		lore = new ArrayList<String>();

		if (!quest.getCollects().isEmpty()) {
			// 1. item - collects
			lore.add(Msg.getMessage(Key.QUEST_INFO_COLLECT));
			item = new MenuItem(Msg.getMessage(Key.WORD_COLLECT), Material.CHEST, lore);
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.openMenu(player,
							new ItemSelectorMenu((QuestInfoMenu) getMenu(),
									Msg.getMessage(Key.WORD_COLLECT) + " " + Msg.getMessage(Key.WORD_ITEMS),
									quest.getCollects()));
				}

			});
			this.setItem(1, item);
		}

		if (!quest.getRewards().isEmpty()) {
			// 2. item - rewards
			lore = new ArrayList<String>();
			lore.add(Msg.getMessage(Key.QUEST_INFO_REWARD));
			item = new MenuItem(Msg.getMessage(Key.WORD_REWARD), Material.GOLD_NUGGET, lore);
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.openMenu(player,
							new ItemSelectorMenu((QuestInfoMenu) getMenu(),
									Msg.getMessage(Key.WORD_REWARD) + " " + Msg.getMessage(Key.WORD_ITEMS),
									quest.getRewards()));
				}

			});
			this.setItem(2, item);
		}

		if (!quest.getReceives().isEmpty()) {
			// 3. item - receive on start item
			lore = new ArrayList<String>();
			lore.add(Msg.getMessage(Key.QUEST_INFO_START_ITEMS));
			item = new MenuItem(Msg.getMessage(Key.WORD_START) + " " + Msg.getMessage(Key.WORD_ITEMS),
					Mat.BOOK_AND_QUILL.e(), lore);
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.openMenu(player,
							new ItemSelectorMenu((QuestInfoMenu) getMenu(),
									Msg.getMessage(Key.WORD_START) + " " + Msg.getMessage(Key.WORD_ITEMS),
									quest.getReceives()));
				}

			});
			this.setItem(3, item);
		}

		// 4. item - exp reward
		lore = new ArrayList<String>();
		item = new MenuItem(Msg.getMessage(Key.WORD_EXPERIENCE) + ": " + ChatColor.GREEN + quest.getExp(),
				Mat.EXP_BOTTLE.e());
		this.setItem(4, item);

		// 5. item - lvl
		lore = new ArrayList<String>();
		item = new MenuItem(Msg.getMessage(Key.WORD_MINIMUM_LEVEL) + ": " + ChatColor.GOLD + quest.getMinLevel(),
				Material.CAKE);
		this.setItem(5, item);

		// 6. item - class
		lore = new ArrayList<String>();
		item = new MenuItem(Msg.getMessage(Key.WORD_REQUIRED_CLASS) + ": " + ChatColor.GRAY + quest.getClazz(),
				Material.DIAMOND_HELMET, lore);
		this.setItem(6, item);

		if (quest.getTargetVillager() != null) {
			// 7. item - villager
			lore = new ArrayList<String>();
			lore.add(Msg.getMessage(Key.QUEST_TARGET_VILLAGER));
			item = new MenuItem(quest.getTargetVillager(), Mat.SKULL_ITEM.e(), (short) 3, lore);
			this.setItem(7, item);
		}

		if (accept) {
			// accept button
			item = new MenuItem(Msg.getMessage(Key.WORD_ACCEPT), Mat.GREEN_STAINED_GLASS_PANE.e(), (short) 13, 1);
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					PlayerManager.getPlayer(player.getUniqueId()).newQuest(quest.getName());
					Playerface.giveItem(player.getPlayer(), ((QuestInfoMenu) getMenu()).getQuest().getReceives());
					player.performCommand("quest book");
					MenuManager.exitMenu(player);
				}
			});
			this.setItem(height * 9 - 1, item);
		} else {
			// exit button
			this.setItem(height * 9 - 1, item.new ExitButton(this, Msg.getMessage(Key.WORD_BACK)));
		}

	}

	public Quest getQuest() {
		return quest;
	}

	public boolean isAccept() {
		return accept;
	}

	class MobSelectorMenu extends Menu {

		public final QuestInfoMenu upperMenu;

		public MobSelectorMenu(QuestInfoMenu menu, Player player) {
			int size = menu.getQuest().getMobs().size();
			this.height = size / 9 + 1;
			this.name = Msg.getMessage(Key.WORD_WANTED_MOBS);
			this.upperMenu = menu;
			init();
			setup(player);
		}

		public void setup(Player player) {
			ArrayList<String> list = new ArrayList<String>();
			int c = 0;
			HashMap<String, Integer> map = upperMenu.getQuest().getMobs();
			for (String name : map.keySet()) {
				if (map.get(name) == 0)
					continue;
				list = new ArrayList<String>();
				String str = ChatColor.AQUA + Msg.getMessage(Key.WORD_AMOUNT) + ": " + map.get(name);
				if (!isAccept())
					str = ChatColor.AQUA + Msg.getMessage(Key.WORD_AMOUNT) + ": "
							+ (map.get(name) - QuestKill.getKilled(player, getQuest().getName(), name)) + "/"
							+ map.get(name);
				list.add(str);
				final MenuItem item = new MenuItem(name, Mat.SKULL_ITEM.e(), list);
				this.setItem(c, item);
				c++;
			}

			this.setItem(height * 9 - 1, new MenuItem().new ExitButton(this, Msg.getMessage(Key.WORD_BACK)));

		}

	}

	class ItemSelectorMenu extends Menu {
		public final QuestInfoMenu upperMenu;

		public ItemSelectorMenu(QuestInfoMenu menu, String name, List<ItemStack> man) {
			this.upperMenu = menu;
			this.height = 1;
			this.name = name;
			init(man);
		}

		public void init(List<ItemStack> man) {
			super.init();
			for (int i = 0; i < height * 9; i++) {
				if (i > 0 && i < height * 9 - 1) {
					if (i - 1 < man.size()) {
						this.setItem(i - 1, new MenuItem(man.get(i - 1), false));
					}
				}
			}
			this.setItem(height * 9 - 1, new MenuItem().new ExitButton(this, Msg.getMessage(Key.WORD_BACK)));
		}

	}

}
