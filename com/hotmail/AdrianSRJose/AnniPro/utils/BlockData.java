package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class BlockData {
	private Location blockLocation;
	private final Map<String, Object> data = new HashMap<String, Object>();

	//
	public BlockData(Location blockLocation) {
		this.blockLocation = blockLocation;
	}

	public void setLocation(Location newLocation) {
		blockLocation = newLocation;
	}

	public void setData(String key, Object value) {
		if (blockLocation != null && key != null)
			data.put(key, value);
	}

	public Object getData(String key) {
		return blockLocation != null && key != null ? data.get(key) : null;
	}

	public Location getLocation() {
		return blockLocation;
	}

}
