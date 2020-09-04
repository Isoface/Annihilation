package com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

public class BossRewards extends ItemMenu
{
	private final ItemStack[] items;
	
	public BossRewards(final ItemStack[] items, final Size size) {
		super(Lang.BOSS_SHOP_NAME.toString(), size);
		this.items = items;
	}
	
	public void open(final Player p) {
		// check player
		if (p == null || !p.isOnline()) {
			return;
		}
		
		// set items
		for (int slot = 0; (slot < items.length && slot < getSize().getSize()); slot ++) {
			ItemStack item = items[slot];
			if (item != null) {
				setItem(slot, new ActionMenuItem(KitUtils.extractName(item, false), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						// get player.
						final Player p = event.getPlayer();
						// remove a boss star from his inventory.
						p.getInventory().removeItem(CustomItem.BossStar.toItemStack(1));
						// add item.
						p.getInventory().addItem(KitUtils.addBossObject(event.getClickedItem()));
						// close inventory.
						event.setWillClose(true);
						// update inventory
						p.updateInventory();
					}
				}, item, KitUtils.extractLore(item)));
			}
		}
		
		// open
		super.open(p);
		// update inventory
		p.updateInventory();
	}
}
