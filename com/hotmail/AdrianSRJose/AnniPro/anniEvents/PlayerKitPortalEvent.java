package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class PlayerKitPortalEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;
	private final AnniPlayer player;
	private final Location from;
	private final Location to;

	public AnniPlayer getPlayer() {
		return player;
	}

	public Player getBukkitPlayer() {
		return player.getPlayer();
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean b) {
		cancelled = b;
	}

	public PlayerKitPortalEvent(AnniPlayer player, Location from, Location to) {
		this.player = player;
		this.to = to;
		this.from = from;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
