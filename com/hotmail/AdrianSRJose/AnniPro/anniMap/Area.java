package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class Area {
	private boolean allowPVP = true;
	private boolean allowDamage = true;
	private boolean allowHunger = true;
	private Loc corner1;
	private Loc corner2;
	private String name;

	public Area(Loc one, Loc two, String name) {
		corner1 = one;
		corner2 = two;
		this.name = name;
	}

	public Area(ConfigurationSection section) {
		assert section != null;
		name = section.getString("Name");
		corner1 = new Loc(section.getConfigurationSection("Corner1"));
		corner2 = new Loc(section.getConfigurationSection("Corner2"));
		if (section.isSet("AllowPVP"))
			this.setAllowPVP(section.getBoolean("AllowPVP"));
		if (section.isSet("AllowHunger"))
			this.setAllowHunger(section.getBoolean("AllowHunger"));
		if (section.isSet("AllowDamage"))
			this.setAllowDamage(section.getBoolean("AllowDamage"));
	}

	public String getName() {
		return name;
	}

	public void setAllowPVP(boolean allowPvp) {
		allowPVP = allowPvp;
	}

	public void setAllowDamage(boolean allowDamage) {
		this.allowDamage = allowDamage;
	}

	public void setAllowHunger(boolean allowHunger) {
		this.allowHunger = allowHunger;
	}

	public boolean getAllowPVP() {
		return allowPVP;
	}

	public boolean getAllowDamage() {
		return allowDamage;
	}

	public boolean getAllowHunger() {
		return allowHunger;
	}

	public Loc getCorner1() {
		return corner1;
	}

	public Loc getCorner2() {
		return corner2;
	}

	// save += Util.setUpdatedIfNotEqual(section, "patch", value);
	public int saveToConfig(ConfigurationSection section) {
		int save = 0;
		//
		if (getName() != null)
			save += Util.setUpdatedIfNotEqual(section, "Name", getName());
		//
		save += Util.createSectionIfNoExitsInt(section, "Corner2");
		save += Util.createSectionIfNoExitsInt(section, "Corner1");
		//
		save += corner1.saveToConfig(section.getConfigurationSection("Corner1"));
		save += corner2.saveToConfig(section.getConfigurationSection("Corner2"));
		//
		save += Util.setUpdatedIfNotEqual(section, "AllowPVP", getAllowPVP());
		save += Util.setUpdatedIfNotEqual(section, "AllowHunger", getAllowHunger());
		save += Util.setUpdatedIfNotEqual(section, "AllowDamage", getAllowDamage());
		//
		return save > 0 ? 1 : 0;
	}
	
	public boolean isInArea(Area a) {
		if (a == null || a.corner1 == null || a.corner2 == null) {
			return false;
		}
		
		return isInArea(a.corner1) && isInArea(a.corner2);
	}

	public boolean isInArea(Loc loc) {
		if (!corner1.getWorld().equals(loc.getWorld())) {
			return false;
		}

		return fallsBetween(corner1.getBlockX(), corner2.getBlockX(), loc.getBlockX())
			&& fallsBetween(corner1.getBlockY(), corner2.getBlockY(), loc.getBlockY())
			&& fallsBetween(corner1.getBlockZ(), corner2.getBlockZ(), loc.getBlockZ());
	}

	private boolean fallsBetween(int one, int two, int num) {
		int min, max;
		if (one < two) {
			min = one;
			max = two;
		} else {
			min = two;
			max = one;
		}

		return num >= min && num <= max;
	}
}
