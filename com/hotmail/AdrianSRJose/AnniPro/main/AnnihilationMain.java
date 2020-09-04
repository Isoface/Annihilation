package com.hotmail.AdrianSRJose.AnniPro.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.util.dependence.PluginDependence;
import com.hotmail.AdrianSR.core.util.lang.CustomPluginLanguageEnumContainer;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSR.core.version.CoreVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PluginDisableEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.BossReal;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Bruja;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.FastHitAntihack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.GameListeners;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.GameListeners2;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.PicakaxeOre;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.EnderChest.EnderChestManager;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.JoinerArmors.JoinerArmorListeners;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.BeaconListener;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.GameBeacon;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.PlayerNPCManager;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPCManager;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.listeners.ChatListener;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.listeners.TabListener;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.LobbyMap;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.BossBarHandler;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.FurnaceHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.SpectatorMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.voting.MapVotingMenu;
import com.hotmail.AdrianSRJose.AnniPro.kits.BungeeSender;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitLoading;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.GlassColor;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.KitsMenuConfig;
import com.hotmail.AdrianSRJose.AnniPro.mapBuilder.MapBuilder;
import com.hotmail.AdrianSRJose.AnniPro.mapBuilder.JoinerArmors.RandomJoinerArmorsCreator;
import com.hotmail.AdrianSRJose.AnniPro.nametag.NametagsManager;
import com.hotmail.AdrianSRJose.AnniPro.plugin.AnniPlugin;
import com.hotmail.AdrianSRJose.AnniPro.plugin.AnniPluginLoader;
import com.hotmail.AdrianSRJose.AnniPro.plugin.AnniPluginManager;
import com.hotmail.AdrianSRJose.AnniPro.plugin.SimpleAnniPluginManager;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.game.GameScoreboardConfigurationHandler;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.game.GameScoreboardHandler;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.lobby.LobbyScoreboardConfigurationHandler;
import com.hotmail.AdrianSRJose.AnniPro.scoreboard.lobby.LobbyScoreboardHandler;
import com.hotmail.AdrianSRJose.AnniPro.utils.BossStar;
import com.hotmail.AdrianSRJose.AnniPro.utils.CallingJumpEvent;
import com.hotmail.AdrianSRJose.AnniPro.utils.GameBeaconLoader;
import com.hotmail.AdrianSRJose.AnniPro.utils.InvisibilityListeners;
import com.hotmail.AdrianSRJose.AnniPro.utils.SpecificTool;
import com.hotmail.AdrianSRJose.AnniPro.utils.Utf8YamlConfiguration;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.voting.AutoStarter;
import com.hotmail.AdrianSRJose.AnniPro.voting.ChatConfigManager;
import com.hotmail.AdrianSRJose.AnniPro.voting.ChatVars;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting;
import com.hotmail.AdrianSRJose.AnniPro.voting.Shop.ShopConfigManager;
import com.hotmail.AdrianSRJose.AnniPro.voting.Shop.ShopVars;

import lombok.Getter;

public class AnnihilationMain extends CustomPlugin implements Listener {
	
	public static JavaPlugin INSTANCE;
	public static AnnihilationAPI API;
	public static BungeeSender SENDER;
	public static boolean VIA_VERSION;
	public static VersionHandler versionHandler;
	public static Utf8YamlConfiguration MAIN_YML;
	private static AnniPluginManager PLUGIN_MANAGER;
	private static final Map<String, AnniPlugin> PLUGINS = new HashMap<String, AnniPlugin>();

