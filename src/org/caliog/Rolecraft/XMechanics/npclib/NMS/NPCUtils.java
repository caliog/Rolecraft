package org.caliog.Rolecraft.XMechanics.npclib.NMS;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.XMechanics.NMS.NMS;

public class NPCUtils {
	public static void sendPacketNearby(Location location, Object packet) {
		sendPacketNearby(location, packet, 64);
	}

	public static void sendPacketNearby(Location location, Object packet, double radius) {
		radius *= radius;
		final World world = location.getWorld();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p == null || world != p.getWorld()) {
				continue;
			}
			if (location.distanceSquared(p.getLocation()) > radius) {
				continue;
			}
			try {
				Class<?> craftPlayerClass = NMS.getCraftbukkitNMSClass("entity.CraftPlayer");
				Class<?> entityPlayerClass = NMS.getNMSClass("EntityPlayer");
				Class<?> packetClass = NMS.getNMSClass("Packet");
				Class<?> playerConnectionClass = NMS.getNMSClass("PlayerConnection");
				Object entityPlayer = craftPlayerClass.getMethod("getHandle").invoke(craftPlayerClass.cast(p));
				Object playerConnection = entityPlayerClass.getField("playerConnection").get(entityPlayer);
				playerConnectionClass.getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static ItemStack[] combineItemStackArrays(Object[] a, Object[] b) {
		ItemStack[] c = new ItemStack[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
}
