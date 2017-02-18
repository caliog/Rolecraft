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

import net.md_5.bungee.api.ChatColor;

public class QuestInventory extends InventoryView {

	private final Player player;
	private final YmlQuest quest;
	private final Inventory top;
	private String questVillager;

	public QuestInventory(Player player, String name) {
		this.player = player;
		quest = new YmlQuest(name);
		top = Bukkit.createInventory(null, 45, "Quest Editor");
		reloadTop();
	}

	private void reloadTop() {
		top.clear();

		ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		if (questVillager != null)
			meta.setDisplayName(ChatColor.GOLD + questVillager);
		else
			meta.setDisplayName("Quest-Villager *");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "<click> - and type");
		lore.add(ChatColor.GRAY + "the name of the");
		lore.add(ChatColor.GRAY + "quest-villager.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(0, stack);

		stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		meta = Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
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
		meta.setDisplayName("Kill Mobs");
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "none");
		lore.add(ChatColor.GRAY + "<left click> - monster");
		lore.add(ChatColor.GRAY + "<right click> - amount");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		for (int i = 2; i < 45; i += 9)
			top.setItem(i, stack);

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
		lore.add(ChatColor.GOLD + "0%");
		lore.add(ChatColor.GRAY + "<left click> - to increase");
		lore.add(ChatColor.GRAY + "<right click> - to decrease");
		lore.add(ChatColor.GRAY + "reward-experience.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(6, stack);

		stack = new ItemStack(Material.LADDER, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.LADDER);
		meta.setDisplayName("Level");
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "min-level: 0");
		lore.add(ChatColor.GRAY + "<left click> - to increase");
		lore.add(ChatColor.GRAY + "<right click> - to decrease");
		lore.add(ChatColor.GRAY + "minimum level.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(7, stack);

		stack = new ItemStack(Material.IRON_SWORD, 1);
		meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);
		meta.setDisplayName("Class");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "none");
		lore.add(ChatColor.GRAY + "<click> to choose");
		lore.add(ChatColor.GRAY + "a required class.");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		top.setItem(8, stack);

		stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		meta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
		meta.setDisplayName(" ");
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
		// TODO save edited quest to file
	}

	public boolean inventoryClick(InventoryClickEvent event) {
		boolean cancel = false;
		if (!event.getClickedInventory().getTitle().equals("Quest Editor"))
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
		if (slot == 0) {
			QuestInventoryConsole.chooseQuestVillager(this, (Player) event.getWhoClicked());
		}

		return cancel;

	}

	public YmlQuest getQuest() {
		return quest;
	}

	public HashMap<String, Integer> getMobMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReceiveItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClazz() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTargetVillager() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMinLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getExp() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> getRewardList() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getCollectList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setQuestVillager(String name) {
		this.questVillager = name;
		if (name != null)
			reloadTop();
	}

}
