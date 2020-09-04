package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.awt.image.BufferedImage;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.bobacadodl.imgmessage.ImageChar;
import com.bobacadodl.imgmessage.ImageMessage;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameEndEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.NexusHitEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.TeamDeathEvent;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class Nexus implements Listener {
	public Nexus(AnniTeam team) {
		Team = team;
		Location = null;
	}

	public final AnniTeam Team;
	private Loc Location;

	public void setLocation(Loc loc) {
		Location = loc;
	}

	public Loc getLocation() {
		return Location;
	}

	public static void gameOverCheck() {
		int total = AnniTeam.Teams.length;
		int destroyed = 0;
		AnniTeam winner = null;
		//
		for (AnniTeam team : AnniTeam.Teams) {
			if (team.isTeamDead())
				destroyed++;
			else
				winner = team;
		}
		//
		if (destroyed == total - 1)
			AnniEvent.callEvent(new GameEndEvent(winner));
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	private void NexusCheck(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE && Game.isGameRunning() && Game.getGameMap() != null) {
			// Check this location is a valid Location and Team is not dead.
			if (Location != null && !Team.isTeamDead()) {
				// Get block location (nexus location)
				final Location loc = event.getBlock().getLocation();
				if (Location.equals(loc)) {
					// Check can get Damage and cancell BlockBreakEvent
					event.setCancelled(true);
					// Get AnniPlayer (Hiter)
					final AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
					if (Game.getGameMap().canDamageNexus()) {
						if (p != null && p.hasTeam() && !p.getTeam().equals(Team)) {
							// Call Nexus Hit Event
							NexusHitEvent e = new NexusHitEvent(p, this, (1 * Game.getGameMap().getDamageMultiplier()));
							Bukkit.getPluginManager().callEvent(e);

							// Check is not cancelled and if his damage is > to 0
							if (!e.isCancelled() && e.getDamage() > 0) {
								final Sound play = UniversalSound.EXPLODE.asBukkit();
								final Sound abl = UniversalSound.ANVIL_LAND.asBukkit();

								// Set Health
								Team.setHealth(Team.getHealth() - (e.getDamage()));

								// Play hit Sound
								loc.getWorld().playSound(loc, abl, 1F, (float) Math.random());

								// Play Particle Effects
								if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1)) {
									loc.getWorld().spawnParticle(Particle.CRIT, loc, 40);
									loc.getWorld().spawnParticle(Particle.FLAME, loc, 40);
									loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1, 400);
									loc.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, loc, 40);
									loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 40);
									loc.getWorld().playEffect(loc, Effect.SMOKE, 1, 400);
								} else {
									loc.getWorld().playEffect(loc, Effect.valueOf("CRIT"), 1, 400);
									loc.getWorld().playEffect(loc, Effect.valueOf("FLAME"), 1, 400);
									loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1, 400);
									loc.getWorld().playEffect(loc, Effect.valueOf("PARTICLE_SMOKE"), 1, 400);
									loc.getWorld().playEffect(loc, Effect.valueOf("VILLAGER_THUNDERCLOUD"), 100);
									loc.getWorld().playEffect(loc, Effect.valueOf("LARGE_SMOKE"), 1, 400);
									loc.getWorld().playEffect(loc, Effect.SMOKE, 1, 400);
									loc.getWorld().playEffect(loc, Effect.valueOf("PARTICLE_SMOKE"), 1, 400);
								}
								
								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.5F, 50, loc.clone().add(0.5D, 0.0D, 0.5D), 32.0D);
									CompatibleParticles.LAVA.displayNewerVersions().display(2.0F, 2.0F, 2.0F, 0.5F, 20, loc, 32.0D);
								} else {
									CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.5F, 50, loc.clone().add(0.5D, 0.0D, 0.5D), 32.0D);
									CompatibleParticles.LAVA.displayOlderVersions().display(2.0F, 2.0F, 2.0F, 0.5F, 20, loc, 32.0D);
								}
								
								// Play notify sound and send notify bar
								for (AnniPlayer player : Team.getPlayers()) {
									// Check is a valid Player.
									if (player == null || !KitUtils.isValidPlayer(player.getPlayer())) {
										continue;
									}

									// Get Player
									Player pl = player.getPlayer();

									// get server version.
									final ServerVersion server_version = ServerVersion.getVersion();
									
									// Get and play Sound
//									final Sound bltp = VersionUtils.isNewSpigotVersion() ? Sound.BLOCK_NOTE_PLING : Sound.valueOf("NOTE_PIANO");
									final Sound bltp = server_version.isOlderEqualsThan(ServerVersion.v1_9_R1)
											? Sound.valueOf("NOTE_PIANO")
											: (server_version.isOlderThan(ServerVersion.v1_13_R1)
													? Sound.valueOf("BLOCK_NOTE_PLING")
													: Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"));
									
									pl.playSound(pl.getLocation(), bltp, 1f, 2.1f);

									// Hit bar
									AnnounceBar.getInstance().getBar().sendToPlayer(pl, Lang.NEXUS_HIT_BAR
											.toStringReplacement(Team.getHealth(), Team.getColor().toString()), null, 75);
								}

								// Send damage Message for Everybody
								for (AnniPlayer pl : AnniPlayer.getPlayers()) {
									if (pl != null && pl.isOnline()) {
										pl.sendMessage((Lang.NEXUS_DAMAGED.toString())
												.replace("%#", String.valueOf(Team.getHealth()))
												.replace("%PLAYER%", p.getTeam().getColor() + p.getName())
												.replace("%hit_team", Team.getExternalColoredName()));
									}
								}

								// Check if is dead
								if (Team.isTeamDead()) {
//									// Remove from the Scoreboard
//									if (!ScoreboardVars.useNewScoreboard) {
//										ScoreboardAPI.removeTeam(Team);
//									}

									// Get World and Player
									final World w = loc.getWorld();

									// Set type to "BEDROCK"
									w.getBlockAt(loc).setType(Material.BEDROCK);

									// Play Effect
									if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1)) {
										w.spawnParticle(Particle.EXPLOSION_NORMAL, loc, 40);
										w.spawnParticle(Particle.EXPLOSION_LARGE, loc, 40);
										event.getBlock().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, event.getBlock().getLocation(), 40);
									} else {
										w.playEffect(loc, Effect.valueOf("EXPLOSION"), 100);
										w.playEffect(loc, Effect.valueOf("EXPLOSION_LARGE"), 100);
										event.getBlock().getWorld().playEffect(event.getBlock().getLocation(),
												Effect.valueOf("EXPLOSION_HUGE"), 1, 100);
									}

									// Call TeamDeathEvent
									TeamDeathEvent teamDeathEvent = new TeamDeathEvent(p, Team);
									AnniEvent.callEvent(teamDeathEvent);

									// Get ImageMessages
									try {
										// Get Buffered Image Message
										final BufferedImage image = AnnihilationMain.API.getTeamBufferedImage(Team);

										// Get ImageMessage and append Text With Lore
										ImageMessage message = new ImageMessage(image, 10,
												ImageChar.MEDIUM_SHADE);
										String s = Lang.TEAMDESTROYED.toStringReplacement(Team.getExternalColoredName())
												.replace("%PLAYER%", event.getPlayer().getName());
										String[] o = (Util.untranslateAlternateColorCodes(s)).split("%n");

										// Replace team Colors
										for (int ii = 0; ii < o.length; ii++) {
											o[ii] = Util.wc(o[ii]).replace("%tc", Team.getColor().toString());
										}

										// Append Text
										message.appendTextToLines(5, o);

										// Send message and play explode sound
										for (Player pl : Bukkit.getOnlinePlayers()) {
											if (KitUtils.isValidPlayer(pl)) {
												// Check enabled
												if (Config.USE_CHAT_TEAM_IMAGES.toBoolean()) {
													// Send Image Message
													message.sendToPlayer(pl);
												}

												// Play Explod Sound
												pl.getWorld().playSound(pl.getLocation(), play, 1F, .8F);
											}
										}

										// Send Team destroyed Titles
										for (AnniPlayer anpt : Team.getPlayers()) {
											if (anpt != null && KitUtils.isValidPlayer(anpt.getPlayer())) {
												Util.sendTitle(anpt.getPlayer(), Lang.TITLE_EQUIPO_DESTRUIDO.toString(),
														Lang.SUBTITLE_EQUIPO_DESTRUIDO.toString());
											}
										}
									} catch (Throwable t) {
									}

									// Check is Game Over
									gameOverCheck();
								}
							}
						}
					}
					else {
						if (!p.getTeam().equals(Team)) {
							event.getPlayer().sendMessage(Lang.NEXUS_DAMAGED_PHASE1.toString());
						}
					}
				}
			}
		}
	}
}
