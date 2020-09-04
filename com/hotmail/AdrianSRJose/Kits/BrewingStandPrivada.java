package com.hotmail.AdrianSRJose.Kits;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class BrewingStandPrivada {
	private static final Map<UUID, BrewingStandPrivada> brewing = new ConcurrentHashMap<UUID, BrewingStandPrivada>();
	private Location location;
	private UUID ap;

	private BrewingStandPrivada(Location loc, AnniPlayer ap) {
		location = loc;
		loc.getBlock().setType(UniversalMaterial.BREWING_STAND.getMaterial());
		this.ap = ap.getID();
	}

	public static void crearBrewingStandPrivada(Location loc, AnniPlayer ap) {
		if (ap == null || loc == null) {
			return;
		}

		// Put new BrewingStandPrivada
		BrewingStandPrivada stand = new BrewingStandPrivada(loc, ap);
		brewing.put(ap.getID(), stand);
	}

	public static ItemStack getBrewingStand() {
		ItemStack stack = new ItemStack(UniversalMaterial.BREWING_STAND_ITEM.getMaterial());
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		}
		meta.setDisplayName(Alchemist.PRIVATE_BREWING_STAND_NAME);
		stack.setItemMeta(meta);
		return KitUtils.addSoulbound(stack);
	}

	public UUID getOwnerID() {
		return ap;
	}

	public AnniPlayer getOwner() {
		return AnniPlayer.getPlayer(ap);
	}

	public void removeBrewing() {
		if (Util.isValidLoc(location)) {
			// Get brewing
			Block bl = location.getBlock();
			if (bl != null && bl.getState() instanceof BrewingStand) {
				BrewingStand breg = (BrewingStand) bl.getState();
				if (breg != null) {
					breg.getInventory().setItem(4, null);
				}
			}

			// Set Material to Air
			bl.setType(UniversalMaterial.AIR.getMaterial());
		}

		// Remove Brewing from map
		brewing.remove(ap);
	}

	public static BrewingStandPrivada getBrewingStand(final Player pl) {
		return pl != null ? brewing.get(pl.getUniqueId()) : null;
	}

	public static BrewingStandPrivada getBrewingStand(final Location loc) {
		if (!Util.isValidLoc(loc)) {
			return null;
		}

		for (BrewingStandPrivada br : brewing.values()) {
			if (br != null && br.location.equals(loc)) {
				return br;
			}
		}

		return null;
	}

	public static boolean isBrewingStand(final Location loc) {
		return getBrewingStand(loc) != null;
	}

	public static void removeBrewing(final Player pl) {
		if (pl == null || pl.getUniqueId() == null) {
			return;
		}

		BrewingStandPrivada br = brewing.get(pl.getUniqueId());
		if (br != null) {
			br.removeBrewing();
		}
	}

	public static Map<UUID, BrewingStandPrivada> getMap() {
		return brewing;
	}
}
