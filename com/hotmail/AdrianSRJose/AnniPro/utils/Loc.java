package com.hotmail.AdrianSRJose.AnniPro.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public final class Loc implements Cloneable {
	private Number x, y, z;
	private float pitch, yaw;
	private String world;

	public Loc(Location loc, boolean precise) {
		world = loc == null ? null : (loc.getWorld() != null ? loc.getWorld().getName() : null);
		if (precise) {
			x = loc.getX();
			y = loc.getY();
			z = loc.getZ();
			pitch = loc.getPitch();
			yaw = loc.getYaw();
		} else {
			x = loc.getBlockX();
			y = loc.getBlockY();
			z = loc.getBlockZ();
			pitch = 0;
			yaw = 0;
		}
	}

	public Loc(Location loc) {
		this(loc, false);
	}

	public Loc(String world, Number x, Number y, Number z) {
		this(world, x, y, z, 0, 0);
	}

	public Loc(String world, Number x, Number y, Number z, float pitch, float yaw) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public Loc(ConfigurationSection section) {
		assert section != null : "Can't load a Loc";
		//
		world = section.getString("World");
		world = Util.getVerifyUniversalPCAddres(world);
		pitch = (float) section.getDouble("Pitch");
		yaw = (float) section.getDouble("Yaw");
		if (section.isDouble("X"))
			x = section.getDouble("X");
		else
			x = section.getInt("X");
		if (section.isDouble("Y"))
			y = section.getDouble("Y");
		else
			y = section.getInt("Y");
		if (section.isDouble("Z"))
			z = section.getDouble("Z");
		else
			z = section.getInt("Z");
	}

	public int getBlockX() {
		return x.intValue();
	}

	public int getBlockY() {
		return y.intValue();
	}

	public int getBlockZ() {
		return z.intValue();
	}

	public String getWorld() {
		return world;
	}

	public World getBukkitWorld() {
		return world != null ? Bukkit.getWorld(world) : null;
	}

	public Location toLocation() {
		if (world == null || x == null || y == null || z == null)
			return null;
		return new Location(getBukkitWorld(), x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
	}

	public void setWorld(final World w) {
		if (w != null && w.getName() != null) {
			this.world = w.getName();
		}
	}

	public Loc setWorld(final String name) {
		this.world = name;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof Loc) {
			Loc l = (Loc) obj;
			return world.equals(l.world) && this.getBlockX() == l.getBlockX() && this.getBlockY() == l.getBlockY()
					&& this.getBlockZ() == l.getBlockZ();
		} else if (obj instanceof Location) {
			Location l = (Location) obj;
			return world.equals(l.getWorld().getName()) && this.getBlockX() == l.getBlockX()
					&& this.getBlockY() == l.getBlockY() && this.getBlockZ() == l.getBlockZ();
		}

		return false;
	}

	// save += Util.setUpdatedIfNotEqual(section, "patch", value);
	public int saveToConfig(ConfigurationSection section) {
		int save = 0;
		save += Util.setUpdatedIfNotEqual(section, "World", world);
		save += Util.setUpdatedIfNotEqual(section, "X", x.doubleValue());
		save += Util.setUpdatedIfNotEqual(section, "Y", y.doubleValue());
		save += Util.setUpdatedIfNotEqual(section, "Z", z.doubleValue());
		save += Util.setUpdatedIfNotEqual(section, "Pitch", (double) pitch);
		save += Util.setUpdatedIfNotEqual(section, "Yaw", (double) yaw);
		return save > 0 ? 1 : 0;
	}

	public Loc clone() {
		try {
			return (Loc) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	@Override
	public String toString() {
		return "World: " + this.getWorld() + " X:" + x + " Y:" + y + " Z:" + z;
	}
}