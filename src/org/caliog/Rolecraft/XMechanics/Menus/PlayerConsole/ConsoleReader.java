package org.caliog.Rolecraft.XMechanics.Menus.PlayerConsole;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.caliog.Rolecraft.Manager;

import org.bukkit.ChatColor;

public abstract class ConsoleReader extends Stoppable {

	private final PlayerChatReader reader;
	private final Player player;
	private String lastLine = null;

	public ConsoleReader(Player player) {
		this.player = player;
		reader = new PlayerChatReader(player);
		Bukkit.getServer().getPluginManager().registerEvents(reader, Manager.plugin);
	}

	@Override
	public void run() {
		if (!super.check())
			return;
		String cLine = reader.getLine(); // sets intern "line" field to null after it returns its value
		if (cLine != null)
			lastLine = cLine;
		// call doWork once to "start"
		if (lastLine == null) {
			this.doWork(null);
			this.lastLine = ""; // making sure lastLine is not null from now on (time; not code loc)
		}
		if (cLine == null)
			return;
		if (cLine.equals("q") || cLine.equals("quit") || cLine.equals("exit")) {
			quit();
		} else {
			this.player.sendMessage(ChatColor.GRAY + ">" + ChatColor.ITALIC + "" + ChatColor.WHITE + lastLine);
			this.doWork(lastLine); // only do work if line has changed
		}

	}

	protected abstract void doWork(String lastLine);

	protected abstract void quit();

	@Override
	protected void stop() {
		super.stop();
		HandlerList.unregisterAll(reader);
	}

}
