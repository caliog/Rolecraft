package org.caliog.Rolecraft.Items.Custom.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Items.Books.DexBook;
import org.caliog.Rolecraft.Items.Books.IntBook;
import org.caliog.Rolecraft.Items.Books.StrBook;
import org.caliog.Rolecraft.Items.Books.VitBook;

public class SkillInventoryView extends InventoryView {
	private HumanEntity entity;
	private Inventory inventory;

	public SkillInventoryView(Player player, Inventory inv) {
		this.entity = player;
		this.inventory = inv;
	}

	public Inventory getBottomInventory() {
		return this.inventory;
	}

	public HumanEntity getPlayer() {
		return this.entity;
	}

	public Inventory getTopInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, "Your Skills");
		inv.addItem(new ItemStack[] { new StrBook(PlayerManager.getPlayer(this.entity.getUniqueId())) });
		inv.addItem(new ItemStack[] { new DexBook(PlayerManager.getPlayer(this.entity.getUniqueId())) });
		inv.addItem(new ItemStack[] { new IntBook(PlayerManager.getPlayer(this.entity.getUniqueId())) });
		inv.addItem(new ItemStack[] { new VitBook(PlayerManager.getPlayer(this.entity.getUniqueId())) });
		return inv;
	}

	public InventoryType getType() {
		return this.inventory.getType();
	}
}