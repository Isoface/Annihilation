package com.hotmail.AdrianSRJose.AnniPro.scoreboard.game;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.manager.CustomPluginManager;
import com.hotmail.AdrianSR.core.util.PrintUtils;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Saturday 11 July, 2020 / 12:17 PM
 */
public class GameScoreboardConfigurationHandler extends CustomPluginManager {

	public static final String CONFIGURATION_FILE_NAME = "AnniGameScoreboardConfig.yml";
	
	public static GameScoreboardConfigurationHandler getInstance ( ) {
		return (GameScoreboardConfigurationHandler) MANAGER_INSTANCES.get ( GameScoreboardConfigurationHandler.class );
	}
	
	public GameScoreboardConfigurationHandler ( CustomPlugin plugin ) {
		super ( plugin ); loadConfiguration ( );
	}
	
	public void loadConfiguration ( ) {
		File file = new File ( plugin.getDataFolder ( ) , CONFIGURATION_FILE_NAME );
		
		// we suppose the game scoreboard handler is already initialized at this point,
		// then we are going to load its configuration from its corresponding .yml
		GameScoreboardHandler handler = GameScoreboardHandler.getInstance ( );
				
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( file );
		if ( handler.getConfiguration ( ).saveDefaults ( yaml ) ) {
			try {
				yaml.save ( file );
			} catch ( IOException ex ) {
				PrintUtils.print ( ChatColor.RED , "file " + CONFIGURATION_FILE_NAME 
						+ " couldn't be loaded correctly: " , plugin );
				ex.printStackTrace ( );
				Bukkit.getPluginManager ( ).disablePlugin ( plugin );
				return;
			}
		}
				
		handler.getConfiguration ( ).load ( yaml );
	}
}