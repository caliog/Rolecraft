package org.caliog.Rolecraft.Items.Books;

import org.bukkit.Material;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.CustomItem;

public abstract class Book extends CustomItem {
	protected RolecraftPlayer player;

	public Book(String name, RolecraftPlayer clazz) {
		super(Material.WRITTEN_BOOK, name, false);
		this.player = clazz;
		syncItemStack();
	}

	public String getLore() {
		return null;
	}
}
