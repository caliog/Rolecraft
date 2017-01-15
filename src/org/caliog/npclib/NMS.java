package org.caliog.npclib;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import org.caliog.myRPG.Manager;

public class NMS {

	public static NMSUtil getUtil() {
		String version = Manager.plugin.getVersion();
		try {
			Class<?> raw = Class.forName("org.caliog.npclib." + version + ".Util");
			Class<? extends NMSUtil> util = raw.asSubclass(NMSUtil.class);
			Constructor<? extends NMSUtil> constructor = util.getConstructor();
			return (NMSUtil) constructor.newInstance();
		} catch (ClassNotFoundException ex) {
			Manager.plugin.getLogger().log(Level.WARNING, "Unsupported bukkit version! (" + version + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static NPCManager getNPCManager() {
		NMSUtil util = getUtil();
		if (util != null)
			return getUtil().getNPCManager();
		return null;
	}
}
