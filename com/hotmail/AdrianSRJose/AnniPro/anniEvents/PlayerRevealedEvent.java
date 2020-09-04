package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class PlayerRevealedEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();

	private final AnniPlayer player;
	private boolean cancelled;

	public PlayerRevealedEvent(final AnniPlayer player) {
		this.player = player;
	}

	public AnniPlayer getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean paramBoolean) {
		cancelled = paramBoolean;
	}

}
