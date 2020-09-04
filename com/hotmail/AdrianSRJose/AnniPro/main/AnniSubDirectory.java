package com.hotmail.AdrianSRJose.AnniPro.main;

import java.io.File;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Wednesday 22 July, 2020 / 06:44 PM
 */
public enum AnniSubDirectory {

	/**
	 * Directory that stores the worlds for the game.
	 */
	WORLDS_DIRECTORY ( "Worlds" ) ,
	
	/**
	 * Directory that stores the worlds for the boss.
	 */
	BOSS_WORLDS_DIRECTORY ( "BossWorlds" ) ,
	
	/**
	 * Directory that stores the kits.
	 */
	KITS_DIRECTORY ( "Kits" ) ,
	
	/**
	 * Directory that stores the images.
	 */
	IMAGES_DIRECTORY ( "Images" ) ,
	;
	
	private final String name;
	
	private AnniSubDirectory ( String name ) { 
		this.name = name;
	}
	
	public String getName ( ) {
		return name;
	}
	
	public File getDirectory ( ) {
//		return new File ( AnnihilationMain.INSTANCE.getDataFolder ( ).getParent ( ) , name );
		return new File ( AnnihilationMain.INSTANCE.getDataFolder ( ) , name );
	}
}