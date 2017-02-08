package org.caliog.Rolecraft.XMechanics.npclib;

import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;

public abstract class NPC extends Moveable {

	public abstract void lookAtPoint(Location loc);

	public abstract void animateArmSwing();

	public abstract PlayerInventory getInventory();

	public abstract void updateEquipment();

}
