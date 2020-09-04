package com.hotmail.AdrianSRJose.AnniPro.utils.Loadable;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopType;

public class LoadableShopPhaseItemStack extends LoadableShopItemStack {
	
	public static final String LOADABLE_SHOP_ITEM_STACK_PHASE_KEY = "Show-in-phase";
	
	private int phase;

	public LoadableShopPhaseItemStack(String material, int cost, int amount, int slot, List<String> lore,
			ShopType shop_type, int phase) {
		super(material, cost, amount, slot, lore, shop_type);
		this.phase = phase;
	}
	
	public LoadableShopPhaseItemStack(String material, int cost, int amount, int slot, ShopType shop_type, int phase) {
		this(material, cost, amount, slot, ShopType.CONTENTS_LORE, shop_type, phase);
	}
	
	public LoadableShopPhaseItemStack(ConfigurationSection sc, List<String> lore) {
		super(sc, lore);
		this.phase = Math.max(sc.getInt(LOADABLE_SHOP_ITEM_STACK_PHASE_KEY), 1);
	}
	
	public int getPhase() {
		return phase;
	}
	
	@Override
	public boolean isValid() {
		return ( super.isValid() && this.phase > 0 );
	}
	
	@Override
	public int save(ConfigurationSection section) {
		int save = super.save(section);
		if ( isValid() ) { /* save phase */
			save += Util.setUpdatedIfNotEqual(section, LOADABLE_SHOP_ITEM_STACK_PHASE_KEY, this.phase);
		} else {
			throw new IllegalArgumentException("The invalid LoadableItemStacks cannot be saved!");
		}
		return save;
	}
	
	@Override
	public int saveNotSet(ConfigurationSection section) {
		int save = super.saveNotSet(section);
		if ( isValid() ) { /* save phase */
			save += Util.setDefaultIfNotSet(section, LOADABLE_SHOP_ITEM_STACK_PHASE_KEY, this.phase);
		} else {
			throw new IllegalArgumentException("The invalid LoadableItemStacks cannot be saved!");
		}
		return save;
	}
}
