package org.caliog.Rolecraft.XMechanics.VersionControll;

import org.bukkit.Material;
import org.caliog.Rolecraft.Manager;

public enum Mat {

	// @formatter:off
	//test
	STATIONARY_WATER, LEASH, SKULL_ITEM, BOOK_AND_QUILL, EXP_BOTTLE, STAINED_GLASS_PANE, FENCE;
	// @formatter:on

	public Material e() {
		if (Manager.plugin.getBukkitVersion().startsWith("v1_13")) {
			return Material.getMaterial(this.name(), true);
		} else {
			return Material.getMaterial(this.name());
		}
	}
}
