package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.NPC.Trader;
import org.caliog.Rolecraft.Villagers.TraderMenu.TraderEditMenu;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;

public class Commandtrader extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: trader SubName: create
		 * 
		 * Permission: rc.trader.create
		 * 
		 * Usage: /trader create <name..>
		 */
		cmds.add(new Command("trader", "rc.trader.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				VManager.spawnTrader(player.getLocation(), args[1], null);
				player.sendMessage(ChatColor.GOLD + "Spawned the trader next to you!");
			}
		}, new CommandField("create", FieldProperty.IDENTIFIER), new CommandField("name", FieldProperty.REQUIRED)));

		/*
		 * Name: trader SubName: add
		 * 
		 * Permission: rc.trader.recipe
		 * 
		 * Usage: /trader add [price]
		 */
		cmds.add(new Command("trader", "rc.trader.recipe", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Trader trader = VManager.getClosestTrader(player.getLocation());
				if (trader == null) {
					player.sendMessage(ChatColor.RED + "There is no trader around you!");
					return;
				}
				if (args.length == 2) {
					if (player.getInventory().getItemInMainHand() == null
							|| player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
						player.sendMessage(ChatColor.RED + "You have to hold the item in your hand!");
						return;
					}
					int price = Integer.parseInt(args[1]);
					trader.addRecipe(player.getInventory().getItemInMainHand(), price);
					player.sendMessage(ChatColor.GOLD + "The trader sells: "
							+ player.getInventory().getItemInMainHand().getType().name().toLowerCase().replace("_", " ") + "!");
				} else {
					ItemStack[] items = { player.getInventory().getItem(0), player.getInventory().getItem(1),
							player.getInventory().getItem(2) };
					if (items[2] == null) {
						player.sendMessage(ChatColor.RED + "You have to put the item you want to sell in the third slot!");
						return;
					} else if (items[0] == null) {
						player.sendMessage(
								ChatColor.RED + "You have to put something you want to have in exchange for your sell in the first slot!");
						return;
					}
					trader.addRecipe(items[0], items[1], items[2]);
					player.sendMessage(
							ChatColor.GOLD + "The trader sells: " + items[2].getType().name().toLowerCase().replace("_", " ") + "!");
				}

			}
		}, new CommandField("add", FieldProperty.IDENTIFIER), new CommandField("price", "not-negative integer", FieldProperty.OPTIONAL)));

		/*
		 * Name: trader SubName: del
		 * 
		 * Permission: rc.trader.recipe
		 * 
		 * Usage: /trader del
		 */
		cmds.add(new Command("trader", "rc.trader.recipe", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Trader trader = VManager.getClosestTrader(player.getLocation());
				if (trader == null) {
					player.sendMessage(ChatColor.RED + "There is no trader around you!");
					return;
				}
				if (player.getInventory().getItemInMainHand() == null
						|| player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
					player.sendMessage(ChatColor.RED + "You have to hold the item in your hand!");
					return;
				}
				if (trader.delRecipe(player.getInventory().getItemInMainHand()))
					player.sendMessage(ChatColor.GOLD + "Deleted this recipe!");
				else
					player.sendMessage(ChatColor.GOLD
							+ "Sorry, I could not find this recipe. Take the item you do not want to sell anymore in your hand (amount is important)!");

			}
		}, new CommandField("del", FieldProperty.IDENTIFIER)));

		/*
		 * Name: trader SubName: edit
		 * 
		 * Permission: rc.trader.edit
		 * 
		 * Usage: /trader edit
		 */
		cmds.add(new Command("trader", "rc.trader.edit", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				Trader trader = VManager.getClosestTrader(player.getLocation());
				if (trader == null) {
					player.sendMessage(ChatColor.RED + "There is no trader around you!");
					return;
				}
				MenuManager.openMenu(player, new TraderEditMenu(trader));

			}
		}, new CommandField("edit", FieldProperty.IDENTIFIER)));

		return cmds;
	}
}
