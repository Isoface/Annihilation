package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class Areas implements Iterable<Area>, Listener {
	private final Map<String, Area> areas;
	private Area playArea = null;

	//
	public Areas(String world) {
		areas = new HashMap<String, Area>();
	}

	public void addArea(Area a) {
		areas.put(a.getName().toLowerCase(), a);
	}

	public void setPlayArea(Area newPlayArea) {
		playArea = newPlayArea;
	}

	public void removeArea(String name) {
		areas.remove(name.toLowerCase());
	}

	public boolean hasArea(String name) {
		return areas.containsKey(name.toLowerCase());
	}

	public Areas loadAreas(ConfigurationSection areaSection) {
		if (areaSection != null) {
			for (String key : areaSection.getKeys(false)) {
				ConfigurationSection area = areaSection.getConfigurationSection(key);
				Area a = new Area(area);
				areas.put(a.getName().toLowerCase(), a);
			}
		}
		return this;
	}

	public Area getArea(String name) {
		return areas.get(name.toLowerCase());
	}

	public Area getArea(Loc loc) {
		if (loc == null)
			return null;
		//
		for (Area a : areas.values()) {
			if (a.isInArea(loc))
				return a;
		}
		return null;
	}

	public boolean isInSomeArea(Loc l) {
		return getArea(l) != null;
	}

	public boolean isInSomeArea(Location l) {
		if (l == null)
			return false;
		//
		Loc loc = new Loc(l, false);
		//
		return isInSomeArea(loc);
	}

	/*
	 * public void saveToConfig(ConfigurationSection areaSection) { int counter = 1;
	 * for(Area a : areas.values()) { ConfigurationSection sec =
	 * areaSection.createSection(counter+""); a.saveToConfig(sec); counter++; } }
	 */

	// save += Util.setUpdatedIfNotEqual(section, "patch", value);
	public int saveToConfig(ConfigurationSection areaSection) {
		int save = 0;
		int counter = 1;
		//
		for (Area a : areas.values()) {
			if (a != null) {
				save += Util.createSectionIfNoExitsInt(areaSection, counter + "");
				//
				save += a.saveToConfig(areaSection.getConfigurationSection(counter + ""));
				counter++;
			}
		}
		//
		return save > 0 ? 1 : 0;
	}

	public void registerListener(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void checkBreaks(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == EntityType.PLAYER) {
			Area a = this.getArea(new Loc(e.getDamager().getLocation(), false));
			if (a != null && !a.getAllowPVP()) {
				e.setCancelled(true);
				return;
			}

			a = this.getArea(new Loc(e.getEntity().getLocation(), false));
			if (a != null && !a.getAllowPVP()) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void checkFood(FoodLevelChangeEvent event) {
		Area a = getArea(new Loc(event.getEntity().getLocation(), false));
		if (a != null && !a.getAllowHunger()) {
			event.setFoodLevel(20);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void checkDamage(EntityDamageEvent event) {
		Area a = getArea(new Loc(event.getEntity().getLocation(), false));
		if (a != null && !a.getAllowDamage())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void checkBreaks(BlockBreakEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			Loc block = new Loc(e.getBlock().getLocation(), false);
			Area a = this.getArea(block);
			//
			if (a != null || (hasPlayArea() && !isOnPlayArea(block))) {
				e.setCancelled(true);
				e.getBlock().getLocation().getWorld().playEffect(e.getBlock().getLocation().add(0.0D, 1D, 0.0D),
						Effect.SMOKE, 4, 100);
				e.getBlock().getLocation().getWorld().playEffect(e.getBlock().getLocation(), Effect.EXTINGUISH, 1, 100);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void checkBreaks(BlockPlaceEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			Loc block = new Loc(e.getBlock().getLocation(), false);
			Area a = this.getArea(block);
			//
			if (a != null || (hasPlayArea() && !isOnPlayArea(block))) {
				e.setCancelled(true);
				e.getBlock().getLocation().getWorld().playEffect(e.getBlock().getLocation().add(0.0D, 1D, 0.0D),
						Effect.SMOKE, 4, 100);
				e.getBlock().getLocation().getWorld().playEffect(e.getBlock().getLocation(), Effect.EXTINGUISH, 1, 100);
			}
		}
	}

	private boolean isOnPlayArea(Loc l) {
		return playArea.isInArea(l);
	}

	public boolean hasPlayArea() {
		return playArea != null;
	}

	public Area getPlayArea() {
		return playArea;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void checkBreaks(PlayerBucketEmptyEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			final Loc block = new Loc(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), false);
			final Area a = this.getArea(block);
			if (a != null || (hasPlayArea() && !isOnPlayArea(block))) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void flow(final BlockFromToEvent event) {
		final Block b = event.getBlock();
		final Block c = event.getToBlock();
		if (!b.isLiquid()) {
			return;
		}

		// When is joining in some area
		if (isInSomeArea(new Loc(c.getLocation(), true))) {
			event.setCancelled(true);
		}
		
		// When is closing the play area
		if ((hasPlayArea() && !isOnPlayArea(new Loc(c.getLocation(), true)))) {
			event.setCancelled(true);
		}
	}

	public static void playAntiBuildEffect(Location loc, boolean precise) {
		if (precise) {
			loc = loc.add(0.0D, 1D, 0.0D);
		}

		loc.getWorld().playEffect(loc, Effect.SMOKE, 4, 100);
		loc.getWorld().playEffect(loc, Effect.EXTINGUISH, 1, 100);
	}

	public static void playAntiBuildEffect(Block block, boolean precise) {
		Location loc = block.getLocation();
		if (precise) {
			loc = loc.add(0.0D, 1D, 0.0D);
		}

		block.getWorld().playEffect(loc, Effect.SMOKE, 4, 100);
		block.getWorld().playEffect(loc, Effect.EXTINGUISH, 1, 100);
	}

	@Override
	public Iterator<Area> iterator() {
		return areas.values().iterator();
	}
}
