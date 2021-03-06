package org.caliog.Rolecraft.Villagers.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Items.Custom.Money;
import org.caliog.Rolecraft.XMechanics.Utils.IO.DataSaver;

public class Recipe {

	private List<ItemStack[]> recipes = new ArrayList<ItemStack[]>();

	public List<ItemStack[]> getRecipe() {
		return recipes;
	}

	public void add(ItemStack give, int price) {
		add(new Money(price).getStackCopy(), give);
	}

	protected void add(ItemStack get, ItemStack give) {
		add(get, null, give);
	}

	public void add(ItemStack i, ItemStack j, ItemStack k) {
		if (k == null || i == null)
			return;
		ItemStack[] array = { i.clone(), j == null ? null : j.clone(), k.clone() };
		recipes.add(array);
	}

	public boolean del(ItemStack itemInHand) {
		int i = recipes.size();
		List<ItemStack[]> n = new ArrayList<ItemStack[]>();
		for (ItemStack[] array : recipes) {
			if (!array[2].equals(itemInHand)) {
				n.add(array);
			}
		}
		recipes = n;
		return recipes.size() != i;
	}

	public static Recipe load(String s) {
		return null;
	}

	public static Recipe fromString(String s) {
		Recipe recipe = new Recipe();
		if (s.contains("I"))
			for (String e : s.split("#")) {
				String a[] = e.split("I");
				recipe.add(DataSaver.getItem(a[0]), DataSaver.getItem(a[1]), DataSaver.getItem(a[2]));
			}
		return recipe;
	}

	public String asString() {
		String t = "";
		for (ItemStack[] array : recipes) {
			t += DataSaver.save(array[0]) + "I" + DataSaver.save(array[1]) + "I" + DataSaver.save(array[2]) + "#";
		}
		return (t + "..").replace("#..", "").replace("..", "");
	}

	public boolean isEmpty() {
		return recipes.isEmpty();
	}

}