	@Override
	public boolean setUp() {
		// Set Instance
		INSTANCE = this;
		
		// LOAD CONFIGS
		loadLang(); // Load language.
		loadKitsMenu(); // Load Kits Menu Config.
		loadImages(); // Load Images.

		// LOAD MANAGERS
		if (!ConfigManager()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return false;
		}
		
		// ConfigManager.load(this); // Load Game Vars Config Manager
		ChatConfigManager.load(this); // Load Chat Vars Config Manager
		ShopConfigManager.load(this); // Load Shop Vars Config Manager

		// Load lobby from file, and All Config Vars.
		loadMainValues();

		// Register '/Anni' Command
		AnniCommand.register(this);

		// Register '/Anni' Command Arguments
		buildAnniCommand();
				
		// When is normal mode
		if (!Config.LOBBY_MODE.toBoolean()) {
			// Get API
			API = new AnnihilationAPI(this);

			// Register Invisibility Listeners
			new InvisibilityListeners(this);

			// Register Events in this class
			Bukkit.getPluginManager().registerEvents(this, this);

			// Register Voting and AutoStart Managers
			handle_Voting_and_AutoStart();

			// Load Private Chest Config
			new EnderChestManager(this);
			// loadPrivateChest();

			// Register new Map Builder
			new MapBuilder(this);

			// Register Anni Player Listeners
			AnniPlayer.RegisterListener(this);

			// Send online Players to the Lobby
			if (Game.LobbyMap != null) {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (KitUtils.isValidPlayer(pl)) {
						Game.LobbyMap.sendToSpawn(pl);
					}
				}
			}
			
			// Super Annihilation User admin commands!
//			new MapBuilder.jjmm(this);
			
			// Team Command and selector menu
			new TeamCommand(this);
			
			// Register new Team Commands and new Private Furnace Handler
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_11_R1)) { // VersionUtils.is1_11_or_1_12()
				new FurnaceHandler(this);
			}

			// Register Boss Bar Handler
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				new BossBarHandler(this);
			}

			// REGISTER NEW HANDLERS
			new SpectatorMenu(this); // Team Spectator Handler
			new GameListeners(this); // Game Listeners Handler
			new GameListeners2(this); // Game Listeners2 Handler
			
			new ChatListener(this);
			new TabListener(this);
			
			new PicakaxeOre(this);
			new GameBeacon(this);
			new AreaCommand(this); // Area Command register
			new NextPhaseCommand(this);
			new KitLoading(this); // Kits Handler
			new Bruja(this); // Witch spawner Handler
			new BossReal(this); // Boss spawner Handler
			new BossStar(this); // Boss Start Handler
			new BeaconListener(this);
			new CallingJumpEvent(this);
			SENDER = new BungeeSender(this);
			new RandomJoinerArmorsCreator(this);
			new JoinerArmorListeners(this);
			
			// NPCS
			new TeamNPCManager(this); // Team NPCs Manager
			if (Config.USE_NPC.toBoolean()) {
				new PlayerNPCManager(this);
			}

			if (Config.USE_SPECIFIC_TOOL.toBoolean()) { // specific tool handler
				new SpecificTool(this);
			}

			// Check AnniOptionsExtension
			final Plugin ox = Bukkit.getPluginManager().getPlugin("AnniPlayerOptions");
			if (ox != null && ox.isEnabled()) {
				for (Player all : Bukkit.getOnlinePlayers()) {
					AnniPlayer ap = AnniPlayer.getPlayer(all.getUniqueId());
					if (!KitUtils.isValidPlayer(ap)) {
						continue;
					}

					// Add Item
					all.getInventory().setItem(8, Util.getOptionsItem(all));
				}
			}

			// Anti Speed Mine Hack
			if (Config.USE_ANTI_SPEED_MINE_HACK.toBoolean()) {
				new FastHitAntihack(this);
			}

			// Check is ViaVersion enabled
			// VIA_VERSION
			Plugin vv = Bukkit.getPluginManager().getPlugin("ViaVersion");
			VIA_VERSION = vv != null && vv.isEnabled();
			if (VIA_VERSION) {
				versionHandler = new VersionHandler(this);
			}
			
