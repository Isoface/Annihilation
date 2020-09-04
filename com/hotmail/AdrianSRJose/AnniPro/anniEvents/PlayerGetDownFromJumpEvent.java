package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;

/**
 * Holds information for player get down from jump events
 */
public class PlayerGetDownFromJumpEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private final @Getter Location groundLocation;
	private final @Getter Block ground;

	public PlayerGetDownFromJumpEvent(final Player player, final Location groundLocation, final Block ground) {
		super(player);
		this.groundLocation = groundLocation;
		this.ground = ground;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}