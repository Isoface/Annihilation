package com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.TeamDeathEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;
import lombok.Setter;

public class TeamBeacon implements Listener {
	private @Getter AnniTeam team;
	private @Getter Loc defaultLocation;
	private @Getter Map<BeaconEffect, Integer> effects = new HashMap<BeaconEffect, Integer>();
	private Integer task;
	private Integer particleTaskID;
	private boolean someTimeDestroyed         = false;
	private @Getter @Setter boolean activated = false;

	public TeamBeacon(final AnniTeam team, final Loc loc) {
		this.team            = team;
		this.defaultLocation = loc;
		Bukkit.getPluginManager().registerEvents(this, AnnihilationMain.INSTANCE);
	}

	public TeamBeacon(final AnniTeam team, final ConfigurationSection sc) {
		this.team       = team;
		defaultLocation = new Loc(sc.getConfigurationSection("Location"));
		Bukkit.getPluginManager().registerEvents(this, AnnihilationMain.INSTANCE);
	}

	public Loc getBeaconLocation() {
		return defaultLocation != null
				? (new Loc(defaultLocation.toLocation().getBlock().getRelative(BlockFace.UP).getLocation(), false))
						: null;
	}

	private static final org.bukkit.Material IB = Material.IRON_BLOCK;
	private static final org.bukkit.Material ST = Material.NETHER_BRICK_STAIRS;
	public void checkBlock() {
		// Start Particles task
		particleTaskID = Integer.valueOf(new BukkitRunnable() {
			@Override
			public void run() {
				// Check is game running
				if (Game.isNotRunning()) {
					return;
				}

				// Check Game map
				final GameMap map = Game.getGameMap();
				if (map == null) {
					return;
				}
				
				if (!enabled()) {
					return;
				}
				
				// Play Particles
				if (Util.isValidLoc(getBeaconLocation()) 
						&& getBeaconLocation().toLocation().getBlock().getType() == Material.BEACON) {
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.ENCHANTED_HIT.displayNewerVersions().display(0.7F, 0.8F, 0.7F, 0.1F, 60, getBeaconLocation().toLocation().clone().add(0.5D, 0.2D, 0.5D), 90000);
					} else {
						CompatibleParticles.ENCHANTED_HIT.displayOlderVersions().display(0.7F, 0.8F, 0.7F, 0.1F, 60, getBeaconLocation().toLocation().clone().add(0.5D, 0.2D, 0.5D), 90000);
					}
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 10L, 10L).getTaskId());
		
		// Get Center
		final Location l1 = defaultLocation.toLocation();
		if (l1 == null) {
			return;
		}

		// Create Structure
		// Set Center Block
		final Block b1 = l1.getBlock();
		final Block beacon = b1.getRelative(BlockFace.UP);
		b1.setType(IB);
		beacon.setType(Material.AIR);
		b1.getRelative(BlockFace.EAST).setType(IB);
		b1.getRelative(BlockFace.WEST).setType(IB);
		b1.getRelative(BlockFace.SOUTH).setType(IB);
		b1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).setType(IB);
		b1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).setType(IB);
		b1.getRelative(BlockFace.NORTH).setType(IB);
		b1.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).setType(IB);
		b1.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).setType(IB);
		stairs(b1.getRelative(BlockFace.EAST).getRelative(BlockFace.UP), BlockFace.WEST);
		stairs(b1.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP), BlockFace.SOUTH);
		stairs(b1.getRelative(BlockFace.WEST).getRelative(BlockFace.UP), BlockFace.EAST);
		stairs(b1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP), BlockFace.NORTH);
		stairs(b1.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH), BlockFace.WEST);
		stairs(b1.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH), BlockFace.SOUTH);
		stairs(b1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getRelative(BlockFace.EAST), BlockFace.NORTH);
		stairs(b1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getRelative(BlockFace.WEST), BlockFace.EAST);
	}

	private static void stairs(final Block b, final BlockFace face) {
		// Set Type
		b.setType(ST);
		b.getState().setType(ST);

		// Get Stair
		setStairsData(b, face);
	}

	public static void setStairsData(final Block b, BlockFace dir) {
		// get facing data.
		byte d = 0;
		if (dir == BlockFace.WEST) {
			d = 0x1;
		} else if (dir == BlockFace.EAST) {
			d = 0x0;
		} else if (dir == BlockFace.NORTH) {
			d = 0x3;
		} else if (dir == BlockFace.SOUTH) {
			d = 0x2;
		}

		// set data.
		CompatibleUtils.setData(b, d, false);
	}
	
	public void addEffect(BeaconEffect effect, int level) {
		effects.put(effect, Integer.valueOf(level));
	}

	public void removeEffect(BeaconEffect effect) {
		effects.remove(effect);
	}

	public boolean isValid() {
		return team != null && defaultLocation != null;
	}

	public boolean enabled() {
		return isValid() && activated && getBeaconLocation().toLocation().getBlock().getType() == Material.BEACON;
	}

	public void start() {
		// Check
		if (!isValid()) {
			return;
		}

		// Check is not team dead
		if (team.isTeamDead()) {
			return;
		}

		// Stop old task
		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.intValue());
		}

		// Start
		task = Integer.valueOf(new BukkitRunnable() {
			@Override
			public void run() {
				for (AnniPlayer ap : team.getPlayers()) {
					// Check player
					if (ap == null || !ap.isOnline()) {
						continue;
					}

					// Get Player
					final Player p = ap.getPlayer();
					if (p != null) {
						// Add all map potion effects
						for (BeaconEffect type : effects.keySet()) {
							if (type == null || effects.get(type) == null) {
								continue;
							}

							// Add potion effect for 1 second
							Integer level = effects.get(type);
							if (level != null) {
								// Check is on range
								final double distance = p.getLocation().distance(defaultLocation.toLocation());
								if (distance <= type.getRange()) {
									// Add to player
									if (type.getEffect().equals(PotionEffectType.NIGHT_VISION)) {
										// When already have Night Vision
										PotionEffect nightVision = null;
										for (PotionEffect eff : p.getActivePotionEffects()) {
											if (PotionEffectType.NIGHT_VISION.equals(eff.getType())) {
												nightVision = eff;
												break;
											}
										}
										
										if (nightVision != null) {
											if (nightVision.getDuration() <= 400) {
												p.removePotionEffect(PotionEffectType.NIGHT_VISION);
											}
										}
									
										
										// When no have night vision
										p.addPotionEffect(new PotionEffect(type.getEffect(), (type.getEffect().equals(PotionEffectType.NIGHT_VISION) ? 20 * 20 : 10), level));
										continue;
									}
									
									// No Night Vision
									p.addPotionEffect(new PotionEffect(type.getEffect(), 10, level));
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L).getTaskId());
	}

	public void stop() {
		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.intValue());
			task = null;
		}
	}

	// When destroy beacon
	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onB(BlockBreakEvent eve) {
		// Check is game running
		if (Game.isNotRunning()) {
			return;
		}

		// Check is valid beacon
		if (!isValid()) {
			return;
		}

		// Check is enabled
		if (!enabled()) {
			return;
		}

		// Get vals
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final Block b = eve.getBlock();

		// Check
		if (p == null || ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}

		// Check is beacon and get it
		if (!getBeaconLocation().equals(b.getLocation())) {
			return;
		}

		// Cancell event
		eve.setCancelled(true);

		// When is not same team
		if (ap.getTeam().equals(team)) {
			p.sendMessage(Lang.MAGIC_BEACON_CANNOT_DESTROY.toString());
			return;
		}

		// Check is not destroyed
		if (task == null) {
			if (someTimeDestroyed) {
				p.sendMessage(Lang.MAGIC_BEACON_ALREADY_DESTROY.toString());
			} else {
				p.sendMessage(Lang.MAGIC_BEACON_NOT_ENABLED.toString());
			}
			return;
		}

		// Give beacon item
		p.getInventory().addItem(CustomItem.BEACON.toItemStack());
		p.updateInventory();

		// Set material to air
		b.setType(Material.AIR);

		// Set not activated
		setActivated(false);

		// Stop
		stop();

		// Set some time destroyed to true
		someTimeDestroyed = true;

		// Lava Particles
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
			CompatibleParticles.LAVA.displayNewerVersions().display(0.1F, 0.1F, 0.1F, 0.2F, 10, b.getLocation(), 90000.0D);
			CompatibleParticles.LAVA.displayNewerVersions().display(2.0F, 2.0F, 2.0F, 0.5F, 30, b.getLocation(), 90000.0D);
			CompatibleParticles.ENCHANTED_HIT.displayNewerVersions().display(0.3F, 0.3F, 0.3F, 0.3F, 70, b.getLocation(), 90000.0D);
		} else {
			CompatibleParticles.LAVA.displayOlderVersions().display(0.1F, 0.1F, 0.1F, 0.2F, 10, b.getLocation(), 90000.0D);
			CompatibleParticles.LAVA.displayOlderVersions().display(2.0F, 2.0F, 2.0F, 0.5F, 30, b.getLocation(), 90000.0D);
			CompatibleParticles.ENCHANTED_HIT.displayOlderVersions().display(0.3F, 0.3F, 0.3F, 0.3F, 70, b.getLocation(), 90000.0D);
		}

		// Play Sound
		p.playSound(p.getLocation(),
				ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1) 
				? Sound.ENTITY_ELDER_GUARDIAN_HURT : Sound.valueOf("IRONGOLEM_THROW"),
						1F, .8F);

		// Send Destroyed Message
		for (AnniPlayer teamMember : team.getPlayers()) {
			if (teamMember == null || !teamMember.isOnline()) {
				continue;
			}

			// Play destroyed sound
			teamMember.getPlayer().playSound(teamMember.getPlayer().getLocation(),
					ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1) 
					? Sound.ENTITY_ELDER_GUARDIAN_HURT
							: Sound.valueOf("IRONGOLEM_THROW"),
							1F, .8F);

			// Send mess
			teamMember.sendMessage(Lang.MAGIC_BEACON_YOUR_BEACON_IS_CAPTURED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onTD(TeamDeathEvent eve) {
		final AnniTeam death = eve.getKilledTeam();
		if (team.equals(death) && particleTaskID != null) {
			// Cancell task
			Bukkit.getScheduler().cancelTask(particleTaskID.intValue());
			
			// Destroy Task ID
			particleTaskID = null;
		}
	}
}
