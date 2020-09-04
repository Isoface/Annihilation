package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class PlayerNPCManager implements Listener 
{
	private static final List<UUID> IN_COMBAT = new ArrayList<UUID>();

	public PlayerNPCManager(JavaPlugin pl) {
		try {
			final String className = "com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.PlayerNPC.versions."
					+ ServerVersion.getVersion().toString() + ".NPCFactory";
			Class<?> cl = Class.forName(className);
			Class<? extends Listener> pack = cl.asSubclass(Listener.class);
			Bukkit.getPluginManager().registerEvents(pack.getConstructor(Plugin.class).newInstance(pl), pl);

			// Combat Log
			if (Config.NPC_SPAWN_ONLY_IN_COMBAT.toBoolean()) {
				new CombatLog(pl);
			}
		} catch (Throwable t) {
			Util.print(ChatColor.RED, "Unsupported Server Version for the NPCs:");
			t.printStackTrace();
		}
	}

	private class CombatLog implements Listener {
		public CombatLog(JavaPlugin pl) {
			Bukkit.getPluginManager().registerEvents(this, pl);
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onD(final EntityDamageByEntityEvent eve) {
			// Check
			if (Game.isNotRunning() || Config.NPC_COMBAT_MODE_SECONDS.toInt() <= 0) {
				return;
			}

			// Get and Check entities
			final Entity v = eve.getEntity();
			final Entity ed = eve.getDamager();
			if (!(v instanceof Player) || ed == null) {
				return;
			}

			// Get Victim Player and Damager Player
			final Player p = (Player) v;
			Player d = (ed instanceof Player) ? (Player) ed : null;
			// When is a Projectile
			if (ed instanceof Projectile) {
				final Projectile pr = (Projectile) ed;
				final ProjectileSource so = pr.getShooter();
				if (so instanceof Player) {
					d = (Player) so;
				}
			}
			// When is a pet
			else if (ed instanceof Tameable) {
				final Tameable them = (Tameable) ed;
				final AnimalTamer at = them.getOwner();
				if (at instanceof Player) {
					d = (Player) at;
				}
			}

			// Check damager player
			if (d != null) {
				// Set in combat
				IN_COMBAT.add(p.getUniqueId());
				IN_COMBAT.add(d.getUniqueId());

				// Monitors
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE,
						new COMBAT_MONITOR(p.getUniqueId()), (Config.NPC_COMBAT_MODE_SECONDS.toInt() * 20));
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE,
						new COMBAT_MONITOR(d.getUniqueId()), (Config.NPC_COMBAT_MODE_SECONDS.toInt() * 20));
			}
		}
	}

	private static class COMBAT_MONITOR implements Runnable {
		private final UUID ID;
		COMBAT_MONITOR(final UUID ID) {
			this.ID = ID;
		}

		@Override
		public void run() {
			if (ID != null) {
				IN_COMBAT.remove(ID);
			}
		}
	}
	
	public static boolean isInCombat(final Player p) {
		return IN_COMBAT.contains(p.getUniqueId());
	}
	
	public static void setInCombat(final Player p, boolean b) {
		if (p != null) {
			if (b) {
				if (!IN_COMBAT.contains(p.getUniqueId())) {
					IN_COMBAT.add(p.getUniqueId());
				}
			}
			else {
				IN_COMBAT.remove(p.getUniqueId());
			}
		}
	}
}
