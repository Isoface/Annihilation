package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EmptyMenuItem extends MenuItem {
	public EmptyMenuItem(String displayName, boolean glass) {
		super(displayName, new ItemStack(!glass ? Material.AIR : Material.STAINED_GLASS_PANE, 1));
	}

	public EmptyMenuItem(String displayName, Material m) {
		super(displayName, new ItemStack(m, 1));
	}

	public EmptyMenuItem(String displayName, ItemStack m) {
		super(displayName, m);
	}

	public EmptyMenuItem() {
		this("", false);
	}
}
