package org.caliog.Rolecraft.XMechanics.Messages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;

public class Translator {

	public static final Translator o = new Translator();
	public static String lang_code = "en";

	public static void init() {
		InputStream stream = new FileCreator().getClass().getResourceAsStream("lang/phrases.yml");
		if (stream == null) {
			return;
		}

		// try to translate
		// TODO bad code with try/catch
		try {
			YamlConfiguration phrases = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(stream, "UTF-8")));
			for (String k : phrases.getKeys(false)) {
				if (!phrases.isString(k + "." + lang_code)) {
					String translation = TransUtil.getTranslation(phrases.getString(k + ".en"), lang_code);
					phrases.set(k + "." + lang_code, translation);
				}
			}

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum cc {
		EN, DE, FR, RU, ES;
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
		WANTED_MOBS, 
		LEFT,
		RIGHT;
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
