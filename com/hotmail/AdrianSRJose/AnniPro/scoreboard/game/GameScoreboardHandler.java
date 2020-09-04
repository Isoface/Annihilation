package com.hotmail.AdrianSRJose.AnniPro.scoreboard.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.scoreboard.CustomScoreboard;
import com.hotmail.AdrianSR.core.util.PlayerUtils;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.ScoreboardHandler;
import com.hotmail.AdrianSRJose.AnniPro.utils.DateUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 09 July, 2020 / 05:30 PM
 */
public class GameScoreboardHandler extends ScoreboardHandler {
	
	public static final String CURRENT_DATE_KEY = "{CURRENT_DATE}";
	public static final String          MAP_KEY = "{MAP}";
	public static final String    RED_TEAM_LINE_KEY = "{RED_TEAM_LINE}";
	public static final String   BLUE_TEAM_LINE_KEY = "{BLUE_TEAM_LINE}";
	public static final String  GREEN_TEAM_LINE_KEY = "{GREEN_TEAM_LINE}";
	public static final String YELLOW_TEAM_LINE_KEY = "{YELLOW_TEAM_LINE}";
	public static final String    TEAM_NAME_KEY = "{TEAM_NAME}";
	public static final String  TEAM_HEALTH_KEY = "{TEAM_HEALTH}";
	public static final String   TEAM_COLOR_KEY = "{TEAM_COLOR}";
	public static final String        PHASE_KEY = "{PHASE}";
	public static final String   TIME_LEFT_KEY = "{TIME_LEFT}";
	public static final String PLAYING_TIME_KEY = "{PLAYING_TIME}";
	
	public static GameScoreboardHandler getInstance ( ) {
		return (GameScoreboardHandler) MANAGER_INSTANCES.get ( GameScoreboardHandler.class );
	}
	
	/** scoreboards map */
	protected final Map < UUID , CustomScoreboard > scoreboards = new HashMap < > ( );
	
	/** scoreboards configuration */
	protected final GameScoreboardConfiguration configuration;
	
	public GameScoreboardHandler ( AnnihilationMain plugin ) {
		super ( plugin );
		this.configuration = new GameScoreboardConfiguration ( 
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + ChatColor.UNDERLINE + "ANNIHILATION" , 
				CustomScoreboard.WHITESPACE_INDICATOR ,
				ChatColor.GRAY + CURRENT_DATE_KEY ,
				CustomScoreboard.WHITESPACE_INDICATOR ,
				ChatColor.GRAY + "Map: " + ChatColor.GREEN + MAP_KEY ,
				CustomScoreboard.WHITESPACE_INDICATOR , 
				RED_TEAM_LINE_KEY ,
				BLUE_TEAM_LINE_KEY , 
				GREEN_TEAM_LINE_KEY , 
				YELLOW_TEAM_LINE_KEY ,
				CustomScoreboard.WHITESPACE_INDICATOR ,
				ChatColor.GRAY + "Current phase: " + ChatColor.GREEN + PHASE_KEY , 
				ChatColor.GRAY + "Next phase in: " + ChatColor.GREEN + TIME_LEFT_KEY ,
				CustomScoreboard.WHITESPACE_INDICATOR ,
				ChatColor.GOLD.toString ( ) + ChatColor.BOLD + "www.SpigotMC.org" );
	}

	@Override
	public GameScoreboardConfiguration getConfiguration ( ) {
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
						.replace ( CURRENT_DATE_KEY , DateUtil.getFormattedCurrentDate ( ) )
						.replace ( MAP_KEY , Game.getGameMap ( ).getNiceWorldName ( ) )
						.replace ( RED_TEAM_LINE_KEY , format ( AnniTeam.Red ) )
						.replace ( BLUE_TEAM_LINE_KEY , format ( AnniTeam.Blue ) )
						.replace ( GREEN_TEAM_LINE_KEY , format ( AnniTeam.Green ) )
						.replace ( YELLOW_TEAM_LINE_KEY , format ( AnniTeam.Yellow ) )
						.replace ( PHASE_KEY , String.valueOf ( Game.getGameMap ( ).getCurrentPhase ( ) ) )
						.replace ( TIME_LEFT_KEY , AnnounceBar.format ( AnnounceBar.getInstance ( ).getTimeLeft ( ) ) )
						.replace ( PLAYING_TIME_KEY , Game.getPlayingTimeMessage ( ) );
			}
		}
		
		scoreboard.addAll ( elements );
		scoreboard.update ( );
		return scoreboard;
	}
	
	public String format ( AnniTeam team ) {
		return getConfiguration ( ).getTeamLineFormat ( )
				.replace ( TEAM_COLOR_KEY  , team.getColor ( ).toString ( ) )
				.replace ( TEAM_NAME_KEY   , team.getExternalName ( ) )
				.replace ( TEAM_HEALTH_KEY , String.valueOf ( team.getHealth ( ) ) );
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