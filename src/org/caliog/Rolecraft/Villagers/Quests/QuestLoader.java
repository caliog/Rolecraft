package org.caliog.Rolecraft.Villagers.Quests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;

public class QuestLoader {

	protected static ClassLoader classLoader;
	private static Set<String> paths = new HashSet<String>();

	public static void init() throws IOException {
		File dir = new File(FilePath.quests);

		List<URL> urls = new ArrayList<URL>();
		for (String file : dir.list()) {
			if (file.endsWith(".jar")) {
				File f = new File(dir, file);

				JarFile jar = new JarFile(f);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry e = entries.nextElement();
					if (e.getName().equalsIgnoreCase("quest.info")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(e)));
						String line;
						while ((line = reader.readLine()) != null) {
							paths.add(line.replaceAll(".java", ""));
						}

					}
				}
				urls.add(f.toURI().toURL());
				jar.close();

			}
		}

		classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), Manager.plugin.getClass().getClassLoader());

	}

	public static Quest load(String name) {
		String mainC = null;
		if (name == null)
			return null;
		for (String path : paths) {
			if (path.endsWith(name))
				mainC = path;
		}
		if (mainC == null)
			return null;
		try {
			Class<?> c = classLoader.loadClass(mainC);
			Class<? extends Quest> questC = c.asSubclass(Quest.class);
			Quest quest = questC.getConstructor("".getClass()).newInstance(name);
			return quest;
		} catch (Exception e) {
			Debugger.exception("Failed to load Quest in QuestLoader.java (name=%s)", name);
			Manager.plugin.getLogger().warning("Failed to load Quest: " + name);
			e.printStackTrace();

		}
		return null;
	}

	public static boolean isJarQuest(String name) {
		for (String p : paths)
			if (p.endsWith(name))
				return true;
		return false;
	}

	public static boolean isYmlQuest(String name) {
		return new File(FilePath.quests + name + ".yml").exists();
	}

	public static Quest loadYMLQuest(String name) {
		return new YmlQuest(name);
	}
}
