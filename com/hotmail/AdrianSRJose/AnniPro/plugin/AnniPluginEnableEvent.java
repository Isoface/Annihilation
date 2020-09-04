package com.hotmail.AdrianSRJose.AnniPro.plugin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a plugin is enabled.
 */
public class AnniPluginEnableEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final AnniPlugin plugin;

	public AnniPluginEnableEvent(final AnniPlugin plugin) {
		this.plugin = plugin;
	}

	public AnniPlugin getPlugin() {
		return plugin;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
