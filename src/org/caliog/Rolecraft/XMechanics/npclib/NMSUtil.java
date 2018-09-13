package org.caliog.Rolecraft.XMechanics.npclib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.NMS.NMS;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.BWorld;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public interface NMSUtil {

	public abstract void setYaw(Entity entity, float yaw);

	public abstract void pathStep(Moveable a);

	public abstract NPCManager getnpcManager();

	public abstract void nodeUpdate(Node node);

	public abstract Object getPlayerHandle(Player player);

	public abstract Entity createNPCEntity(NPCManager manager, BWorld world, String name);

	public static NMSUtil getUtil() {
		String version = Manager.plugin.getBukkitVersion();
		try {
			Class<?> raw = Class.forName("org.caliog.Rolecraft.XMechanics.npclib." + version + ".Util");
			Class<? extends NMSUtil> util = raw.asSubclass(NMSUtil.class);
			Constructor<? extends NMSUtil> constructor = util.getConstructor();
			return (NMSUtil) constructor.newInstance();
		} catch (ClassNotFoundException ex) {
			Debugger.exception("NMSUtil threw ClassNotFoundException (unsupported bukkit version).");
			Manager.plugin.getLogger().log(Level.WARNING, "Unsupported bukkit version! (" + version + ")");
		} catch (Exception e) {
			Debugger.exception("NMSUtil threw exception:", e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public static NPCManager getNPCManager() {
		NMSUtil util = getUtil();
		if (util != null)
			return getUtil().getnpcManager();
		return null;
	}

	public static void sendPacketsTo(Iterable<? extends Player> recipients, Object... packets) {
		try {
			Class<?> craftPlayerClass = NMS.getCraftbukkitNMSClass("entity.CraftPlayer");
			Class<?> entityPlayerClass = NMS.getNMSClass("EntityPlayer");
			Class<?> playerConnectionClass = NMS.getNMSClass("PlayerConnection");
			Class<?> packetClass = NMS.getNMSClass("Packet");
			Iterable<Object> nmsRecipients = Iterables.transform(recipients, new Function<Player, Object>() {

				@Override
				public Object apply(Player a) {
					try {
						return craftPlayerClass.getMethod("getHandle").invoke(craftPlayerClass.cast(a));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
							| SecurityException e) {
						e.printStackTrace();
						return null;
					}
				}
			});
			for (Object recipient : nmsRecipients) {
				if (recipient != null) {
					for (Object packet : packets) {
						if (packet != null) {
							Object playerConnection = entityPlayerClass.getField("playerConnection").get(recipient);
							playerConnectionClass.getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPacketsTo(Player player, Object... packet) {
		ArrayList<Player> it = new ArrayList<Player>();
		it.add(player);
		sendPacketsTo(it, packet);
	}

}
