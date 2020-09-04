package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.StaticMenuItem} that
 * closes the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}.
 */
public class CloseMenuItem extends StaticMenuItem {
	public CloseMenuItem(ItemStack icon) {
		super(ChatColor.RED + "Close", icon);
	}

	public CloseMenuItem() {
		this(new ItemStack(Material.RECORD_4 , 1));
	}

	public void setInMenu(ItemMenu menu) {
		if (menu != null) {
			menu.setItem((menu.getSize().getSize() - 9), this);
		}
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		event.setWillClose(true);
	}
}