package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.bobacadodl.imgmessage.ImageChar;
import com.bobacadodl.imgmessage.ImageMessage;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.Title.TitleHandler;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniPlayerRespawnEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameEndEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameStartEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerBossPortalEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent.KillAttribute;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.TeamDeathEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.autoRespawn.RespawnHandler;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.Announcement;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitLoading;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.game.GameScoreboardHandler;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.lobby.LobbyScoreboardHandler;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.voting.ChatVars;

public class GameListeners implements Listener {
	
	public GameListeners(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
		RespawnHandler.register(p);
	}

	@SuppressWarnings("deprecation")
	@EventHandler ( priority = EventPriority.LOW )
	public void onGameStartII ( GameStartEvent event ) {
		LobbyScoreboardHandler.getInstance ( ).stopUpdating ( );
		GameScoreboardHandler.getInstance ( ).startUpdating ( );
		
		try {
			if ( Game.getGameMap ( ) != null ) {
				final GameMap map = Game.getGameMap();
				
				// teams health
				int team_init_health = Math.max ( Config.TEAMS_INITIALI_HEALTH.toInt ( ) , 1 ); // one to be alive
				for ( AnniTeam team : AnniTeam.Teams ) {
					team.setHealth ( team_init_health );
				}
				//
				// -------------------------------------------------
				AnnounceBar.getInstance()
						.countDown(new Announcement("" + Lang.PHASEBAR.toStringReplacement(1) + " - {#}",
								"" + Lang.BOSSBAR.toStringReplacement(1) + " - {#}").setTime(map.getPhaseTime())
										.setCallback(new StandardPhaseHandler(true)));
				//
				map.setPhase(1);
				map.setCanDamageNexus(false);
				//
				//
				for (final AnniPlayer p : AnniPlayer.getPlayers()) {
					if (p != null && p.isOnline()) {
						final Player player = p.getPlayer();
						//
						if (player != null) {
							if (p.hasTeam()) {
								player.setHealth(20);
								player.setMaxHealth(20D);
								player.setFoodLevel(20);
								player.setGameMode(GameMode.SURVIVAL);
								player.getInventory().clear();
								player.getInventory().setArmorContents(null);
								
								/* sending to random spawn */
								final Location spawn = p.getTeam ( ).getRandomSpawn ( );
								if ( spawn != null ) {
									player.teleport ( spawn );
								}
								
								player.setLevel(0);
								player.setExp(0);
								player.getActivePotionEffects().clear();
								p.getKit().onPlayerSpawn(player);
							} else if (!p.hasTeam() || p.getTeam().isTeamDead()) {
								player.getInventory().clear();
								if (Config.USE_SPECTATOR_MODE.toBoolean()) {
									player.getInventory().setItem(0, CustomItem.SPECTATORMAP.toItemStack());
								}
								
								if (KitLoading.ShopIsEnabled() && Config.USE_KITS_SHOP_ITEM.toBoolean()) {
									player.getInventory().setItem(1, CustomItem.KIST_SHOT.toItemStack());
								}
								
								if (Config.USE_LOBBY_COMMPAS.toBoolean()) {
									player.getInventory().setItem(8, CustomItem.GO_TO_LOBBY.toItemStack());
								}
								
								/* add kit map */
								player.getInventory().setItem(3, CustomItem.KITMAP.toItemStack());
								
								/* add team map */
								if (Config.USE_TEAM_SELECTOR.toBoolean()) {
									player.getInventory().setItem(5, CustomItem.TEAMMAP.toItemStack());
								}
								
								if (Util.AnniOEEnabled()) {
									player.getInventory().setItem(4, Util.getOptionsItem(player));
								}
								player.updateInventory();
							}

							if (player.getGameMode() != GameMode.CREATIVE) {
								player.setFlying(false);
								player.setAllowFlight(false);
							}
							//
							if (player.getActivePotionEffects() != null)
								player.getActivePotionEffects().clear();
							//
							Util.removeAllPotionEffects(player);
							player.updateInventory();
						}
					}
				}
				//
				try {
					if (map.getDiamondLocations() != null && !map.getDiamondLocations().isEmpty()) {
						for (Loc loc : map.getDiamondLocations()) {
							if (loc != null) {
								final Location l = loc.toLocation();
								//
								if (l != null && l.getWorld() != null)
									l.getWorld().getBlockAt(l).setType(Material.COBBLESTONE);
							}
						}
					}
				} catch (Throwable e) {
				}
				//
				//
				// -------------------------------------------------
				//
				//
				int Fase = map.getCurrentPhase();
				//
				String t = "" + Lang.TITLE_FASE_NOMBRE.toString();
				t = t.contains("%#") ? "" + Lang.TITLE_FASE_NOMBRE.toStringReplacement(Fase) : t + Fase;
				String s = "" + Lang.SUBTITLE_FASE_1.toString();
				// ----
				if (Config.BOSS_MAP_LOADING_USE_BOSS_MAP.toBoolean())
					new PortalBlocks();
				// ----
				for (Player pl : Bukkit.getOnlinePlayers())
					if (KitUtils.isValidPlayer(pl) && Config.USE_PHASE_TITLES.toBoolean())
						Util.sendTitle(pl, t, s);
				// ----
				for (AnniTeam teams : AnniTeam.Teams) {
					// Check Nexus Block
					if (teams.getNexus() != null && Util.isValidLoc(teams.getNexus().getLocation())) {
						Block b = teams.getNexus().getLocation().toLocation().getBlock();
						if (b != null) {
							b.setType(Material.ENDER_STONE);
						}
					}
				}
				
				new BukkitRunnable() {
					@Override
					public void run() {
						if (Game.getGameMap() == null) {
							cancel();
							return;
						}
						//
						if (Game.getGameMap().getWorld() == null) {
							Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Null Game Map World");
							cancel();
							return;
						}

						for (AnniTeam nt : AnniTeam.Teams) {
							if (nt != null && nt.getNexus() != null && Util.isValidLoc(nt.getNexus().getLocation())) {
								if (!nt.isTeamDead()) {
									Location lnx = nt.getNexus().getLocation().toLocation();
									//
									if (lnx != null) {
										if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
											lnx.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, lnx.clone().add(0.5D, 1.0D, 0.5D), 10, 1.0F, 1.0F, 1.0F);
											lnx.getWorld().spawnParticle(Particle.SUSPENDED_DEPTH, lnx.clone().add(0.5D, 1.0D, 0.5D), 10, 2.0F, 2.0F, 2.0F);
										} else {
											CompatibleParticles.ENCHANT.display(1.0F, 1.0F, 1.0F, 0.1F, 10, lnx.clone().add(0.5D, 1.0D, 0.5D), 1000.0D);
											CompatibleParticles.UNDERWATER.display(2.0F, 2.0F, 2.0F, 6.0F, 10, lnx.clone().add(0.5D, 1.0D, 0.5D), 10000.0D);
										}
									}
								}
							}
						}

						for (Loc fl : Game.getGameMap().getFurnaceLocations()) {
							if (Util.isValidLoc(fl)) {
								Location rfl = fl.toLocation();
								//
								if (rfl != null) {
									CompatibleParticles.PORTAL.display(1.0F, 1.0F, 1.0F, 0.5F, 8, rfl.clone().add(0.5D, 0.0D, 0.5D), 200.0D);
								}
							}
						}
					}
				}.runTaskTimer(AnnihilationMain.INSTANCE, 2L, 2L);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void gameEndEvent(GameEndEvent eve) {
		// Get GameMap and Check not Null
		GameMap map = Game.getGameMap();
		if (map == null) {
			return;
		}

		// Set Cant damage Nexuses and Phase 0
		map.setPhase(0);
		map.setCanDamageNexus(false);

		// Perform End Game Commands in Config
		AnnounceBar.getInstance().countDown(new Announcement(Lang.GAME_END_BAR.toString())
				.setTime(Config.END_OF_GAME_COUNTDOWN.toInt()).setCallback(new Runnable() {
					@Override
					public void run() {
						List<String> forPlayers = Config.END_OF_GAME_COMMANDS_PLAYERS.toStringList();
						if (forPlayers != null && !forPlayers.isEmpty()) {
							for (String c : forPlayers) {
								if (c != null && !c.isEmpty()) {
									for (Player eve : Bukkit.getOnlinePlayers()) {
										if (KitUtils.isValidPlayer(eve)) {
											eve.performCommand(c);
										}
									}
								}
							}
						}
						//
						//
						List<String> forConsole = Config.END_OF_GAME_COMMANDS.toStringList();
						if (forConsole != null && !forConsole.isEmpty()) {
							for (String c : forConsole)
								if (c != null && !c.isEmpty())
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
						}
					}
				}));

		// Image Message
		if (eve.getWinningTeam() != null) {
			// Get Winner Team
			final AnniTeam winner = eve.getWinningTeam();

			// Send End Game Image Message
			if (Config.USE_GAME_END_IMAGE.toBoolean()) {
				// Get Image and Lore
				final BufferedImage image = AnnihilationMain.API.getGameEndBufferedImage();
				String[] lore = new String[] { "", "", "", "",
						Lang.GAME_END_BROADCAST_MESSAGE.toStringReplacement(winner.getExternalColoredName()) };

				// Get ImageMessage and Append Text with Lore
				ImageMessage message = new ImageMessage(image, 10, ImageChar.MEDIUM_SHADE);
				message.appendText(lore);

				// Send ImageMessage for Everybody
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (KitUtils.isValidPlayer(all)) {
						message.sendToPlayer(all);
					}
				}
			}

			// Get Game Titles
			String pp = Lang.TITLE_PARTIDA_PERDIDA.toString();
			String pg = Lang.TITLE_PARTIDA_GANADA.toString();

			// Send Title for everybody (Winning and Loser)
			for (AnniPlayer ap : AnniPlayer.getPlayers()) {
				if (ap == null || !ap.hasTeam() || !ap.isOnline()) {
					continue;
				}

				boolean winnerTeam = eve.getWinningTeam().equals(ap.getTeam());
				Util.sendTitle(ap.getPlayer(), (winnerTeam ? pg : pp), "", 6, 30, 6);
			}
		}

		// Fire Works Task
		Bukkit.getScheduler().scheduleSyncRepeatingTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				// Spawn in Diamonds
				final List<Loc> diamonds = Game.getGameMap().getDiamondLocations();
				if (diamonds != null && !diamonds.isEmpty()) {
					for (Loc l : diamonds) {
						if (Util.isValidLoc(l)) {
							Util.FireWorkEffect(l.toLocation());
						}
					}
				}

				// Spawn In Lobby
				if (Game.LobbyMap != null && Game.LobbyMap.getSpawn() != null) {
					Util.FireWorkEffect(Game.LobbyMap.getSpawn());
				}

				// Spawn in Winning Team Spawns
				final Color col = Util.getColorFromChatColor(eve.getWinningTeam().getColor());
				if (col != null) {
					final Location spawn = eve.getWinningTeam ( ).getRandomSpawn ( );
					if ( spawn != null ) {
						Util.FireWorkEffect ( spawn , col );
					}
				}
			}
		}, 1, 60);
	}

	@EventHandler
	public void openInventoryEvent(InventoryOpenEvent e) {
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) && e != null
				&& e.getInventory().getType() == InventoryType.BREWING) {
			e.getInventory().setItem(4, new ItemStack(Material.BLAZE_POWDER, 64));
			return;
		}

		if (!Config.USE_AUTO_LAPIZ.toBoolean()) { // GameVars.UseAutoLapiz
			return;
		}

		/* create and add lapis */
		Dye dye = new Dye(); dye.setColor(DyeColor.BLUE);
		if (e != null && e.getInventory() instanceof EnchantingInventory) {
			e.getInventory().setItem(1, dye.toItemStack(64)); /* add lapis */
		}
	}

	@EventHandler
	public void closeInventoryEvent(InventoryCloseEvent e) {
		if (!Config.USE_AUTO_LAPIZ.toBoolean()) {
			return;
		}

		if (e != null && e.getInventory() instanceof EnchantingInventory) {
			e.getInventory().setItem(1, null);
		}
	}

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) && e.getClickedInventory() != null
				&& e.getClickedInventory().getType() == InventoryType.BREWING) {
			if (e.getSlot() == 4) {
				e.setCancelled(true);
			}
		}

		if (!Config.USE_AUTO_LAPIZ.toBoolean()) {
			return;
		}

		if (e.getClickedInventory() != null && e.getClickedInventory() instanceof EnchantingInventory) {
			if (e.getSlot() == 1) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent eve) {
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
			if (eve.getBlock() != null && eve.getBlock().getType() == Material.BREWING_STAND
					&& eve.getBlock().getState() != null) {
				BrewingStand stand = (BrewingStand) eve.getBlock().getState();
				if (stand != null) {
					stand.getInventory().setItem(4, null);
				}
			}
		}
	}

	@EventHandler
	public void enchantItemEvent(EnchantItemEvent e) {
		if (!Config.USE_AUTO_LAPIZ.toBoolean()) {
			return;
		}

		Dye dye = new Dye(); dye.setColor(DyeColor.BLUE);
		if (e.getInventory() == null) {
			return;
		}
		//
		e.getInventory().setItem(1, dye.toItemStack(64)); /* add lapis */
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void pingServer(ServerListPingEvent event) {
		if (Config.USE_MOTD.toBoolean()) {
			event.setMotd((Game.isNotRunning() ? Lang.MOTDINLOBBY.toString()
					: Lang.MOTDINGAME.toString().replace("%#", String.valueOf(Game.getGameMap().getCurrentPhase()))
							.replace("%w", Game.getGameMap().getNiceWorldName())).replace("%n", "\n"));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTeamDeath(TeamDeathEvent event) {
		if (!Config.USE_TEAM_DEATH_TITLES.toBoolean()) {
			return;
		}

		final AnniTeam kT = event.getKilledTeam();
		Location nex = event.getNexusDestroyed().getLocation().toLocation();

		if (nex != null) {
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				nex.getWorld().spawnParticle(Particle.LAVA, nex.clone().add(0.5D, 0.0D, 0.5D), 80, 0.0F, 0.0F, 0.0F);
			} else {
				CompatibleParticles.LAVA.display(0.0F, 0.0F, 0.0F, 0.0F, 80, nex.clone().add(0.5D, 0.0D, 0.5D), 100.0D);
			}
		}

		if (kT != null && kT.getPlayers() != null && !kT.getPlayers().isEmpty()) {
			for (AnniPlayer ap : kT.getPlayers()) {
				if (ap != null && ap.getPlayer() != null) {
					String t = "" + Lang.TITLE_EQUIPO_DESTRUIDO.toString();
					String s = "" + Lang.SUBTITLE_EQUIPO_DESTRUIDO.toString();

					Player p = ap.getPlayer();

					if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1)) {
						TitleHandler.getInstance().SendTitleToPlayer(p, t, s, 6, 22, 6);
					} else {
						p.sendTitle(t, s);
					}

					if (ServerVersion.getVersion().isOlderThan(ServerVersion.v1_9_R1)) {
						p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_STARE"), 1.0F, 4.0F);
					} else if (ServerVersion.getVersion().isOlderThan(ServerVersion.v1_13_R1)) {
						p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ENDERMEN_STARE"), 1.0F, 4.0F);
					} else {
						p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_STARE"), 1.0F, 4.0F);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void AnniPlayersOnCommand(PlayerCommandPreprocessEvent event) {
		final String[] args = event.getMessage().split(" ");
		final Player player = event.getPlayer();
		//
		if (args[0].equalsIgnoreCase("/tp")) {
			if (player != null && player.hasPermission("A.anni") || player.isOp()) {
				if (args.length > 1) {
					AnniTeam team = AnniTeam.getTeamByName(args[1]);
					if (team != null) {
						Loc loc = team.getSpectatorLocation();
						if (loc != null) {
							event.setCancelled(true);
							player.teleport(loc.toLocation());
						} else {
							Location rs = team.getRandomSpawn();
							if (rs != null) {
								event.setCancelled(true);
								player.teleport(rs);
							}
						}
					} else if (args[1].equalsIgnoreCase("lobby")) {
						if (Game.LobbyMap != null) {
							Location lobby = Game.LobbyMap.getSpawn();
							if (lobby != null) {
								event.setCancelled(true);
								player.teleport(lobby);
							}
						}
					} else if (args[1].equalsIgnoreCase("boss")) {
						if (GameBoss.getBossMap() != null) {
							Location loc = GameBoss.getBossMap().getRandomSpawn();
							if (loc != null) {
								event.setCancelled(true);
								player.teleport(loc);
							}
						}
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("/kill")) {
			event.setCancelled(true);
			if (player == null)
				return;

			if (args.length > 1) {
				if (args[1] != null) {
					Player toKill = Bukkit.getPlayer(args[1]);
					if (toKill != null && toKill.isOnline()) {
						if (player.isOp()) {
							toKill.setHealth(0.0D);
							player.sendMessage(player.getName() + " Killed " + toKill.getName());
						}
					}
				}
			} else
				player.setHealth(0.0D);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void DeathListener(PlayerDeathEvent event) {
		String message = "";
		final Player player = event.getEntity();
		final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
		if (player == null || p == null || !p.hasTeam()) {
			return;
		}

		// Get Killer
		final Player killer = player.getKiller();
		event.setDeathMessage(null);

		// Cleanup Kit and update Inventory
		p.getKit().cleanup(player);
		p.setResurrected(false);
		player.updateInventory();

		// Check is game running
		if (Game.isNotRunning()) {
			return;
		}

		// Send Assert Death Message and call new PlayerKilledEvent
		final EntityDamageEvent eve = player.getLastDamageCause();
		PlayerKilledEvent e = null;
		if (eve != null) {
			// Get and check Cause
			final DamageCause cause = eve.getCause();
			if (cause == null || cause.name() == null) {
				return;
			}
			
			// get parentesis.
			final String parentesis1 = ChatVars.splitKitName ? " (" : "(";
			final String parentesis2 = ChatVars.splitKitName ? ") " : ")";

			// Get Message
			final String ptk = p.getTeam().getColor() + player.getName() + parentesis1 + p.getKit().getName() + parentesis2;
			if (killer != null) {
				if (cause != DamageCause.VOID) {
					final AnniPlayer k = AnniPlayer.getPlayer(killer.getUniqueId());
					if (k != null && k.isOnline() && k.hasTeam()) {
						// Get Format
						String ktk = k.getTeam().getColor() + killer.getName() + parentesis1 + k.getKit().getName() + ")";
						String mesNormalKill = Lang.DEATHPHRASE.toString();
						String mesShotKill   = Lang.DEATHPHRASESHOT.toString();

						// Set Event
						e = new PlayerKilledEvent(k, p, event.getDrops());

						// Add Attributes to Messages
						if (e.getAttributes().contains(KillAttribute.REMEMBRANCE)) {
							message = " " + Lang.REMEMBRANCE.toString();
						} else if (e.getAttributes().contains(KillAttribute.NEXUSDEFENSE)) {
							message = " " + Lang.NEXUSKILL.toString();
						}

						// Cancell Exp drop
						if (!e.shouldDropXP()) {
							event.setDroppedExp(0);
						}

						// Get Assert Death Message
						if (cause.name().equals(DamageCause.ENTITY_ATTACK.name())) {
							event.setDeathMessage(ktk + " " + mesNormalKill + " " + ptk + message);
							p.setData("LastDamagerUUID", null);
						}

						if (cause.name().equals(DamageCause.PROJECTILE.name())) {
							event.setDeathMessage(ktk + " " + mesShotKill + " " + ptk + message);
							p.setData("LastDamagerUUID", null);
						}

						if (cause.name().equals(DamageCause.FALL.name())) {
							event.setDeathMessage(ktk + " " + mesNormalKill + " " + ptk + message);
							p.setData("LastDamagerUUID", null);
						}
					}
				} else {
					if (p.getData("LastDamagerUUID") != null) {
						final Player damager = Bukkit.getPlayer((UUID) p.getData("LastDamagerUUID"));
						if (damager != null) {
							final AnniPlayer damagerAp = AnniPlayer.getPlayer(damager.getUniqueId());
							if (damagerAp != null && damagerAp.isOnline() && damagerAp.hasTeam()) {
								// Get Messages
								String mesTrewToVoid = damagerAp.getTeam().getColor() + damager.getName() + parentesis1
										+ damagerAp.getKit().getName() + parentesis2 + Lang.DEATHPHRASEVOID.toString() + " "
										+ ptk + message;
								event.setDeathMessage(mesTrewToVoid);

								// Remove Last Damager Data
								p.setData("LastDamagerUUID", null);

								// Set Killer
								Util.setKiller(player, damager);

								// Set Event
								e = new PlayerKilledEvent(damagerAp, p, event.getDrops());
							}
						}
					} else {
						if (player.getKiller() != null) {
							// Get And Check Killer
							final Player kp = player.getKiller();
							final AnniPlayer dk = AnniPlayer.getPlayer(kp);
							if (kp == null || dk == null) {
								return;
							}

							// Set Death Message
							String mesTrewToVoid = dk.getTeam().getColor() + kp.getName() + parentesis1 + dk.getKit().getName()
									+ parentesis2 + Lang.DEATHPHRASEVOID.toString() + " " + ptk + message;
							event.setDeathMessage(mesTrewToVoid);

							// Remove Last Damager Data
							p.setData("LastDamagerUUID", null);

							// Set Event
							e = new PlayerKilledEvent(dk, p, event.getDrops());
						}
					}
				}
			} else {
				if (cause.name().equals(DamageCause.FALL.name())) {
					if (p.getData("LastFallDamagerUUID") != null) {
						final Player damager = Bukkit.getPlayer((UUID) p.getData("LastFallDamagerUUID"));
						final AnniPlayer damagerAp = AnniPlayer.getPlayer(damager);
						if (damager != null && damagerAp != null && damagerAp.hasTeam()) {
							// Set Death Message
							String messFalll = damagerAp.getTeam().getColor() + damager.getName() + parentesis1
									+ damagerAp.getKit().getName() + parentesis2 + Lang.DEATHPHRASE.toString() + " " + ptk
									+ message;
							event.setDeathMessage(messFalll);

							// Remove Last Damager Data
							p.setData("LastFallDamagerUUID", null);

							// Set Killer
							Util.setKiller(player, damager);

							// Set Event
							e = new PlayerKilledEvent(damagerAp, p, event.getDrops());
						}
					}
				}
			}

			// Call PlayerKilledEvent
			if (e != null) {
				AnniEvent.callEvent(e);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void DeathListener(EntityDamageByEntityEvent event) {
		if (Game.isGameRunning() && event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
			if (ap == null || event.getDamager() == null) {
				return;
			}

			if (event.getDamager() instanceof Player) {
				final Player damager = (Player) event.getDamager();
				final AnniPlayer dp = AnniPlayer.getPlayer(damager.getUniqueId());
				if (ap != null && ap.getTeam() != null && damager != null) {
					if (dp.getTeam() != null && !dp.getTeam().equals(ap.getTeam())) {
						ap.setData("LastDamagerUUID", damager.getUniqueId());
						Util.removeLastDamager(ap);
					}
				}
			} else if (event.getDamager() instanceof Arrow) {
				final Arrow arr = (Arrow) event.getDamager();
				if (arr.getShooter() != null && arr.getShooter() instanceof Player) {
					final Player damager = (Player) arr.getShooter();
					final AnniPlayer dp = AnniPlayer.getPlayer(damager.getUniqueId());
					if (dp.getTeam() != null && !dp.getTeam().equals(ap.getTeam()) && ap.getKit() != null) {
						ap.setData("LastDamagerUUID", damager.getUniqueId());
						Util.removeLastDamager(ap);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void teleportToLobbyThing(PlayerJoinEvent event) {
		if (Game.LobbyMap != null && Game.LobbyMap.getSpawn() != null) {
			final Player     pl = event.getPlayer();
			final AnniPlayer p = AnniPlayer.getPlayer(pl.getUniqueId());
			event.setJoinMessage(null);
			if (pl != null && p != null) {
				new BukkitRunnable() {
					@Override
					public void run() {
						for (Player all : Bukkit.getOnlinePlayers()) {
							if (pl != null && all != null) {
								if (pl.getGameMode() != GameMode.SPECTATOR) {
									all.showPlayer(pl);
								}
							}
						}

						if (pl.isOnline()) {
							if (p.getTeam() == null || p.getTeam().isTeamDead()) {
								if (!p.isResurrected()) {
									if (p.getTeam() == null) {
										Game.LobbyMap.sendToSpawn(pl);
									} else {
										Game.LobbyMap.sendToDeadSpawn(pl);
									}
									return;
								}
							}

							if (Game.isGameRunning()) {
								if (KitUtils.isOnLobby(pl)) {
									pl.setGameMode(GameMode.SURVIVAL);
									pl.getInventory().clear();
									pl.setHealth(0.0D);
									pl.updateInventory();
									return;
								}
							}
						}
					}
				}.runTaskLater(AnnihilationMain.INSTANCE, 10L); // 25L
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void respawnHandler(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
		if (player != null && p != null) {
			if (Game.isGameRunning()) {
				if (player != null && p != null && p.getTeam() != null && !p.getTeam().isTeamDead()) {
					// Get and set new Respawn Loaction
					final Location newSpawn = p.getTeam().getRandomSpawn();
					if ( newSpawn != null ) {
						event.setRespawnLocation ( newSpawn );
					}

					if (p.hasKit()) {
						p.getKit().onPlayerSpawn(player);
					}

					final AnniPlayerRespawnEvent eve = new AnniPlayerRespawnEvent(player, newSpawn);
					AnniEvent.callEvent(eve);
					return;
				}
			}

			if (Game.LobbyMap != null && Game.LobbyMap.getSpawn() != null) {
				event.setRespawnLocation(Game.LobbyMap.getSpawn());
				if (player != null) {
					if (p.getTeam() != null && p.getTeam().isTeamDead())
						Game.LobbyMap.sendToDeadSpawn(player);
					else
						Game.LobbyMap.sendToSpawn(player);
				}
			}
		}
	}

	/**
	 * @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	 *                        public void respawnHandler(PlayerRespawnEvent event) {
	 *                        final Player player = event.getPlayer(); final
	 *                        AnniPlayer p =
	 *                        AnniPlayer.getPlayer(player.getUniqueId()); if (player
	 *                        != null && p != null) { if (Game.isGameRunning()) { if
	 *                        (player != null && p != null && p.getTeam() != null &&
	 *                        !p.getTeam().isTeamDead()) { // Get and set new
	 *                        Respawn Loaction final Location newSpawn =
	 *                        p.getTeam().getRandomSpawn(); if
	 *                        (p.getTeam().getRandomSpawn() != null) {
	 *                        event.setRespawnLocation(newSpawn); }
	 * 
	 *                        if (p.hasKit()) { p.getKit().onPlayerSpawn(player); }
	 * 
	 *                        final AnniPlayerRespawnEvent eve = new
	 *                        AnniPlayerRespawnEvent(player, newSpawn);
	 *                        AnniEvent.callEvent(eve); return; } }
	 * 
	 *                        if (Game.LobbyMap != null && Game.LobbyMap.getSpawn()
	 *                        != null) {
	 *                        event.setRespawnLocation(Game.LobbyMap.getSpawn()); if
	 *                        (player != null) { if (p.getTeam() != null &&
	 *                        p.getTeam().isTeamDead())
	 *                        Game.LobbyMap.sendToDeadSpawn(player); else
	 *                        Game.LobbyMap.sendToSpawn(player); } } } }
	 * @param eve
	 */

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent eve) {
		if (eve.getEntity() == null || !(eve.getEntity() instanceof LivingEntity)) {
			return;
		}
		//
		if (eve.getEntity() instanceof Villager) {
			if (eve.getEntity().getCustomName() != null) {
				for (AnniTeam team : AnniTeam.Teams) {
					String name = Util.remC("" + Lang.NAME_ALDEANO_JOINERS.toStringReplacement(team.getExternalName()));
					String vname = Util.remC(eve.getEntity().getCustomName());

					if (vname.equalsIgnoreCase(name))
						eve.setCancelled(true);
				}
			}
		} else if (eve.getEntity() instanceof Player) {
			if (eve.getCause() != null && eve.getCause().name().equals(DamageCause.VOID.name())) {
				eve.setDamage(eve.getDamage() * 4);
				return;
			}
		}
		//
//		if (Config.USE_BLOOD_EFFECT.toBoolean() && !eve.isCancelled()) {
//			if (!VersionUtils.getVersion().contains("v1_7") && eve.getEntity() instanceof ArmorStand) {
//				return;
//			}
//
//			if (!Util.isValidLoc(eve.getEntity().getLocation())) {
//				return;
//			}
//
//			try {
//				BlockData data = new ParticleEffect.BlockData(Material.REDSTONE_WIRE, (byte) 1);
//				new ParticlePacket(ParticleEffect.BLOCK_CRACK, 0.2F, 0.5F, 0.2F, 0.1F, 100, 256.0D > 256, data)
//						.customSendTo(eve.getEntity().getLocation().clone().add(0.0D, 1.0D, 0.0D), 256,
//								new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0));
//			} catch (Throwable e) {
//
//			}
//		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void aldeanoJoiners(PlayerInteractEntityEvent eve) {
		final Player p = eve.getPlayer();
		if (p != null && p.isOnline() && p.getGameMode() != GameMode.CREATIVE) {
			if (eve.getRightClicked() != null && eve.getRightClicked() instanceof Villager) {
				Villager v = (Villager) eve.getRightClicked();
				if (v.getCustomName() != null) {
					for (AnniTeam team : AnniTeam.Teams) {
						String name = Util.remC(Lang.NAME_ALDEANO_JOINERS.toStringReplacement(team.getExternalName()));
						String vname = Util.remC(v.getCustomName());

						if (vname.equalsIgnoreCase(name)) {
							eve.setCancelled(true);
							p.performCommand("Team " + team.getExternalName());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void staticLobbyArmor(InventoryClickEvent eve) {
		final Player p = (Player) eve.getWhoClicked();
		if (p != null && eve.getInventory() != null && eve.getClickedInventory() != null
				&& p.getGameMode() != GameMode.CREATIVE && KitUtils.isOnLobby(p)) {
			if (eve.getCurrentItem() != null && eve.getCurrentItem().getType().name().contains("LEATHER")) {
				eve.setCancelled(true);
			}
		}
	}

	private static final Map<UUID, Location> joinPortalLoc = new HashMap<UUID, Location>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void bossPortal(final PlayerPortalEvent event) {
		// Check
		if (Game.isNotRunning()) {
			return;
		}

		if (event.getCause() != TeleportCause.END_PORTAL && event.getCause() != TeleportCause.NETHER_PORTAL) {
			return;
		}

		if (Game.getGameMap().getCurrentPhase() < 4) {
			return;
		}

		if (GameBoss.getBossMap() == null) {
			Util.print(ChatColor.RED, "The Boss Map could not be Found", System.currentTimeMillis());
			return;
		}

		// Get and Check player
		final Player pl = event.getPlayer();
		final AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (p != null && p.isOnline() && p.hasTeam()) {
			// Get Locations
			final Location from = event.getFrom();

			// Check is not Changing kit
			if (event.getCause() == TeleportCause.NETHER_PORTAL && KitUtils.isGameMap(from.getWorld())) {
				return;
			}

			// Get to
			final Location to = KitUtils.isOnBossMap(pl) ? p.getTeam ( ).getRandomSpawn ( )
					: GameBoss.getBossMap ( ).getRandomSpawn ( );

			// Check is valid Loc
			if (!Util.isValidLoc(to)) {
				Util.print(ChatColor.RED, "Boss Map Random Spawns not Found", System.currentTimeMillis());
				return;
			}

			// Call new Player Boss Portal Event
			final PlayerBossPortalEvent pbpe = new PlayerBossPortalEvent(p, from, to);
			AnniEvent.callEvent(pbpe);

			// Check is not Cacelled
			if (pbpe.isCancelled()) {
				return;
			}

			// Load location Chunk
			if (to.getChunk() != null) {
				if (!to.getChunk().isLoaded()) {
					to.getChunk().load();
					to.getWorld().loadChunk(to.getChunk());
				}
			}

			// Teleport
			if (!Config.BOSS_MAP_LOADING_SEND_PLAYER_FROM_PORTAL_TO_BASE.toBoolean()) {
				// Save Last Loc
				if (KitUtils.isGameMap(to.getWorld())) {
					final Location lastFrom = joinPortalLoc.get(pl.getUniqueId());
					if (lastFrom != null) {
						lastFrom.setYaw(0.0F);
						lastFrom.setPitch(0.0F);
						pl.teleport(lastFrom.clone().add(0.0D, 3.5D, 0.0D));
						pl.setVelocity(pl.getLocation().getDirection().multiply(-1.3));
						event.setCancelled(true);
						KitUtils.addMomentaryFallImmunity(AnniPlayer.getPlayer(pl), 3);
						return;
					}
				} else {
					joinPortalLoc.put(pl.getUniqueId(), from.clone().add(0.0D, 2.0D, 0.0D));
				}
			}

			// Teleport
			pl.teleport(to.clone().add(0.0D, 1.0D, 0.0D));
		}
	}
}
