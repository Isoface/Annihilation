package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class FacingObject {
	private final BlockFace facing;
	private final Loc location;

	//
	public FacingObject(BlockFace facingDirection, Loc location) {
		facing = facingDirection;
		this.location = location;
	}

	public BlockFace getFacingDirection() {
		return facing;
	}

	public Loc getLocation() {
		return location;
	}

	public int saveToConfig(ConfigurationSection configSection) {
		int save = 0;
		//
		if (configSection != null) {
			if (facing != null)
				save += Util.setUpdatedIfNotEqual(configSection, "FacingDirection", facing.name());
			//
			if (location != null)
				save += location.saveToConfig(configSection.createSection("Location"));
		}
		//
		return save;
	}

	public static FacingObject loadFromConfig(ConfigurationSection configSection) {
		if (configSection != null) {
			Loc loc = new Loc(configSection.getConfigurationSection("Location"));
			BlockFace facing = null;
			//
			if (configSection.isString("FacingDirection")) {
				facing = BlockFace.valueOf(configSection.getString("FacingDirection"));
			}
			//
			return new FacingObject(facing, loc);
		}
		return null;
	}
	
	public static FacingObject loadFromConfig(ConfigurationSection configSection, String worldName) {
		if (configSection != null) {
			Loc loc = new Loc(configSection.getConfigurationSection("Location")).setWorld(worldName);
			loc.setWorld(worldName);
			BlockFace facing = null;
			//
			if (configSection.isString("FacingDirection")) {
				facing = BlockFace.valueOf(configSection.getString("FacingDirection"));
			}
			//
			return new FacingObject(facing, loc);
		}
		return null;
	}
}
