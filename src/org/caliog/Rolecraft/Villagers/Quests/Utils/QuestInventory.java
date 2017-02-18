package org.caliog.Rolecraft.Villagers.Quests.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Villagers.Quests.YmlQuest;
import org.caliog.Rolecraft.XMechanics.Utils.Pair;

import org.bukkit.ChatColor;

public class QuestInventory extends InventoryView {

	private final Player player;
	private final YmlQuest quest;
	private final Inventory top;
	private String questVillager;
	private String targetVillager;
	private HashMap<Integer, Pair<String, Integer>> mobs = new HashMap<Integer, Pair<String, Integer>>();
	private String clazz;
	private int minLvl = 0;
	private int exp = 0;
	private List<ItemStack> rewards = new ArrayList<ItemStack>();
	private List<ItemStack> collects = new ArrayList<ItemStack>();
	private ItemStack receive;

	public QuestInventory(Player player, String name) {
		this.player = player;
		quest = new YmlQuest(name);
		if (quest.isLoaded())
			loadQuest();

		top = Bukkit.createInventory(null, 45, "Quest Editor");
		reloadTop();
	}

	private void loadQuest() {
		targetVillager = quest.getConfig().getString("target-villager");
		clazz = quest.getClazz();
		minLvl = quest.getMinLevel();
		exp = quest.getExp();
		rewards = quest.getRewards();
		collects = quest.getCollects();
		receive = quest.getReceive();
	}

