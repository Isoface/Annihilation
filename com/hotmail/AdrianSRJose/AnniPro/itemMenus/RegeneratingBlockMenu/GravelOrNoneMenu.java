package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

public class GravelOrNoneMenu extends TwoOptionsMenu {
	private final Player owner;
	private static final String gb = ChatColor.GRAY.toString() + ChatColor.BOLD.toString();

	//
	public GravelOrNoneMenu(Player owner) {
		super(Material.GRAVEL, "§d§lTypical Gravel drop effect", "§d§lSelectecd Effect: §6§lGravel",
				Material.THIN_GLASS, "§d§lNone", "§d§lSelectecd Effect: §6§lNone", "§d§lSelect the Drop Effect", false,
				new String[] { "", gb + "This Effect Drop:", gb + "", gb + "§6§l(Random)§7§l:",
						gb + "--------------------------------",
						gb + "|   §6§lMaterial   §7§l|      §6§lAmount             §7§l|",
						gb + "|-------------------------------|",
						gb + "|  - §6§lFlint      §7§l|   §6§l(between 0 and 3)  §7§l|",
						gb + "|  - §6§lArrows   §7§l|   §6§l(between 0 and 4)  §7§l|",
						gb + "|  - §6§lString    §7§l|   §6§l(between 0 and 2)  §7§l|",
						gb + "|  - §6§lBone      §7§l|   §6§l(between 0 and 2)  §7§l|",
						gb + "|  - §6§lFeather  §7§l|   §6§l(between 0 and 3)  §7§l|",
						gb + "--------------------------------", "§e§l(Without your Selected §6§lProduct§e§l)" },
				new String[] { "", gb + "This Effect Drop:", "", gb + "Only Your Selected §6§lProduct" });
		//
		this.owner = owner;
		//
		setItem(25, new SaveCancelButton(false));
		setItem(26, new SaveCancelButton(true));
	}

	@Override
	public void firstOption(ItemClickEvent eve) {
		final Player p = eve.getPlayer();
		if (p != null && p.isOnline()) {
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null)
				ap.setData("key-Effect-Gravel-or-None-key(Verify)", "Gravel");
		}
	}

	@Override
	public void secondOption(ItemClickEvent eve) {
		final Player p = eve.getPlayer();
		if (p != null && p.isOnline()) {
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null)
				ap.setData("key-Effect-Gravel-or-None-key(Verify)", "None");
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
						final Object obj = ap.getData("key-Effect-Gravel-or-None-key(Verify)");
						//
						if (obj != null && obj instanceof String) {
							ap.setData("key-Effect-Gravel-or-None-key", obj);
							p.sendMessage("§d§lCurrent Effect Selected: §6§l" + (String) obj);
						}
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}

	public void specialOpen() {
		if (KitUtils.isValidPlayer(owner))
			open(owner);
	}
}
