package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public class RandomOrNormalAmmountMenu extends ItemMenu {

	public RandomOrNormalAmmountMenu(ItemMenu returnMenu) {
		super("§d§lRandom or Normal Drop?", Size.THREE_LINE);
		//
		setItem(26, new ActionMenuItem("§c§lCancel", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent eve) {
				if (returnMenu != null)
					returnMenu.open(eve.getPlayer());
				else
					eve.setWillClose(true);
			}
		}, new WoolItemStack ( WoolColor.RED , 1 ) ) {
		});
		//
		setItem(12, new optionsItem(true));
		setItem(14, new optionsItem(false));
	}

	private class optionsItem extends MenuItem {
		private final boolean random;

		//
		public optionsItem(boolean random) {
			super((random ? "§d§lRandom" : "§d§lNormal"),
					new ItemStack((random ? Material.SEEDS : Material.REDSTONE), 1));
			this.random = random;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			//
			if (p != null && p.isOnline()) {
				if (random)
					new RandomAmmuntSelectMenu(p).open(p);
				else
					new AmmountSelectMenu(1, p).open(p);
			}
		}
	}
}
