package com.hotmail.AdrianSRJose.AnniPro.scoreboard.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.scoreboard.CustomScoreboard;
import com.hotmail.AdrianSR.core.util.PlayerUtils;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.ScoreboardHandler;
import com.hotmail.AdrianSRJose.AnniPro.utils.DateUtil;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting.VotingMap;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 09 July, 2020 / 11:41 PM
 */
public class LobbyScoreboardHandler extends ScoreboardHandler {
	
	public static final String     CURRENT_DATE_KEY = "{CURRENT_DATE}";
	public static final String         MAP_NAME_KEY = "{MAP_NAME}";
	public static final String        MAP_SCORE_KEY = "{MAP_SCORE}";
	public static final String             MAPS_KEY = "{MAPS}";
	public static final String   ONLINE_PLAYERS_KEY = "{ONLINE_PLAYERS}";
	public static final String REQUIRED_PLAYERS_KEY = "{REQUIRED_PLAYERS}";

	public static LobbyScoreboardHandler getInstance ( ) {
		return (LobbyScoreboardHandler) MANAGER_INSTANCES.get ( LobbyScoreboardHandler.class );
	}
	
	/** scoreboards map */
	protected final Map < UUID , CustomScoreboard > scoreboards = new HashMap < > ( );
	
	/** scoreboards configuration */
	protected final LobbyScoreboardConfiguration configuration;
	
	public LobbyScoreboardHandler ( AnnihilationMain plugin ) {
		super ( plugin );
		this.configuration = new LobbyScoreboardConfiguration ( 
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + "/vote [map name] to vote" , 
				CustomScoreboard.WHITESPACE_INDICATOR ,
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + "Maps: " ,
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + MAPS_KEY , 
				CustomScoreboard.WHITESPACE_INDICATOR , 
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + "Players: " ,
				ChatColor.GREEN.toString ( ) + ChatColor.BOLD + ONLINE_PLAYERS_KEY + "/" + ChatColor.GOLD + REQUIRED_PLAYERS_KEY ,
				CustomScoreboard.WHITESPACE_INDICATOR ,
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + "www.SpigotMC.org" );
	}

	@Override
	public LobbyScoreboardConfiguration getConfiguration ( ) {
		return configuration;
	}

	@Override
	public CustomScoreboard get ( AnniPlayer player ) {
		CustomScoreboard scoreboard = scoreboards.get ( player.getID ( ) );
		if ( scoreboard == null ) {
			scoreboard = new CustomScoreboard ( configuration.getName ( ) , configuration.getElements ( ) );
			scoreboard.addViewer ( player.getPlayer ( ) );
			
			scoreboards.put ( player.getID ( ) , scoreboard );
		}
		return scoreboard;
	}
	
	@Override
	public CustomScoreboard update ( AnniPlayer player ) {
		final Player               handle = player.getPlayer ( );
		final CustomScoreboard scoreboard = get ( player );
		if ( handle == null ) {
			return null;
		}
		
		if ( !scoreboard.getViewers ( ).contains ( handle ) ) {
			// we make sure the player is viewing the scoreboard
			scoreboard.addViewer ( handle );
		}
		
		/* placeholder replacing */
		String [ ] elements = Arrays.copyOfRange ( getConfiguration ( ).getElements ( ) , 0 , 
				getConfiguration ( ).getElements ( ).length );
		for ( int i = 0 ; i < elements.length ; i ++ ) {
			String element = elements [ i ];
			if ( element != null ) {
				elements [ i ] = element
						.replace ( CURRENT_DATE_KEY     , DateUtil.getFormattedCurrentDate ( ) )
						.replace ( ONLINE_PLAYERS_KEY   , String.valueOf ( Bukkit.getOnlinePlayers ( ).size ( ) ) )
						.replace ( REQUIRED_PLAYERS_KEY , String.valueOf ( Config.AUTO_START_PLAYERS_TO_START.toInt ( ) ) );
			}
		}
		
		// maps placeholder
		for ( int i = 0 ; i < elements.length ; i ++ ) {
			String element = elements [ i ];
			if ( element == null || !element.contains ( MAPS_KEY ) ) {
				continue;
			}
			
			if ( MapVoting.VOTING_MAPS.size ( ) > 0 ) {
				elements [ i ] = format ( MapVoting.VOTING_MAPS.get ( 0 ) );
				
				int index = i;
				if ( MapVoting.VOTING_MAPS.size ( ) > 1 ) {
					for ( int j = 1 ; j < MapVoting.VOTING_MAPS.size ( ) ; j ++ ) {
						elements = (String[]) ArrayUtils.add ( elements , index ++ , 
								format ( MapVoting.VOTING_MAPS.get ( j ) ) );
					}
				}
			}
			break;
		}
		
		scoreboard.addAll ( elements );
		scoreboard.update ( );
		return scoreboard;
	}
	
	protected String format ( VotingMap map ) {
		return getConfiguration ( ).getVotingMapFormat ( )
				.replace ( MAP_NAME_KEY , map.getName ( ) )
				.replace ( MAP_SCORE_KEY , String.valueOf ( map.getScore ( ) ) );
	}

	@Override
	public void run ( ) {
		AnniPlayer.getPlayers ( ).forEach ( player -> {
			if ( player != null && PlayerUtils.isValid ( player.getPlayer ( ) )  ) {
				update ( player );
			}
		});
	}
}