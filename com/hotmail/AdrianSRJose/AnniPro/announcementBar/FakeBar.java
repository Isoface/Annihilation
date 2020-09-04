package com.hotmail.AdrianSRJose.AnniPro.announcementBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class FakeBar implements Bar {
	private Map<UUID, Long> timers;

	public FakeBar() {
		timers = new HashMap<>();
	}

	@Override
	public void sendToPlayer(final Player player, final String message, final String bossbarMessage, final float percentOfTotal) {
		Long l = timers.get(player.getUniqueId());
		if (l == null || System.currentTimeMillis() >= l.longValue()) {
			Bukkit.getConsoleSender()
					.sendMessage("§c[Annihilation] This server is using an unsupported Spigot version (Phase Bars).!");
			Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Please, Contact with AdrianSR to fix it.!");
			//
			player.sendMessage(message);
			//
			timers.put(player.getUniqueId(), System.currentTimeMillis() + (16000L));
		}
	}

	@Override
	public void removeBossBar(Player player) {
	}
	
	@Override
	public void destroyBossBar() {
	}

	@Override
	public void destroyPhaseBar() {
	}
}
