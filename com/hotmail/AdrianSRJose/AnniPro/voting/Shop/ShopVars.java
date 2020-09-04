package com.hotmail.AdrianSRJose.AnniPro.voting.Shop;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableItemStack;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableItemStackUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableShopItemStack;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopType;

public class ShopVars {
	
	public static final String                  SHOP_CONFIGURATION_FILE_NAME = "AnniShopConfig.yml";
	public static final String          SHOP_BASE_CONFIGURATION_SECTION_NAME = "AnniShopConfig";
	public static final String SHOP_CONFIGURATION_CONTENTS_LORE_SECTION_NAME = "Contents-lore";
	public static final String              SHOP_CONFIGURATION_MENU_NAME_KEY = "Menu-name";
	public static final String              SHOP_CONFIGURATION_MENU_SIZE_KEY = "Menu-size";
	public static final String      SHOP_CONFIGURATION_CONTENTS_SECTION_NAME = "Contents";
	
	public static void loadVars(ConfigurationSection config) {
		ConfigurationSection base = config.getConfigurationSection(SHOP_BASE_CONFIGURATION_SECTION_NAME);
		if (base == null) { /* load and check base configuration section validity */
			return;
		}
		
		/* load contents lore */
		ShopType.CONTENTS_LORE.clear();
		ShopType.CONTENTS_LORE.addAll( base.getStringList( SHOP_CONFIGURATION_CONTENTS_LORE_SECTION_NAME ) );
		
		/* load shops configuration */
		for (ShopType type : ShopType.values()) {
			ConfigurationSection shop_cs = base.getConfigurationSection(type.getConfigurationSectionName());
			if (shop_cs == null) {
				continue;
			}
			
			/* load menu core values */
			type.setMenuName( shop_cs.getString( SHOP_CONFIGURATION_MENU_NAME_KEY ) ).getMenuName();
			type.setMenuSize( shop_cs.getInt( SHOP_CONFIGURATION_MENU_SIZE_KEY) ).getMenuSize();
			
			/* load contents */
			ConfigurationSection contents_cs = shop_cs.getConfigurationSection( SHOP_CONFIGURATION_CONTENTS_SECTION_NAME );
			for (String key : contents_cs.getKeys(false)) {
				ConfigurationSection content_cs = contents_cs.getConfigurationSection( key );
				if (content_cs == null) {
					continue;
				}
				
				/* load item and check validity */
				LoadableItemStack item = LoadableItemStackUtils.from(content_cs);
				if (item == null || !item.isValid() || !LoadableShopItemStack.class
						.isAssignableFrom(item.getClass())) { /* when the content item is not valid */
					continue;
				}
				
				/* just add to the shop contents */
				type.addContent((LoadableShopItemStack) item);
			}
		}
	}
}