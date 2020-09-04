package com.hotmail.AdrianSRJose.AnniPro.scoreboard.lobby;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSR.core.manager.CustomPluginManager;
import com.hotmail.AdrianSR.core.util.PrintUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 10 July, 2020 / 02:07 PM
 */
public class LobbyScoreboardConfigurationHandler extends CustomPluginManager {
	
	public static final String CONFIGURATION_FILE_NAME = "AnniLobbyScoreboardConfig.yml";
	
	public static LobbyScoreboardConfigurationHandler getInstance ( ) {
		return (LobbyScoreboardConfigurationHandler) MANAGER_INSTANCES.get ( LobbyScoreboardConfigurationHandler.class );
	}

	public LobbyScoreboardConfigurationHandler ( AnnihilationMain plugin ) {
		super ( plugin ); loadConfiguration ( );
	}
	
	public void loadConfiguration ( ) {
		File file = new File ( plugin.getDataFolder ( ) , CONFIGURATION_FILE_NAME );
		
		// we suppose the lobby scoreboard handler is already initialized at this point,
		// then we are going to load its configuration from its corresponding .yml
		LobbyScoreboardHandler handler = LobbyScoreboardHandler.getInstance ( );
		
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