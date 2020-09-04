package com.hotmail.AdriaSRJose.vaultPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hotmail.AdriaSRJose.vaultPlugin.database.MySQL;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class Shop implements Listener, CommandExecutor {
	public static String purchasedMessage;
	public static String forsaleMessage;
	public static String confirmMessage;
	public static String confirmExpired;
	public static String notEnoughXP;
	public static String kitPurchased;
	String noKitsToPurchase;
	private ShopItem[] items;
	private final Map<UUID, ItemMenu> menus;
	public final VaultHook instance;
	public final MySQL sql;

	public Shop(ConfigurationSection sc, final VaultHook instance, final MySQL sql) {
		menus = new HashMap<UUID, ItemMenu>();
		this.sql = sql;
		this.instance = instance;
		Bukkit.getPluginManager().registerEvents(this, instance);
		instance.getCommand("KitShop").setExecutor(this);
		
		// Load Online Players Kits
		for (Player p : Bukkit.getOnlinePlayers()) {
			sql.loadKits(p);
		}
	
		// Get Config vals
		int s = 0;
		purchasedMessage = getString(sc, "Already-Purchased-Kit");
		forsaleMessage = getString(sc, "Not-Yet-Purchased-Kit");
		confirmMessage = getString(sc, "Confirm-Purchase-Kit");
		confirmExpired = getString(sc, "Confirmation-Expired");
		notEnoughXP = getString(sc, "Not-Enough-XP");
		kitPurchased = getString(sc, "Kit-Purchased");
		noKitsToPurchase = getString(sc, "No-Kits-To-Purchase");
		
		if (!sc.isConfigurationSection("Kits")) {
			sc.createSection("Kits");
			s++;
		}
		
		ConfigurationSection kitSec = sc.getConfigurationSection("Kits");
		Collection<Kit> kits = Kit.getKits();
		for (Kit k : kits) {
			// Set Defaults
			s += Util.setDefaultIfNotSet(kitSec, (k.getName() + ".Price"), 1000);
			s += Util.setDefaultIfNotSet(kitSec, (k.getName() + ".CanBeBuyed"), true);
		}
		
		if (s > 0) {
			instance.saveConfig();
		}
		
		items = new ShopItem[kits.size()];
		int c = 0;
		for (Kit k : kits) {
			// Get Config Values
			int price = kitSec.getInt(k.getName() + ".Price");
			boolean canBeBuyed = kitSec.getBoolean(k.getName() + ".CanBeBuyed");

			// Check is enabled
			if (!canBeBuyed) {
				continue;
			}

			// Set
			ShopItem item = new ShopItem(new KitWrapper(k, price), this);
			items[c] = item;
			c++;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemMenu menu = menus.get(player.getUniqueId());
			if (menu == null) {
				menu = new ItemMenu(player.getName() + " Kit Shop", Size.fit(items.length));
				menus.put(player.getUniqueId(), menu);
			}

			menu.clearAllItems();
			int counter = 0;
			for (ShopItem m : items) {
				// Check
				if (m == null || m.getWrapper() == null || m.getWrapper().kit == null) {
					continue;
				}

				if (!m.getWrapper().kit.hasPermission(player)) {
					menu.setItem(counter, m);
					counter++;
				}
			}

			if (counter == 0) {
				sender.sendMessage(this.noKitsToPurchase);
			} else {
				menu.open(player);
			}
		}
		return true;
	}

	private String getString(ConfigurationSection section, String path) {
		return ChatColor.translateAlternateColorCodes('&', section.getString(path));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent eve) {
		// Load Kits
		sql.loadKits(eve.getPlayer());
	}
}
