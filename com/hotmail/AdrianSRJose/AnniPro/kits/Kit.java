package com.hotmail.AdrianSRJose.AnniPro.kits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public abstract class Kit implements Listener, Comparable<Kit> {

	public static final Kit CivilianInstance;
	private static final Map<String, Kit> kits;
	static {
		kits = new TreeMap<String, Kit>();
		CivilianInstance = new CivilianKit();
		registerKit(CivilianInstance);
	}

	static void registerKit(Kit kit) {
		kits.put(kit.getName().toLowerCase(), kit);
	}

	public static boolean existsKit(Kit kit) {
		if (kits == null) {
			return false;
		}

		if (kit == null || kit.getName() == null) {
			return false;
		}

		if (getKit(kit.getName()) != null 
				&& getKit(kit.getName()).equals(kit)) {
			return true;
		}
		return false;
	}

	public static Collection<Kit> getKits() {
		return Collections.unmodifiableCollection(kits.values());
	}

	public static List<Kit> getKitList() {
		List<Kit> tor = new ArrayList<Kit>();
		for (Kit k : getKits()) {
			tor.add(k);
		}
		return tor;
	}

	public static int LoadedKits() {
		return getKits().size();
	}

	public static Kit getKit(String name) {
		return kits.get(Util.remC(Util.wc(name)).toLowerCase());
	}

	protected final ChatColor aqua         = ChatColor.AQUA;
	protected final ChatColor red          = ChatColor.RED;
	protected final ChatColor blue         = ChatColor.BLUE;
	protected final ChatColor gray         = ChatColor.GRAY;
	protected final ChatColor bold         = ChatColor.BOLD;
	protected final ChatColor gold         = ChatColor.GOLD;
	protected final ChatColor white        = ChatColor.WHITE;
	protected final ChatColor yellow       = ChatColor.YELLOW;
	protected final ChatColor green        = ChatColor.GREEN;
	protected final ChatColor light_purple = ChatColor.LIGHT_PURPLE;
	protected final ChatColor reset        = ChatColor.RESET;
	public abstract boolean Initialize();
	public abstract String getDisplayName();
	public abstract String getOfficialName();
	public abstract IconPackage getIconPackage();
	public abstract boolean hasPermission(Player player);
	public abstract void onPlayerSpawn(Player player);
	public abstract void cleanup(Player player);
	public abstract boolean onItemClick(Inventory inv, AnniPlayer ap);
	
	public String getName() {
		return Util.remC(Util.wc(getDisplayName()));
	}
	
	public String getColoredName() {
		return Util.wc(getDisplayName());
	}

	public void cleanup(AnniPlayer ap) {
		if (ap != null && ap.isOnline()) {
			cleanup(ap.getPlayer());
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Kit kit) {
		return this.getName().compareTo(kit.getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Kit other = (Kit) obj;
		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}
}
