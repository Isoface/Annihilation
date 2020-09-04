package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class RegenerationBlockCreatorMenu {
	private static final Map<UUID, PrincipalPage> regeneratingBlockCreatorMenus = new HashMap<UUID, PrincipalPage>();

	public static PrincipalPage getMenu(final Player p) {
		if (!regeneratingBlockCreatorMenus.containsKey(p.getUniqueId())
				|| regeneratingBlockCreatorMenus.get(p.getUniqueId()) == null)
			regeneratingBlockCreatorMenus.put(p.getUniqueId(), new PrincipalPage(p));
		//
		return regeneratingBlockCreatorMenus.get(p.getUniqueId());
	}
}
