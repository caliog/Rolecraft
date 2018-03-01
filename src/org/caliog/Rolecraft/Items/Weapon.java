package org.caliog.Rolecraft.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Messages.Translator.Phrase;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class Weapon extends CustomItemInstance {

	private int level;
	private int kills;
	private final short durability;

	public Weapon(Material type, String name, int level, int kills, short durabilty, boolean tradeable, YamlConfiguration config) {
		super(type, name, tradeable, config);
		this.level = level;
		this.kills = kills;
		this.durability = durabilty;
		syncItemStack();
	}

	public int[] getDamage() {
		String[] s = this.config.getString("damage").split(",");
		int[] a = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			a[i] = (Integer.parseInt(s[i]) + getLevel() - 1);
		}
		return a;
	}

	public int getLevel() {
		return this.level;
	}

	public void syncItemStack() {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + getName() + ChatColor.GOLD + " Lv. " + getLevel());
		if (Utils.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (RolecraftConfig.disableDurability())
			meta.setUnbreakable(true);
		List<String> lore = new ArrayList<String>();
		String damage = getDamage().length == 1 ? String.valueOf(getDamage()[0])
				: (getDamage()[0] + "-" + getDamage()[(getDamage().length - 1)]);
		lore.add(ChatColor.ITALIC + "" + ChatColor.BLUE + "Dmg: " + damage);
		for (ItemEffect effect : getEffects()) {
			if (effect.getPower() > 0)
				lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + effect.getString());
		}
		if (getLore() != null) {
			lore.add(ChatColor.LIGHT_PURPLE + ChatColor.translateAlternateColorCodes('&', getLore()));
		}
		lore.add(ChatColor.GRAY + "Kills: " + kills);
		if ((hasClass()) || (hasMinLevel()) || (!isTradeable())) {
			lore.add(" ");
		}
		if (hasMinLevel()) {
			lore.add(ChatColor.RED + "MinLv: " + getMinLevel());
		}
		if (hasClass()) {
			lore.add(ChatColor.RED + Phrase.CLASS.translate() + getClazz());
		}
		if (!isTradeable())
			lore.add(ChatColor.RED + Phrase.SOULBOUND.translate() + "!");
		meta.setLore(lore);
		setItemMeta(meta);
	}

	public int getRandomDamage() {
		int[] a = getDamage();
		int e = (int) (Math.random() * a.length);
		return a[e];
	}

	public static Weapon getInstance(RolecraftPlayer clazz, ItemStack item) {
		if (!isWeapon(clazz, item)) {
			return null;
		}
		String name = null;
		String level = null;
		int kills = 0;
		boolean soulbound = false;

		String dn = item.getItemMeta().getDisplayName();
		if ((dn == null) || (!dn.contains("" + ChatColor.GOLD))) {
			return null;
		}
		name = dn.substring(dn.indexOf("" + ChatColor.DARK_GRAY) + 2, dn.indexOf("" + ChatColor.GOLD));

		name = Utils.cleanString(name);

		level = dn.substring(dn.indexOf(" Lv. ") + 5);

		for (String l : item.getItemMeta().getLore()) {
			if (l.contains("soulbound!") || l.contains(Phrase.SOULBOUND.translate() + "!")) {
				soulbound = true;
				break;
			} else if (l.contains("Kills:")) {
				String s = l.replace(ChatColor.GRAY + "", "").replace("Kills:", "").replaceAll(" ", "");
				try {
					kills = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
			}
		}
		if ((name == null) || (level == null)) {
			return null;
		}
		Weapon ci = getInstance(name, Integer.parseInt(level), kills, item.getDurability(), !soulbound);
		return ci;
	}

	public static Weapon getInstance(String name, int level, int kills, short durability, boolean tradeable) {
		File f = new File(FilePath.weapons + name + ".yml");
		if (!f.exists()) {
			return null;
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

		Material mat = Material.matchMaterial(config.getString("material", "none"));
		if (mat == null) {
			return null;
		}
		Weapon instance = new Weapon(mat, name, level, kills, durability, tradeable, config);
		return instance;
	}

	public static boolean isWeapon(RolecraftPlayer clazz, ItemStack item) {
		boolean isWeapon = true;
		if (item == null) {
			return false;
		}
		if (!item.hasItemMeta()) {
			return false;
		}
		if (!item.getItemMeta().hasDisplayName()) {
			return false;
		}
		if (!isCustomItem(item)) {
			isWeapon = false;
		}
		if (isWeapon) {
			for (String l : item.getItemMeta().getLore()) {
				if (l.contains("Dmg: ")) {
					return true;
				}
			}
		}
		return false;
	}

	public void raiseLevel(final Player p) {
		if (this.level < 9) {
			this.level += 1;
			p.getInventory()
					.setItemInMainHand(new Weapon(getType(), getName(), this.level, 0, this.durability, isTradeable(), this.config));
		}
	}

	public int getKills() {
		ItemMeta meta = this.getItemMeta();
		for (String l : meta.getLore()) {
			if (l.contains("Kills:")) {
				String s = l.replace(ChatColor.GRAY + "", "").replace("Kills:", "").replaceAll(" ", "");
				try {
					return Integer.parseInt(s);
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		}
		return 0;
	}

	public void kill(Player player) {
		ItemMeta meta = this.getItemMeta();
		List<String> lore = new ArrayList<String>();
		final int k = getKills() + 1;
		for (String l : meta.getLore()) {
			if (l.contains("Kills:")) {
				lore.add(ChatColor.GRAY + "Kills: " + k);
			} else
				lore.add(l);
		}
		meta.setLore(lore);
		this.setItemMeta(meta);
		player.getInventory().setItemInMainHand(this);
	}

	public static List<String> getWeaponList() {
		File f = new File(FilePath.weapons);
		List<String> list = new ArrayList<String>();
		for (File a : f.listFiles()) {
			if (!a.isDirectory() && a.getName().endsWith(".yml")) {
				YamlConfiguration c = YamlConfiguration.loadConfiguration(a);
				if (c.isSet("material")) {
					list.add(a.getName().replace(".yml", ""));
				}
			}
		}
		return list;
	}
}
