package org.caliog.Rolecraft.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Reflection.BukkitReflect;

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
		if (BukkitReflect.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<String>();
		if (getLore() != null) {
			lore.add(ChatColor.GOLD + ChatColor.translateAlternateColorCodes('&', getLore()));
		}
		if ((hasClass()) || (hasMinLevel()) || (!isTradeable())) {
			lore.add(" ");
		}
		if (hasMinLevel()) {
			lore.add(ChatColor.RED + "MinLv: " + getMinLevel());
		}
		if (hasClass()) {
			lore.add(ChatColor.RED + Msg.getMessage(Key.WORD_CLASS) + ": " + getClazz());
		}
		if (!isTradeable()) {
			lore.add(ChatColor.RED + Msg.getMessage(Key.WORD_SOULBOUND) + "!");
		}
		meta.setLore(lore);
		setItemMeta(meta);
	}

	public String getName() {
		return this.name;
	}

	public abstract int getMinLevel();

	public abstract String getClazz();

	public abstract String getLore();

	public boolean hasClass() {
		return getClazz() != null;
	}

	public boolean hasMinLevel() {
		return getMinLevel() > 0;
	}

	public ItemStack getStackCopy() {
		ItemStack stack = new ItemStack(getType(), getAmount());
		stack.setItemMeta(this.getItemMeta());
		return stack;
	}

	public static boolean isItemTradeable(ItemStack item) {
		if (!isCustomItem(item)) {
			return true;
		}
		for (String l : item.getItemMeta().getLore()) {
			if (l.contains("soulbound") || l.contains(Msg.getMessage(Key.WORD_SOULBOUND))) {
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
