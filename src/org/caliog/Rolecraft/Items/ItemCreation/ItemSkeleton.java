package org.caliog.Rolecraft.Items.ItemCreation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Items.Armor;
import org.caliog.Rolecraft.Items.CustomItemInstance;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.Items.ItemEffect.ItemEffectType;
import org.caliog.Rolecraft.Items.Weapon;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.VersionControll.Mat;

public class ItemSkeleton {

	private Material material;
	private String name;
	private boolean tradeable;
	private List<ItemEffect> effects;
	private int minlvl;
	private String clazz;
	private String lore;
	private int d;

	// off
	private ItemStack stack;

	public ItemSkeleton() {
		name = "";
		tradeable = true;
		effects = new ArrayList<ItemEffect>();
		minlvl = 0;
		clazz = "none";
		lore = null;
		d = 0;
	}

	public ItemSkeleton(Material mat) {
		this();
		material = mat;
	}

	public ItemSkeleton(CustomItemInstance instance) {
		stack = instance;
		material = instance.getType();
		name = instance.getName();
		tradeable = instance.isTradeable();
		effects = instance.getEffects();
		minlvl = instance.getMinLevel();
		clazz = instance.getClazz();
		lore = instance.getLore();
		if (instance instanceof Weapon) {
			int[] dd = ((Weapon) instance).getDamage();
			int s = 0;
			for (int x : dd) {
				s += x;
			}
			d = s / dd.length;
		} else if (instance instanceof Armor) {
			d = ((Armor) instance).getDefense();
		}
	}

	//read
	public void readFromFile(YamlConfiguration config, boolean isWeapon) {
		material = Mat.matchMaterial(config.getString("material", "none"));
		d = config.getInt(isWeapon ? "damage" : "defense", 0);
		minlvl = config.getInt("min-level", 0);
		clazz = config.getString("class-type");
		lore = config.getString("lore");
		effects = new ArrayList<ItemEffect>();
		for (ItemEffectType t : ItemEffect.ItemEffectType.values()) {
			if (config.isSet("item-effects." + t.name())) {
				int e = config.getInt("item-effects." + t.name());
				if (e > 0) {
					effects.add(new ItemEffect(t, e));
				}
			}
		}

	}

	// save
	public void saveToFile() {
		if (name == null || name.equals("") || material == null)
			return;
		final String path = (isWeapon() ? FilePath.weapons : FilePath.armor) + name + ".yml";
		File f = new File(path);
		if (f.exists())
			f.delete();
		YamlConfiguration config = saveToConfig();
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private YamlConfiguration saveToConfig() {
		YamlConfiguration config = new YamlConfiguration();
		// material
		config.set("material", material.name());
		// damage / defense
		config.set(isWeapon() ? "damage" : "defense", d);
		// min-level
		config.set("min-level", minlvl);
		// class
		config.set("class-type", clazz);
		// lore
		config.set("lore", lore);
		// effects
		if (!effects.isEmpty()) {
			ConfigurationSection section = config.createSection("item-effects");
			for (ItemEffect effect : effects)
				section.set(effect.getType().name(), effect.getPower());
		}
		return config;
	}

	// static
	public static ItemSkeleton loadByName(String name) {
		ItemSkeleton skel = new ItemSkeleton();
		skel.setName(name);
		File file = new File(FilePath.armor + name + ".yml");
		if (file.exists()) {
			skel.readFromFile(YamlConfiguration.loadConfiguration(file), false);
			return skel;
		}
		file = new File(FilePath.weapons + name + ".yml");
		if (file.exists()) {
			skel.readFromFile(YamlConfiguration.loadConfiguration(file), true);
			return skel;
		}
		return null;
	}

	//getset
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTradeable() {
		return tradeable;
	}

	public void setTradeable(boolean tradeable) {
		this.tradeable = tradeable;
	}

	public List<ItemEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<ItemEffect> effects) {
		this.effects = effects;
	}

	public int getMinlvl() {
		return minlvl;
	}

	public void setMinlvl(int minlvl) {
		this.minlvl = minlvl;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getLore() {
		return lore;
	}

	public void setLore(String lore) {
		this.lore = lore;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getEffectPower(ItemEffectType type) {
		if (effects == null)
			return 0;
		else
			for (ItemEffect e : effects)
				if (e.getType().equals(type))
					return e.getPower();
		return 0;
	}

	public void setEffectPower(int p, ItemEffectType type) {
		for (ItemEffect effect : effects)
			if (effect.getType().equals(type)) {
				effects.remove(effect);
				break;
			}
		effects.add(new ItemEffect(type, p));
	}

	public boolean isWeapon() {
		if (material == null)
			return true;
		if (material.name().contains("CHESTPLATE") || material.name().contains("BOOTS")
				|| material.name().contains("LEGGINGS") || material.name().contains("HELMET"))
			return false;
		return true;
	}

	public ItemStack getStack() {
		if (material != null && name != null && !name.equals("")) {
			ItemStack s = isWeapon() ? new Weapon(material, name, 0, 0, (short) 0, tradeable, saveToConfig())
					: new Armor(material, name, 0, (short) 0, tradeable, saveToConfig());
			if (s != null)
				return s;
		}
		if (stack == null && material != null)
			return new ItemStack(material);

		return stack;
	}

}
