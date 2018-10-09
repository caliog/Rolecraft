package org.caliog.Rolecraft.Villagers.Quests.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.Quests.Quest;
import org.caliog.Rolecraft.Villagers.Quests.YmlQuest;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuInventoryView;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.Menus.PlayerConsole.ConsoleReader;
import org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Mat;

public class QuestEditorMenu extends Menu {

	private final YmlQuest quest;
	private HashMap<String, Integer> mobs = new HashMap<String, Integer>();
	private ArrayList<ItemStack> collects = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> rewards = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> receives = new ArrayList<ItemStack>();
	private int exp = 0;
	private int minLevel = 0;
	private String clazz = "none";
	private String targetVillager;
	private String requiredQuest;

	public QuestEditorMenu(Player player, String name) {
		super(1, name);
		quest = new YmlQuest(name);
		if (quest.isLoaded())
			loadQuestData();
		setup();
	}

	private void loadQuestData() {
		mobs = quest.getMobs();
		collects = (ArrayList<ItemStack>) quest.getCollects();
		rewards = (ArrayList<ItemStack>) quest.getRewards();
		receives = (ArrayList<ItemStack>) quest.getReceives();
		exp = quest.getExp();
		minLevel = quest.getMinLevel();
		clazz = quest.getClazz();
		targetVillager = quest.getTargetVillager();
	}

