package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

import lombok.Getter;

public class EnderChestOpenEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private final AnniPlayer player;
	private final @Getter Location chestLocation;
	private final @Getter Inventory inventory;
	private boolean cancelled;

	public AnniPlayer getPlayer() {
		return player;
	}

	public Player getNormalPlayer() {
		return player.getPlayer();
	}

	public EnderChestOpenEvent(AnniPlayer player, final Location chestLoc, final Inventory inventory) {
		this.player    = player;
		chestLocation  = chestLoc;
		this.inventory = inventory;
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
