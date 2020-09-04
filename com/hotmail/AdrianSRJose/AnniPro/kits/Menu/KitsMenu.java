package com.hotmail.AdrianSRJose.AnniPro.kits.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BookItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.EmptyMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.NextMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ReturnMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class KitsMenu extends BookItemMenu {
	
	public KitsMenu(final Player owner) {
		// Constructor
		super(KitsMenuConfig.MENU_TITLE.toString().replace("%PLAYER%", owner.getName()), iconss(owner.getUniqueId()),
				optionsItems(), false, false, getNextButton(), getBackButton());
	}

	private static MenuItem[] optionsItems() {
		// make array.
		MenuItem[] tor = new MenuItem[7];
		
		// check is enabled background.
		if (!KitsMenuConfig.BACKGROUND_ITEMS_USE.toBoolean()) {
			return tor;
		}
		
		// create background.
		for (int x = 0; x < tor.length; x++) {
			// get material.
			Material m = Util.getMaterial(KitsMenuConfig.BACKGROUND_ITEMS_MATERIAL.toString(), Material.STAINED_GLASS_PANE);
			
			// get short.
			short t = GlassColor.getGlassColor(KitsMenuConfig.BACKGROUND_ITEMS_COLOR.toString(), GlassColor.BLUE).shortValue();
			
			// set item.
			tor[x] = new EmptyMenuItem(KitsMenuConfig.BACKGROUND_ITEMS_NAME.toString(),
					new ItemStack(m, 1, m == Material.STAINED_GLASS_PANE ? t : (short) 0));
		}
		return tor;
	}

	private static NextMenuItem getNextButton() {
		return new NextMenuItem(null, KitsMenuConfig.NEXT_PAGE_BUTTON_NAME.toString(),
				Util.getMaterial(KitsMenuConfig.NEXT_PAGE_BUTTON_NAME.toString(), Material.ARROW));
	}

	private static ReturnMenuItem getBackButton() {
		return new ReturnMenuItem(null, KitsMenuConfig.BACK_PAGE_BUTTON_NAME.toString(),
				Util.getMaterial(KitsMenuConfig.BACK_PAGE_BUTTON_NAME.toString(), Material.ARROW));
	}

	private static List<MenuItem> iconss(final UUID ownerID) {
		final List<Kit> kits = Kit.getKitList();
		final List<Kit> canUse = new ArrayList<Kit>();
		final List<Kit> cantUse = new ArrayList<Kit>();
		final List<MenuItem> tor = new ArrayList<MenuItem>();
		for (Kit k : kits) {
			tor.add(new KitMenuItem(k, Bukkit.getPlayer(ownerID)));
		}

		if (KitsMenuConfig.SPACE_USE.toBoolean()) {
			final int spaces = KitsMenuConfig.SPACE_SPACES.toInt();
			int start = KitsMenuConfig.SPACE_START_SLOT.toInt();
			final Material m = Util.getMaterial(KitsMenuConfig.SPACE_MATERIAL.toString(), Material.STAINED_GLASS_PANE);
			start = start < 0 ? 0 : start;
			if (spaces > 0) {
				int f = 0;
				int fx = 0;
				for (int x = start; f < spaces; x++) {
					tor.set(x, new EmptyMenuItem(KitsMenuConfig.SPACE_NAME.toString(), new ItemStack(m, 1,
							m == Material.STAINED_GLASS_PANE
									? GlassColor.getGlassColor(KitsMenuConfig.SPACE_COLOR.toString(), GlassColor.WITHE)
											.shortValue()
									: (short) 0)));
					fx = x;
					f++;
				}

				for (int x = tor.size(); x > fx; x--) {
					if (x < tor.size()) {
						tor.remove(tor.get(x));
					}
				}

				for (int x = (f + 1); x < kits.size(); x++) {
					tor.add(new KitMenuItem(kits.get(x), Bukkit.getPlayer(ownerID)));
				}
			}
		}

		if (KitsMenuConfig.UNLOCKED_KIT_SPACE_USE.toBoolean()) {
			final int spaces = KitsMenuConfig.UNLOCKED_KITS_SPACE_SPACE_BETWEEN_KITS.toInt();
			final Material m = Util.getMaterial(KitsMenuConfig.UNLOCKED_KITS_SPACE_MATERIAL.toString(),
					Material.STAINED_GLASS_PANE);
			final GlassColor gl = GlassColor.getGlassColor(KitsMenuConfig.UNLOCKED_KITS_SPACE_COLOR.toString(),
					GlassColor.GREEN);
			for (Kit k : kits) {
				if (k.hasPermission(Bukkit.getPlayer(ownerID))) {
					canUse.add(k);
				} else {
					cantUse.add(k);
				}
			}

			tor.clear();
			for (Kit cu : canUse) {
				tor.add(new KitMenuItem(cu, Bukkit.getPlayer(ownerID)));
			}

			if (!cantUse.isEmpty()) {
				for (int x = 0; x < spaces; x++) {
					tor.add(new EmptyMenuItem(KitsMenuConfig.SPACE_NAME.toString(),
							new ItemStack(m, 1, m == Material.STAINED_GLASS_PANE ? gl.shortValue() : (short) 0)));
				}
			}

			for (Kit ct : cantUse) {
				tor.add(new KitMenuItem(ct, Bukkit.getPlayer(ownerID)));
			}
		}
		return tor;
	}
}
