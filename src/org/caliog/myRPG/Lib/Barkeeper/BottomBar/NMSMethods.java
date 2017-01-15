package org.caliog.myRPG.Lib.Barkeeper.BottomBar;

import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.NMS.NMS;

public class NMSMethods {

	public static String getNMSMethodChar() {
		if (Manager.plugin.getVersion().equals("v1_8_R2") || Manager.plugin.getVersion().equals("v1_8_R3"))
			return "a";
		else
			return "b";
	}

	public static void sendHotBar(Player player, String msg) {
		try {
			Class<?> chatSerializer = NMS.getNMSClass("IChatBaseComponent$ChatSerializer");
			Class<?> packetPlayOutChat = NMS.getNMSClass("PacketPlayOutChat");

			Object bc = chatSerializer.getMethod(getNMSMethodChar(), String.class).invoke(null, "{'text': '" + msg + "'}");
			Object packet = packetPlayOutChat.getConstructor(NMS.getNMSClass("IChatBaseComponent"), byte.class).newInstance(bc, (byte) 2);
			NMS.sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
