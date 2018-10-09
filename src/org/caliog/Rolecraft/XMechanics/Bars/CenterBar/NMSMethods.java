package org.caliog.Rolecraft.XMechanics.Bars.CenterBar;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Utils.Reflect;

public class NMSMethods {

	public static String getNMSMethodChar() {
		if (Manager.getServerVersion().equals("v1_10_R1"))
			return "b";
		else
			return "b";
	}

	public static void sendBar(Player player, String title, String subtitle, int fadein, int active, int fadeout) {
		try {
			Class<?> iChatBaseComponent = Reflect.getNMSClass("IChatBaseComponent");
			Class<?> chatSerializer = Reflect.getNMSClass("IChatBaseComponent$ChatSerializer");
			Class<?> packetPlayOutTitle = Reflect.getNMSClass("PacketPlayOutTitle");
			Class<?> enumTitleAction = Reflect.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
			if (title != null) {
				Object bc = chatSerializer.getMethod(getNMSMethodChar(), String.class).invoke(null, "{\"text\":\"" + title + "\"}");
				Object packet = packetPlayOutTitle.getConstructor(enumTitleAction, iChatBaseComponent, int.class, int.class, int.class)
						.newInstance(enumTitleAction.getField("TITLE").get(null), bc, fadein, active, fadeout);
				Reflect.sendPacket(player, packet);
			}
			if (subtitle != null) {
				Object bc = chatSerializer.getMethod(getNMSMethodChar(), String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
				Object packet = packetPlayOutTitle.getConstructor(enumTitleAction, iChatBaseComponent, int.class, int.class, int.class)
						.newInstance(enumTitleAction.getField("SUBTITLE").get(null), bc, fadein, active, fadeout);
				Reflect.sendPacket(player, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
