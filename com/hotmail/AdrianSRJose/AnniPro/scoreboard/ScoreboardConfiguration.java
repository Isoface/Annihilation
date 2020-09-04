package com.hotmail.AdrianSRJose.AnniPro.scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfigurationOptions;

import com.hotmail.AdrianSR.core.scoreboard.CustomScoreboard;
import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 09 July, 2020 / 04:29 PM
 */
public class ScoreboardConfiguration {

	public static final String     NAME_KEY = "name";
	public static final String ELEMENTS_KEY = "elements";

	protected       String         name = null;
	protected final String [ ] elements = new String [ CustomScoreboard.MAX_ELEMENTS ];
	protected final String [ ] defaults = new String [ CustomScoreboard.MAX_ELEMENTS ];
	
	public ScoreboardConfiguration ( String name , String [ ] elements , String [ ] default_elements ) {
		this.name = name;
		
		// we are limited to CustomScoreboard.MAX_ELEMENTS.
		for ( int i = 0 ; ( i < Math.min ( this.elements.length , elements.length ) ) ; i ++ ) {
			this.elements [ i ] = elements [ i ];
		}
		
		for ( int i = 0 ; ( i < Math.min ( this.defaults.length, default_elements.length ) ); i ++ ) {
			this.defaults [ i ] = default_elements [ i ];
		}
	}
	
	public ScoreboardConfiguration ( String name , String... elements ) {
		this ( name , elements , elements );
	}
	
	public String getName ( ) {
		return name;
	}
	
	public ScoreboardConfiguration setName ( String name ) {
		this.name = name;
		return this;
	}
	
	public String [ ] getElements ( ) {
		return elements;
	}
	
	public String [ ] getDefaultElements ( ) {
		return Arrays.copyOfRange ( defaults , 0 , defaults.length );
	}
	
	/**
	 * Inserts the default elements into the elements array of the configuration.
	 * <p>
	 * @return this Object, for chaining.
	 */
	public ScoreboardConfiguration insertDefaultElements ( ) {
		for ( int i = 0 ; i < Math.min ( elements.length , defaults.length ) ; i ++ ) {
			elements [ i ] = defaults [ i ];
		}
		return this;
	}
	
	public ScoreboardConfiguration save ( ConfigurationSection section ) {
		section.set ( NAME_KEY , TextUtils.untranslateColors ( TextUtils.getNotNull ( name , "" ) ) );
		section.set ( ELEMENTS_KEY , TextUtils.untranslateColors ( excludeNulls ( elements ) ) );
		return this;
	}

	/**
	 * @param section
	 * @return whether the configuration has been changed.
	 */
	public boolean saveDefaults ( ConfigurationSection section ) {
		boolean     name = false;
		boolean elements = false;
		
		// we are adding the header.
		header ( section );
		
		// we are saving the name if not set.
		name = YmlUtils.setNotSet ( section , NAME_KEY , 
				TextUtils.untranslateColors ( TextUtils.getNotNull ( this.name , "" ) ) ) > 0;
		
		// we are saving the default elements if not set.
		if ( !section.isSet ( ELEMENTS_KEY ) ) {
			section.set ( ELEMENTS_KEY , TextUtils.untranslateColors ( excludeNulls ( defaults ) ) );
			elements = true;
		}
		return name || elements;
	}
	
	protected void header ( ConfigurationSection section ) {
		ConfigurationOptions options = section.getRoot ( ).options ( );
		if ( options instanceof FileConfigurationOptions ) {
			FileConfigurationOptions yaml_options = (FileConfigurationOptions) options;
			yaml_options.header ( header ( ) );
			yaml_options.copyHeader ( true );
		}
	}
	
	protected String header ( ) {
		return "---------------------------- header here ---------------------------- #";
	}

	public ScoreboardConfiguration load ( ConfigurationSection section ) {
		this.name = section.getString ( NAME_KEY,  " " );

		/* clear before loading */
		Arrays.fill ( elements , null );

		/* load elements */
		List < String > current_list = section.getStringList ( ELEMENTS_KEY );
		for ( int i = 0 ; i < Math.min ( elements.length , current_list.size ( ) ) ; i ++ ) {
			elements [ i ] = current_list.get ( i );
		}
		return this;
	}
	
	protected String [ ] excludeNulls ( String [ ] array ) {
		List < String > collection = Arrays.asList ( array )
				.stream ( )
				.filter ( element -> element != null )
				.collect ( Collectors.toList ( ) );
		return collection.toArray ( new String [ collection.size ( ) ] );
	}
}