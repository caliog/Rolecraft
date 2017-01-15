package org.caliog.myRPG.Utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.caliog.Villagers.NPC.Guards.GManager;
import org.caliog.Villagers.NPC.Guards.Guard;
import org.caliog.myRPG.NMS.NMS;;

public class PlayerList {

	public static void refreshList() {
		ArrayList<Object> hide = new ArrayList<Object>();
		try {
			Class<?> craftPlayer = NMS.getCraftbukkitNMSClass("entity.CraftPlayer");
			Class<?> packetPlayOutPlayerInfo = NMS.getNMSClass("PacketPlayOutPlayerInfo");
			Class<?> enumPlayerInfoAction = NMS.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
			// Class<?> entityPlayer = NMS.getNMSClass("EntityPlayer");

			for (Guard guard : GManager.getGuards()) {
				hide.add(craftPlayer.getMethod("getHandle").invoke(guard.getBukkitEntity()));
			}

			if (hide.isEmpty())
				return;
			Object packet = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Iterable.class)
					.newInstance(enumPlayerInfoAction.getField("REMOVE_PLAYER").get(null), hide);
			for (Player p : Bukkit.getOnlinePlayers()) {
				try {
					if (hide.contains(craftPlayer.getMethod("getHandle").invoke(p)))
						continue;
				} catch (Exception e) {
					continue;
				}
				NMS.sendPacket(p, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void refreshList(Player p) {
		ArrayList<Object> hide = new ArrayList<Object>();
		try {
			Class<?> craftPlayer = NMS.getCraftbukkitNMSClass("entity.CraftPlayer");
			Class<?> packetPlayOutPlayerInfo = NMS.getNMSClass("PacketPlayOutPlayerInfo");
			Class<?> enumPlayerInfoAction = NMS.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

			for (Guard guard : GManager.getGuards()) {
				hide.add(craftPlayer.getMethod("getHandle").invoke(guard.getBukkitEntity()));
			}

			if (hide.isEmpty())
				return;

			Object packet = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Iterable.class)
					.newInstance(enumPlayerInfoAction.getField("REMOVE_PLAYER").get(null), hide);

			NMS.sendPacket(p, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void restoreList(Player p) {
		ArrayList<Object> hide = new ArrayList<Object>();
		try {
			Class<?> craftPlayer = NMS.getCraftbukkitNMSClass("entity.CraftPlayer");
			Class<?> packetPlayOutPlayerInfo = NMS.getNMSClass("PacketPlayOutPlayerInfo");
			Class<?> enumPlayerInfoAction = NMS.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

			for (Guard guard : GManager.getGuards()) {
				hide.add(craftPlayer.getMethod("getHandle").invoke(guard.getBukkitEntity()));
			}

			if (hide.isEmpty())
				return;

			Object packet = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Iterable.class)
					.newInstance(enumPlayerInfoAction.getField("ADD_PLAYER").get(null), hide);

			NMS.sendPacket(p, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
