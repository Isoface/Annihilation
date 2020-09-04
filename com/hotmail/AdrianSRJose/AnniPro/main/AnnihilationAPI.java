package com.hotmail.AdrianSRJose.AnniPro.main;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

public class AnnihilationAPI {
	private final AnnihilationMain instance;
	private final Map<AnniTeam, UUID> witchs;

	public AnnihilationAPI(AnnihilationMain main) {
		Validate.notNull(main, "The main cant be null!");
		Validate.isTrue(main.isEnabled(), "The main cant be disabled!");
		instance = main;
		witchs = new HashMap<AnniTeam, UUID>();
	}

	public BufferedImage getTeamBufferedImage(final AnniTeam team) {
		return instance.getTeamBufferedImage(team);
	}

	public BufferedImage getPhaseBufferedImage(final int phase) {
		return (phase > 0 && phase <= 5) ? instance.getPhaseBufferedImage(phase) : null;
	}

	public BufferedImage getGameEndBufferedImage() {
		return instance.getGameEndImage();
	}

	public GameMap getGameMap() {
		return Game.getGameMap();
	}

	public World getGameMapWorld() {
		return getGameMap() != null ? getGameMap().getWorld() : null;
	}

	public UUID getWitchID(final AnniTeam team) {
		return team != null ? witchs.get(team) : null;
	}

	public LivingEntity getWitch(final AnniTeam team) {
		// Validate
		Validate.isTrue(Game.isGameRunning(), "The game must be running!");
		Validate.notNull(team, "Thet team cant be null!");

		// Get Witch id from team
		LivingEntity tor = null;
		if (getWitchID(team) == null) {
			return tor;
		}

		// Get Witch in the Game Map World from id
		if (getGameMapWorld() != null) {
			for (LivingEntity o : getGameMapWorld().getLivingEntities()) {
				if (o == null || o.getUniqueId() == null || o.isDead()) {
					continue;
				}

				if (o.getUniqueId().equals(getWitchID(team))) {
					tor = o;
					break;
				}
			}
		}
		return tor;
	}

	public AnniPlayer getAnniPlayer(final Player p) {
		return AnniPlayer.getPlayer(p);
	}

