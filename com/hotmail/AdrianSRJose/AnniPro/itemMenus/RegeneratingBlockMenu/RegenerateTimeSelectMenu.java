package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class RegenerateTimeSelectMenu extends ItemMenu {
	private int time = 10;
	private TimeUnit unit = TimeUnit.SECONDS;
	private final TimeUnit[] uitems = new TimeUnit[] { TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS };
	private final ItemMenu instance;

	//
	public RegenerateTimeSelectMenu(final Player owner) {
		super("§d§lSelect the Regenerate Time", Size.THREE_LINE);
		//
		instance = this;
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(owner.getUniqueId());
		if (ap != null) {
			if (ap.getData("key-regenerate-time-selected") != null
					&& ap.getData("key-regenerate-time-selected") instanceof Integer) {
				time = ((Integer) ap.getData("key-regenerate-time-selected")).intValue();
			}
		}
		//
		//
		setItem(10, new UpDownButton(false));
		setItem(11, new UpDownAmmountButton(unit));
		setItem(12, new UpDownButton(true));
		//
		setItem(25, new SaveCancelButton(false));
		setItem(26, new SaveCancelButton(true));

		for (int x = 0; x < 3; x++) {
			TimeUnit u = uitems[x];
			int slot = (14 + x);
			//
			setItem(slot, new unitButton(u));
		}
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
				time += 1;
			} else {
				if ((time - 1) > 0)
					time -= 1;
			}
			//
			instance.setItem(11, new UpDownAmmountButton(unit));
			instance.update(eve.getPlayer());
		}
	}

	private ItemStack oldC = null;
	private String rn = "";
	private Integer slot = null;

	//
	private class unitButton extends MenuItem {
		private final String name;
		private final TimeUnit buttonUnit;

		//
		public unitButton(TimeUnit un) {
			super("§d§l" + un.name(), new ItemStack(Material.WATCH));
			name = "§d§l" + un.name();
			this.buttonUnit = un;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			final String rc = Util.remC("" + eve.getClickedItem().getItemMeta().getDisplayName());
			//
			if (rc.equalsIgnoreCase("SECONDS") || rc.equalsIgnoreCase("MINUTES") || rc.equalsIgnoreCase("HOURS")) {
				if (oldC != null && slot != null) {
					final Inventory inv = eve.getInventory();
					if (inv != null) {
						final ItemStack set = oldC;
						final ItemMeta mt = set.getItemMeta();
						mt.setDisplayName(rn);
						set.setItemMeta(mt);
						//
						inv.setItem(slot, set);
					}
				}
				//
				final ItemStack st = eve.getClickedItem();
				rn = name;
				oldC = st;
				slot = Integer.valueOf(eve.getSlot());
				//
				if (!st.getItemMeta().getDisplayName().contains("§6§l(SELECTED)")) {
					final ItemMeta meta = st.getItemMeta();
					meta.setDisplayName(name + " §6§l(SELECTED)");
					st.setItemMeta(meta);
				}
				//
				eve.getPlayer().updateInventory();
				//
				unit = buttonUnit;
				instance.setItem(11, new UpDownAmmountButton(unit));
				instance.update(eve.getPlayer());
			}
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
						ap.setData("key-regenerate-time-selected", Integer.valueOf(time));
						ap.setData("key-regenerate-key-Unit", unit.name());
						//
						p.sendMessage("§d§lRegeneration Time Selected: §6§l" + time + " " + unit.name());
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}

	private class UpDownAmmountButton extends MenuItem {
		public UpDownAmmountButton(TimeUnit unit) {
			super("§d§lRegenerate Time: §6§l" + time + " " + unit.name().substring(0, 1),
					new ItemStack(Material.STICK, 1));
		}
	}
}
