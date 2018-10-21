package org.caliog.Rolecraft.Mobs.MobCreation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuInventoryView;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.Menus.PlayerConsole.ConsoleReader;
import org.caliog.Rolecraft.XMechanics.Messages.CmdMessage;
import org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Mat;

@SuppressWarnings({ "serial", "unchecked" })
public class MobEditMenu extends Menu {

	private MobSkeleton skel;
	public final static ArrayList<String> eq = new ArrayList<String>() {
		{
			add("HELMET");
			add("CHESTPLATE");
			add("LEGGINGS");
			add("BOOTS");
		}
	};
	public final static ArrayList<Material> helmet = new ArrayList<Material>() {
		{
			add(Mat.DIAMOND_HELMET.match());
			add(Mat.GOLD_HELMET.match());
			add(Mat.IRON_HELMET.match());
			add(Mat.LEATHER_HELMET.match());
		}
	};

	public final static ArrayList<Material> chestplate = new ArrayList<Material>() {
		{
			add(Mat.DIAMOND_CHESTPLATE.match());
			add(Mat.GOLD_CHESTPLATE.match());
			add(Mat.IRON_CHESTPLATE.match());
			add(Mat.LEATHER_CHESTPLATE.match());
		}
	};

	public final static ArrayList<Material> leggings = new ArrayList<Material>() {
		{
			add(Mat.DIAMOND_LEGGINGS.match());
			add(Mat.GOLD_LEGGINGS.match());
			add(Mat.IRON_LEGGINGS.match());
			add(Mat.LEATHER_LEGGINGS.match());
		}
	};

	public final static ArrayList<Material> boots = new ArrayList<Material>() {
		{
			add(Mat.DIAMOND_BOOTS.match());
			add(Mat.GOLD_BOOTS.match());
			add(Mat.IRON_BOOTS.match());
			add(Mat.LEATHER_BOOTS.match());
		}
	};

	public MobEditMenu() {
		super(3, "Mob Editor");
		skel = new MobSkeleton();
		setup();
	}

	public MobEditMenu(MobSkeleton skel) {
		super(3, "Mob Editor");
		this.skel = skel;
		setup();
	}

