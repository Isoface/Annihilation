package com.hotmail.AdrianSRJose.AnniPro.announcementBar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class BossBarHandler implements Listener {
	public static BossBar bar;
	public static String message;
	public static double progrees;

	public BossBarHandler(JavaPlugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
	}
	
	public void pipi() {

	}

	public static void sendBar(final Player player, String message, final double progress)
			throws ClassNotFoundException {
		if (bar == null)
			bar = Bukkit.createBossBar(message, BarColor.BLUE, BarStyle.SOLID);
		//
		bar.setProgress(progress);
		//
		if (player != null && player.isOnline() && !containsPlayer(player))
			bar.addPlayer(player);
	}

	private static boolean containsPlayer(Player p) {
		if (p.getUniqueId() == null)
			return false;
		//
		boolean b = false;
		for (Player t : bar.getPlayers())
			if (t != null && t.isOnline())
				if (t.getUniqueId() != null && t.getUniqueId().equals(p.getUniqueId()))
					b = true;
		return b;
	}

	public static void updater(boolean defaultUpdater, boolean a, boolean b, String newMess, double newProgress) {
		if (!defaultUpdater) {
			if (a)
				bar.setTitle(newMess);
			if (b)
				bar.setProgress(newProgress);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (a)
						bar.setTitle(newMess);
					if (b)
						bar.setProgress(newProgress);
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 20L, 20L);
		}
	}

	public static void destroyBar() {
		if (bar != null) {
			bar.removeAll();
		}
	}

	public static void removePlayer(Player p) {
		if (bar != null && p.isOnline())
			bar.removePlayer(p);
	}

	public static void addPlayer(Player p) {
		if (bar != null && p.isOnline())
			bar.addPlayer(p);
	}

	public static void setMessage(String newMess) {
		if (bar != null && newMess != null)
			bar.setTitle(newMess);
	}

	public static void setProgress(double newProgress) {
		if (bar != null && newProgress <= 1.0 && newProgress >= 0.0)
			bar.setProgress(newProgress);
	}

	@EventHandler
	public void onQ(PlayerQuitEvent eve) { // !VersionUtils.isNewSpigotVersion()
		if (ServerVersion.serverOlderThan(ServerVersion.v1_9_R1) 
				|| bar == null || Game.isNotRunning()) {
			return;
		}
		
		final Player p = eve.getPlayer();
		if (bar.getPlayers().contains(p)) {
			removePlayer(p);
		}
	}

	@EventHandler
	public void onK(PlayerKickEvent eve) {
		if (ServerVersion.serverOlderThan(ServerVersion.v1_9_R1) 
				|| bar == null || Game.isNotRunning()) {
			return;
		}

		final Player p = eve.getPlayer();
		if (bar.getPlayers().contains(p)) {
			removePlayer(p);
		}
	}
	
	@EventHandler
	public void onJ(PlayerJoinEvent eve) {
		if (ServerVersion.serverOlderThan(ServerVersion.v1_9_R1) 
				|| bar == null || Game.isNotRunning()) {
			return;
		}

		final Player p = eve.getPlayer();
		addPlayer(p);
	}
}
