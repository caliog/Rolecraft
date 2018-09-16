package org.caliog.Rolecraft.XMechanics.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityUtils;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Groups.GManager;
import org.caliog.Rolecraft.Items.ItemUtils;
import org.caliog.Rolecraft.Items.Weapon;
import org.caliog.Rolecraft.Items.Books.Spellbook;
import org.caliog.Rolecraft.Items.Custom.Apple_1;
import org.caliog.Rolecraft.Items.Custom.Apple_2;
import org.caliog.Rolecraft.Items.Custom.HealthPotion;
import org.caliog.Rolecraft.Items.Custom.Money;
import org.caliog.Rolecraft.Items.Custom.Skillstar;
import org.caliog.Rolecraft.Mobs.Pet;
import org.caliog.Rolecraft.Utils.SkillInventoryView;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Utils.ChestHelper;
import org.caliog.Rolecraft.XMechanics.Utils.GroupManager;
import org.caliog.Rolecraft.XMechanics.Utils.ParticleEffect;
import org.caliog.Rolecraft.XMechanics.Utils.PlayerList;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;
import org.caliog.Rolecraft.XMechanics.VersionControll.Mat;

@SuppressWarnings("deprecation")
public class RolecraftListener implements Listener {

	HashMap<UUID, String[]> petMap = new HashMap<UUID, String[]>(); // player,
																	// mob, name

