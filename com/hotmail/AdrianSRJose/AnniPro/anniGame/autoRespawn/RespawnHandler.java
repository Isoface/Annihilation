package com.hotmail.AdrianSRJose.AnniPro.anniGame.autoRespawn;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;

public class RespawnHandler implements Listener {
	private static RespawnHandler instance;

	public static void register(Plugin plugin) {
		if (instance == null) {
			instance = new RespawnHandler();
			Bukkit.getPluginManager().registerEvents(instance, plugin);
		}
	}

	private RespawnPacket packet;

	private RespawnHandler() {
		try {
			Class<?> cl = Class.forName( ( "com.hotmail.AdrianSRJose.AnniPro.anniGame.autoRespawn.versions." + ServerVersion.getVersion().name() + "Packet" ) );
			Class<? extends RespawnPacket> pack = cl.asSubclass(RespawnPacket.class);
			RespawnPacket p = pack.newInstance();
			packet = p;
		} catch (Throwable t) {
			packet = new FakePacket();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void autoRespawn(PlayerDeathEvent event) {
		// Add Immunity Seconds
		if (Config.SPAWN_IMMUNITY_SECONDS.toInt() > 0) {
			KitUtils.addHitDamageImmunity(AnniPlayer.getPlayer(event.getEntity()), Config.SPAWN_IMMUNITY_SECONDS.toInt());
		}

		// Auto Respawn Task
		new AutoRespawnTask(event.getEntity()).runTaskLater(AnnihilationMain.INSTANCE, 2L);
	}

	private class AutoRespawnTask extends BukkitRunnable {
		private Player player;

		public AutoRespawnTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (KitUtils.isValidPlayer(player)) {
				// Send Respawn Packet
				packet.sendToPlayer(player);
				player = null;
			}
		}
	}

	private class FakePacket implements RespawnPacket {
		@Override
		public void sendToPlayer(final Player player) {
			player.sendMessage("WARNING: This server is using an Annihilation version that is not supported.");
		}
	}
}
