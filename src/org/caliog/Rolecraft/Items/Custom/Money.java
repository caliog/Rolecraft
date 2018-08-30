package org.caliog.Rolecraft.Items.Custom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class Money extends CustomItem {

	private int amount = 1;

	public Money(int amount) {
		super(RolecraftConfig.getCurrency(), getCurrencyName(false), true);
		this.amount = amount;
		syncItemStack();
	}

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName());
		List<String> lore = new ArrayList<String>();

		lore.add(ChatColor.GREEN + Msg.getMessage(Key.WORD_AMOUNT) + ": " + ChatColor.GOLD + amount);
		lore.add(" ");
		lore.add(ChatColor.GRAY + Msg.getMessage(Key.MONEY_CLICK));

		meta.setLore(lore);
		setItemMeta(meta);
	}

	@SuppressWarnings("deprecation")
	public void onClick(Player player) {
		if (Manager.economy != null) {
			if (player.getItemInHand().equals(this)) {
				Manager.economy.depositPlayer(player, this.amount);
				player.setItemInHand(new ItemStack(Material.AIR));
			}
		} else {
			Debugger.warning(LogTitle.NONE, "Tried to convert money-item to money without Vault installed. (Money.java, onClick)");
		}
	}

	public int getMoneyAmount() {
		return this.amount;
	}

	public void addAmount(int a) {
		this.amount += a;
		syncItemStack();
	}

	@Override
	public List<ItemEffect> getEffects() {
		return this.effects;
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getClazz() {
		return null;
	}

	@Override
	public String getLore() {
		return null;
	}

	public static boolean isMoney(ItemStack stack) {
		return getMoney(stack) != null;

	}

	public static Money getMoney(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasLore() && stack.getItemMeta().hasDisplayName()) {
			if (!stack.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + getCurrencyName(false)))
				return null;
			ItemMeta meta = stack.getItemMeta();
			List<String> lore = meta.getLore();
			String a = "1";
			for (String l : lore) {
				if (l.contains(": ")) {
					a = l.split(": ")[1].substring(2);
					break;
				}
			}
			if (Utils.isInteger(a)) {
				return new Money(Integer.parseInt(a));
			}
		}
		return null;
	}

	public static String getCurrencyName(boolean singular) {
		if (Manager.economy == null)
			return "money";
		String name = singular ? Manager.economy.currencyNameSingular() : Manager.economy.currencyNamePlural();
		if (name == null || name.length() == 0)
			name = "money";
		return name;
	}

}
