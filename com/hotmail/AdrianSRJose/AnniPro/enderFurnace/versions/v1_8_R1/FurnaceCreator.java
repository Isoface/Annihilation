package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_8_R1;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.EnderFurnace;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.IFurnace;

public class FurnaceCreator implements com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.FurnaceCreator {
	@Override
	public IFurnace createFurnace(final AnniPlayer player) {
		IFurnace f = new Furnace_V1_8_R1(player.getPlayer());
		if (EnderFurnace.getFurnaceData(player) != null)
			f.load(EnderFurnace.getFurnaceData(player));
		return f;
	}
}
