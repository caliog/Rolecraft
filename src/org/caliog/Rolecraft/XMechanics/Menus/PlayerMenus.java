package org.caliog.Rolecraft.XMechanics.Menus;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerMenus {

	private final Player player;
	private final HashMap<Integer, Menu> openMenus = new HashMap<Integer, Menu>();
	private boolean editing = false;

	public PlayerMenus(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void addMenu(Menu menu) {
		int max = getMaxMenuID() + 1;
		openMenus.put(max, menu);
		display();
	}

	// protects the previous inventory to get closed to soon
	// there is an inventory view which is not registered here, in replacement
	// we register the dummy
	public void addDummy() {
		int max = getMaxMenuID() + 1;
		// only place the dummy if there is sth to protect
		if (max > 0)
			openMenus.put(max, null);
	}

	public void display() {
		int max = getMaxMenuID();
		if (max == -1) {
			if (player.getOpenInventory() instanceof MenuInventoryView) {
				player.closeInventory();
			}
		} else {
			Menu m = openMenus.get(max);
			if (m != null)
				m.display(player);
		}
	}

	public void closeMenu() {
		int max = getMaxMenuID();
		if (max == -1)
			return;
		Menu m = openMenus.remove(max);
		if (editing && m != null) {
			// m.save(player.getOpenInventory().getTopInventory());
			editing = false;
		}
		// if m == null, we remove the dummy
		// only display "previous" inventory if we didnt close the dummy
		if (m != null)
			display();

	}

	public void closing(String title) {
		int max = getMaxMenuID();
		if (max == -1)
			return;
		Menu m = openMenus.get(max);

		// removing dummy in case m == null
		if (m == null || m.getName().equals(title)) {
			closeMenu();
		}

	}

	private int getMaxMenuID() {
		int max = -1;
		for (int k : openMenus.keySet())
			if (k > max)
				max = k;
		return max;
	}

	public void closedAll() {
		openMenus.clear();
	}

	public boolean edit(String title) {
		int max = getMaxMenuID();
		if (max == -1)
			return false;
		if (title == null || openMenus.get(max).getName().equalsIgnoreCase(title)) {
			editing = true;
			return true;
		}
		return false;
	}

	public boolean isEditing() {
		return editing;
	}

	public boolean isEmpty() {
		return getMaxMenuID() == -1;
	}

}
