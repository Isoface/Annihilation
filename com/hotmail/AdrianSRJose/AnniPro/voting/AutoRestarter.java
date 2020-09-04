package com.hotmail.AdrianSRJose.AnniPro.voting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.Announcement;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.TempData;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;

public class AutoRestarter implements Listener {
	private final int players;
	private final int countdown;

	private boolean canRun;
	private boolean countingDown;

	private TempData data;

	public AutoRestarter(Plugin p, int playersToRestart, int countdown) {
		Bukkit.getPluginManager().registerEvents(this, p);
		if (playersToRestart < 0)
			playersToRestart = 0;
		if (countdown < 1)
			countdown = 1;
		players = playersToRestart;
		this.countdown = countdown;
		canRun = true;
		countingDown = false;
		data = null;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerCheck(PlayerQuitEvent event) {
		check();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void teleportCheck(PlayerKickEvent event) {
		check();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerCheck(PlayerJoinEvent event) {
		check();
	}

	private void beginRestart() {
		canRun = false;
		countingDown = true;
		data = AnnounceBar.getInstance().getData();
		//
		if (Config.AUTO_RESTART_COMMAND.toString().equals("")) { 
			Bukkit.broadcastMessage(
					"The auto restart feature has been activated, but no end of game command was specified.");
			Bukkit.broadcastMessage(
					"Please have an admin set an end of game command if he wants this feature to work.");
		} else {
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "Auto-restart activated! restarting the server in " + countdown + " seconds.");
			Announcement ann = new Announcement(ChatColor.DARK_PURPLE + "Auto-restart in: {#}");
			ann.setTime(countdown).setCallback(new Runnable() {
				@Override
				public void run() {
					countingDown = false;
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.AUTO_RESTART_COMMAND.toString());
				}
			});
			AnnounceBar.getInstance().countDown(ann);
		}
	}

	public void check() {
		Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (Game.isGameRunning() && Game.getGameMap().getCurrentPhase() >= Config.AUTO_RESTART_MINIMAL_PHASE_TO_RESTART.toInt()) { // GameVars.MinimalPhaseToRestart
					if (countingDown && !canRun) {
						int count = Bukkit.getOnlinePlayers().size();
						if (count > players) {
							AnnounceBar.getInstance()
									.countDown(new Announcement(ChatColor.RED + "Auto-restart aborted!").setTime(2)
											.setCallback(new Runnable() {
												@Override
												public void run() {
													AnnounceBar.getInstance().countDown(data);
													data = null;
												}
											}));
							canRun = true;
							countingDown = false;

							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Auto-Restart aborted!");
						}
					} else if (canRun) {
						int count = Bukkit.getOnlinePlayers().size();

						if (count <= players)
							beginRestart();
					}
				}
			}
		}, 40);
	}
}
