package com.hotmail.AdrianSRJose.AnniPro.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.NexusHitEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerRevealedEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

public final class InvisibilityListeners implements Listener {
	
	public InvisibilityListeners ( Plugin plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockBreakingChecker(final BlockBreakEvent event) {
		Player player = event.getPlayer();
		AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
		if (p != null) {
			if (Config.REMOVE_INVISIBILITY_ON_BLOCK_BREAK.toBoolean()) { 
				checkInvis(player);
			}
		}
	}

	@EventHandler
	public void nexuChecker(NexusHitEvent event) {
		Player p = event.getPlayer().getPlayer();
		if (p != null) {
			if (Config.REMOVE_INVISIBILITY_ON_DAMAGE_NEXUS.toBoolean()) {
				checkInvis(p);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDamageChecker(final EntityDamageByEntityEvent eve) {
		// Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Annihilation] Anti
		// Invisibility bug. Step 0.");
		final Entity ent = eve.getEntity();
		if (ent != null && ent instanceof Player) {
			// Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Annihilation] Anti
			// Invisibility bug. Step 1.");
			final Player e = (Player) ent;
			final AnniPlayer ed = AnniPlayer.getPlayer(e);
			if (ed != null && ed.isOnline() && ed.hasTeam()) {
				// Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Annihilation] Anti
				// Invisibility bug. Step 2.");
				final Entity da = eve.getDamager();
				if (da != null) {
					// Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Annihilation] Anti
					// Invisibility bug. Step 3.");
					if (da instanceof Player) {
						// Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Annihilation] Anti
						// Invisibility bug. Step 4.");
						final Player d = (Player) da;
						final AnniPlayer ad = AnniPlayer.getPlayer(d);

						if (ad != null && ad.isOnline() && ad.hasTeam()) {
							if (ad.getTeam().equals(ed.getTeam())) {
								// Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Annihilation] Anti
								// Invisibility bug. Step 4_1.");
								eve.setCancelled(true);
							} else {
								if (Config.REMOVE_INVISIBILITY_ON_GET_DAMAGE.toBoolean()) {
									checkInvis(e);
								}
							}
						}
					} else if (da instanceof Projectile) {
						// Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Annihilation] Anti
						// Invisibility bug. Step 5.");
						final ProjectileSource ps = ((Projectile) da).getShooter();
						if (ps != null && ps instanceof Player) {
							// Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Annihilation] Anti
							// Invisibility bug. Step 5_1.");
							final Player st = (Player) ps;
							final AnniPlayer sp = AnniPlayer.getPlayer(st);
							if (sp != null && sp.hasTeam() && sp.getTeam().equals(ed.getTeam())) {
								// Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Annihilation] Anti
								// Invisibility bug. Step 5_2.");
								eve.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	private void checkInvis(Player player) {
		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			PlayerRevealedEvent eve = new PlayerRevealedEvent(AnniPlayer.getPlayer(player));
			AnniEvent.callEvent(eve);
			//
			if (!eve.isCancelled()) {
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
				player.sendMessage(Lang.INVISREVEAL.toString());
			}
		}
	}
}