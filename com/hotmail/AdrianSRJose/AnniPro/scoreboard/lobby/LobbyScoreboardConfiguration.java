package com.hotmail.AdrianSRJose.AnniPro.scoreboard.lobby;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.ScoreboardConfiguration;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 09 July, 2020 / 11:40 PM
 */
public class LobbyScoreboardConfiguration extends ScoreboardConfiguration {
	
	public static final String     VOTING_MAP_FORMAT_KEY = "voting-map-format";
	public static final String DEFAULT_VOTING_MAP_FORMAT = ChatColor.GOLD.toString ( ) 
			+ ChatColor.BOLD + LobbyScoreboardHandler.MAP_NAME_KEY 
			+ ChatColor.GREEN + " " + LobbyScoreboardHandler.MAP_SCORE_KEY;
	
	protected String voting_map_format = DEFAULT_VOTING_MAP_FORMAT;

	public LobbyScoreboardConfiguration ( String name , String... elements ) {
		super ( name , elements );
	}

	public LobbyScoreboardConfiguration ( String name , String [ ] elements, String [ ] default_elements ) {
		super ( name , elements , default_elements );
	}
	
	@Override
	protected String header ( ) {
		return  "---------------------------- Annihilation Lobby Scoreboard ---------------------------- #\r\n" +
				"This is the lobby scoreboard configuration file.\r\n" +
				LobbyScoreboardHandler.CURRENT_DATE_KEY + " will be replaced with the current date.\r\n" +
				LobbyScoreboardHandler.MAPS_KEY + " will be replaced with the voting maps, using the format of 'voting-map-format'.\r\n" +
				LobbyScoreboardHandler.ONLINE_PLAYERS_KEY + " will be replaced with the count of online players.\r\n" +
				LobbyScoreboardHandler.REQUIRED_PLAYERS_KEY + " will be replaced with the number of required players to start the game.\r\n" +
				"";
	}
	
	public String getVotingMapFormat ( ) {
		return voting_map_format;
	}
	
	public void setVotingMapFormat ( String format ) {
		this.voting_map_format = format;
	}
	
	@Override
	public boolean saveDefaults ( ConfigurationSection section ) {
		boolean changed = YmlUtils.setNotSet ( section , VOTING_MAP_FORMAT_KEY , 
				TextUtils.untranslateColors ( DEFAULT_VOTING_MAP_FORMAT ) ) > 0;
		boolean  supper = super.saveDefaults ( section );
		
		return supper || changed;
	}
	
	@Override
	public LobbyScoreboardConfiguration load ( ConfigurationSection section ) {
		super.load ( section );
		
		// voting map format
		setVotingMapFormat ( section.getString ( VOTING_MAP_FORMAT_KEY , DEFAULT_VOTING_MAP_FORMAT ) );
		return this;
	}
}