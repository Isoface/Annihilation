package com.hotmail.AdrianSRJose.AnniPro.main;

import java.util.UUID;

import org.bukkit.entity.Player;

import us.myles.ViaVersion.ViaVersionPlugin;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 04 September, 2020 / 04:17 PM
 */
public class VersionHandler  {
	
	VersionHandler ( AnnihilationMain main ) {
		// nothing...
	}
	
	public int getVersion ( Player player ) {
		return ViaVersionPlugin.getInstance ( ).getApi ( ).getPlayerVersion ( player );
	}
	
	public int getVersion ( UUID uuid ) {
		return ViaVersionPlugin.getInstance ( ).getApi ( ).getPlayerVersion ( uuid );
	}
}