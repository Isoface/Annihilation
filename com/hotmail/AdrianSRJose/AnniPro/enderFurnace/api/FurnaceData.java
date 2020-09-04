package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api;

import org.bukkit.inventory.ItemStack;

import lombok.Setter;

public abstract class FurnaceData {
	private @Setter ItemStack[] items;
	private @Setter int burnTime;
	private @Setter int ticksForCurrentFuel;
	private @Setter int cookTime;

	public FurnaceData(ItemStack[] items, int burnTime, int ticksForCurrentFuel, int cookTime) {
		this.items = items;
		this.burnTime = burnTime;
		this.ticksForCurrentFuel = ticksForCurrentFuel;
		this.cookTime = cookTime;
	}

	public int getBurnTime() {
		return burnTime;
	}

	public int getTicksForCurrentFuel() {
		return ticksForCurrentFuel;
	}

	public int getCookTime() {
		return cookTime;
	}

	public ItemStack[] getItems() {
		return items;
	}
	
	@Override
	public String toString() {
		return "burnTime = " + burnTime + ", ticksForCurrentFuel = " + ticksForCurrentFuel + ", cookTime = " + cookTime + ", items = " + (items != null ? items.length : 0);
	}
	
	public void printData() {
		System.out.println(toString());
		if (items != null) {
			System.out.println("Items:");
			int x = 0;
			for (ItemStack st : items) {
				System.out.println("Item " + x + ":");
				System.out.println("Type:" + st.getType().name());
				System.out.println("Amount: " + st.getAmount());
				System.out.println("ShortDat: " + st.getDurability());
				x++;
			}
		}
	}
}
