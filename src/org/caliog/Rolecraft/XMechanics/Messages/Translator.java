package org.caliog.Rolecraft.XMechanics.Messages;

import java.io.InputStream;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;

public class Translator {

	public static final Translator o = new Translator();
	public static String lang_code = "en";

	@SuppressWarnings("deprecation")
	public static void init() {
		InputStream stream = new FileCreator().getClass().getResourceAsStream("lang/phrases.yml");
		if (stream == null) {
			return;
		}

		YamlConfiguration phrases = YamlConfiguration.loadConfiguration(stream);
		for (String name : phrases.getKeys(false)) {
			try {
				Phrase p = Phrase.valueOf(name);
				ConfigurationSection section = phrases.getConfigurationSection(name);
				for (String lc : section.getKeys(false)) {
					try {
						cc k = cc.valueOf(lc.toUpperCase());
						p.translations.put(k, section.getString(lc));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public enum cc {
		EN, DE;
	}

	public class Trans {

		public final cc lang_code;
		public final String text;

		public Trans(cc lang_code, String text) {
			this.lang_code = lang_code;
			this.text = text;
		}
	}

	public enum Phrase {
		// @formatter:off
		AMOUNT,
		MOB, 
		REQUIRED_CLASS,
		MINIMUM_LEVEL,
		EXPERIENCE, 
		ACCEPT, 
		BACK, 
		ITEMS, 
		START,
		REWARD, 
		COLLECT,
		WANTED_MOBS;
		// @formatter:on
		public final HashMap<cc, String> translations = new HashMap<cc, String>();

		public String translate() {
			try {
				cc code = cc.valueOf(lang_code.toUpperCase());
				return translations.get(code);
			} catch (Exception e) {
				return translations.get(cc.EN);
			}
		}
	}

}
