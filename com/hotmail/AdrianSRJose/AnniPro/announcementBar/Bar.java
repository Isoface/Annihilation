package com.hotmail.AdrianSRJose.AnniPro.announcementBar;

import org.bukkit.entity.Player;

public interface Bar {
	void sendToPlayer(Player player, String message, String BossBarMessage, float percentOfTotal);
	void removeBossBar(Player player);
	void destroyBossBar();
	void destroyPhaseBar();
}
