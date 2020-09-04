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

public class RandomAmmuntSelectMenu extends ItemMenu {
	private int min = 1;
	private int max = 3;
	private final ItemMenu instance;

	//
	public RandomAmmuntSelectMenu(final Player owner) {
		super("§d§lRandom Amount to Drop", Size.THREE_LINE);
		instance = this;
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(owner.getUniqueId());
		if (ap != null) {
			Object d1 = ap.getData("key-min-ammount-selected");
			if (d1 != null && d1 instanceof Integer)
				min = ((Integer) d1).intValue();
			//
			//
			Object d2 = ap.getData("key-max-ammount-selected");
			if (d2 != null && d2 instanceof Integer)
				max = ((Integer) d2).intValue();
		}
		//
		setItem(10, new UpDownButton(false, true));
		setItem(11, new UpDownAmmountButton(min, true));
		setItem(12, new UpDownButton(true, true));
		//
		setItem(14, new UpDownButton(false, false));
		setItem(15, new UpDownAmmountButton(max, false));
		setItem(16, new UpDownButton(true, false));
		//
		setItem(25, new SaveCancelButton(false));
		setItem(26, new SaveCancelButton(true));
	}

	private class UpDownButton extends MenuItem {
		private final boolean positive;
		private final boolean bmin;

		//
		public UpDownButton(boolean positive, boolean min) {
			super((positive ? "§a§l" : "§c§l") + (positive ? "+" : "-"),
					new WoolItemStack ( positive ? WoolColor.GREEN : WoolColor.RED , 1 )
					);
			this.positive = positive;
			this.bmin = min;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			int set = this.bmin ? min : max;
			//
			if (positive) {
				if (set <= 64)
					set += 1;
			} else {
				if ((set - 1) > 0)
					set -= 1;
			}
			//
			if (bmin)
				min = set;
			else
				max = set;
			//
			instance.setItem((this.bmin ? 11 : 15), new UpDownAmmountButton((this.bmin ? min : max), bmin));
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
						ap.setData("key-min-ammount-selected", Integer.valueOf(min));
						ap.setData("key-max-ammount-selected", Integer.valueOf(max));
						//
						ap.setData("key-random-min-and-max-to-drop",
								new Integer[] { Integer.valueOf(min), Integer.valueOf(max) });
						//
						ap.setData("key-random-ammount-selected", "true");
						//
						p.sendMessage("§d§lCurrent Selected Random Amount: §c§lMin: §6§l" + min + " " + "§a§lMax: §6§l"
								+ max);
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}

	private class UpDownAmmountButton extends MenuItem {
		public UpDownAmmountButton(int ammount, boolean min) {
			super(min ? "§6§lMin Amount to Drop" : "§6§lMax Amount to Drop",
					new ItemStack(Material.STICK, ammount < 1 ? 1 : ammount));
		}
	}

}
