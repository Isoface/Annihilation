package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BookItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ReturnMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.menu.MenuUtil;

public class SelectMaterialMenu {
	
	private final BookItemMenu menu;
	private final List<Material> yes   = new ArrayList<Material>();
	private final List<MenuItem> items = new ArrayList<MenuItem>();
	private final MenuItem[] opitm = new MenuItem[7];
	private final ItemMenu returnMenu;
	private final ReturnMenuItem back;
	private final Player owner;

	//
	public SelectMaterialMenu(Player owner, ItemMenu returnMenu, String title) {
		MenuItem curretSeted = null;
		//
		this.returnMenu = returnMenu;
		this.owner = owner;
		//
		for (Material m : Material.values()) {
			if ( !MenuUtil.isValidForMenus ( m ) || !m.isBlock ( ) ) {
				continue;
			}
			
			// check is not a wool
			if ( m.name ( ).contains ( "WOOL" ) ) {
				continue;
			}
			
			yes.add(m);
		}
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(owner);
		if (ap != null) {
			Object ov = ap.getData("key-Material-key-(verify)");
			if (ov != null && ov instanceof Material) {
				curretSeted = new MenuItem("§d§lCurrent selected Type: §6§l" + ((Material) ov).name(),
						new ItemStack ( ((Material) ov) , 1 ));
			} 
		}
		//
		for (Material m : yes) {
			items.add(new selectItem(m));
		}
		//
		//
		back = new ReturnMenuItem(this.returnMenu, "§c§lCancel", new WoolItemStack ( WoolColor.RED , 1 ) );
		opitm[0] = new SaveCancelButton(true);
		opitm[1] = back;
		if (curretSeted != null)
			opitm[4] = curretSeted;
		//
		//
		menu = new BookItemMenu(title, items, opitm, true, false, null, null);
	}

	public void open() {
		if (KitUtils.isValidPlayer(owner))
			menu.open(owner);
	}

	private Material sel;
	private int slotSel;
	private Inventory inv;

	private class selectItem extends MenuItem {
		private final Material m;

		//
		public selectItem(Material m) {
			super("§d§l" + m.name(), new ItemStack ( m , 1 ) );
			this.m = m;
		}

