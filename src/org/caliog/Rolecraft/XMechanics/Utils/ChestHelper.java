package org.caliog.Rolecraft.XMechanics.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;

public class ChestHelper {

	private static HashMap<String, UUID> chests = new HashMap<String, UUID>();
	private static HashMap<String, Integer[]> tasks = new HashMap<String, Integer[]>();

	public static boolean dropItem(Player player, Location loc, List<ItemStack> stacks) {
		// search chest
		for (final String s : tasks.keySet()) {
			if (Vector.fromString(s).distanceSquared(loc) <= 36) {
				placeInChest(player, s, stacks);
				return true;
			}
		}
		boolean empty = true;
		for (ItemStack stack : stacks)
			if (stack != null)
				empty = false;
		if (empty)
			return false;
		for (int h = 0; h < 7; h++) {
			if (loc.getBlock().getType().equals(Material.AIR)) {
				loc.getBlock().setType(Material.CHEST);
				placeInChest(player, new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()).toString(),
						stacks);
				return true;
			}
			loc.add(0, 1, 0);
		}
		return false;
	}

	private static void placeInChest(Player player, final String s, List<ItemStack> stacks) {
		Chest chest = (Chest) Vector.fromString(s).toLocation().getBlock().getState();
		chest.getInventory().addItem(stacks.toArray(new ItemStack[0]));
		chests.put(s, player.getUniqueId());
		Integer[] a = tasks.get(s);
		if (a != null) {
			Manager.cancelTask(a[0]);
			Manager.cancelTask(a[1]);
		}
		int t1 = Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				Block block = Vector.fromString(s).toLocation().getBlock();
				if (block.getState() instanceof Chest) {
					((Chest) block.getState()).getInventory().setContents(new ItemStack[0]);
				}
				block.setType(Material.AIR);
				chests.remove(s);
				tasks.remove(s);
			}
		}, RolecraftConfig.getRemoveItemTime() * 20L);
		int t2 = Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				chests.remove(s);
			}
		}, 200L);
		Integer[] t = { t1, t2 };
		tasks.put(s, t);
	}

	public static boolean isAvailable(UUID id, Vector v) {
		if (chests.containsKey(v.toString()))
			return chests.get(v.toString()).equals(id);
		return true;
	}

	public static void loot(Chest chest) {
		boolean b = true;
		Vector v = new Vector(chest.getBlock().getLocation());
		Integer[] a = tasks.get(v.toString());
		if (a == null)
			return;
		for (ItemStack stack : chest.getInventory().getContents())
			if (stack != null)
				b = false;

		if (b) {

			Manager.cancelTask(a[0]);
			Manager.cancelTask(a[1]);
			chests.remove(v.toString());
			tasks.remove(v.toString());
			chest.getBlock().setType(Material.AIR);
		}

	}

	public static void cleanUp() {
		for (String s : chests.keySet()) {
			if (s != null) {
				Vector v = Vector.fromString(s);
				if (v != null) {
					Block block = v.toLocation().getBlock();
					if (block.getState() instanceof Chest) {
						((Chest) block.getState()).getInventory().setContents(new ItemStack[0]);
					}
					block.setType(Material.AIR);
				}
			}

		}
		chests.clear();
		tasks.clear();
	}

}
