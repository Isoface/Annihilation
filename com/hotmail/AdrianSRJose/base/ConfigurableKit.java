package com.hotmail.AdrianSRJose.base;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.KitChangeEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Nexus;
import com.hotmail.AdrianSRJose.AnniPro.kits.IconPackage;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public abstract class ConfigurableKit extends Kit {
	private String name;
	private ItemStack icon;
	private String[] kitDescription;
	protected KitConfig instance;
	private Loadout loadout;
	private boolean isFree;

	@Override
	public boolean Initialize() {
		// TODO------Change all the class instances to use this one instance instead of
		// KitConfig.getInstance()
		instance = KitConfig.getInstance();
		int x = 0;
		ConfigurationSection sec = instance.getKitSection(getInternalName());
		if (sec == null) {
			sec = instance.createKitSection(getInternalName());
			x++;
		}

		// Get Description
		List<String> description = new ArrayList<String>();
		for (String line : getDefaultDescription()) {
			if (line != null) {
				description.add(line.replace("§", "&"));
			}
		}
		
		// Get default Icon
		icon    = getDefaultIcon();

		// Save Defaults
		x += Util.setDefaultIfNotSet(sec, "Name", getInternalName());
		x += Util.setDefaultIfNotSet(sec, "Kit Description", description);
		x += Util.setDefaultIfNotSet(sec, "Icon", icon.getType().name());
		x += Util.setDefaultIfNotSet(sec, "Disable", false);
		x += Util.setDefaultIfNotSet(sec, "Free", false);
		x += setDefaults(sec);

		if (x > 0) {
			instance.saveConfig();
		}

		isFree = sec.getBoolean("Free");
		if (sec.getBoolean("Disable")) {
			return false;
		}

		// Load from Config
		loadKitStuff(sec);
		loadFromConfig(sec);

		// Register Perimission
		Permission perm = new Permission("Anni.Kits." + ChatColor.stripColor(getName()).toLowerCase());
		boolean register = true;
		for (Permission another : Bukkit.getPluginManager().getPermissions()) {
			if (another != null && another.getName() != null && another.getName().equalsIgnoreCase(perm.getName())) {
				register = false;
			}
		}

		if (register) {
			perm.setDefault(PermissionDefault.FALSE);
			Bukkit.getPluginManager().addPermission(perm);
			perm.recalculatePermissibles();
		}

		// SetUp
		setUp();
		loadout = getFinalLoadout().addNavCompass().finalizeLoadout();
		return true;
	}
	
	protected void loadKitStuff(ConfigurationSection section) {
		// Loading Name
		name = section.getString("Name");
		
		// Loading Description
		List<String> realDes = new ArrayList<String>();
		for (String line : section.getStringList("Kit Description")) {
			realDes.add(Util.wc(line));
		}
		
		kitDescription = getArrayFromList(realDes);
		
		// Loading Icon
		icon = instance.getItemStack(section.getString("Icon"), icon.getType());
	}

	@Override
	public IconPackage getIconPackage() {
		return new IconPackage(icon, kitDescription);
	}

	protected abstract void setUp();
	protected abstract String getInternalName();
	protected abstract ItemStack getDefaultIcon();
	protected abstract int setDefaults(ConfigurationSection section);
	protected abstract void loadFromConfig(ConfigurationSection section);
	protected abstract List<String> getDefaultDescription();
	protected abstract Loadout getFinalLoadout();
	protected abstract void onPlayerRespawn(final Player p, AnniPlayer ap);

	protected void addToList(List<String> list, String... strings) {
		for (String str : strings) {
			list.add(str);
		}
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasPermission(final Player player) {
		if (player == null) {
			return false;
		}

		final AnniPlayer p = AnniPlayer.getPlayer(player);
		if (player.isOp()) {
			return true;
		}

		if (p != null) {
			final Object gg = p.getData("clanperm.kits.keys");
			if (gg != null && gg instanceof List) {
				final List<String> kits = (List<String>) gg;
				if (kits != null && !kits.isEmpty()) {
					if (kits.contains(ChatColor.stripColor(getName()).toLowerCase())) {
						return true;
					}
				}
			}
		}
		
		if (instance.useAllKits() || isFree) {
			return true;
		}

		if (player.hasPermission("Anni.Kits.*") || player.hasPermission("Anni.Kits." + ChatColor.stripColor(getName()))) {
			return true;
		}

		if (p != null) {
			Object obj = p.getData("Kits");
			if (obj != null && obj instanceof List) {
				List<String> l = (List<String>) obj;
				if (l.contains(getName().toLowerCase())) {
					return true;
				}
			}
			
			Object obj2 = p.getData("Vault-Kits");
			if (obj2 != null && obj2 instanceof List) {
				List<String> l = (List<String>) obj2;
				if (l.contains(getName().toLowerCase())) {
					return true;
				}
			}
		}

		return false;
	}

	private String[] getArrayFromList(List<String> list) {
		String[] r = new String[list.size()];
		for (int x = 0; x < list.size(); x++)
			r[x] = list.get(x);
		return r;
	}

	@Override
	public void onPlayerSpawn(Player player) {
		if (player != null) {
			loadout.giveLoadout(player);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onRespawn(final PlayerRespawnEvent eve) {
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		if (Game.isGameRunning() && ap.getTeam() != null && ap.getKit().equals(this)) {
			Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					AnnihilationMain.INSTANCE.getServer().getScheduler().runTask(AnnihilationMain.INSTANCE,
							new Runnable() {
						@Override
						public void run() {
							if (p != null && ap != null) {
								onPlayerRespawn(p, ap);
							}
						}
					});
				}
			}, 40);
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onChangeKit(KitChangeEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}
		
		if (!KitConfig.getInstance().killOnChangetAwayNexus()) {
			return;
		}
		
		final AnniPlayer ap = eve.getPlayer();
		if (ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}
		
		final Player p = ap.getPlayer();
		final Nexus n = ap.getTeam().getNexus();
		final Location ploc = p.getLocation();
		if (!Util.isValidLoc(ploc)) {
			return;
		}
		
		if (n == null || !Util.isValidLoc(n.getLocation())) {
			return;
		}
		
		final Location nexus = n.getLocation().toLocation();
		if (nexus.getWorld().getName() == null) {
			return;
		}

		if (nexus.getWorld().getName().equals(ploc.getWorld().getName())) {
			final double distance = nexus.distance(ploc);
			if (distance >= KitConfig.getInstance().getDistanceToKillOnAwayNexus()) {//nexus.distance(ploc) >= KitConfig.getInstance().getDistanceToKillOnAwayNexus()
				p.setHealth(0.0D);
			}
		} else {
			p.setHealth(0.0D);
		}
	}

	public void addLoadoutToInventory(final Inventory inv) {
		if (this.getFinalLoadout().getFinalStacks() != null) {
			inv.addItem(this.getFinalLoadout().getFinalStacks());
		} else {
			for (ItemStack all : this.getFinalLoadout().ToStacks()) {
				inv.addItem(all);
			}
		}
		
		// Add Armor
		if (this.getFinalLoadout().getArmor() != null) {
			for (ItemStack armor : this.getFinalLoadout().getArmor()) {
				if (armor != null) {
					inv.addItem(armor);
				}
			}
		}
	}

	public boolean hasThisKit(final Player p) {
		return hasThisKit(AnniPlayer.getPlayer(p));
	}

	public boolean hasThisKit(final AnniPlayer ap) {
		return ap != null && this.equals(ap.getKit());
	}

	@Override
	public String getOfficialName() {
		return this.getClass().getCanonicalName().replace("com.hotmail.AdrianSRJose.Kits.", "");
	}
}
