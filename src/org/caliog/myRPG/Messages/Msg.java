package org.caliog.myRPG.Messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Commands.Utils.CommandField;
import org.caliog.myRPG.Resource.FileCreator;
import org.caliog.myRPG.Utils.FilePath;

public class Msg {

	public static final String WEAPON = "%WEAPON%";
	public static final String LEVEL = "%LEVEL%";
	public static final String CLASS = "%CLASS%";
	public static final String PLAYER = "%PLAYER%";
	public static YamlConfiguration file;

	@SuppressWarnings("deprecation")
	public static void init() throws IOException {
		file = YamlConfiguration.loadConfiguration(new File(FilePath.messages));
		InputStream stream = new FileCreator().getClass().getResourceAsStream("messages.yml");
		if (stream == null)
			return;
		YamlConfiguration def = YamlConfiguration.loadConfiguration(stream);
		file.addDefaults(def);
		file.options().copyDefaults(true);
		try {
			File f = new File(FilePath.messages);
			String str = file.saveToString();
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			while (str.contains("comment")) {
				str = str.replace(str.substring(str.indexOf("comment"), str.indexOf(": '#") + 3), "");
				str = str.replaceFirst("#'", "");
			}
			bf.write(str);
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		file = YamlConfiguration.loadConfiguration(new File(FilePath.messages));

	}

	private static boolean sendMessageTo(Player player, String msg, String key) {
		if (msg == null || msg.length() == 0) {
			String k = key == null ? "" : (" (key = " + key + ")");
			Manager.plugin.getLogger().warning("Message error! Look over your messages file!" + k);
			return false;
		} else
			player.sendMessage(msg);
		return true;
	}

	public static void sendMessage(Player player, String msgKey, String[] key, String[] replace) {
		if (key != null)
			if (key.length != replace.length) {
				Manager.plugin.getLogger().warning("Parameter Error in Msg.java");
				return;
			}

		sendMessageTo(player, getMessage(msgKey, key, replace), msgKey);
	}

	public static String getMessage(String msgKey, String key, String replace) {
		String[] a = { key };
		String[] b = { replace };
		return getMessage(msgKey, a, b);
	}

	private static String getMessage(String msgKey, String[] key, String[] replace) {
		String msg = file.getString(msgKey);
		if (msg == null || (key != null && replace != null && replace.length != key.length))
			return null;
		if (key != null)
			for (int i = 0; i < key.length; i++)
				msg = msg.replace(key[i], replace[i]);
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
	}

	public static void sendMessage(Player player, String msgKey, String key, String replace) {
		String[] a = { key }, b = { replace };
		sendMessage(player, msgKey, a, b);
	}

	public static void sendMessage(Player player, String msgKey) {
		String[] a = null, b = null;
		sendMessage(player, msgKey, a, b);
	}

	public static void commandUsageError(CommandField field, Player player) {
		String type = "";
		if (field.getType().contains("positive"))
			type += "positive integer";
		else if (field.getType().contains("not-negative"))
			type += "not-negative integer";
		else if (field.getType().contains("integer"))
			type += "integer";
		String message = field.getName() + " has to be a " + type + "!";
		sendMessageTo(player, ChatColor.RED + message, null);
	}

	public static void commandOptionError(CommandField field, Player player) {
		sendMessageTo(player, ChatColor.RED + field.getName() + " has to be on of these options: " + field.getType(), null);
	}

	public static void noPermission(Player player) {
		sendMessageTo(player, ChatColor.RED + "You do not have the permission to do this!", null);
	}

}
