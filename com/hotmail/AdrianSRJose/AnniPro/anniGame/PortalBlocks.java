package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;

public class PortalBlocks {
	public static GameMap map = Game.getGameMap();

	public static List<Loc> pls = map.getPortalsLocations();

	public PortalBlocks() {
		if (pls == null)
			return;

		for (Loc loc : pls) {
			if (loc == null)
				return;

			int x = loc.toLocation().getBlockX();
			int y = loc.toLocation().getBlockY();
			int z = loc.toLocation().getBlockZ();
			Location l = new Location(map.getWorld(), x, y, z);

			try {
				l.getWorld().getBlockAt(x + 2, y + 1, z).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 2, y, z).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x + 2, y + 1, z - 1).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 2, y, z - 1).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x + 2, y + 1, z + 1).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 2, y, z + 1).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x + 2, y + 1, z + 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 2, y, z + 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x + 2, y + 1, z - 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 2, y, z - 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 2, y + 1, z).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 2, y, z).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 2, y + 1, z - 1).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 2, y, z - 1).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 2, y + 1, z + 1).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 2, y, z + 1).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 2, y + 1, z + 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 2, y, z + 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 2, y + 1, z - 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 2, y, z - 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 1, y + 1, z - 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 1, y, z - 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x + 1, y + 1, z - 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 1, y, z - 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x, y + 1, z - 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x, y, z - 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x - 1, y + 1, z + 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x - 1, y, z + 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x + 1, y + 1, z + 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x + 1, y, z + 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x, y + 1, z + 2).setType(Material.ENDER_PORTAL_FRAME);
				l.getWorld().getBlockAt(x, y, z + 2).setType(Material.ENDER_STONE);

				l.getWorld().getBlockAt(x, y, z).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x + 1, y, z).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x, y, z + 1).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x - 1, y, z).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x, y, z - 1).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x + 1, y, z + 1).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x + 1, y, z - 1).setType(Material.OBSIDIAN);

				l.getWorld().getBlockAt(x - 1, y, z - 1).setType(Material.OBSIDIAN);
				l.getWorld().getBlockAt(x - 1, y, z + 1).setType(Material.OBSIDIAN);

				l.getWorld().getBlockAt(x, y + 1, z).setType(Material.AIR);
				l.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x, y + 1, z + 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x + 1, y + 1, z).setType(Material.AIR);
				l.getWorld().getBlockAt(x - 1, y + 1, z).setType(Material.AIR);
				l.getWorld().getBlockAt(x - 1, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x - 1, y + 1, z + 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x + 1, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x + 1, y + 1, z + 1).setType(Material.AIR);
				
			} catch (NullPointerException e) {

			}
		}
	}

	public static void addPortal() {
		if (pls == null)
			return;

		for (Loc loc : pls) {
			if (loc == null) {
				return;
			}

			int x = loc.toLocation().getBlockX();
			int y = loc.toLocation().getBlockY();
			int z = loc.toLocation().getBlockZ();

			Location l = new Location(map.getWorld(), x, y, z);

			try {
				l.getWorld().getBlockAt(x, y + 1, z).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x, y + 1, z + 1).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x + 1, y + 1, z).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x - 1, y + 1, z).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x - 1, y + 1, z - 1).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x - 1, y + 1, z + 1).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x + 1, y + 1, z - 1).setType(Material.ENDER_PORTAL);
				l.getWorld().getBlockAt(x + 1, y + 1, z + 1).setType(Material.ENDER_PORTAL);
			} catch (NullPointerException e) {
			}
		}
	}

	public static void quitPortal() {
		if (pls == null)
			return;

		for (Loc loc : pls) {
			if (loc == null) {
				return;
			}

			int x = loc.toLocation().getBlockX();
			int y = loc.toLocation().getBlockY();
			int z = loc.toLocation().getBlockZ();

			Location l = new Location(map.getWorld(), x, y, z);

			try {
				l.getWorld().getBlockAt(x, y + 1, z).setType(Material.AIR);
				l.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x, y + 1, z + 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x + 1, y + 1, z).setType(Material.AIR);
				l.getWorld().getBlockAt(x - 1, y + 1, z).setType(Material.AIR);
				l.getWorld().getBlockAt(x - 1, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x - 1, y + 1, z + 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x + 1, y + 1, z - 1).setType(Material.AIR);
				l.getWorld().getBlockAt(x + 1, y + 1, z + 1).setType(Material.AIR);
			} catch (Throwable e) {
			}
		}
	}
}
