package org.caliog.Rolecraft.Spells;

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
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class SpellLoader {

	protected static ClassLoader classLoader;
	private static Set<String> paths = new HashSet<String>();

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
							paths.add(line.replaceAll(".java", ""));
						}

					}
				}
				urls.add(f.toURI().toURL());
				jar.close();

			}
		}

		// defaults
		paths.add("org.caliog.Rolecraft.Spells.SpeedSpell");
		paths.add("org.caliog.Rolecraft.Spells.InvisibleSpell");
		classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), Manager.plugin.getClass().getClassLoader());

	}

	public static Spell load(RolecraftPlayer player, String name) {
		String mainC = null;
		if (name == null)
			return null;
		if ((name.equals("Speed") || name.equals("Invisible")) && !name.endsWith("Spell"))
			name += "Spell";
		for (String path : paths) {
			if (path.endsWith(name))
				mainC = path;
		}
		if (mainC == null)
			return null;
		try {
			Class<?> c = classLoader.loadClass(mainC);
			Class<? extends Spell> spellC = c.asSubclass(Spell.class);
			Spell spell = spellC.getConstructor(player.getClass()).newInstance(player);
			return spell;
		} catch (Exception e) {
			Manager.plugin.getLogger().warning("Failed to load Spell: " + name);
			e.printStackTrace();

		}
		return null;
	}
}
