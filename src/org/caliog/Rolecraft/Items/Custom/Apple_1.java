package org.caliog.Rolecraft.Items.Custom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Messages.Key;

public class Apple_1 extends CustomItem {
	public Apple_1(int amount) {
		super(Material.GOLDEN_APPLE, "Apple I", false);
		setAmount(amount);
		syncItemStack();
	}

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName());
		List<String> lore = new ArrayList<String>();

		lore.add(" ");
		lore.add(ChatColor.GOLD + "This apple gives you 50% of your lifepoints!");
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

	public String getLore() {
		return null;
	}
}
