package org.caliog.Rolecraft.Items.Books;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.XMechanics.Messages.Translator.Phrase;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class IntBook extends Book {
	public IntBook(RolecraftPlayer clazz) {
		super("Intelligence", clazz);
	}

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName());
		if (Utils.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.values());
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + " + " + this.player.getIntelligence());

		lore.add(" ");
		lore.add(ChatColor.GOLD + "Drag a skillstar at this book");// TODO
																	// translate
		lore.add(ChatColor.GOLD + "to increase your intelligence!");
		lore.add(" ");

		lore.add(ChatColor.RED + Phrase.SOULBOUND.translate() + "!");
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
