package com.hotmail.AdrianSRJose.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Defender_KillPlayersOnHisNexusEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;
	private final AnniPlayer victim;
	private int givedExp = 20;

	public Defender_KillPlayersOnHisNexusEvent(AnniPlayer who, AnniPlayer victim) {
		super(who);
		this.victim = victim;
	}

	public int getGivedExp() {
		return givedExp;
	}

	public AnniPlayer getVictim() {
		return victim;
	}

	public Player getBukkitVictimPlayer() {
		return victim != null ? victim.getPlayer() : null;
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

	// --- Voids

	public void setGivedExp(int newExp) {
		if (newExp < 0)
			throw new IllegalArgumentException("Invalid Number. The int: 'newExp' can´t be < to 0");
		this.givedExp = newExp;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
