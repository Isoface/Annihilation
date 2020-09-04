package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public class CobblestoneOrAirReplaceSelectMenu extends TwoOptionsMenu {
	
	public CobblestoneOrAirReplaceSelectMenu() {
		super(Material.COBBLESTONE, "§d§lReplace with §6§lCobblestone",
				"§d§lThe block will be replaced by: §6§lCobblestone", Material.GLASS, "§d§lReplace with §6§lAir",
				"§d§lThe block will be replaced by: §6§lAir", "§d§lCobblestone or Air ?", false);
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
				ap.setData("key-replace-with-cobblestone-key(Verify)", Boolean.valueOf(true));
		}
	}

	@Override
	public void secondOption(ItemClickEvent eve) {
		final Player p = eve.getPlayer();
		if (p != null && p.isOnline()) {
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null)
				ap.setData("key-replace-with-cobblestone-key(Verify)", Boolean.valueOf(false));
		}
	}

	private class SaveCancelButton extends MenuItem {
		private final boolean save;

		public SaveCancelButton(boolean save) {
			super((save ? "§a§l" : "§c§l") + (save ? "Save" : "Cancel"),
					new WoolItemStack ( save ? WoolColor.GREEN : WoolColor.RED , 1 )
					);
			this.save = save;
		}
		
		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			if (p != null && p.isOnline()) {
				final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
				if (ap != null) {
					if (save) {
						final Object obj = ap.getData("key-replace-with-cobblestone-key(Verify)");
						if (obj != null && obj instanceof Boolean) {
							ap.setData("key-replace-with-cobblestone-key", obj);
							p.sendMessage ( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + " Your block will be replaced by: " 
									+ ChatColor.GOLD + ChatColor.BOLD.toString ( ) + ( ((Boolean) obj).booleanValue ( ) ? "Cobblestone" : "Air" ) );
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
