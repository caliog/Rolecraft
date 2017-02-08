package org.caliog.Rolecraft.Entities.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Groups.GManager;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Utils.ChestHelper;

public class Playerface {
	private static HashMap<UUID, UUID> playerDrops = new HashMap<UUID, UUID>();

	public static int getExp(int level) {
		int exp = 0;
		if (RolecraftConfig.isLevelLinear()) {
			exp = level * 100 - 100;
		} else
			exp = 50 * level * (level - 1);
		return exp;
	}

	public static int getExpDifference(int l1, int l2) {
		return Math.abs(getExp(l1) - getExp(l2));
	}

	public static int killed(Player player, Mob mob) {
		int level = player.getLevel();
		if (level == 0) {
			player.setLevel(1);
			level = 1;
		}
		int mobLevel = mob.getLevel();
		int dif = mobLevel - level;
		if (level - mobLevel > 7) {
			return 0;
		}
		float d = 1.0F;
		if (dif > 0) {
			d = 1.1F;
		} else if (dif < 0) {
			dif = Math.abs(dif);
			if (dif > 5) {
				d = 0.1F;
			} else if (dif > 3) {
				d = 0.4F;
			} else if (dif > 2) {
				d = 0.6F;
			} else if (dif > 1) {
				d = 0.75F;
			} else if (dif == 1) {
				d = 0.9F;
			}
		}
		int exp = mob.getExp();
		if (GManager.isInGroup(player)) {
			GManager.playerEarnedExp(player, exp);
			return exp;
		}
		giveExp(player, (int) (d * exp));
		return (int) (d * exp);
	}

	public static void giveExp(Player player, int exp) {
		int level = player.getLevel();
		float pExp = player.getExp();
		int difference = getExpDifference(level + 1, level);
		float newExp = exp + pExp * difference;
		float percent = newExp / difference;

		int counter = 0;
		while (percent >= 1.0F) {
			percent -= 1.0F;
			counter++;
		}
		if (counter > 0) {
			player.setLevel(player.getLevel() + counter);
		}
		player.setExp(percent);
	}

	public static void giveItem(Player p, List<ItemStack> stacks) {
		if ((stacks == null) || (stacks.isEmpty())) {
			return;
		}
		if (stacks.size() > 14) {
			return;
		}
		for (ItemStack s : stacks) {
			giveItem(p, s);
		}
	}

	public static boolean giveItem(Player p, ItemStack... s) {
		if (s == null) {
			return false;
		}
		for (ItemStack stack : s)
			if (stack == null)
				return false;
		if (p.getInventory().firstEmpty() != -1) {
			p.getInventory().addItem(s);
			p.updateInventory();
			return true;
		} else {
			dropItem(p, p.getLocation(), Arrays.asList(s));
			return true;
		}
	}

	public static void takeItem(Player p, List<ItemStack> stack) {
		if (stack == null) {
			return;
		}
		for (ItemStack s : stack) {
			p.getInventory().removeItem(new ItemStack[] { s });
		}
	}

	public static void takeItem(Player p, ItemStack stack) {
		if (stack == null) {
			return;
		}
		p.getInventory().removeItem(new ItemStack[] { stack });
	}

	public static boolean hasItem(Player p, ItemStack stack) {
		return p.getInventory().containsAtLeast(stack, stack.getAmount());
	}

	public static boolean hasItem(Player p, List<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			if (!hasItem(p, stack)) {
				return false;
			}
		}
		return true;
	}

	public static void dropItem(Player player, Location loc, List<ItemStack> stacks) {
		if ((stacks == null) || (player == null) || (loc == null)) {
			return;
		}
		if (RolecraftConfig.isLootChestEnabled())
			if (ChestHelper.dropItem(player, loc, stacks))
				return;

		for (ItemStack s : stacks)
			dropItem(player, loc, loc.getWorld().dropItemNaturally(loc, s));
	}

	public static void dropItem(Player player, Location location, final Item item) {
		final UUID id = item.getUniqueId();
		long time = RolecraftConfig.getRemoveItemTime();
		time = time <= 0 ? 0 : time;
		playerDrops.put(id, player.getUniqueId());
		Manager.scheduleTask(new Runnable() {
			public void run() {
				Playerface.playerDrops.remove(id);
			}
		}, CustomItem.isItemTradeable(item.getItemStack()) ? 200L : (time * 20));
		if (time != 0)
			Manager.scheduleTask(new Runnable() {
				public void run() {
					if (item.isOnGround()) {
						item.remove();
					}
				}
			}, 20L * time);
	}

	public static boolean isAccessible(Player player, Item item) {
		if (playerDrops.containsKey(Integer.valueOf(item.getEntityId()))) {
			if (((UUID) playerDrops.get(item.getUniqueId())).equals(player.getUniqueId())) {
				return true;
			}
			return false;
		}
		return true;
	}

	public static void clear() {
		playerDrops.clear();
	}

	public static String spell(int[] spell) {

		String r = "";
		for (int i : spell) {
			if (i == -1) {
				break;
			}
			String n = ChatColor.RED + "X";
			if (i == 0) {
				n = ChatColor.BLUE + "O";
			}
			r += n + ChatColor.RESET + " - ";
		}
		r = (r + "..").replace(ChatColor.RESET + " - ..", "");
		return r;
	}
}
