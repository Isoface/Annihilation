package com.hotmail.AdrianSRJose.AnniPro.kits.Menu;

import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public enum KitsMenuConfig {
	// Save Last MaintanceMode Time
	MENU_TITLE("MenuTitle", "&6&l%PLAYER% Kits"), FINAL_LORE("FinalLore",
			"&6--------------------------"), UNLOCKED_KIT("Unlocked", "&aUNLOCKED"), LOCKED_KIT("Locked", "&cLOCKED"),

	// Details
	// Space
	SPACE_USE("Space.Use", false), 
	SPACE_START_SLOT("Space.StartInSlot", 10),
	SPACE_SPACES("Space.Spaces",9), 
	SPACE_MATERIAL("Space.Material", "STAINED_GLASS_PANE"), 
	SPACE_NAME("Space.Name", "."), 
	SPACE_COLOR("Space.Color", "WHITE"),

	// Unlocked Kits Space
	UNLOCKED_KIT_SPACE_USE("UnlockedKitsSpace.Use", true), 
	UNLOCKED_KITS_SPACE_MATERIAL("UnlockedKitsSpace.Material", "STAINED_GLASS_PANE"), 
	UNLOCKED_KITS_SPACE_SPACE_BETWEEN_KITS("UnlockedKitsSpace.SpaceBetweenKits", 9), 
	UNLOCKED_KITS_SPACE_NAME("UnlockedKitsSpace.Name", "."), 
	UNLOCKED_KITS_SPACE_COLOR("UnlockedKitsSpace.Color", "GREEN"),

	// Next Page Button
	NEXT_PAGE_BUTTON_MATERIAL("NextPageButton.Material", "ARROW"), NEXT_PAGE_BUTTON_NAME("NextPageButton.Name",
			"&aNext Page"),

	// Back Page Button
	BACK_PAGE_BUTTON_MATERIAL("BackPageButton.Material", "ARROW"), 
	BACK_PAGE_BUTTON_NAME("BackPageButton.Name","&cReturn"),

	// Background Items
	BACKGROUND_ITEMS_USE("BackgroundItems.Use", true), 
	BACKGROUND_ITEMS_MATERIAL("BackgroundItems.Material", "STAINED_GLASS_PANE"), 
	BACKGROUND_ITEMS_COLOR("BackgroundItems.Color", "BLUE"), 
	BACKGROUND_ITEMS_NAME("BackgroundItems.Name", ".");

	// Variables
	private final String path;
	private final Object def;
	private static YamlConfiguration file;

	KitsMenuConfig(String path, Object def) {
		this.path = path;
		this.def = def;
	}

	public String getPath() {
		return path;
	}

	public Object getDefault() {
		return def;
	}

	/**
	 * Set the {@code YamlConfiguration} to use.
	 * 
	 * @param config
	 *            The config to set.
	 */
	public static void setFile(YamlConfiguration config) {
		file = config;
	}

	@Override
	public String toString() {
		if (!(def instanceof String))
			return null;
		//
		final String gg = file.getString(path, (String) def);
		//
		return Util.wc(gg != null ? gg : "" + def);
	}

	public String toStringReplaPlayerName(String word) {
		return toString().replace("%PLAYER%", word);
	}

	public String[] toStringArray() {
		String s = toString();
		//
		if (s.contains("%n"))
			return s.split("%n");
		else
			return new String[] { s };
	}

	public int toInt() {
		if (!(def instanceof Integer))
			return 0;
		//
		return file.getInt(path, (Integer) def);
	}

	public boolean toBoolean() {
		if (!(def instanceof Boolean))
			return false;
		//
		return file.getBoolean(path, (Boolean) def);
	}
}
