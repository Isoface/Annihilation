package com.hotmail.AdrianSRJose.AnniPro.scoreboard.game;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.ScoreboardConfiguration;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 09 July, 2020 / 05:10 PM
 */
public class GameScoreboardConfiguration extends ScoreboardConfiguration {
	
	public static final String     TEAM_LINE_FORMAT_KEY = "team-line-format";
	public static final String DEFAULT_TEAM_LINE_FORMAT = "[" + '\u06e9' + "] " + GameScoreboardHandler.TEAM_COLOR_KEY 
			+ GameScoreboardHandler.TEAM_NAME_KEY + ": " + ChatColor.GREEN + GameScoreboardHandler.TEAM_HEALTH_KEY;

	protected String team_line_format = DEFAULT_TEAM_LINE_FORMAT;
	
	public GameScoreboardConfiguration ( String name , String... elements ) {
		super ( name , elements );
	}

	public GameScoreboardConfiguration ( String name , String [ ] elements , String [ ] default_elements ) {
		super ( name , elements , default_elements );
	}
	
	@Override
	protected String header ( ) {
		return "";
	}
	
	public String getTeamLineFormat ( ) {
		return team_line_format;
	}
	
	public void setTeamLineFormat ( String format ) {
		this.team_line_format = format;
	}
	
	@Override
	public boolean saveDefaults ( ConfigurationSection section ) {
		boolean changed = YmlUtils.setNotSet ( section , TEAM_LINE_FORMAT_KEY , 
				TextUtils.untranslateColors ( DEFAULT_TEAM_LINE_FORMAT  ) ) > 0;
		boolean supper = super.saveDefaults ( section );
		
		return changed || supper;
	}
	
	@Override
	public GameScoreboardConfiguration load ( ConfigurationSection section ) {
		super.load ( section );
		
		// team line format
		setTeamLineFormat ( section.getString ( TEAM_LINE_FORMAT_KEY , DEFAULT_TEAM_LINE_FORMAT ) );
		return this;
	}
}