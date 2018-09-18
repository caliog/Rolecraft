package org.caliog.Rolecraft.XMechanics.Reflection;

import org.caliog.Rolecraft.Manager;

public class BukkitReflect {

	public static boolean isBukkitMethod(String c, String m, Class<?>... param) {
		try {
			Class<?> cl = Manager.plugin.getClass().getClassLoader().loadClass(c);
			cl.getMethod(m, param);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isBukkitField(String c, String f) {
		try {
			Class<?> cl = Manager.plugin.getClass().getClassLoader().loadClass(c);
			cl.getField(f);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isBukkitClass(String string) {
		try {
			Manager.plugin.getClass().getClassLoader().loadClass(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
