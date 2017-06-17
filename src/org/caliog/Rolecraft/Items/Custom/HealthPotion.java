package org.caliog.Rolecraft.Items.Custom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

@SuppressWarnings("deprecation")
public class HealthPotion {

	public static ItemStack getHP1(int amount) {
		return getItemStack(amount, "Health Potion I", 1);
	}

	public static ItemStack getHP2(int amount) {
		return getItemStack(amount, "Health Potion II", 2);
	}

	public static ItemStack getHP3(int amount) {
		return getItemStack(amount, "Health Potion III", 4);
	}

	public static ItemStack getItemStack(int amount, String name, int heart) {
		Potion potion = new Potion(PotionType.INSTANT_HEAL);
		ItemStack stack = potion.toItemStack(amount);
		ItemMeta meta = stack.getItemMeta();
		if (stack.hasItemMeta()) {
			meta = stack.getItemMeta();
		} else
			meta = Bukkit.getItemFactory().getItemMeta(Material.POTION);
		if (Utils.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.setDisplayName(ChatColor.DARK_GRAY + name);
		List<String> lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GOLD + "This potion gives you " + heart + " heart" + (heart == 1 ? "" : "s") + "!");
		lore.add(" ");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public static List<ItemStack> all() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(getHP1(1));
		list.add(getHP2(1));
		list.add(getHP3(1));
		return list;
	}
}
