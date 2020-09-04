package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bobacadodl.imgmessage.ImageChar;
import com.bobacadodl.imgmessage.ImageMessage;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.BossSpawnEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PhaseChangeEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.Announcement;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class StandardPhaseHandler implements Runnable {
	
	// Images Map
	private final Map<Integer, ImageMessage> images;

	// Constructor
	public StandardPhaseHandler(boolean sendFirstImage) {
		// Set HashMap
		images = new HashMap<Integer, ImageMessage>();

		// Get ImageMessages
		for (int x = 1; x < 6; x++) {
			try {
				// Get Phase Image
				BufferedImage image = AnnihilationMain.API.getPhaseBufferedImage(x);
				if (image != null) {
					// Get ImageMessage and append Text To Lines
					ImageMessage message = new ImageMessage(image, 10, ImageChar.MEDIUM_SHADE);
					message.appendTextToLine(5, Lang.PHASESTART.toStringReplacement(x));
					message.appendTextToLines(6, Lang.valueOf("PHASE" + x + "MESSAGE").toStringArray());

					// Save Images
					images.put(x, message);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		// Send First Image (Phase 1)
		if (sendFirstImage) {
			sendImage(1);
		}
	}

	/**
	 * @param phase = phase number
	 */
	private void sendImage(int phase) {
		if (phase < 0 || phase > 5) {
			return;
		}
		
//		if (GameBackup.isLOADING_BACKUP()) {
//			return;
//		}

		if (!Config.USE_CHAT_PHASE_CHANGE_IMAGES.toBoolean()) {
			return;
		}

		// Get Image from Map
		ImageMessage m = images.get(phase);
		if (m == null) {
			return;
		}

		// Send image to everybody.
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (KitUtils.isValidPlayer(player)) {
				m.sendToPlayer(player);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		try {
			if (Game.getGameMap() != null) {
				// Variables
				final GameMap map = Game.getGameMap();
				int newPhase = map.getCurrentPhase() + 1;
				int Fase = newPhase;
				Announcement ann = new Announcement(Lang.PHASEBAR.toStringReplacement(newPhase) + " - {#}",
						Lang.BOSSBAR.toStringReplacement(newPhase) + " - {#}");
				String t = Lang.TITLE_FASE_NOMBRE.toString();
				t = t.contains("%#") ? "" + Lang.TITLE_FASE_NOMBRE.toStringReplacement(Fase) : t + Fase;
				String s = Lang.SUBTITLE_FASE_2.toString();

				// Do
				switch (newPhase) {
				// Fase 2
				case 2:
					// Update phase time and set can damage Nexuses
					map.setCanDamageNexus(true);
					ann.setTime(map.getPhaseTime()).setCallback(this);

					// Spawn Witchs
					for (AnniTeam teams : AnniTeam.Teams) {
						// Check has with Loc
						Loc wls = teams.getWitchLocation();
						if (!Util.isValidLoc(wls)) {
							continue;
						}

						// Load chunck
						Chunk c = wls.toLocation().getChunk();
						if (c != null && !c.isLoaded()) {
							teams.getWitchLocation().toLocation().getWorld().loadChunk(c);
						}

						// Spawn Team Witch and set modifies
						LivingEntity witch = map.getWorld().spawn(teams.getWitchLocation().toLocation(), Witch.class);
						witch.setRemoveWhenFarAway(false);
						witch.getLocation().getWorld().strikeLightningEffect(witch.getLocation());
						
						if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
							CompatibleParticles.WITCH.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 10,
									witch.getLocation().clone(), 1000D);
						} else {
							CompatibleParticles.WITCH.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 10,
									witch.getLocation().clone(), 1000D);
						}
						
						witch.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));

						// Save Witch
						AnnihilationMain.API.setWitch(teams, witch, false);
					}
					break;

				// Fase 3
				// ---------------------------------------------------------------------------------------------------------------//
				case 3:
					// Play phase sound for everybody
					playSoundForAll(
							UniversalSound.AMBIENCE_CAVE.asBukkit(),
							30, 1);

					// Update phase time
					ann.setTime(map.getPhaseTime()).setCallback(this);
					break;

				// Fase 4
				// ---------------------------------------------------------------------------------------------------------------//
				case 4:
					// Update phase time
					ann.setTime(map.getPhaseTime()).setCallback(this);
					break;

				// Fase 5
				// ---------------------------------------------------------------------------------------------------------------//
				case 5:
					// Set Doble damage and permanent Annoucement
					map.setDamageMultiplier(2);
					ann.setPermanent(true).setMessage(Lang.PHASEBAR.toStringReplacement(newPhase));

					// Update BossBar Message
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) { // VersionUtils.isNewSpigotVersion()
						ann.setBossBarMessage(Lang.BOSSBAR.toStringReplacement(5)).setProgress(0.0);
					}
					break;
				}

				// Get next phse and countDown the announcement
				int to = map.getCurrentPhase() + 1;
				AnnounceBar.getInstance().countDown(ann);

				// Call PhaseChangeEvent
				PhaseChangeEvent eve = new PhaseChangeEvent(map.getCurrentPhase(), to);
				AnniEvent.callEvent(eve);

				// Update Phase
				map.setPhase(to);

				// Spawn Diamonds
				spawnDiamonds(to);
				
				// Spawn Boss
				spawnBoss(to);

//				// Update phase in the Scoreboards
//				if (ScoreboardVars.useNewScoreboard) {
//					ScoreboardAPINEW.updatePhase();
//				} else {
//					ScoreboardAPI.updatePhase();
//				}

				// Print new Phase Start in Console
				Util.print(ChatColor.DARK_PURPLE + "Phase " + ChatColor.AQUA + map.getCurrentPhase()
						+ ChatColor.DARK_PURPLE + " Has Begun!");

				// Send Phase Message
				sendImage(map.getCurrentPhase());

				// Send Phase Title for Everybody
				if (Config.USE_PHASE_TITLES.toBoolean()) { // GameVars.useTituloFase
					s = Lang.valueOf("SUBTITLE_FASE_" + newPhase).toString();
					s = s == null ? "" : s;

					// for Everybody
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (KitUtils.isValidPlayer(pl)) {
							Util.sendTitle(pl, t, s);
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void spawnBoss(int phase) {
		if (phase != Config.BOSS_MAP_LOADING_BOSS_SPAWN_PHASE.toInt()) {
			return;
		}
		
		if (phase < 4) {
			return;
		}
		
		// Load Boss Map
		if (Config.BOSS_MAP_LOADING_USE_BOSS_MAP.toBoolean()) {
			final String bossMapName = Config.BOSS_MAP_LOADING_DEFAULT_BOSS_MAP.toString();
			if (bossMapName != null && !bossMapName.isEmpty()) {
				if (GameBoss.loadBossMap(bossMapName, false)) {
					// get and check boss
					final LivingEntity boss = BossReal.SpawnBoss();
					if (boss == null || boss.isDead()) {
						return;
					}
					
					// Get and call event
					final BossSpawnEvent event = new BossSpawnEvent(boss, GameBoss.getBoss().getSpawn().toLocation());
					Bukkit.getPluginManager().callEvent(event);
					
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
						playSoundForAll(UniversalSound.WITHER_SPAWN.asBukkit(), 30.0F, 1.0F);
					}
				}
			}
		}
	}

	private void spawnDiamonds(int phase) {
		if (phase == Config.MAP_LOADING_DIAMONDS_SPAWN_PHASE.toInt()) { // GameVars.DiamonsSpawnPhase
			final GameMap map = Game.getGameMap();
			if (map != null) {
				final List<Loc> dlocs = map.getDiamondLocations();
				if (dlocs != null && !dlocs.isEmpty()) {
					for (Loc loc : dlocs) {
						if (!Util.isValidLoc(loc)) {
							continue;
						}

						Location l = loc.toLocation();
						l.getWorld().getBlockAt(l).setType(Material.DIAMOND_ORE);
					}
				}
			}
		}
	}

	private void playSoundForAll(Sound play, float paramFloat1, float paramFloat2) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (KitUtils.isValidPlayer(p)) {
				p.playSound(p.getLocation(), play, paramFloat1, paramFloat2);
			}
		}
	}
}
