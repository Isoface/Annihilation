package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;

public final class PlayerJoinTeamEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	private AnniPlayer player;
	private AnniTeam team;
	private boolean cancelled;

	public PlayerJoinTeamEvent(AnniPlayer player, AnniTeam team) {
		this.player = player;
		this.team = team;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean b) {
		cancelled = b;
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
}
