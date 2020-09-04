package com.hotmail.AdrianSRJose.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Defender_DefendHisNexusEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;
	private boolean addPotions = true;
	private final List<PotionEffect> potions;

	public Defender_DefendHisNexusEvent(AnniPlayer who) {
		super(who);
		potions = new ArrayList<PotionEffect>();
	}

	public boolean addPotions() {
		return addPotions;
	}

	public List<PotionEffect> getPotions() {
		return potions;
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

	// ---- Voids

	public void setAddPotions(boolean b) {
		addPotions = b;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

	public boolean addPotion(PotionEffect eff) {
		return !potions.contains(eff) && potions.add(eff);
	}

	public boolean removePotion(PotionEffectType pet) {
		if (potions.isEmpty())
			return false;
		for (PotionEffect eff : potions)
			if (eff.getType().equals(pet))
				return potions.remove(eff);
		return false;
	}

	public void clearPotions() {
		potions.clear();
	}
}
