package org.caliog.Rolecraft.XMechanics.Bars.BottomBar;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Reflection.Reflect;

public class NMSMethods {

	public static String getNMSMethodChar() {
		if (Manager.plugin.getBukkitVersion().equals("v1_8_R2") || Manager.plugin.getBukkitVersion().equals("v1_8_R3"))
			return "a";
		else
			return "b";
	}

	public static void sendHotBar(Player player, String msg) {
		try {
			Class<?> chatSerializer = Reflect.getNMSClass("IChatBaseComponent$ChatSerializer");
			Class<?> packetPlayOutChat = Reflect.getNMSClass("PacketPlayOutChat");

			Object bc = chatSerializer.getMethod(getNMSMethodChar(), String.class).invoke(null, "{'text': '" + msg + "'}");
			Object packet;

			if (Manager.plugin.getBukkitVersion().startsWith("v1_11")) {
				packet = packetPlayOutChat.getConstructor(Reflect.getNMSClass("IChatBaseComponent"), byte.class).newInstance(bc, (byte) 2);
			} else {// version v1_12
				Object chatMessageType = Reflect.getNMSClass("ChatMessageType").getMethod("a", byte.class).invoke(null, (byte) 2);
				packet = packetPlayOutChat.getConstructor(Reflect.getNMSClass("IChatBaseComponent"), Reflect.getNMSClass("ChatMessageType"))
						.newInstance(bc, chatMessageType);
			}

			Reflect.sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
