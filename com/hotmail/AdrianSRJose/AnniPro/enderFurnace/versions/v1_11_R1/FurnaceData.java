package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_11_R1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.UFData;

import net.minecraft.server.v1_11_R1.TileEntityFurnace;

public class FurnaceData implements Listener {
	public FurnaceData(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		final Player p = event.getPlayer();
		if (p != null) {
			final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null) {
				if (Furnace.furnaces != null && Furnace.furnaces.containsKey(p.getUniqueId())) {
					final TileEntityFurnace fur = Furnace.furnaces.get(p.getUniqueId());
					if (fur != null && fur.getContents() != null && !fur.getContents().isEmpty()) {
						UFData data = new UFData(ap, fur);
						data.save();
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (p != null) {
			final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			if (ap != null) {
				if (Furnace.furnaces != null && Furnace.furnaces.containsKey(p.getUniqueId())) {
					final TileEntityFurnace fur = Furnace.furnaces.get(p.getUniqueId());
					Object fdata = ap.getData("Player_FData");
					if (fur != null && fdata != null && fdata instanceof UFData) {
						UFData data = ((UFData) fdata);
						//
						if (data != null)
							data.load(fur);
					}
				}
			}
		}
	}
}