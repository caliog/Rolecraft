package org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Curses;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Color;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;

public class VCDustOptions {

	private VCDustOptions() {

	}

	public static Object getDustOptions(Color c, float f) {
		try {
			Class<?> dustOptions = Class.forName("org.bukkit.Particle$DustOptions");
			Constructor<?> constr = dustOptions.getConstructor(c.getClass(), float.class);
			return constr.newInstance(c, f);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Debugger.warning(LogTitle.SPELL, "Server structure does not support DustOptions for Curses.");
			Manager.plugin.getLogger().warning("Server structure does not support DustOptions for Curses.");
		}
		return null;
	}
}
