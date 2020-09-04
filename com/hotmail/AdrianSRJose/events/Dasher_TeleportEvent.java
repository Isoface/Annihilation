package com.hotmail.AdrianSRJose.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Dasher_TeleportEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled = false;
	private final Location from;
	private Location to;
	private boolean addPotions = true;
	private boolean playCircleEffect = true;
	private boolean playPathEffect = true;
	private int foodLost;
	private final List<PotionEffect> potions;

	public Dasher_TeleportEvent(AnniPlayer who, Location from, Location to, int foodLost) {
		super(who);
		this.from = from;
		this.to = to;
		potions = new ArrayList<PotionEffect>();
		this.foodLost = foodLost;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

	public boolean playCircleEffect() {
		return playCircleEffect;
	}

	public boolean playPathEffect() {
		return playPathEffect;
	}

	public boolean addPotionsOnTeleport() {
		return addPotions;
	}

	public List<PotionEffect> getPotions() {
		return potions;
	}

	public int getFoodLost() {
		return foodLost;
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

	public void setTo(Location newTo) {
		if (newTo == null || newTo.getWorld() == null) {
			throw new IllegalArgumentException("The location cannot be null!");
		}
		
		to = newTo;
	}

	public void setAddPotionsOnTeleport(boolean b) {
		addPotions = b;
	}

	public void setPlayCircelEffect(boolean b) {
		playCircleEffect = b;
	}

	public void setPlayPathEffect(boolean b) {
		playPathEffect = b;
	}

	public boolean addPotion(PotionEffect eff) {
		return !potions.contains(eff) && potions.add(eff);
	}

	public boolean removePotion(PotionEffectType pet) {
		if (potions.isEmpty()) {
			return false;
		}
		
		for (PotionEffect eff : potions) {
			if (eff.getType().equals(pet)) {
				return potions.remove(eff);
			}
		}
		return false;
	}

	public void clearPotions() {
		potions.clear();
	}

	public void setFoodLost(int newFoodLost) {
		foodLost = newFoodLost;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
