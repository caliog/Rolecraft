package org.caliog.Rolecraft.Items;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Items.ItemEffect.ItemEffectType;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;

public class CustomItemInstance extends CustomItem {
	protected final YamlConfiguration config;

	public CustomItemInstance(Material type, String name, boolean tradeable, YamlConfiguration config) {
		super(type, name, tradeable);
		this.config = config;
		syncItemStack();
	}

	public List<ItemEffect> getEffects() {
		ConfigurationSection sec = config.getConfigurationSection("item-effects");
		this.effects.clear();
		if (sec == null)
			return effects;
		for (ItemEffectType type : ItemEffectType.values()) {
			if (sec.isInt(type.name())) {
				this.effects.add(new ItemEffect(type, sec.getInt(type.name())));
			}
		}
		return this.effects;
	}

	public String getLore() {
		return this.config.getString("lore", null);
	}

	public int getMinLevel() {
		return this.config.getInt("min-level", 0);
	}

	public String getClazz() {
		try {
			String c = this.config.getString("class-type");
			if (ClazzLoader.isClass(c))
				return c;
		} catch (Exception e) {
			Debugger.exception(LogTitle.NONE, "CustomItemInstance gave exception in getClazz: " + e.getMessage());
		}
		return null;
	}

	public static CustomItemInstance getInstance(String name, int i, boolean tradeable) {
		File f = new File(FilePath.items + name + ".yml");
		if (!f.exists()) {
			return null;
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

		Material mat = Material.matchMaterial(config.getString("material", "none"));
		if (mat == null) {
			return null;
		}
		CustomItemInstance instance = new CustomItemInstance(mat, name, tradeable, config);
		instance.setAmount(i);

		return instance;
	}
}
