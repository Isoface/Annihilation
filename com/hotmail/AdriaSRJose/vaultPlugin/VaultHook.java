package com.hotmail.AdriaSRJose.vaultPlugin;

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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdriaSRJose.vaultPlugin.database.BaseData;
import com.hotmail.AdriaSRJose.vaultPlugin.database.MySQL;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Perm;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultHook extends JavaPlugin {
	private Economy econ;
	private YamlConfiguration config;
	private File configFile;
	private List<Perm> perms;

	@Override
	public void onEnable() {
		final PluginManager manager = Bukkit.getPluginManager();
		final Plugin anni = manager.getPlugin("Annihilation");
		final Plugin vault = manager.getPlugin("Vault");
		if (anni == null || !anni.isEnabled()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[AnnihilationVault] Annihilaton Not Found");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[AnnihilationVault] Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (vault == null || !vault.isEnabled()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[AnnihilationVault] Vault Not Found");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[AnnihilationVault] Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (!verifyEcon()) {
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "[AnnihilationVault] Error! Could not find a economy plugin on your server.");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[AnnihilationVault] Disabling...");
			;
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[AnnihilationVault] Successfully hooked into Vault");
		configFile = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath(),
				"AnnihilationVaultConfig.yml");
		checkFile(configFile);
		config = YamlConfiguration.loadConfiguration(configFile);

		int x = 0;
		x += Util.setDefaultIfNotSet(config, "Nexus-Hit-Money", 1.00D);
		x += Util.setDefaultIfNotSet(config, "Player-Kill-Money", 3.00D);
		x += Util.setDefaultIfNotSet(config, "Winning-Team-Money", 100.00D);
		x += Util.setDefaultIfNotSet(config, "Second-Place-Team-Money", 75.00D);
		x += Util.setDefaultIfNotSet(config, "Third-Place-Team-Money", 50.00D);
		x += Util.setDefaultIfNotSet(config, "Last-Place-Team-Money", 25.00D);
		x += Util.setDefaultIfNotSet(config, "Gave-Money-Message", "&a+%# Annihilation XP");
		if (!config.isConfigurationSection("Money-Multipliers")) {
			ConfigurationSection multipliers = config.createSection("Money-Multipliers");
			multipliers.set("Multiplier-1.Permission", "Anni.Money.2");
			multipliers.set("Multiplier-1.Multiplier", 2);
			x++;
		}
		x += Util.createSectionIfNoExitsInt(config, "Kit-Shop");
		
		if (x > 0) {
			saveConfig();
		}

		loadMultipliers(config.getConfigurationSection("Money-Multipliers"));
		loadXPVars(config);
		loadShop(config.getConfigurationSection("Kit-Shop"));
	}
	
	private void loadShop(ConfigurationSection sc) {
		// Save Defaults
		int s = 0;
		s += Util.setDefaultIfNotSet(sc, "On", false);
		s += Util.setDefaultIfNotSet(sc, "Database.Host", "Test");
		s += Util.setDefaultIfNotSet(sc, "Database.Port", "Test");
		s += Util.setDefaultIfNotSet(sc, "Database.Database", "Test");
		s += Util.setDefaultIfNotSet(sc, "Database.Username", "Test");
		s += Util.setDefaultIfNotSet(sc, "Database.Password", "Test");
		s += Util.setDefaultIfNotSet(sc, "Already-Purchased-Kit", "&aPURCHASED");
		s += Util.setDefaultIfNotSet(sc, "Not-Yet-Purchased-Kit", "&cLOCKED. PURCHASE FOR &6%# &cXP");
		s += Util.setDefaultIfNotSet(sc, "Confirm-Purchase-Kit",
				"&aPUCHASE BEGUN. CONFIRM FOR &6%# &AXP");
		s += Util.setDefaultIfNotSet(sc, "Confirmation-Expired",
				"&cThe confirmation time has expired. Please try again.");
		s += Util.setDefaultIfNotSet(sc, "Not-Enough-XP",
				"&cYou do not have enough XP to purchase this kit.");
		s += Util.setDefaultIfNotSet(sc, "Kit-Purchased", "&aKit %w purchased!");
		s += Util.setDefaultIfNotSet(sc, "No-Kits-To-Purchase", "&cNo kits left to purchase!");
		if (s > 0) {
			saveConfig();
		}
		
		// Check shop Enabled
		boolean shopEnabled = sc.getBoolean("On");
		if (!shopEnabled) {
			return;
		}
		
		// Load MySQL
		final BaseData data = new BaseData(sc.getString("Database.Host"), sc.getString("Database.Database"), sc.getString("Database.Username"), sc.getString("Database.Password"), sc.getInt("Database.Port"));
		final MySQL sql     = new MySQL(data);
		sql.OpenConnection();
		if (!sql.CheckConnection()) {
			return;
		}
		
		// Load Shop
		new Shop(sc, this, sql);
	}

	private boolean verifyEcon() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}

		econ = rsp.getProvider();
		return econ != null;
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

	public EconomyResponse giveMoney(Player player, double amount) {
		return econ.depositPlayer(player, amount);
	}
	
	public EconomyResponse removeMoney(Player player, double amount) {
		return econ.withdrawPlayer(player, amount);
	}
	
	public double getBalance(Player player) {
		return econ.getBalance(player);
	}

	private void loadXPVars(ConfigurationSection section) {
		if (section != null) {
			double nexusHitXP = section.getDouble("Nexus-Hit-Money");
			double killXP = section.getDouble("Player-Kill-Money");
			String gaveXPMessage = section.getString("Gave-Money-Message");
			double[] teamXPs = new double[4];
			teamXPs[0] = section.getDouble("Winning-Team-Money");
			teamXPs[1] = section.getDouble("Second-Place-Team-Money");
			teamXPs[2] = section.getDouble("Third-Place-Team-Money");
			teamXPs[3] = section.getDouble("Last-Place-Team-Money");

			Listeners listeners = new Listeners(this, gaveXPMessage, killXP, nexusHitXP, teamXPs);
			try {
				Bukkit.getPluginManager().registerEvents(listeners, this);
			} catch (IllegalPluginAccessException e) {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.RED + "[AnnihilationVault] Imcompatible Economy Plugin");
			}
		}
	}

	private void loadMultipliers(ConfigurationSection multipliers) {
		assert multipliers != null;
		perms = new ArrayList<Perm>();

		if (multipliers == null) {
			return;
		}

		for (String key : multipliers.getKeys(false)) {
			ConfigurationSection multSec = multipliers.getConfigurationSection(key);
			String perm = multSec.getString("Permission");
			int multiplier = multSec.getInt("Multiplier");
			if (perm != null && !perm.isEmpty() && multiplier > 0) {
				Permission p = new Permission(perm);
				p.setDefault(PermissionDefault.FALSE);
				Bukkit.getPluginManager().addPermission(p);
				p.recalculatePermissibles();
				perms.add(new Perm(perm, multiplier));
			}
		}
		
		Collections.sort(perms);
	}

	public double checkMultipliers(Player player, double initialXP) {
		if (perms.size() > 0) {
			for (Perm p : perms) {
				if (player.hasPermission(p.perm)) {
					initialXP = initialXP * p.multiplier;
					break;
				}
			}
		}
		return initialXP;
	}
	
	public static String formatString(String string, int amount) {
		return ChatColor.translateAlternateColorCodes('&', string.replace("%#", "" + amount));
	}

	public static String formatString(String string, int amount, final Player p) {
		return ChatColor.translateAlternateColorCodes('&', string.replace("%#", "" + amount)).replace("%PLAYER%", p.getName());
	}
}
