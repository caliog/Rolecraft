package org.caliog.Rolecraft.XMechanics.Utils.VersionControll;

import org.bukkit.Material;
import org.caliog.Rolecraft.Manager;

public enum Mat {

	// @formatter:off
	STATIONARY_WATER, LEASH, SKULL_ITEM, BOOK_AND_QUILL, EXP_BOTTLE, STAINED_GLASS_PANE, FENCE, WHITE_WOOL, GREEN_STAINED_GLASS_PANE;
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
