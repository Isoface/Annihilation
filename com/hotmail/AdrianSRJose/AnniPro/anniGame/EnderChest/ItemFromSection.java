package com.hotmail.AdrianSRJose.AnniPro.anniGame.EnderChest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.GlassColor;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;

public class ItemFromSection {
	private @Getter String name;
	private @Getter String material;
	private @Getter int ammount;
	private @Getter int slot;
	private @Getter List<String> lore;
	private @Getter GlassColor color;
	private @Getter boolean requiresPermission;
	private @Getter String permission;

	public ItemFromSection(ConfigurationSection sc) {
		if (sc != null) {
			if (sc.isString("Name"))
				name = Util.wc(sc.getString("Name"));

			if (sc.isString("Material")) {
				material = sc.getString("Material").toUpperCase();
			}

			if (sc.isInt("Ammount"))
				ammount = sc.getInt("Ammount");

			if (sc.isInt("Slot"))
				slot = sc.getInt("Slot", 0);

			if (sc.isString("Color")) {
				try {
					GlassColor ts = GlassColor.valueOf(sc.getString("Color").toUpperCase());
					if (ts != null) {
						color = ts;
					}
				} catch (Throwable t) {
					Util.print(ChatColor.RED, "Invalid Glass Color: '" + sc.getString("Color") + "'");
					color = GlassColor.RED;
				}
			}

			permission = sc.getString("Permission", "");
			requiresPermission = sc.getBoolean("RequiresPermission");

			// Get and Translate colors in lore
			this.lore = sc.getStringList("Lore");
			if (lore != null && !lore.isEmpty()) {
				for (int x = 0; x < lore.size(); x++) {
					lore.set(x, Util.wc(lore.get(x)));
				}
			}
		}
	}

	public ItemFromSection(String name, Material material, int ammount, int slot, GlassColor color, List<String> lore,
			boolean requiresPermission, String permission) {
		this.name = Util.wc(name);
		this.material = material.name();
		this.ammount = ammount;
		this.slot = slot;
		this.lore = lore != null ? lore : new ArrayList<String>();
		this.color = color;
		this.requiresPermission = requiresPermission;
		this.permission = permission;
	}

	public int save(ConfigurationSection sc) {
		return Util.setDefaultIfNotSet(sc, "Name", name != null ? Util.untranslateAlternateColorCodes(name) : name)
				+ Util.setDefaultIfNotSet(sc, "Material", material) + Util.setDefaultIfNotSet(sc, "Ammount", ammount)
				+ Util.setDefaultIfNotSet(sc, "Slot", slot)
				+ Util.setDefaultIfNotSet(sc, "RequiresPermission", requiresPermission)
				+ Util.setDefaultIfNotSet(sc, "Permission", (permission != null ? permission : ""))
				+ Util.setDefaultIfNotSet(sc, "Color", ((color != null ? color.name() : "")))
				+ Util.setDefaultIfNotSet(sc, "Lore", lore);
	}

	public boolean isValid() {
		return toItemStack() != null && slot >= 0;
	}

//	private final List<Integer> idNoAptos = Arrays.asList(new Integer[] { 0, 6, 8, 9, 10, 11, 26, 27, 28, 31, 34, 36,
//			37, 38, 39, 40, 43, 50, 51, 55, 59, 62, 63, 64, 66, 68, 71, 74, 75, 76, 83, 90, 92, 93, 94, 99, 100, 104,
//			105, 111, 115, 117, 118, 119, 124, 125, 127, 131, 132, 137, 140, 141, 142, 143, 144, 149, 150, 157, 175,
//			282, 289, 326, 327, 379, 390, 392, 422, 177 });

	public ItemStack toItemStack() {
		Material m = null;
		try {
			m = Material.valueOf(material);
		} catch (Throwable t) {
			Util.print(ChatColor.RED, "Invalid Material: '" + material + "'", System.currentTimeMillis());
			m = Material.AIR;
		}

//		if (m != null && idNoAptos.contains(m.getId())) {
//			Util.print(ChatColor.RED, "Invalid Material for inventories: '" + material + "'",
//					System.currentTimeMillis());
//			m = Material.AIR;
//		}

		// Get Damge Short
		short damage = (short) 0;
		if (color != null && m == Material.STAINED_GLASS_PANE) {
			damage = color.shortValue();
		}

		ItemStack tor = new ItemStack(m, ammount, damage);
		return KitUtils.setNameLore(tor, name, lore);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof ItemFromSection)) {
			return false;
		}

		ItemFromSection other = (ItemFromSection) obj;
		return other != null ? (other.getMaterial().equals(material) && this.getAmmount() == other.getAmmount())
				: false;
	}
}
