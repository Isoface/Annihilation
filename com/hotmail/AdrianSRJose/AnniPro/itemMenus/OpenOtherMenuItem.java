package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpenOtherMenuItem extends MenuItem {
	private final ItemMenu to;

	public OpenOtherMenuItem(String displayName, ItemStack icon, ItemMenu to) {
		super(displayName, icon);
		this.to = to;
	}

	public OpenOtherMenuItem(ItemMenu to) {
		this("Open Other Menu", new ItemStack(Material.ARROW), to);
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		if (to == null) {
			return;
		}

		final Player p = event.getPlayer();
		if (p == null || !p.isOnline()) {
			return;
		}

		// Open to
		to.open(p);
	}

	public ItemMenu getTo() {
		return to;
	}
}
