package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.NexusHitEvent;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class FastHitAntihack implements Listener {
	private final Map<UUID, Long> lh;

	public FastHitAntihack(JavaPlugin pl) {
		Bukkit.getPluginManager().registerEvents(this, pl);
		lh = new HashMap<UUID, Long>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(final NexusHitEvent eve) {
		final AnniPlayer b = eve.getPlayer();
		if (b == null || !b.isOnline()) {
			return;
		}

		// Get Player
		final Player c = b.getPlayer();

		// Check Y
//		if (b(c, eve.getHitNexus())) {
//			eve.setCancelled(true);
//			return;
//		}

		// Continue
		if (!lh.containsKey(c.getUniqueId()) || lh.get(c.getUniqueId()) == null) {
			lh.put(c.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
			return;
		}

		final Long hs = lh.get(c.getUniqueId());
		final Long r = (System.currentTimeMillis() - hs);
		if (r <= 52L) {
			c.kickPlayer(Lang.SMHD.toString());
			eve.setCancelled(true);
			eve.setDamage(0);
			if (eve.getHitNexus() != null && eve.getHitNexus().Team != null) {
				AnnihilationMain.API.setTeamHealth(eve.getHitNexus().Team, (eve.getHitNexus().Team.getHealth() + 1));
			}
		}

		lh.put(c.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
	}

	private boolean b(final Player p, final Nexus nexus) {
		if (nexus != null && Util.isValidLoc(nexus.getLocation())) {
			final Location nex = nexus.getLocation().toLocation();
			final Location loc = p.getLocation();
			if (Math.abs(loc.getY() - nex.getY()) > 3.0D) {
				if (loc.distance(nex) >= 2.8) { // 2.0, 2.5
					return true;
				}
			}
		}
		return false;
	}
}
