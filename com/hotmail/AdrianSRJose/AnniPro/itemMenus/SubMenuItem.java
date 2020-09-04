package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

/**
 * A {@link ninja.amp.ampmenus.items.MenuItem}
 * {@link ninja.amp.ampmenus.menus.ItemMenu}.
 */
public class SubMenuItem extends MenuItem {
	private final ItemMenu menu;

	public SubMenuItem(String displayName, ItemMenu menu, ItemStack icon, String... lore) {
		super(displayName, icon, lore);
		this.menu = menu;
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		event.setWillClose(true);
		final UUID ID = event.getPlayer().getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				Player p = Bukkit.getPlayer(ID);
				if (p != null && menu != null) {
					menu.open(p);
				}
			}
		}, 3);
	}
}