	public void setWitch(final AnniTeam team, final LivingEntity witch, boolean killOld) {
		// Validate
		Validate.notNull(team, "The team cant be null!");
		Validate.notNull(witch, "The witch cant be null!");
		Validate.isTrue(!witch.isDead(), "The witch cant be dead!");

		// Check no same
		final UUID oldID = getWitchID(team);
		if (oldID != null) {
			if (oldID.equals(witch.getUniqueId())) {
				return;
			}
		}

		// Kill Old
		final LivingEntity oldWitch = getWitch(team);
		if (oldWitch != null && !oldWitch.isDead() && killOld) {
			oldWitch.remove();
		}

		// Modifies
		witch.setRemoveWhenFarAway(false);
		witch.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));

		// Save
		witchs.put(team, witch.getUniqueId());
	}

	public void setTeam(final AnniPlayer ap, final AnniTeam team, boolean sendImageMessage) {
		// Validate
		Validate.notNull(ap, "The Player cant be null!");
		Validate.isTrue(ap.isOnline(), "The player cant be offline!");

		// Get Team
		final AnniTeam old = ap.getTeam();
		if (team == null && ap.hasTeam()) {
			leaveTeam(ap);
			return;
		}

		// Validate
		Validate.notNull(team, "The team cant be null!");
		Validate.isTrue((!team.equals(ap.getTeam())), "The player is already on this team!");

		// Check has Team
		if (old != null) {
			old.leaveTeam(ap);
//			if (VersionUtils.getIntVersion() >= 11) {
//				TeamCommandNew.leave(ap);
//			} else {
				TeamCommand.leave(ap);
//			}
		}

		// Join Team
//		if (VersionUtils.getIntVersion() >= 11) {
//			TeamCommandNew.joinTeam(ap, team, false);
//		} else {
			TeamCommand.joinTeam(ap, team, false, sendImageMessage);
//		}
	}

	public void leaveTeam(final AnniPlayer ap) {
		// Validate
		Validate.notNull(ap, "The Player cant be null!");
		Validate.isTrue(ap.isOnline(), "The player cant be offline!");
		Validate.isTrue(ap.hasTeam(), "The player dont have team!");

		// Get Old Team
		final AnniTeam old = ap.getTeam();

		// Leave
		old.leaveTeam(ap);

		// Leave from Team Command Class
//		if (VersionUtils.getIntVersion() >= 11) {
//			TeamCommandNew.leave(ap);
//		} else {
			TeamCommand.leave(ap);
//		}

		// Call Event
		callEvent(new PlayerLeaveTeamEvent(ap, old));

		// Kill
		if (Game.isGameRunning()) {
			ap.getPlayer().setHealth(0.0D);
		}
	}

	public void callEvent(Event eve) {
		AnniEvent.callEvent(eve);
	}

	public void setKit(final AnniPlayer ap, final Kit kit, boolean kill) {
		// Validate
		Validate.notNull(ap, "The Player cant be null!");
		Validate.isTrue(ap.isOnline(), "The player cant be offline");
		Validate.notNull(kit, "The Kit cant be null!");

		// Cleanup Old Kit
		final Kit old = ap.getKit();
		if (old != null) {
			old.cleanup(ap);
		}

		// Set Kit
		ap.setKit(kit);
		if (Game.isGameRunning()) {
			// Kill if has team
			if (kill) {
				if (ap.hasTeam()) {
					ap.getPlayer().setHealth(0.0D);
				}
			} else {
				// Add items to inventory
				KitUtils.removeSoulbounds(ap.getPlayer().getInventory(), false);
				kit.onItemClick(ap.getPlayer().getInventory(), ap);
			}
		}
	}

	public void setTeamHealth(final AnniTeam team, final int health) {
		// Validate
		Validate.notNull(team, "The team cant be null!");
		Validate.isTrue((!team.isTeamDead()), "The team cant be dead!");
//		Validate.isTrue(health <= 99, "The Max health must be 99");
		Validate.isTrue(Game.isGameRunning(), "The Game is not Running!");

		// Destroy
		if (health <= 0) {
			destroyTeam(team);
			return;
		}

		// set health
		team.setHealth(health);
		
//		// Update scoreboards
//		if (ScoreboardVars.useNewScoreboard) {
//			ScoreboardAPINEW.updateTeams();
//		} else {
//			ScoreboardAPI.setScore(team, team.getHealth());
//		}
	}

	public void destroyTeam(final AnniTeam team) {
		// Validate
		Validate.notNull(team, "The team cant be null!");
		Validate.isTrue((!team.isTeamDead()), "The team cant be dead!");
		Validate.isTrue(Game.isGameRunning(), "The Game is not Running!");

		// Old Health
		final int oldHealth = team.getHealth();

		// Set Health
		team.setHealth(0);

//		if (ScoreboardVars.useNewScoreboard) {
//			ScoreboardAPINEW.updateTeams();
//		} else {
//			ScoreboardAPINEW.removeTeam(team, oldHealth);
//		}
	}

	public void sendPlayerToLobby(final AnniPlayer ap, boolean leaveTeam, boolean deadSpawn) {
		// Check
		if (!KitUtils.isValidPlayer(ap)) {
			return;
		}

		if (Game.LobbyMap == null) {
			return;
		}

		if (Game.LobbyMap.getSpawn() == null) {
			return;
		}

		// Leave Team
		if (leaveTeam) {
			leaveTeam(ap);
		}

		// Send to Spawn
		if (deadSpawn) {
			Game.LobbyMap.sendToDeadSpawn(ap.getPlayer());
		} else {
			Game.LobbyMap.sendToSpawn(ap.getPlayer());
		}
	}

	public void sendToBossMap(final AnniPlayer ap) {
		// Check
		if (!KitUtils.isValidPlayer(ap)) {
			return;
		}

		if (GameBoss.getBossMap() == null) {
			return;
		}

		if (GameBoss.getBossMap().getWorld() == null) {
			return;
		}

		// Get Where To
		final Location to = GameBoss.getBossMap().getRandomSpawn();
		if (to != null) {
			ap.getPlayer().teleport(to);
		}
	}
	
	public void sendToGameMap(final AnniPlayer ap, boolean kill) {
		// Check
		if (!KitUtils.isValidPlayer(ap) || !ap.hasTeam()) {
			return;
		}
		
		if (Game.getGameMap() == null || Game.getGameMap().getWorld() == null) {
			return;
		}
		
		if (kill) {
			ap.getPlayer().setHealth(0.0D);
			return;
		}

		// Get Where To
		final Location to = ap.getTeam().getRandomSpawn();
		if (to != null) {
			ap.getPlayer().teleport(to);
		}
	}
}