	public void setup() {
		ArrayList<String> lore = new ArrayList<String>();
		MenuItem item;

		// dummies
		item = new MenuItem(" ", Mat.STAINED_GLASS_PANE.match());
		this.setItem(14, item);
		this.setItem(15, item);
		this.setItem(16, item);
		this.setItem(17, item);

		// 0 - Name
		lore = new ArrayList<String>();
		lore.add("The name of the mob.");
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
							((MobEditMenu) getMenu()).setup();
							MenuManager.openMenu(player, getMenu());
							((MenuInventoryView) event.getView()).reload();
						}
					}, 0L, 4L);
				}
			});
		}
		this.setItem(0, item);
		// 1 - type
		lore = new ArrayList<String>();
		lore.add("The type of the mob.");
		item = new MenuItem((skel.getType() == null) ? "Type" : skel.getType().name(), Mat.SKULL_ITEM.match(), lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.exitMenu(player);

					Manager.scheduleRepeatingTask(new ConsoleReader(player) {

						@Override
						protected void doWork(String lastLine) {
							if (lastLine == null) {
								player.sendMessage(
										ChatColor.GOLD + "Enter the type: " + ChatColor.GRAY + "(q to quit)");
								return;
							} else {
								if (!lastLine.equals("") && !lastLine.equalsIgnoreCase("none")) {
									String t = lastLine.trim().toUpperCase().replaceAll(" ", "_");
									EntityType type = null;
									try {
										type = EntityType.valueOf(t);
										skel.setType(type);
										quit();
									} catch (Exception e) {
										player.sendMessage(
												ChatColor.GRAY + t + ChatColor.RED + " is not a valid entity-type.");
									}
								} else {
									skel.setType(null);
									quit();
								}
							}
						}

						@Override
						protected void quit() {
							super.stop();
							((MobEditMenu) getMenu()).setup();
							MenuManager.openMenu(player, getMenu());
							((MenuInventoryView) event.getView()).reload();
						}
					}, 0L, 4L);
				}
			});
		}
		this.setItem(1, item);

		// 2 - Damage
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Damage: " + skel.getDamage());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("Damage", Material.IRON_SWORD, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick())
						skel.setDamage(skel.getDamage() + 1);
					else if (skel.getDamage() > 0)
						skel.setDamage(skel.getDamage() - 1);
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(2, item);

		// 3 - Defense
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Defense: " + skel.getDefense());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("Defense", Material.IRON_CHESTPLATE, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick())
						skel.setDefense(skel.getDefense() + 1);
					else if (skel.getDefense() > 0)
						skel.setDefense(skel.getDefense() - 1);
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(3, item);

		// 4 - hp
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Hitpoints: " + skel.getHP());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("HP", Material.ANVIL, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick())
						skel.setHp(skel.getHP() + 1);
					else if (skel.getHP() > 0)
						skel.setHp(skel.getHP() - 1);
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}

			});
		}
		this.setItem(4, item);

		// 5 - Aggressive
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Aggressive: " + String.valueOf(skel.isAggressive()));
		lore.add("<click> - to change.");
		item = new MenuItem("Aggressive", Material.END_CRYSTAL, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					skel.toggleAggressive();
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(5, item);

		// 6 - level
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Level: " + skel.getLevel());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		item = new MenuItem("Level", Material.CAKE, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick())
						skel.setLevel(skel.getLevel() + 1);
					else if (skel.getLevel() > 0)
						skel.setLevel(skel.getLevel() - 1);
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}

			});
		}
		this.setItem(6, item);

		// 7 - exp
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Experience: " + skel.getExp());
		lore.add("<left> - increase");
		lore.add("<right> - decrease");
		lore.add(ChatColor.GRAY + "Percentage of experience ");
		lore.add(ChatColor.GRAY + "you need from this mob's");
		lore.add(ChatColor.GRAY + "level to the next level.");
		item = new MenuItem("Experience", Mat.EXP_BOTTLE.match(), lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					if (event.isLeftClick() && skel.getExp() < 100)
						skel.setExp(skel.getExp() + 1);
					else if (skel.getExp() > 0)
						skel.setExp(skel.getExp() - 1);
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}

			});
		}
		this.setItem(7, item);

		// 8 - pet
		lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Pet: " + String.valueOf(skel.isPet()));
		lore.add("<click> - to change.");
		item = new MenuItem("Pet", Material.END_CRYSTAL, lore);
		{
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					skel.togglePet();
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
				}
			});
		}
		this.setItem(8, item);

		// 9 - weapon input
		MenuItem input = new MenuItem("Weapon", Material.STICK, true);
		input.getLore().add("Put any weapon item here.");
		Material mat = skel.getEq().get("HAND");
		if (mat != null)
			input = new MenuItem(new ItemStack(mat), true);
		input.setButtonClickHandler(input.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				ItemStack stack = event.getCursor().clone();
				if (stack == null)
					return;
				skel.setEq("HAND", stack.getType());
				((MobEditMenu) getMenu()).setup();
				((MenuInventoryView) event.getView()).reload();
			}
		});
		this.setItem(9, input);

		// 10 - 13 equipment
		{

			int slot = 9;
			for (final String id : eq) {
				slot++;
				lore = new ArrayList<String>();
				lore.add("<click> - to swap");
				Material m = skel.getEq().get(id);

				try {
					final Field field = MobEditMenu.class.getField(id.toLowerCase());
					ArrayList<Material> list = ((ArrayList<Material>) field.get(null));
					String name = id;
					if (m == null) {
						m = Mat.WHITE_WOOL.last();
						name = "NONE";
					}
					item = new MenuItem(name, m, lore);

					{
						item.setButtonClickHandler(item.new ButtonClickHandler(this) {

							@Override
							public void onClick(InventoryClickEvent event, Player player) {
								Material current = skel.getEq().get(id);
								Material next = getNext(current, list);
								skel.setEq(id, next);
								((MobEditMenu) getMenu()).setup();
								((MenuInventoryView) event.getView()).reload();
							}
						});
					}
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					e.printStackTrace();
				}
				this.setItem(slot, item);
			}
		}

		// 18 - 25 input drop items with probability
		Object[] oa = skel.getDrops().keySet().toArray();
		ItemStack[] stacks = Arrays.copyOf(oa, oa.length, ItemStack[].class);
		for (int i = 0; i < 8; i++) {
			item = new MenuItem();
			if (stacks.length > i) {
				item = new MenuItem(stacks[i], false);
				item.getLore().add("Drop chance: " + String.valueOf(skel.getDrops().get(stacks[i])));
				item.getLore().add("<left> - increase");
				item.getLore().add("<right> - decrease");
				item.getLore().add("<shift+left> - remove");
			}
			final int j = i;
			item.setButtonClickHandler(item.new ButtonClickHandler(this) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					boolean empty = event.getCursor() == null || event.getCursor().getType().equals(Material.AIR);
					if (stacks.length > j && event.isShiftClick()) {
						skel.removeDrop(event.getCurrentItem());
					} else if (stacks.length > j && empty) {
						if (event.isLeftClick())
							skel.increaseDrop(stacks[j]);
						else
							skel.decreaseDrop(stacks[j]);
					} else if (!empty) {
						skel.addDrop(new ItemStack(event.getCursor().getType(), event.getCursor().getAmount()), 50);
					}
					((MobEditMenu) getMenu()).setup();
					((MenuInventoryView) event.getView()).reload();
					player.updateInventory();
				}
			});
			this.setItem(i + 18, item);
		}

		// 26 - save button
		item = new MenuItem("Save", Mat.GREEN_STAINED_GLASS_PANE.remove_first(), (short) 13, 1);
		item.setButtonClickHandler(item.new ButtonClickHandler(this) {

			@Override
			public void onClick(InventoryClickEvent event, Player player) {
				if (skel.saveToFile()) {
					player.sendMessage(
							CmdMessage.savedItemMob.replaceAll("%A%", skel.getName() + "_" + skel.getLevel()));
				} else
					player.sendMessage(CmdMessage.failedSaveItemMob);
				MenuManager.exitMenu(player);
			}
		});
		this.setItem(height * 9 - 1, item);

	}

	private Material getNext(Material mat, Object l) {
		ArrayList<Material> list = (ArrayList<Material>) l;
		boolean r = false;
		for (Material m : (ArrayList<Material>) list) {
			if (r)
				return m;
			if (m.equals(mat))
				r = true;
		}
		if (r)
			return null;
		return list.get(0);
	}

	public static MobEditMenu loadByName(String name) {
		MobSkeleton s = MobSkeleton.loadByName(name);
		return new MobEditMenu(s);
	}
}
