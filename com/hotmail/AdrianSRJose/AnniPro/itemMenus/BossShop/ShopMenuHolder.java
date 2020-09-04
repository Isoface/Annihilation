package com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;

public class ShopMenuHolder implements InventoryHolder {
	private ItemMenu menu;
	private Inventory inventory;

	public ShopMenuHolder(ItemMenu menu, Inventory inventory) {
		this.menu      = menu;
		this.inventory = inventory;
	}

	public ItemMenu getMenu() {
		return menu;
	}
	
	public boolean isPluginItem(int slot) {
		return slot < menu.getSize().getSize() && menu.getItems()[slot] != null;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
}