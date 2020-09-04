package com.hotmail.AdrianSRJose.AnniPro.utils.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableShopItemStack;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableShopPhaseItemStack;

public enum ShopType {

	BREWING(Size.THREE_LINE, "&5Brewing Shop", "Brewing-Shop"),
	WEAPON(Size.THREE_LINE,  "&5Weapon Shop",  "Weapon-Shop");

	/**
	 * The contents lore.
	 * <p>
	 * With a default value.
	 */
	public static List<String> CONTENTS_LORE = new ArrayList<String>( Arrays.asList( new String[] 
			{
					"&6Cost: %c Gold Ingots",
					"&6Quantity: %q"
			} ) );
	
	/**
	 * The shops default contents.
	 */
	private static final Map<ShopType, LoadableShopItemStack[]> DEFAULT_CONTENTS = new HashMap<ShopType, LoadableShopItemStack[]>();
	static {
		/* the brewing shop default contents */
		ShopType.DEFAULT_CONTENTS.put(ShopType.BREWING, new LoadableShopItemStack[]
				{ 
						/* normal items */ // material, cost, amount, slot
						new LoadableShopItemStack("BREWING_STAND_ITEM",   10, 1, 0,   ShopType.BREWING),
						new LoadableShopItemStack("GLASS_BOTTLE",         1,  3, 1,   ShopType.BREWING),
						new LoadableShopItemStack("NETHER_STALK",         5,  1, 2,   ShopType.BREWING),
						new LoadableShopItemStack("REDSTONE",             3,  1, 9,   ShopType.BREWING),
						new LoadableShopItemStack("REDSTONE",             3,  1, 9,   ShopType.BREWING),
						new LoadableShopItemStack("GLOWSTONE_DUST",       3,  1, 10,  ShopType.BREWING),
						new LoadableShopItemStack("FERMENTED_SPIDER_EYE", 3,  1, 11,  ShopType.BREWING),
						new LoadableShopItemStack("SULPHUR",              3,  1, 12,  ShopType.BREWING),
						new LoadableShopItemStack("MAGMA_CREAM",          2,  1, 18,  ShopType.BREWING),
						new LoadableShopItemStack("SUGAR",                2,  1, 19,  ShopType.BREWING),
						new LoadableShopItemStack("SPECKLED_MELON",       2,  1, 20,  ShopType.BREWING),
						new LoadableShopItemStack("GHAST_TEAR",           15,  1, 21, ShopType.BREWING),
						new LoadableShopItemStack("GOLDEN_CARROT",        2,  1, 22,  ShopType.BREWING),
						new LoadableShopItemStack("SPIDER_EYE",           2,  1, 23,  ShopType.BREWING),
						
						/* phase items */
						new LoadableShopPhaseItemStack("BLAZE_POWDER", 15, 1, 24, ShopType.BREWING, 4),
						new LoadableShopPhaseItemStack("APPLE",        65, 1, 25, ShopType.BREWING, 4)
				});
		
		/* the weapon shop default contents */
		ShopType.DEFAULT_CONTENTS.put(ShopType.WEAPON, new LoadableShopItemStack[]
				{
						new LoadableShopItemStack("CHAINMAIL_HELMET",     10, 1, 0,  ShopType.WEAPON),
						new LoadableShopItemStack("CHAINMAIL_CHESTPLATE", 10, 1, 1,  ShopType.WEAPON),
						new LoadableShopItemStack("CHAINMAIL_LEGGINGS",   10, 1, 2,  ShopType.WEAPON),
						new LoadableShopItemStack("CHAINMAIL_BOOTS",      10, 1, 3,  ShopType.WEAPON),
						new LoadableShopItemStack("IRON_SWORD",           5, 1, 4,   ShopType.WEAPON),
						new LoadableShopItemStack("BOW",                  5, 1, 5,   ShopType.WEAPON),
						new LoadableShopItemStack("ARROW",                5, 16, 6,  ShopType.WEAPON),
						new LoadableShopItemStack("FISHING_ROD",          5, 1, 9,   ShopType.WEAPON),
						new LoadableShopItemStack("CAKE",                 5, 1, 10,  ShopType.WEAPON),
						new LoadableShopItemStack("RAW_BEEF",             5, 3, 11,  ShopType.WEAPON),
						new LoadableShopItemStack("BOOK",                 5, 1, 12,  ShopType.WEAPON),
						new LoadableShopItemStack("ENDER_PEARL",          40, 1, 13, ShopType.WEAPON),
				});
	}
	
	private static LoadableShopItemStack[] getDefaultContents(ShopType type) {
		return DEFAULT_CONTENTS.get(type);
	}
	
	/**
	 * The default shop values.
	 */
	private final String           config_section_name;
	private       Size                       menu_size;
	private       String                     menu_name;
	private final Size               default_menu_size;
	private final String             default_menu_name;
	private final List<LoadableShopItemStack> contents;

	/**
	 * Empty Constructor.
	 */
	ShopType(Size size, String name, String csn) {
		this.menu_size           = size;
		this.menu_name           = Util.wc(name);
		this.default_menu_size   = this.menu_size;
		this.default_menu_name   = this.menu_name;
		this.config_section_name = csn;
		this.contents            = new ArrayList<LoadableShopItemStack>();
	}

	public ShopType setMenuSize(Size size) {
		this.menu_size = size;
		return this;
	}
	
	public ShopType setMenuSize(int size) {
		this.setMenuSize(Size.fit(size));
		return this;
	}
	
	public Size getMenuSize() {
		return this.menu_size;
	}
	
	public Size getDefaultMenuSize() {
		return this.default_menu_size;
	}
	
	public ShopType setMenuName(String name) {
		this.menu_name = Util.wc(name);
		return this;
	}
	
	public String getMenuName() {
		return this.menu_name;
	}
	
	public String getDefaultMenuName() {
		return this.default_menu_name;
	}
	
	public String getConfigurationSectionName() {
		return this.config_section_name;
	}
	
	public List<LoadableShopItemStack> getContents() {
		return Collections.unmodifiableList(this.contents);
	}
	
	public List<LoadableShopItemStack> getDefaultContents() {
		return Collections.unmodifiableList( Arrays.asList( ShopType.getDefaultContents(this) ) );
	}
	
	public ShopType addContent(LoadableShopItemStack item) {
		if (item != null && !this.contents.contains(item)) {
			this.contents.add(item);
		}
		return this;
	}
	
	public ShopType removeContent(LoadableShopItemStack item) {
		this.contents.remove(item);
		return this;
	}
	
	public static ShopType getFromString(String key) {
		if (key != null) {
			for (ShopType type : values()) {
				if (type.name().equalsIgnoreCase(key)) {
					return type;
				}
				
				if (key.toUpperCase().contains(type.name())) {
					return type;
				}
			}
		}
		return null;
	}
}
