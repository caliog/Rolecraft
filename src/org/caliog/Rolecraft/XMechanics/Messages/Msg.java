package org.caliog.Rolecraft.XMechanics.Messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class Msg {

	public static final String WEAPON = "%WEAPON%";
	public static final String LEVEL = "%LEVEL%";
	public static final String CLASS = "%CLASS%";
	public static final String PLAYER = "%PLAYER%";
	public static final String QUEST = "%QUEST%";

	public static YamlConfiguration file;

	public static void init() throws IOException {
		String lang_path = RolecraftConfig.getLangCode() + "_lang.yml";
		File messages_file = new File(FilePath.messages);
		if (!messages_file.exists()) {
			messages_file.createNewFile();
		}

		file = YamlConfiguration.loadConfiguration(messages_file);

		InputStream stream = new FileCreator().getClass().getResourceAsStream("lang/" + lang_path);
		if (stream == null) {
			stream = new FileCreator().getClass().getResourceAsStream("lang/en_lang.yml");
			if (stream == null) {
				Debugger.warning(LogTitle.NONE, "Could not find default lang file: en_lang.yml in Msg.java");
				return;
			}
		}

		YamlConfiguration def = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(stream, "UTF-8")));
		final boolean copy_hard = !def.getString("lang", "en").equals(file.getString("lang", "."));

		if (copy_hard)
			for (String key : file.getKeys(true)) {
				file.set(key, null);
			}
		file.addDefaults(def);
		file.options().copyDefaults(true);
		file.options().header("do not change 'lang' value - to change language, use the config.yml");
		file.save(messages_file);
		file = YamlConfiguration.loadConfiguration(new File(FilePath.messages));

		// order
		BufferedReader r = new BufferedReader(new FileReader(messages_file));
		String nextLine = "";
		ArrayList<String> list = new ArrayList<String>();
		while ((nextLine = r.readLine()) != null) {
			list.add(nextLine);
		}
		r.close();
		FileWriter w = new FileWriter(messages_file);
		String text = "";
		if (list.size() >= 2) {
			text += list.get(0) + "\n";
			text += list.get(1) + "\n";
			for (Key k : Key.values()) {
				boolean found = false;
				secondloop: for (String l : list) {
					if (!found && l.startsWith(k.getKey())) {
						found = true;
						text += l + "\n";
						continue;
					}
					if (found) {
						for (Key kk : Key.values()) {
							if (l.startsWith(kk.getKey()))
								break secondloop;
						}
						// extended line
						text += l + "\n";
					}
				}
			}
		}
		w.write(text);
		w.close();
	}

	private static boolean sendMessageTo(Player player, String msg, Key msgKey) {
		if (msg == null || msg.length() == 0) {
			String k = msgKey == null ? "" : (" (key = " + msgKey.name() + ")");
			Manager.plugin.getLogger().warning("Message error! Look over your messages file!" + k);
			return false;
		} else
			player.sendMessage(msg);
		return true;
	}

	public static void sendMessage(Player player, Key msgKey, String[] key, String[] replace) {
		if (key != null)
			if (key.length != replace.length) {
				Manager.plugin.getLogger().warning("Parameter Error in Msg.java");
				return;
			}

		sendMessageTo(player, getMessage(msgKey, key, replace), msgKey);
	}

	public static String getMessage(Key msgKey, String key, String replace) {
		String[] a = { key };
		String[] b = { replace };
		return getMessage(msgKey, a, b);
	}

	public static String getMessage(Key msgKey) {
		String t = null;
		return getMessage(msgKey, t, t);
	}

	private static String getMessage(Key msgKey, String[] key, String[] replace) {
		String msg = msgKey.getString();
		if (msg == null || (key != null && replace != null && replace.length != key.length))
			return null;
		if (key != null)
			for (int i = 0; i < key.length; i++)
				if (key[i] != null && replace[i] != null)
					msg = msg.replace(key[i], replace[i]);

		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
	}

	public static void sendMessage(Player player, Key msgKey, String key, String replace) {
		String[] a = { key }, b = { replace };
		sendMessage(player, msgKey, a, b);
	}

	public static void sendMessage(Player player, Key msgKey) {
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
