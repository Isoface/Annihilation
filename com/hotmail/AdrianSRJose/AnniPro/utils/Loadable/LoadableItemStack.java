package com.hotmail.AdrianSRJose.AnniPro.utils.Loadable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.YamlSaveable;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopMenuItem;

public class LoadableItemStack implements YamlSaveable {
	
	public static final String LOADABLE_ITEM_STACK_MATERIAL_KEY = "Material";
	public static final String     LOADABLE_ITEM_STACK_COST_KEY = "Cost";
	public static final String   LOADABLE_ITEM_STACK_AMOUNT_KEY = "Amount";
	public static final String     LOADABLE_ITEM_STACK_SLOT_KEY = "Slot";
	
	private String   material;
	private int          cost;
	private int        amount;
	private int          slot;
	private List<String> lore;

	public LoadableItemStack(String material, int cost, int amount, int slot, List<String> lore) {
		this.material = material;
		this.cost     = cost;
		this.amount   = amount;
		this.slot     = slot;
		this.lore     = lore != null ? lore : new ArrayList<String>();
	}

	public LoadableItemStack(ConfigurationSection sc, List<String> lore) {
		if (sc != null) {
			this.material = sc.getString(LOADABLE_ITEM_STACK_MATERIAL_KEY, "AIR");
			if (sc.isInt(LOADABLE_ITEM_STACK_COST_KEY)) {
				cost = sc.getInt(LOADABLE_ITEM_STACK_COST_KEY);
			}

			if (sc.isInt(LOADABLE_ITEM_STACK_SLOT_KEY)) {
				slot = sc.getInt(LOADABLE_ITEM_STACK_SLOT_KEY);
			}

			if (sc.isInt(LOADABLE_ITEM_STACK_AMOUNT_KEY)) {
				amount = sc.getInt(LOADABLE_ITEM_STACK_AMOUNT_KEY);
			}
			this.lore = lore;
		}
	}
	
	public void setOnItemMenu(ItemMenu menu, int slot) {
		Material m = Material.getMaterial(material);
		if (m == null) {
			return;
		}

		ItemStack st = new ItemStack(m, amount);
		st.setAmount(amount);
		menu.setItem(slot, new ShopMenuItem(st, st, cost, lore));
	}

	public void setOnItemMenu(ItemMenu menu) {
		setOnItemMenu(menu, this.slot);
	}

	public String getItemMaterial() {
		return material;
	}

	public int getCost() {
		return cost;
	}

	public int getAmount() {
		return this.amount;
	}

	public int getSlot() {
		return slot;
	}
	
	public boolean isValid() {
		return getSlot() >= 0 && getAmount() > 0 && getCost() >= 0;
	}
	
	@Override
	public int save(ConfigurationSection section) {
		int     save = 0;
		if (section == null) {
			return save;
		} else if ( !isValid() ) {
			throw new IllegalArgumentException("The invalid LoadableItemStacks cannot be saved!");
		}
		
		/* save values */
		if (material != null) { /* item material */
			save += Util.setUpdatedIfNotEqual(section, LOADABLE_ITEM_STACK_MATERIAL_KEY, material);
		}
		
		save += Util.setUpdatedIfNotEqual(section, LOADABLE_ITEM_STACK_COST_KEY, cost);     /* item cost */
		save += Util.setUpdatedIfNotEqual(section, LOADABLE_ITEM_STACK_AMOUNT_KEY, amount); /* item amount */
		save += Util.setUpdatedIfNotEqual(section, LOADABLE_ITEM_STACK_SLOT_KEY, slot);     /* item slot */
		return save;
	}

	@Override
	public int saveNotSet(ConfigurationSection section) {
		int     save = 0;
		if (section == null) {
			return save;
		} else if ( !isValid() ) {
			throw new IllegalArgumentException("The invalid LoadableItemStacks cannot be saved!");
		}
		
		/* save not set values */
		if (material != null) { /* item material */
			save += Util.setDefaultIfNotSet(section, LOADABLE_ITEM_STACK_MATERIAL_KEY, material);
		}
		
		save += Util.setDefaultIfNotSet(section, LOADABLE_ITEM_STACK_COST_KEY, cost);     /* item cost */
		save += Util.setDefaultIfNotSet(section, LOADABLE_ITEM_STACK_AMOUNT_KEY, amount); /* item amount */
		save += Util.setDefaultIfNotSet(section, LOADABLE_ITEM_STACK_SLOT_KEY, slot);     /* item slot */
		return save;
	}
}
