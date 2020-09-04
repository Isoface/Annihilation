package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An event called when an Item in the
 * {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} is clicked.
 */
public class ItemClickEvent {
	private Player player;
	private ClickType clicktype;
	private boolean goBack = false;
	private boolean close = false;
	private boolean update = false;
	private ItemStack stack;
	private Inventory inventory;
	private int slot;
	private int rawSlot;

	public ItemClickEvent(Player player, ItemStack stack, ClickType type, Inventory inventory, int slot, int rawSlot) {
		this.player = player;
		this.stack = stack;
		clicktype = type;
		this.inventory = inventory;
		this.slot = slot;
		this.rawSlot = rawSlot;
	}

	/**
	 * Gets the player who clicked.
	 *
	 * @return The player who clicked.
	 */
	public Player getPlayer() {
		return player;
	}

	public int getSlot() {
		return slot;
	}

	public int getRawSlot() {
		return rawSlot;
	}

	public ClickType getClickType() {
		return clicktype;
	}

	public ItemStack getClickedItem() {
		return stack;
	}

	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Checks if the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}
	 * will go back to the parent menu.
	 *
	 * @return True if the
	 *         {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} will go
	 *         back to the parent menu, else false.
	 */
	public boolean willGoBack() {
		return goBack;
	}

	/**
	 * Sets if the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} will
	 * go back to the parent menu.
	 *
	 * @param goBack
	 *            If the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}
	 *            will go back to the parent menu.
	 */
	public void setWillGoBack(boolean goBack) {
		this.goBack = goBack;
		if (goBack) {
			close = false;
			update = false;
		}
	}

	/**
	 * Checks if the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}
	 * will close.
	 *
	 * @return True if the
	 *         {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} will
	 *         close, else false.
	 */
	public boolean willClose() {
		return close;
	}

	/**
	 * Sets if the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} will
	 * close.
	 *
	 * @param close
	 *            If the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}
	 *            will close.
	 */
	public void setWillClose(boolean close) {
		this.close = close;
		if (close) {
			goBack = false;
			update = false;
		}
	}

	/**
	 * Checks if the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}
	 * will update.
	 *
	 * @return True if the
	 *         {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} will
	 *         update, else false.
	 */
	public boolean willUpdate() {
		return update;
	}

	/**
	 * Sets if the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu} will
	 * update.
	 *
	 * @param update
	 *            If the {@link com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu}
	 *            will update.
	 */
	public void setWillUpdate(boolean update) {
		this.update = update;
		if (update) {
			goBack = false;
			close = false;
		}
	}
}