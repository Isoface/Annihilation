package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.BossSpawnEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKillBossEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.BossMap;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class BossReal implements Listener {
	public ScheduledExecutorService executor;

	public BossReal(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
		executor = Executors.newScheduledThreadPool(3);
	}

	public static LivingEntity SpawnBoss() {
		final BossMap mapa = GameBoss.getBossMap();
		if (mapa == null) {
			return null;
		}

		final AnniBoss anniBoss = GameBoss.getBoss();
		if (anniBoss != null) {
			final Loc spawnLoc = anniBoss.getSpawn();
			//
			if (Util.isValidLoc(spawnLoc)) {
				final Location spawn = spawnLoc.toLocation();
				if (spawn != null) {
					final Class<? extends LivingEntity> type = anniBoss.getType();
					if (type != null) {
						final String name = anniBoss.getName();
						final Double vida = anniBoss.getVida();
						try {
							if (!spawn.getChunk().isLoaded()) {
								spawn.getWorld().loadChunk(spawn.getChunk());
								spawn.getChunk().load();
							}
						} catch (Throwable e) {
							// ignore.
						}
						//
						final LivingEntity wit = mapa.getWorld().spawn(spawn, type);
						wit.setCustomName(Util.wc(name != null ? name : ""));
						wit.setCustomNameVisible(name != null ? true : false);
						wit.setMetadata("Boss", new FixedMetadataValue(AnnihilationMain.INSTANCE, "Boss"));
						wit.setCanPickupItems(false);
						wit.setRemoveWhenFarAway(false);

						if (vida > 2040.0) {
							anniBoss.setVida(2040.0);
							wit.setMaxHealth(2040.0);
							wit.setHealth(2040.0);
							Bukkit.getConsoleSender()
									.sendMessage("§c[Annihilation] The Boss Health must be between 0 and 2040.0");
						} else {
							anniBoss.setVida(vida);
							wit.setMaxHealth(vida);
							wit.setHealth(vida);
						}

						new BukkitRunnable() {
							@Override
							public void run() {
								if (wit.isDead() || wit == null) {
									cancel();
								}

								if (type.equals(Wither.class)) {
									Location unRot = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(),
											spawn.getZ(), wit.getEyeLocation().getYaw(),
											wit.getEyeLocation().getPitch());
									wit.teleport(unRot);
								}

								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									CompatibleParticles.PORTAL.displayNewerVersions().display(0.8F, 0.8F, 0.8F, 0.1F, 5,
											wit.getLocation().clone().add(0.0D, 2.0D, 0.0D), 1000.0D);

									CompatibleParticles.UNDERWATER.displayNewerVersions().display(0.8F, 0.8F, 0.8F,
											0.1F, 5, wit.getLocation().clone().add(0.0D, 2.0D, 0.0D), 1000.0D);
								} else {
									CompatibleParticles.PORTAL.displayOlderVersions().display(0.8F, 0.8F, 0.8F, 0.1F, 5,
											wit.getLocation().clone().add(0.0D, 2.0D, 0.0D), 1000.0D);

									CompatibleParticles.UNDERWATER.displayOlderVersions().display(0.8F, 0.8F, 0.8F,
											0.1F, 5, wit.getLocation().clone().add(0.0D, 2.0D, 0.0D), 1000.0D);
								}
							}
						}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);

						final double midRealhealth = (anniBoss.getVida() * 50 / 100);
						new BukkitRunnable() {
							@Override
							public void run() {
								if (wit.isDead() || wit == null) {
									cancel();
								}

								if (wit.getHealth() <= midRealhealth) {
									for (final Location l2 : Util
											.getCircle(wit.getLocation().clone().add(0.5D, 3.D, 0.5D), 1.0, 15)) {
										if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2, 100.0D);
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -0.5D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -1.0D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -1.5D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -2.0D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -3.0D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -2.5D, 0.0D), 100.0D);
										} else {
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2, 100.0D);
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -0.5D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -1.0D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -1.5D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -2.0D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -3.0D, 0.0D), 100.0D);
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F,
													0.0F, 0.0F, 1, l2.clone().add(0.0D, -2.5D, 0.0D), 100.0D);
										}
									}
								}
							}
						}.runTaskTimer(AnnihilationMain.INSTANCE, 40L, 40L);
						if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1)) {
							try {
								wit.setCollidable(false);
							} catch (Throwable t) {
								// ignore.
							}
						}
						return wit;
					} else
						Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Boss Type Not Set");
				} else
					Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Boss Spawn Not Set");
			} else
				Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Boss Spawn Not Set");
		} else
			Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Invalid Boss");
		return null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		final Player p = eve.getPlayer();
		final Location loc = eve.getBlockPlaced().getLocation();
		if (p != null && p.isOnline() && p.getGameMode() != GameMode.CREATIVE) {
			final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null && ap.isOnline()) {
				if (KitUtils.isOnGameMap(p)) {
					if (isOnPortalZone(loc))
						eve.setCancelled(true);
				} else if (KitUtils.isOnBossMap(p))
					eve.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		final Player p = eve.getPlayer();
		final Location loc = eve.getBlock().getLocation();
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}

		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		if (ap != null && ap.isOnline()) {
			if (KitUtils.isOnGameMap(p)) {
				if (isOnPortalZone(loc)) {
					eve.setCancelled(true);
				}
			} else if (KitUtils.isOnBossMap(p)) {
				eve.setCancelled(true);
			}
		}
	}

	private boolean isOnPortalZone(final Location l) {
		boolean b = false;
		if (Util.isValidLoc(l) && Game.getGameMap().getPortalsLocations() != null) {
			for (Loc loc : Game.getGameMap().getPortalsLocations()) {
				if (!Util.isValidLoc(loc)) {
					continue;
				}

				if (l.distance(loc.toLocation()) < 6) {
					b = true;
				}
			}
		}
		return b;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBossBlockBreak(EntityChangeBlockEvent eve) {
		final Entity entity = eve.getEntity();
		if (entity != null) {
			if (entity instanceof Wither || entity instanceof WitherSkull) {
				final Location enloc = entity.getLocation();
				if (Util.isValidLoc(enloc)) {
					final World w = enloc.getWorld();
					if (KitUtils.isBossMap(w)) {
						eve.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void AntiPaintingBreak(HangingBreakByEntityEvent eve) {
		if (eve.getRemover() != null) {
			final EntityType Ent = eve.getRemover().getType();
			if (eve.getEntity() != null && Util.isValidLoc(eve.getEntity().getLocation())) {
				if (KitUtils.isBossMap(eve.getEntity().getLocation().getWorld())) {
					if (Ent == EntityType.WITHER || Ent == EntityType.WITHER_SKULL) {
						eve.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void antiWitherSkullExplosions(EntityExplodeEvent eve) {
		final Entity ent = eve.getEntity();
		if (ent != null) {
			if (ent instanceof WitherSkull) {
				final Location enloc = ent.getLocation();
				if (Util.isValidLoc(enloc)) {
					final World w = enloc.getWorld();
					if (KitUtils.isBossMap(w)) {
						eve.blockList().clear();

						w.playEffect(enloc, Effect.MOBSPAWNER_FLAMES, 6);
						
						if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
							CompatibleParticles.FLAME.displayNewerVersions().display(0.3F, 0.3F, 0.3F, 0.1F, 50, enloc,
									1000.0D);
						} else {
							CompatibleParticles.FLAME.displayOlderVersions().display(0.3F, 0.3F, 0.3F, 0.1F, 50, enloc,
									1000.0D);
						}

						if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_12_R1)) { // VersionUtils.getIntVersion() >= 12
							w.playSound(enloc, Sound.valueOf("BLOCK_ENDER_CHEST_OPEN"), 1.0F, 4.0F);
						} else if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1)) {
							w.playSound(enloc, Sound.valueOf("BLOCK_ENDERCHEST_OPEN"), 1.0F, 4.0F);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void BossDeathEvent(EntityDeathEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		final LivingEntity boss = eve.getEntity();
		if (boss != null) {
			if (boss.hasMetadata("Boss")) {
				final Player p = boss.getKiller();
				if (KitUtils.isOnBossMap(p)) {
					final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
					if (ap != null) {
						final AnniBoss anniBoss = GameBoss.getBoss();
						if (anniBoss != null) {
							PlayerKillBossEvent event = new PlayerKillBossEvent(ap, boss, anniBoss.getRespawnTime());
							AnniEvent.callEvent(event);
							if (anniBoss.getRespawnUnit() != null) {
								executor.schedule(new Respawn(), anniBoss.getRespawnTime(), anniBoss.getRespawnUnit());
							}
						}

						// quit Portal
						PortalBlocks.quitPortal();

						// Remove Drop
						if (eve.getDrops() != null) {
							for (ItemStack drop : new ArrayList<ItemStack>(eve.getDrops())) {
								eve.getDrops().remove(drop);
							}
						}

						// Give Boss Bar for team Players
						final AnniTeam team = ap.getTeam();
						if (team != null) {
							for (AnniPlayer tp : team.getPlayers()) {
								if (tp != null) {
									Player drop = tp.getPlayer();
									if (KitUtils.isValidPlayer(drop)) {
										drop.getInventory().addItem(CustomItem.BossStar.toItemStack(1));
										drop.updateInventory();
									}
								}
							}
						}
						//
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (KitUtils.isValidPlayer(player)) {
								final AnniPlayer apll = AnniPlayer.getPlayer(player.getUniqueId());
								if (apll != null) {

									// Send Boss Death Messagte
									if (team != null) {
										if (Config.USE_BOSS_DEATH_MESSAGE.toBoolean()) {
											player.sendMessage(Lang.BOSSDEATHMESSAGE
													.toStringReplacement(ap.getTeam().getExternalColoredName()));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	class Respawn implements Runnable {
		@Override
		public void run() {
			Bukkit.getScheduler().runTask(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					// get and check boss
					final LivingEntity boss = SpawnBoss();
					if (boss == null || boss.isDead()) {
						return;
					}
					
					// Get and call event
					final BossSpawnEvent event = new BossSpawnEvent(boss, GameBoss.getBoss().getSpawn().toLocation());
					AnniEvent.callEvent(event);
					
					// When is cancelled
					if (event.isCancelled()) {
						boss.remove();
						return;
					}
					
					// Set current boss uuid
					GameBoss.setCurrentBossID(boss.getUniqueId());
					
					// Spawn portal
					if (event.isWillOpenPortal()) {
						PortalBlocks.addPortal();
					}
				
					// Check use spawn sound
					if (event.isUseSpawnSound()) {
						// Play Sound
						for (Player toSound : Bukkit.getOnlinePlayers()) {
							if (KitUtils.isValidPlayer(toSound)) {
								if (toSound.getLocation() != null)
									toSound.playSound(toSound.getLocation(),
											ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1) 
											? Sound.ENTITY_WITHER_SPAWN
													: Sound.valueOf("WITHER_SPAWN"),
											30.0F, 1.0F);

								// Send Boss Respawn Message
								if (Config.USE_BOSS_RESPAWN_MESSAGE.toBoolean()) {
									if (event.isUseSpawnMessage()) {
										toSound.sendMessage(Lang.BOSS_RESPAWN_MESSAGE.toString());
									}
								}
							}
						}
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void PrlaunchC(ProjectileLaunchEvent eve) {
		final Projectile pr = eve.getEntity();
		if (pr != null) {
			final Location loc = pr.getLocation();
			if (Util.isValidLoc(loc) && KitUtils.isBossMap(loc.getWorld())) {
				if (pr instanceof WitherSkull) {
					final WitherSkull tiro = (WitherSkull) eve.getEntity();
					if (tiro.getShooter() != null && tiro.getShooter() instanceof Wither) {
						final LivingEntity e = (LivingEntity) tiro.getShooter();
						if (e != null && e.hasMetadata("Boss")) {
							new BukkitRunnable() {
								@Override
								public void run() {
									if (tiro.isDead() || tiro.isOnGround()) {
										cancel();
									}

									// Play Projectile hit effect
									Location location = tiro.getLocation();
									if (location != null) {
										if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
											CompatibleParticles.FIREWORK.displayNewerVersions().display(0.1F, 0.1F,
													0.1F, 0.05F, 3, location, 400D);
										} else {
											CompatibleParticles.FIREWORK.displayOlderVersions().display(0.1F, 0.1F,
													0.1F, 0.05F, 3, location, 400D);
										}
									}
								}
							}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);
						}
					}
				}
			}
		}
	}

}