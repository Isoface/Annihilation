package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BookItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ReturnMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.menu.MenuUtil;

public class SelectProductMenu {
	
	private final Player owner;
	private final List<Material> yes = new ArrayList<Material>();
	private final List<MenuItem> items = new ArrayList<MenuItem>();
	private final MenuItem[] opitm = new MenuItem[7];
	private final BookItemMenu menu;
	private final ItemMenu returnMenu;
	private final ReturnMenuItem back;

	//
	public SelectProductMenu(String title, final Player owner, ItemMenu returnMenu) {
		MenuItem curretSeted = null;
		//
		this.owner = owner;
		this.returnMenu = returnMenu; // this.title = title;
		//
		for (Material m : Material.values()) {
			if ( !MenuUtil.isValidForMenus ( m ) ) {
				continue;
			}
			
			// check is not a wool
			if (m.name().contains("WOOL")) {
				continue;
			}
			
			yes.add(m);
		}
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(owner);
		if (ap != null) {
			Object ov = ap.getData("key-Product-key-(verify)");
			if (ov != null && ov instanceof Material) {
				curretSeted = new MenuItem("§d§lCurrent selected Product: §6§l" + ((Material) ov).name(),
						new ItemStack ( ((Material) ov) , 1 ));
			}
		}
		//
		for (Material m : yes) {
			items.add(new selectItem(m));
		}
		//
		back = new ReturnMenuItem(this.returnMenu, "§c§lCancel", new WoolItemStack ( WoolColor.RED , 1 ));
		//
		opitm[0] = new SaveCancelButton(true);
		opitm[1] = back;
		if (curretSeted != null)
			opitm[4] = curretSeted;
		//
		//
		menu = new BookItemMenu(title, items, opitm, true, false, null, null);
	}

	public void open() {
		if (KitUtils.isValidPlayer(owner))
			menu.open(owner);
	}

	private Material sel;
	private int slotSel;
	private Inventory inv;

	private class selectItem extends MenuItem {
		private final Material m;

		//
		public selectItem(Material m) {
			super("§d§l" + m.name(), new ItemStack ( m , 1 ));
			this.m = m;
		}

		//
		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			if (p.isOnline()) {
				final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
				if (ap != null)
					ap.setData("key-Product-key-(verify)", m);
				//
				if (sel != null && inv != null) {
					for (MenuItem i : items) {
						if (i.getIcon().getType().equals(sel)) {
							ItemStack set = i.getIcon();
							ItemMeta meta = set.getItemMeta();
							if (meta == null)
								meta = Bukkit.getItemFactory().getItemMeta(set.getType());
							meta.setDisplayName("§d§l" + set.getType().name());
							set.setItemMeta(meta);
							i.setIcon(set);
							//
							inv.setItem(slotSel, set);
						}
					}
				}
				//
				p.updateInventory();
				//
				for (ItemMenu men : menu.getPages().values()) {
					men.update(p);
				}
				//
				final ItemStack st = eve.getClickedItem();
				final ItemMeta meta = st.getItemMeta();
				//
				if (meta != null) {
					//
					if (!meta.getDisplayName().contains("§6§l(SELECTED)")) {
						meta.setDisplayName(meta.getDisplayName() + " §6§l(SELECTED)");
						st.setItemMeta(meta);
					}
					//
					eve.getInventory().setItem(eve.getSlot(), st);
					//
					slotSel = eve.getSlot();
					sel = st.getType();
					inv = eve.getInventory();
				}
				//
				//
				p.updateInventory();
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
						final Object sc = ap.getData("key-Product-key-(verify)");
						if (sc != null && sc instanceof Material) {
							ap.setData("key-Product-key", sc);
							p.sendMessage(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
								+ "RegeneratingBlock Product Selected: " + ChatColor.GOLD + ChatColor.BOLD.toString ( ) + ((Material) sc).name());
						}
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}
}