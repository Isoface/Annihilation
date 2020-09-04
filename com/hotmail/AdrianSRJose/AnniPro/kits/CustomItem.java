package com.hotmail.AdrianSRJose.AnniPro.kits;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils.SoulboundType;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public enum CustomItem {
	
	NAVCOMPASS(ChatColor.DARK_PURPLE + "Right click to change target nexus", Material.COMPASS, (byte) 0,
			SoulboundType.ClassSoulbound, null),

	KITMAP(ChatColor.AQUA + "Right click to choose a kit", Material.FEATHER, (byte) 0, SoulboundType.Undropabble,
			null),

	VOTEMAP(ChatColor.AQUA + "Right click to vote for a map", Material.GRASS, (byte) 0,
			SoulboundType.Undropabble, null),

	TEAMMAP(ChatColor.AQUA + "Right click to join a team", Material.NETHER_STAR, (byte) 0, SoulboundType.Undropabble,
			null),

	SPECTATORMAP(ChatColor.AQUA + "Right click View a team", Material.STICK, (byte) 0, SoulboundType.Undropabble,
			null),
	//
	//
	MAPBUILDER(ChatColor.AQUA + "Right click to open the map builder", Material.DIAMOND_PICKAXE, (byte) 0, true,
			null), IMPORTAR_MAPA_BOSS(ChatColor.AQUA + "Click To Load a BossMap", Material.ENDER_STONE, (byte) 0, true,
					null),
	
	BEACON_HANDLER(ChatColor.AQUA + "Click a block to set the magic beacon spawn", Material.BEACON, (byte) 0, true, null),
	BEACON(Util.wc("&6&lMagic Beacon"), Material.BEACON, (byte) 0, false, null),
	
	//
	ADD_SKI_JUMP(ChatColor.AQUA + "Ski-Jump Helper", Material.REDSTONE, (byte) 0, true,
			new String[] { ChatColor.DARK_PURPLE + "Right click to add a Ski-Jump.",
					ChatColor.DARK_PURPLE + "Left click to remove a Ski-Jump." }),
	//
	//
	BossStar(ChatColor.DARK_RED + (ChatColor.BOLD + "BossStar"), Material.NETHER_STAR, (byte) 0, false, null), 
	
	OPTIONSITEM(ChatColor.AQUA + "Right Click to Open the OptionsMenu", Material.SKULL_ITEM, (byte) 0,
					SoulboundType.Undropabble, null), 
	
	GO_TO_LOBBY(ChatColor.DARK_RED + "Right Click to Go to Lobby",
							Material.WATCH, (byte) 0, SoulboundType.Undropabble, null),

	REMOVE_JOINER_ARMOR_STAND(ChatColor.RED + "Left click on an ArmorStand to remove it", Material.STICK, (byte) 0,
			SoulboundType.ClassSoulbound,
			null), 
	
	ADD_RANDOM_JOINER_ARMOR_STAND(ChatColor.AQUA + "Add a Random Armor Stand Joiner", Material.EMERALD,
					(byte) 0, SoulboundType.ClassSoulbound, null),

	KIST_SHOT(ChatColor.GOLD + "Right Click to Buy a Kit", Material.EMERALD, (byte) 0, SoulboundType.ClassSoulbound,
			null),

	BREWINGSHOP(ChatColor.AQUA + "Brewing Shop Helper", Material.BREWING_STAND_ITEM, (byte) 0, true, new String[] {
			ChatColor.DARK_PURPLE + "Right click to add a brewing shop.",
			ChatColor.DARK_PURPLE + "Left click to remove a brewing shop." }), WEAPONSHOP(
					ChatColor.AQUA + "Weapon Shop Helper", Material.ARROW, (byte) 0, true,
					new String[] { ChatColor.DARK_PURPLE + "Right click to add a weapon shop.",
							ChatColor.DARK_PURPLE + "Left click to remove a weapon shop." }), ENDERFURNACE(
									ChatColor.AQUA + "Ender Furnace Helper", Material.EYE_OF_ENDER, (byte) 0, true,
									new String[] { ChatColor.DARK_PURPLE + "Right click to add an ender furnace.",
											ChatColor.DARK_PURPLE
													+ "Left click to remove an ender furnace." }), ENDER_BREWING(
															ChatColor.AQUA + "Ender Brewing Helper",
															Material.BLAZE_POWDER, (byte) 0, true,
															new String[] {
																	ChatColor.DARK_PURPLE
																			+ "Right click to add an ender Brewing.",
																	ChatColor.DARK_PURPLE
																			+ "Left click to remove an ender Brewing." }), REGENBLOCKHELPER(
																					ChatColor.AQUA
																							+ "Regenerating Block Helper",
																					Material.STICK, (byte) 0, true,
																					new String[] { ChatColor.DARK_PURPLE
																							+ "Left or Right click a block to select it." }), 
	AREAWAND(ChatColor.AQUA+ "Area Wand", Material.BLAZE_ROD, (byte) 0, true, new String[] {
			ChatColor.DARK_PURPLE + "Left click a block to set it as corner one.",
			ChatColor.DARK_PURPLE + "Right click a block to set it as corner two." }),
	DIAMONDHELPER(
																															ChatColor.AQUA
																																	+ "Diamond Helper",
																															Material.DIAMOND,
																															(byte) 0,
																															true,
																															new String[] {
																																	ChatColor.DARK_PURPLE
																																			+ "Left click a block to add it as a diamond.",
																																	ChatColor.DARK_PURPLE
																																			+ "Right click a block to remove it as a diamond." }), UNPLACEABLEBLOCKSWAND(
																																					ChatColor.AQUA
																																							+ "Unplaceable Blocks Wand",
																																					Material.DIAMOND_SPADE,
																																					(byte) 0,
																																					true,
																																					new String[] {
																																							ChatColor.DARK_PURPLE
																																									+ "Left click a block to add it as unplaceable",
																																							ChatColor.DARK_PURPLE
																																									+ "Right click a block to remove it as a diamond." }), BOSS_MASTER(
																																											ChatColor.GOLD
																																													+ "Boss Master",
																																											Material.SKULL_ITEM,
																																											(byte) 0,
																																											true,
																																											new String[] {
																																													ChatColor.RED
																																															+ "Right Click To Set Boss Spawn",
																																													ChatColor.RED
																																															+ "Here.." }), Barita_Portal(
																																																	ChatColor.GOLD
																																																			+ "Set Portal Spawns to Boss Map",
																																																	Material.GOLD_AXE,
																																																	(byte) 0,
																																																	true,
																																																	new String[] {
																																																			ChatColor.RED
																																																					+ "Right Click To Add a Portal Spawn",
																																																			ChatColor.RED
																																																					+ "Here." }), Spawn_Boss_Map_Seter(
																																																							ChatColor.GOLD
																																																									+ "Add Spawn Here",
																																																							Material.TORCH,
																																																							(byte) 0,
																																																							true,
																																																							new String[] {
																																																									ChatColor.RED
																																																											+ "Click To add a Spawn Here" }), EXIT_OF_SPECTATOR_MODE(
																																																													ChatColor.RED
																																																															+ "Exit of the Spectator Mode",
																																																													Material.BED,
																																																													(byte) 0,
																																																													true,
																																																													new String[] {
																																																															ChatColor.RED
																																																																	+ "Right Click To Exit of the Spectator Mode" });

	private String name;
	private String[] lore;
	private Material type;
	private byte data;
	private short shortt = (short) 0;
	private boolean soulBound;
	private SoulboundType Stype = null;

	@Deprecated
	CustomItem(String name, Material type, byte data, boolean soulBound, String[] lore) {
		this.name = name;
		this.lore = lore;
		this.type = type;
		this.data = data;
		this.soulBound = soulBound;
	}

	CustomItem(String name, Material type, byte data, SoulboundType stype, String[] lore) {
		this.name = name;
		this.lore = lore;
		this.type = type;
		this.data = data;
		soulBound = stype == null ? false : stype.equals(SoulboundType.Tradicional) ? true : false;
		Stype = soulBound ? SoulboundType.Tradicional : stype;
	}

	CustomItem(String name, Material type, short data, SoulboundType stype, String[] lore) {
		this.name = name;
		this.lore = lore;
		this.type = type;
		this.shortt = data;
		this.data = (byte) 0;
		soulBound = stype == null ? false : stype.equals(SoulboundType.Tradicional) ? true : false;
		Stype = soulBound ? SoulboundType.Tradicional : stype;
	}

	public static boolean isCustomItem(final ItemStack stack) {
		if (stack == null) {
			return false;
		}

		boolean tor = false;
		for (CustomItem it : CustomItem.values()) {
			if (KitUtils.itemHasName(stack, it.getName())) {
				tor = true;
				break;
			}
		}
		return tor;
	}

	@SuppressWarnings("deprecation")
	public ItemStack toItemStack(int amount) {
//		ItemStack stack = new ItemStack(type.toBukkit(), amount, shortt, data);
		ItemStack stack = new ItemStack(type, amount, shortt);
		stack.setData(new MaterialData(type, data));
		
		ItemMeta meta = stack.getItemMeta();
		if (name != null)
			meta.setDisplayName(this.getName());
		if (lore != null)
			meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		return Stype != null ? KitUtils.addSoulboundFromType(stack, Stype)
				: Stype == null && soulBound == true ? KitUtils.addSoulbound(stack)
						: Stype == null && soulBound == false ? stack : KitUtils.addSoulboundFromType(stack, Stype);
	}

	public ItemStack toItemStack() {
		return toItemStack(1);
	}

	public String getName() {
		if (this == CustomItem.NAVCOMPASS)
			return Lang.NAVCOMPASS.toString();
		else if (this == CustomItem.KITMAP)
			return Lang.KITMAP.toString();
		else if (this == CustomItem.VOTEMAP)
			return Lang.VOTEMAP.toString();
		else if (this == CustomItem.TEAMMAP)
			return Lang.TEAMMAP.toString();
		else if (this == CustomItem.SPECTATORMAP)
			return Lang.SPECTATORMAP.toString();
		else if (this == CustomItem.OPTIONSITEM)
			return Lang.OPTIONSITEM.toString();
		else if (this == CustomItem.KIST_SHOT)
			return Lang.KITS_SHOP.toString();
		else if (this == CustomItem.GO_TO_LOBBY)
			return Lang.GO_TO_LOBBY.toString();
		else if (this == CustomItem.BossStar)
			return Lang.BOSS_STAR.toString();
		return name;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
