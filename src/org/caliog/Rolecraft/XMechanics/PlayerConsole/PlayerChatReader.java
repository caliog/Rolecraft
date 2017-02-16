package org.caliog.Rolecraft.XMechanics.PlayerConsole;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatReader implements Listener {

	private String line;
	private Player player;

	public PlayerChatReader(Player player) {
		this.player = player;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (player != null && event.getPlayer().equals(player)) {
			line = event.getMessage();
		}
	}

	public String getLine() {
		return line;
	}

}