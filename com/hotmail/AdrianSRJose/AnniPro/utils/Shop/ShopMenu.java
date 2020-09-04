package com.hotmail.AdrianSRJose.AnniPro.utils.Shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.google.common.base.Objects;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableShopItemStack;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loadable.LoadableShopPhaseItemStack;

public enum ShopMenu {
	
	BREWING_SHOP(ShopType.BREWING),
	WEAPON_SHOP(ShopType.WEAPON);
	
	private final ShopType                        type;
	private       ItemMenu                      handle;
	private       List<LoadableShopItemStack> contents;
	
	ShopMenu(ShopType type) {
		this.type     = type;
		this.contents = new ArrayList<LoadableShopItemStack>();
		
		/* build handle */
		this.update();
	}
	
	public ShopType getType() {
		return type;
	}
	
	public ItemMenu get() {
		return this.update().handle;
	}
	
	public ShopMenu update() {
		if (this.handle == null) { /* creating instance */
			this.handle = new ItemMenu(type.getMenuName(), type.getMenuSize());
		} else if (!this.handle.getSize().equals(type.getMenuSize())
				|| !Objects.equal(this.handle.getName(), type.getMenuName())) { /* updating menu */
			this.handle.setSize(type.getMenuSize());
			this.handle.setName(type.getMenuName());
		}
		
		
		/* just update shop contents */
		for (LoadableShopItemStack item : type.getContents()) {
			if ( (item == null || !item.isValid()) || this.contents.contains(item) ) {
				continue;
			}
			
			/* check item type */
			if (item instanceof LoadableShopPhaseItemStack) {
				if (Game.isNotRunning() || Game.getGameMap().getCurrentPhase() != ((LoadableShopPhaseItemStack) item).getPhase()) {
					continue;
				}
			}
			
			/* register added content to don't add again */
			this.contents.add(item); 
		}
		
		/* clear menu before show new contents */
		this.handle.clearAllItems();
		
		/* show contents */
		this.contents.forEach(item -> {
			if (item.getSlot() < this.handle.getSize().getSize()) {
				item.setOnItemMenu(this.handle);
			}
			
			/* update view */
			Bukkit.getOnlinePlayers().forEach(player -> { this.handle.update(player); });
		});
		return this;
	}
}