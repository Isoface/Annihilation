package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;

public final class PlayerLeaveTeamEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	private final AnniPlayer player;
	private final AnniTeam team;

	public PlayerLeaveTeamEvent(final AnniPlayer player, final AnniTeam team) {
		this.player = player;
		this.team = team;
	}

	public AnniPlayer getPlayer() {
		return player;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	public AnniTeam getTeam() {
		return team;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
