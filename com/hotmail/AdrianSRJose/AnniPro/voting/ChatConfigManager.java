package com.hotmail.AdrianSRJose.AnniPro.voting;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class ChatConfigManager {
	private static YamlConfiguration mainConfig = null;
	private static File mainConfigFile = null;

	public static void load(Plugin p) {
		mainConfigFile = new File(p.getDataFolder().getAbsolutePath());
		if (!mainConfigFile.exists() || !mainConfigFile.isDirectory())
			mainConfigFile.mkdir();
		mainConfigFile = new File(p.getDataFolder().getAbsolutePath(), "AnniChatConfig.yml");
		try {
			if (!mainConfigFile.exists())
				Util.saveResource(AnnihilationMain.INSTANCE, "AnniChatConfig.yml");// mainConfigFile.createNewFile();
			mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);

			int save = 0;
			
			save += Util.setDefaultIfNotSet(mainConfig, "Split-kit-name", true);

			save += setDefaultIfNotSet(mainConfig, "Variable-Global-Message-Format", "&7(Global) ");

			save += setDefaultIfNotSet(mainConfig, "Variable-Team-Message-Format", "&7(Team) ");

			save += setDefaultIfNotSet(mainConfig, "Variable-On-Lobby-Format", "[&5Lobby&7] ");

			save += setDefaultIfNotSet(mainConfig, "Variable-On-Team-Format", "[%team&7] ");

			save += setDefaultIfNotSet(mainConfig, "Format", "{VARIABLES} {PREFIX} &r&7{PLAYER_NAME}&r&f: {MESSAGE}");

			if (save > 0)
				saveMainConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int setDefaultIfNotSet(ConfigurationSection section, String path, String str) {
		if (section != null) {
			if (!section.isSet(path)) {
				section.set(path, str);
				return 1;
			}
		}
		return 0;
	}

	public static YamlConfiguration getConfig() {
		return mainConfig;
	}

	public static void saveMainConfig() {
		if (mainConfig != null) {
			try {
				mainConfig.save(mainConfigFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
