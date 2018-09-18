package org.caliog.Rolecraft.Items.Books;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Reflection.BukkitReflect;
import org.caliog.Rolecraft.XMechanics.Messages.Key;

public class StrBook extends Book {
	public StrBook(RolecraftPlayer clazz) {
		super("Strength", clazz);
	}

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName());
		if (BukkitReflect.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.values());
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + " + " + this.player.getStrength());

		lore.add(" ");
		lore.add(ChatColor.GOLD + "Drag a skillstar at this book");
		lore.add(ChatColor.GOLD + "to increase your strength!");
		lore.add(" ");

		lore.add(ChatColor.RED + Msg.getMessage(Key.WORD_SOULBOUND) + "!");
		meta.setLore(lore);
		setItemMeta(meta);
	}

	public List<ItemEffect> getEffects() {
		return this.effects;
	}

	public int getMinLevel() {
		return -1;
	}

	public String getClazz() {
		return null;
	}
}
