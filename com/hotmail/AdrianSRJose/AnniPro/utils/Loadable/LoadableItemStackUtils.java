package com.hotmail.AdrianSRJose.AnniPro.utils.Loadable;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopType;

public class LoadableItemStackUtils {
	
	public static Class<? extends LoadableItemStack> detectLoadable(ConfigurationSection cs) {
		if ( isLoadableShopPhaseItemStack(cs) ) {
			return LoadableShopPhaseItemStack.class;
		} else if ( isLoadableShopItemStack(cs) ) {
			return LoadableShopItemStack.class;
		} else if ( isLoadableItemStack(cs) ) {
			return LoadableItemStack.class;
		}
		return null;
	}
	
	public static LoadableItemStack from(ConfigurationSection cs) {
		return from(cs, ShopType.CONTENTS_LORE);
	}
	
	public static LoadableItemStack from(ConfigurationSection cs, List<String> lore) {
		Class<? extends LoadableItemStack> type = detectLoadable(cs);
		if ( type == LoadableShopPhaseItemStack.class ) {
			return new LoadableShopPhaseItemStack(cs, lore);
		} else if ( type == LoadableShopItemStack.class ) {
			return new LoadableShopItemStack(cs, lore);
		} else if ( type == LoadableItemStack.class ) {
			return new LoadableItemStack(cs, lore);
		}
		return null;
	}
	
	public static boolean isLoadableItemStack(ConfigurationSection cs) {
		return new LoadableItemStack(cs, null).isValid();
	}

	public static boolean isLoadableShopItemStack(ConfigurationSection cs) {
		return new LoadableShopItemStack(cs, null).isValid();
	}
	
	public static boolean isLoadableShopPhaseItemStack(ConfigurationSection cs) {
		if ( !new LoadableShopPhaseItemStack(cs, null).isValid() ) {
			return false;
		}
		return cs.isInt( LoadableShopPhaseItemStack.LOADABLE_SHOP_ITEM_STACK_PHASE_KEY );
	}
}