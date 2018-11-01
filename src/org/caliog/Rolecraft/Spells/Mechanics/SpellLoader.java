package org.caliog.Rolecraft.Spells.Mechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;

public class SpellLoader {

	protected static ClassLoader classLoader;
	private static Set<String> paths = new HashSet<String>();

	public final static HashMap<String, String> spellIdName = new HashMap<String, String>();

	public static void init() throws IOException {
		File dir = new File(FilePath.spells);

		List<URL> urls = new ArrayList<URL>();
		for (String file : dir.list()) {
			if (file.endsWith(".jar")) {
				File f = new File(dir, file);

				JarFile jar = new JarFile(f);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry e = entries.nextElement();
					if (e.getName().equalsIgnoreCase("spell.info")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(e)));
						String line;
						while ((line = reader.readLine()) != null) {
							if (!line.startsWith("version:"))
								paths.add(line.replaceAll(".java", ""));
						}

					}
				}
				urls.add(f.toURI().toURL());
				jar.close();

			} else if (file.endsWith(".yml")) {
				YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(FilePath.spells + file));
				if (c.isSet("name"))
					spellIdName.put(new File(file).getName().replaceAll(".yml", ""), c.getString("name", null));

			}
		}
		for (String s : paths) {
			String[] split = s.split("\\.");
			if (split.length > 1)
				copySpellFile(split[split.length - 1]);
		}

		// defaults
		paths.add("org.caliog.Rolecraft.Spells.SpeedSpell");
		paths.add("org.caliog.Rolecraft.Spells.InvisibleSpell");
		paths.add("org.caliog.Rolecraft.Spells.Curse");
		classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
				Manager.plugin.getClass().getClassLoader());

	}

	// name is identifier
	public static Spell load(RolecraftPlayer player, String name) {
		String mainC = null;
		if (name == null)
			return null;

		for (String key : spellIdName.keySet())
			if (spellIdName.get(key).equals(name)) {
				name = key;
				break;
			}

		if ((name.equals("Speed") || name.equals("Invisible")) && !name.endsWith("Spell"))
			name += "Spell";
		for (String path : paths) {
			if (path.endsWith(name))
				mainC = path;
		}
		final boolean isCurse = isCurse(name);
		if (isCurse)
			mainC = "org.caliog.Rolecraft.Spells.Curse";

		if (mainC == null)
			return null;
		try {
			Class<?> c = classLoader.loadClass(mainC);
			Class<? extends Spell> spellC = c.asSubclass(Spell.class);
			Spell spell;
			if (isCurse) {
				spell = spellC.getConstructor(player.getClass(), String.class).newInstance(player, name);
			} else {
				spell = spellC.getConstructor(player.getClass()).newInstance(player);
			}
			return spell;
		} catch (Exception e) {
			Manager.plugin.getLogger().warning("Failed to load Spell: " + name);
			Debugger.exception("Failed to load Spell:", name);
			e.printStackTrace();
		}
		return null;
	}

	private static void copySpellFile(String name) {
		try {
			InputStream s = new FileCreator().getClass().getResourceAsStream("Spells/" + name + ".yml");
			if (s == null)
				return;
			File outputFile = new File(FilePath.spells + name + ".yml");
			if (outputFile.exists()) {
				BufferedReader r = new BufferedReader(new FileReader(outputFile));
				if (r.readLine() != null) {
					r.close();
					return;
				}
				r.close();
			} else
				outputFile.createNewFile();

			OutputStream o = new FileOutputStream(outputFile);
			FileCreator.copyFile(s, o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isCurse(String name) {
		File f = new File(FilePath.spells + name + ".yml");
		if (f.exists()) {
			YamlConfiguration spells = YamlConfiguration.loadConfiguration(f);
			return spells.isConfigurationSection("curse");
		}
		return false;
	}

	public static String getIdentifier(String name) {
		for (String key : spellIdName.keySet())
			if (spellIdName.get(key).equals(name)) {
				return key;
			}
		return name;
	}
}
