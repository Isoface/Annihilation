package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_8_R3;

import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.metadata.PlayerMetadataStore;

/**
 * Implementation based on {@link org.bukkit.metadata.MetadataStore} using the
 * uuid rather than name of the player. <br />
 * <b>See #19</b>
 */
public class NPCMetadataStore extends PlayerMetadataStore {

	@Override
	protected String disambiguate(OfflinePlayer player, String metadataKey) {
		return player.getUniqueId().toString() + ":" + metadataKey;
	}
}
