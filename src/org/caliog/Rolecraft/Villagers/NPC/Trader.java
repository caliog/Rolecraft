package org.caliog.Rolecraft.Villagers.NPC;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.Traders.TraderMenu;
import org.caliog.Rolecraft.Villagers.Utils.Recipe;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;

public class Trader extends Villager {

	private Recipe recipe = new Recipe();
	private TraderMenu traderMenu;
	private boolean openMenu = false;

	public Trader(org.bukkit.entity.Villager v, Location location, String name) {
		super(v, VillagerType.TRADER, location, name);
		this.setInteractionRadius(4F);
		traderMenu = new TraderMenu(this);
	}

	public boolean openInventory(Player player) {
		if (traderMenu.isNonEmpty()) {
			if (!openMenu) {
				MenuManager.openMenu(player, traderMenu);
				openMenu = true;
				Manager.scheduleTask(new Runnable() {

					@Override
					public void run() {
						openMenu = false;
					}
				}, 5L);
				return true;
			}
		}
		return NMSMethods.openInventory(this, player);
	}

	public void addRecipe(ItemStack s1, ItemStack s2, ItemStack s3) {
		this.recipe.add(s1, s2, s3);
	}

	public void addRecipe(ItemStack s, int i) {
		this.recipe.add(s, i);
	}

	public void setRecipe(Recipe recipe) {
		if (recipe == null)
			return;
		this.recipe = recipe;

	}

	@Override
	public FileWriter save(FileWriter writer) throws IOException {
		super.save(writer);
		writer.append(recipe.asString() + "&");
		writer.append(tradeMenuToString() + "\r");
		return writer;
	}

	public boolean delRecipe(ItemStack itemInHand) {
		return recipe.del(itemInHand);

	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void loadTradeMenu(String s) {
		this.traderMenu = new TraderMenu(this);
		traderMenu.loadFromString(s);
	}

	private String tradeMenuToString() {
		return traderMenu.toString();
	}

	public void editMenu(Inventory inv, HashMap<Integer, Integer> costs) {
		traderMenu.editMenu(inv, costs);
	}

	public TraderMenu getTraderMenu() {
		return traderMenu;
	}

}
