package com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MagicBeacon.BeaconMenu;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class BeaconListener implements Listener
{
	private static final Map<AnniTeam, BeaconMenu> MENUS = new HashMap<AnniTeam, BeaconMenu>();
	public BeaconListener(AnnihilationMain main) {
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	// Opening Beacon Menu
	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onC(PlayerInteractEvent eve) {
		// Checing if is clicking block
		if (eve.getClickedBlock() == null || eve.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		// Checking Player.
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}
		
		// CHECKING CLICKED BLOCK
		if ( eve.getClickedBlock ( ).getType ( ) == Material.BEACON ) {
			// Check is not clicking other team beacon
			for (AnniTeam tt : AnniTeam.Teams) {
				if (tt.getBeacon() != null && Util.isValidLoc(tt.getBeacon().getBeaconLocation())) {
					if (tt.getBeacon().getBeaconLocation().equals(eve.getClickedBlock().getLocation())) {
						eve.setCancelled(true);
					}
				}
			}
			
			// Check is not clicking the not captured beacon
			if (Game.getGameMap() != null && Util.isValidLoc(Game.getGameMap().getBeaconLoc())) {
				if (Game.getGameMap().getBeaconLoc().equals(eve.getClickedBlock().getLocation())) {
					eve.setCancelled(true);
				}
			}
		}
		else {
			return;
		}
		
		// Checking the team beacon is not null.
		final TeamBeacon beacon = ap.getTeam().getBeacon();
		if (beacon == null || !Util.isValidLoc(beacon.getBeaconLocation()) || !beacon.isValid() || !beacon.isActivated()) {
			return;
		}
		
		// Checking is clicking his team beacon.
		final Location loc   = beacon.getBeaconLocation().toLocation();
		final Location block = eve.getClickedBlock().getLocation();
		if (!loc.equals(block)) {
			return;
		}
		
		// Checking menu is in the map.
		if (!MENUS.containsKey(ap.getTeam()) 
				&& !(MENUS.get(ap.getTeam()) instanceof BeaconMenu)) {
			MENUS.put(ap.getTeam(), new BeaconMenu(ap.getTeam()));
		}
		
		// Cancelleing Event
		eve.setCancelled(true);
		p.getOpenInventory().close();
		p.closeInventory();
		
		// Getting Menu from the map and open it.
		final BeaconMenu menu = MENUS.get(ap.getTeam());
		menu.open(p);
		menu.build();
		menu.update(p);
	}
}
