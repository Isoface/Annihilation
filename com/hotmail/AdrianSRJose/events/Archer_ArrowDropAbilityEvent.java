package com.hotmail.AdrianSRJose.events;

import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

public class Archer_ArrowDropAbilityEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList h = new HandlerList();
	private boolean isCancelled;
	private ItemStack droped = KitUtils.addSoulbound(new ItemStack(Material.ARROW, 16));

	public Archer_ArrowDropAbilityEvent(AnniPlayer who) {
		super(who);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return h;
	}

	public ItemStack getDropedArrows() {
		return droped;
	}

	// ----Voids

	public void setDropedsArrowAmmount(int ammount) {
		droped = ammount > 0 ? KitUtils.addSoulbound(new ItemStack(Material.ARROW, ammount)) : (ItemStack) null;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	public static HandlerList getHandlerList() {
		return h;
	}
}
