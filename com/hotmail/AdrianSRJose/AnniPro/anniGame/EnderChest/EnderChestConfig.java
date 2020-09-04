package com.hotmail.AdrianSRJose.AnniPro.anniGame.EnderChest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.GlassColor;
import com.hotmail.AdrianSRJose.AnniPro.utils.Utf8YamlConfiguration;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;

public class EnderChestConfig {
	private final Utf8YamlConfiguration file;
	private final @Getter boolean enabled;
	private final @Getter boolean useCommand;
	private final @Getter String name;
	private final @Getter int slots;
	private final @Getter List<ItemFromSection> items;

	public EnderChestConfig(JavaPlugin pl) throws IOException {
		if (!pl.getDataFolder().exists()) {
			pl.getDataFolder().mkdir();
		}

		File chestConfig = new File(pl.getDataFolder(), "AnniEnderChestConfig.yml");
		if (!chestConfig.exists()) {
			chestConfig.createNewFile();
		}

		file = Utf8YamlConfiguration.loadConfiguration(chestConfig);
		items = new ArrayList<ItemFromSection>();

		// Set Defaults
		int save = Util.setDefaultIfNotSet(file, "Enable", true);
		save += Util.setDefaultIfNotSet(file, "Name", "&4&lEnder Chest");
		save += Util.setDefaultIfNotSet(file, "CanBeOpenWithCommnad", true);
		save += Util.setDefaultIfNotSet(file, "Slots", Size.THREE_LINE.getSize());
		if (!file.isConfigurationSection("Items")) {
			// Get Configuration Section
			save += Util.createSectionIfNoExitsInt(file, "Items");
			ConfigurationSection cs = file.getConfigurationSection("Items");

			// Save Default Items
			ItemFromSection[] defItems = new ItemFromSection[] {
					new ItemFromSection("&4Blocked Slot", Material.STAINED_GLASS_PANE, 1, (1 + 9), GlassColor.RED,
							Arrays.asList(new String[] { "&cYou cant use this slot" }), true, "enderchest.slot9"),
					new ItemFromSection("&4Blocked Slot", Material.STAINED_GLASS_PANE, 1, (3 + 9), GlassColor.RED,
							Arrays.asList(new String[] { "&cYou cant use this slot" }), true, "enderchest.slot11"),
					new ItemFromSection("&4Blocked Slot", Material.STAINED_GLASS_PANE, 1, (5 + 9), GlassColor.RED,
							Arrays.asList(new String[] { "&cYou cant use this slot" }), true, "enderchest.slot13"),
					new ItemFromSection("&4Blocked Slot", Material.STAINED_GLASS_PANE, 1, (7 + 9), GlassColor.RED,
							Arrays.asList(new String[] { "&cYou cant use this slot" }), true, "enderchest.slot15") };

			int count = 0;
			for (int x = 0; x < Size.THREE_LINE.getSize(); x++) {
				if (x != (1 + 9) && x != (3 + 9) && x != (5 + 9) && x != (7 + 9)) {
					new ItemFromSection(".", Material.STAINED_GLASS_PANE, 1, x, GlassColor.WITHE,
							Arrays.asList(new String[] {}), false, "").save(cs.createSection("Item" + count));
					count++;
				}
			}

			for (ItemFromSection adi : defItems) {
				save += adi.save(cs.createSection("Item" + count));
				count++;
			}
		}

		// Save Defaults
		if (save > 0) {
			file.save(chestConfig);
		}

		// load From Config
		enabled = file.getBoolean("Enable");
		useCommand = file.getBoolean("CanBeOpenWithCommnad");
		name = file.getString("Name");
		slots = file.getInt("Slots");
		ConfigurationSection cs = file.getConfigurationSection("Items");
		if (cs != null) {
			for (String key : cs.getKeys(false)) {
				ConfigurationSection itemSection = cs.getConfigurationSection(key);
				if (itemSection != null) {
					ItemFromSection item = new ItemFromSection(itemSection);
					if (item != null) {
						// Get Permission
						if (item.isRequiresPermission() && item.getPermission() != null
								&& !item.getPermission().isEmpty()) {
							// Get Premission
							Permission perm = new Permission(Util.remC(item.getPermission()));
							boolean registre = true;

							// Chek dont exists
							for (Permission another : Bukkit.getPluginManager().getPermissions()) {
								if (another != null && another.getName() != null
										&& another.getName().equalsIgnoreCase(perm.getName())) {
									registre = false;
								}
							}

							// Registre
							if (registre) {
								perm.setDefault(PermissionDefault.FALSE);
								Bukkit.getPluginManager().addPermission(perm);
								perm.recalculatePermissibles();
							}
						}

						// Add To List
						items.add(item);
					}
				}
			}
		}
	}
}
