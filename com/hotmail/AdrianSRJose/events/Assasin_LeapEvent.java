package com.hotmail.AdrianSRJose.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Assasin_LeapEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList h = new HandlerList();
	private boolean isCancelled = false;
	private boolean restoreBackup = true;
	private boolean addPotions = true;
	private int endLeep = 8;
	private double height = 1.0;
	private final List<PotionEffect> potions;

	private ItemStack[] armorBackup;

	public Assasin_LeapEvent(AnniPlayer who, ItemStack[] armorBackup) {
		super(who);
		this.armorBackup = armorBackup;
		potions = new ArrayList<PotionEffect>();
	}

	public List<PotionEffect> getPotions() {
		return potions;
	}

	public ItemStack[] getArmorBackup() {
		return armorBackup;
	}

	public int getBukupRestoreCoutdown() {
		return endLeep;
	}

	public double getHeight() {
		return height;
	}

	public boolean getRestoreBackup() {
		return restoreBackup;
	}

	public boolean addPotions() {
		return addPotions;
	}

	// ----Voids

	public void setArmorBackup(ItemStack[] all) {
		armorBackup = all;
	}

	/**
	 * Set armor Backup with pos.
	 * 
	 * Pos 0 = helmet. Pos 1 = chestPlate. Pos 2 = Leggins. Pos 3 = Boots. The int:
	 * "pos" can´t be high to 3.
	 */
	public void setArmorBackupFromPos(int pos, ItemStack newStack) {
		if (armorBackup == null)
			return;
		if (pos > 3)
			throw new IllegalArgumentException("The int: 'pos' can´t be high to 3.");

		armorBackup[pos] = newStack;
	}

	/**
	 * 
	 * @see: Set This On Seconds. (>= to 0)
	 * 
	 */
	public void setBukupRestoreCoutdown(int newCoutdown) {
		if (newCoutdown < 0)
			throw new IllegalArgumentException("Invalid Number. The int: 'newCoutdown' can´t be < to 0");
		endLeep = newCoutdown;
	}

	public void setMultiplicatedHeight(double h) {
		height = h;
	}

	public void setRestoreBackup(boolean b) {
		restoreBackup = b;
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

	public void setAddPotions(boolean b) {
		addPotions = b;
	}

	@Override
	public HandlerList getHandlers() {
		return h;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	public static HandlerList getHandlerList() {
		return h;
	}
}
