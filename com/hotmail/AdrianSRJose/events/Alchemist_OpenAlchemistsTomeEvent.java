package com.hotmail.AdrianSRJose.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.Kits.Alchemist;

public class Alchemist_OpenAlchemistsTomeEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList h = new HandlerList();
	private boolean isCancelled;
	private String invName = Alchemist.ALCHEMIST_TOME_INVENTORY_NAME;
	private Inventory tome;

	public Alchemist_OpenAlchemistsTomeEvent(AnniPlayer who, Inventory inv) {
		super(who);
		tome = inv;
	}

	public String getAlchemistTomeInventoryName() {
		return invName;
	}

	@Override
	public HandlerList getHandlers() {
		return h;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	public Inventory getTomeInventory() {
		return tome;
	}

	// ----Voids

	/*
	 * public void setAlchemistTomeInventory(Inventory NewInv) { if (NewInv == null)
	 * throw new NullPointerException("Invalid Inventory"); tome = NewInv; }
	 */

	public void setAlchemistTomeInventoryName(String newName) {
		if (newName == null)
			throw new NullPointerException("Invalid Name");
		invName = newName;
		tome = Bukkit.getServer().createInventory(null, 9, invName);
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	public static HandlerList getHandlerList() {
		return h;
	}
}
