package org.caliog.Rolecraft.XMechanics.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Menus.MenuInventoryView;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;

public class MenuListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player)
			if (event.getView() instanceof MenuInventoryView) {
				MenuManager.closing((MenuInventoryView) event.getView());

				Bukkit.getScheduler().scheduleSyncDelayedTask(Manager.plugin, new Runnable() {

					@Override
					public void run() {
						if (!(event.getPlayer().getOpenInventory() instanceof MenuInventoryView)) {
							MenuManager.closedAll((Player) event.getPlayer());
						}
					}
				});

			}
	}
}
