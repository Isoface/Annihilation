package com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;
import lombok.Setter;

public class ImprovableBeaconEffect extends BeaconEffect
{
//	private final @Getter int costMultiplier;
	private final @Getter int maxLevel;
	private @Setter @Getter int currentLevel;
	private @Getter boolean improvable = true;
	
	public ImprovableBeaconEffect(final ConfigurationSection sc) {
		// Super vals
		super(sc);
		
		// Custom vals
		maxLevel       = Math.max(sc.getInt("improvable-max-effect-level"), 1);
		currentLevel   = super.getLevel();
	}
	
	public ImprovableBeaconEffect(
			final PotionEffectType effect, 
			final int level,
			final double range, 
			final Material icon, 
			final int cost, 
			final Material costMaterial,
			final int maxLevel,
			final String itemName) {
		// Super vals
		super(effect, level, range, icon, cost, costMaterial, itemName);
		
		// Custom vals
		this.maxLevel       = Math.max(maxLevel, 1);
		currentLevel        = super.getLevel();
	}
	
	@Override
	public ImprovableBeaconEffect addLine(boolean increaseLore, String... args) {
		for (String g : args) {
			if (g == null) {
				continue;
			}
			
			//add
			if (increaseLore) {
				this.getIncreaseLore().add(g);
			} else {
				this.getBuyLore().add(g);
			}
		}
		return this;
	}
	
	@Override
	public ImprovableBeaconEffect addLineMaxLore(String... args) {
		for (String g : args) {
			if (g == null) {
				continue;
			}
			
			// add
			this.getMaxLevelLore().add(g);
		}
		return this;
	}
	
	public ImprovableBeaconEffect setImprovable(boolean improvable) {
		this.improvable = improvable;
		return this;
	}
	
	public int save(final ConfigurationSection sc) {
		return    Util.setDefaultIfNotSet(sc, "effect", getEffect().getName())
				+ Util.setDefaultIfNotSet(sc, "effect-level", getLevel())
				+ Util.setDefaultIfNotSet(sc, "effect-range", getRange())
				+ Util.setDefaultIfNotSet(sc, "icon", getIcon().name())
				+ Util.setDefaultIfNotSet(sc, "cost", getCost())
				+ Util.setDefaultIfNotSet(sc, "cost-material", getCostMaterial().name())
				+ Util.setDefaultIfNotSet(sc, "improvable", improvable)
				+ Util.setDefaultIfNotSet(sc, "improvable-max-effect-level", maxLevel)
				+ Util.setDefaultIfNotSet(sc, "item-name", getItemName())
				+ saveLores(sc);
	}
	
	private int saveLores(final ConfigurationSection sc) {
		int tor = 0;
		if (!this.getIncreaseLore().isEmpty()) {
			tor += Util.setDefaultIfNotSet(sc, "item-lore-increase", this.getIncreaseLore());
		}
		
		if (!this.getBuyLore().isEmpty()) {
			tor += Util.setDefaultIfNotSet(sc, "item-lore-buy", this.getBuyLore());
		}

		if (!this.getMaxLevelLore().isEmpty()) {
			tor += Util.setDefaultIfNotSet(sc, "item-lore-max-level", this.getMaxLevelLore());
		}
		return tor;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof ImprovableBeaconEffect)) {
			return false;
		}
		
		ImprovableBeaconEffect other = (ImprovableBeaconEffect) obj;
		return this.getEffect().equals(other.getEffect()) && this.getCost() == other.getCost() 
				&& this.getCostMaterial() == other.getCostMaterial() && this.getIcon() == other.getIcon() 
				&& this.getCurrentLevel() == other.getCurrentLevel() && this.getRange() == other.getRange();
	}
}
