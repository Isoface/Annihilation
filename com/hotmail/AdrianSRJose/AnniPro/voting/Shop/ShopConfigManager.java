package com.hotmail.AdrianSRJose.AnniPro.voting.Shop;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopType;

public class ShopConfigManager {
	
	/**
	 * The Shop configuration {@link File}.
	 */
	private static File CONFIG_FILE = null;
	
	/**
	 * The shop {@link YamlConfiguration}.
	 */
	private static YamlConfiguration CONFIG_YAML = null;
	
	/***
	 * Save default shop configuration
	 * if not set.
	 * <p>
	 * @param plugin the plugin instance.
	 */
	public static void load(Plugin plugin) {
		ShopConfigManager.CONFIG_FILE = new File(plugin.getDataFolder().getAbsolutePath());
		if (!ShopConfigManager.CONFIG_FILE.exists() || !ShopConfigManager.CONFIG_FILE.isDirectory()) {
			ShopConfigManager.CONFIG_FILE.mkdir();
		}
		
		ShopConfigManager.CONFIG_FILE = new File(ShopConfigManager.CONFIG_FILE, ShopVars.SHOP_CONFIGURATION_FILE_NAME);
		try {
			/* load yaml configuration */
			if (!ShopConfigManager.CONFIG_FILE.exists()) {
				ShopConfigManager.CONFIG_FILE.createNewFile();
			}
			
			int             save_changes = 0;
			ShopConfigManager.CONFIG_YAML = YamlConfiguration.loadConfiguration(CONFIG_FILE);
			
			/* configuration header */
			String header_keys = "";
			for (ShopType type : ShopType.values()) {
				header_keys += ( type.name() + ", " );
			}
			ShopConfigManager.CONFIG_YAML.options().header("Shop Menus: [" + header_keys.substring(0, header_keys.lastIndexOf(", ")) + "]");
			
			
			/* load base configuration section */
			ConfigurationSection base = ShopConfigManager.CONFIG_YAML.getConfigurationSection(ShopVars.SHOP_BASE_CONFIGURATION_SECTION_NAME);
			if (base == null) {
				base         = ShopConfigManager.CONFIG_YAML.createSection(ShopVars.SHOP_BASE_CONFIGURATION_SECTION_NAME);
				save_changes ++;
			}
			
			/* save default lore */
			if (!base.isSet(ShopVars.SHOP_CONFIGURATION_CONTENTS_LORE_SECTION_NAME)) {
				base.set(ShopVars.SHOP_CONFIGURATION_CONTENTS_LORE_SECTION_NAME, ShopType.CONTENTS_LORE);
				save_changes ++;
			}
			
			/* save default contents */
			for (ShopType type : ShopType.values()) {
				ConfigurationSection shop_cs = base.getConfigurationSection(type.getConfigurationSectionName());
				if (shop_cs == null) {
					shop_cs      = base.createSection(type.getConfigurationSectionName());
					save_changes ++;
				}
				
				/* save menu core default values */
				if ( !shop_cs.isSet( ShopVars.SHOP_CONFIGURATION_MENU_NAME_KEY ) ) { /* saving default menu name */
					shop_cs.set( ShopVars.SHOP_CONFIGURATION_MENU_NAME_KEY , Util.untranslateAlternateColorCodes(type.getDefaultMenuName()) );
					save_changes ++;
				}
				
				if ( !shop_cs.isSet( ShopVars.SHOP_CONFIGURATION_MENU_SIZE_KEY ) ) { /* saving default menu size */
					shop_cs.set( ShopVars.SHOP_CONFIGURATION_MENU_SIZE_KEY , type.getDefaultMenuSize().getSize() );
					save_changes ++;
				}
				
				/* save default contents */
				ConfigurationSection contents_cs = shop_cs.getConfigurationSection( ShopVars.SHOP_CONFIGURATION_CONTENTS_SECTION_NAME );
				if ( contents_cs == null ) {
					contents_cs   = shop_cs.createSection( ShopVars.SHOP_CONFIGURATION_CONTENTS_SECTION_NAME );
					for (int i = 0; i < type.getDefaultContents().size(); i++) {
						type.getDefaultContents().get(i).save(contents_cs.createSection(String.valueOf( i )));
					}
					save_changes ++;
				}
			}
			
			/* save configuration changes */
			if (save_changes > 0) {
				ShopConfigManager.saveConfiguration();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The shop {@link YamlConfiguration}.
	 */
	public static YamlConfiguration getConfiguration() {
		return ShopConfigManager.CONFIG_YAML;
	}

	/**
	 * Save shop configuration.
	 */
	public static void saveConfiguration() {
		if (ShopConfigManager.getConfiguration() != null) {
			try {
				ShopConfigManager.getConfiguration().save(ShopConfigManager.CONFIG_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}