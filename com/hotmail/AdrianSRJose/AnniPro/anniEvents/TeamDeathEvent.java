package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Nexus;

public final class TeamDeathEvent extends Event// implements Cancellable
{
	private static final HandlerList list = new HandlerList();

	private AnniPlayer player;
	private final AnniTeam team;
	private Nexus nexus;
	// private boolean cancelled;

	public TeamDeathEvent(AnniPlayer player, AnniTeam teamDeath) {
		this.player = player;
		team = teamDeath;
		nexus = teamDeath.getNexus();
	}

	public AnniPlayer getKiller() {
		return player;
	}

	public AnniTeam getKilledTeam() {
		return team;
	}

	public Nexus getNexusDestroyed() {
		return nexus;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	// @Override
	// public boolean isCancelled()
	// {
	// return cancelled;
	// }
	//
	// @Override
	// public void setCancelled(final boolean b)
	// {
	// cancelled = b;
	// }
}
