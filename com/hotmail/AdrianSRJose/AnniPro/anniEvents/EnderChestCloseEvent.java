package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

import lombok.Getter;

public class EnderChestCloseEvent extends Event {
	private static final HandlerList list = new HandlerList();
	private final AnniPlayer player;
	private final @Getter Inventory inventory;

	public AnniPlayer getPlayer() {
		return player;
	}

	public Player getNormalPlayer() {
		return player.getPlayer();
	}

	public EnderChestCloseEvent(AnniPlayer player, final Inventory inventory) {
		this.player    = player;
		this.inventory = inventory;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
