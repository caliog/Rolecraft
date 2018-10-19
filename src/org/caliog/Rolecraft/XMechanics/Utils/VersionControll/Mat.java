package org.caliog.Rolecraft.XMechanics.Utils.VersionControll;

import org.bukkit.Material;
import org.caliog.Rolecraft.Manager;

public enum Mat {

	// @formatter:off
	STATIONARY_WATER,
	SKULL_ITEM,
	LEASH,
	BOOK_AND_QUILL,
	EXP_BOTTLE,
	STAINED_GLASS_PANE,
	FENCE, WHITE_WOOL,
	GREEN_STAINED_GLASS_PANE,
	GOLD_HELMET, DIAMOND_HELMET, IRON_HELMET, LEATHER_HELMET,
	DIAMOND_CHESTPLATE, GOLD_CHESTPLATE, IRON_CHESTPLATE, LEATHER_CHESTPLATE,
	DIAMOND_LEGGINGS, GOLD_LEGGINGS, IRON_LEGGINGS, LEATHER_LEGGINGS,
	DIAMOND_BOOTS, GOLD_BOOTS, IRON_BOOTS, LEATHER_BOOTS;
	// @formatter:on

	public Material e() {
		if (Manager.getServerVersion().startsWith("v1_13")) {
			return Material.getMaterial(this.name(), true);
		} else {
			return Material.getMaterial(this.name());
		}
	}

	public Material f() {
		if (Manager.getServerVersion().startsWith("v1_13")) {
			return Material.getMaterial(this.name());
		} else {
			return Material.matchMaterial(this.name().split("_")[1]);
		}
	}

	public Material g() {
		if (Manager.getServerVersion().startsWith("v1_13")) {
			return Material.getMaterial(this.name());
		} else {
			return Material.matchMaterial(this.name().split("_")[0]);
		}
	}

	public static Material matchMaterial(String string) {
		if (Manager.getServerVersion().startsWith("v1_13")) {
			return Material.matchMaterial(string, true);
		} else {
			return Material.matchMaterial(string);
		}
	}

}
