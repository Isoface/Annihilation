package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_12_R1.Furnace;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_12_R1.FurnaceData;
import com.hotmail.AdrianSRJose.AnniPro.utils.MapKey;

public class FurnaceHandler implements Listener {
	public FurnaceHandler(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		if (ServerVersion.serverSameVersion(ServerVersion.v1_12_R1)) // VersionUtils.is1_12()
			new FurnaceData(plugin);
		else if (ServerVersion.serverSameVersion(ServerVersion.v1_11_R1)) // VersionUtils.is1_11()
			new com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_11_R1.FurnaceData(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void FurnaceClickCheck(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if (b != null) {
				if (b.getType() == Material.FURNACE 
						|| b.getType() == Material.BURNING_FURNACE) {
					MapKey key = MapKey.getKey(b.getLocation());
					if (Game.getGameMap().enderFurnaces.containsKey(key)) {
						event.setCancelled(true);
						AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
						if (p != null) {
							if (ServerVersion.serverSameVersion(ServerVersion.v1_12_R1)) // VersionUtils.is1_12()
								new Furnace(p.getPlayer());
							else if (ServerVersion.serverSameVersion(ServerVersion.v1_11_R1)) // VersionUtils.is1_11()
								new com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_11_R1.Furnace(
										p.getPlayer());
						}
					}
				}
			}
		}
	}
}
