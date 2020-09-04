package com.hotmail.AdrianSRJose.AnniPro.kits;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class IconPackage {
	private final ItemStack stack;
	private String name = null;
	private String[] lore = null;

	public IconPackage(ItemStack stack) {
		this.stack = stack;
	}

	public IconPackage(ItemStack stack, String name) {
		this(stack);
		this.name = name;
	}

	public IconPackage(ItemStack stack, String[] lore) {
		this(stack);
		this.lore = lore;
	}

	public IconPackage(ItemStack stack, String name, String[] lore) {
		this(stack);
		this.name = name;
		this.lore = lore;
	}

	public ItemStack getFinalIcon() {
		ItemStack s = stack.clone();
		if (name != null)
			KitUtils.setName(s, name);
		if (lore != null) {
			ArrayList<String> str = new ArrayList<String>();
			//
			for (String x : lore) {
				str.add(Util.wc(x));
			}
			//
			KitUtils.setLore(s, str);
		}
		return s;
	}

	public ItemStack getIcon() {
		return stack;
	}

	public String[] getLore() {
		return lore;
	}

	public String getName() {
		return name;
	}
}
