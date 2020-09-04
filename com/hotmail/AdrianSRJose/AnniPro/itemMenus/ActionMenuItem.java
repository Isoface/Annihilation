package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ActionMenuItem extends MenuItem {
	
	private ItemClickHandler handler;
//	private final MenuItem instance;

	public ActionMenuItem(String displayName, ItemClickHandler handler, ItemStack icon, String... lore) {
		super(displayName, icon, lore);
		this.handler = handler;
//		instance = this;
	}
	
	public ActionMenuItem(String displayName, ItemClickHandler handler, ItemStack icon, List<String> lore) {
		super(displayName, icon, arrayFromList(lore));
		this.handler = handler;
//		instance = this;
	}
	
	private static String[] arrayFromList(final List<String> list) {
		final String[] tor = new String[list.size()];
		for (int x = 0; x < tor.length; x++) {
			tor[x] = list.get(x);
		}
		return tor;
	}
	
	public ActionMenuItem(String displayName, ItemClickHandler handler, ItemStack icon) {
		this(displayName, handler, icon, new String[] {});
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		handler.onItemClick(event);
	}

	public ItemClickHandler getHandler() {
		return handler;
	}

	public void setHandler(ItemClickHandler handler) {
		this.handler = handler;
	}
}
