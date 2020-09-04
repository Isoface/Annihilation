package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.StaticMenuItem} that
 * opens the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}'s
 * parent menu if it exists.
 */
public class BackMenuItem extends StaticMenuItem {

	public BackMenuItem() {
		super(ChatColor.RED + "Back", new ItemStack ( Material.FENCE_GATE ));
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		event.setWillGoBack(true);
	}
}