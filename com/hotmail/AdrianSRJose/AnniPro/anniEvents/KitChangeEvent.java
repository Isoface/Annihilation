package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;

public class KitChangeEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();

	private Kit oldKit;
	private Kit newKit;
	private boolean cancelled;

	private AnniPlayer player;

	public AnniPlayer getPlayer() {
		return player;
	}

	public Player getNormalPlayer() {
		return player.getPlayer();
	}

	public Kit getNewKit() {
		return newKit;
	}

	public Kit getOldKit() {
		return oldKit;
	}

	public KitChangeEvent(AnniPlayer player, Kit oldKit, Kit newKit) {
		this.player = player;
		this.oldKit = oldKit;
		this.newKit = newKit;
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
	public void setCancelled(boolean cancell) {
		cancelled = cancell;
	}
}
