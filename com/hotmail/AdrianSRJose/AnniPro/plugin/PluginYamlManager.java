package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSRJose.AnniPro.utils.Utf8YamlConfiguration;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;

public class PluginYamlManager 
{
	private final @Getter AnniPlugin   plugin;
	private final @Getter File           file;
	private final @Getter boolean       uft_8;
	private YamlConfiguration         current;
	
	public PluginYamlManager(final AnniPlugin plugin, boolean UFT_8) {
		this.plugin = plugin;
		this.file   = plugin.getConfigFile();
		this.uft_8  = UFT_8;
	}
	
	public void checkFile() {
		// check file
		Validate.notNull(file, "the plugin .yml file cannot be null!");
		
		// create is not exists
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public YamlConfiguration getYamlConfiguration() {
		// check file
		checkFile();
		
		// check yaml
		if (current == null) {
			if (uft_8) {
				current = Utf8YamlConfiguration.loadConfiguration(file);
			} else {
				current = YamlConfiguration.loadConfiguration(file);
			}
		}
		return current;
	}
	
	public int setDefaultIfNotSet(final ConfigurationSection section, final String path, final Object value) {
		return Util.setDefaultIfNotSet(getYamlConfiguration().getConfigurationSection(section.getName()), path, value);
	}
	
	public int createSectionIfNoExits(final String name) {
		return Util.createSectionIfNoExitsInt(getYamlConfiguration(), name);
	}
	
	public int createSectionIfNoExits(final ConfigurationSection father, final String name) {
		return Util.createSectionIfNoExitsInt(father, name);
	}
	
	public ConfigurationSection getConfigurationSection(String nameOrPath) {
		return getYamlConfiguration().getConfigurationSection(nameOrPath);
	}
}
