package org.caliog.Rolecraft.Villagers.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class DataSaver {

	private static File f = new File(FilePath.villagerDataFile);

	private static YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

	public static long save(Object s) {
		if (s == null)
			return 0;
		long id = 1;
		while (config.isSet(String.valueOf(id)))
			id++;

		config.set(String.valueOf(id), s);

		return id;
	}

	public static void save() {
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getStringList(String id) {
		if (Integer.valueOf(id) == 0)
			return null;
		List<String> r = config.getStringList(id);
		if (r == null)
			return new ArrayList<String>();
		return r;
	}

	public static ItemStack getItem(String id) {
		if (Integer.valueOf(id) == 0)
			return null;
		ItemStack stack = config.getItemStack(id);
		if (stack == null)
			return null;
		if (stack.getAmount() < 1)
			stack.setAmount(1);
		return stack;
	}

	public static void clean() throws IOException {
		f.delete();
		f.createNewFile();
		config = YamlConfiguration.loadConfiguration(f);
	}

	public static String getString(String id) {
		if (Integer.valueOf(id) == 0)
			return null;
		if (id == null)
			return null;
		else
			return config.getString(id);
	}
}
