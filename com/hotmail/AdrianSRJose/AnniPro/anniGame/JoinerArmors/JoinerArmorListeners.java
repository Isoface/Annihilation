package com.hotmail.AdrianSRJose.AnniPro.anniGame.JoinerArmors;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.LobbyMap;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.TeamCommand;

public class JoinerArmorListeners implements Listener {
	
	public JoinerArmorListeners(final AnnihilationMain plugin) {
		// register events.
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * On use joiner to
	 * join a team.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onUse(final PlayerArmorStandManipulateEvent eve) {
		// get player.
		final Player p = eve.getPlayer();
		
		// get and check lobby map.
		final LobbyMap map = Game.LobbyMap;
		if (map == null) {
			return;
		}
		
		// check is joiner stand.
		final ArmorStand stand = eve.getRightClicked();
		if (!map.isJoinerArmor(stand)) {
			return;
		}
		
		// get joiner.
		final JoinerArmorStand joiner = map.getJoinerArmor(stand);
		
		// join team.
		if (joiner.getType() == JoinerArmorStandType.TEAM) { // join specific team.
			p.getPlayer().performCommand("Team " + joiner.getTeam().getName()); // join team
		} else { // join random team.
			// get random team.
			final AnniTeam random = TeamCommand.getRandomTeam();
			if (random != null) {
				p.getPlayer().performCommand("Team " + random.getName()); // join team
			}
		}
		
		// cancell.
		eve.setCancelled(true);
	}
	
	/**
	 * On damage joiner.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onUse(final EntityDamageEvent eve) {
		// check is ArmorStand.
		if (!(eve.getEntity() instanceof ArmorStand)) {
			return;
		}
		
		// get and check lobby map.
		final LobbyMap map = Game.LobbyMap;
		if (map == null) {
			return;
		}
		
		// check is joiner stand.
		final ArmorStand stand = (ArmorStand) eve.getEntity();
		if (!map.isJoinerArmor(stand)) {
			return;
		}
		
		// cancell
		eve.setCancelled(true);
	}
}
