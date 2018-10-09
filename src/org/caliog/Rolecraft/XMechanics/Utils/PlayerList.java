package org.caliog.Rolecraft.XMechanics.Utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Guards.GManager;
import org.caliog.Rolecraft.Guards.Guard;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;;

public abstract class PlayerList {

	public static void refreshList() {
		ArrayList<Object> hide = new ArrayList<Object>();
		try {
			Class<?> craftPlayer = Reflect.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> packetPlayOutPlayerInfo = Reflect.getNMSClass("PacketPlayOutPlayerInfo");
			Class<?> enumPlayerInfoAction = Reflect.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
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
				Reflect.sendPacket(p, packet);
			}
		} catch (Exception e) {
			Debugger.exception("PlayerList.refreshList() threw an exception: ", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void refreshList(Player p) {
		ArrayList<Object> hide = new ArrayList<Object>();
		try {
			Class<?> craftPlayer = Reflect.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> packetPlayOutPlayerInfo = Reflect.getNMSClass("PacketPlayOutPlayerInfo");
			Class<?> enumPlayerInfoAction = Reflect.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

			for (Guard guard : GManager.getGuards()) {
				hide.add(craftPlayer.getMethod("getHandle").invoke(guard.getBukkitEntity()));
			}

			if (hide.isEmpty())
				return;

			Object packet = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Iterable.class)
					.newInstance(enumPlayerInfoAction.getField("REMOVE_PLAYER").get(null), hide);

			Reflect.sendPacket(p, packet);
		} catch (Exception e) {
			Debugger.exception("PlayerList.refreshList(Player p) threw an exception:", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void restoreList(Player p) {
		ArrayList<Object> hide = new ArrayList<Object>();
		try {
			Class<?> craftPlayer = Reflect.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> packetPlayOutPlayerInfo = Reflect.getNMSClass("PacketPlayOutPlayerInfo");
			Class<?> enumPlayerInfoAction = Reflect.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

			for (Guard guard : GManager.getGuards()) {
				hide.add(craftPlayer.getMethod("getHandle").invoke(guard.getBukkitEntity()));
			}

			if (hide.isEmpty())
				return;

			Object packet = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, Iterable.class)
					.newInstance(enumPlayerInfoAction.getField("ADD_PLAYER").get(null), hide);

			Reflect.sendPacket(p, packet);
		} catch (Exception e) {
			Debugger.exception("PlayerList.restoreList threw an exception:", e.getMessage());
			e.printStackTrace();
		}
	}

}
