package org.caliog.Rolecraft.Items.ItemCreation;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Items.Armor;
import org.caliog.Rolecraft.Items.ItemEffect.ItemEffectType;
import org.caliog.Rolecraft.Items.Weapon;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuInventoryView;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.ConsoleReader;
import org.caliog.Rolecraft.XMechanics.VersionControll.Mat;

public class ItemEditMenu extends Menu {

	private ItemSkeleton skel;

	public ItemEditMenu() {
		super(2, "Item Editor");
		skel = new ItemSkeleton();
		setup();
	}

	public ItemEditMenu(ItemSkeleton skel) {
		super(2, "Item Editor");
		this.skel = skel;
		setup();
	}

	public void setup() {
		ArrayList<String> lore = new ArrayList<String>();
		MenuItem item;

		// 8 - Info
		lore.add("Place item in the");
		lore.add("left slot.");
		items.set(8, new MenuItem("Info", Material.SIGN, lore));

		// 6,15 - dummies
		item = new MenuItem(" ", Mat.STAINED_GLASS_PANE.e());
		this.setItem(6, item);
		this.setItem(15, item);
		this.setItem(16, item);

		// 0 - Name
		lore = new ArrayList<String>();
		lore.add("The name of the " + waString() + ".");
		item = new MenuItem((skel.getName() == null || skel.getName().length() == 0) ? "Name" : skel.getName(),
				Material.NAME_TAG, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.exitMenu(player);

					Manager.scheduleRepeatingTask(new ConsoleReader(player) {

						@Override
						protected void doWork(String lastLine) {
							if (lastLine == null) {
								player.sendMessage(
										ChatColor.GOLD + "Enter the name: " + ChatColor.GRAY + "(q to quit)");
								return;
							} else {
								if (!lastLine.equals("") && !lastLine.equalsIgnoreCase("none")) {
									skel.setName(lastLine);
								} else {
									skel.setName(null);
								}
								quit();
							}
						}

						@Override
						protected void quit() {
							super.stop();
							((ItemEditMenu) getMenu()).setup();
							MenuManager.openMenu(player, getMenu());
							((MenuInventoryView) event.getView()).reload();
						}
					}, 0L, 4L);
				}
			});
		}
		this.setItem(0, item);

		//TODO damage format should be 3,4,4,5
		// 1 - Damage/Defense
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + ddString().toLowerCase() + ": " + skel.getD());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem(ddString(), isWeapon() ? Material.IRON_SWORD : Material.LEATHER_CHESTPLATE, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick())
						skel.setD(skel.getD() + 1);
					else if (skel.getD() > 0)
						skel.setD(skel.getD() - 1);
					((ItemEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(1, item);

		// 2 - MinLvl
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "min-level: " + skel.getMinlvl());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("Minimum Level", Material.CAKE, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick())
						skel.setMinlvl(skel.getMinlvl() + 1);
					else if (skel.getMinlvl() > 0)
						skel.setMinlvl(skel.getMinlvl() - 1);
					((ItemEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}

			});
		}
		this.setItem(2, item);

		// 3 - Clazz
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Class: " + skel.getClazz());
		lore.add("<click> - to switch between classes.");
		item = new MenuItem("Required Class", Material.DIAMOND_HELMET, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					skel.setClazz(ClazzLoader.getNextClass(skel.getClazz()));
					((ItemEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(3, item);

		// 4 - Lore
		lore = new ArrayList<String>();
		if (skel.getLore() != null) {
			lore.add(ChatColor.GOLD + "Lore: " + ChatColor.GRAY + skel.getLore());
		}
		lore.add("<click> - and enter the");
		lore.add("desired lore.");
		lore.add("Type none for no lore.");
		item = new MenuItem("Lore", Mat.BOOK_AND_QUILL.e(), lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.exitMenu(player);

					Manager.scheduleRepeatingTask(new ConsoleReader(player) {

						@Override
						protected void doWork(String lastLine) {
							if (lastLine == null) {
								player.sendMessage(ChatColor.GOLD + "Enter the lore (e.g. Rare): " + ChatColor.GRAY
										+ "(q to quit)");
								return;
							} else {
								if (!lastLine.equals("") && !lastLine.equalsIgnoreCase("none")) {
									skel.setLore(lastLine);
								} else {
									skel.setLore(null);
								}
								quit();
							}
						}

						@Override
						protected void quit() {
							super.stop();
							((ItemEditMenu) getMenu()).setup();
							MenuManager.openMenu(player, getMenu());
							((MenuInventoryView) event.getView()).reload();
						}
					}, 0L, 4L);
				}
			});
		}
		this.setItem(4, item);

		// 5 - Tradeable
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Soulbound: " + String.valueOf(!skel.isTradeable()));
		lore.add("<click> - to change.");
		item = new MenuItem("Soulbound", Material.END_CRYSTAL, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					skel.setTradeable(!skel.isTradeable());
					((ItemEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(5, item);

		// 9 - 15 Effects
		{
			int slot = 8;
			for (final ItemEffectType type : ItemEffectType.values()) {
				slot++;
				lore = new ArrayList<String>();
				lore.add(ChatColor.GOLD + "Power: " + skel.getEffectPower(type));
				lore.add("<left> - increase");
				lore.add("<right> - decrease");
				item = new MenuItem(type.name(), Material.ENCHANTED_BOOK, lore);
				{
					item.setButtonClickHandler(item.new ButtonClickHandler(this) {

						@Override
						public void onClick(InventoryClickEvent event, Player player) {
							if (event.isLeftClick())
								skel.setEffectPower(skel.getEffectPower(type) + 1, type);
							else if (skel.getEffectPower(type) > 0)
								skel.setEffectPower(skel.getEffectPower(type) - 1, type);
							((ItemEditMenu) getMenu()).setup();
							((MenuInventoryView) event.getView()).reload();
						}
					});
				}
				this.setItem(slot, item);
			}
		}

		// 7 - Input
		MenuItem input = new MenuItem();
		ItemStack stack = skel.getStack();
		if (stack != null) {
			input = new MenuItem(stack, true);
		}
		input.setButtonClickHandler(input.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				ItemStack stack = event.getCursor().clone();
				if (stack == null)
					return;
				if (Weapon.isWeapon(stack)) {
					skel = new ItemSkeleton(Weapon.getInstance(stack));
				} else if (Armor.isArmor(stack)) {
					skel = new ItemSkeleton((Armor.getInstance(stack)));
				} else {
					skel = new ItemSkeleton(stack.getType());
				}
				event.setCancelled(true);

				((ItemEditMenu) getMenu()).setup();
				((MenuInventoryView) event.getView()).reload();
			}
		});
		this.setItem(7, input);

		// 26 - save button
		item = new MenuItem("Save", Mat.GREEN_STAINED_GLASS_PANE.f(), (short) 13, 1);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				skel.saveToFile();
				MenuManager.exitMenu(player);
			}
		});
		this.setItem(height * 9 - 1, item);

	}

	// helpers

	private boolean isWeapon() {
		return skel.isWeapon();
	}

	private String waString() {
		return isWeapon() ? "weapon" : "armor";
	}

	private String ddString() {
		return isWeapon() ? "Damage" : "Defense";
	}

	// static

	public static ItemEditMenu loadByName(String name) {
		ItemSkeleton skel = ItemSkeleton.loadByName(name);
		if (skel == null) {
			return null;
		}
		return new ItemEditMenu(skel);
	}

}
