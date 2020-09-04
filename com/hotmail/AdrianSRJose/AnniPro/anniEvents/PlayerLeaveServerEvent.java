package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

import lombok.Getter;

public final class PlayerLeaveServerEvent extends Event {
	private static final HandlerList list = new HandlerList();

	private final @Getter AnniPlayer player;
	private final @Getter String reason;
	private final @Getter boolean kicked;

	public PlayerLeaveServerEvent(AnniPlayer player, String reason, boolean isKicked) {
		this.player = player;
		this.reason = reason;
		this.kicked = isKicked;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}
}
