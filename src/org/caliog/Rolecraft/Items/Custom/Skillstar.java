package org.caliog.Rolecraft.Items.Custom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Messages.Key;

public class Skillstar extends CustomItem {
	public Skillstar(int amount) {
		super(Material.NETHER_STAR, "Skillstar", false);
		setAmount(amount);
		syncItemStack();
	}

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName());
		List<String> lore = new ArrayList<String>();

		lore.add(" ");
		lore.add(ChatColor.GOLD + "With this star you can");
		lore.add(ChatColor.GOLD + "increase your skills!");
		lore.add(ChatColor.GOLD + "Drag the star to a book!");
		lore.add(" ");
		if (!isTradeable()) {
			lore.add(ChatColor.RED + Msg.getMessage(Key.WORD_SOULBOUND) + "!");
		}
		meta.setLore(lore);
		setItemMeta(meta);
	}

	public List<ItemEffect> getEffects() {
		return this.effects;
	}

	public int getMinLevel() {
		return 0;
	}

	public String getClazz() {
		return null;
	}

	public static boolean isSkillstar(ItemStack stack) {
		if (!isCustomItem(stack)) {
			return false;
		}
		if (stack.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Skillstar")) {
			return true;
		}
		return false;
	}

	public String getLore() {
		return null;
	}
}
