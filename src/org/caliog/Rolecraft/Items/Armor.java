package org.caliog.Rolecraft.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Messages.Translator.Phrase;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class Armor extends CustomItemInstance {

	private int level;
	private final short durability;

	public Armor(Material type, String name, int level, short durability, boolean tradeable, YamlConfiguration config) {
		super(type, name, tradeable, config);
		this.level = level;
		this.durability = durability;
		syncItemStack();
	}

	public int getDefense() {
		return this.config.getInt("defense");
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
		lore.add(ChatColor.ITALIC + "" + ChatColor.BLUE + "Def: " + getDefense());
		if (!getEffects().isEmpty()) {
			lore.add(" ");
		}
		for (ItemEffect effect : getEffects()) {
			if (effect.getPower() > 0)
				lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + effect.getString());
		}
		if (getLore() != null) {
			lore.add(ChatColor.LIGHT_PURPLE + ChatColor.translateAlternateColorCodes('&', getLore()));
		}
		if ((hasClass()) || (hasMinLevel()) || (!isTradeable())) {
			lore.add(" ");
		}
		if (hasMinLevel()) {
			lore.add(ChatColor.RED + "MinLv: " + getMinLevel());
		}
		if (hasClass()) {
			lore.add(ChatColor.RED + "Class: " + this.getClazz());
		}
		if (!isTradeable()) {
			lore.add(ChatColor.RED + Phrase.SOULBOUND.translate() + "!");
		}
		meta.setLore(lore);
		setItemMeta(meta);
	}

	public static Armor getInstance(ItemStack item) {
		if ((item == null) || (!item.hasItemMeta())) {
			return null;
		}
		String name = null;
		String level = null;
		boolean soulbound = false;

		String dn = item.getItemMeta().getDisplayName();
		if ((dn == null) || (!dn.contains("" + ChatColor.GOLD)) || (!dn.contains("" + ChatColor.DARK_GRAY))) {
			return null;
		}
		name = dn.substring(dn.indexOf("" + ChatColor.DARK_GRAY) + 2, dn.indexOf("" + ChatColor.GOLD));

		name = Utils.cleanString(name);

		level = dn.substring(dn.indexOf(" Lv. ") + 5);
		for (String l : item.getItemMeta().getLore()) {
			if (l.contains("soulbound!") || l.contains(Phrase.SOULBOUND.translate() + "!")) {
				soulbound = true;
				break;
			}
		}
		if ((name == null) || (level == null)) {
			return null;
		}
		Armor ci = getInstance(name, Integer.parseInt(level), item.getDurability(), !soulbound);
		return ci;
	}

	public static Armor getInstance(String name, int level, short durability, boolean tradeable) {
		File f = new File(FilePath.armor + name + ".yml");
		if (!f.exists()) {
			return null;
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

		Material mat = Material.matchMaterial(config.getString("material", "none"));
		if (mat == null) {
			return null;
		}
		Armor instance = new Armor(mat, name, level, durability, tradeable, config);

		return instance;
	}

	public static boolean isArmor(ItemStack item) {
		return getInstance(item) != null;
	}

	public short getDurability() {
		return durability;
	}
}
