package org.caliog.Rolecraft.XMechanics.Bars.BottomBar;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.NMS.NMS;

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
			Object packet;

			if (Manager.plugin.getVersion().startsWith("v1_11")) {
				packet = packetPlayOutChat.getConstructor(NMS.getNMSClass("IChatBaseComponent"), byte.class).newInstance(bc, (byte) 2);
			} else {// version v1_12
				Object chatMessageType = NMS.getNMSClass("ChatMessageType").getMethod("a", byte.class).invoke(null, (byte) 2);
				packet = packetPlayOutChat.getConstructor(NMS.getNMSClass("IChatBaseComponent"), NMS.getNMSClass("ChatMessageType"))
						.newInstance(bc, chatMessageType);
			}

			NMS.sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
