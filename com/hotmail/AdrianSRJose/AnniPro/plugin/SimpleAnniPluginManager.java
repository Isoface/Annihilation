package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

/**
 * Handles all plugin management from the Server
 */
public final class SimpleAnniPluginManager implements AnniPluginManager {
	private final Server server;
	private final Map<Pattern, AnniPluginLoader> fileAssociations = new HashMap<Pattern, AnniPluginLoader>();
	private final Map<String, AnniPlugin> lookupNames = AnnihilationMain.getPlugins();
	private final Map<String, List<Listener>> pluginsListeners = new HashMap<String, List<Listener>>();
	private static File updateDirectory = null;
	private boolean useTimings = false;
	private final JavaPlugin MAIN;

	public SimpleAnniPluginManager(Server instance, AnnihilationMain MAIN) {
		server           = instance;
		this.MAIN        = MAIN;
	}

	/**
	 * Checks if the given AnniPlugin is loaded and returns it when applicable
	 * <p>
	 * Please note that the name of the AnniPlugin is case-sensitive
	 *
	 * @param name
	 *            Name of the AnniPlugin to check
	 * @return AnniPlugin if it exists, otherwise null
	 */
	public synchronized AnniPlugin getPlugin(String name) {
		return lookupNames.get(name.replace(' ', '_'));
	}

	public synchronized AnniPlugin[] getPluginsArray() {
		return new ArrayList<AnniPlugin>(lookupNames.values()).toArray(new AnniPlugin[0]);
	}

	public synchronized List<AnniPlugin> getPlugins() {
		return Collections.unmodifiableList(new ArrayList<AnniPlugin>(lookupNames.values()));
	}

	/**
	 * Checks if the given plugin is enabled or not
	 * <p>
	 * Please note that the name of the plugin is case-sensitive.
	 *
	 * @param name
	 *            Name of the plugin to check
	 * @return true if the plugin is enabled, otherwise false
	 */
	public boolean isPluginEnabled(String name) {
		AnniPlugin plugin = getPlugin(name);

		return isPluginEnabled(plugin);
	}

	/**
	 * Checks if the given plugin is enabled or not
	 *
	 * @param plugin
	 *            Plugin to check
	 * @return true if the plugin is enabled, otherwise false
	 */
	public boolean isPluginEnabled(AnniPlugin plugin) {
		if ((plugin != null) && (getPlugins().contains(plugin))) {
			return plugin.isEnabled();
		} else {
			return false;
		}
	}

	public void enablePlugin(final AnniPlugin plugin) {
		if (!plugin.isEnabled()) {
			try {
				plugin.getLoader().enablePlugin(plugin);
			} catch (Throwable ex) {
				server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while enabling "
						+ plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}

			HandlerList.bakeAll();
		}
	}

	public void disablePlugins() {
		AnniPlugin[] plugins = getPluginsArray();
		for (int i = plugins.length - 1; i >= 0; i--) {
			disablePlugin(plugins[i]);
		}
	}

	public void disablePlugin(final AnniPlugin plugin) {
		if (plugin.isEnabled()) {
			try {
				plugin.getLoader().disablePlugin(plugin);
			} catch (Throwable ex) {
				server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling "
						+ plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}

            try {
                this.unregisterEvents(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
		}
	}
	
	public void clearPlugins() {
		((AnnihilationMain) MAIN).clearPlugins(fileAssociations);
	}

	/**
	 * Calls an event with the given details.
	 * <p>
	 * This method only synchronizes when the event is not asynchronous.
	 *
	 * @param event
	 *            Event details
	 */
	public void callEvent(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public void registerEvents(AnniPlugin plugin, Listener listener) {
		if (plugin != null && listener != null) {
			// register event
			Bukkit.getPluginManager().registerEvents(listener, MAIN);
			
			// ADD TO HIS PLUGIN LISTENERS LIST
			// check list
			List<Listener> listeners = pluginsListeners.get(plugin.getName());
			if (listeners == null) {
				listeners = new ArrayList<Listener>();
			}
			
			// add to list
			listeners.add(listener);
			
			// save list
			pluginsListeners.put(plugin.getName(), listeners);
		}
		else {
			Validate.notNull(plugin, "The plugin cannot be null!");
			Validate.notNull(listener, "The listener cannot be null!");
		}
	}
	
	@Override
	public void unregisterEvents(AnniPlugin plugin) {
		if (plugin != null) {
			// get and check list
			final List<Listener> listeners = pluginsListeners.get(plugin.getName());
			if (listeners == null || listeners.isEmpty()) {
				return;
			}
			
			// unregister
			for (Listener list : listeners) {
				HandlerList.unregisterAll(list);
			}
		}
		else {
			Validate.notNull(plugin, "The plugin cannot be null!");
		}
	}

	public boolean useTimings() {
		return useTimings;
	}

	/**
	 * Sets whether or not per event timing code should be used
	 *
	 * @param use
	 *            True if per event timing code should be used
	 */
	public void useTimings(boolean use) {
		useTimings = use;
	}
}
