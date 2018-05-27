package org.caliog.Rolecraft.Items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.Custom.Apple_1;
import org.caliog.Rolecraft.Items.Custom.Apple_2;
import org.caliog.Rolecraft.Items.Custom.HealthPotion;
import org.caliog.Rolecraft.Items.Custom.Skillstar;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Messages.MsgKey;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class ItemUtils {

	public static CustomItem getCustomItem(String name, int a, short d, boolean t) {
		if (name.equalsIgnoreCase("Skillstar")) {
			return new Skillstar(a);
		}
		if ((name.equalsIgnoreCase("Apple_1")) || (name.equalsIgnoreCase("Apple I"))) {
			return new Apple_1(a);
		}
		if ((name.equalsIgnoreCase("Apple_2")) || (name.equalsIgnoreCase("Apple II"))) {
			return new Apple_2(a);
		}
		CustomItem i = CustomItemInstance.getInstance(name, a, t);
		if (i == null) {
			i = Armor.getInstance(name, a, d, t);
		}
		if (i == null) {
			i = Weapon.getInstance(name, a, 0, d, t);
		}
		return i;
	}

	public static boolean checkForUse(Player p, ItemStack stack) {
		RolecraftPlayer player = PlayerManager.getPlayer(p.getUniqueId());
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
				Msg.sendMessage(p, MsgKey.NEED_CLASS1, Msg.CLASS, item.getClazz());
			} else {
				Msg.sendMessage(p, MsgKey.NEED_CLASS2, Msg.CLASS, item.getClazz());
			}
			return false;
		}
		if ((item.hasMinLevel()) && (player.getLevel() < item.getMinLevel())) {
			if (Math.random() < 0.5D) {
				Msg.sendMessage(p, MsgKey.NEED_EXP1);
			} else {
				Msg.sendMessage(p, MsgKey.NEED_EXP2);
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
		short d = 0;
		boolean soulbound = false;
		String name;
		if (string.contains(":")) {
			String[] split = string.split(":");
			name = split[0];
			al = Integer.parseInt(split[1]);
			if (split.length > 2) {
				soulbound = Boolean.getBoolean(split[2]);
			}
			if (split.length > 3 && Utils.isInteger(split[3])) {
				d = Short.parseShort(split[3]);
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

		return getCustomItem(name, al, d, !soulbound);
	}

}