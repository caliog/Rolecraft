package org.caliog.Rolecraft.XMechanics.npclib.NMS;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.caliog.Rolecraft.XMechanics.Reflection.Reflect;

public class BWorld {

	private Object wServer;

	public BWorld(World world) {
		try {
			Class<?> craftworld = Reflect.getCraftbukkitClass("CraftWorld");
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
