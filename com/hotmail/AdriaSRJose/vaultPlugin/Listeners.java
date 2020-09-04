package com.hotmail.AdriaSRJose.vaultPlugin;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
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

class Listeners implements Listener {
	private static final DecimalFormat format = new DecimalFormat("#.##");

	private final VaultHook plugin;
	private final String message;

	private final double killMoney;
	private final double nexusHitMoney;
	private final double[] teamMonies;

	public Listeners(VaultHook system, String message, double killXP, double nexusXP, double[] teamXPs) {
		this.plugin = system;
		this.message = message;
		this.killMoney = killXP;
		this.nexusHitMoney = nexusXP;
		this.teamMonies = teamXPs;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void gameEnd(GameEndEvent e) {
		if (e.getWinningTeam() != null) {
			for (AnniPlayer p : e.getWinningTeam().getPlayers()) {
				Player player = p.getPlayer();
				if (player != null) {
					double amount = plugin.checkMultipliers(player, teamMonies[0]);
					this.giveMoney(player, amount);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void xpNexusHit(final NexusHitEvent e) {
		// check.
		if (Game.isGameRunning() && !e.isCancelled() && e.getDamage() > 0) {
			Player player = e.getPlayer().getPlayer();
			assert player != null;

			for (AnniPlayer pl : e.getPlayer().getTeam().getPlayers()) {
				Player player1 = pl.getPlayer();
				if (player1 != null) {
					double amount = plugin.checkMultipliers(player, e.getDamage() * nexusHitMoney);
					this.giveMoney(player1, amount);
				}
			}

			if (e.willKillTeam()) {
				AnniTeam t = e.getHitNexus().Team;
				int alive = 0;
				for (AnniTeam team : AnniTeam.Teams) {
					if (!team.isTeamDead() && !team.equals(t))
						alive++;
				}
				for (AnniPlayer p : t.getPlayers()) {
					Player player1 = p.getPlayer();
					if (player1 != null) {
						double amount = plugin.checkMultipliers(player, teamMonies[alive]);
						this.giveMoney(player1, amount);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void xpPlayerKill(PlayerKilledEvent e) {
		if (Game.isGameRunning()) {
			final Player player = e.getKiller().getPlayer();
			if (player != null) {
				double amount = plugin.checkMultipliers(player, killMoney);
				this.giveMoney(player, amount);
			}
		}
	}

	private void giveMoney(Player player, double amount) {
		if (plugin.giveMoney(player, amount).transactionSuccess())
			sendMoneyMessage(player, amount);
		else
			sendMoneyMessage(player, 0);
	}

	private void sendMoneyMessage(Player player, double money) {
		player.sendMessage(
				ChatColor.translateAlternateColorCodes('&', message.replace("%#", "" + format.format(money))));
	}
}
