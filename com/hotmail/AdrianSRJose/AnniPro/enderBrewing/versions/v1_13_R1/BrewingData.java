package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.versions.v1_13_R1;

import net.minecraft.server.v1_13_R1.TileEntityBrewingStand;

class BrewingData extends com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.BrewingData {
	public BrewingData(TileEntityBrewingStand brewing) {
		super(brewing.getContents(), brewing.getProperty(0), brewing.getProperty(1));
	}
}
