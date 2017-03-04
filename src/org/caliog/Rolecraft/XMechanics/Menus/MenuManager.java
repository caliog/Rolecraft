package org.caliog.Rolecraft.XMechanics.Menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MenuManager {

	private static final List<Menu> menus = new ArrayList<Menu>();
	private static final HashMap<Player, PlayerMenus> playerMenus = new HashMap<Player, PlayerMenus>();
	private static final List<Material> tools = new ArrayList<Material>();

	public static Menu findMenu(String name) {
		for (Menu m : menus)
			if (m.getName().equalsIgnoreCase(name))
				return m;
		return null;
	}

	public static boolean openMenu(Player player, String string) {
		Menu menu = findMenu(string);
		if (menu == null)
			return false;
		return openMenu(player, menu);
	}

	public static boolean openMenu(Player player, Menu menu) {
		PlayerMenus pm;
		if (!playerMenus.containsKey(player)) {
			pm = new PlayerMenus(player);
		} else
			pm = playerMenus.get(player);
		pm.addMenu(menu);
		playerMenus.put(player, pm);
		return true;
	}

	public static void exitMenu(Player player) {
		if (playerMenus.containsKey(player))
			playerMenus.get(player).closeMenu();
	}

	public static void toolUsed(Player player, String n, Material material) {
		if (player.hasPermission("minemenu.open")) {
			if (tools.contains(material))
				openMenu(player, n);
		}
	}

	public static void addTool(Material material) {
		tools.add(material);
	}

	public static void playerJoined(Player player) {
		if (player == null)
			return;
	}

	public static void closing(MenuInventoryView view) {
		String title = view.getTopInventory().getTitle();
		view.closeListener();
		if (title == null)
			return;
		if (playerMenus.containsKey(view.getPlayer()))
			playerMenus.get(view.getPlayer()).closing(title);
	}

	public static void closedAll(Player player) {
		if (player == null)
			return;
		if (playerMenus.containsKey(player))
			playerMenus.get(player).closedAll();

	}

	public static void refreshView(Player player) {
		if (player == null)
			return;
		if (playerMenus.containsKey(player))
			playerMenus.get(player).display();

	}

	public static void quit(Player player) {
		playerMenus.remove(player);
	}

	public static void addDummy(Player player) {
		if (playerMenus.containsKey(player))
			playerMenus.get(player).addDummy();

	}

	public static List<Menu> getMenus() {
		return menus;
	}

	public static boolean edit(String menu, Player player) {
		PlayerMenus pm = null;
		if (playerMenus.containsKey(player)) {
			pm = playerMenus.get(player);
		} else {
			pm = new PlayerMenus(player);

		}
		if (pm.isEmpty()) {
			if (menu == null)
				return false;
			Menu m = findMenu(menu);
			if (m == null)
				return false;
			pm.addMenu(m);
			playerMenus.put(player, pm);
		}
		return pm.edit(menu);

	}

	public static boolean isEditing(Player player) {
		if (playerMenus.containsKey(player))
			return playerMenus.get(player).isEditing();

		return false;
	}
}