	@EventHandler(priority = EventPriority.HIGH)
	public void creatureSpawnEvent(CreatureSpawnEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;

		if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
			if (RolecraftConfig.isNaturalSpawnDisabled(event.getEntity().getWorld().getName()))
				event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void levelUp(PlayerLevelChangeEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		RolecraftPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (player == null) {
			return;
		}
		// max level
		int maxLevel = RolecraftConfig.getMaxLevel();
		if (maxLevel > 0 && event.getNewLevel() > maxLevel) {
			event.getPlayer().setLevel(maxLevel);
			return;
		}
		if (!PlayerManager.changedClass.contains(player.getPlayer().getUniqueId())) {
			if (event.getOldLevel() + 1 == event.getNewLevel()) {
				if (RolecraftConfig.isFireworkEnabled()) {
					Location loc = player.getPlayer().getLocation();
					Firework firework = (Firework) player.getPlayer().getWorld().spawn(loc, Firework.class);
					FireworkMeta data = firework.getFireworkMeta();
					Color c = Color.YELLOW;
					if (event.getNewLevel() > 20) {
						c = Color.GREEN;
					}
					if (event.getNewLevel() > 40) {
						c = Color.BLUE;
					}
					if (event.getNewLevel() > 60) {
						c = Color.LIME;
					}
					data.addEffects(new FireworkEffect[] {
							FireworkEffect.builder().withColor(c).with(FireworkEffect.Type.STAR).build() });
					data.setPower(0);

					firework.setFireworkMeta(data);
				}

				Playerface.giveItem(player.getPlayer(), new Skillstar(3));
				player.giveSpellPoint();
				Msg.sendMessage(event.getPlayer(), Key.LEVEL_REACHED, Msg.LEVEL, String.valueOf(event.getNewLevel()));
			}
		} else
			PlayerManager.changedClass.remove(player.getPlayer().getUniqueId());

		GroupManager.updateGroup(player.getPlayer(), event.getNewLevel());

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void inventoryClose(InventoryCloseEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		if ((event.getInventory().getType().equals(InventoryType.PLAYER))
				|| (event.getInventory().getType().equals(InventoryType.CRAFTING))) {
			ItemStack[] armor = (ItemStack[]) event.getPlayer().getInventory().getArmorContents().clone();
			for (int i = 0; i < armor.length; i++) {
				if ((armor[i] != null) && (!armor[i].getType().equals(Material.AIR))
						&& (!ItemUtils.checkForUse((Player) event.getPlayer(), armor[i]))) {
					Playerface.giveItem((Player) event.getPlayer(), armor[i]);
					armor[i] = null;
				}
			}
			event.getPlayer().getInventory().setArmorContents(armor);
		}

		// chests
		if (event.getInventory().getHolder() instanceof Chest) {
			Chest chest = (Chest) event.getInventory().getHolder();
			ChestHelper.loot(chest);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void spellEvent(final PlayerInteractEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		boolean useable = ItemUtils.checkForUse(event.getPlayer(), event.getItem());
		final RolecraftPlayer c = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (c == null) {
			return;
		}
		if (!Weapon.isWeapon(c, event.getItem())) {
			return;
		}
		if (useable) {
			if (RolecraftConfig.spellsEnabled())
				c.register(event.getAction());

		} else {
			event.setCancelled(true);
			Manager.scheduleTask(new Runnable() {

				@Override
				public void run() {
					event.getPlayer().updateInventory();

				}
			});
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void customItemClick(PlayerInteractEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		ItemStack stack = event.getPlayer().getInventory().getItemInMainHand(); // TODO
																				// CHECK
																				// FOR
																				// VERSION
																				// CONTROLL
		if (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
				&& (Skillstar.isSkillstar(stack))) {
			event.getPlayer()
					.openInventory(new SkillInventoryView(event.getPlayer(), event.getPlayer().getInventory()));
		} else if (((event.getAction().equals(Action.RIGHT_CLICK_AIR))
				|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) && (Spellbook.isSpellbook(stack))) {
			Spellbook.onClick(event.getPlayer());
		} else if (((event.getAction().equals(Action.RIGHT_CLICK_AIR))
				|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) && (Money.isMoney(stack))) {
			Money.getMoney(stack).transform(event.getPlayer(), true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void playerJoin(final PlayerJoinEvent event) {
		if (!PlayerManager.login(event.getPlayer())) {
			PlayerManager.register(event.getPlayer(), RolecraftConfig.getDefaultClass());
		}
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		RolecraftPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (player == null) {
			return;
		}
		if (event.getPlayer().getLevel() <= 0) {
			event.getPlayer().setLevel(1);
		}
		player.getPlayer().setSaturation(2.0F);
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				PlayerList.refreshList(event.getPlayer());
			}
		}, 20L);

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerQuit(PlayerQuitEvent event) {
		if (GManager.isInGroup(event.getPlayer())) {
			GManager.leaveGroup(event.getPlayer());
		}
		PlayerManager.logout(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void pickupItemEvent(PlayerPickupItemEvent event) {
		if (!Playerface.isAccessible(event.getPlayer(), event.getItem())) {
			event.setCancelled(true);
		} else {
			ItemStack stack = event.getItem().getItemStack();
			if (stack != null && stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null) {
				String name = event.getItem().getItemStack().getItemMeta().getDisplayName();

				// money
				if (Money.isMoney(stack)) {
					Money pickup = Money.getMoney(stack);

					// only pickup the value
					pickup.transform(event.getPlayer());
					event.getItem().remove();
					event.setCancelled(true);
					event.getPlayer().playSound(event.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2F, 0.2F);
					return;
					/*
					 * for (int i = 0; i <
					 * event.getPlayer().getInventory().getSize(); i++) {
					 * ItemStack s =
					 * event.getPlayer().getInventory().getItem(i); if
					 * (Money.isMoney(s)) { Money money = Money.getMoney(s);
					 * money.addAmount(pickup.getMoneyAmount());
					 * event.getPlayer().getInventory().setItem(i, money);
					 * event.getPlayer().playSound(event.getItem().getLocation()
					 * , Sound.ENTITY_ITEM_PICKUP, 0.2F, 0.2F);
					 * event.getItem().remove(); event.setCancelled(true);
					 * break; } }
					 */
				}

				// potions stack
				for (ItemStack hp : HealthPotion.all()) {
					if (name.equalsIgnoreCase(hp.getItemMeta().getDisplayName())) {
						ItemStack[] contents = event.getPlayer().getInventory().getContents();
						for (int i = 0; i < contents.length; i++) {
							if (contents[i] == null)
								continue;
							ItemMeta meta = null;
							if ((meta = contents[i].getItemMeta()) != null && meta.getDisplayName() != null)
								if (meta.getDisplayName().equalsIgnoreCase(hp.getItemMeta().getDisplayName())) {
									contents[i].setAmount(contents[i].getAmount() + 1);
									event.getPlayer().playSound(event.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP,
											0.2F, 0.2F);
									event.getItem().remove();
									event.setCancelled(true);
									return;
								}
						}

					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void dropItem(PlayerDropItemEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		Playerface.dropItem(event.getPlayer(), event.getPlayer().getLocation(), event.getItemDrop());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void inventoryClick(final InventoryClickEvent event) {
		if ((event.getView() instanceof SkillInventoryView)) {
			final RolecraftPlayer player = PlayerManager.getPlayer(event.getView().getPlayer().getUniqueId());
			if (event.getRawSlot() < 9) {
				if (Skillstar.isSkillstar(event.getCursor())) {
					if (event.getRawSlot() == 0) {
						if (player.skillStrength(event.getCursor().getAmount())) {
							event.setCursor(null);
						} else {
							Msg.sendMessage(player.getPlayer(), Key.FULL_STR);
						}
					} else if (event.getRawSlot() == 1) {
						if (player.skillDexterity(event.getCursor().getAmount())) {
							event.setCursor(null);
						} else {
							Msg.sendMessage(player.getPlayer(), Key.FULL_DEX);
						}
					} else if (event.getRawSlot() == 2) {
						if (player.skillIntelligence(event.getCursor().getAmount())) {
							event.setCursor(null);
						} else {
							Msg.sendMessage(player.getPlayer(), Key.FULL_INT);
						}
					} else if (event.getRawSlot() == 3) {
						if (player.skillVitality(event.getCursor().getAmount())) {
							event.setCursor(null);
						} else {
							Msg.sendMessage(player.getPlayer(), Key.FULL_VIT);
						}
					}
				}
				Manager.scheduleTask(new Runnable() {
					public void run() {
						player.getPlayer().closeInventory();
						player.getPlayer().openInventory(event.getView());
					}
				});
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotion(final PlayerInteractEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		RolecraftPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (player == null) {
			return;
		}
		if (player.getHealth() == player.getMaxHealth()) {
			return;
		}
		String apple1 = new Apple_1(1).getItemMeta().getDisplayName();
		String apple2 = new Apple_2(1).getItemMeta().getDisplayName();
		String hp1 = HealthPotion.getHP1(1).getItemMeta().getDisplayName();
		String hp2 = HealthPotion.getHP2(1).getItemMeta().getDisplayName();
		String hp3 = HealthPotion.getHP3(1).getItemMeta().getDisplayName();
		if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
			ItemStack stack = event.getItem();
			if ((stack.hasItemMeta()) && (stack.getItemMeta().hasDisplayName())) {
				String name = stack.getItemMeta().getDisplayName();
				boolean isApple = true;// strange name of var
				if (name.equals(hp1)) {
					player.addHealth(2D, true);
				} else if (name.equals(hp2)) {
					player.addHealth(4D, true);
				} else if (name.equals(hp3)) {
					player.addHealth(8D, true);
				} else if (name.equals(apple1)) {
					player.addHealth(0.5D * player.getMaxHealth(), true);
				} else if (name.equals(apple2)) {
					player.addHealth(player.getMaxHealth(), true);
				} else {
					isApple = false;
				}
				if (isApple) {
					int amount = stack.getAmount() - 1;
					if (amount > 0) {
						event.getPlayer().getInventory().getItemInMainHand().setAmount(amount);
					} else {
						Manager.scheduleTask(new Runnable() {
							public void run() {
								event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								event.getPlayer().updateInventory();
							}
						});
					}
					ParticleEffect.HEART.display(0.05F, 0.2F, 0.05F, 0.2F, 10, event.getPlayer().getEyeLocation(), 20);

				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestOpen(InventoryOpenEvent event) {
		if (event.getInventory().getHolder() instanceof Chest) {
			if (!ChestHelper.isAvailable(event.getPlayer().getUniqueId(),
					new Vector(((Chest) event.getInventory().getHolder()).getLocation())))
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEggThrow(final PlayerEggThrowEvent event) {
		if (petMap.containsKey(event.getPlayer().getUniqueId())) {
			final String[] a = petMap.get(event.getPlayer().getUniqueId());
			final Location loc = event.getEgg().getLocation().getBlock().getLocation();
			Manager.scheduleTask(new Runnable() {

				@Override
				public void run() {
					PlayerManager.getPlayer(event.getPlayer().getUniqueId()).spawnPet(loc, a[0], a[1]);
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEggThrow(final PlayerInteractEvent event) {
		RolecraftPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		ItemStack egg = event.getItem();
		if (egg == null)
			return;
		if (!egg.getType().equals(Material.EGG))
			return;
		if (egg.getItemMeta() == null)
			return;
		String eggName = egg.getItemMeta().getDisplayName();
		if (eggName == null)
			return;
		String[] s = eggName.split("\\(");
		if (s.length < 2)
			return;
		String name = Utils.cleanString(s[0]);
		String mob = Utils.cleanString(s[1].substring(0, s[1].length() - 1));
		String[] a = { mob, name };
		petMap.put(player.getPlayer().getUniqueId(), a);
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				petMap.remove(event.getPlayer().getUniqueId());
			}
		}, 20L);

	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPetCall(PlayerInteractEvent event) {
		RolecraftPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (!player.getPlayer().isSneaking())
			return;
		ItemStack hand = player.getPlayer().getInventory().getItemInMainHand();
		if (hand == null || !hand.getType().equals(Mat.LEASH.e()))
			return;
		for (Pet pet : player.getPets()) {
			Entity entity = EntityUtils.getEntity(pet.getUniqueId(), event.getPlayer().getWorld());
			LivingEntity le = (LivingEntity) entity;
			if (le == null)
				continue;
			if (le.isLeashed())
				if (le.getLeashHolder().getUniqueId().equals(player.getPlayer().getUniqueId()))
					continue;
			Location v = le.getLocation().subtract(player.getPlayer().getLocation());
			Location loc = player.getPlayer().getLocation().add(v.toVector().normalize().multiply(3));
			le.teleport(loc);
			le.setLeashHolder(player.getPlayer());
			if (hand.getAmount() == 1)
				player.getPlayer().getInventory().setItemInMainHand(null);
			else
				hand.setAmount(hand.getAmount() - 1);
			break;
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getPlayer().getWorld()))
			return;
		RolecraftPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (player == null)
			return;
		String cf = RolecraftConfig.getChatFormat();
		if (cf == null || !cf.contains("%PLAYER%") || !cf.contains("%MESSAGE%"))
			return;
		cf = cf.replace("%PLAYER%", player.getPlayer().getName());
		cf = cf.replace("%MESSAGE%", event.getMessage());
		String clazz = player.getType();
		String group = GroupManager.getGroup(player);
		String level = String.valueOf(player.getLevel());
		if (clazz == null)
			clazz = "";
		if (group == null)
			group = "";
		if (level == null)
			level = "";
		String chatColor = ClazzLoader.getClassColor(clazz);
		chatColor = chatColor == null ? "" : chatColor;
		String format = cf.replace("%CLASS%", chatColor + clazz).replace("%GROUP%", group).replace("%LEVEL%", level);
		event.setFormat(ChatColor.translateAlternateColorCodes('&', format));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTeleport(final PlayerTeleportEvent event) {
		PlayerList.restoreList(event.getPlayer());
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				PlayerList.refreshList(event.getPlayer());
			}
		}, 20L);

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSkillstartCraft(final InventoryClickEvent event) {
		if (event.isCancelled())
			return;
		Class<?>[] a = new Class<?>[0];
		Inventory inv = null;
		// TODO Version Controll
		if (Utils.isBukkitMethod("org.bukkit.event.inventory.InventoryClickEvent", "getClickedInventory", a)) {
			inv = event.getClickedInventory();
		} else {
			inv = event.getInventory();
		}

		if (inv == null)
			return;
		if (inv.getType().equals(InventoryType.CRAFTING) || inv.getType().equals(InventoryType.WORKBENCH)) {
			ItemStack stack = event.getCursor();
			if (stack != null && Skillstar.isSkillstar(stack)) {
				event.setCancelled(true);
			}
		}
	}
}
