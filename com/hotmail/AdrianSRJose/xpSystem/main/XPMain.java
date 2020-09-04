package com.hotmail.AdrianSRJose.xpSystem.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniCommand;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Perm;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.xpSystem.shop.Shop;

public final class XPMain extends JavaPlugin implements Listener {
	private XPSystem xpSystem;
	private YamlConfiguration config;
	private File configFile;
	public static boolean ShopIsEnabled;
	public static Shop shop;

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin anniPlugin = manager.getPlugin("Annihilation");
		if (anniPlugin == null || !anniPlugin.isEnabled()) {
			Bukkit.getConsoleSender().sendMessage("§c[AnnihilationXPSystem] Annihilation Not Found");
			Bukkit.getConsoleSender().sendMessage("§c[AnnihilationXPSystem] Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		configFile = new File(AnnihilationMain.INSTANCE.getDataFolder(), "AnnihilationXPConfig.yml");
		Bukkit.getConsoleSender().sendMessage("[AnnihilationXPSystem] Loading XP system...");
		Bukkit.getConsoleSender().sendMessage("§a§l[AnnihilationXPSystem] Enabled");
		Bukkit.getPluginManager().registerEvents(this, this);

		checkFile(configFile);
		config = YamlConfiguration.loadConfiguration(configFile);

		// %# to be replaced by the number
		int x = 0;
		x += Util.setDefaultIfNotSet(config, "Nexus-Hit-XP", 1);
		x += Util.setDefaultIfNotSet(config, "Player-Kill-XP", 3);
		x += Util.setDefaultIfNotSet(config, "Winning-Team-XP", 100);
		x += Util.setDefaultIfNotSet(config, "Second-Place-Team-XP", 75);
		x += Util.setDefaultIfNotSet(config, "Third-Place-Team-XP", 50);
		x += Util.setDefaultIfNotSet(config, "Last-Place-Team-XP", 25);
		x += Util.setDefaultIfNotSet(config, "Gave-XP-Message", "&a+%# Annihilation XP");
		x += Util.setDefaultIfNotSet(config, "Gift-XP-Message", "&a%PLAYER% give you %# Annihilation XP");
		x += Util.setDefaultIfNotSet(config, "MyXP-Command-Message", "&dYou have &a%#&d Annihilation XP.");
		if (!config.isConfigurationSection("XP-Multipliers")) {
			ConfigurationSection multipliers = config.createSection("XP-Multipliers");
			multipliers.set("Multiplier-1.Permission", "Anni.XP.2");
			multipliers.set("Multiplier-1.Multiplier", 2);
			x++;
		}

		ConfigurationSection data = config.getConfigurationSection("Database");
		if (data == null)
			data = config.createSection("Database");

		x += Util.setDefaultIfNotSet(data, "Host", "Test");
		x += Util.setDefaultIfNotSet(data, "Port", "Test");
		x += Util.setDefaultIfNotSet(data, "Database", "Test");
		x += Util.setDefaultIfNotSet(data, "Username", "Test");
		x += Util.setDefaultIfNotSet(data, "Password", "Test");

		ConfigurationSection shopSec = config.getConfigurationSection("Kit-Shop");
		if (shopSec == null) {
			shopSec = config.createSection("Kit-Shop");
			shopSec.createSection("Kits");
		}

		x += Util.setDefaultIfNotSet(shopSec, "On", false);
		x += Util.setDefaultIfNotSet(shopSec, "Already-Purchased-Kit", "&aPURCHASED");
		x += Util.setDefaultIfNotSet(shopSec, "Not-Yet-Purchased-Kit", "&cLOCKED. PURCHASE FOR &6%# &cXP");
		x += Util.setDefaultIfNotSet(shopSec, "Confirm-Purchase-Kit", "&aPUCHASE BEGUN. CONFIRM FOR &6%# &AXP");
		x += Util.setDefaultIfNotSet(shopSec, "Confirmation-Expired",
				"&cThe confirmation time has expired. Please try again.");
		x += Util.setDefaultIfNotSet(shopSec, "Not-Enough-XP", "&cYou do not have enough XP to purchase this kit.");
		x += Util.setDefaultIfNotSet(shopSec, "Kit-Purchased", "&aKit %w purchased!");
		x += Util.setDefaultIfNotSet(shopSec, "No-Kits-To-Purchase", "&cNo kits left to purchase!");

		if (x > 0)
			this.saveConfig();

		this.xpSystem = new XPSystem(config.getConfigurationSection("Database"));
		if (!this.xpSystem.isActive()) {
			Bukkit.getConsoleSender().sendMessage("§c§l[AnnihilationXPSystem] Could NOT connect to the database");
			disable();
			return;
		}
		Bukkit.getConsoleSender().sendMessage("§a§l[AnnihilationXPSystem] CONNECTED to the database");

		boolean useShop = config.getBoolean("Kit-Shop.On");
		shop = new Shop(this.xpSystem, config.getConfigurationSection("Kit-Shop"));
		if (useShop) {
			XPMain.ShopIsEnabled = useShop;
			Bukkit.getConsoleSender().sendMessage("§a§l[AnnihilationXPSystem] The shop is ENABLED");
			this.getCommand("Shop").setExecutor(shop);
			saveConfig();
		} else
			Bukkit.getConsoleSender().sendMessage("§e§l[AnnihilationXPSystem] The shop is DISABLED");

		loadMultipliers(config.getConfigurationSection("XP-Multipliers"));
		loadXPVars(config); // This also loads the listeners with the values they need
		AnniCommand.registerArgument(new XPArgument(xpSystem));
		AnniCommand.registerArgument(new KitArgument(xpSystem));

		// Load online players kits
		for (AnniPlayer p : AnniPlayer.getPlayers()) {
			if (p != null) {
				xpSystem.loadKits(p, null);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void loadKits(PlayerJoinEvent e) {
		final AnniPlayer p = AnniPlayer.getPlayer(e.getPlayer().getUniqueId());
		if (p != null) {
			xpSystem.loadKits(p, null);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void test(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		String[] args = e.getMessage().split(" ");
		if (args[0].equals("/test") && player.getName().equalsIgnoreCase("Mr_Little_Kitty")) {
			AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
			if (p != null) {
				Object obj = p.getData("Kits");
				if (obj != null) {
					if (obj instanceof List) {
						@SuppressWarnings("unchecked")
						List<String> list = (List<String>) obj;
						if (!list.isEmpty()) {
							for (String str : list)
								player.sendMessage("Has: " + str);
						} else
							player.sendMessage("Nope 4");
					} else
						player.sendMessage("Nope 3");
				} else
					player.sendMessage("Nope 2");
			} else
				player.sendMessage("Nope 1");
		}
	}

	public static boolean getuseShop() {
		return ShopIsEnabled;
	}

	private void checkFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void disable() {
		if (xpSystem != null)
			xpSystem.disable();
		Bukkit.getConsoleSender().sendMessage("§c§l[AnnihilationXPSystem] Disabling...");
		Bukkit.getPluginManager().disablePlugin(this);
	}

	public void loadXPVars(ConfigurationSection section) {
		assert section != null;

		int nexusHitXP = section.getInt("Nexus-Hit-XP");
		int killXP = section.getInt("Player-Kill-XP");
		String gaveXPMessage = section.getString("Gave-XP-Message");
		String giftXPMessage = section.getString("Gift-XP-Message");
		String myXPMessage = section.getString("MyXP-Command-Message");
		int[] teamXPs = new int[4];
		teamXPs[0] = section.getInt("Winning-Team-XP");
		teamXPs[1] = section.getInt("Second-Place-Team-XP");
		teamXPs[2] = section.getInt("Third-Place-Team-XP");
		teamXPs[3] = section.getInt("Last-Place-Team-XP");

		XPListeners listeners = new XPListeners(this.xpSystem, gaveXPMessage, killXP, nexusHitXP, teamXPs);
		MyXPCommand command = new MyXPCommand(this.xpSystem, myXPMessage);
		new GiveXpCommand(this, this.xpSystem, gaveXPMessage);
		new GiftXpCommand(this, this.xpSystem, giftXPMessage);

		// AnniEvent.registerListener(listeners);
		Bukkit.getPluginManager().registerEvents(listeners, this);

		// Set command executor
		this.getCommand("MyXP").setExecutor(command);
	}

	private static List<Perm> perms;

	public void loadMultipliers(ConfigurationSection multipliers) {
		assert multipliers != null;
		perms = new ArrayList<Perm>();
		// ConfigurationSection multipliers =
		// config.getConfigurationSection("XP-Multipliers");
		if (multipliers != null) {
			for (String key : multipliers.getKeys(false)) {
				ConfigurationSection multSec = multipliers.getConfigurationSection(key);
				String perm = multSec.getString("Permission");
				int multiplier = multSec.getInt("Multiplier");
				if (perm != null && !perm.equals("") && multiplier > 0) {
					Permission p = new Permission(perm);
					p.setDefault(PermissionDefault.FALSE);
					Bukkit.getPluginManager().addPermission(p);
					p.recalculatePermissibles();
					perms.add(new Perm(perm, multiplier));
				}
			}
			Collections.sort(perms);
		}
	}

	@Override
	public void onDisable() {
		if (xpSystem != null) {
			xpSystem.disable();
		}
	}

	public static String formatString(String string, int amount) {
		return ChatColor.translateAlternateColorCodes('&', string.replace("%#", "" + amount));
	}

	public static String formatString(String string, int amount, final Player p) {
		return ChatColor.translateAlternateColorCodes('&', string.replace("%#", "" + amount)).replace("%PLAYER%",
				p.getName());
	}

	public static int checkMultipliers(Player player, int initialXP) {
		if (perms.size() > 0) {
			for (Perm p : perms) {
				if (player.hasPermission(p.perm)) {
					initialXP = (int) Math.ceil(((double) initialXP) * p.multiplier);
					break;
				}
			}
		}
		return initialXP;
	}
}