	private void reloadTop() {

		ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		if (questVillager != null)
			meta.setDisplayName(ChatColor.GOLD + questVillager);
		else
			meta.setDisplayName("Quest-Villager");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "<click> - and type");
		lore.add(ChatColor.GRAY + "the name of the");
		lore.add(ChatColor.GRAY + "quest-villager.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(0, stack);

		stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		meta = Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

		if (targetVillager != null)
			meta.setDisplayName(ChatColor.GOLD + targetVillager);
		else
			meta.setDisplayName("Target-Villager");

		lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "<click> - and type");
		lore.add(ChatColor.GRAY + "the name of the");
		lore.add(ChatColor.GRAY + "target-villager.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(1, stack);

		stack = new ItemStack(Material.SKULL_ITEM, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		for (int i = 2; i < 45; i += 9) {
			String mobName = null;
			int amount = -1;
			if (mobs.containsKey(i)) {
				mobName = mobs.get(i).first;
				amount = mobs.get(i).second;
			}
			meta.setDisplayName("Kill Mobs");
			lore = new ArrayList<String>();
			if (mobName == null)
				lore.add(ChatColor.GOLD + "none");
			else
				lore.add(ChatColor.GOLD + mobName + ChatColor.WHITE + ":" + ChatColor.AQUA + amount);
			lore.add(ChatColor.GRAY + "<left click> - monster");
			lore.add(ChatColor.GRAY + "<right click> - amount");
			meta.setLore(lore);
			stack.setItemMeta(meta);
			top.setItem(i, stack);
		}

		stack = new ItemStack(Material.BOOK_AND_QUILL, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.BOOK_AND_QUILL);
		meta.setDisplayName("Receive");
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Place the item");
		lore.add(ChatColor.GRAY + "which the quester");
		lore.add(ChatColor.GRAY + "receives from");
		lore.add(ChatColor.GRAY + "the quest-villager below.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(3, stack);

		stack = new ItemStack(Material.CHEST, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.CHEST);
		meta.setDisplayName("Collects");
		lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Place the item(s)");
		lore.add(ChatColor.GRAY + "which the quester has");
		lore.add(ChatColor.GRAY + "to collect below.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(4, stack);

		stack = new ItemStack(Material.GOLD_NUGGET, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.GOLD_NUGGET);
		meta.setDisplayName("Rewards");
		lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Place the item(s)");
		lore.add(ChatColor.GRAY + "which the quester");
		lore.add(ChatColor.GRAY + "is rewarded with below.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(5, stack);

		stack = new ItemStack(Material.EXP_BOTTLE, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.EXP_BOTTLE);
		meta.setDisplayName("Experience");
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "" + exp + "%");
		lore.add(ChatColor.GRAY + "<left click> - to increase");
		lore.add(ChatColor.GRAY + "<right click> - to decrease");
		lore.add(ChatColor.GRAY + "the reward-experience.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(6, stack);

		stack = new ItemStack(Material.LADDER, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.LADDER);
		meta.setDisplayName("Level");
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "min-level: " + minLvl);
		lore.add(ChatColor.GRAY + "<left click> - to increase");
		lore.add(ChatColor.GRAY + "<right click> - to decrease");
		lore.add(ChatColor.GRAY + "the minimum level.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(7, stack);

		stack = new ItemStack(Material.IRON_SWORD, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);
		meta.setDisplayName("Class");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lore = new ArrayList<String>();
		if (clazz == null)
			lore.add(ChatColor.GOLD + "none");
		else
			lore.add(ChatColor.GOLD + clazz);
		lore.add(ChatColor.GRAY + "<click> to choose");
		lore.add(ChatColor.GRAY + "a required class.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(8, stack);

		stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		meta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
		meta.setDisplayName("-");
		stack.setItemMeta(meta);
		for (int i = 9; i < 37; i += 9) {
			top.setItem(i, stack);
			top.setItem(i + 1, stack);
			top.setItem(i + 6, stack);
			top.setItem(i + 7, stack);
			top.setItem(i + 8, stack);
			if (i > 10)
				top.setItem(i + 3, stack);

		}
	}

	@Override
	public Inventory getBottomInventory() {
		return player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	@Override
	public Inventory getTopInventory() {
		return top;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	public void closed() {
		this.getCollectList();
		this.getRewardList();
		this.getReceiveItem();
	}

	public boolean inventoryClick(InventoryClickEvent event) {
		boolean cancel = false;
		if (event.getClickedInventory() == null || event.getClickedInventory().getTitle() == null
				|| !event.getClickedInventory().getTitle().equals("Quest Editor"))
			return cancel;
		List<Integer> avSlots = new ArrayList<Integer>();
		avSlots.add(12);
		for (int i = 13; i < 54; i += 9) {
			avSlots.add(i);
			avSlots.add(i + 1);
		}
		if (!avSlots.contains(event.getSlot()))
			cancel = true;
		int slot = event.getSlot();
		Player p = (Player) event.getWhoClicked();

		if (event.isShiftClick()) {
			if (slot == 0)
				this.questVillager = null;
			else if (slot == 1)
				this.targetVillager = null;
			else if (slot % 9 == 2)
				mobs.remove(slot);
			else if (slot == 8)
				this.clazz = null;

		} else {
			if (slot == 0)
				QuestInventoryConsole.chooseQuestVillager(this, p);
			else if (slot == 1)
				QuestInventoryConsole.chooseTargetVillager(this, p);
			else if (slot % 9 == 2) {
				String mobName = null;
				if (mobs.containsKey(slot))
					mobName = mobs.get(slot).first;
				if (event.isLeftClick() || mobName != null)
					QuestInventoryConsole.chooseMob(this, p, slot, mobName, event.isLeftClick()); // its only right click if mobName is already set
			} else if (slot == 6) {
				if (event.isLeftClick()) {
					this.exp += 2;
				} else if (event.isRightClick()) {
					this.exp -= 2;
					if (this.exp < 0)
						this.exp = 0;
				}
				reloadTop();
			} else if (slot == 7) {
				if (event.isLeftClick()) {
					this.minLvl++;
				} else if (event.isRightClick()) {
					this.minLvl--;
					if (this.minLvl < 0)
						this.minLvl = 0;
				}
				reloadTop();
			} else if (slot == 8) {
				QuestInventoryConsole.chooseClazz(this, p);
			}
		}
		return cancel;
	}

	public YmlQuest getQuest() {
		return quest;
	}

	public HashMap<String, Integer> getMobMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Pair<String, Integer> p : mobs.values()) {
			map.put(p.first, p.second);
		}
		return map;
	}

	public ItemStack getReceiveItem() {
		return receive = top.getItem(12);
	}

	public String getClazz() {
		return clazz;
	}

	public String getQuestVillager() {
		return questVillager;
	}

	public String getTargetVillager() {
		return targetVillager;
	}

	public int getMinLevel() {
		return minLvl;
	}

	public int getExp() {
		return exp;
	}

	public List<ItemStack> getRewardList() {
		rewards.clear();
		for (int i = 14; i <= 41; i += 9) {
			rewards.add(top.getItem(i));
		}
		return rewards;
	}

	public List<ItemStack> getCollectList() {
		collects.clear();
		for (int i = 13; i <= 40; i += 9) {
			collects.add(top.getItem(i));
		}
		return collects;
	}

	public void setQuestVillager(String name) {
		if (name != null) {
			this.questVillager = name;
			reloadTop();
		}
	}

	public void setTargetVillager(String name) {
		if (name != null) {
			this.targetVillager = name;
			reloadTop();
		}
	}

	public void setMob(int slot, String name) {
		if (slot % 9 == 2 && name != null) {
			mobs.put(slot, new Pair<String, Integer>(name, 1));
			reloadTop();
		}
	}

	public void setMobAmount(int slot, int amount) {
		if (slot % 9 == 2) {
			Pair<String, Integer> p = mobs.get(slot);
			if (p == null)
				return;
			Pair<String, Integer> np = new Pair<String, Integer>(p.first, amount > 0 ? amount : 1);
			mobs.put(slot, np);
			reloadTop();
		}
	}

	public void setClazz(String clazz) {
		if (clazz != null) {
			this.clazz = clazz;
			reloadTop();
		}
	}

}
