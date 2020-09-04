package com.hotmail.AdrianSRJose.AnniPro.kits.Menu;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;

public class KitPrevlzerMenuItem extends MenuItem {
	
	private final Kit kit;

	public KitPrevlzerMenuItem(final Kit kit) {
		super(kit.getColoredName(), kit.getIconPackage().getIcon(), kit.getIconPackage().getLore());
		this.kit = kit;
	}

	public Kit getKit() {
		return kit;
	}

	@Override
	public String[] getLoreArray() {
		final String[] finalLore = KitsMenuConfig.FINAL_LORE.toStringArray();
		final String perm = KitsMenuConfig.UNLOCKED_KIT.toString();
		final boolean usePermLore = false; // perm != null && !perm.isEmpty();
		final String[] tor = new String[(getLore().size() + finalLore.length) + (usePermLore ? 1 : 0)];
		for (int x = 0; x < tor.length; x++) {
			if (x < getLore().size()) {
				tor[x] = getLore().get(x);
			}
		}

		for (int g = 0; g < finalLore.length; g++) {
			tor[(tor.length - finalLore.length - 1) + g] = finalLore[g];
		}

		if (usePermLore) {
			tor[tor.length - 1] = perm;
		}
		return tor;
	}
}
