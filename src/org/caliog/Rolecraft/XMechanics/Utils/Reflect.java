package org.caliog.Rolecraft.XMechanics.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;

public abstract class Reflect {
	public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
		String name = "net.minecraft.server." + Manager.getServerVersion() + "." + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getHandle = player.getClass().getMethod("getHandle");
		Object nmsPlayer = getHandle.invoke(player);
		Field conField = nmsPlayer.getClass().getField("playerConnection");
		Object con = conField.get(nmsPlayer);
		return con;
	}

	public static Class<?> getCraftbukkitClass(String nmsClassString) throws ClassNotFoundException {
		String name = "org.bukkit.craftbukkit." + Manager.getServerVersion() + "." + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	public static void sendPacket(Player player, Object packet)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, ClassNotFoundException, NoSuchFieldException {
		Object connection = getConnection(player);
		if (connection == null)
			return;
		connection.getClass().getMethod("sendPacket", Reflect.getNMSClass("Packet")).invoke(connection, packet);
	}

	public static boolean isBukkitMethod(String c, String m, Class<?>... param) {
		try {
			Class<?> cl = Manager.plugin.getClass().getClassLoader().loadClass(c);
			cl.getMethod(m, param);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			return false;
		}
		return true;
	}

	public static boolean isBukkitField(String c, String f) {
		try {
			Class<?> cl = Manager.plugin.getClass().getClassLoader().loadClass(c);
			cl.getField(f);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
			return false;
		}
		return true;
	}

	public static boolean isBukkitClass(String string) {
		try {
			Manager.plugin.getClass().getClassLoader().loadClass(string);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
}
