package org.caliog.Rolecraft.Villagers.Traders;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.NPC.Trader;
import org.caliog.Rolecraft.Villagers.Utils.DataSaver;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem.ExitButton;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class TraderMenu extends Menu {

	static String costsPhrase;

	public TraderMenu(Trader trader) {
		super(3, trader.getName());
		ExitButton button = new MenuItem().new ExitButton(this, "Exit!");
		items.set(3 * 9 - 1, button);
		costsPhrase = Msg.getMessage(Key.WORD_COSTS) + ": ";
	}

	/**
	 * This method only saves costs, slot and stack of the MenuItem in a string.
	 */
	public void loadFromString(String str) {
		String[] split1 = str.split(";;");
		for (String s : split1) {
			String[] split2 = s.split("::");
			int slot = 0, costs = 0;
			ItemStack stack;
			if (split2.length >= 3) {
				if (Utils.isInteger(split2[0]) && Utils.isInteger(split2[1])) {
					slot = Integer.valueOf(split2[0]);
					costs = Integer.valueOf(split2[1]);
					stack = DataSaver.getItem(split2[2]);
					MenuItem item = new MenuItem(stack, costs);
					if (stack.hasItemMeta()) {
						if (stack.getItemMeta().hasLore())
							item.getLore().addAll(stack.getItemMeta().getLore());
					}
					item.getLore().add(TraderMenu.costsPhrase + costs);
					item.setButtonClickHandler(item.new ButtonClickHandler(this) {
						@Override
						public void onClick(InventoryClickEvent event, Player player) {
							if (Manager.economy.has(player, item.getCosts())) {
								item.giveItem(player);
								Manager.economy.withdrawPlayer(player, item.getCosts());
							} else {
								// TODO ?! Message that he has to less money?!
							}
						}
					});
					items.set(slot, item);
				}
			}
		}
		// should be there by constructur, but just the be sure
		ExitButton button = new MenuItem().new ExitButton(this, "Exit!");
		items.set(3 * 9 - 1, button);
	}

	public String toString() {
		String text = "";
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getStack() != null && !items.get(i).getStack().getType().equals(Material.AIR))
				text += String.valueOf(i) + "::" + items.get(i).getCosts() + "::" + String.valueOf(DataSaver.save(items.get(i).getStack()))
						+ ";;";
		}
		return text;
	}

	public void editMenu(Inventory inv, HashMap<Integer, Integer> costs) {
		for (int i = 0; i < inv.getSize() - 1; i++) {
			int c = 0;
			if (costs.containsKey(i))
				c = costs.get(i);
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
				setItem(i, new MenuItem(Material.AIR));
				continue;
			}
			if (inv.getItem(i).hasItemMeta() && inv.getItem(i).getItemMeta().hasLore()) {
				ItemMeta meta = inv.getItem(i).getItemMeta();
				List<String> lore = inv.getItem(i).getItemMeta().getLore();
				for (String l : lore) {
					if (l.startsWith(costsPhrase)) {
						// if item only moved
						if (!costs.containsKey(i))
							c = Integer.valueOf(l.replace(costsPhrase, ""));
						lore.remove(l);
						break;
					}
				}
				meta.setLore(lore);
				inv.getItem(i).setItemMeta(meta);
			}
			MenuItem item = new MenuItem(inv.getItem(i), c);
			if (inv.getItem(i).hasItemMeta()) {
				if (inv.getItem(i).getItemMeta().hasLore()) {
					item.getLore().addAll(inv.getItem(i).getItemMeta().getLore());
				}
			}
			item.getLore().add(TraderMenu.costsPhrase + c);
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {
				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (Manager.economy.has(player, item.getCosts())) {
						item.giveItem(player);
						Manager.economy.withdrawPlayer(player, item.getCosts());
					} else {
						// TODO ?! Message that he has to less money?!
					}
				}
			});
			setItem(i, item);
		}
	}

	public boolean isNonEmpty() {
		for (MenuItem item : items) {
			if (item.getStack() != null && !item.getStack().getType().equals(Material.AIR))
				return true;
		}
		return false;
	}

}
