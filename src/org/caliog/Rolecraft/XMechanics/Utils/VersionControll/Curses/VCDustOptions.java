package org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Curses;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Color;

public class VCDustOptions {

	private VCDustOptions() {

	}

	public static Object getDustOptions(Color c, float f) {
		try {
			Class<?> dustOptions = Class.forName("org.bukkit.Particle.DustOptions");
			Constructor<?> constr = dustOptions.getConstructor(c.getClass(), float.class);
			return constr.newInstance(c, f);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}
		return null;
	}
}
