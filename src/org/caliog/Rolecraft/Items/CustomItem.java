package org.caliog.Rolecraft.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public abstract class CustomItem extends ItemStack {
	private final String name;
	private final boolean tradeable;
	protected List<ItemEffect> effects = new ArrayList<ItemEffect>();

	public CustomItem(Material type, String name, boolean tradeable) {
		super(type);
		this.tradeable = tradeable;
		this.name = name;
		if (hasItemMeta()) {
			setItemMeta(Bukkit.getItemFactory().getItemMeta(type));
			getItemMeta().setLore(new ArrayList<String>());
		}
	}

	public boolean isTradeable() {
		return this.tradeable;
	}

	public abstract List<ItemEffect> getEffects();

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName());
		if (Utils.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<String>();
		if ((hasClass()) || (hasMinLevel()) || (!isTradeable())) {
			lore.add(" ");
		}
		if (hasMinLevel()) {
			lore.add(ChatColor.RED + "MinLv: " + getMinLevel());
		}
		if (hasClass()) {
			lore.add(ChatColor.RED + "Klasse: " + getClazz());
		}
		if (!isTradeable()) {
			lore.add(ChatColor.RED + "soulbound!");
		}
		meta.setLore(lore);
		setItemMeta(meta);
	}

	public String getName() {
		return this.name;
	}

	public abstract int getMinLevel();

	public abstract String getClazz();

	public boolean hasClass() {
		return getClazz() != null;
	}

	public boolean hasMinLevel() {
		return getMinLevel() > 0;
	}

	public static boolean isItemTradeable(ItemStack item) {
		if (!isCustomItem(item)) {
			return false;
		}
		for (String l : item.getItemMeta().getLore()) {
			if (l.contains("soulbound")) {
				return false;
			}
		}
		return true;
	}

	public static boolean isCustomItem(ItemStack item) {
		if (item == null) {
			return false;
		}
		if (!item.hasItemMeta()) {
			return false;
		}
		if (!item.getItemMeta().hasLore()) {
			return false;
		}
		if (!item.getItemMeta().hasDisplayName()) {
			return false;
		}
		if (item.getItemMeta().getDisplayName().contains("" + ChatColor.DARK_GRAY)) {
			return true;
		}
		return false;
	}
}
