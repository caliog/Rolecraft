package org.caliog.Rolecraft.XMechanics.Logging;

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

public class LOG {

	public enum LogLevel {
		INFO, WARNING, ERROR, EXCEPTION;
	}

	public enum LogTitle {
		CMD, NONE, SPELL, PET, SPAWN;

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
		if (!RolecraftConfig.isLOGEnabled())
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

	public static void log(LogLevel level, LogTitle title, String msg) {
		if (!RolecraftConfig.isLOGEnabled())
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

	public static void log(LogLevel info, LogTitle cmd, String string, String... args) {
		for (String a : args)
			string += " " + a;
		log(info, cmd, string);
	}
}
