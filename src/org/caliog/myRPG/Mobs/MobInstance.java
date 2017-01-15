package org.caliog.myRPG.Mobs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Entities.Playerface;
import org.caliog.myRPG.Items.ItemUtils;
import org.caliog.myRPG.Utils.FilePath;
import org.caliog.myRPG.Utils.Vector;

public class MobInstance extends Mob {
	public YamlConfiguration mobConfig = new YamlConfiguration();

	public MobInstance(String name, UUID id, Vector m) {
		super(name, id, m);
		File f = new File(FilePath.mobs + name + ".yml");
		if (f.exists()) {
			this.mobConfig = YamlConfiguration.loadConfiguration(f);
			setHealth(getHP());
		}
	}

	public int getLevel() {
		return this.mobConfig.getInt("level");
	}

	public EntityType getType() {
		try {
			return EntityType.valueOf(this.mobConfig.getString("entity-type", "error"));
		} catch (Exception e) {
			Manager.plugin.getLogger().warning("Error in " + getName() + "+.yml! Entity-Type is not a valid entity.");
			return EntityType.COW;
		}
	}

	public HashMap<String, ItemStack> eq() {
		this.eq.put("HAND", ItemUtils.getItem(this.mobConfig.getString("equipment.hand")));
		this.eq.put("HELMET", ItemUtils.getItem(this.mobConfig.getString("equipment.helmet")));
		this.eq.put("CHESTPLATE", ItemUtils.getItem(this.mobConfig.getString("equipment.chestplate")));
		this.eq.put("LEGGINGS", ItemUtils.getItem(this.mobConfig.getString("equipment.leggings")));
		this.eq.put("BOOTS", ItemUtils.getItem(this.mobConfig.getString("equipment.boots")));
		return this.eq;
	}

	public double getHP() {
		return this.mobConfig.getInt("hitpoints");
	}

	public boolean isAgressive() {
		return this.mobConfig.getBoolean("agressive");
	}

	public int getExp() {
		String s = this.mobConfig.getString("experience");
		int e = 0;
		try {
			if (s.length() >= 5 && s.split("%").length == 2 && s.split("-").length == 2) {
				e = (int) (Playerface.getExpDifference(Integer.parseInt(s.split("%")[1].split("-")[0]),
						Integer.parseInt(s.split("%")[1].split("-")[1])) * (Integer.parseInt(s.split("%")[0]) / 100.0F));
			} else if (s.contains("%")) {
				e = (int) (Playerface.getExpDifference(getLevel(), getLevel() + 1) * (Integer.parseInt(s.replace("%", "")) / 100F));
			} else
				throw new Exception();
		} catch (Exception exc) {
			Manager.plugin.getLogger().warning("Error in " + getName() + "+.yml! Experience expression is incorrect.");
			return 0;
		}
		return e;
	}

	public HashMap<ItemStack, Float> drops() {
		List<String> list = this.mobConfig.getStringList("drops");
		for (String l : list) {
			if (l.contains("%") && l.split("%").length == 2) {
				this.drops.put(ItemUtils.getItem(l.split("%")[1]), Float.valueOf(Integer.parseInt(l.split("%")[0]) / 100.0F));
			}
		}
		return this.drops;
	}

	public int getExtraTime() {
		return this.mobConfig.getInt("extra-spawn-time");
	}

	public double getDefense() {
		return this.mobConfig.getInt("defense");
	}

	public double getDamage() {
		return this.mobConfig.getInt("damage");
	}

	public boolean isPet() {
		return this.mobConfig.getBoolean("pet", false);
	}
}
