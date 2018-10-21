package org.caliog.Rolecraft.XMechanics.Menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.XMechanics.Utils.Reflect;
import org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Mat;

public class MenuItem {

	private final Material material;
	private final short data;
	private final int amount;
	private final List<String> lore;
	private String name;
	private final HashMap<Enchantment, Integer> enchantments;
	private ItemStack stack;
	private ButtonClickHandler bch = null;
	private final boolean editable;
	private int costs;

	public MenuItem() {
		this(null, Material.AIR, true);
	}

	public MenuItem(Material mat) {
		this(null, mat);
	}

	public MenuItem(String name, Material mat) {
		this(name, mat, false);
	}

	public MenuItem(String name, Material mat, boolean editable) {
		this(name, mat, (short) 0, 1, editable);
	}

	public MenuItem(String name, Material mat, short data, int amount) {
		this(name, mat, data, amount, false);
	}

	public MenuItem(String name, Material mat, short data, int amount, boolean editable) {
		this(name, mat, data, amount, new ArrayList<String>(), null, null, editable, 0);
	}

	public MenuItem(String name, Material mat, List<String> lore) {
		this(name, mat, lore, false);
	}

	public MenuItem(String name, Material mat, List<String> lore, boolean editable) {
		this(name, mat, (short) 0, lore, editable);
	}

	public MenuItem(String name, Material mat, short data, List<String> lore) {
		this(name, mat, data, lore, false);
	}

	public MenuItem(String name, Material mat, short data, List<String> lore, boolean editable) {
		this(name, mat, data, 1, lore, null, null, editable, 0);
	}

	public MenuItem(ItemStack stack, boolean editable) {
		this(null, stack.getType(), (short) 0, 1, new ArrayList<String>(), null, stack, editable, 0);
	}

	public MenuItem(ItemStack stack, int costs) {
		this(stack, costs, false);
	}

	public MenuItem(ItemStack stack, int costs, boolean editable) {
		this(null, Material.AIR, (short) 0, 0, new ArrayList<String>(), null, stack, editable, costs);
	}

	public MenuItem(String name, Material mat, short data, int amount, List<String> lore,
			HashMap<Enchantment, Integer> enc, ItemStack stack, boolean editable, int costs) {
		Validate.notNull(mat, "Material cannot be null!");
		this.material = mat;
		this.data = data;
		this.amount = amount;
		this.lore = lore;
		this.name = name;
		this.enchantments = enc;
		this.stack = stack;
		this.editable = editable;
		this.costs = costs;
	}

	public ItemStack createItemStack() {
		ItemStack stack = new ItemStack(material, amount, data);
		if (this.stack != null)
			stack = this.stack.clone();

		if (stack.getType().equals(Material.AIR))
			return stack;
		ItemMeta meta = null;
		if (stack.hasItemMeta())
			meta = stack.getItemMeta();
		else
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		if (name != null)
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		if (lore != null)
			meta.setLore(lore);
		if (enchantments != null && !enchantments.isEmpty())
			for (Enchantment e : enchantments.keySet())
				meta.addEnchant(e, enchantments.get(e), true);
		if (!editable)
			if (Reflect.isBukkitClass("org.bukkit.inventory.ItemFlag")) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
			}
		stack.setItemMeta(meta);
		return stack;
	}

	public void giveItem(Player player) {
		ItemStack stack = new ItemStack(material, amount, data);
		ItemMeta meta = null;
		if (this.stack != null) {
			stack = this.stack;
			if (stack.hasItemMeta())
				meta = stack.getItemMeta();
		}
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(material);

		if (enchantments != null && !enchantments.isEmpty())
			for (Enchantment e : enchantments.keySet())
				meta.addEnchant(e, enchantments.get(e), true);

		stack.setItemMeta(meta);
		player.getInventory().addItem(stack);
	}

	public Material getMaterial() {
		return material;
	}

	public short getData() {
		return data;
	}

	public int getAmount() {
		return amount;
	}

	public List<String> getLore() {
		return lore;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getCosts() {
		return costs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCosts(Integer i) {
		this.costs = i;
	}

	public List<String> getEnchantments() {
		List<String> list = new ArrayList<String>();
		if (enchantments == null || enchantments.isEmpty())
			return list;
		for (Enchantment enc : enchantments.keySet()) {
			list.add(enc.getName() + "#" + enchantments.get(enc));
		}
		return list;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setButtonClickHandler(ButtonClickHandler bch) {
		this.bch = bch;
	}

	public ButtonClickHandler getClickHandler() {
		return bch;
	}

	public void onClick(InventoryClickEvent event) {
		if (bch != null && event.getWhoClicked() instanceof Player)
			bch.onClick(event, (Player) event.getWhoClicked());
	}

	public abstract class ButtonClickHandler {

		private Menu menu;

		public ButtonClickHandler(Menu menu) {
			this.menu = menu;
		}

		public Menu getMenu() {
			return menu;
		}

		public abstract void onClick(InventoryClickEvent event, Player player);
	}

	public class ExitButton extends MenuItem {

		public ExitButton(Menu menu, String title) {
			super(title, Mat.GREEN_STAINED_GLASS_PANE.remove_first(), (short) 13, 1);
			this.setButtonClickHandler(this.new ButtonClickHandler(menu) {

				@Override
				public void onClick(InventoryClickEvent event, Player player) {
					MenuManager.exitMenu(player);
				}
			});
		}

	}

}
