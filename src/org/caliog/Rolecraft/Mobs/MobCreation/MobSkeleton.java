package org.caliog.Rolecraft.Mobs.MobCreation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Items.ItemUtils;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class MobSkeleton {

	private String name;
	private int damage, defense;
	private int level = 1;
	private int hp;
	private boolean aggressive = true, pet = false;
	private int exp;
	private EntityType type;
	private LinkedHashMap<ItemStack, Integer> drops = new LinkedHashMap<ItemStack, Integer>();
	private HashMap<String, Material> eq = new HashMap<String, Material>();

	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int d) {
		this.damage = d;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int d) {
		this.defense = d;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		this.level = l;
	}

	public int getHP() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public void toggleAggressive() {
		this.aggressive = !aggressive;
	}

	public boolean isPet() {
		return pet;
	}

	public void togglePet() {
		this.pet = !pet;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public HashMap<ItemStack, Integer> getDrops() {
		return drops;
	}

	public void addDrop(ItemStack stack, int p) {
		this.drops.put(stack, p);
	}

	public void removeDrop(ItemStack itemStack) {
		for (ItemStack stack : drops.keySet())
			if (stack.getType().equals(itemStack.getType()) && stack.getAmount() == itemStack.getAmount()) {
				drops.remove(stack);
				break;
			}
	}

	public HashMap<String, Material> getEq() {
		return eq;
	}

	public void setEq(String key, Material mat) {
		this.eq.put(key, mat);
	}

	public void increaseDrop(ItemStack stack) {
		int p = drops.get(stack);
		if (p < 100)
			drops.put(stack, p + 1);
	}

	public void decreaseDrop(ItemStack stack) {
		int p = drops.get(stack);
		if (p > 0)
			drops.put(stack, p - 1);
	}

	// IO
	public boolean saveToFile() {
		if (name == null || name.equals("") || type == null)
			return false;
		final String path = FilePath.mobs + name + "_" + String.valueOf(level) + ".yml";
		File f = new File(path);
		if (f.exists())
			f.delete();
		YamlConfiguration config = saveToConfig();
		try {
			config.save(f);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private YamlConfiguration saveToConfig() {
		YamlConfiguration config = new YamlConfiguration();
		// name
		config.set("name", name);
		// entity-type
		config.set("entity-type", type.name());
		// level
		config.set("level", level);
		// hitpoints
		config.set("hitpoints", hp);
		// defense
		config.set("defense", defense);
		// damage
		config.set("damage", damage);
		// aggressive
		config.set("aggressive", aggressive);
		// pet
		config.set("pet", pet);
		// experience
		config.set("experience", String.valueOf(exp) + "%" + String.valueOf(level) + "-" + String.valueOf(level + 1));
		// extra spawn time / not variable
		config.set("extra-spawn-time", 0);
		// equipment
		if (!eq.isEmpty()) {
			ConfigurationSection section = config.createSection("equipment");
			for (String key : eq.keySet()) {
				section.set(key.toLowerCase(), eq.get(key) != null ? eq.get(key).name() : "");
			}
		}
		//drops
		if (!drops.isEmpty()) {
			ArrayList<String> list = new ArrayList<String>();
			for (ItemStack key : drops.keySet()) {
				if (drops.get(key) != null)
					list.add(drops.get(key).toString() + "%" + key.getType().name() + ":"
							+ String.valueOf(key.getAmount()));
			}
			config.set("drops", list);
		}
		return config;
	}

	public static MobSkeleton loadByName(String name) {
		String ident = MobSpawner.getIdentifier(name);
		File f = new File(FilePath.mobs + ident + ".yml");
		MobSkeleton ms = new MobSkeleton();
		ms.setName(name);
		if (f.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			ms.setName(config.getString("name"));
			String type = config.getString("entity-type");
			try {
				EntityType t = EntityType.valueOf(type.toUpperCase().replaceAll(" ", "_"));
				ms.setType(t);
			} catch (Exception e) {
			}
			ms.setLevel(config.getInt("level", 50));
			ms.setHp(config.getInt("hitpoints", 10));
			ms.setDefense(config.getInt("defense", 0));
			ms.setDamage(config.getInt("damage", 0));

			if (ms.isAggressive() != config.getBoolean("aggressive"))
				ms.toggleAggressive();

			if (ms.isPet() != config.getBoolean("pet"))
				ms.togglePet();
			String exp = config.getString("experience", "50%").split("%")[0];
			if (Utils.isInteger(exp))
				ms.setExp(Integer.valueOf(exp));
			ConfigurationSection eq = config.getConfigurationSection("equipment");
			for (String key : eq.getKeys(false)) {
				String mat = eq.getString(key);
				ItemStack m = ItemUtils.getItem(mat);
				ms.getEq().put(key.toUpperCase(), m == null ? null : m.getType());
			}
			List<String> dropList = config.getStringList("drops");
			for (String l : dropList) {
				if (l.contains("%")) {
					String[] s = l.split("%");
					ItemStack stack = ItemUtils.getItem(s[1]);
					if (stack != null && Utils.isInteger(s[0]))
						ms.getDrops().put(stack, Integer.valueOf(s[0]));
				}
			}
		}
		return ms;
	}

}
