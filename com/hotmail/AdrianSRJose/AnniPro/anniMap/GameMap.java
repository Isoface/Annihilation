package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.Furnace;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameStartEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.TeamBeacon;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.MapKey;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.voting.AutoRestarter;

import lombok.Getter;
import lombok.Setter;

public final class GameMap extends AnniMap implements Listener {
	
	public RegeneratingBlocks blocks;
	private List<Loc> diamondLocs;
	private List<Loc> PortalLocs;
	private List<Loc> FurnaceLocs;
	private List<Loc> BrewingLocs;
	private @Getter @Setter Loc beaconLoc;
	public Map<MapKey, FacingObject> enderFurnaces;
	public Map<MapKey, FacingObject> brewingStands;
	private static Map<Material, UnplaceableBlock> unplaceableBlocks;
	private int currentphase;
	private int PhaseTime;
	private boolean canDamageNexus;
	private int damageMultiplier;
	private AutoRestarter restarter = null;
	private int witchRespawnTime;
	private TimeUnit witchRespawnUnit;

	public static GameMap instance;

	public GameMap(String worldName, File mapDirectory, boolean loadOnCostructor) {
		super(worldName, new File(mapDirectory, "AnniMapConfig.yml"), loadOnCostructor);
		instance = this;
		if (Config.USE_AUTO_RESTART.toBoolean()) {
			restarter = new AutoRestarter(AnnihilationMain.INSTANCE, Config.AUTO_RESTART_PLAYERS_TO_AUTO_RESTART.toInt(),
					Config.AUTO_RESTART_COUNTDOWN_TIME.toInt());
		}
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		blocks = new RegeneratingBlocks(this.getWorldName(), section.getConfigurationSection("RegeneratingBlocks"));
		unplaceableBlocks = new EnumMap<Material, UnplaceableBlock>(Material.class);
		diamondLocs = new ArrayList<Loc>();
		PortalLocs = new ArrayList<Loc>();
		FurnaceLocs = new ArrayList<Loc>();
		BrewingLocs = new ArrayList<Loc>();
		enderFurnaces = new HashMap<MapKey, FacingObject>();
		brewingStands = new HashMap<MapKey, FacingObject>();
		currentphase = 0;
		canDamageNexus = false;
		damageMultiplier = 1;
		witchRespawnTime = 0;
		witchRespawnUnit = null;
		PhaseTime = (int) TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES);
		if (section != null) {
			// Load Phase Time
			if (section.isInt("PhaseTime")) {
				PhaseTime = section.getInt("PhaseTime");
			}
			
			// Load Magic Beacon
			ConfigurationSection beaconSec = section.getConfigurationSection("BeaconLoc");
			if (beaconSec != null) {
				beaconLoc = new Loc(beaconSec);
			}

			// Load Ender Brewings
			ConfigurationSection brewings = section.getConfigurationSection("EnderBrewings");
			if (brewings != null) {
				for (String key : brewings.getKeys(false)) {
					if (key == null) {
						continue;
					}

					FacingObject obj = FacingObject.loadFromConfig(brewings.getConfigurationSection(key));
					if (obj == null) {
						continue;
					}

					// registre
					this.addBrewingStand(obj);

					// Save locs
					Loc fLocs = new Loc(brewings.getConfigurationSection(key + ".Location"));
					if (fLocs != null) {
						BrewingLocs.add(fLocs);
					}
				}
			}

			// Load Ender Furnaces
			ConfigurationSection furnaces = section.getConfigurationSection("EnderFurnaces");
			if (furnaces != null) {
				for (String key : furnaces.getKeys(false)) {
					if (key != null) {
						FacingObject obj = FacingObject.loadFromConfig(furnaces.getConfigurationSection(key));
						if (obj != null) {
							addEnderFurnace(obj);
							//
							Loc fLocs = new Loc(furnaces.getConfigurationSection(key + ".Location"));
							if (fLocs != null)
								FurnaceLocs.add(fLocs);
						}
					}
				}
			}

			ConfigurationSection diamonds = section.getConfigurationSection("DiamondLocations");
			if (diamonds != null) {
				for (String key : diamonds.getKeys(false)) {
					if (key != null) {
						Loc loc = new Loc(diamonds.getConfigurationSection(key));
						//
						if (loc != null)
							diamondLocs.add(loc);
					}
				}
			}

			ConfigurationSection portales = section.getConfigurationSection("PortalLocations");
			if (portales != null) {
				for (String key : portales.getKeys(false)) {
					if (key != null) {
						Loc loc = new Loc(portales.getConfigurationSection(key));
						if (loc != null)
							PortalLocs.add(loc);
					}
				}
			}

			ConfigurationSection teams = section.getConfigurationSection("Teams");
			if (teams != null) {
				for (AnniTeam team : AnniTeam.Teams) {
					ConfigurationSection teamSection = teams.getConfigurationSection(team.getName() + " Team");
					if (teamSection != null) {
						ConfigurationSection nexSec = teamSection.getConfigurationSection("Nexus.Location");
						if (nexSec == null)
							team.getNexus().setLocation(null);
						else {
							Loc nexusloc = new Loc(teamSection.getConfigurationSection("Nexus.Location"));
							team.getNexus().setLocation(nexusloc);
						}
						
						// Load Beacon
						ConfigurationSection beaSec = teamSection.getConfigurationSection("Beacon");
						if (beaSec != null) {
							Loc beaLoc = new Loc(beaSec.getConfigurationSection("Location"));
							TeamBeacon beacon = new TeamBeacon(team, beaLoc);
							team.setBeacon(beacon);
							
							// Register Events in the beacon class
//							Bukkit.getPluginManager().registerEvents(beacon, AnnihilationMain.INSTANCE);
						}
						
						ConfigurationSection specSec = teamSection.getConfigurationSection("SpectatorLocation");
						if (specSec == null)
							team.setSpectatorLocation((Loc) null);
						else {
							Loc spectatorspawn = new Loc(teamSection.getConfigurationSection("SpectatorLocation"));
							team.setSpectatorLocation(spectatorspawn);
						}

						ConfigurationSection witchSec = teamSection.getConfigurationSection("WitchLocation");
						if (witchSec == null)
							team.setWitchLocation((Loc) null);
						else {
							Loc witchSpawn = new Loc(witchSec);
							team.setWitchLocation(witchSpawn);
						}

						team.clearSpawns();
						ConfigurationSection spawns = teamSection.getConfigurationSection("Spawns");
						if (spawns != null) {
							for (String key : spawns.getKeys(false)) {
								if (key != null) {
									Loc loc = new Loc(spawns.getConfigurationSection(key));
									//
									if (loc != null) {
										team.addSpawn(loc.toLocation());
									}
								}
							}
						}
					}
				}

				if (teams.getString("WitchsRespawnTime") != null)
					this.setWitchRespawnTime(
							Integer.parseInt(teams.getString("WitchsRespawnTime").replaceAll("'", "")));

				if (teams.getString("Witch-Respawn-Unit") != null)
					setWitchRespawnUnit(TimeUnit.valueOf(teams.getString("Witch-Respawn-Unit")));
			}
			ConfigurationSection unplaceableSec = section.getConfigurationSection("UnplaceableBlocks");
			if (unplaceableSec != null) {
				for (String key : unplaceableSec.getKeys(false)) {
					if (key != null) {
						ConfigurationSection matSec = unplaceableSec.getConfigurationSection(key);
						Material mat = Material.getMaterial(matSec.getString("Material"));
						List<Byte> b = matSec.getByteList("Values");
						if (b != null)
							for (Byte bt : b)
								addUnplaceableBlock(mat, bt);
					}
				}
			}
			// ---------------------------------------------------------------------------------
			ConfigurationSection playAreaSection = section.getConfigurationSection("PlayArea");
			if (playAreaSection != null) {
				getAreas().setPlayArea(new Area(playAreaSection));
			}
			// ---------------------------------------------------------------------------------
		}
	}

	@Override
	protected int saveToConfig(ConfigurationSection section) {
		int save = 0;
		//
		if (section != null) {
			// RegeneratingBlocks
			save += Util.createSectionIfNoExitsInt(section, "RegeneratingBlocks");
			save += blocks.saveToConfig(section.createSection("RegeneratingBlocks"));

			// Phase Time
			save += Util.setUpdatedIfNotEqual(section, "PhaseTime", PhaseTime);
			
			// Beacon Loc
			if (beaconLoc != null) {
				save += beaconLoc.saveToConfig(section.createSection("BeaconLoc"));
			}

			// Section Counter
			int counter = 1;

			// Ender Furnaces
			save += Util.createSectionIfNoExitsInt(section, "EnderFurnaces");
			ConfigurationSection enderFurnaces = section.getConfigurationSection("EnderFurnaces");
			for (FacingObject obj : this.enderFurnaces.values()) {
				if (obj == null) {
					continue;
				}

				save += obj.saveToConfig(enderFurnaces.createSection("" + counter));
				counter++;
			}

			// Reset counter
			counter = 1;

			// Ender Brewings
			save += Util.createSectionIfNoExitsInt(section, "EnderBrewings");
			ConfigurationSection enderBrewings = section.getConfigurationSection("EnderBrewings");
			for (FacingObject obj : brewingStands.values()) {
				if (obj == null) {
					continue;
				}

				save += obj.saveToConfig(enderBrewings.createSection("" + counter));
				counter++;
			}

			// Reset counter
			counter = 1;

			// Save Diamonds
			save += Util.createSectionIfNoExitsInt(section, "DiamondLocations");
			ConfigurationSection diamonds = section.createSection("DiamondLocations");
			//
			for (Loc loc : diamondLocs) {
				if (loc != null) {
					save += Util.createSectionIfNoExitsInt(diamonds, "" + counter);
					save += loc.saveToConfig(diamonds.getConfigurationSection("" + counter));
					counter++;
				}
			}
			// ------------------------------------------------------------------------------------------------
			counter = 1;
			//
			save += Util.createSectionIfNoExitsInt(section, "PortalLocations");
			ConfigurationSection portales = section.createSection("PortalLocations");
			//
			for (Loc loc : PortalLocs) {
				if (loc != null) {
					save += Util.createSectionIfNoExitsInt(portales, "" + counter);
					loc.saveToConfig(portales.createSection("" + counter));
					counter++;
				}
			}
			// ------------------------------------------------------------------------------------------------
			counter = 1;
			//
			save += Util.createSectionIfNoExitsInt(section, "Teams");
			ConfigurationSection teams = section.createSection("Teams");
			//
			// ------------------------------------------------------------------------------------------------
			// teams.set("WitchsRespawnTime", String.valueOf(getWitchRespawnTime()));
			save += Util.setUpdatedIfNotEqual(teams, "WitchsRespawnTime", String.valueOf(getWitchRespawnTime()));
			//
			if (getWitchRespawnUnit() != null)
				save += Util.setUpdatedIfNotEqual(teams, "Witch-Respawn-Unit", getWitchRespawnUnit().name());
			// teams.set("Witch-Respawn-Unit", getWitchRespawnUnit().toString());
			// ------------------------------------------------------------------------------------------------
			//
			for (AnniTeam team : AnniTeam.Teams) {
				save += Util.createSectionIfNoExitsInt(teams, team.getName() + " Team");
				ConfigurationSection teamSection = teams.createSection(team.getName() + " Team");
				//
				// -------------------------------------------------------------------------------//
				Loc nexusLoc = team.getNexus().getLocation();
				if (nexusLoc != null) {
					save += Util.createSectionIfNoExitsInt(teamSection, "Nexus.Location");
					save += nexusLoc.saveToConfig(teamSection.createSection("Nexus.Location"));
				}
				// -------------------------------------------------------------------------------//
				// Save Beacon
				TeamBeacon beacon = team.getBeacon();
				if (beacon != null && beacon.isValid()) {
					save += Util.createSectionIfNoExitsInt(teamSection, "Beacon.Location");
					save += beacon.getDefaultLocation().saveToConfig(teamSection.createSection("Beacon.Location"));
				}
				// -------------------------------------------------------------------------------//
				Loc spectatorspawn = team.getSpectatorLocation();
				if (spectatorspawn != null) {
					save += Util.createSectionIfNoExitsInt(teamSection, "SpectatorLocation");
					save += spectatorspawn.saveToConfig(teamSection.createSection("SpectatorLocation"));
				}
				// -------------------------------------------------------------------------------//
				Loc witchspawn = team.getWitchLocation();
				if (witchspawn != null) {
					save += Util.createSectionIfNoExitsInt(teamSection, "WitchLocation");
					save += witchspawn.saveToConfig(teamSection.createSection("WitchLocation"));
				}
				// -------------------------------------------------------------------------------//
				save += Util.createSectionIfNoExitsInt(teamSection, "Spawns");
				ConfigurationSection spawnSection = teamSection.createSection("Spawns");
				//
				List<Loc> spawns = team.getSpawnList();
				if (spawns != null && !spawns.isEmpty()) {
					for (int x = 0; x < spawns.size(); x++)
						save += spawns.get(x).saveToConfig(spawnSection.createSection(x + ""));
				}
				// -------------------------------------------------------------------------------//
			}
			//
			//
			// ------------------------------------------------------------------------------------------------
			save += Util.createSectionIfNoExitsInt(section, "UnplaceableBlocks");
			ConfigurationSection unplaceableSec = section.createSection("UnplaceableBlocks");
			//
			for (Entry<Material, UnplaceableBlock> entry : unplaceableBlocks.entrySet()) {
				if (entry != null) {
					save += Util.createSectionIfNoExitsInt(unplaceableSec, entry.getKey().toString());
					ConfigurationSection matSec = unplaceableSec.createSection(entry.getKey().toString());
					//
					save += Util.setUpdatedIfNotEqual(matSec, "Material", entry.getKey().toString());
					// matSec.set("Material", entry.getKey().toString());
					save += Util.setUpdatedIfNotEqual(matSec, "Values", entry.getValue().getValues());
					// matSec.set("Values", entry.getValue().getValues());
				}
			}
			// ------------------------------------------------------------------------------------------------
			if (getAreas().hasPlayArea()) {
				save += Util.createSectionIfNoExitsInt(section, "PlayArea");
				ConfigurationSection playAreaSection = section.getConfigurationSection("PlayArea");
				save += getAreas().getPlayArea().saveToConfig(playAreaSection);
				//
				playAreaSection.set("AllowPVP", null);
				playAreaSection.set("AllowHunger", null);
				playAreaSection.set("AllowDamage", null);
			}
			// ------------------------------------------------------------------------------------------------
		}
		//
		return save;
	}

	public void unLoadMap() {
		World tpworld = Game.LobbyMap != null ? Game.LobbyMap.getWorld() : null;
		if (tpworld == null) {
			tpworld = Bukkit.getWorlds().size() > 0 ? Bukkit.getWorlds().get(0) : null;
		}
		//
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null) {
				if (p.getWorld().getName().equals(this.getWorldName())) {
					if (tpworld != null)
						p.teleport(tpworld.getSpawnLocation());
					else
						p.kickPlayer("Unloading the world and we dont want you to get trapped or glitched!");
				}
			}
		}
		//
		this.unregisterListeners();

		boolean b = Bukkit.unloadWorld(super.getWorldName(), false);
		Bukkit.getLogger().info("[Annihilation] " + super.getNiceWorldName() + " was unloaded successfully: " + b);
	}

	public void backUpWorld() {
		super.getWorld().save();
	}

	public void backupConfig() {
		super.saveToConfig(true, true);
	}

	public boolean addUnplaceableBlock(Material mat, byte b) {
		UnplaceableBlock block = unplaceableBlocks.get(mat);
		if (block == null) {
			block = new UnplaceableBlock();
			unplaceableBlocks.put(mat, block);
		}
		return block.addData(b);
	}

	public boolean removeUnplaceableBlock(Material mat, byte b) {
		UnplaceableBlock block = unplaceableBlocks.get(mat);
		if (block == null) {
			block = new UnplaceableBlock();
			unplaceableBlocks.put(mat, block);
		}
		return block.removeData(b);
	}

	@Override
	public void registerListeners(Plugin plugin) {
		super.registerListeners(plugin);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		blocks.registerListeners(plugin);
	}

	@Override
	public void unregisterListeners() {
		super.unregisterListeners();
		HandlerList.unregisterAll(this);
		blocks.unregisterListeners();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onWeatherChange(WeatherChangeEvent event) {
		if (Config.MAP_LOADING_DISABLE_RAIN.toBoolean()) {
			event.setCancelled(event.toWeatherState());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EnderContainersUpdater(GameStartEvent eve) {
		// Set Ender Furnaces blocks
		if (!enderFurnaces.isEmpty()) {
			for (FacingObject obj : enderFurnaces.values()) {
				if (obj == null || obj.getLocation() == null || obj.getFacingDirection() == null) {
					continue;
				}

				// Get and Check Block
				Block b = obj.getLocation().toLocation().getBlock();
				if (b == null || b.getType() == Material.BURNING_FURNACE) {
					continue;
				}

				// Set
				Furnace f = new Furnace(Material.BURNING_FURNACE);
				f.setFacingDirection(obj.getFacingDirection());
				BlockState s = b.getState();
				s.setData(f);
				s.update(true);
			}
		}

		// Set Ender Brewings blocks
		if (!this.brewingStands.isEmpty()) {
			for (FacingObject obj : brewingStands.values()) {
				if (obj == null || obj.getLocation() == null) {
					continue;
				}

				// Get and Check Block
				Block b = obj.getLocation().toLocation().getBlock();
				if (b == null || b.getType() == Material.BREWING_STAND) {
					continue;
				}

				// Set
				b.setType(Material.BREWING_STAND);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockPlaceCheck(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			final Block b = event.getBlock();
			UnplaceableBlock block = unplaceableBlocks.get(b.getType());
			if (block != null) {
				if (block.isData((byte) -1) || block.isData(b.getData())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void enderContainers(PlayerInteractEvent event) {
		// Check interact is with a block
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// Get block and check
			final Block b = event.getClickedBlock();
			if (b == null) {
				return;
			}

			// Check Material
			if (b.getType() == Material.FURNACE || b.getType() == Material.BURNING_FURNACE
					|| b.getType() == Material.BREWING_STAND) {
				// Get and check Key
				final MapKey key = MapKey.getKey(b.getLocation());
				if (key == null) {
					return;
				}

				// Get and check AnniPlayer
				final AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
				if (p == null || !p.isOnline()) {
					return;
				}

				if (enderFurnaces.containsKey(key)) {
					// Cancell interaction
					event.setCancelled(true);

					// Check is game running
					if (Game.isNotRunning()) {
						p.getPlayer().sendMessage(Lang.CANT_OPEN_ENDER_FURNACE_GAME_NOT_RUNNING.toString());
						return;
					}

					// Open Furnace
					if (ServerVersion.serverOlderThan(ServerVersion.v1_11_R1)) { //!VersionUtils.getVersion().contains("v1_12") && !VersionUtils.getVersion().contains("v1_11")
						p.openFurnace();
						return;
					}

				} else if (brewingStands.containsKey(key)) {
					// Cancell interaction
					event.setCancelled(true);

					// Check is game running
					if (Game.isNotRunning()) {
						p.getPlayer().sendMessage(Lang.CANT_OPEN_ENDER_BREWING_GAME_NOT_RUNNING.toString());
						return;
					}

					// Open Brewing
					p.openBrewing();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void enderContainersBreakCheck(BlockBreakEvent event) {
		// Get and check block type
		final Block b = event.getBlock();
		if (b.getType() == Material.FURNACE || b.getType() == Material.BURNING_FURNACE
				|| b.getType() == Material.BREWING_STAND) {

			// Get and Check Key
			final MapKey key = MapKey.getKey(b.getLocation());
			if (key != null) {

				// Ender Furnace
				if (enderFurnaces.containsKey(key)) {
					if (!KitUtils.itemHasName(event.getPlayer().getItemInHand(), CustomItem.ENDERFURNACE.getName())) {
						event.setCancelled(true);
					}
				} else if (brewingStands.containsKey(key)) { // Ender Brewing
					if (!KitUtils.itemHasName(event.getPlayer().getItemInHand(), CustomItem.ENDER_BREWING.getName())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public void setWitchRespawnTime(int l) {
		witchRespawnTime = l;
	}

	public void setWitchRespawnUnit(TimeUnit unit) {
		witchRespawnUnit = unit;
	}

	public int getPhaseTime() {
		return PhaseTime;
	}

	public boolean setPhaseTime(int time) {
		if (time > 0)
			PhaseTime = time;
		return time > 0;
	}

	public int getDamageMultiplier() {
		return damageMultiplier;
	}

	public boolean setDamageMultiplier(int multiplier) {
		if (multiplier > 0)
			damageMultiplier = multiplier;
		return multiplier > 0;
	}

	public int getCurrentPhase() {
		return currentphase;
	}

	public boolean setPhase(int phase) {
		if (phase > -1)
			currentphase = phase;
		if (restarter != null)
			restarter.check();
		return phase > -1;
	}

	public boolean canDamageNexus() {
		return canDamageNexus;
	}

	public int getWitchRespawnTime() {
		return witchRespawnTime;
	}

	public TimeUnit getWitchRespawnUnit() {
		return witchRespawnUnit;
	}

	public void setCanDamageNexus(boolean canDamage) {
		canDamageNexus = canDamage;
	}

	public RegeneratingBlocks getRegeneratingBlocks() {
		return blocks;
	}

	public List<Loc> getDiamondLocations() {
		return Collections.unmodifiableList(diamondLocs);
	}

	public List<Loc> getPortalsLocations() {
		return Collections.unmodifiableList(PortalLocs);
	}

	public List<Loc> getFurnaceLocations() {
		return Collections.unmodifiableList(FurnaceLocs);
	}

	public Map<MapKey, FacingObject> getEnderFurnaces() {
		return Collections.unmodifiableMap(enderFurnaces);
	}

	public void addEnderFurnace(Loc loc, BlockFace direction) {
		this.addEnderFurnace(new FacingObject(direction, loc));
	}

	public void addEnderFurnace(FacingObject furnace) {
		MapKey key = MapKey.getKey(furnace.getLocation());
		if (key != null && !enderFurnaces.containsKey(key)) {
			try {
				Block block = furnace.getLocation().toLocation().getBlock();
				if (block.getType() != Material.FURNACE && block.getType() != Material.BURNING_FURNACE)
					block.setType(Material.BURNING_FURNACE);

				Furnace f = new Furnace(Material.BURNING_FURNACE);
				f.setFacingDirection(furnace.getFacingDirection());
				BlockState s = block.getState();
				s.setData(f);
				s.update(true);
			} catch (Exception e) {
				// ----
			}
			//
			enderFurnaces.put(key, furnace);
		}
	}

	public void addBrewingStand(Loc loc) {
		addBrewingStand(new FacingObject(null, loc));
	}

	public void addBrewingStand(FacingObject brewing) {
		MapKey key = MapKey.getKey(brewing.getLocation());
		if (key != null && !brewingStands.containsKey(key)) {
			// Spawn Block
			Block block = brewing.getLocation().toLocation().getBlock();

			// Set to BREWING_STAND
			if (block.getType() != Material.BREWING_STAND) {
				block.setType(Material.BREWING_STAND);
			}

			// Save
			brewingStands.put(key, brewing);
		}
	}

	public boolean removeEnderFurnace(Loc loc) {
		return removeEnderFurnace(loc.toLocation());
	}

	public boolean removeEnderFurnace(Location loc) {
		MapKey key = MapKey.getKey(loc);
		if (key != null && enderFurnaces.containsKey(key)) {
			enderFurnaces.remove(key);
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
			return true;
		}
		return false;
	}

	public boolean removeEnderBrewing(Loc loc) {
		return removeEnderBrewing(loc.toLocation());
	}

	public boolean removeEnderBrewing(Location loc) {
		MapKey key = MapKey.getKey(loc);
		if (key != null && brewingStands.containsKey(key)) {
			brewingStands.remove(key);
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
			return true;
		}
		return false;
	}

	public void addDiamond(Loc loc) {
		if (loc != null && !diamondLocs.contains(loc))
			diamondLocs.add(loc);
	}

	public void addPortal(Loc loc) {
		if (loc != null && !PortalLocs.contains(loc))
			PortalLocs.add(loc);
	}

	public boolean removeDiamond(Loc loc) {
		for (int x = 0; x < diamondLocs.size(); x++) {
			Loc l = diamondLocs.get(x);
			//
			if (l != null) {
				if (l.equals(loc)) {
					diamondLocs.remove(x);
					return true;
				}
			}
		}
		return false;
	}

	public boolean removePortal(Loc loc) {
		if (loc != null && PortalLocs.contains(loc)) {
			PortalLocs.remove(loc);
			return true;
		}
		return false;
	}

	public static Map<Material, UnplaceableBlock> getunplaceableBlocks() {
		return unplaceableBlocks;
	}

	private class UnplaceableBlock {
		private ArrayList<Byte> dataVals;

		public UnplaceableBlock() {
			dataVals = new ArrayList<Byte>();
		}

		public boolean addData(Byte b) {
			return dataVals.add(b);
		}

		public boolean removeData(Byte b) {
			return dataVals.remove(b);
		}

		public boolean isData(Byte b) {
			return dataVals.contains(b);
		}

		public List<Byte> getValues() {
			return Collections.unmodifiableList(dataVals);
		}
	}
}