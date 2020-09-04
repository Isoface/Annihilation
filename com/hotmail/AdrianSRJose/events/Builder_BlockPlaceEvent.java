package com.hotmail.AdrianSRJose.events;

import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Builder_BlockPlaceEvent extends AnniPlayerEvent {
	private static final HandlerList list = new HandlerList();
	private float givedExp = 0.01F;

	public Builder_BlockPlaceEvent(AnniPlayer who) {
		super(who);
	}

	public float getGivedExp() {
		return givedExp;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	// ---- Voids

	/**
	 * Default Gived Exp: 0.01F.
	 */
	public void setGivedExp(float newGivedExp) {
		givedExp = newGivedExp;
	}
}
