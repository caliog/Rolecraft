package org.caliog.Rolecraft.Items.Books;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.Spells.Menu.SpellMenu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;

public class Spellbook extends CustomItem {

	public Spellbook(boolean enchanted) {
		super(enchanted ? Material.ENCHANTED_BOOK : Material.BOOK, ChatColor.DARK_GRAY + "Book of Spells", false);
		this.syncItemStack();
	}

	@Override
	public List<ItemEffect> getEffects() {
		return null;
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getClazz() {
		return null;
	}

	public static void onClick(Player p) {
		if (p == null)
			return;
		RolecraftPlayer player = PlayerManager.getPlayer(p.getUniqueId());
		if (player == null)
			return;
		MenuManager.openMenu(p, new SpellMenu(player));
	}

	public static boolean isSpellbook(ItemStack stack) {
		Spellbook dummy = new Spellbook(stack.getType().equals(Material.ENCHANTED_BOOK));
		if (!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) {
			return false;
		}
		return dummy.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName());
	}

	public static void refresh(RolecraftPlayer player) {
		for (ItemStack stack : player.getPlayer().getInventory().getContents())
			if (stack != null && isSpellbook(stack)) {
				stack.setType(player.getSpellPoints() > 0 ? Material.ENCHANTED_BOOK : Material.BOOK);
			}
		player.getPlayer().updateInventory();
	}

	public String getLore() {
		return null;
	}

	public static void giveSpellbookToPlayer(RolecraftPlayer rolecraftPlayer) {
		Spellbook book = new Spellbook(rolecraftPlayer.getSpellPoints() > 0);
		boolean found = false;
		for (ItemStack stack : rolecraftPlayer.getPlayer().getInventory().getContents()) {
			if (stack == null || !stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName())
				continue;
			if (stack.getItemMeta().getDisplayName().equals(book.getItemMeta().getDisplayName())) {
				found = true;
				stack.setType(book.getType());
				rolecraftPlayer.getPlayer().updateInventory();
			}
		}
		if (!found)
			Playerface.giveItem(rolecraftPlayer.getPlayer(), book);
	}

}
