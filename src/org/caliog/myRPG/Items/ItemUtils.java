package org.caliog.myRPG.Items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Items.Custom.Apple_1;
import org.caliog.myRPG.Items.Custom.Apple_2;
import org.caliog.myRPG.Items.Custom.HealthPotion;
import org.caliog.myRPG.Items.Custom.Skillstar;
import org.caliog.myRPG.Messages.Msg;

public class ItemUtils {
	public static CustomItem getCustomItem(String name, int a, boolean t) {
		if (name.equals("Skillstar")) {
			return new Skillstar(a);
		}
		if ((name.equals("Apple_1")) || (name.equals("Apple I"))) {
			return new Apple_1(a);
		}
		if ((name.equals("Apple_2")) || (name.equals("Apple II"))) {
			return new Apple_2(a);
		}
		CustomItem i = CustomItemInstance.getInstance(name, a, t);
		if (i == null) {
			i = Armor.getInstance(name, a, t);
		}
		if (i == null) {
			i = Weapon.getInstance(name, a, 0, t);
		}
		return i;
	}

	public static boolean checkForUse(Player p, ItemStack stack) {
		myClass player = PlayerManager.getPlayer(p.getUniqueId());
		if (player == null) {
			return true;
		}
		if (stack == null) {
			return true;
		}
		CustomItem item = null;
		if (Weapon.isWeapon(player, stack)) {
			item = Weapon.getInstance(player, stack);
		} else if (Armor.isArmor(stack)) {
			item = Armor.getInstance(stack);
		}
		if (item == null) {
			return true;
		}
		if ((item.hasClass()) && (!item.getClazz().equals(player.getType()))) {
			if (Math.random() < 0.5D) {
				Msg.sendMessage(p, "need-class1", Msg.CLASS, item.getClazz());
			} else {
				Msg.sendMessage(p, "need-class2", Msg.CLASS, item.getClazz());
			}
			return false;
		}
		if ((item.hasMinLevel()) && (player.getLevel() < item.getMinLevel())) {
			if (Math.random() < 0.5D) {
				Msg.sendMessage(p, "need-exp1");
			} else {
				Msg.sendMessage(p, "need-exp2");
			}
			return false;
		}
		return true;
	}

	public static ItemStack setDisplayName(ItemStack stack, String name) {
		if (!stack.hasItemMeta()) {
			return stack;
		}
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getItem(String string) {
		if (string == null) {
			return null;
		}
		int al = 1;
		boolean soulbound = false;
		String name;
		if (string.contains(":")) {
			name = string.split(":")[0];
			al = Integer.parseInt(string.split(":")[1]);
			if (string.split(":").length > 2) {
				soulbound = Boolean.getBoolean(string.split(":")[2]);
			}
		} else {
			name = string;
		}
		if (Material.matchMaterial(name) != null) {
			return new ItemStack(Material.matchMaterial(name), al);
		}

		if ((name.equals("HealthPotion_1")) || (name.equals("Health Potion I"))) {
			return HealthPotion.getHP1(al);
		}
		if ((name.equals("HealthPotion_2")) || (name.equals("Health Potion II"))) {
			return HealthPotion.getHP2(al);
		}
		if ((name.equals("HealthPotion_3")) || (name.equals("Health Potion III"))) {
			return HealthPotion.getHP3(al);
		}

		return getCustomItem(name, al, !soulbound);
	}

}