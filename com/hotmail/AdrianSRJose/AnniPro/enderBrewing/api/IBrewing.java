package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api;

import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

public interface IBrewing {
	void tick();

	void open();

	void clear();

	BrewingData getBrewingData();

	void load(BrewingData data);

	String name = Lang.PRIVATE_BREWING_NAME.toString();
}
