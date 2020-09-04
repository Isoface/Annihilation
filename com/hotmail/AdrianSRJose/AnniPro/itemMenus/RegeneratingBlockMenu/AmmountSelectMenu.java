package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public class AmmountSelectMenu extends ItemMenu {
	private final int type;
	private int amm = 1;
	private final ItemMenu instance;

	//
	public AmmountSelectMenu(int type, final Player owner) {
		super ( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Select the Amount to Drop" , Size.THREE_LINE );
		//
		instance = this;
		this.type = type;
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(owner.getUniqueId());
		if (ap != null) {
			if (ap.getData("key-ammount-selected" + type) != null
					&& ap.getData("key-ammount-selected" + type) instanceof Integer) {
				amm = ((Integer) ap.getData("key-ammount-selected" + type)).intValue();
			}
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

		public UpDownButton(boolean positive) {
			super( ( positive ? ChatColor.GREEN : ChatColor.RED ) + ChatColor.BOLD.toString ( ) + ( positive ? "+" : "-" ),
//					new ItemStack(Material.WOOL.toBukkit(), 1, (positive ? (byte) 13 : (byte) 14)));
//					(positive ? Material.GREEN_WOOL.toItemStack() : Material.RED_WOOL.toItemStack())
					new WoolItemStack ( positive ? WoolColor.GREEN : WoolColor.RED , 1 )
					);
			this.positive = positive;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			if (positive) {
				if (amm <= 64) {
					amm += 1;
				}
			} else {
				if ((amm - 1) > 0) {
					amm -= 1;
				}
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
			super( ( save ? ChatColor.GREEN : ChatColor.RED ) + ChatColor.BOLD.toString ( ) + ( save ? "Save" : "Cancel" ),
//					(save ? Material.GREEN_WOOL.toItemStack() : Material.RED_WOOL.toItemStack())
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
						if (type > 0) {
							ap.setData("key-ammount-selected" + type, Integer.valueOf(amm));
						} else {
							ap.setData("key-ammount-selected", Integer.valueOf(amm));
						}
						
						ap.setData("key-random-ammount-selected", "false");
						p.sendMessage ( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Current Selected Amount: " 
								+ ChatColor.GOLD + ChatColor.BOLD.toString ( ) + amm );
					}
				}
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}

	private class UpDownAmmountButton extends MenuItem {
		public UpDownAmmountButton(int ammount) {
			super ( ChatColor.GOLD + ChatColor.BOLD.toString ( ) + "Total Amount", new ItemStack(Material.STICK, ammount < 1 ? 1 : ammount));
		}
	}
}
