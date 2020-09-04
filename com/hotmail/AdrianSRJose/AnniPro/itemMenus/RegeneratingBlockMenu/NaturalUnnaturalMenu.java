package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public class NaturalUnnaturalMenu extends TwoOptionsMenu {
	//
	public NaturalUnnaturalMenu() {
		super(Material.GRASS, "§d§lNormal Drop §6§l(Natural)", "§d§lSelected Drop Type: §6§l" + "Natural",
				Material.CHEST, "§d§lDrop to Inventory §6§l(Unnatural)", "§d§lSelected Drop Type: §6§l" + "Unnatural",
				"§d§lNatural or Unnatural Droped?", false);
		//
		setItem(25, new SaveCancelButton(false));
		setItem(26, new SaveCancelButton(true));
	}

	@Override
	public void firstOption(ItemClickEvent eve) {
		final Player p = eve.getPlayer();
		if (p != null && p.isOnline()) {
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null) {
				ap.setData("key-naturalBreak-key(Verify)", Boolean.valueOf(true));
			}
		}
	}

	@Override
	public void secondOption(ItemClickEvent eve) {
		final Player p = eve.getPlayer();
		if (p != null && p.isOnline()) {
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null) {
				ap.setData("key-naturalBreak-key(Verify)", Boolean.valueOf(false));
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
						final Object obj = ap.getData("key-naturalBreak-key(Verify)");
						if (obj != null && obj instanceof Boolean) {
							boolean wc = ((Boolean) obj).booleanValue();
							//
							ap.setData("key-naturalBreak-key", obj);
							p.sendMessage("§d§lYour Drop Type is: §6§l" + (wc ? "Natural" : "Unnatural"));
						}
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}
	//
}
