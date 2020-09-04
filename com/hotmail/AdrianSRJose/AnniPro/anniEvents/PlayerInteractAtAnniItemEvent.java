package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class PlayerInteractAtAnniItemEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancel;
	private final AnniPlayer player;
	private final ItemStack stack;

	public PlayerInteractAtAnniItemEvent(AnniPlayer ap, ItemStack stack) {
		player = ap;
		this.stack = stack;
	}

	public PlayerInteractAtAnniItemEvent(PlayerInteractEvent event) {
		assert event != null : "The PlayerInteractEvent cant be null!";
		this.player = AnniPlayer.getPlayer(event.getPlayer());
		this.stack = event.getItem();
	}

	public AnniPlayer getPlayer() {
		return player;
	}

	public Player getBukkitPlayer() {
		return player.getPlayer();
	}

	public ItemStack getItem() {
		return stack;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean b) {
		cancel = b;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
