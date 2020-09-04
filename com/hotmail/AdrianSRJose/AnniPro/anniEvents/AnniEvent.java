package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public final class AnniEvent {
	public static void callEvent(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}
}
