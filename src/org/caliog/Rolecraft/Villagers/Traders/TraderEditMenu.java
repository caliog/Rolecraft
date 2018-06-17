package org.caliog.Rolecraft.Villagers.Traders;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.NPC.Trader;
import org.caliog.Rolecraft.Villagers.Utils.DataSaver;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuInventoryView;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem.ExitButton;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.ConsoleReader;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class TraderEditMenu extends Menu {

	private final Trader trader;
	private HashMap<Integer, Integer> costs = new HashMap<Integer, Integer>();

	public TraderEditMenu(Trader trader) {
		super(3, "[edit]" + trader.getName());
		this.trader = trader;
		loadFromString(trader.getTraderMenu().toString());
		TraderMenu.costsPhrase = Msg.getMessage(Key.WORD_COSTS) + ": ";
	}

	/**
	 * This method only saves costs, slot and stack of the MenuItem in a string.
	 */
	public void loadFromString(String str) {
		String[] split1 = str.split(";;");
		this.init();
		for (String s : split1) {
			String[] split2 = s.split("::");
			int slot = 0, costs = 0;
			ItemStack stack;
			if (split2.length >= 3) {
				if (Utils.isInteger(split2[0]) && Utils.isInteger(split2[1])) {
					slot = Integer.valueOf(split2[0]);
					costs = Integer.valueOf(split2[1]);
					this.costs.put(slot, costs);
					stack = DataSaver.getItem(split2[2]);
					MenuItem item = new MenuItem(stack, costs, true);
					if (stack.hasItemMeta()) {
						if (stack.getItemMeta().hasLore()) {
							item.getLore().addAll(stack.getItemMeta().getLore());
						}
					}
					item.getLore().add(TraderMenu.costsPhrase + costs);
					item.setButtonClickHandler(item.new ButtonClickHandler(this) {

						@Override
						public void onClick(InventoryClickEvent event, Player player) {
							if (event.isShiftClick()) {
								MenuManager.exitMenu(player);
								Manager.scheduleRepeatingTask(new ConsoleReader(player) {

									@Override
									protected void doWork(String lastLine) {
										if (lastLine == null) {
											player.sendMessage(ChatColor.GOLD + "Enter the money this item should cost !" + ChatColor.GRAY
													+ "(q to quit)");
											return;
										}

										if (!Utils.isInteger(lastLine)) {
											player.sendMessage(ChatColor.DARK_GRAY + lastLine + ChatColor.RED + " is not an integer!");
											player.sendMessage(ChatColor.GOLD + "Enter the money this item should cost !" + ChatColor.GRAY
													+ "(q to quit)");
											return;
										} else {
											((TraderEditMenu) getMenu()).changedCosts(event.getSlot(), Integer.valueOf(lastLine));
											quit();
										}
									}

									@Override
									protected void quit() {
										super.stop();
										MenuManager.openMenu(player, getMenu());
										((MenuInventoryView) event.getView()).reload();
									}
								}, 0L, 1L);
							}
						}
					});

					items.set(slot, item);
				}
			}
		}

		for (int i = 0; i < 3 * 9; i++) {
			if (items.get(i).getStack() == null || items.get(i).getStack().getType().equals(Material.AIR)) {
				items.get(i).setButtonClickHandler(items.get(i).new ButtonClickHandler(this) {

					@Override
					public void onClick(InventoryClickEvent event, Player player) {
						ItemStack c = event.getCursor();
						if (c != null && !c.getType().equals(Material.AIR)) {
							Manager.scheduleTask(new Runnable() {

								@Override
								public void run() {
									saveToMenu(event.getInventory());
									loadFromString(trader.getTraderMenu().toString());
									((MenuInventoryView) event.getView()).reload();
									// MenuManager.exitMenu(player);
									// MenuManager.openMenu(player, menu)
								}
							}, 5L);

						}
					}
				});
			}
		}

		ExitButton save = new MenuItem().new ExitButton(this, "Save!");
		save.setButtonClickHandler(new MenuItem().new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				saveToMenu(event.getInventory());// craftbukkit vs spigot
													// (getClickedInventory)
				MenuManager.exitMenu(player);
			}

		});
		items.set(3 * 9 - 1, save);
	}

	protected void changedCosts(int slot, Integer costs) {
		this.costs.put(slot, costs);
		items.get(slot).setCosts(costs);
		for (int i = 0; i < items.get(slot).getLore().size(); i++) {
			if (items.get(slot).getLore().get(i).startsWith(TraderMenu.costsPhrase)) {
				items.get(slot).getLore().set(i, TraderMenu.costsPhrase + costs);
				break;
			}
		}
	}

	public void saveToMenu(Inventory inv) {
		trader.editMenu(inv, costs);
	}

}
