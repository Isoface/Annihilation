package com.hotmail.AdrianSRJose.AnniPro.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class KitUtils {

	/**
	 * Soulbound Types
	 */
	public static enum SoulboundType {
		Tradicional, Undropabble, ClassSoulbound;
	}

	/**
	 * Get all the leather armor
	 */
	public static ItemStack[] getLeatherArmor() {
		ItemStack[] stacks = new ItemStack[] { new ItemStack(Material.LEATHER_BOOTS),
				new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE),
				new ItemStack(Material.LEATHER_HELMET) };

		for (int x = 0; x < stacks.length; x++)
			stacks[x] = KitUtils.addSoulbound(stacks[x]);
		return stacks;
	}

	/**
	 * Get new Kit special Item From Material.
	 */
	public static ItemStack getNewSpecialItem(String name, Material icon) {
		ItemStack tor = new ItemStack((icon != null ? icon : Material.BONE), 1);
		tor = setName(tor, name);
		//
		return tor;
	}

	/**
	 * Get new Kit special Item From ItemSack.
	 */
	public static ItemStack getNewSpecialItem(String name, ItemStack icon) {
		ItemStack tor = icon != null ? icon : new ItemStack(Material.BONE, 1);
		tor = setName(tor, name);
		//
		return tor;
	}

	/**
	 * Get a ItemStack ItemMeta.
	 */
	public static ItemMeta getItemMeta(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		
		ItemMeta meta = stack.getItemMeta();
		return meta != null ? meta : Bukkit.getItemFactory().getItemMeta(stack.getType());
	}

	/**
	 * Check if is any Soulbound Type.
	 */
	public static boolean isAnySoulbound(ItemStack stack) {
		return isSoulbound(stack) || isClassUndropabbleSoulbound(stack) || isClassSoulbound(stack);
	}

	/**
	 * Check if is a BossObject.
	 */
	public static boolean isBossObject(ItemStack stack) {
		if (stack == null) {
			return false;
		}
		
		return extractLore(stack).contains(Lang.BOSS_OBJECT.toString());
	}

	/**
	 * Set like a BossObject.
	 */
	public static ItemStack addBossObject(ItemStack stack) {
		if (stack == null)
			return stack;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add(Lang.BOSS_OBJECT.toString());
		meta.setLore(lore);
		stack.setItemMeta(meta);
		//
		return stack;
	}

	/**
	 * Check if is a Soulbound.
	 */
	public static boolean isSoulbound(ItemStack stack) {
		if (stack == null)
			return false;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return false;
		List<String> lore = meta.getLore();
		if (lore == null)
			return false;
		return lore.contains(Lang.SOULBOUND.toString()) 
				&& !lore.contains(Lang.CLASS_ITEM.toString());
	}

	/**
	 * Check if is a ClassUndroppableSoulbound.
	 */
	public static boolean isClassUndropabbleSoulbound(ItemStack stack) {
		if (stack == null)
			return false;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return false;
		List<String> lore = meta.getLore();
		if (lore == null)
			return false;
		if (lore.contains(Lang.CLASS_ITEM.toString()) && lore.contains(Lang.UNDROPABBLE.toString()))
			return true;
		return false;
	}

	/**
	 * Check if is a Class Soulbound.
	 */
	public static boolean isClassSoulbound(ItemStack stack) {
		if (stack == null)
			return false;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return false;
		List<String> lore = meta.getLore();
		if (lore == null)
			return false;
		if (lore.contains(Lang.CLASS_ITEM.toString()) && lore.contains(Lang.SOULBOUND.toString()))
			return true;
		return false;
	}

	/**
	 * Remove all Soulbound Items of a Inventory.
	 */
	public static void removeSoulbounds(final Inventory inv, boolean remCompass) {
		if (inv == null) {
			return;
		}

		for (int x = 0; x < inv.getSize(); x++) {
			ItemStack st = inv.getItem(x);
			if (KitUtils.isAnySoulbound(st)) {
				if (!remCompass && KitUtils.itemHasName(st, CustomItem.NAVCOMPASS.getName())) {
					continue;
				}

				// Set Null
				inv.setItem(x, null);

				final InventoryHolder holder = inv.getHolder();
				if (holder instanceof Player) {
					final Player p = (Player) holder;
					if (p != null && p.isOnline()) {
						p.updateInventory();
					}
				}
			}
		}
	}

	/**
	 * Add Soulbound from a type. Class: SoulboundType.[TYPE] | Types: [Tradicional,
	 * Undropabble, ClassSoulbound;].
	 */
	public static ItemStack addSoulboundFromType(ItemStack stack, SoulboundType type) {
		if (stack == null)
			return stack;

		if (type == null)
			return stack;

		return type == SoulboundType.ClassSoulbound ? KitUtils.addClassSoulbound(stack)
				: type == SoulboundType.Tradicional ? KitUtils.addSoulbound(stack)
						: KitUtils.addClassUndropabbleSoulbound(stack);
	}

	/**
	 * Add a Normal Soulbound.
	 */
	public static ItemStack addSoulbound(ItemStack stack) {
		if (stack == null)
			return stack;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add(Lang.SOULBOUND.toString());
		meta.setLore(lore);
		stack.setItemMeta(meta);

		return stack;
	}

	/**
	 * Add a ClassSoulbound.
	 */
	public static ItemStack addClassSoulbound(ItemStack stack) {
		if (stack == null)
			return stack;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add(Lang.SOULBOUND.toString());
		lore.add(Lang.CLASS_ITEM.toString());
		meta.setLore(lore);
		stack.setItemMeta(meta);

		return stack;
	}

	/**
	 * Add a ClassUndroppableSoulbound.
	 * </ul>
	 * 
	 * (This is Undroppable)
	 */
	public static ItemStack addClassUndropabbleSoulbound(ItemStack stack) {
		if (stack == null)
			return stack;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add(Lang.UNDROPABBLE.toString());
		lore.add(Lang.CLASS_ITEM.toString());
		meta.setLore(lore);
		stack.setItemMeta(meta);

		return stack;
	}

	/**
	 * Agregar un Soulboud encantado con LOOT_BONUS_BLOCKS.
	 */
	public static ItemStack addSoulboundEncantado(ItemStack stack, Enchantment m, int level) {
		if (stack == null)
			return stack;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add(Lang.SOULBOUND.toString());
		meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
		meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2, false);
		meta.addEnchant(m, level, true);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * Add Normal Soulboud Enchanted.
	 */
	public static ItemStack addSoulboundEnchantedItem(ItemStack stack, Enchantment m, int level) {
		if (stack == null)
			return stack;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add(Lang.SOULBOUND.toString());
		meta.addEnchant(m, level, true);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * Add a Specific Soulboud Type Enchanted.
	 */
	public static ItemStack addSoulboundEnchantedItem(ItemStack stack, SoulboundType type, Enchantment m, int level) {
		if (stack == null)
			return stack;
		ItemStack toR = addSoulboundFromType(stack, type);
		ItemMeta meta = toR.getItemMeta();
		meta.addEnchant(m, level, true);
		toR.setItemMeta(meta);
		return toR;
	}

	/**
	 * Add Enchantment to a Item.
	 */
	public static ItemStack addEnchant(ItemStack s, Enchantment m, int level) {
		s.addUnsafeEnchantment(m, level);
		return s;
	}

	/**
	 * Check with name if is a Other Item.
	 */
	public static boolean itemHasName(ItemStack stack, String name) {
		if (stack == null)
			return false;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return false;
		if (!meta.hasDisplayName())
			return false;
		return meta.getDisplayName().equalsIgnoreCase(name);
	}

	/**
	 * Check if a item name Contains his name.
	 */
	public static boolean itemNameContains(ItemStack stack, String name) {
		if (stack == null)
			return false;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return false;
		if (!meta.hasDisplayName())
			return false;
		return meta.getDisplayName().contains(name);
	}

	/**
	 * Check if tradicional Item. Tradicional Item example: [Sword, axe, pickaxe...
	 * etc].
	 */
	public static boolean isTradicionaItem(ItemStack stack) {
		List<ItemStack> l = new ArrayList<ItemStack>();

		l.add(KitUtils.addSoulbound(new ItemStack(Material.WOOD_SWORD)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.WOOD_AXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.WOOD_PICKAXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.WOOD_SPADE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.WOOD_HOE)));

		l.add(KitUtils.addSoulbound(new ItemStack(Material.STONE_SWORD)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.STONE_AXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.STONE_PICKAXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.STONE_SPADE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.STONE_HOE)));

		l.add(KitUtils.addSoulbound(new ItemStack(Material.IRON_SWORD)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.IRON_AXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.IRON_PICKAXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.IRON_SPADE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.IRON_HOE)));

		l.add(KitUtils.addSoulbound(new ItemStack(Material.GOLD_SWORD)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.GOLD_AXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.GOLD_PICKAXE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.GOLD_SPADE)));
		l.add(KitUtils.addSoulbound(new ItemStack(Material.GOLD_HOE)));

		if (KitUtils.isSoulbound(stack) && l.contains(stack))
			return true;

		return false;
	}

	/**
	 * Set Item Name.
	 */
	public static ItemStack setName(ItemStack itemStack, String name) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		if (meta == null) {
			return itemStack;
		}
		meta.setDisplayName(Util.wc(name));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	/**
	 * Set Item Lore.
	 */
	public static ItemStack setLore(ItemStack itemStack, List<String> lore) {
		if (lore == null || lore.isEmpty()) {
			return itemStack;
		}
		
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		
		if (meta == null) {
			return itemStack;
		}
		
		for (int x = 0; x < lore.size(); x++) {
			lore.set(x, Util.wc(lore.get(x)));
		}
		
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	/**
	 * @param st
	 *            item to set
	 * @param newAmount
	 *            = New ItemStack amount
	 * @return The same item stack with new Ammount
	 */
	public static ItemStack setAmount(ItemStack st, int newAmount) {
		if (st != null && newAmount > 0) {
			final ItemStack tor = new ItemStack(st.getType(), newAmount, st.getDurability());
			tor.setItemMeta(st.getItemMeta());
			return tor;
		}
		return st;
	}

	/**
	 * Check if has name and you can Check if has Lore with the boolean:
	 * "CheckLore".
	 */
	public static boolean isSpecial(ItemStack stack, boolean CheckLore) {
		if (stack == null)
			return false;
		if (!stack.hasItemMeta())
			return false;
		if (!stack.getItemMeta().hasDisplayName())
			return false;
		if (!stack.getItemMeta().hasLore() && CheckLore)
			return false;
		return true;
	}

	public static String extractName(ItemStack item, boolean stripColor) {
		if (item == null || item.getItemMeta() == null) {
			return "";
		}

		String displayName = item.getItemMeta().getDisplayName();
		return displayName == null ? "" : (stripColor ? ChatColor.stripColor(displayName) : displayName);
	}
	
	public static List<String> extractLore(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			final List<String> lore = item.getItemMeta().getLore();
			if (lore != null) {
				return lore;
			}
		}
		return new ArrayList<String>();
	}

	/**
	 * Check if a Valid Player.
	 */
	public static boolean isValidPlayer(final Player p) {
		return p != null && p.isOnline();
	}

	/**
	 * Check if a Valid AnniPlayer.
	 */
	public static boolean isValidPlayer(final AnniPlayer ap) {
		return ap != null && ap.isOnline();
	}

	/**
	 * Check if a player is in the lobby.
	 */
	public static boolean isOnLobby(final Player p) {
		if (p == null || !p.isOnline())
			return false;
		if (Game.LobbyMap == null)
			return false;
		if (Game.LobbyMap.getWorldName() == null)
			return false;
		if (p.getLocation().getWorld().getName().equals(Game.LobbyMap.getWorldName()))
			return true;
		return false;
	}

	/**
	 * Check if a player is in the Game map.
	 */
	public static boolean isOnGameMap(final Player p) {
		if (p == null || !p.isOnline())
			return false;
		if (Game.getGameMap() == null)
			return false;
		if (Game.getGameMap().getWorldName() == null)
			return false;
		if (p.getLocation().getWorld().getName().equals(Game.getGameMap().getWorldName()))
			return true;
		return false;
	}

	/**
	 * Check if a player is in the Boss Map.
	 */
	public static boolean isOnBossMap(final Player p) {
		if (p == null || !p.isOnline())
			return false;
		if (GameBoss.getBossMap() == null)
			return false;
		if (GameBoss.getBossMap().getWorldName() == null)
			return false;
		if (p.getLocation().getWorld().getName().equals(GameBoss.getBossMap().getWorldName()))
			return true;
		return false;
	}

	/**
	 * Check if a World is the LobbyMap World.
	 */
	public static boolean isLobbyMap(final World w) {
		if (!Util.isValidWorld(w))
			return false;
		if (Game.LobbyMap == null)
			return false;
		if (Game.LobbyMap.getWorldName() == null)
			return false;
		return w.getName().equals(Game.LobbyMap.getWorldName());
	}

	/**
	 * Check if a World Name is the LobbyMap World.
	 */
	public static boolean isLobbyMap(final String w) {
		if (w == null || w.length() == 0)
			return false;
		if (Game.LobbyMap == null)
			return false;
		if (Game.LobbyMap.getWorldName() == null)
			return false;
		return w.equals(Game.LobbyMap.getWorldName());
	}

	/**
	 * Check if a World is the GameMap World.
	 */
	public static boolean isGameMap(final World w) {
		if (!Util.isValidWorld(w))
			return false;
		if (Game.getGameMap() == null)
			return false;
		if (Game.getGameMap().getWorldName() == null)
			return false;
		return w.getName().equals(Game.getGameMap().getWorldName());
	}

	/**
	 * Check if a World Name is the GameMap World.
	 */
	public static boolean isGameMap(final String w) {
		if (w == null || w.length() == 0)
			return false;
		if (Game.getGameMap() == null)
			return false;
		if (Game.getGameMap().getWorldName() == null)
			return false;
		return w.equals(Game.getGameMap().getWorldName());
	}

	/**
	 * Check if a World is the BossMap World.
	 */
	public static boolean isBossMap(final World w) {
		if (!Util.isValidWorld(w))
			return false;
		if (GameBoss.getBossMap() == null)
			return false;
		if (GameBoss.getBossMap().getWorldName() == null)
			return false;
		return w.getName().equals(GameBoss.getBossMap().getWorldName());
	}

	/**
	 * Set name and Lore.
	 */
	public static ItemStack setNameLore(ItemStack itemStack, String name, List<String> lore) {
		ItemStack ot = itemStack;
		if (name != null) {
			ot = setName(itemStack, name);
		}
		return setLore(ot, lore);
	}

	/**
	 * Add Momentary fall Immunity to a Player.
	 */
	public static void addMomentaryFallImmunity(final AnniPlayer p, int seconds) {
		if (p != null && seconds > 0) {
			p.setData("kafy", "" + Long.valueOf(seconds) + ";" + Long.valueOf(System.currentTimeMillis()));
		}
	}

	/**
	 * Add One fall Immunity to a Player.
	 */
	public static void addOneFallImmunity(final AnniPlayer p) {
		if (p != null) {
			p.setData("one-kafy", "true");
		}
	}

	/**
	 * Add Momentary Hit Damage Immunity to a Player.
	 */
	public static void addHitDamageImmunity(final AnniPlayer ap, int seconds) {
		if (ap != null) {
			ap.setData("anni-hit-damage-imm",
					"" + (Long.valueOf(seconds) + ";" + Long.valueOf(System.currentTimeMillis())));
		}
	}
}
