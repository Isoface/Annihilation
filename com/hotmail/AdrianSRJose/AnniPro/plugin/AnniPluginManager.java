package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Handles all plugin management from the Server
 */
public interface AnniPluginManager {
    /**
     * Checks if the given plugin is loaded and returns it when applicable
     * <p>
     * Please note that the name of the plugin is case-sensitive
     *
     * @param name Name of the plugin to check
     * @return Plugin if it exists, otherwise null
     */
    public AnniPlugin getPlugin(String name);

    /**
     * Gets a list of all currently loaded plugins
     *
     * @return Array of Plugins
     */
    public List<AnniPlugin> getPlugins();

    /**
     * Checks if the given plugin is enabled or not
     * <p>
     * Please note that the name of the plugin is case-sensitive.
     *
     * @param name Name of the plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isPluginEnabled(String name);

    /**
     * Checks if the given plugin is enabled or not
     *
     * @param plugin Plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isPluginEnabled(AnniPlugin AnniPlugin);

    /**
     * Disables all the loaded plugins
     */
    public void disablePlugins();

    /**
     * Disables and removes all plugins
     */
    public void clearPlugins();

    /**
     * Calls an event with the given details
     *
     * @param event Event details
     * @throws IllegalStateException Thrown when an asynchronous event is
     *     fired from synchronous code.
     *     <p>
     *     <i>Note: This is best-effort basis, and should not be used to test
     *     synchronized state. This is an indicator for flawed flow logic.</i>
     */
    public void callEvent(Event event) throws IllegalStateException;

    /**
     * Enables the specified AnniPlugin
     * <p>
     * Attempting to enable a AnniPlugin that is already enabled will have no
     * effect
     *
     * @param AnniPlugin AnniPlugin to enable
     */
    public void enablePlugin(AnniPlugin AnniPlugin);

    /**
     * Disables the specified AnniPlugin
     * <p>
     * Attempting to disable a AnniPlugin that is not enabled will have no effect
     *
     * @param AnniPlugin AnniPlugin to disable
     */
    public void disablePlugin(AnniPlugin AnniPlugin);
    
    public void registerEvents(AnniPlugin plugin, Listener listener);
    
    public void unregisterEvents(AnniPlugin plugin);

    /**
     * Returns whether or not timing code should be used for event calls
     *
     * @return True if event timings are to be used
     */
    public boolean useTimings();
}