//			// Game Backup
//			if (Config.USE_GAME_BACKUP.toBoolean()) {
//				Bukkit.getPluginManager().registerEvents(new GameBackup(this), this);
//			}

			// Print Enabled Messages
			Bukkit.getConsoleSender()
					.sendMessage(Util.wc("&a&l&n[Annihilation] Enabled&f | &lVersion: " + getDescription().getVersion()));
			Bukkit.getConsoleSender().sendMessage(Util.wc(
					"&a&l[Annihilation] You are using the spigot version: &f&n" + ServerVersion.getVersion().name()));
			
			// create plugin manager instance
			PLUGIN_MANAGER = new SimpleAnniPluginManager(getServer(), this);
			
			// load plugins
			loadPlugins(new File(this.getDataFolder(), "plugins"));
			return true;
		}
		
		// Register Anni Player Listeners
		AnniPlayer.RegisterListener(this);
		
		// Shop
		new KitLoading(this); // Kits Handler

		// Print lobby mode enabled message
		Util.print("Lobby Mode Enabled!");
		return true;
	}
	
	private void loadPlugins(final File plugins) {
		// check plugins folder
		if (!plugins.exists() || !plugins.isDirectory()) {
			// remove if exists as file
			if (plugins.exists() && !plugins.isDirectory()) {
				plugins.delete();
			}
			
			// mkdir
			plugins.mkdir();
		}
		
		// load plugins
		for (String pluginName : plugins.list(new FilenameFilter() {
			// .jar filter
			@Override
			public boolean accept(File dir, String name) {
				return dir != null && name.endsWith(".jar");
			}})) {
			
			// Check .jar file
			final File jar = new File(plugins, pluginName);
			if (!jar.exists()) {
				continue;
			}
			
			// print loading message
			System.out.println("[Annihilation] loading " + pluginName);
		
			// load
			final AnniPluginLoader loader = new AnniPluginLoader();
			try {
				// load plugin
				final AnniPlugin plugin = loader.loadPlugin(jar);
				
				// on enable
				plugin.onEnable();
				
				// put plugin in plugins map
				PLUGINS.put(plugin.getName(), plugin);
			} catch (InvalidPluginException e) {
				e.printStackTrace();
			}
		}
	}

	private @Getter final Map<AnniTeam, BufferedImage> teamImages = new HashMap<AnniTeam, BufferedImage>();
	private @Getter final Map<Integer, BufferedImage> phaseImages = new HashMap<Integer, BufferedImage>();
	private @Getter BufferedImage gameEndImage;

	private void loadImages() {
		// get original images
		try {
			// get Original Team Images
			for (AnniTeam team : AnniTeam.Teams) {
				BufferedImage image = ImageIO.read(getResource("Images/" + team.getName() + "Team.png"));
				teamImages.put(team, image);
			}

			// Get Original Phase Images
			for (int x = 1; x <= 5; x++) {
				BufferedImage image = ImageIO.read(getResource("Images/Phase" + x + ".png"));
				phaseImages.put(Integer.valueOf(x), image);
			}

			// Get Original GameEnd Image
			gameEndImage = ImageIO.read(getResource("Images/GameEnd.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get Images Folder
		File imagesFolder = AnniSubDirectory.IMAGES_DIRECTORY.getDirectory ( ); // new File(getDataFolder(), "Images");
		if (!imagesFolder.exists() || !imagesFolder.isDirectory()) {
			imagesFolder.mkdir();
			return;
		}
		
		// save default team images.
		for (AnniTeam team : AnniTeam.Teams) {
			// get image file and check is not already exists.
			File image_file = new File(imagesFolder, team.getName() + "Team.png");
			if (image_file.exists()) {
				continue;
			}

			// save default image.
			try {
				// write.
				ImageIO.write(teamImages.get(team), "png", image_file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// save default phase images.
		for (int x = 1; x <= 5; x++) {
			// get image file and check is not already exists.
			File image_file = new File(imagesFolder, "Phase" + x + ".png");
			if (image_file.exists()) {
				continue;
			}

			// save default image.
			try {
				// write.
				ImageIO.write(phaseImages.get(x), "png", image_file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// save default end game image.
		File end_image_file = new File(imagesFolder, "GameEnd.png");
		if (!end_image_file.exists()) {
			// save default image.
			try {
				// write.
				ImageIO.write(gameEndImage, "png", end_image_file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Get files in folder
		File[] files = imagesFolder.listFiles();
		for (int x = 0; x < files.length; x++) {
			// Get file
			File f = files[x];

			// Check is not null
			if (f == null) {
				continue;
			}

			// Check is not a Directory, and check is a .png file.
			if (f.isDirectory() || !f.getName().endsWith(".png")) {
				continue;
			}

			// Geting final Image
			// ImageMessage finalImg = null;
			BufferedImage finalImg = null;
			try {
				// Get BufferedImage from file
				BufferedImage bfImg = ImageIO.read(f);

				// Check is not a null BufferedImage
				if (bfImg == null) {
					continue;
				}

				// Check has a valid Height
				if (bfImg.getHeight() != 10) {
					Util.print(ChatColor.RED, "Invalid Image dimensions: '" + f.getName() + "'");
					Util.print(ChatColor.RED, "The images dimensions must be 10x10");
					continue;
				}

				// Set
				finalImg = bfImg;
			} catch (Throwable e) {
				Bukkit.getConsoleSender().sendMessage(
						Util.wc("&c[Annhilation] Failed on Attempt to Load the image: '" + f.getName() + "'"));
				continue;
			}

			// Variables
			boolean continuarComoTeamImage = false;
			boolean continuarComoPhaseImage = false;
			boolean continuarComoEndImage = false;
			AnniTeam imageTeam = null;
			Integer imagePhase = null;

			// Get Team from image Name
			for (AnniTeam team : AnniTeam.Teams) {
				if (f.getName().equalsIgnoreCase(team.getName() + "Team.png")
						|| f.getName().equalsIgnoreCase(team.getName() + ".png")) {
					// Continue as Team Image
					continuarComoTeamImage = true;

					// Get image Team
					imageTeam = team;
				}
			}

			// Save
			if (continuarComoTeamImage && imageTeam != null) {
				// Set image as Team Image
				if (finalImg != null) {
					teamImages.put(imageTeam, finalImg);
				}

				// Next!!
				continue;
			}

			// Get Phase Images
			String fileName = f.getName().replace(".png", "");
			for (int p = 1; p < 6; p++) {
				if (fileName.equalsIgnoreCase("Phase" + p) || fileName.equalsIgnoreCase("Fase" + p)
						|| fileName.equalsIgnoreCase(String.valueOf(p)) || fileName.equalsIgnoreCase("Phase " + p)
						|| fileName.equalsIgnoreCase("Fase " + p)) {
					// Get Phase Number
					imagePhase = Integer.valueOf(p);

					// Continue as Phase Image
					continuarComoPhaseImage = true;
				}
			}

			// Save
			if (continuarComoPhaseImage && imagePhase != null) {
				// Set image as Phase Image
				if (finalImg != null) {
					phaseImages.put(imagePhase, finalImg);
				}

				// Next!!
				continue;
			}

			// Get GameEnd Image
			if (fileName.equalsIgnoreCase("GameEnd") || fileName.equalsIgnoreCase("EndGame")
					|| fileName.equalsIgnoreCase("Game End") || fileName.equalsIgnoreCase("End Game")
					|| fileName.equalsIgnoreCase("Fin") || fileName.equalsIgnoreCase("End")) {
				// Continue as GameEnd Image
				continuarComoEndImage = true;
			}

			// Save
			if (continuarComoEndImage) {
				// Set
				gameEndImage = finalImg;

				// Next!!
				continue;
			}
		}
	}

	// Team Images Getter
	public BufferedImage getTeamBufferedImage(AnniTeam team) {
		return team != null ? teamImages.get(team) : null;
	}

	// Phase Images Getter
	public BufferedImage getPhaseBufferedImage(int phase) {
		return (phase > 0 && phase <= 5) ? phaseImages.get(Integer.valueOf(phase)) : null;
	}

	private void loadKitsMenu() {
		File f = new File(getDataFolder(), "AnniKitsMenuConfig.yml");
		if (!f.exists()) {
			try {
				getDataFolder().mkdir();
				f.createNewFile();
			} catch (IOException e) {
				Util.print(ChatColor.RED + "Couldn't create kits menu config file. Disabling..");
				Bukkit.getPluginManager().disablePlugin(this);
				e.printStackTrace();
			}
		}

		YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
		String spliter = "\r\n";
		String header = "";
		for (GlassColor gc : GlassColor.values()) {
			header += (spliter + gc.name());
		}
		conf.options().header("Glass Pane Colors: " + header);

		int save = 0;
		for (KitsMenuConfig item : KitsMenuConfig.values()) {
			if (conf.getString(item.getPath()) == null || !conf.isSet(item.getPath())) {
				conf.set(item.getPath(), item.getDefault());
				save++;
			}
		}

		KitsMenuConfig.setFile(conf);
		try {
			if (save > 0) {
				conf.save(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadLang() {
		File lang = new File(getDataFolder(), "LanguageConfig.yml");
		//
		if (!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Util.print(ChatColor.RED, "&4&l[Annihilation] Couldn't create language file.");
				Util.print(ChatColor.RED, "&4&l[Annihilation] This is a fatal error. Now disabling");
				this.setEnabled(false);
			}
		}
		//
		Utf8YamlConfiguration conf = Utf8YamlConfiguration.loadConfiguration(lang);
		conf.options()
				.header("This is the language config file. "
						+ "\r\nThe: %# will be replaced with a number when needed. "
						+ "\r\nThe: %w will be replaced with a word when needed. "
						+ "\r\nThe: %tc will be replaced with the team color when needed. "
						+ "\r\nThe: %n is the line separator. " + "\r\nNormal MC color codes are supported.");
		//
		for (Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		//
		Lang.setFile(conf);
		//
		try {
			conf.save(lang);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean ConfigManager() {
		final File mainConfig = new File(this.getDataFolder(), "AnniMainConfig.yml");
		if (!mainConfig.exists()) {
			try {
				getDataFolder().mkdir();
				mainConfig.createNewFile();
			} catch (IOException e) {
				Util.print(ChatColor.RED, "Could not load the main config file!");
				e.printStackTrace();
				return false;
			}
		}
		
		// Get and check yml
		MAIN_YML = Utf8YamlConfiguration.loadConfiguration(mainConfig);
		if (MAIN_YML == null) {
			Util.print(ChatColor.RED, "Could not load the main config yml!");
			return true;
		}
		
		int save = 0;
		for (Config item : Config.values()) {
			if (!MAIN_YML.isSet(item.getPath()) || MAIN_YML.get(item.getPath()) == null) {
				MAIN_YML.set(item.getPath(), item.getDefault());
				save ++;
			}
		}
		
		// Set File
		Config.setFile(MAIN_YML);
		
		// Save
		if (save > 0) {
			try {
				MAIN_YML.save(mainConfig);
			} catch (IOException e) {
				Util.print(ChatColor.RED, "Could not save defaults in the main config yml!");
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void saveMainYML() {
		final File mainConfig = new File(this.getDataFolder(), "AnniMainConfig.yml");
		if (!mainConfig.exists()) {
			try {
				getDataFolder().mkdir();
				mainConfig.createNewFile();
			} catch (IOException e) {
				return;
			}
		}
		
		if (MAIN_YML != null) {
			try {
				MAIN_YML.save(mainConfig);
			} catch (IOException e) {
			}
		}
	}

	private void buildAnniCommand() {
		AnniCommand.registerArgument(new AnniArgument() {
			@Override
			public String getHelp() {
				return ChatColor.LIGHT_PURPLE + "Start--" + ChatColor.GREEN + "Starts a game of Annihilation.";
			}

			@Override
			public boolean useByPlayerOnly() {
				return false;
			}

			@Override
			public String getArgumentName() {
				return "Start";
			}

			@Override
			public void executeCommand(CommandSender sender, String label, String[] args) {
				if (!Game.isGameRunning()) {
					if (Game.startGame()) {
						sender.sendMessage(ChatColor.GREEN + "[Annihilation] The game has begun!");
					} else {
						sender.sendMessage(ChatColor.RED + "[Annihilation] The game was not started!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "[Annihilation] The game is already running.");
				}
			}

			@Override
			public String getPermission() {
				return null;
			}

			@Override
			public MenuItem getMenuItem() {
				return new ActionMenuItem(Util.wc("&6&lStart Game"), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						executeCommand(event.getPlayer(), null, null);
						event.setWillClose(true);
					}
				}, new ItemStack(Material.FEATHER), Game.isGameRunning() ? Util.wc("&4&lThe game is already running.!")
						: Util.wc("&6Click to start the game."));
			}
		});

		AnniCommand.registerArgument(new AnniArgument() {
			@Override
			public String getHelp() {
				return ChatColor.LIGHT_PURPLE + "Mapbuilder--" + Util.wc("&6Gives the mapbuilder item.");
			}

			@Override
			public boolean useByPlayerOnly() {
				return true;
			}

			@Override
			public String getArgumentName() {
				return "Mapbuilder";
			}

			@Override
			public void executeCommand(CommandSender sender, String label, String[] args) {
				if (sender instanceof Player) {
					((Player) sender).getInventory().addItem(CustomItem.MAPBUILDER.toItemStack(1));
				}
			}

			@Override
			public String getPermission() {
				return null;
			}

			@Override
			public MenuItem getMenuItem() {
				return new ActionMenuItem(Util.wc("&6&lGet Mapbuilder"), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						executeCommand(event.getPlayer(), null, null);

					}
				}, new ItemStack(Material.DIAMOND_PICKAXE), Util.wc("&6Click to get the mapbuilder item."));
			}
		});

		AnniCommand.registerArgument(new AnniArgument() {
			@Override
			public String getHelp() {
				return ChatColor.LIGHT_PURPLE + Util.wc("Save--&6[Lobby,config,Map,All]");
			}

			@Override
			public boolean useByPlayerOnly() {
				return false;
			}

			@Override
			public String getArgumentName() {
				return "Save";
			}

			@Override
			public void executeCommand(CommandSender sender, String label, String[] args) {
				if (args != null && args.length > 0) {
					if (args[0].equalsIgnoreCase("Lobby")) {
						if (Game.LobbyMap != null) {
							Game.LobbyMap.saveToConfig(true, true);
							sender.sendMessage(
									ChatColor.GREEN + Util.wc("&6Lobby Saved: ") + Game.LobbyMap.getWorldName());
						} else
							sender.sendMessage(ChatColor.RED + "You have not created any lobby!");
					} else if (args[0].equalsIgnoreCase("BossMap")) {
						if (GameBoss.getBossMap() != null) {
							// Saver World
							Bukkit.getWorld(GameBoss.getBossMap().getWorldName()).save();
							sender.sendMessage(ChatColor.GREEN + "Boss Map Saved!");

							// Set as default boss map
							GameBoss.getBossMap().getConfig().set("WorldName", GameBoss.getBossMap().getWorldName());

							// Save Boss
							if (GameBoss.getBoss() != null) {
								ConfigurationSection Boss = GameBoss.getBossMap().getConfig()
										.createSection("BossConfiguration");
								ConfigurationSection spawn = Boss.createSection("BossSpawnPoint");

								if (GameBoss.getBoss().getSpawn() != null) {
									GameBoss.getBoss().getSpawn().saveToConfig(spawn);
								}

								if (GameBoss.getBoss().getName() != null) {
									Boss.set("Name", GameBoss.getBoss().getName());
								}

								Boss.set("Respawn Time", GameBoss.getBoss().getRespawnTime());
								if (GameBoss.getBoss().getRespawnUnit() != null) {
									Boss.set("Unit", GameBoss.getBoss().getRespawnUnit().name());
								}

								Boss.set("Health", GameBoss.getBoss().getVida());
								if (GameBoss.getBoss().getType() != null) {
									if (GameBoss.getBoss().getType().equals(Wither.class)) {
										Boss.set("Type", "Wither");
									} else {
										Boss.set("Type", "IronGolem");
									}
								}
								
								// save rewards
								final ConfigurationSection rewar = Boss.createSection ( "Rewards" );
								
								rewar.set ( "InventorySize" , GameBoss.getBoss ( ).getRewardsInventorySize ( ) );
								
								if ( GameBoss.getBoss ( ).getRewards ( ) != null ) {
									for ( int x = 0 ; x < GameBoss.getBoss ( ).getRewards ( ).length ; x ++ ) {
										ItemStack stack = GameBoss.getBoss ( ).getRewards ( ) [ x ];
										if ( stack != null ) {
											rewar.set ( String.valueOf ( x /* x as the slot */ ) , stack );
//											GameBackup.saveItem(st, x, rewar);
										}
									}
								}

								// Save map config
								GameBoss.getBossMap().saveTheConfig();

								// Create cache
								checkAnniMapsCacheFile(GameBoss.getBossMap().getNiceBossWorldName(), false);
							}
						} else
							sender.sendMessage(ChatColor.RED + "You do not have a boss map loaded!");
					} else {
						if (Game.getGameMap() != null) {
							final GameMap map = Game.getGameMap();
							String name = map.getNiceWorldName();
							//

							if (args[0].equalsIgnoreCase("config")) {
								map.saveToConfig(true, true);
								map.backupConfig();
								//
								sender.sendMessage(
										ChatColor.GREEN + Util.wc("&6Saved ") + name + Util.wc("&6 (config)"));
								//
								checkAnniMapsCacheFile(Game.getGameMap().getNiceWorldName(), true);
							} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("map")) {
								Game.getGameMap().backUpWorld();
								sender.sendMessage(ChatColor.GREEN + Util.wc("&6Saved ") + name + " (World)");
								//
								checkAnniMapsCacheFile(Game.getGameMap().getNiceWorldName(), true);
							} else if (args[0].equalsIgnoreCase("both") || args[0].equalsIgnoreCase("all")) {
								map.saveToConfig(true, true);
								//
								Game.getGameMap().backupConfig();
								Game.getGameMap().backUpWorld();
								sender.sendMessage(Util.wc("&6" + name + " &6Saved, Config and World"));
								//
								checkAnniMapsCacheFile(Game.getGameMap().getNiceWorldName(), true);
							}
						} else
							sender.sendMessage(ChatColor.RED + "You do not have a game map loaded!");
					}
				}
			}

			@Override
			public String getPermission() {
				return null;
			}

			@Override
			public MenuItem getMenuItem() {
				return new ActionMenuItem(Util.wc("&6&lSave"), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						if (event.getClickType() == ClickType.LEFT)
							executeCommand(event.getPlayer(), null, new String[] { "Config" });
						else if (event.getClickType() == ClickType.RIGHT)
							executeCommand(event.getPlayer(), null, new String[] { "World" });
						else if (event.getClickType() == ClickType.SHIFT_LEFT
								|| event.getClickType() == ClickType.SHIFT_RIGHT)
							executeCommand(event.getPlayer(), null, new String[] { "All" });
					}
				}, new ItemStack(Material.ANVIL), Util.wc("&6Left click to save the Map Config."),
						Util.wc("&6Right click to save the Map World."),
						Util.wc("&6Shift + Click to save Both. (Config and World)"));
			}
		});
	}

	public void checkAnniMapsCacheFile(String mapName, boolean normalMap) {
		final String wfn = normalMap ? AnniSubDirectory.WORLDS_DIRECTORY.getName ( ) 
				: AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getName ( );

		File anniMaps = new File(getDataFolder(), wfn);
		try {
			if (!anniMaps.exists())
				anniMaps.mkdir();
		} catch (Throwable e) {
		}

		File mapFolder = searchFolder(anniMaps, mapName);
		if (!mapFolder.exists()) {
			try {
				mapFolder.createNewFile();
			} catch (Throwable e) {
			}
		}

		if (mapFolder != null) {
			File cache = new File(mapFolder, "AnniMap.cache");
			if (!cache.exists()) {
				try {
					cache.createNewFile();
				} catch (IOException ParamException) {
					Bukkit.getConsoleSender()
							.sendMessage(Util.wc("&cFailed on attempt to create the map cache file, "
									+ "please re-save the configuration with this command, "
									+ " so that the configuration is not lost"));
				}
			}
		}
	}

	private static File searchFolder(File folder, String nameToFind) {
		for (File file : folder.listFiles()) {
			if (file.getName().equals(nameToFind)) {
				return file;
			}
		}
		return null;
	}

	private void handle_Voting_and_AutoStart() {
		if (Config.USE_AUTO_START.toBoolean()) {
			new AutoStarter(this, Config.AUTO_START_PLAYERS_TO_START.toInt(), Config.AUTO_START_COUNTDOWN_TIME.toInt());
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		// Check is valid Inventory
		if (event.getClickedInventory() == null || event.getInventory() == null) {
			return;
		}

		// Get Variables
		final ItemStack stack = event.getCurrentItem();
		final Player p = (Player) event.getWhoClicked();
		if (stack == null || stack.getItemMeta() == null) {
			return;
		}

		// Check has Meta
		if (!stack.getItemMeta().hasDisplayName()) {
			return;
		}

		// Check is any souldbound
		if (!KitUtils.isAnySoulbound(stack)) {
			return;
		}

		// Get Item
		if (KitUtils.itemHasName(stack, CustomItem.VOTEMAP.getName())) {
			event.setCancelled(true);
			MapVotingMenu.openMenu ( p );
		} else if (KitUtils.itemHasName(stack, CustomItem.TEAMMAP.getName())) {
			event.setCancelled(true);
//			if (!VersionUtils.getVersion().contains("11") && !VersionUtils.getVersion().contains("12")) {
				TeamCommand.openTeamGUI(p);
//			} else {
//				TeamCommandNew.openTeamGUI(p);
//			}
		}
		
//		if (KitUtils.itemHasName(stack, CustomItem.VOTEMAP.getName())) {
//			event.setCancelled(true);
//			LobbyScoreboard.openVoteGUI(p);
//		} else if (KitUtils.itemHasName(stack, CustomItem.TEAMMAP.getName())) {
//			event.setCancelled(true);
////			if (!VersionUtils.getVersion().contains("11") && !VersionUtils.getVersion().contains("12")) {
//				TeamCommand.openTeamGUI(p);
////			} else {
////				TeamCommandNew.openTeamGUI(p);
////			}
//		}
	}

	@Override
	public void onDisable() {
		try {
			// disable plugins
			for (AnniPlugin plugin : AnnihilationMain.PLUGINS.values()) {
				if (plugin != null) {
					getPluginManager().disablePlugin(plugin);
				}
			}
			
			for (AnniPlayer p : AnniPlayer.getPlayers()) {
				if (p != null && p.isOnline()) {
					// Cleanup Kit
					if (p.hasKit()) {
						p.getKit().cleanup(p.getPlayer());
					}

					try {
						// Remove Bar
						final AnnounceBar bar = AnnounceBar.getInstance();
						if (bar != null && bar.getBar() != null) {
							bar.getBar().removeBossBar(p.getPlayer());
						}
					}
					catch(Throwable t) {
						// ignore
					}
				}
			}

			// Unload GameMap
			String GameMapName = null;
			try {
				if (Game.getGameMap() != null) {
					GameMapName = Game.getGameMap().getNiceWorldName();
					Game.getGameMap().unLoadMap();
				}
			} catch (Exception e) {
				Util.print(ChatColor.RED, "Error on Unload GameMap.");
				Util.print(ChatColor.RED, "Do not use the command '/Reload'.");
				Util.print(ChatColor.RED, "Please use: '/Stop'");
			}

			String BossMap = null;
			// Unload BossMap
			try {
				if (GameBoss.getBossMap() != null) {
					BossMap = GameBoss.getBossMap().getNiceBossWorldName();
					GameBoss.getBossMap().unLoadMap();
				}
			} catch (Exception e) {
				Util.print(ChatColor.RED, "Error on Unload BossMap.");
				Util.print(ChatColor.RED, "Do not use the command '/Reload'.");
				Util.print(ChatColor.RED, "Please use: '/Stop'");
			}

			// Destroy Bar
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) { // VersionUtils.isNewSpigotVersion()
				try {
					BossBarHandler.destroyBar();
				} catch (BootstrapMethodError e) {
				}
			}

			// Cancel Taks in this plugin
			Bukkit.getScheduler().cancelTasks(this);

			// Send Info
			if (GameMapName != null || BossMap != null) {
				Bukkit.getLogger().info("[Annihilation] Removing Temp Files...");
			}

			// Rem Tem-Copy
			if (GameMapName != null) {
				// Remove
				File worldFols = AnniSubDirectory.WORLDS_DIRECTORY.getDirectory ( ); // new File(getDataFolder(), "Worlds");
				if (!worldFols.exists()) {
					return;
				}

				for (File f : worldFols.listFiles()) {
					if (f.isDirectory()) {
						if (f.getName().startsWith(GameMapName)) {
							if (f.getName().endsWith("-TempCopy")) {
								Util.forcedRemoveDir(f);
							}
						}
					}
				}
			}

			if (BossMap != null) {
				// Remove
				File worldFols = AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getDirectory ( ); // new File(getDataFolder(), "BossWorlds");
				if (!worldFols.exists()) {
					return;
				}

				for (File f : worldFols.listFiles()) {
					if (f.isDirectory()) {
						if (f.getName().startsWith(BossMap)) {
							if (f.getName().endsWith("-TempCopy")) {
								Util.forcedRemoveDir(f);
							}
						}
					}
				}
			}

			// Call PluginDisableEvent and send Disable Massage
			Bukkit.getPluginManager().callEvent(new PluginDisableEvent());
			Bukkit.getConsoleSender().sendMessage(Util.wc("&c&l&n[Annihilation] Disabled"));
		} catch (Throwable e) {
			// Ignore
		}
	}
	
	public static AnniPluginManager getPluginManager() {
		return PLUGIN_MANAGER;
	}
	
	public static Map<String, AnniPlugin> getPlugins() {
		return PLUGINS;
	}
	
	public void clearPlugins(Map<Pattern, AnniPluginLoader> fileAssociations) {
		synchronized (PLUGIN_MANAGER) {
			PLUGIN_MANAGER.disablePlugins();
			PLUGINS.clear();
			HandlerList.unregisterAll(INSTANCE);
			fileAssociations.clear();
		}
	}

	// ---------------------Main Configuration Values--------------------- //
	private void loadMainValues() {
		// Create Worlds Folder if not Exists
		File worldFolder = AnniSubDirectory.WORLDS_DIRECTORY.getDirectory ( ); // new File(getDataFolder().getAbsolutePath() + "/Worlds");
		if (!worldFolder.exists()) {
			worldFolder.mkdir();
		}

		// Create Worlds Folder if not Exists
		File BossworldFolder = AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getDirectory ( ); // new File(getDataFolder().getAbsolutePath() + "/BossWorlds");
		if (!BossworldFolder.exists()) {
			BossworldFolder.mkdir();
		}
		
		// Check is not lobby mode
		if (Config.LOBBY_MODE.toBoolean()) {
			return;
		}
		
		// Chat Vars
		YamlConfiguration chatConfig = ChatConfigManager.getConfig();
		if (chatConfig != null) {
			ChatVars.loadChatVars(chatConfig);
		}

		// Shop Vars
		YamlConfiguration shopConfig = ShopConfigManager.getConfiguration();
		if (shopConfig != null) {
			ShopVars.loadVars(shopConfig);
		}
		
		// GameBeacon Loader
		GameBeaconLoader.load(this);

		// Load Lobby from File
		File lobbyFile = new File(this.getDataFolder(), "AnniLobbyConfig.yml");
		if (lobbyFile.exists()) {
			Game.LobbyMap = new LobbyMap(lobbyFile, true);
			Game.LobbyMap.registerListeners(this);
		}
	}
	
	@Override
	public void initManagers ( ) {
		/* lobby scoreboard */
		new LobbyScoreboardHandler ( this );
		new LobbyScoreboardConfigurationHandler ( this );
		LobbyScoreboardHandler.getInstance ( ).startUpdating ( );
		MapVoting.prepareVoting ( this );
		
		/* game scoreboard */
		new GameScoreboardHandler ( this );
		new GameScoreboardConfigurationHandler ( this );
		
		/* name tags */
		new NametagsManager ( this );
	}
	
	@Override
	public CoreVersion getRequiredCoreVersion() {
		return null;
	}

	@Override
	public String getRequiredCoreVersionMessage() {
		return null;
	}

	@Override
	public PluginDependence[] getDependencies() {
		return null;
	}

	@Override
	public Class<? extends Enum<? extends CustomPluginLanguageEnumContainer>> getLanguageContainer() {
		return null;
	}

	@Override
	public String getLanguageResourcesPackage() {
		return null;
	}
}