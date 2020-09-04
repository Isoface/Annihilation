package com.hotmail.AdrianSRJose.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Builder_OpenResurceDropBookEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;
	private Inventory resourceDrop;

	public Builder_OpenResurceDropBookEvent(AnniPlayer who, Inventory inventory) {
		super(who);
		resourceDrop = inventory;
	}

	public Inventory getResourceDropInvenotry() {
		return resourceDrop;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	// ----- Voids

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

	public void setResourceDropInventory(Inventory newInv) {
		resourceDrop = newInv;
	}

	public void setResourceDropInventoryName(String newName) {
		resourceDrop = Bukkit.createInventory(null, 27, newName);
	}
}
