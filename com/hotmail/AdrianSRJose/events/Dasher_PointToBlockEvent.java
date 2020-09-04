package com.hotmail.AdrianSRJose.events;

import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Dasher_PointToBlockEvent extends AnniPlayerEvent {
	private static final HandlerList list = new HandlerList();
	private boolean playEffect = true;
	private Block block;

	public Dasher_PointToBlockEvent(AnniPlayer who, Block block) {
		super(who);
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public boolean playEffect() {
		return playEffect;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	// --- Voids

	public void setPlayPointEffect(boolean b) {
		playEffect = b;
	}
}
