package org.caliog.Rolecraft.XMechanics.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class Debugger {

	public enum LogLevel {
		INFO, WARNING, ERROR, EXCEPTION;
	}

	public enum LogTitle {
		CMD, NONE, SPELL, PET, SPAWN, QUEST;

		@Override
		public String toString() {
			if (this.equals(NONE))
				return "";
			else
				return this.name();
		}
	}

	private static List<String> log = new ArrayList<String>();

	public static void save() {
		if (!RolecraftConfig.isDebugging())
			return;
		log.add("Saving...");
		String text = "";
		for (String key : log) {
			text += key + "\n";
		}
		File file = new File(FilePath.log);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(text);
			writer.close();
			log.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exception(String msg) {
		exception(LogTitle.NONE, msg);
	}

	public static void exception(LogTitle title, String msg) {
		log(LogLevel.EXCEPTION, title, msg);
	}

	public static void error(LogTitle title, String msg) {
		log(LogLevel.ERROR, title, msg);
	}

	public static void warning(LogTitle title, String msg) {
		log(LogLevel.WARNING, title, msg);
	}

	public static void info(LogTitle title, String msg) {
		log(LogLevel.INFO, title, msg);
	}

	public static void exception(String msg, String... args) {
		log(LogLevel.EXCEPTION, LogTitle.NONE, msg, args);
	}

	public static void error(LogTitle title, String msg, String... args) {
		log(LogLevel.ERROR, title, msg, args);
	}

	public static void warning(LogTitle title, String msg, String... args) {
		log(LogLevel.WARNING, title, msg, args);
	}

	public static void info(LogTitle title, String msg, String... args) {
		log(LogLevel.INFO, title, msg, args);
	}

	private static void log(LogLevel level, LogTitle title, String msg) {
		if (!RolecraftConfig.isDebugging())
			return;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		dateFormat.format(date);
		log.add(level.toString() + "(" + dateFormat.format(date) + ")> " + title.toString() + "\n" + msg);
		if (log.size() >= 1024)
			Manager.scheduleTask(new Runnable() {

				@Override
				public void run() {
					save();
				}
			});
	}

	private static void log(LogLevel info, LogTitle cmd, String msg, String... args) {
		int c = 0;
		while (c < args.length && msg.contains("%s")) {
			if (args[c] == null)
				args[c] = "";
			msg.replaceFirst("%s", args[c]);
			c++;
		}
		for (int i = c; i < args.length; i++) {
			if (args[c] == null)
				args[c] = "";
			msg += args[i];
		}
		log(info, cmd, msg);
	}
}
