package com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class BeaconEffect
{
	private final PotionEffectType effect;
	private final               int level;
	private final            double range;
	private final           Material icon;
	private final                int cost;
	private final   Material costMaterial;
	private final         String itemName;
	private          List<String> buyLore = new ArrayList<String>();
	private     List<String> increaseLore = new ArrayList<String>();
	private     List<String> maxLevelLore = new ArrayList<String>();
	
	public BeaconEffect(final ConfigurationSection sc) {
		// Asserting that the ConfigurationSection is not null
		assert sc != null : "Error loading a magic beacon effect!";
		
		// Loading Vals
		effect       = effectFromSC(sc);
		level        = Math.max(sc.getInt("effect-level"), 0);
		range        = Math.max(sc.getDouble("effect-range"), 0.0D);
		icon         = materialFromSC(sc, "icon");
		cost         = Math.max(sc.getInt("cost"), 0);
		costMaterial = materialFromSC(sc, "cost-material");
		itemName     = Util.wc(sc.getString("item-name"));
		buyLore      = sc.getStringList("item-lore-buy");
		increaseLore = sc.getStringList("item-lore-increase");
		maxLevelLore = sc.getStringList("item-lore-max-level");
	}
	
	public List<String> coloredBuyLore() {
		final List<String> tor = buyLore;
		if (tor != null && !tor.isEmpty()) {
			for (int x = 0; x < tor.size(); x++) {
				tor.set(x, Util.wc(tor.get(x)));
			}
		}
		return tor;
	}
	
	public List<String> coloredIncreaseLore() {
		final List<String> tor = increaseLore;
		if (tor != null && !tor.isEmpty()) {
			for (int x = 0; x < tor.size(); x++) {
				tor.set(x, Util.wc(tor.get(x)));
			}
		}
		return tor;
	}
	
	public BeaconEffect(final PotionEffectType effect, final int level, 
			final double range, final Material icon2, final int cost, 
			final Material costMaterial2, final String itemName) {
		this.effect        = effect;
		this.level         = Math.max(level, 0);
		this.range         = Math.max(range, 0.0D);
		this.icon          = icon2;
		this.cost          = Math.max(cost, 0);
		this.costMaterial  = costMaterial2;
		this.itemName      = itemName;
	}
	
	public PotionEffectType getEffect ( ) {
		return effect;
	}
	
	public int getLevel ( ) {
		return level;
	}
	
	public double getRange ( ) {
		return range;
	}
	
	public Material getIcon ( ) {
		return icon;
	}
	
	public int getCost ( ) {
		return cost;
	}
	
	public Material getCostMaterial ( ) {
		return costMaterial;
	}
	
	public String getItemName ( ) {
		return itemName;
	}
	
	public List < String > getBuyLore ( ) {
		return buyLore;
	}
	
	public List < String > getIncreaseLore ( ) {
		return increaseLore;
	}
	
	public List < String > getMaxLevelLore ( ) {
		return maxLevelLore;
	}
	
	public BeaconEffect addLine(boolean increaseLore, String... args) {
		for (String g : args) {
			if (g == null) {
				continue;
			}
			
			//add
			if (increaseLore) {
				this.increaseLore.add(g);
			} else {
				this.buyLore.add(g);
			}
		}
		return this;
	}
	
	public BeaconEffect addLineMaxLore(String... args) {
		for (String g : args) {
			if (g == null) {
				continue;
			}
			
			// add
			this.maxLevelLore.add(g);
		}
		return this;
	}

	public static PotionEffectType effectFromSC(final ConfigurationSection sc) {
		// Checking is a string
		if (sc.isString("effect")) {
			// Loading and checking effect name
			final String effectName = sc.getString("effect");
			if (effectName != null) {
				// Getting and checkeing the potion effect type from the loaded name
				final PotionEffectType effect = PotionEffectType.getByName(effectName.toLowerCase());
				if (effect != null) {
					return effect;
				} else {
					if (effectName.isEmpty()) {
						Util.print(ChatColor.RED, "The effect cant be emtpy!");
					} else {
						Util.print(ChatColor.RED, "'" + effectName + "' is not a valid effect!");
					}
					return null;
				}
			} else {
				Util.print(ChatColor.RED, "The effect cant be emtpy!");
			}
		}
		return null;
	}

	public static Material materialFromSC(final ConfigurationSection sc, final String objectName) {
		// Checking is a string
		if (sc.isString(objectName)) {
			// Loading and checking material name
			final String materialName = sc.getString(objectName);
			if (materialName != null) {
				// Throws messages when is not a valid material.
				try {
					return Material.getMaterial(materialName.toUpperCase());
				} catch (Throwable t) {
					if (materialName.isEmpty()) {
						Util.print(ChatColor.RED, "The " + objectName + " cant be emtpy!");
					} else {
						Util.print(ChatColor.RED, "'" + materialName + "' is not a valid material!");
					}
					return null;
				}
			} else {
				Util.print(ChatColor.RED, "The " + objectName + " cant be emtpy!");
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof BeaconEffect)) {
			return false;
		}
		
		BeaconEffect other = (BeaconEffect) obj;
		return this.getEffect().equals(other.getEffect()) && this.getCost() == other.getCost() 
				&& this.getCostMaterial() == other.getCostMaterial() && this.getIcon() == other.getIcon() 
				&& this.getLevel() == other.getLevel() && this.getRange() == other.getRange();
	}
}
