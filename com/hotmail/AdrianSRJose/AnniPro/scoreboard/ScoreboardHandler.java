package com.hotmail.AdrianSRJose.AnniPro.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.manager.CustomPluginManager;
import com.hotmail.AdrianSR.core.scoreboard.CustomScoreboard;
import com.hotmail.AdrianSR.core.util.Schedulers;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;

/**
 * Handler for handling scoreboards, {@link #run()} is called for updating
 * scoreboards.
 * <p>
 * @author AdrianSR / Thursday 09 July, 2020 / 05:19 PM
 */
public abstract class ScoreboardHandler extends CustomPluginManager implements Runnable {

	/** updater task */
	protected BukkitTask updater;
	
	/**
	 * Constructs the scoreboard handler.
	 * <p>
	 * @param plugin the plugin that initializes this handler.
	 */
	public ScoreboardHandler ( CustomPlugin plugin ) {
		super ( plugin );
	}
	
	/**
	 * Gets the configuration that is to be applied to the scoreboards that are
	 * handled by this handler.
	 * <p>
	 * @return configuration for handling scoreboards.
	 */
	public abstract ScoreboardConfiguration getConfiguration ( );
	
	/**
	 * Gets the scoreboard of the provided {@code player}.
	 * <p>
	 * @param player the player owner of the scoreboard.
	 * @return the scoreboard of the provided {@code player}.
	 */
	public abstract CustomScoreboard get ( AnniPlayer player );
	
	/**
	 * Updates the scoreboard of the provided {@code player}.
	 * <p>
	 * @param player the player owner of the scoreboard to update.
	 * @return the scoreboard of the provided {@code player}.
	 */
	public abstract CustomScoreboard update ( AnniPlayer player );
	
	/**
	 * Starts the updater task.
	 * <p>
	 * @return false when the updater has already been started.
	 */
	public boolean startUpdating ( ) {
		if ( updater == null || !Bukkit.getScheduler ( ).isCurrentlyRunning ( updater.getTaskId ( ) ) ) {
			updater = Schedulers.syncRepeating ( this , 0 , Config.SCOREBOARDS_REFRESH_DELAY.toInt ( ) , plugin );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Stops the updater task.
	 * <p>
	 * @return false when the updater has already been stopped, or when has never
	 *         been started.
	 */
	public boolean stopUpdating ( ) {
		if ( updater != null ) {
			updater.cancel ( );
			updater = null;
			return true;
		} else {
			return false;
		}
	}
}