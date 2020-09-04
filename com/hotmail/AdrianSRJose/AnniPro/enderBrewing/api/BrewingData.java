package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api;

import lombok.Setter;

public abstract class BrewingData {
	private @Setter Object items;
	private @Setter int brewTime;
	private @Setter int fuelLevel;

	public BrewingData(Object items, int brewTime, int fuelLevel) {
		this.items = items;
		this.brewTime = brewTime;
		this.fuelLevel = fuelLevel;
	}

	public int getBrewTime() {
		return brewTime;
	}

	public int getFuelLevel() {
		return fuelLevel;
	}

	public Object getItems() {
		return items;
	}
}