	public void setup() {
		MenuItem item;
		ArrayList<String> lore = new ArrayList<String>();

		// 0. item - mob kills
		lore.add("Choose mobs the quester has to kill.");

		item = new MenuItem("Kill Mobs", Mat.SKULL_ITEM.e(), lore);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				MenuManager.openMenu(player, new MobSelectorMenu((QuestEditorMenu) getMenu()));
			}
		});
		this.setItem(0, item);

		lore = new ArrayList<String>();

		// 1. item - collects
		lore.add("Choose items the quester has to hand in.");
		item = new MenuItem("Collect", Material.CHEST, lore);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				MenuManager.openMenu(player,
						new ItemSelectorMenu((QuestEditorMenu) getMenu(), "Collect Items", collects));
			}

		});
		this.setItem(1, item);

		// 2. item - rewards
		lore = new ArrayList<String>();
		lore.add("Choose items the quester gets as reward.");
		item = new MenuItem("Reward Items", Material.GOLD_NUGGET, lore);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				MenuManager.openMenu(player,
						new ItemSelectorMenu((QuestEditorMenu) getMenu(), "Reward Items", rewards));
			}

		});
		this.setItem(2, item);

		// 3. item - receive on start item
		lore = new ArrayList<String>();
		lore.add("Choose items the quester will");
		lore.add("receive when he starts the quest.");
		item = new MenuItem("Start Items", Mat.BOOK_AND_QUILL.e(), lore);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				MenuManager.openMenu(player,
						new ItemSelectorMenu((QuestEditorMenu) getMenu(), "Start Items", receives));
			}

		});
		this.setItem(3, item);

		// 4. item - exp reward
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "exp: " + exp);
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("Experience Reward", Mat.EXP_BOTTLE.e(), lore);
		{
			final MenuItem final_item = item;
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					List<String> lore = final_item.getLore();
					for (int i = 0; i < lore.size(); i++) {
						String l = lore.get(i);
						if (l.contains("exp: ")) {
							if (event.isLeftClick())
								exp++;
							else if (exp > 0)
								exp--;
							lore.set(i, ChatColor.GOLD + "exp: " + exp);
							((MenuInventoryView) event.getView()).reload();
							break;
						}
					}
				}

			});
		}
		this.setItem(4, item);

		// 5. item - lvl
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "min-level: " + minLevel);
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("Minimum Level", Material.CAKE, lore);
		{
			final MenuItem final_item = item;
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					List<String> lore = final_item.getLore();
					for (int i = 0; i < lore.size(); i++) {
						String l = lore.get(i);
						if (l.contains("min-level: ")) {
							if (event.isLeftClick())
								minLevel++;
							else if (minLevel > 0)
								minLevel--;
							lore.set(i, ChatColor.GOLD + "min-level: " + minLevel);
							((MenuInventoryView) event.getView()).reload();
							break;
						}
					}
				}

			});
		}
		this.setItem(5, item);

		// 6. item - class
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Class: " + clazz);
		lore.add("<click> - to switch between classes.");
		item = new MenuItem("Required Class", Material.DIAMOND_HELMET, lore);
		{
			final MenuItem final_item = item;
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					List<String> lore = final_item.getLore();
					for (int i = 0; i < lore.size(); i++) {
						String l = lore.get(i);
						if (l.contains("Class: ")) {
							String c = l.split(": ")[1];
							clazz = ClazzLoader.getNextClass(c);
							lore.set(i, l.replace(c, clazz));
							((MenuInventoryView) event.getView()).reload();
							break;
						}
					}
				}
			});
		}
		this.setItem(6, item);

		// 7. item - villager
		lore = new ArrayList<String>();
		lore.add("<click> - and enter the");
		lore.add("name of the villager, which");
		lore.add("receives the collect items.");
		lore.add(ChatColor.GRAY + "leave it blank and the");
		lore.add(ChatColor.GRAY + "quester can keep his items.");
		item = new MenuItem(targetVillager == null ? "Villager" : targetVillager, Mat.SKULL_ITEM.e(), (short) 3, lore);
		{
			final MenuItem final_item = item;
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.exitMenu(player);
					Manager.scheduleRepeatingTask(new ConsoleReader(player) {

						@Override
						protected void doWork(String lastLine) {
							if (lastLine == null) {
								player.sendMessage(ChatColor.GOLD + "Enter the name of a villager: " + ChatColor.GRAY
										+ "(q to quit)");
								return;
							}
							Villager v = VManager.getVillager(lastLine);
							if (v == null) {
								player.sendMessage(
										ChatColor.DARK_GRAY + lastLine + ChatColor.RED + " is not a villager!");
								player.sendMessage(ChatColor.GOLD + "Enter the name of a villager: " + ChatColor.GRAY
										+ "(q to quit)");
								return;
							} else {
								((QuestEditorMenu) getMenu()).targetVillager = v.getName();
								final_item.setName(ChatColor.GOLD + v.getName());
								quit();
							}
						}

						@Override
						protected void quit() {
							super.stop();
							MenuManager.openMenu(player, getMenu());
							((MenuInventoryView) event.getView()).reload();
						}
					}, 0L, 4L);
				}
			});
		}
		this.setItem(7, item);

		// exit button
		item = new MenuItem("Save..", Mat.GREEN_STAINED_GLASS_PANE.e(), (short) 13, 1);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				quest.editedQuest((QuestEditorMenu) getMenu());
				MenuManager.exitMenu(player);
			}
		});
		this.setItem(height * 9 - 1, item);

	}

	public String getClazz() {
		return clazz;
	}

	public String getTargetVillager() {
		return targetVillager;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public ArrayList<ItemStack> getCollects() {
		return collects;
	}

	public ArrayList<ItemStack> getRewards() {
		return rewards;
	}

	public ArrayList<ItemStack> getReceives() {
		return receives;
	}

	public int getExp() {
		return exp;
	}

	public HashMap<String, Integer> getMobMap() {
		return mobs;
	}

	public YmlQuest getQuest() {
		return quest;
	}

	public String getRequiredQuest() {
		return requiredQuest;
	}

	public void setRequiredQuest(Quest q) {
		this.requiredQuest = q == null ? null : q.getName();
	}

	class MobSelectorMenu extends Menu {

		public final QuestEditorMenu upperMenu;

		public MobSelectorMenu(QuestEditorMenu menu) {
			Set<String> mobList = MobSpawner.getIdentSet();
			int size = mobList.size();
			this.height = size / 9 + 1;
			this.name = size == 0 ? "No mobs available" : "Choose Mobs to kill";
			this.upperMenu = menu;
			init();
			setup(mobList);
		}

		public void setup(Set<String> mobList) {
			ArrayList<String> list = new ArrayList<String>();
			int c = 0;
			for (String name : mobList) {
				list = new ArrayList<String>();
				int a = 0;
				if (upperMenu.mobs.containsKey(name))
					a = upperMenu.mobs.get(name);
				list.add(ChatColor.AQUA + "Amount: " + a);
				list.add(ChatColor.GRAY + "<left> - increase");
				list.add(ChatColor.GRAY + "<right> - decrease");
				final MenuItem item = new MenuItem(name, Mat.SKULL_ITEM.e(), list);
				item.setButtonClickHandler(item.new ButtonClickHandler(this) {

					@Override
					public void onClick(InventoryClickEvent event, Player player) {
						for (int i = 0; i < item.getLore().size(); i++) {
							String l = item.getLore().get(i);
							if (l.contains("Amount: ")) {
								try {
									int a = Integer.parseInt(l.split(": ")[1]);
									int n = a + (event.isLeftClick() ? 1 : -1);
									if (n < 0)
										n = 0;
									item.getLore().set(i, l.replace(String.valueOf(a), String.valueOf(n)));
									upperMenu.mobs.put(name, n);
									if (n == 0)
										upperMenu.mobs.remove(name);
								} catch (NumberFormatException e) {

								}
								((MenuInventoryView) event.getView()).reload();
								break;
							}
						}
					}
				});
				this.setItem(c, item);
				c++;
			}

			this.setItem(height * 9 - 1, new MenuItem().new ExitButton(this, "Save"));

		}

	}

	class ItemSelectorMenu extends Menu {
		public final QuestEditorMenu upperMenu;

		public ItemSelectorMenu(QuestEditorMenu menu, String name, ArrayList<ItemStack> man) {
			this.upperMenu = menu;
			this.height = 1;
			this.name = name;
			init(man);
			setup(man);
		}

		public void init(ArrayList<ItemStack> man) {
			super.init();
			for (int i = 0; i < height * 9; i++) {
				if (i > 0 && i < height * 9 - 1) {
					if (i - 1 < man.size()) {
						this.setItem(i, new MenuItem(man.get(i - 1), true));
					} else {
						this.setItem(i, new MenuItem());
					}
				} else {
					this.setItem(i, new MenuItem());
				}
			}

		}

		public void setup(ArrayList<ItemStack> man) {
			// info paper
			ArrayList<String> list = new ArrayList<String>();
			list.add("Drag & Drop items here...");
			MenuItem item = new MenuItem("Info", Material.PAPER, list);
			this.setItem(0, item);
			// Exit button
			item = new MenuItem("Save..", Mat.STAINED_GLASS_PANE.e(), (short) 13, 1);
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					man.clear();
					for (int i = 1; i < height * 9 - 1; i++) {
						ItemStack stack = event.getView().getTopInventory().getItem(i);
						if (stack != null && !stack.getType().equals(Material.AIR)) {
							man.add(stack);
						}
					}
					MenuManager.exitMenu(player);
				}

			});
			this.setItem(height * 9 - 1, item);
		}

	}

}
