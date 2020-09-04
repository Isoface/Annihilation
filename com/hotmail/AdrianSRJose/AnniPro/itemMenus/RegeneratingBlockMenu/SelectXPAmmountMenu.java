package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public class SelectXPAmmountMenu extends ItemMenu {
	private int amm = 0;
	private final ItemMenu instance;

	//
	public SelectXPAmmountMenu(final Player owner) {
		super("§d§lSelect the Xp Amount", Size.THREE_LINE);
		//
		instance = this;
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(owner.getUniqueId());
		if (ap != null) {
			if (ap.getData("key-xp-ammount-selected-key") != null
					&& ap.getData("key-xp-ammount-selected-key") instanceof Integer)
				amm = ((Integer) ap.getData("key-xp-ammount-selected-key")).intValue();
		}
		//
		//
		setItem(12, new UpDownButton(false));
		setItem(13, new UpDownAmmountButton(amm));
		setItem(14, new UpDownButton(true));
		//
		setItem(25, new SaveCancelButton(false));
		setItem(26, new SaveCancelButton(true));
	}

	private class UpDownButton extends MenuItem {
		private final boolean positive;

		//
		public UpDownButton(boolean positive) {
			super((positive ? "§a§l" : "§c§l") + (positive ? "+" : "-"),
					new WoolItemStack ( positive ? WoolColor.GREEN : WoolColor.RED , 1 )
					);
			this.positive = positive;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			if (positive) {
				if (amm <= 64)
					amm += 1;
			} else {
				if ((amm - 1) >= 0)
					amm -= 1;
			}
			//
			instance.setItem(13, new UpDownAmmountButton(amm));
			instance.update(eve.getPlayer());
		}
	}

	private class SaveCancelButton extends MenuItem {
		private final boolean save;

		//
		public SaveCancelButton(boolean save) {
			super((save ? "§a§l" : "§c§l") + (save ? "Save" : "Cancel"),
					new WoolItemStack ( save ? WoolColor.GREEN : WoolColor.RED , 1 )
					);
			this.save = save;
		}

		//
		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			if (p != null && p.isOnline()) {
				final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
				if (ap != null) {
					if (save) {
						ap.setData("key-xp-ammount-selected-key", Integer.valueOf(amm));
						p.sendMessage("§d§lCurrent Selected Xp: §6§l" + amm);
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}

	private class UpDownAmmountButton extends MenuItem {
		public UpDownAmmountButton(int ammount) {
			super("§d§lTotal Xp Amount: §6§l" + ammount, new ItemStack(Material.EXP_BOTTLE, 1));
		}
	}
}
