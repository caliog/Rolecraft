package org.caliog.Rolecraft.XMechanics.npclib.NMS;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.caliog.Rolecraft.XMechanics.NMS.NMS;

public class BWorld {

	private Object wServer;

	public BWorld(World world) {
		try {
			Class<?> craftworld = NMS.getCraftbukkitNMSClass("CraftWorld");
			Object cWorld = craftworld.cast(world);
			wServer = craftworld.getMethod("getHandle").invoke(cWorld);
		} catch (final Exception ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public Object getWorldServer() {
		return wServer;
	}

}
