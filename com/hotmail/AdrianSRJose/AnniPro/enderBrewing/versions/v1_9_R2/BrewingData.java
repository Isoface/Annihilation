package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.versions.v1_9_R2;

import net.minecraft.server.v1_9_R2.TileEntityBrewingStand;

class BrewingData extends com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.BrewingData {
	public BrewingData(TileEntityBrewingStand brewing) {
		super(asBukkitCopy(brewing.getContents()), brewing.getProperty(0), brewing.getProperty(1));
	}

	private static org.bukkit.inventory.ItemStack[] asBukkitCopy(net.minecraft.server.v1_9_R2.ItemStack[] stacks) {
		org.bukkit.inventory.ItemStack[] items = new org.bukkit.inventory.ItemStack[stacks.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asBukkitCopy(stacks[i]);
		}
		return items;
	}
}