		//
		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			if (p.isOnline()) {
				final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
				if (ap != null)
					ap.setData("key-Material-key-(verify)", m);
				//
				if (sel != null && inv != null) {
					for (MenuItem i : items) {
						if (i.getIcon().getType().equals(sel)) {
							ItemStack set = i.getIcon();
							ItemMeta meta = set.getItemMeta();
							if (meta == null)
								meta = Bukkit.getItemFactory().getItemMeta(set.getType());
							meta.setDisplayName("§d§l" + set.getType().name());
							set.setItemMeta(meta);
							i.setIcon(set);
							//
							inv.setItem(slotSel, set);
						}
					}
				}
				//
				p.updateInventory();
				//
				for (ItemMenu men : menu.getPages().values()) {
					men.update(p);
				}
				//
				final ItemStack st = eve.getClickedItem();
				final ItemMeta meta = st.getItemMeta();
				//
				if (meta != null) {
					//
					if (!meta.getDisplayName().contains("§6§l(SELECTED)")) {
						meta.setDisplayName(meta.getDisplayName() + " §6§l(SELECTED)");
						st.setItemMeta(meta);
					}
					//
					eve.getInventory().setItem(eve.getSlot(), st);
					//
					slotSel = eve.getSlot();
					sel = st.getType();
					inv = eve.getInventory();
				}
				//
				//
				p.updateInventory();
			}
		}
	}

	private class SaveCancelButton extends MenuItem {
		private final boolean save;

		//
		public SaveCancelButton(boolean save) {
			super((save ? "§a§l" : "§c§l") + (save ? "Save" : "Cancel"),
					new WoolItemStack ( save ? WoolColor.GREEN : WoolColor.RED , 1 )
					);
			this.save = save;
		}

		//
		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			if (p != null && p.isOnline()) {
				final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
				if (ap != null) {
					if (save) {
						final Object sc = ap.getData("key-Material-key-(verify)");
						if (sc != null && sc instanceof Material) {
							ap.setData("key-Material-key", sc);
							p.sendMessage ( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "RegeneratingBlock Material Selected: " 
									+ ChatColor.GOLD + ChatColor.BOLD.toString ( ) + ((Material) sc).name() );
						}
					}
				}
				//
				RegenerationBlockCreatorMenu.getMenu(p).open(true);
			}
		}
	}

	/*
	 * private boolean apto(Material m) { try { switch(m) { case ACACIA_DOOR: return
	 * false; case ACACIA_DOOR_ITEM: break; case ACACIA_FENCE: break; case
	 * ACACIA_FENCE_GATE: break; case ACACIA_STAIRS: break; case ACTIVATOR_RAIL:
	 * return false; case AIR: return false; case ANVIL: break; case APPLE: break;
	 * case ARMOR_STAND: break; case ARROW: break; case BAKED_POTATO: break; case
	 * BANNER: break; case BARRIER: return false; case BEACON: break; case BED:
	 * break; case BEDROCK: break; case BED_BLOCK: return false; case BEETROOT:
	 * break; case BEETROOT_BLOCK: return false; case BEETROOT_SEEDS: break; case
	 * BEETROOT_SOUP: break; case BIRCH_DOOR: return false; case BIRCH_DOOR_ITEM:
	 * break; case BIRCH_FENCE: break; case BIRCH_FENCE_GATE: break; case
	 * BIRCH_WOOD_STAIRS: break; case BLAZE_POWDER: break; case BLAZE_ROD: break;
	 * case BOAT: break; case BOAT_ACACIA: break; case BOAT_BIRCH: break; case
	 * BOAT_DARK_OAK: break; case BOAT_JUNGLE: break; case BOAT_SPRUCE: break; case
	 * BONE: break; case BONE_BLOCK: break; case BOOK: break; case BOOKSHELF: break;
	 * case BOOK_AND_QUILL: break; case BOW: break; case BOWL: break; case BREAD:
	 * break; case BREWING_STAND: return false; case BREWING_STAND_ITEM: return
	 * false; case BRICK: break; case BRICK_STAIRS: break; case BROWN_MUSHROOM:
	 * return false; case BUCKET: break; case BURNING_FURNACE: return false; case
	 * CACTUS: break; case CAKE: break; case CAKE_BLOCK: return false; case CARPET:
	 * break; case CARROT: return false; case CARROT_ITEM: break; case CARROT_STICK:
	 * break; case CAULDRON: return false; case CAULDRON_ITEM: break; case
	 * CHAINMAIL_BOOTS: break; case CHAINMAIL_CHESTPLATE: break; case
	 * CHAINMAIL_HELMET: break; case CHAINMAIL_LEGGINGS: break; case CHEST: break;
	 * case CHORUS_FLOWER: return false; case CHORUS_FRUIT: break; case
	 * CHORUS_FRUIT_POPPED: break; case CHORUS_PLANT: break; case CLAY: break; case
	 * CLAY_BALL: break; case CLAY_BRICK: break; case COAL: break; case COAL_BLOCK:
	 * break; case COAL_ORE: break; case COBBLESTONE: break; case
	 * COBBLESTONE_STAIRS: break; case COBBLE_WALL: break; case COCOA: return false;
	 * case COMMAND: return false; case COMMAND_CHAIN: return false; case
	 * COMMAND_MINECART: return false; case COMMAND_REPEATING: return false; case
	 * COMPASS: break; case COOKED_BEEF: break; case COOKED_CHICKEN: break; case
	 * COOKED_FISH: break; case COOKED_MUTTON: break; case COOKED_RABBIT: break;
	 * case COOKIE: break; case CROPS: return false; case DARK_OAK_DOOR: return
	 * false; case DARK_OAK_DOOR_ITEM: break; case DARK_OAK_FENCE: break; case
	 * DARK_OAK_FENCE_GATE: break; case DARK_OAK_STAIRS: break; case
	 * DAYLIGHT_DETECTOR: break; case DAYLIGHT_DETECTOR_INVERTED: return false; case
	 * DEAD_BUSH: break; case DETECTOR_RAIL: return false; case DIAMOND: break; case
	 * DIAMOND_AXE: break; case DIAMOND_BARDING: break; case DIAMOND_BLOCK: break;
	 * case DIAMOND_BOOTS: break; case DIAMOND_CHESTPLATE: break; case
	 * DIAMOND_HELMET: break; case DIAMOND_HOE: break; case DIAMOND_LEGGINGS: break;
	 * case DIAMOND_ORE: break; case DIAMOND_PICKAXE: break; case DIAMOND_SPADE:
	 * break; case DIAMOND_SWORD: break; case DIODE: break; case DIODE_BLOCK_OFF:
	 * return false; case DIODE_BLOCK_ON: return false; case DIRT: break; case
	 * DISPENSER: break; case DOUBLE_PLANT: return false; case DOUBLE_STEP: return
	 * false; case DOUBLE_STONE_SLAB2: return false; case DRAGONS_BREATH: break;
	 * case DRAGON_EGG: break; case DROPPER: break; case EGG: break; case ELYTRA:
	 * break; case EMERALD: break; case EMERALD_BLOCK: break; case EMERALD_ORE:
	 * break; case EMPTY_MAP: break; case ENCHANTED_BOOK: break; case
	 * ENCHANTMENT_TABLE: break; case ENDER_CHEST: break; case ENDER_PEARL: break;
	 * case ENDER_PORTAL: return false; case ENDER_PORTAL_FRAME: break; case
	 * ENDER_STONE: break; case END_BRICKS: break; case END_CRYSTAL: break; case
	 * END_GATEWAY: return false; case END_ROD: break; case EXPLOSIVE_MINECART:
	 * break; case EXP_BOTTLE: break; case EYE_OF_ENDER: break; case FEATHER: break;
	 * case FENCE: break; case FENCE_GATE: break; case FERMENTED_SPIDER_EYE: break;
	 * case FIRE: return false; case FIREBALL: break; case FIREWORK: break; case
	 * FIREWORK_CHARGE: break; case FISHING_ROD: break; case FLINT: break; case
	 * FLINT_AND_STEEL: break; case FLOWER_POT: return false; case FLOWER_POT_ITEM:
	 * return false; case FROSTED_ICE: return false; case FURNACE: break; case
	 * GHAST_TEAR: break; case GLASS: break; case GLASS_BOTTLE: break; case
	 * GLOWING_REDSTONE_ORE: return false; case GLOWSTONE: break; case
	 * GLOWSTONE_DUST: break; case GOLDEN_APPLE: break; case GOLDEN_CARROT: break;
	 * case GOLD_AXE: break; case GOLD_BARDING: break; case GOLD_BLOCK: break; case
	 * GOLD_BOOTS: break; case GOLD_CHESTPLATE: break; case GOLD_HELMET: break; case
	 * GOLD_HOE: break; case GOLD_INGOT: break; case GOLD_LEGGINGS: break; case
	 * GOLD_NUGGET: break; case GOLD_ORE: break; case GOLD_PICKAXE: break; case
	 * GOLD_PLATE: break; case GOLD_RECORD: break; case GOLD_SPADE: break; case
	 * GOLD_SWORD: break; case GRASS: break; case GRASS_PATH: break; case GRAVEL:
	 * break; case GREEN_RECORD: break; case GRILLED_PORK: break; case HARD_CLAY:
	 * break; case HAY_BLOCK: break; case HOPPER: break; case HOPPER_MINECART:
	 * break; case HUGE_MUSHROOM_1: return false; case HUGE_MUSHROOM_2: return
	 * false; case ICE: break; case INK_SACK: break; case IRON_AXE: break; case
	 * IRON_BARDING: break; case IRON_BLOCK: break; case IRON_BOOTS: break; case
	 * IRON_CHESTPLATE: break; case IRON_DOOR: break; case IRON_DOOR_BLOCK: return
	 * false; case IRON_FENCE: break; case IRON_HELMET: break; case IRON_HOE: break;
	 * case IRON_INGOT: break; case IRON_LEGGINGS: break; case IRON_ORE: break; case
	 * IRON_PICKAXE: break; case IRON_PLATE: break; case IRON_SPADE: break; case
	 * IRON_SWORD: break; case IRON_TRAPDOOR: break; case ITEM_FRAME: break; case
	 * JACK_O_LANTERN: break; case JUKEBOX: break; case JUNGLE_DOOR: return false;
	 * case JUNGLE_DOOR_ITEM: break; case JUNGLE_FENCE: break; case
	 * JUNGLE_FENCE_GATE: break; case JUNGLE_WOOD_STAIRS: break; case LADDER: break;
	 * case LAPIS_BLOCK: break; case LAPIS_ORE: break; case LAVA: return false; case
	 * LAVA_BUCKET: return false; case LEASH: break; case LEATHER: break; case
	 * LEATHER_BOOTS: break; case LEATHER_CHESTPLATE: break; case LEATHER_HELMET:
	 * break; case LEATHER_LEGGINGS: break; case LEAVES: break; case LEAVES_2:
	 * break; case LEVER: break; case LINGERING_POTION: break; case LOG: break; case
	 * LOG_2: break; case LONG_GRASS: return false; case MAGMA: break; case
	 * MAGMA_CREAM: break; case MAP: break; case MELON: break; case MELON_BLOCK:
	 * break; case MELON_SEEDS: break; case MELON_STEM: return false; case
	 * MILK_BUCKET: break; case MINECART: break; case MOB_SPAWNER: break; case
	 * MONSTER_EGG: break; case MONSTER_EGGS: break; case MOSSY_COBBLESTONE: break;
	 * case MUSHROOM_SOUP: return false; case MUTTON: break; case MYCEL: break; case
	 * NAME_TAG: break; case NETHERRACK: break; case NETHER_BRICK: break; case
	 * NETHER_BRICK_ITEM: break; case NETHER_BRICK_STAIRS: break; case NETHER_FENCE:
	 * break; case NETHER_STALK: break; case NETHER_STAR: break; case NETHER_WARTS:
	 * return false; case NETHER_WART_BLOCK: break; case NOTE_BLOCK: break; case
	 * OBSIDIAN: break; case PACKED_ICE: break; case PAINTING: break; case PAPER:
	 * break; case PISTON_BASE: break; case PISTON_EXTENSION: return false; case
	 * PISTON_MOVING_PIECE: return false; case PISTON_STICKY_BASE: break; case
	 * POISONOUS_POTATO: break; case PORK: break; case PORTAL: return false; case
	 * POTATO: return false; case POTATO_ITEM: return false; case POTION: break;
	 * case POWERED_MINECART: break; case POWERED_RAIL: return false; case
	 * PRISMARINE: break; case PRISMARINE_CRYSTALS: break; case PRISMARINE_SHARD:
	 * break; case PUMPKIN: break; case PUMPKIN_PIE: break; case PUMPKIN_SEEDS:
	 * break; case PUMPKIN_STEM: return false; case PURPUR_BLOCK: break; case
	 * PURPUR_DOUBLE_SLAB: return false; case PURPUR_PILLAR: break; case
	 * PURPUR_SLAB: break; case PURPUR_STAIRS: break; case QUARTZ: break; case
	 * QUARTZ_BLOCK: break; case QUARTZ_ORE: break; case QUARTZ_STAIRS: break; case
	 * RABBIT: break; case RABBIT_FOOT: break; case RABBIT_HIDE: break; case
	 * RABBIT_STEW: break; case RAILS: return false; case RAW_BEEF: break; case
	 * RAW_CHICKEN: break; case RAW_FISH: break; case RECORD_10: break; case
	 * RECORD_11: break; case RECORD_12: break; case RECORD_3: break; case RECORD_4:
	 * break; case RECORD_5: break; case RECORD_6: break; case RECORD_7: break; case
	 * RECORD_8: break; case RECORD_9: break; case REDSTONE: break; case
	 * REDSTONE_BLOCK: break; case REDSTONE_COMPARATOR: break; case
	 * REDSTONE_COMPARATOR_OFF: return false; case REDSTONE_COMPARATOR_ON: return
	 * false; case REDSTONE_LAMP_OFF: break; case REDSTONE_LAMP_ON: return false;
	 * case REDSTONE_ORE: break; case REDSTONE_TORCH_OFF: return false; case
	 * REDSTONE_TORCH_ON: return false; case REDSTONE_WIRE: return false; case
	 * RED_MUSHROOM: return false; case RED_NETHER_BRICK: break; case RED_ROSE:
	 * return false; case RED_SANDSTONE: break; case RED_SANDSTONE_STAIRS: break;
	 * case ROTTEN_FLESH: break; case SADDLE: break; case SAND: break; case
	 * SANDSTONE: break; case SANDSTONE_STAIRS: break; case SAPLING: return false;
	 * case SEA_LANTERN: break; case SEEDS: break; case SHEARS: break; case SHIELD:
	 * break; case SIGN: break; case SIGN_POST: return false; case SKULL: return
	 * false; case SKULL_ITEM: break; case SLIME_BALL: break; case SLIME_BLOCK:
	 * break; case SMOOTH_BRICK: break; case SMOOTH_STAIRS: break; case SNOW: break;
	 * case SNOW_BALL: break; case SNOW_BLOCK: break; case SOIL: break; case
	 * SOUL_SAND: break; case SPECKLED_MELON: break; case SPECTRAL_ARROW: break;
	 * case SPIDER_EYE: break; case SPLASH_POTION: break; case SPONGE: break; case
	 * SPRUCE_DOOR: return false; case SPRUCE_DOOR_ITEM: break; case SPRUCE_FENCE:
	 * break; case SPRUCE_FENCE_GATE: break; case SPRUCE_WOOD_STAIRS: break; case
	 * STAINED_CLAY: break; case STAINED_GLASS: break; case STAINED_GLASS_PANE:
	 * break; case STANDING_BANNER: return false; case STATIONARY_LAVA: return
	 * false; case STATIONARY_WATER: return false; case STEP: break; case STICK:
	 * break; case STONE: break; case STONE_AXE: break; case STONE_BUTTON: break;
	 * case STONE_HOE: break; case STONE_PICKAXE: break; case STONE_PLATE: break;
	 * case STONE_SLAB2: break; case STONE_SPADE: break; case STONE_SWORD: break;
	 * case STORAGE_MINECART: break; case STRING: break; case STRUCTURE_BLOCK:
	 * return false; case STRUCTURE_VOID: return false; case SUGAR: break; case
	 * SUGAR_CANE: break; case SUGAR_CANE_BLOCK: return false; case SULPHUR: return
	 * false; case THIN_GLASS: break; case TIPPED_ARROW: break; case TNT: break;
	 * case TORCH: return false; case TRAPPED_CHEST: break; case TRAP_DOOR: break;
	 * case TRIPWIRE: return false; case TRIPWIRE_HOOK: return false; case VINE:
	 * break; case WALL_BANNER: return false; case WALL_SIGN: return false; case
	 * WATCH: break; case WATER: return false; case WATER_BUCKET: return false; case
	 * WATER_LILY: return false; case WEB: break; case WHEAT: break; case WOOD:
	 * break; case WOODEN_DOOR: return false; case WOOD_AXE: break; case
	 * WOOD_BUTTON: return false; case WOOD_DOOR: break; case WOOD_DOUBLE_STEP:
	 * return false; case WOOD_HOE: break; case WOOD_PICKAXE: break; case
	 * WOOD_PLATE: break; case WOOD_SPADE: break; case WOOD_STAIRS: break; case
	 * WOOD_STEP: break; case WOOD_SWORD: break; case WOOL: break; case WORKBENCH:
	 * break; case WRITTEN_BOOK: break; case YELLOW_FLOWER: return false; default:
	 * return true; } } catch (Throwable paramException) { return false; } return
	 * true; }
	 */

}
