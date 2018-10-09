package org.caliog.Rolecraft.XMechanics.npclib.NMS;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.caliog.Rolecraft.XMechanics.Utils.Reflect;

/**
 * Server hacks for Bukkit
 * 
 * @author Kekec852
 */
public class BServer {

	private static BServer ins;
	private Object mcServer;
	private Object cServer;
	private final Server server;
	Class<?> craftserver;

	private BServer() {
		server = Bukkit.getServer();
		try {
			craftserver = Reflect.getCraftbukkitClass("CraftServer");
			cServer = craftserver.cast(server);
			mcServer = craftserver.getMethod("getServer").invoke(cServer);
		} catch (final Exception ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
			ex.printStackTrace();
		}
	}

	public Server getServer() {
		return server;
	}

	public static BServer getInstance() {
		if (ins == null) {
			ins = new BServer();
		}
		return ins;
	}

	public Object getMCServer() {
		return mcServer;
	}

}
