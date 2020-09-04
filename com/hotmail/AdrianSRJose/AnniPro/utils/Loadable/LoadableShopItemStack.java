package com.hotmail.AdrianSRJose.AnniPro.utils.Loadable;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopType;

public class LoadableShopItemStack extends LoadableItemStack {
	
	public static final String LOADABLE_SHOP_ITEM_STACK_SHOP_KEY = "Set-in-shop";
	
	private final ShopType shop_type;

	public LoadableShopItemStack(String material, int cost, int amount, int slot, List<String> lore, ShopType shop_type) {
		super(material, cost, amount, slot, lore);
		this.shop_type = shop_type;
	}
	
	public LoadableShopItemStack(String material, int cost, int amount, int slot, ShopType shop_type) {
		this(material, cost, amount, slot, ShopType.CONTENTS_LORE, shop_type);
	}

	public LoadableShopItemStack(ConfigurationSection sc, List<String> lore) {
		super(sc, lore);
		this.shop_type = ShopType.getFromString(sc.getString(LOADABLE_SHOP_ITEM_STACK_SHOP_KEY));
	}
	
	public LoadableShopItemStack(ConfigurationSection sc) {
		super(sc, ShopType.CONTENTS_LORE);
		this.shop_type = ShopType.getFromString(sc.getString(LOADABLE_SHOP_ITEM_STACK_SHOP_KEY));
	}
	
	public ShopType getShopType() {
		return this.shop_type;
	}
	
	@Override
	public boolean isValid() {
		return ( super.isValid() && this.shop_type != null );
	}
	
	@Override
	public int save(ConfigurationSection section) {
		int save = super.save(section);
		if ( isValid() ) {
			if ( this.shop_type != null ) { /* save shop type */
				save += Util.setUpdatedIfNotEqual(section, LOADABLE_SHOP_ITEM_STACK_SHOP_KEY, this.shop_type.name());
			}
		} else {
			throw new IllegalArgumentException("The invalid LoadableItemStacks cannot be saved!");
		}
		return save;
	}
	
	@Override
	public int saveNotSet(ConfigurationSection section) {
		int save = super.saveNotSet(section);
		if ( isValid() ) {
			if ( this.shop_type != null ) { /* save shop type */
				save += Util.setDefaultIfNotSet(section, LOADABLE_SHOP_ITEM_STACK_SHOP_KEY, this.shop_type.name());
			}
		} else {
			throw new IllegalArgumentException("The invalid LoadableItemStacks cannot be saved!");
		}
		return save;
	}
}
