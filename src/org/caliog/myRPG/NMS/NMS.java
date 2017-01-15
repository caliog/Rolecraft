package org.caliog.myRPG.NMS;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;

public class NMS {
	public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
		String name = "net.minecraft.server." + Manager.plugin.getVersion() + "." + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getHandle = player.getClass().getMethod("getHandle");
		Object nmsPlayer = getHandle.invoke(player);
		Field conField = nmsPlayer.getClass().getField("playerConnection");
		Object con = conField.get(nmsPlayer);
		return con;
	}

	public static Class<?> getCraftbukkitNMSClass(String nmsClassString) throws ClassNotFoundException {
		String name = "org.bukkit.craftbukkit." + Manager.plugin.getVersion() + "." + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	public static void sendPacket(Player player, Object packet) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NoSuchFieldException {
		Object connection = getConnection(player);
		if (connection == null)
			return;
		connection.getClass().getMethod("sendPacket", NMS.getNMSClass("Packet")).invoke(connection, packet);
	}
}
