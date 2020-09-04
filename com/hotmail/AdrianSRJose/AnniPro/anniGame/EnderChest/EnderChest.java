package com.hotmail.AdrianSRJose.AnniPro.anniGame.EnderChest;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class EnderChest {
	
	private final UUID ownerID;
	private Inventory inventory;
	private String inventory_name;
	private final List<ItemFromSection> items;
	private final int size;

	EnderChest(final AnniPlayer pl, EnderChestConfig config) {
		ownerID = pl.getID();
		inventory = Bukkit.createInventory(pl.getPlayer(), Size.fit(config.getSlots()).getSize(),
				Util.wc(config.getName()));
		inventory_name = Util.wc ( config.getName ( ) );
		size = Size.fit(config.getSlots()).getSize();
		items = config.getItems();
	}
	
	public UUID getOwnerID ( ) {
		return ownerID;
	}
	
	public Inventory getInventory ( ) {
		return inventory;
	}
	
	public String getInventoryName ( ) {
		return inventory_name;
	}

	public List < ItemFromSection > getItems ( ) {
		return items;
	}
	
	public int getSize ( ) {
		return size;
	}
	
	public void open() {
		// Get and Check Player
		Player p = Bukkit.getPlayer(ownerID);
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}

		// Set Items
		setItemsFromSection(inventory, p);
		
		// Open
		p.openInventory(inventory);
	}
	
	public void clear() {
		// Get and Check Player
		Player p = Bukkit.getPlayer(ownerID);
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}
		
		// clear
		inventory.clear();
		
		// set items from section
		setItemsFromSection(inventory, p);
		
		// update
		try {
			p.updateInventory();
		}
		catch(Throwable t) {
			// ignore
		}
	}
	
	public void setItem(final int x, final ItemStack st) {
		// Get and Check Player
		Player p = Bukkit.getPlayer(ownerID);
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}
		
		// set items from section
		setItemsFromSection(inventory, p);
		
		// set
		inventory.setItem(x, st);
		
		// update
		try {
			p.updateInventory();
		}
		catch(Throwable t) {
			// ignore
		}
	}
	
	private void setItemsFromSection(final Inventory inv, final Player p) {
		for (ItemFromSection item : items) {
			if (item.isValid()) {
				// Check
				if (item.getSlot() >= size) {
					continue;
				}

				// Check Permission
				if (item.isRequiresPermission() && item.getPermission() != null && !item.getPermission().isEmpty()
						&& p.hasPermission(item.getPermission())) {
					continue;
				}

				// Set Item as Blocked Slot
				inventory.setItem(item.getSlot(), item.toItemStack());
			}
		}
	}
	
	public boolean isItemFromConfig(int slot, ItemStack stackInSLot) {
		for (ItemFromSection item : getItems()) {
			if (item == null) {
				continue;
			}

			if (item.getSlot() == slot) {
				if (item.toItemStack() != null && item.toItemStack().getType() != null
						&& item.toItemStack().getType() != Material.AIR) {
					if (stackInSLot != null) {
						if (stackInSLot.getType() == item.toItemStack().getType()) {
							return true;
						} else
							continue;
					} else
						continue;
				} else
					continue;
			} else
				continue;
		}
		return false;
	}
}
