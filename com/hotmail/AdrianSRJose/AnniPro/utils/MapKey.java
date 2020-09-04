package com.hotmail.AdrianSRJose.AnniPro.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MapKey implements Comparable<MapKey> {
	public static MapKey getKey(Location location) {
		try {
			return location != null ? new MapKey(location.getBlockX() + " " + location.getBlockY() + " "
					+ location.getBlockZ() + " " + location.getWorld().getName()) : null;
		} catch (NullPointerException e) {
			if (location == null) {
				Bukkit.getConsoleSender().sendMessage("§cInvalid Location");
			} else {
				if (location.getWorld() == null) {
					Bukkit.getConsoleSender().sendMessage("§cInvalid World");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "§nError: 'Map no Found' ");
					Bukkit.getConsoleSender().sendMessage("__________________________");
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "§nMap Address No Found.");
					Bukkit.getConsoleSender().sendMessage("__________________________");
				} else if (location.getWorld().getName() == null) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "§nError: 'Map Name no Found' ");
					Bukkit.getConsoleSender().sendMessage("__________________________");
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "§nMap Name Address No Found.");
					Bukkit.getConsoleSender().sendMessage("__________________________");
				}
			}
			return null;
		}
	}

	public static MapKey getKey(Loc location) {
		return location != null ? getKey(location.toLocation()) : null;
	}

	private String key;

	public MapKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapKey other = (MapKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public int compareTo(MapKey o) {
		return key.compareTo(o.key);
	}

	public String getStringKey() {
		return key;
	}

	@Override
	public String toString() {
		return key;
	}
}
