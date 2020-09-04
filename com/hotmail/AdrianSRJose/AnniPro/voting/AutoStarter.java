package com.hotmail.AdrianSRJose.AnniPro.voting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.Title.TitleHandler;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.Announcement;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.TempData;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting.VotingMap;

public class AutoStarter implements Listener {
	private final int players;
	private final int countdown;
	private boolean canRun;
	private int title = (Config.AUTO_START_COUNTDOWN_TIME.toInt() - 1); //GameVars.CountdowntoStart - 1;
	private int b = 1;

	private TempData data;

	public AutoStarter(Plugin p, int playersToStart, int countdown) {
		Bukkit.getPluginManager().registerEvents(this, p);
		players = playersToStart;
		this.countdown = countdown;
		canRun = true;
		data = null;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerCheck(PlayerJoinEvent event) {
		check();
	}

	@SuppressWarnings("deprecation")
	private void check() {
		if (!Game.isGameRunning() && canRun) {
			int count = Bukkit.getOnlinePlayers().size();

			if (count >= players) {
				canRun = false;
				Announcement ann = new Announcement(Lang.LOBBY_BAR.toString()).setTime(countdown);

				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (!KitUtils.isValidPlayer(pl)) {
						continue;
					}

					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						pl.sendTitle(ChatColor.GOLD + "§l" + countdown, "");
					} else {
						TitleHandler.getInstance().SendTitleToPlayer(pl, ChatColor.GOLD + "§l" + countdown, "", 6, 17, 6);
					}
					pl.playSound(pl.getLocation(), UniversalSound.ORB_PICKUP.asBukkit(), 2F, 0F);
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						// Resta
						title -= b;

						// Send Titles
						for (Player pl : Bukkit.getOnlinePlayers()) {
							if (title <= 5 && title >= 0 && Game.isNotRunning()) {
								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									if (title != 0) {
										pl.sendTitle(ChatColor.GOLD + "§l" + title, "");
										pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 0F);
									} else {
										pl.sendTitle(Lang.STARTING_TITLE.toString(), "");
										pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 0F);
										cancel();
									}
								} else { // VersionUtils.getVersion().contains("v1_8")
									if (title != 0) {
										TitleHandler.getInstance().SendTitleToPlayer(pl, ChatColor.GOLD + "§l" + title, "", 6, 17, 6);
										pl.playSound(pl.getLocation(), Sound.valueOf("ORB_PICKUP"), 2F, 0F);
									} else {
										TitleHandler.getInstance().SendTitleToPlayer(pl, Lang.STARTING_TITLE.toString(), "", 6, 17, 6);
										pl.playSound(pl.getLocation(), Sound.valueOf("LEVEL_UP"), 2F, 0F);
										cancel();
									}
								}
							}

							if ((title == 10 || title == 15 || title == 20 || title == 25) && Game.isNotRunning()) {
								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									pl.sendTitle(ChatColor.GOLD + "§l" + title, "");
									pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 0F);

								} else {
									TitleHandler.getInstance().SendTitleToPlayer(pl, ChatColor.GOLD + "§l" + title, "", 6, 17, 6);
									pl.playSound(pl.getLocation(), Sound.valueOf("ORB_PICKUP"), 2F, 0F);
								}

							}
							
							if (title == 7 && Game.isNotRunning()) {
								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									pl.sendTitle("", "");
								} else {
									TitleHandler.getInstance().SendTitleToPlayer(pl, "", "", 6, 17, 6);
								}
							}
						}
					}
				}.runTaskTimer(AnnihilationMain.INSTANCE, 20L, 20L);

				if (Config.MAP_LOADING_VOTING.toBoolean()) { // GameVars.Voting
					ann.setCallback(new Runnable() {
						@Override
						public void run() {
							try {
								if (count >= players) {
									VotingMap winner = MapVoting.getWinningMap ( );
									if ( winner != null ) {
										Bukkit.broadcastMessage ( Lang.WINNER_GAME_MAP.toStringReplacement ( winner.getName ( ) ) );
										if ( Game.loadGameMap ( winner.getName ( ) , false ) ) {
											Game.startGame ( );
										} else {
											Bukkit.broadcastMessage ( ChatColor.RED
													+ "There Has Been An Error In Loading The Map: " + winner.getName ( ) );
											Bukkit.broadcastMessage ( ChatColor.RED + "The Game Will Not Start." );
										}
									}
									
//									String winner = LobbyScoreboard.getWinningMap();
//									Bukkit.broadcastMessage(Lang.WINNER_GAME_MAP.toStringReplacement(winner));
//									if (Game.loadGameMap(winner, false)) {
//										Game.startGame();
//									} else {
//										Bukkit.broadcastMessage(ChatColor.RED
//												+ "There Has Been An Error In Loading The Map: " + winner);
//										Bukkit.broadcastMessage(ChatColor.RED + "The Game Will Not Start.");
//									}
								} else {
									AnnounceBar.getInstance()
											.countDown(new Announcement(ChatColor.RED + "Auto-Start aborted!")
													.setTime(2).setCallback(new Runnable() {
														@Override
														public void run() {
															data = AnnounceBar.getInstance().getData();
															AnnounceBar.getInstance().countDown(data);
															data = null;
														}
													}));

									Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Auto-Start aborted!");
								}

							} catch (Exception e) {
								Bukkit.getLogger().warning(
										"[ANNIHILATION] FATAL ERROR. VOTING IS ENABLED BUT THERE ARE NO MAPS IN THE WORLDS FOLDER!");
								Bukkit.getPluginManager().disablePlugin(AnnihilationMain.INSTANCE);
							}
						}
					});
				} else {
					ann.setCallback(new Runnable() {
						@Override
						public void run() {
							if (Game.loadGameMap(Config.MAP_LOADING_USE_MAP.toFile(), false)) {
								Game.startGame();
							} else {
								Bukkit.broadcastMessage(
										ChatColor.RED + "There Has Been An Error In Loading The Fixed Map: " + Config.MAP_LOADING_USE_MAP.toFile().getName());
								Bukkit.broadcastMessage(ChatColor.RED + "The Game Will Not Start.");
							}
						}
					});
				}
				AnnounceBar.getInstance().countDown(ann);
			}
		}
	}
}
