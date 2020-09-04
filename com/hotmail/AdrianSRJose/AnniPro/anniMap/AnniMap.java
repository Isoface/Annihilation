package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;

public abstract class AnniMap {
	private String world;
	private Areas areas;
	private Signs signs;

	protected final @Getter File configFile;
	private final YamlConfiguration config;

	public AnniMap(String worldName, File configFile, boolean loadOnConstructor) {
		this.configFile = configFile;
		com.hotmail.AdrianSRJose.AnniPro.utils.Util.tryCreateFile(configFile);
		config = YamlConfiguration.loadConfiguration(configFile);
		//
		world = worldName;
		areas = new Areas(worldName);
		signs = new Signs();
		//
		String world = config.getString("WorldName");
		if (world != null) {
			world = Util.getVerifyUniversalPCAddres(world);
			this.world = world;
		}
		//
		//
		ConfigurationSection areaSec = config.getConfigurationSection("Areas");
		if (areaSec != null)
			areas.loadAreas(areaSec);
		//
		ConfigurationSection signSec = config.getConfigurationSection("Signs");
		if (signSec != null)
			signs = new Signs(signSec);
		//
		//
		if (loadOnConstructor)
			loadFromConfig(config);
	}

	// ------------------------------------------------------------------
	protected abstract void loadFromConfig(ConfigurationSection section);

	protected abstract int saveToConfig(ConfigurationSection section);
	// ------------------------------------------------------------------

	public YamlConfiguration getConfig() {
		return config;
	}

	public void saveConfig() {
		if (config != null) {
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void registerListeners(Plugin plugin) {
		areas.registerListener(plugin);
		signs.registerListener(plugin);
	}

	public void unregisterListeners() {
		areas.unregisterListener();
		signs.unregisterListener();
	}

	public Areas getAreas() {
		return areas;
	}

	public Signs getSigns() {
		return signs;
	}

	public String getWorldName() {
		return world;
	}

	public World getWorld() {
		return getWorldName() != null ? Bukkit.getWorld(getWorldName()) : null;
	}

	public String getNiceWorldName() {
		return this.toString();
	}

	public String getNiceBossWorldName() {
		return this.toStringBoss();
	}

	public String toStringBossMapWorldName() {
		String str = GameBoss.getNiceBossWorldName(world);
		return str == null ? this.getWorldName() : str;
	}

	@Override
	public String toString() {
		String str = Game.getNiceWorldName(world);
		return str == null ? this.getWorldName() : str;
	}

	public String toStringBoss() {
		String str = GameBoss.getNiceBossWorldName(world);
		return str == null ? this.getWorldName() : str;
	}

	public void setWorldName(String name) {
		world = name;
	}

	// save += Util.setUpdatedIfNotEqual(section, "patch", value);
	public void saveToConfig(boolean saveAreas, boolean saveSings) {
		int save = 0;
		//
		save += Util.setUpdatedIfNotEqual(config, "WorldName", getWorldName());
		//
		if (saveAreas) {
			save += Util.createSectionIfNoExitsInt(config, "Areas");
			save += areas.saveToConfig(config.getConfigurationSection("Areas"));
		}
		//
		if (saveSings) {
			save += Util.createSectionIfNoExitsInt(config, "Signs");
			save += signs.saveToConfig(config.getConfigurationSection("Signs"));
		}
		//
		save += saveToConfig(config);
		//
		if (save > 0)
			saveConfig();
	}
}
