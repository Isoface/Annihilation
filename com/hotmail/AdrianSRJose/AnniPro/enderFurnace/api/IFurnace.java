package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api;

public interface IFurnace {
	void tick();

	void open();

	void clear();

	FurnaceData getFurnaceData();

	void load(FurnaceData data);
}
