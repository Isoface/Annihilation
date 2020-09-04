package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;

public class Bruja implements Listener {
	public ScheduledExecutorService executor;

	public Bruja(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
		executor = Executors.newScheduledThreadPool(3);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void MuerteDeLasBrujas(EntityDeathEvent eve) {
		// Check is game running and if is a witch entity
		if (Game.isGameRunning() && eve.getEntity() instanceof Witch) {
			// Get Variables
			final Witch ent = (Witch) eve.getEntity();
			final UUID entID = ent.getUniqueId();

			// Check if a team witch and start
			for (AnniTeam team : AnniTeam.Teams) {
				UUID id = AnnihilationMain.API.getWitchID(team);
				if (id == null) {
					continue;
				}

				// Check is Witch
				if (id.equals(entID)) {
					// Get Variables
					final Loc spawn = team.getWitchLocation();
					final TimeUnit unit = Game.getGameMap().getWitchRespawnUnit();
					final int time = Game.getGameMap().getWitchRespawnTime();

					// Check is valid spawn and unit.
					if (spawn == null || unit == null) {
						break;
					}

					// Respawn Sheduler
					executor.schedule(new Runnable() {
						@Override
						public void run() {
							Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
								@Override
								public void run() {
									// Check team do not have a already spawned witch
									LivingEntity ow = AnnihilationMain.API.getWitch(team);
									if (ow != null && !ow.isDead()) {
										return;
									}

									// Spawn Team Witch and set modifies
									LivingEntity witch = Game.getGameMap().getWorld().spawn(spawn.toLocation(),
											Witch.class);
									witch.setRemoveWhenFarAway(false);
									witch.getLocation().getWorld().strikeLightningEffect(witch.getLocation());

									if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
										CompatibleParticles.WITCH.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 0.1F,
												10, witch.getLocation().clone(), 1000D);
									} else {
										CompatibleParticles.WITCH.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 0.1F,
												10, witch.getLocation().clone(), 1000D);
									}
									
									witch.addPotionEffect(
											new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));

									// Save Witch
									AnnihilationMain.API.setWitch(team, witch, false);

									// Send Witch Respawn Message
									for (Player all : Bukkit.getOnlinePlayers()) {
										if (KitUtils.isValidPlayer(all)) {
											all.sendMessage(Lang.WITCH_RESPAWN_MESSAGE
													.toStringReplacement(team.getColoredName()));
										}
									}
								}
							});
						}
					}, time, unit);

					// Close for cycle
					break;
				}
			}
		}
	}

}