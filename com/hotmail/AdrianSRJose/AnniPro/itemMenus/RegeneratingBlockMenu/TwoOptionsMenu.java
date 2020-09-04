package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public class TwoOptionsMenu extends ItemMenu {
	private final Material b1;
	private final String op1Name;
	protected final String mess1;
	private final Material b2;
	private final String op2Name;
	protected final String mess2;
	//
	private final boolean sc;

	public TwoOptionsMenu(Material b1, String op1Name, String mess1, Material b2, String op2Name, String mess2,
			String title, boolean saveCancelButtons, String[] lore1, String[] lore2) {
		super("§d§l" + title, Size.THREE_LINE);
		//
		this.b1 = b1;
		this.b2 = b2;
		this.op1Name = op1Name;
		this.mess1 = mess1;
		this.op2Name = op2Name;
		this.mess2 = mess2;
		//
		setItem(12, new FirstSecondButton(true, lore1));
		setItem(14, new FirstSecondButton(false, lore2));
		//
		sc = saveCancelButtons;
		if (saveCancelButtons) {
			setItem(25, new SaveCancelButton(false));
			setItem(26, new SaveCancelButton(true));
		}
	}

	public TwoOptionsMenu(Material b1, String op1Name, String mess1, Material b2, String op2Name, String mess2,
			String title, boolean saveCancelButtons) {
		this(b1, op1Name, mess1, b2, op2Name, mess2, title, saveCancelButtons, new String[] {}, new String[] {});
	}

	public TwoOptionsMenu(Material b1, String op1Name, String mess1, Material b2, String op2Name, String mess2,
			boolean saveCancelButtons) {
		this(b1, op1Name, mess1, b2, op2Name, mess2, "§d§lSelect the desired option", saveCancelButtons);
	}

	private class FirstSecondButton extends MenuItem {
		private final boolean first;

		//
		public FirstSecondButton(boolean first, String[] lore) {
			super((first ? op1Name : op2Name), new ItemStack((first ? b1 : b2), 1), lore);
			this.first = first;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			if (p != null && p.isOnline()) {
				final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
				if (ap != null) {
					if (first) {
						if (sc)
							ap.setData("key-Two-Options", "key_-Regenerable-_key");
						//
						//
						final ItemStack s1 = eve.getInventory().getItem(12);
						final ItemMeta meta1 = s1.getItemMeta();
						//
						if (meta1 != null) {
							meta1.setDisplayName(op1Name + " §6§l(SELECTED)");
							s1.setItemMeta(meta1);
						}
						//
						final ItemStack s2 = eve.getInventory().getItem(14);
						final ItemMeta meta2 = s2.getItemMeta();
						//
						if (meta2 != null) {
							meta2.setDisplayName(op2Name);
							s2.setItemMeta(meta2);
						}
						//
						firstOption(eve);
					} else {
						if (sc)
							ap.setData("key-Two-Options", "key_-Unbreakable-_key");
						//
						//
						final ItemStack s1 = eve.getInventory().getItem(12);
						final ItemMeta meta1 = s1.getItemMeta();
						//
						if (meta1 != null) {
							meta1.setDisplayName(op1Name);
							s1.setItemMeta(meta1);
						}
						//
						final ItemStack s2 = eve.getInventory().getItem(14);
						final ItemMeta meta2 = s2.getItemMeta();
						//
						if (meta2 != null) {
							meta2.setDisplayName(op2Name + " §6§l(SELECTED)");
							s2.setItemMeta(meta2);
						}
						//
						secondOption(eve);
					}
				}
			}
		}
	}

	public void firstOption(ItemClickEvent eve) {
	}

	public void secondOption(ItemClickEvent eve) {
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
						final Object obj = ap.getData("key-Two-Options");
						if (obj != null && obj instanceof String) {
							String data = (String) obj;
							ap.setData("key-Regenreable_or_Unbreakable-key", data);
							//
							if (data.equals("key_-Unbreakable-_key"))
								p.sendMessage("" + mess1);
							else if (data.equals("key_-Regenerable-_key"))
								p.sendMessage("" + mess2);
							//
						}
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}
}
