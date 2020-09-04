package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerInteractAtSoulboundItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJoinTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveServerEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPC;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCDamageEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCDeathEvent;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;

public class GameListeners2 implements Listener 
{
	public GameListeners2(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onNPCDeath(final EntityDeathEvent eve) {
		final Entity ent = eve.getEntity();
		if (ent == null) {
			return;
		}
		
		final EntityType type = ent.getType();
		if (type != EntityType.PLAYER) {
			return;
		}

		// Check is NPC
		if (!ent.hasMetadata("PLAYER_NPC")) {
			return;
		}
		
		// Get LivingEntity and Killer
		final LivingEntity liv = (LivingEntity) ent;
		final Player    killer = liv.getKiller();

		// Call Event
		final NPCDeathEvent event = new NPCDeathEvent(liv, killer, eve.getDrops());
		AnniEvent.callEvent(event);
		
		// Change Drops
		eve.getDrops().clear();
		if (event.getDrops() != null) {
			for (ItemStack st : event.getDrops()) {
				if (st == null) {
					continue;
				}
				
				// Add
				eve.getDrops().add(st);
			}
		}
	}
	
	// On Damage NPC
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDNpc(final NPCDamageEvent eve) {
		final NPC npc = eve.getNpc();
		if (npc == null || npc.getTeam() == null) {
			return;
		}
		
		final Entity ent = eve.getDamager();
		if (ent instanceof Player) {
			final AnniPlayer ap = AnniPlayer.getPlayer(ent.getUniqueId());
			if (ap == null || !ap.getTeam().equals(npc.getTeam())) {
				return;
			}
			
			// Cancell
			eve.setCancelled(true);
			eve.setDamage(0.0D);
		}
		else if (ent instanceof Projectile) {
			final Projectile prj = (Projectile) ent;
			if (prj.getShooter() instanceof Player) {
				final AnniPlayer ap = AnniPlayer.getPlayer(((Player)prj.getShooter()).getUniqueId());
				if (ap == null || !ap.getTeam().equals(npc.getTeam())) {
					return;
				}
				
				// Cancell
				eve.setCancelled(true);
				eve.setDamage(0.0D);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQut(PlayerQuitEvent eve) {
		AnniEvent.callEvent(new PlayerLeaveServerEvent(AnniPlayer.getPlayer(eve.getPlayer()), eve.getQuitMessage(), false));
		AnnounceBar.getInstance().getBar().removeBossBar(eve.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent eve) {
		AnniEvent.callEvent(new PlayerLeaveServerEvent(AnniPlayer.getPlayer(eve.getPlayer()), eve.getReason(), true));
		AnnounceBar.getInstance().getBar().removeBossBar(eve.getPlayer());
	}
	
	// Anti Crops Break
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTrample(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() == Action.PHYSICAL) {
			final Block block = event.getClickedBlock();
			if (block == null) {
				return;
			}
			
			if (ServerVersion.serverOlderEqualsThan(ServerVersion.v1_12_R1)) { /* check server version is older or equals to 1.12 */
				if (block.getType().name().equals("CROPS")) {
					event.setUseInteractedBlock(Event.Result.DENY);
					event.setCancelled(true);
//					block.setTypeId(blockType);				
					block.setType(org.bukkit.Material.valueOf("CROPS"));
				}
			}
			
			if (block.getType() == Material.SOIL) {
				event.setUseInteractedBlock(Event.Result.DENY);
				event.setCancelled(true);
				block.setType(Material.SOIL);
			}
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinTeamEvent eve) {
		if (!eve.isCancelled()) {
			AnniPlayer ap = eve.getPlayer();
			if (ap != null && ap.isOnline() && Game.isGameRunning()) {
				ap.getPlayer().setGameMode(GameMode.SURVIVAL);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void playerCheck(PlayerJoinEvent event) {
		final AnniPlayer ap = AnniPlayer.getPlayer(event.getPlayer());
		try {
			if (ap != null && ap.getPlayer() != null) {
				final Player p = ap.getPlayer();
				boolean tt = ap.hasTeam();
				
//				if (!Game.isGameRunning()) {
//					LobbyScoreboard.registerPlayerOnTeamAndBoard(p, (tt ? false : true));
//				} else {
//					if (ScoreboardVars.useNewScoreboard) {
//						ScoreboardAPINEW.registerPlayerOnTeamAndBoard(p, (tt ? false : true));
//					} else {
//						ScoreboardAPI.registerPlayerOnTeamAndBoard(p, (tt ? false : true));
//					}
//				}
				
				if ( tt ) {
					ap.getTeam().verifyInBoardTeam(p);
//					if (!LobbyScoreboard.teams.get(ap.getTeam()).hasPlayer(p)) {
//						LobbyScoreboard.teams.get(ap.getTeam()).addPlayer(p);
//					}
				}
				
//				if (!Game.isGameRunning()) {
//					LobbyScoreboard.registerPlayerOnTeamAndBoard(p, (tt ? false : true));
//				} else {
//					if (ScoreboardVars.useNewScoreboard)
//						ScoreboardAPINEW.registerPlayerOnTeamAndBoard(p, (tt ? false : true));
//					else
//						ScoreboardAPI.registerPlayerOnTeamAndBoard(p, (tt ? false : true));
//				}
//				
//				if (tt) {
//					ap.getTeam().verifyInBoardTeam(p);
//					if (!LobbyScoreboard.teams.get(ap.getTeam()).hasPlayer(p)) {
//						LobbyScoreboard.teams.get(ap.getTeam()).addPlayer(p);
//					}
//					ap.getTeam().setPlayerListName(p);
//				}
			}
		} catch (Throwable e) {
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerCheck(PlayerLeaveTeamEvent event) {
		if (event.isCancelled()) {
			return;
		}

		final AnniPlayer ap = event.getPlayer();
		final Player p = ap.getPlayer();
		if (ap == null || ap.getPlayer() == null) {
			return;
		}

//		try {
//			LobbyScoreboard.teams.get(event.getTeam()).addPlayer(ap.getPlayer());
//			Team t = LobbyScoreboard.getLobbyTeam();
//			if (!t.hasPlayer(p)) {
//				t.addPlayer(p);
//			}
//
//			// Set Player List name
//			p.setPlayerListName(LobbyScoreboard.getLobbysPrefix() + p.getName());
//		} catch (Throwable e) {
//		}
	}

	@EventHandler // KitUtils.addFallInmunity() & KitUtils.addOneFallInmunity() void
	public void onDamage(EntityDamageEvent eve) {
		if (!(eve.getEntity() instanceof Player)) {
			return;
		}

		final Player p = (Player) eve.getEntity();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}

		// When is from an Entity
		if (eve.getCause() == DamageCause.ENTITY_ATTACK) {
			Object obj3 = ap.getData("anni-hit-damage-imm");
			if (obj3 instanceof String) {
				String val = (String) obj3;
				String[] vals = val.split(";");
				if (vals.length == 2) {
					// Values
					Long seconds = Long.valueOf(vals[0]);
					Long last = Long.valueOf(vals[1]);

					// Cancell
					if (seconds != null && last != null) {
						if (((System.currentTimeMillis() - last.longValue()) / 1000) <= seconds) {
							eve.setCancelled(true);
						}
					}
				}
			}
			return;
		}

		if (eve.getCause() != DamageCause.FALL) {
			return;
		}

		Object obj = ap.getData("kafy");
		Object obj2 = ap.getData("one-kafy");
		if (obj instanceof String) {
			String val = (String) obj;
			String[] vals = val.split(";");
			if (vals.length == 2) {
				// Values
				Long seconds = Long.valueOf(vals[0]);
				Long last = Long.valueOf(vals[1]);

				// Cancell
				if (seconds != null && last != null) {
					if (((System.currentTimeMillis() - last.longValue()) / 1000) <= seconds) {
						eve.setCancelled(true);
					}
				}
			}
		}

		if (obj2 instanceof String) {
			ap.setData("one-kafy", null);
			eve.setCancelled(true);
		}
	}

	// PlayerInteractAtSoulboundItemEvent called!
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInt(PlayerInteractEvent eve) {
		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		final ItemStack item = eve.getItem();

		if (item == null || ap == null) {
			return;
		}

		if (!KitUtils.isAnySoulbound(item)) {
			return;
		}

		if (CustomItem.isCustomItem(eve.getItem())) {
			return;
		}

		PlayerInteractAtSoulboundItemEvent event = new PlayerInteractAtSoulboundItemEvent(eve);
		Bukkit.getPluginManager().callEvent(event);

		eve.setCancelled(event.isCancelled());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJ(PlayerJoinEvent eve) {
		if (Game.isNotRunning() || !Config.SEND_PLAYERS_TO_HIS_NEXUS_ON_RECONNECT.toBoolean()) {
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		if (ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}

		final Location sp = ap.getTeam().getRandomSpawn();
		if (sp != null) {
			ap.getPlayer().teleport(sp);
		}
	}
}
