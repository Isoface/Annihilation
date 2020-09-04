package com.hotmail.AdrianSRJose.xpSystem.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameEndEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.NexusHitEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;

public class XPListeners implements Listener {
	private final XPSystem xpSystem;
	private final String xpMessage;
	private final int killXP;
	private final int nexusHitXP;
	private final int[] teamXPs;

	public XPListeners(XPSystem system, String message, int killXP, int nexusXP, int[] teamXPs) {
		this.xpSystem = system;
		this.xpMessage = message;
		this.killXP = killXP;
		this.nexusHitXP = nexusXP;
		this.teamXPs = teamXPs;
	}

	private void sendXPMessage(AnniPlayer player, int XP) {
		player.sendMessage(XPMain.formatString(xpMessage, XP));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void gameEnd(GameEndEvent e) {
		if (e.getWinningTeam() != null && xpSystem.isActive()) {
			for (AnniPlayer p : e.getWinningTeam().getPlayers()) {
				if (p.getPlayer() != null) {
					int amount = XPMain.checkMultipliers(p.getPlayer(), teamXPs[0]);
					xpSystem.giveXP(p.getID(), amount);
					sendXPMessage(p, amount);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void xpNexusHit(NexusHitEvent e) {
		// check is not cancelled.
		if (e.isCancelled() || e.getDamage() <= 0) {
			return;
		}
		
		// Check Nexus
		if (e.getHitNexus() == null || e.getHitNexus().Team == null) {
			return;
		}

		if (xpSystem.isActive() && Game.isGameRunning() && !e.isCancelled()) {
			// Check
			if (e.getPlayer() == null || !e.getPlayer().isOnline()) {
				return;
			}

			// Check
			final Player player = e.getPlayer().getPlayer();
			if (player == null || !player.isOnline()) {
				return;
			}
			/**/
			for (AnniPlayer pl : e.getPlayer().getTeam().getPlayers()) {
				// Check
				if (pl == null || !pl.isOnline() || pl.getID() == null || pl.getPlayer() == null
						|| pl.getPlayer().getName() == null) {
					continue;
				}

				// Add
				int amount = XPMain.checkMultipliers(player, e.getDamage() * nexusHitXP);
				xpSystem.giveXP(pl.getID(), amount);
				sendXPMessage(pl, amount);
			}
			/**/
			if (e.willKillTeam()) {
				AnniTeam t = e.getHitNexus().Team;
				int alive = 0;
				for (AnniTeam team : AnniTeam.Teams) {
					if (!team.isTeamDead() && !team.equals(t)) {
						alive++;
					}
				}

				for (AnniPlayer p : t.getPlayers()) {
					if (p == null || p.getPlayer() == null || p.getID() == null || p.getPlayer().getName() == null) {
						continue;
					}

					int amount = XPMain.checkMultipliers(player, teamXPs[alive]);
					xpSystem.giveXP(p.getID(), amount);
					sendXPMessage(p, amount);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void xpPlayerKill(PlayerKilledEvent e) {
		if (xpSystem.isActive() && Game.isGameRunning()) {
			int amount = XPMain.checkMultipliers(e.getKiller().getPlayer(), killXP);
			xpSystem.giveXP(e.getKiller().getID(), amount);
			sendXPMessage(e.getKiller(), amount);
		}
	}
}
