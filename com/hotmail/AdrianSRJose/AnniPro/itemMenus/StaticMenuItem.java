package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link ninja.amp.ampmenus.items.MenuItem} whose icon never changes.
 */
public class StaticMenuItem extends MenuItem {
	public StaticMenuItem(String displayName, ItemStack icon, String... lore) {
		super(displayName, icon, lore);
		setNameAndLore(getIcon(), getDisplayName(), getLore());
	}

	@Override
	public ItemStack getFinalIcon(Player player) {
		return getIcon();
	}
}
