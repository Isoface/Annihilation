package com.hotmail.AdrianSRJose.AnniPro.utils;

import org.bukkit.configuration.ConfigurationSection;

public interface YamlSaveable {

	/**
	 * Save to a section.
	 * <p>
	 * @param section the section to save in.
	 * @return the amount of made changes.
	 */
	public int save(ConfigurationSection section);
	
	/**
	 * Save to a section, only if this
	 * has been not already saved.
	 * <p>
	 * @param section the section to save in.
	 * @return the amount of made changes.
	 */
	public int saveNotSet(ConfigurationSection section);
}
