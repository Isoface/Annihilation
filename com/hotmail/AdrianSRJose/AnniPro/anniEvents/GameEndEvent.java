package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;

public final class GameEndEvent extends Event {
	private static final HandlerList list = new HandlerList();

	private final AnniTeam winner;

	public GameEndEvent(AnniTeam winningTeam) {
		winner = winningTeam;
	}

	public AnniTeam getWinningTeam() {
		return winner;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
