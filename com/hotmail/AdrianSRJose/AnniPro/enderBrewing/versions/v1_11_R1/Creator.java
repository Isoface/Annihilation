package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.versions.v1_11_R1;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.EnderBrewing;
import com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.IBrewing;

public class Creator implements com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.BrewingCreator {
	@Override
	public IBrewing createBrewing(AnniPlayer player) {
		IBrewing brewing = new AnniBrewing(player.getPlayer());
		if (EnderBrewing.getBrewingData(player) != null) {
			brewing.load(EnderBrewing.getBrewingData(player));
		}
		return brewing;
	}
}
