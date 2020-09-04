package com.hotmail.AdrianSRJose.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class KitConfig {
	private static KitConfig instance;

	public static KitConfig getInstance() {
		if (instance == null) {
			instance = new KitConfig();
		}

		return instance;
	}

	//
	private YamlConfiguration kitConfig;
	private boolean useAllKits;
	private boolean killOnChangeAway;
	private int distanceToKillOnAway = 64;
	private String readyPrefix = ChatColor.GREEN + " READY"; 
	File cFile = null;

	private KitConfig() {
		// check and load file
		File f = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath(), "StarterKitsConfig.yml");
		try {
			if (f == null || !f.exists()) {
				f = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath(), "AnniKitsConfig.yml");
				//
				if (!f.exists())
					f.createNewFile();
			}
			
			cFile = f;
			kitConfig = YamlConfiguration.loadConfiguration(cFile);
			
			// save defaults
			int save = 0;
			save += Util.createSectionIfNoExitsInt(kitConfig, "StarterKits");
			final ConfigurationSection kitsSecion = Util.createSectionIfNoExits(kitConfig, "StarterKits");
			save += Util.setDefaultIfNotSet(kitsSecion, "EnableAllKits", true);
			save += Util.setDefaultIfNotSet(kitsSecion, "KillOnChangeKitAwayNexus", true);
			save += Util.setDefaultIfNotSet(kitsSecion, "DistanceToKillOnAwayNexus", 64);
			save += Util.setDefaultIfNotSet(kitsSecion, "ReadyPrefix", Util.untranslateAlternateColorCodes(readyPrefix));
			if (save > 0) {
				kitConfig.save(f);
			}
			
			// load config
			final ConfigurationSection section = Util.createSectionIfNoExits(kitConfig, "StarterKits");
			useAllKits           = section.getBoolean("EnableAllKits");
			killOnChangeAway     = section.getBoolean("KillOnChangeKitAwayNexus");
			distanceToKillOnAway = section.getInt("DistanceToKillOnAwayNexus", 64);
			readyPrefix          = Util.wc(section.getString("ReadyPrefix", readyPrefix));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player getPlayerInSight(Player player, int distance) {
		Location playerLoc = player.getLocation();
		Vector3D playerDirection = new Vector3D(playerLoc.getDirection());
		Vector3D start = new Vector3D(playerLoc);
		Vector3D end = start.add(playerDirection.multiply(distance));
		Player inSight = null;
		for (Entity nearbyEntity : player.getNearbyEntities(distance, distance, distance)) {
			if (nearbyEntity instanceof Player) {
				Vector3D nearbyLoc = new Vector3D(nearbyEntity.getLocation());

				// Bounding box
				Vector3D min = nearbyLoc.subtract(0.5D, 1.6D, 0.5D);
				Vector3D max = nearbyLoc.add(0.5D, 0.3D, 0.5D);

				if (hasIntersection(start, end, min, max)) {
					if (inSight == null || inSight.getLocation().distanceSquared(playerLoc) > nearbyEntity.getLocation()
							.distanceSquared(playerLoc)) {
						inSight = (Player) nearbyEntity;
						return inSight;
					}
				}
			}
		}
		return inSight;
	}

	public LivingEntity getLivingEntityInSight(Player player, int distance) {
		Location playerLoc = player.getLocation();
		Vector3D playerDirection = new Vector3D(playerLoc.getDirection());
		Vector3D start = new Vector3D(playerLoc);
		Vector3D end = start.add(playerDirection.multiply(distance));
		LivingEntity inSight = null;
		for (Entity nearbyEntity : player.getNearbyEntities(distance, distance, distance)) {
			if (nearbyEntity instanceof LivingEntity) {
				Vector3D nearbyLoc = new Vector3D(nearbyEntity.getLocation());

				// Bounding box
				Vector3D min = nearbyLoc.subtract(0.5D, 1.6D, 0.5D);
				Vector3D max = nearbyLoc.add(0.5D, 0.3D, 0.5D);

				if (hasIntersection(start, end, min, max)) {
					if (inSight == null || inSight.getLocation().distanceSquared(playerLoc) > nearbyEntity.getLocation()
							.distanceSquared(playerLoc)) {
						inSight = (LivingEntity) nearbyEntity;
						return inSight;
					}
				}
			}
		}
		return inSight;
	}

	public Vector3D getLivingEntityInSightVector(Player player, int distance) {
		Location playerLoc = player.getLocation();
		Vector3D playerDirection = new Vector3D(playerLoc.getDirection());
		Vector3D start = new Vector3D(playerLoc);
		Vector3D end = start.add(playerDirection.multiply(distance));
		LivingEntity inSight = null;
		Vector3D tor = null;
		for (Entity nearbyEntity : player.getNearbyEntities(distance, distance, distance)) {
			if (nearbyEntity instanceof LivingEntity) {
				Vector3D nearbyLoc = new Vector3D(nearbyEntity.getLocation());

				// Bounding box
				Vector3D min = nearbyLoc.subtract(0.5D, 1.6D, 0.5D);
				Vector3D max = nearbyLoc.add(0.5D, 0.3D, 0.5D);

				if (hasIntersection(start, end, min, max)) {
					if (inSight == null || inSight.getLocation().distanceSquared(playerLoc) > nearbyEntity.getLocation()
							.distanceSquared(playerLoc)) {
						inSight = (LivingEntity) nearbyEntity;
						tor = max;
						return tor;
					}
				}
			}
		}
		return tor;
	}

	/**
	 * Get nearby entities:
	 * <ul>
	 * <li>(double radius is need only in old bukkit versions: 1.7 - 1.8)
	 * <li>If you use only a Bukkit version > to 1.8 you set the "range" 0.
	 * <ul>
	 * Method:
	 * <ul>
	 * <ul>
	 * <ul>
	 * <ul>
	 * public static Collection<Entity> getNearbyEntities(Location loc, double x,
	 * double y, double z, double radius)
	 * <ul>
	 * {
	 * <ul>
	 * if (VersionUtils.isNewSpigotVersion())
	 * <ul>
	 * return loc.getWorld().getNearbyEntities(loc, x, y, z);
	 * <ul>
	 * else
	 * <ul>
	 * return getNearbyEntities(loc, radius);
	 * <ul>
	 * }
	 * </ul>
	 */
	public static Collection<Entity> getNearbyEntities(Location loc, double x, double y, double z, double radius) {
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1))
			return loc.getWorld().getNearbyEntities(loc, x, y, z);
		else
			return getNearbyEntities(loc, radius);
	}
	
	public static Collection<Player> getNearbyPlayers(Location loc, double x, double y, double z, double radius) {
		if (loc != null) {
			final Collection<Entity> ents = getNearbyEntities(loc, x, y, z, radius);
			final List<Player> tor        = new ArrayList<Player>();
			for (Entity ent : ents) {
				if (ent instanceof Player) {
					tor.add(Bukkit.getPlayer(ent.getUniqueId()));
				}
			}
			return tor;
		}
		return new ArrayList<>();
	}

	/**
	 * Normal metod
	 */
	public static Collection<Entity> getNearbyEntities(Location loc, double range) {
		List<Entity> ents = new ArrayList<Entity>();

		for (Entity ent : loc.getWorld().getEntities()) {
			if (ent.getLocation().distance(loc) <= range) {
				if (!ents.contains(ent))
					ents.add(ent);
			} else
				ents.remove(ent);
		}
		return ents;
	}

	private boolean hasIntersection(Vector3D start, Vector3D end, Vector3D min, Vector3D max) {
		final double epsilon = 0.0001f;

		Vector3D d = end.subtract(start).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = start.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();

		if (Math.abs(c.getX()) > e.getX() + ad.getX()) {
			return false;
		}

		if (Math.abs(c.getY()) > e.getY() + ad.getY()) {
			return false;
		}

		if (Math.abs(c.getZ()) > e.getX() + ad.getZ()) {
			return false;
		}

		if (Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY()
				+ epsilon) {
			return false;
		}

		if (Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ()
				+ epsilon) {
			return false;
		}

		if (Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX()
				+ epsilon) {
			return false;
		}

		return true;
	}

	public boolean isLeftAction(Action c) {
		return c.equals(Action.LEFT_CLICK_AIR) || c.equals(Action.LEFT_CLICK_BLOCK);
	}

	public boolean isRightAction(Action c) {
		return c.equals(Action.RIGHT_CLICK_AIR) || c.equals(Action.RIGHT_CLICK_BLOCK);
	}
	
	public ItemStack getItemStack(String name, Material defType) {
		if (name != null && !name.isEmpty()) {
			try {
				Material mat = Material.valueOf(name);
				if (mat != null) {
					return new ItemStack(mat, 1);
				}
			}
			catch(Throwable t) {
			}
		}
		return new ItemStack(defType, 1);
	}

	public boolean useAllKits() {
		return useAllKits;
	}

	public boolean killOnChangetAwayNexus() {
		return killOnChangeAway;
	}

	public int getDistanceToKillOnAwayNexus() {
		return distanceToKillOnAway;
	}
	
	public String getReadyPrefix() {
		return readyPrefix;
	}

	public void saveConfig() {
		try {
			kitConfig.save(cFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ConfigurationSection createKitSection(String kitname) {
		return kitConfig.getConfigurationSection("StarterKits").createSection(kitname);
	}

	public ConfigurationSection getKitSection(String kit) {
		try {
			return kitConfig.getConfigurationSection("StarterKits").getConfigurationSection(kit);
		} catch (Exception e) {
			return null;
		}
	}
}
