package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJoinTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.JoinerArmors.JoinerArmorStand;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitLoading;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class LobbyMap extends AnniMap implements Listener {
	
	/**
	 * AldeanoJoiners Global Values.
	 */
	private static String Nombre_AldeanoJoinerAzul, Nombre_AldeanoJoinerRojo, Nombre_AldeanoJoinerAmarillo, Nombre_AldeanoJoinerVerde;
	private static Loc AldeanoJoinerAzul_Loc, AldeanoJoinerRojo_Loc, AldeanoJoinerAmarillo_Loc, AldeanoJoinerVerde_Loc;

	/**
	 * Class values
	 */
	private Location spawn;
	private LivingEntity AldeanoJoiner_Azul_E, AldeanoJoiner_Rojo_E, AldeanoJoiner_Amarillo_E, AldeanoJoiner_Verde_E;
	private Map<AnniTeam, List<Loc>> npcsPoses;
	private List<JoinerArmorStand> joiner_armors;

	public LobbyMap(Location spawn, boolean loadOnCostructor) {
		super(spawn.getWorld().getName(), new File(AnnihilationMain.INSTANCE.getDataFolder(), "AnniLobbyConfig.yml"),
				loadOnCostructor);
		this.spawn = spawn;
		registerListener();
	}

	public LobbyMap(File configFile, boolean loadOnCostructor) {
		super(null, configFile, loadOnCostructor);
		registerListener();
	}

	private void registerListener() {
		Bukkit.getPluginManager().registerEvents(this, AnnihilationMain.INSTANCE);
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location newSpawn) {
		if (newSpawn != null) {
			spawn = newSpawn;
			super.setWorldName(newSpawn.getWorld().getName());
		}
	}
	
	public void addJoinerArmor(final JoinerArmorStand joiner) {
		// add.
		this.joiner_armors.add(joiner);
	}
	
	public void removeJoinerArmor(final JoinerArmorStand joiner) {
		// remove.
		this.joiner_armors.remove(joiner);
	}
	
	public boolean isJoinerArmor(final ArmorStand stand) {
		return getJoinerArmor(stand) != null;
	}
	
	public JoinerArmorStand getJoinerArmor(final ArmorStand stand) {
		// check all joiners.
		for (JoinerArmorStand joiner : this.joiner_armors) {
			// check is equals.
			if (joiner != null 
					&& stand.getUniqueId().equals(joiner.getId())) {
				return joiner; // return this joiner if is equals.
			}
		}
		return null;
	}
	
	public void addTeamNPCsPosition(final AnniTeam team, final Loc l) {
		if (team != null && l != null) {
			if (!hasTeamNPCsLocation(team, l)) {
				npcsPoses.get(team).add(l);
			}
		}
	}

	public void removeTeamNPCsPostion(final AnniTeam team, final Loc l) {
		if (team != null && l != null) {
			npcsPoses.get(team).remove(l);
		}
	}

	public List<Loc> getNPCsPositions(final AnniTeam team) {
		return npcsPoses.get(team);
	}

	private boolean hasTeamNPCsLocation(final AnniTeam team, final Loc loc) {
		if (loc == null) {
			return false;
		}

		for (Loc l : npcsPoses.get(team)) {
			if (l == null || l.getWorld() == null) {
				continue;
			}

			if (loc.equals(l)) {
				return true;
			}
		}
		return false;
	}

	public static void setAldeanoJoinerAzul_Loc(Loc loc) {
		AldeanoJoinerAzul_Loc = loc;
	}

	public Loc getAldeanoJoinerAzul_Loc() {
		return AldeanoJoinerAzul_Loc;
	}

	public String getNombre_AldeanoJoinerAzul() {
		return Nombre_AldeanoJoinerAzul;
	}

	public static void setNombre_AldeanoJoinerAzul(String s) {
		Nombre_AldeanoJoinerAzul = s;
	}

	public void setAldeanoJoiner_Azul_E(LivingEntity e) {
		AldeanoJoiner_Azul_E = e;
	}

	public LivingEntity getAldeanoJoiner_Azul_livingEntity() {
		return AldeanoJoiner_Azul_E;
	}

	// AldeanoJoiner Verde
	public void setAldeanoJoinerVerde_Loc(Loc loc) {
		AldeanoJoinerVerde_Loc = loc;
	}

	public Loc getAldeanoJoinerVerde_Loc() {
		return AldeanoJoinerVerde_Loc;
	}

	public String getNombre_AldeanoJoinerVerde() {
		return Nombre_AldeanoJoinerVerde;
	}

	public void setNombre_AldeanoJoinerVerde(String s) {
		Nombre_AldeanoJoinerVerde = s;
	}

	public void setAldeanoJoiner_Verde_E(LivingEntity e) {
		AldeanoJoiner_Verde_E = e;
	}

	public LivingEntity getAldeanoJoiner_Verde_livingEntity() {
		return AldeanoJoiner_Verde_E;
	}

	public void setAldeanoJoinerRojo_Loc(Loc loc) {
		AldeanoJoinerRojo_Loc = loc;
	}

	public Loc getAldeanoJoinerRojo_Loc() {
		return AldeanoJoinerRojo_Loc;
	}

	public String getNombre_AldeanoJoinerRojo() {
		return Nombre_AldeanoJoinerRojo;
	}

	public void setNombre_AldeanoJoinerRojo(String s) {
		Nombre_AldeanoJoinerRojo = s;
	}

	public void setAldeanoJoiner_Rojo_E(LivingEntity e) {
		AldeanoJoiner_Rojo_E = e;
	}

	public LivingEntity getAldeanoJoiner_Rojo_livingEntity() {
		return AldeanoJoiner_Rojo_E;
	}

	// AldeanoJoiner Amarillo
	public void setAldeanoJoinerAmarillo_Loc(Loc loc) {
		AldeanoJoinerAmarillo_Loc = loc;
	}

	public Loc getAldeanoJoinerAmarillo_Loc() {
		return AldeanoJoinerAmarillo_Loc;
	}

	public String getNombre_AldeanoJoinerAmarillo() {
		return Nombre_AldeanoJoinerAmarillo;
	}

	public void setNombre_AldeanoJoinerAmarillo(String s) {
		Nombre_AldeanoJoinerAmarillo = s;
	}

	public void setAldeanoJoiner_Amarillo_E(LivingEntity e) {
		AldeanoJoiner_Amarillo_E = e;
	}

	public LivingEntity getAldeanoJoiner_Amarillo_livingEntity() {
		return AldeanoJoiner_Amarillo_E;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onJ(PlayerJoinTeamEvent eve) {
		// Check Game State
		if (Game.isGameRunning()) {
			return;
		}

		// Get And Check Player
		final AnniPlayer ap = eve.getPlayer();
		final AnniTeam team = eve.getTeam();
		if (ap == null || !ap.isOnline() || eve.getTeam() == null) {
			return;
		}

		final Player p = ap.getPlayer();
		if (p == null) {
			return;
		}

		// Set Armor
		final ItemStack[] armor = Loadout.coloredArmor(team);
		p.getInventory().setHelmet(armor[3]);
		p.getInventory().setChestplate(armor[2]);
		p.getInventory().setLeggings(armor[1]);
		p.getInventory().setBoots(armor[0]);
		p.updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onL(PlayerLeaveTeamEvent eve) {
		// Check Game State
		if (Game.isGameRunning()) {
			return;
		}

		// Get And Check Player
		final AnniPlayer ap = eve.getPlayer();
		if (ap == null || !ap.isOnline()) {
			return;
		}

		final Player p = ap.getPlayer();
		if (p == null) {
			return;
		}

		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.updateInventory();
	}
	
	public void sendToSpawn(final Player player) {
		if (spawn != null && KitUtils.isValidPlayer(player)) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setMaxHealth(20.0D);
			player.setLevel(0);
			player.setExp(0F);
			player.setFoodLevel(20);
			player.setHealth(20.0D);
			player.setGameMode(Config.GAME_MODE.toGameMode());
			player.teleport(getSpawn());
			player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
			//
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (KitUtils.isValidPlayer(all)) {
					all.showPlayer(player);
				}
			}

			if (Config.USE_STRIKE_LIGHTNING_EFFECT.toBoolean())
				player.getWorld().strikeLightningEffect(player.getLocation());
			//
			if (Config.MAP_LOADING_VOTING.toBoolean()) {
				player.getInventory().setItem(3, CustomItem.KITMAP.toItemStack());
				if (Config.USE_TEAM_SELECTOR.toBoolean()) {
					player.getInventory().setItem(4, CustomItem.TEAMMAP.toItemStack());
				}
			} else {
				player.getInventory().setItem(3, CustomItem.KITMAP.toItemStack());
				if (Config.USE_TEAM_SELECTOR.toBoolean()) {
					player.getInventory().setItem(5, CustomItem.TEAMMAP.toItemStack());
				}
			}
			//
			if (Game.isNotRunning()) {
				if (Config.MAP_LOADING_VOTING.toBoolean())
					player.getInventory().setItem(5, CustomItem.VOTEMAP.toItemStack());

				if (Config.USE_LOBBY_COMMPAS.toBoolean())
					player.getInventory().setItem(7, CustomItem.GO_TO_LOBBY.toItemStack());

				if (Util.AnniOEEnabled())
					player.getInventory().setItem(8, Util.getOptionsItem(player));
			} else {
				if (Config.MAP_LOADING_VOTING.toBoolean()) {
					player.getInventory().clear();
					player.updateInventory();
					//
					player.getInventory().setItem(3, CustomItem.KITMAP.toItemStack());
					if (Config.USE_TEAM_SELECTOR.toBoolean()) {
						player.getInventory().setItem(5, CustomItem.TEAMMAP.toItemStack());
					}
				}

				if (Config.USE_SPECTATOR_MODE.toBoolean())
					if (Util.AnniOEEnabled()) {
						player.getInventory().setItem(0, CustomItem.SPECTATORMAP.toItemStack());
						player.getInventory().setItem(4, Util.getOptionsItem(player));
					} else {
						player.getInventory().setItem(4, CustomItem.SPECTATORMAP.toItemStack());
					}

				if (Config.USE_LOBBY_COMMPAS.toBoolean())
					player.getInventory().setItem(8, CustomItem.GO_TO_LOBBY.toItemStack());
			}

			if (Config.USE_KITS_SHOP_ITEM.toBoolean() && KitLoading.ShopIsEnabled())
				player.getInventory().setItem(1, CustomItem.KIST_SHOT.toItemStack());

			Util.removeAllPotionEffects(player);
			//
			player.getInventory().setArmorContents(null);
			player.updateInventory();
		}
	}

	@SuppressWarnings("deprecation")
	public void sendToDeadSpawn(final Player player) {
		if (spawn != null && player != null) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setMaxHealth(20.0D);
			player.setLevel(0);
			player.setExp(0F);
			player.setFoodLevel(20);
			player.setHealth(20.0D);
			player.setGameMode(Config.GAME_MODE.toGameMode());
			if (getSpawn() != null) {
				player.teleport(getSpawn());
			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Invalid Lobby Spawn");
			}
			
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				CompatibleParticles.LAVA.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 5, player.getLocation(), 2000D);
				CompatibleParticles.CLOUD.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 5, player.getLocation().clone(), 2000D);
				CompatibleParticles.CLOUD.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 5, player.getLocation().clone().add(0.0D, -1.0D, 0.0D), 2000D);
			} else {
				CompatibleParticles.LAVA.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 5, player.getLocation(), 2000D);
				CompatibleParticles.CLOUD.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 5, player.getLocation().clone(), 2000D);
				CompatibleParticles.CLOUD.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 0.1F, 5, player.getLocation().clone().add(0.0D, -1.0D, 0.0D), 2000D);
			}
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (KitUtils.isValidPlayer(all)) {
					all.showPlayer(player);
				}
			}
			
			Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					player.getLocation().getWorld().playSound(player.getLocation(), UniversalSound.ZOMBIE_UNFECT.asBukkit(), 2.0F, 6.0F);
				}
			}, 4L);
			
			ItemStack[] armor = KitUtils.getLeatherArmor();
			for (ItemStack ar : armor) {
				if (ar != null) {
					LeatherArmorMeta meta = (LeatherArmorMeta) ar.getItemMeta();
					meta.setColor(Color.WHITE);
					ar.setItemMeta(meta);
				}
			}
			
			player.getInventory().setHelmet(armor[3]);
			player.getInventory().setChestplate(armor[2]);
			player.getInventory().setLeggings(armor[1]);
			player.getInventory().setBoots(armor[0]);
			
			if (Config.USE_SPECTATOR_MODE.toBoolean()) {
				player.getInventory().setItem(0, CustomItem.SPECTATORMAP.toItemStack());
			}
			
			if (Config.USE_LOBBY_COMMPAS.toBoolean()) {
				player.getInventory().setItem(8, CustomItem.GO_TO_LOBBY.toItemStack());
			}
		}
		player.updateInventory();
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			// ----Spawn
			if (section.isConfigurationSection("SpawnLocation")) {
				spawn = new Loc(section.getConfigurationSection("SpawnLocation")).toLocation();
				//
				if (spawn != null && spawn.getWorld() != null)
					super.setWorldName(spawn.getWorld().getName());
				else
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Annihilation] Lobby Map not Found");
			}
			
			// Load NPCs Team Postions
			loadNPCsTeamPositons(section);
			
			// Joiner ArmorStands managament.
			JoinerArmorStands(section);
		}
	}
	
	private void JoinerArmorStands(final ConfigurationSection section) {
//		System.out.println("[Annihilation] Cleaning entities....");
		// clean old armor stands.
		Shedulers.scheduleSync(() ->{
			// clear armors in the world.
//			int removeArmors = 0;

			// remove...
			for (World world : Bukkit.getWorlds()) {
				for (Entity ent : world.getEntities()) {
					if (ent instanceof ArmorStand 
							|| (ent != null && ent.getType() == EntityType.ARMOR_STAND)) {
						// change visibility.
						((ArmorStand)ent).setVisible(false);
						
						// remove.
						ent.remove();
						
						// ++ removeArmors
//						removeArmors ++;
					}
				}
			}
			
//			System.out.println("[Annihilation] Cleaned entities: " + removeArmors);
		}, 10);
		
		// load joiners.
		Shedulers.scheduleSync(() -> {
			// check joiners list.
			if (this.joiner_armors == null) {
				this.joiner_armors = new ArrayList<JoinerArmorStand>();
			}
			
			// load joiners in config.
			if (section.isConfigurationSection("JoinerArmorStands")) {
				// get section.
				final ConfigurationSection joiners = section.getConfigurationSection("JoinerArmorStands");
				
				// load joiners.
				for (String key : joiners.getKeys(false)) {
					// get and check joiner section.
					ConfigurationSection sc = joiners.getConfigurationSection(key);
					if (sc == null) {
						continue;
					}
					
					// load joiner.
					JoinerArmorStand joiner = new JoinerArmorStand(sc);
					
					// spawn.
					joiner.spawn();
					
					// register.
					addJoinerArmor(joiner);
				}
			}
			
//			System.out.println("[Annihilation] Loaded Joiner ArmorStands: " + joiner_armors.size());
		}, 20);
	}
	
	public void saveTheConfig() {
		this.saveConfig();
		super.saveToConfig(false, false);

		if (getWorld() != null) {
			this.getWorld().save();
		}
	}

	@Override
	protected int saveToConfig(ConfigurationSection section) {
		int save = 0;
		//
		if (section != null) {
			// ---Save Spawn
			if (getSpawn() != null) {
				final Loc b = new Loc(getSpawn(), true);
				//
				if (b != null) {
					save += Util.createSectionIfNoExitsInt(section, "SpawnLocation");
					save += b.saveToConfig(section.getConfigurationSection("SpawnLocation"));
				}
			}
			
			// Create if not exits
			save += Util.createSectionIfNoExitsInt(section, "Team-NPCs-Positions");

			// Get Created
			ConfigurationSection posesSection = section.getConfigurationSection("Team-NPCs-Positions");

			// Save
			for (AnniTeam team : AnniTeam.Teams) {
				List<Loc> poses = getNPCsPositions(team);
				if (poses == null || poses.isEmpty()) {
					continue;
				}

				// Create if not exits
				save += Util.createSectionIfNoExitsInt(posesSection, team.getName() + "NPCs-Poses");

				// Get Created
				ConfigurationSection teamPoses = posesSection.getConfigurationSection(team.getName() + "NPCs-Poses");

				// Counter int
				int counter = 1;

				// Save
				for (Loc l : poses) {
					if (l == null) {
						continue;
					}

					save += Util.createSectionIfNoExitsInt(teamPoses, "" + counter);
					save += l.saveToConfig(teamPoses.getConfigurationSection("" + counter));
					counter++;
				}
			}
			
			// save joiner armor stands.
			// make counter.
			int counter = 0;
			// clear section.
			final ConfigurationSection joiners = section.createSection("JoinerArmorStands");
			for (JoinerArmorStand joiner : this.joiner_armors) {
				// get joiner section.
				ConfigurationSection sc = joiners.createSection("joiner-" + counter);
				
				// save.
				joiner.saveToConfig(sc);
				
				// ++ counter.
				counter ++;
			}

			// save Config
			saveConfig();
		}
		return save;
	}

	private void loadNPCsTeamPositons(ConfigurationSection sc) {
		if (sc != null) {
			if (npcsPoses == null || npcsPoses.isEmpty()) {
				// Set
				npcsPoses = new HashMap<AnniTeam, List<Loc>>();

				// Put team
				for (AnniTeam team : AnniTeam.Teams) {
					npcsPoses.put(team, new ArrayList<Loc>());
				}
			}
			// Get Main Section
			ConfigurationSection a = sc.getConfigurationSection("Team-NPCs-Positions");
			if (a == null) {
				return;
			}

			// Get Team NPCs Sections
			for (AnniTeam team : AnniTeam.Teams) {
				// Check is not null
				ConfigurationSection b = a.getConfigurationSection(team.getName() + "NPCs-Poses");
				if (b == null) {
					continue;
				}

				// Get Locs
				for (String key : b.getKeys(false)) {
					Loc l = new Loc(b.getConfigurationSection(key));
					if (l != null) {
						// Save
						addTeamNPCsPosition(team, l);
					}
				}
			}
		}
	}
}
