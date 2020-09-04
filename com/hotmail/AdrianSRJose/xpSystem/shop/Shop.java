package com.hotmail.AdrianSRJose.xpSystem.shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.xpSystem.main.XPSystem;

public class Shop implements CommandExecutor {
	static String purchasedMessage;
	static String forsaleMessage;
	static String confirmMessage;
	static String confirmExpired;
	public static String notEnoughXP;
	static String kitPurchased;
	String noKitsToPurchase;
	private KitShopMenuItem[] items;
	private final Map<UUID, ItemMenu> menus;

	public Shop(XPSystem system, ConfigurationSection shopSection) {
		menus = new HashMap<UUID, ItemMenu>();
		purchasedMessage = getString(shopSection, "Already-Purchased-Kit");
		forsaleMessage = getString(shopSection, "Not-Yet-Purchased-Kit");
		confirmMessage = getString(shopSection, "Confirm-Purchase-Kit");
		confirmExpired = getString(shopSection, "Confirmation-Expired");
		notEnoughXP = getString(shopSection, "Not-Enough-XP");
		kitPurchased = getString(shopSection, "Kit-Purchased");
		noKitsToPurchase = getString(shopSection, "No-Kits-To-Purchase");
		if (!shopSection.isConfigurationSection("Kits"))
			shopSection.createSection("Kits");
		ConfigurationSection kitSec = shopSection.getConfigurationSection("Kits");
		Collection<Kit> kits = Kit.getKits();
		items = new KitShopMenuItem[kits.size()];
		int c = 0;
		for (Kit k : kits) {
			// Set Defaults
			Util.setDefaultIfNotSet(kitSec, (k.getName() + ".Price"), 10000);
			Util.setDefaultIfNotSet(kitSec, (k.getName() + ".CanBeBuyed"), true);

			// Get Config Values
			int price = kitSec.getInt(k.getName() + ".Price");
			boolean canBeBuyed = kitSec.getBoolean(k.getName() + ".CanBeBuyed");

			// Check is enabled
			if (!canBeBuyed) {
				continue;
			}

			// Set
			KitShopMenuItem item = new KitShopMenuItem(new KitWrapper(k, price), system);
			items[c] = item;
			c++;
		}
	}

	private String getString(ConfigurationSection section, String path) {
		return ChatColor.translateAlternateColorCodes('&', section.getString(path));
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
			for (KitShopMenuItem m : items) {
				if (m != null) {
					if (!m.getKit().hasPermission(player)) {
						menu.setItem(counter, m);
						counter++;
					}
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
}
