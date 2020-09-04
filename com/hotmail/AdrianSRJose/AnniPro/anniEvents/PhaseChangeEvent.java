package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PhaseChangeEvent extends Event {
	private static final HandlerList list = new HandlerList();
	private int oldPhase;
	private int newPhase;

	public int getNewPhase() {
		return newPhase;
	}

	public int getOldPhase() {
		return oldPhase;
	}

	public PhaseChangeEvent(int oldKit, int newPhase) {
		this.oldPhase = oldKit;
		this.newPhase = newPhase;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
