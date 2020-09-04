package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class BossMap extends AnniMap implements Listener {
	
	private final List<Loc> spawns = new ArrayList<Loc>();
	private final Random    random = new Random(System.currentTimeMillis());;

	public BossMap(String worldName, File mapDirectory, boolean loadOnCostructor) {
		super(worldName, new File(mapDirectory, "AnniBossMapConfig.yml"), loadOnCostructor);
	}

	public BossMap(File configFile, boolean loadOnCostructor) {
		super(null, configFile, loadOnCostructor);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		spawns.clear ( );
		if (section != null && section.isConfigurationSection("BossConfiguration")) {
			/**/
			ConfigurationSection Boss = section.getConfigurationSection("BossConfiguration");
			/**/
			if (Boss != null) {
				GameBoss.setAnniBoss(new AnniBoss(Boss));
				/**/
				if (section.isString("WorldName"))
					setWorldName(section.getString("WorldName"));
				/**/
				ConfigurationSection spawns = Boss.getConfigurationSection("Spawns");
				if (spawns != null) {
					for (String key : spawns.getKeys(false)) {
						if (key != null) {
							Loc loc = new Loc(spawns.getConfigurationSection(key));
							if (loc != null)
								this.spawns.add(loc);
						}
					}
				}
			}
		}
	}

	@Override
	protected int saveToConfig(ConfigurationSection section) {
		int save = 0;
		/**/
		if (section != null) {
			save += Util.createSectionIfNoExitsInt(section, "BossConfiguration");
			ConfigurationSection boss = section.getConfigurationSection("BossConfiguration");
			/**/
			save += Util.createSectionIfNoExitsInt(boss, "Spawns");
			ConfigurationSection spawnSection = boss.createSection("Spawns");
			/**/
			List<Loc> spawns = getSpawnList();
			if (spawns != null && !spawns.isEmpty()) {
				for (int x = 0; x < spawns.size(); x++) {
					save += spawns.get(x).saveToConfig(spawnSection.createSection(x + ""));
				}
			}
		}
		/**/
		return save;
	}

	public void unLoadMap() {
		World tpworld = Game.LobbyMap != null ? Game.LobbyMap.getWorld() : null;
		if (tpworld == null) {
			tpworld = Bukkit.getWorlds().size() > 0 ? Bukkit.getWorlds().get(0) : null;
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null && p.isOnline()) {
				if (p.getWorld().getName().equals(getWorldName())) {
					if (tpworld != null)
						p.teleport(tpworld.getSpawnLocation());
					else
						p.kickPlayer("Unloading the world and we dont want you to get trapped or glitched!");
				}
			}
		}

		this.unregisterListeners();
		boolean b = Bukkit.unloadWorld(super.getWorldName(), false);
		Bukkit.getLogger().info("[Annihilation] " + super.getNiceWorldName() + " was unloaded successfully: " + b);
	}

	@Override
	public void registerListeners(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void unregisterListeners() {
		HandlerList.unregisterAll(this);
	}

	public void addSpawn(Loc loc) {
		if (loc != null && loc.toLocation() != null)
			spawns.add(loc);
	}

	public void addSpawn(Location loc, boolean presice) {
		if (loc != null)
			spawns.add(new Loc(loc, presice));
	}

	public void removeSpawn(int index) {
		if (spawns != null)
			spawns.remove(index);
	}

	public Location getRandomSpawn ( ) {
		if ( spawns.isEmpty ( ) ) {
			Util.print ( ChatColor.RED , "NO SPAWNS SET FOR BOSS MAP" );
			return null;
		} else { 
			return spawns.get ( random.nextInt ( spawns.size ( ) ) ).toLocation ( );
		}
//		if (!spawns.isEmpty() && spawns != null)
//			return spawns.get(rand.nextInt(spawns.size())).toLocation();
//		else {
//			Util.print(ChatColor.RED, "NO SPAWNS SET IN BOSS MAP");
//			return null;
//		}
	}

//	public Location getSecureRandomSpawn() {
//		// Create new list
//		final List<Loc> secureLocs = new ArrayList<Loc>();
//		for (Loc s : spawns) {
//			// Check is not null
//			if (!Util.isValidLoc(s)) {
//				continue;
//			}
//
//			// Add
//			secureLocs.add(s);
//		}
//
//		// Return secure
//		if (!secureLocs.isEmpty()) {
//			return secureLocs.get(rand.nextInt(secureLocs.size())).toLocation();
//		}
//
//		// Returns null and throw message if is null
//		Util.print(ChatColor.RED, "NO SPAWNS SET IN BOSS MAP");
//		return null;
//	}

	public List<Loc> getSpawnList() {
		try {
			return Collections.unmodifiableList(spawns);
		} catch (NullPointerException e) {
			Util.print(ChatColor.YELLOW, "No random spawns seted");
			return null;
		}
	}

	public void backUpWorld() {
		super.getWorld().save();
	}

	public void backupConfig() {
		super.saveToConfig(false, false);
	}

	public void saveTheConfig() {
		this.saveConfig();
		super.saveToConfig(false, false);

		if (getWorld() != null)
			this.getWorld().save();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUseSkiJump(PlayerMoveEvent eve) {
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		/**/
		if (ap != null) {
			Block down = eve.getTo().getBlock().getRelative(BlockFace.SELF);
			/**/
			if (down != null) {
				if (!eve.getFrom().getBlock().getRelative(BlockFace.SELF).getLocation().equals(down.getLocation())) {
					if (down.getType() == Material.IRON_PLATE) {
						p.setVelocity(eve.getTo().getDirection().multiply(4));
						KitUtils.addOneFallImmunity(ap);
						p.playSound(p.getLocation(), UniversalSound.WITHER_SHOOT.asBukkit(), 4.0F, 2.0F);
					}
				}
			}
		}
	}
	/**/
}
