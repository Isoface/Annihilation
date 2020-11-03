package com.hotmail.AdrianSRJose.base;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public interface DelayUpdate {
	
	/**
	 * @param player the corresponding player, that can be offline.
	 * @param secondsLeft delay seconds left.
	 */
	void update ( AnniPlayer player , int secondsLeft );
}
