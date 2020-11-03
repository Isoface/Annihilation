package com.hotmail.AdrianSRJose.Kits;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.hotmail.AdrianSR.core.util.RandomUtils;
import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.itemstack.ItemStackUtils;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 29 October, 2020 / 05:23 PM
 */
public class Enchanter extends ClassItemKit {
	
	/** key for identifying intensifier enchanters */
	protected static final String INTENSIFIER_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	/**
	 * Enchanter kit configuration.
	 * <p>
	 * @author AdrianSR / Tuesday 27 October, 2020 / 03:06 PM
	 */
	protected static enum Configuration {
		
		// --------- levels instead health
		LEVEL_INSTEAD_HEALTH_START ( "consume-levels-instead-health-starting-at" , 7.0D ),
		
		/** whether to consume levels instead health is enabled */
		LEVEL_INSTEAD_HEALTH_USE ( "enchanter-self-xp-multiplier-use" , true ) {
			@Override public void loadConfiguration ( ConfigurationSection section ) {
				String key = LEVEL_INSTEAD_HEALTH_USE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					LEVEL_INSTEAD_HEALTH_USE.value = section.getBoolean ( key );
				}
			}
			
			@Override public int saveDefaults ( ConfigurationSection section ) {
				String key = LEVEL_INSTEAD_HEALTH_USE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					return 0;
				} else {
					section.set ( key , LEVEL_INSTEAD_HEALTH_USE.default_value );
					return 1;
				}
			}
		},
		
		// --------- increase enchantment level
		ENCHANTMENT_INCREASE_LEVEL_BY ( "enchantment-increase-level-by" , 1 ) ,
		ENCHANTMENT_INCREASE_LEVEL_CHANCE ( "enchantment-increase-level-chance" , 30.0D ) ,
		ENCHANTMENT_INCREASE_LEVEL_MESSAGE ( "enchantment-level-increased-message" , 
				ChatColor.GREEN + "Your Enchanter class allowed you to get a higher level " + ChatColor.YELLOW + "%w" + ChatColor.GREEN + " enchantment!" ) {
			@Override public void loadConfiguration ( ConfigurationSection section ) {
				String key = ENCHANTMENT_INCREASE_LEVEL_MESSAGE.key;
				
				if ( section.get ( key ) instanceof String ) {
					ENCHANTMENT_INCREASE_LEVEL_MESSAGE.value = Util.wc ( section.getString ( key ) );
				}
			}
			
			@Override public int saveDefaults ( ConfigurationSection section ) {
				String key = ENCHANTMENT_INCREASE_LEVEL_MESSAGE.key;
				
				if ( section.get ( key ) instanceof String ) {
					return 0;
				} else {
					section.set ( key , 
							ENCHANTMENT_INCREASE_LEVEL_MESSAGE.default_value.toString ( ).replace ( ChatColor.COLOR_CHAR , '&' ) );
					return 1;
				}
			}
		},
		
		/** whether the enchanter self xp multiplier is enabled */
		ENCHANTMENT_INCREASE_LEVEL_USE ( "enchantment-increase-level-use" , true ) {
			@Override public void loadConfiguration ( ConfigurationSection section ) {
				String key = ENCHANTMENT_INCREASE_LEVEL_USE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					ENCHANTMENT_INCREASE_LEVEL_USE.value = section.getBoolean ( key );
				}
			}
			
			@Override public int saveDefaults ( ConfigurationSection section ) {
				String key = ENCHANTMENT_INCREASE_LEVEL_USE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					return 0;
				} else {
					section.set ( key , ENCHANTMENT_INCREASE_LEVEL_USE.default_value );
					return 1;
				}
			}
		},
		
		// --------- self xp multiplier
		ENCHANTER_SELF_XP_MULTIPLIER ( "enchanter-self-xp-multiplier" , 2.0D ) ,
		
		/** whether the enchanter self xp multiplier is enabled */
		ENCHANTER_SELF_XP_MULTIPLIER_ENABLE ( "enchanter-self-xp-multiplier-use" , true ) {
			@Override public void loadConfiguration ( ConfigurationSection section ) {
				String key = ENCHANTER_SELF_XP_MULTIPLIER_ENABLE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					ENCHANTER_SELF_XP_MULTIPLIER_ENABLE.value = section.getBoolean ( key );
				}
			}
			
			@Override public int saveDefaults ( ConfigurationSection section ) {
				String key = ENCHANTER_SELF_XP_MULTIPLIER_ENABLE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					return 0;
				} else {
					section.set ( key , ENCHANTER_SELF_XP_MULTIPLIER_ENABLE.default_value );
					return 1;
				}
			}
		},
		
		// --------- teammate xp multiplier
		ENCHANTER_TEAMMATE_XP_MULTIPLIER ( "enchanter-teammate-xp-multiplier" , 1.25D ) ,
		
		/** whether the enchanter teammate xp multiplier is enabled */
		ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE ( "enchanter-teammate-xp-multiplier-use" , true ) {
			@Override public void loadConfiguration ( ConfigurationSection section ) {
				String key = ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE.value = section.getBoolean ( key );
				}
			}
			
			@Override public int saveDefaults ( ConfigurationSection section ) {
				String key = ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					return 0;
				} else {
					section.set ( key , ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE.default_value );
					return 1;
				}
			}
		},
		
		/** whether the xp multiplier will only affect ore resources */
		AFFECT_ONLY_ORES ( "xp-multipliers-affect-only-ores" , true ) {
			@Override public void loadConfiguration ( ConfigurationSection section ) {
				String key = AFFECT_ONLY_ORES.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					AFFECT_ONLY_ORES.value = section.getBoolean ( key );
				}
			}
			
			@Override public int saveDefaults ( ConfigurationSection section ) {
				String key = AFFECT_ONLY_ORES.key;
				
				if ( section.get ( key ) instanceof Boolean ) {
					return 0;
				} else {
					section.set ( key , AFFECT_ONLY_ORES.default_value );
					return 1;
				}
			}
		},
		
		// --------- intensifier
		INTENSIFIER_DURATION ( "intensifier-duration" , 40L ),
		INTENSIFIER_RANGE ( "intensifier-range" , 10.0D );
		
		private final String           key;
		private final Object default_value;
		private       Object         value;
		
		private Configuration ( String key , Object default_value ) {
			this.key           = key;
			this.default_value = default_value;
			this.value         = default_value;
		}
		
		public Object getValue ( ) {
			return value;
		}
		
		public String getAsString ( ) {
			if ( default_value instanceof String ) {
				return value instanceof String 
						? ((String) getValue ( ))
						: ((String) default_value);
			} else {
				throw new UnsupportedOperationException ( "This configuration entry is not a String!" );
			}
		}
		
		public double getAsDouble ( ) {
			if ( default_value instanceof Number ) {
				return value instanceof Number 
						? ((Number) getValue ( )).doubleValue ( ) 
						: ((Number) default_value).doubleValue ( );
			} else {
				throw new UnsupportedOperationException ( "This configuration entry is not a number!" );
			}
		}
		
		public int getAsInteger ( ) {
			if ( default_value instanceof Number ) {
				return value instanceof Number 
						? ((Number) getValue ( )).intValue ( ) 
						: ((Number) default_value).intValue ( );
			} else {
				throw new UnsupportedOperationException ( "This configuration entry is not a number!" );
			}
		}
		
		public long getAsLong ( ) {
			if ( default_value instanceof Number ) {
				return value instanceof Number 
						? ((Number) getValue ( )).longValue ( ) 
						: ((Number) default_value).longValue ( );
			} else {
				throw new UnsupportedOperationException ( "This configuration entry is not a number!" );
			}
		}
		
		public boolean getAsBoolean ( ) {
			if ( default_value instanceof Boolean ) {
				return value instanceof Boolean 
						? ((Boolean) getValue ( )).booleanValue ( ) 
						: ((Boolean) default_value).booleanValue ( );
			} else {
				throw new UnsupportedOperationException ( "This configuration entry is not a boolean!" );
			}
		}
		
		public void loadConfiguration ( ConfigurationSection section ) {
			if ( section.get ( key ) instanceof Number ) {
				this.value = section.getDouble ( key );
			}
		}
		
		public int saveDefaults ( ConfigurationSection section ) {
			if ( section.get ( key ) instanceof Number ) {
				return 0;
			} else {
				section.set ( key , default_value );
				return 1;
			}
		}
	}

	@Override
	protected ItemStack specialItem ( ) {
		return ItemStackUtils.setName ( 
				KitUtils.addClassUndropabbleSoulbound ( new ItemStack ( UniversalMaterial.GOLD_NUGGET.getMaterial ( ) ) ) , 
				getSpecialItemName ( ) + instance.getReadyPrefix ( ) );
	}

	@Override
	protected String defaultSpecialItemName ( ) {
		return ChatColor.YELLOW + "Intensifier";
	}

	@Override
	protected boolean isSpecialItem ( ItemStack stack ) {
		if ( KitUtils.isClassUndropabbleSoulbound ( stack ) ) {
			return ItemStackUtils.extractName ( stack , false ).startsWith ( getSpecialItemName ( ) );
		} else {
			return false;
		}
	}

	@Override
	protected boolean performPrimaryAction ( Player player , AnniPlayer ap , PlayerInteractEvent event ) {
		// here we're saving the millis of the instant when the player actives the intensifier.
		player.setMetadata ( INTENSIFIER_METADATA_KEY , new FixedMetadataValue ( AnnihilationMain.INSTANCE , System.currentTimeMillis ( ) ) );
		return false;
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onDamage ( EntityDamageEvent event ) {
		if ( event.getEntityType ( ) != EntityType.PLAYER ) {
			return;
		}
		
		Player player = (Player) event.getEntity ( );
		
		if ( hasThisKit ( player ) && Configuration.LEVEL_INSTEAD_HEALTH_USE.getAsBoolean ( ) 
				&& player.getHealth ( ) <= Configuration.LEVEL_INSTEAD_HEALTH_START.getAsDouble ( ) ) {
			
			double damage = event.getDamage ( );
			int     level = player.getLevel ( );
			
			if ( level >= event.getDamage ( ) ) {
				player.setLevel ( (int) ( level - Math.floor ( damage ) ) );
				event.setDamage ( 0.0D );
			} else if ( level > 0 ) {
				player.setLevel ( 0 );
				event.setDamage ( damage - level );
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onEnchanting ( EnchantItemEvent event ) {
		// here we're increasing the level of one enchantment.
		if ( hasThisKit ( event.getEnchanter ( ) ) && Configuration.ENCHANTMENT_INCREASE_LEVEL_USE.getAsBoolean ( ) ) {
			Map < Enchantment , Integer > enchantments = event.getEnchantsToAdd ( );
			for ( Enchantment enchantment : enchantments.keySet ( ) ) {
				if ( RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= Configuration.ENCHANTMENT_INCREASE_LEVEL_CHANCE.getAsDouble ( ) ) {
					int updated_level = enchantments.get ( enchantment ) + Configuration.ENCHANTMENT_INCREASE_LEVEL_BY.getAsInteger ( );
					
					if ( updated_level <= enchantment.getMaxLevel ( ) ) {
						enchantments.put ( enchantment , Integer.valueOf ( updated_level ) );
						
						event.getEnchanter ( ).sendMessage ( 
								Configuration.ENCHANTMENT_INCREASE_LEVEL_MESSAGE.getAsString ( ).replace ( "%w" , enchantment.getName ( ) ) );
					}
					break;
				}
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onMining ( ResourceBreakEvent event ) {
		if ( !event.getResource ( ).Type.name ( ).contains ( "_ORE" ) 
				&& Configuration.AFFECT_ONLY_ORES.getAsBoolean ( ) ) {
			return;
		}
		
		Player        player = event.getPlayer ( ).getPlayer ( );
		double xp_multiplier = 1;
		
		// if this player is an enchanter, it will have at least the base xp multiplier.
		if ( hasThisKit ( player ) && Configuration.ENCHANTER_SELF_XP_MULTIPLIER_ENABLE.getAsBoolean ( ) ) {
			xp_multiplier += Configuration.ENCHANTER_SELF_XP_MULTIPLIER.getAsDouble ( );
		}
		
		// here we're looking for near teammates that are enchanters and has activated the intensifier.
		if ( Configuration.ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE.getAsBoolean ( ) ) {
			for ( AnniPlayer teammate : event.getPlayer ( ).getTeam ( ).getPlayers ( ) ) {
				if ( teammate.isOnline ( ) && !player.getUniqueId ( ).equals ( teammate.getID ( ) ) ) {
					Player mate = teammate.getPlayer ( );
					
					if ( isIntensifier ( mate ) 
							&& mate.getLocation ( ).distance ( player.getLocation ( ) ) <= Configuration.INTENSIFIER_RANGE.getAsDouble ( ) ) {
						xp_multiplier += Configuration.ENCHANTER_TEAMMATE_XP_MULTIPLIER.getAsDouble ( );
						break;
					}
				}
			}
		}
		
		if ( event.getXP ( ) > 0 ) {
			event.setXP ( (int) Math.ceil ( event.getXP ( ) * xp_multiplier ) );
		}
	}
	
	/**
	 * Gets whether the desired {@code player} has activated the intensifier. 
	 * <p>
	 * @param player the player to check.
	 * @return true if rushing.
	 */
	protected boolean isIntensifier ( Player player ) {
		if ( player.hasMetadata ( INTENSIFIER_METADATA_KEY ) ) {
			List < MetadataValue > metadata = player.getMetadata ( INTENSIFIER_METADATA_KEY );
			
			if ( metadata.size ( ) > 0 ) {
				MetadataValue value = metadata.get ( 0 );
				if ( TimeUnit.MILLISECONDS.toSeconds ( 
						System.currentTimeMillis ( ) - value.asLong ( ) ) <= Configuration.INTENSIFIER_DURATION.getAsLong ( ) ) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	protected long getDefaultDelayLength ( ) {
		return 120 * 1000; // 120 seconds.
	}

	@Override
	protected boolean useDefaultChecking ( ) {
		return true;
	}

	@Override
	protected boolean useCustomMessage ( ) {
		return false;
	}

	@Override
	protected String positiveMessage ( ) {
		return null;
	}

	@Override
	protected String negativeMessage ( ) {
		return null;
	}

	@Override
	protected String getInternalName ( ) {
		return "Enchanter";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return UniversalMaterial.EXPERIENCE_BOTTLE.getItemStack ( );
	}

	@Override
	protected void loadFromConfig ( ConfigurationSection section ) {
		for ( Configuration entry : Configuration.values ( ) ) {
			entry.loadConfiguration ( section );
		}
	}
	
	@Override
	protected int setInConfig ( ConfigurationSection section ) {
		int count = 0;
		
		for ( Configuration entry : Configuration.values ( ) ) {
			count += entry.saveDefaults ( section );
		}
		return count;
	}

	@Override
	protected List < String > getDefaultDescription ( ) {
		return Arrays.asList 
				( 
						aqua + "You are the master.", 
						"",
						aqua + "You upgrade your team's gear", 
						aqua + "with your advanced knowledge",
						aqua + "of enchantments.",
						"",
						aqua + "You gain extra XP from gathering",
						aqua + "resources, and your special",
						aqua + "ability buffs your teammate's",
						aqua + "XP gain as well."
				);
	}

	@Override
	protected Loadout getFinalLoadout ( ) {
		Loadout loadout = new Loadout ( )
				.addGoldSword ( )
				.addWoodPick ( )
				.addWoodAxe ( );
		
		if ( Configuration.ENCHANTER_TEAMMATE_XP_MULTIPLIER_ENABLE.getAsBoolean ( ) ) {
			loadout.addItem ( getSpecialItem ( ) );
		}
		
		return loadout;
	}

	@Override
	public boolean onItemClick ( Inventory inventory , AnniPlayer anni ) {
		addLoadoutToInventory ( inventory );
		return true;
	}
	
	@Override protected boolean performSecondaryAction ( Player player , AnniPlayer anni , PlayerInteractEvent event ) { return false; }
	@Override protected void onInitialize ( ) { }
	@Override protected void onPlayerRespawn ( Player player , AnniPlayer anni ) { }
	@Override public void cleanup ( Player player ) { }
}

//package com.hotmail.AdrianSRJose.Kits;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.enchantment.EnchantItemEvent;
//import org.bukkit.event.entity.EntityDamageEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.metadata.FixedMetadataValue;
//
//import com.hotmail.AdrianSR.core.util.UniversalMaterial;
//import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;
//import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
//import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
//import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
//import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
//import com.hotmail.AdrianSRJose.base.ClassItemKit;
//
//public class Enchanter extends ClassItemKit {
//	
//	private static final Random rand = new Random(System.currentTimeMillis());
//	private int aoeDuration = 30;
//	private int aoeRadius = 10;
//	private float aoeXpMultiplier = 3.5F;
//	private float chanceToIncreaseEnchant = 0.45F;
//	private float selfXpMultiplier = 5.0F; // 10.0
////	private double divideRegenerationTimeBy = 1.5;
//	private static final String ITEM_NAME = ChatColor.YELLOW + "Intensifier";
//	private static final String META_KEY = "ENCHANTER_ACTIVE";
//
//	@Override
//	protected void onInitialize() {
//	}
//
//	@EventHandler
//	public void onDamage(EntityDamageEvent e) {
//		if (e.getEntity() instanceof Player) {
//			final Player player = (Player) e.getEntity();
//			final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
//			if (hasThisKit(ap) && player.getHealth() - e.getDamage() <= 1.0D) {
//				int damage = (int) Math.ceil(e.getDamage());
//				if (player.getLevel() >= damage) {
//					player.setLevel(Math.max((player.getLevel() - 10), 0));
//					e.setDamage(0.0D);
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	public void onPlayerEnchantItemEvent(EnchantItemEvent event) {
//		final Player player = event.getEnchanter();
//		final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
//		if (ap != null && hasThisKit(ap)) {
//			final Map<Enchantment, Integer> enchantments = event.getEnchantsToAdd();
//			for (Enchantment enchant : enchantments.keySet()) {
//				int enchantLevel = enchantments.get(enchant).intValue();
//				if ((enchantLevel + 1 <= enchant.getMaxLevel()) && (rand.nextFloat() <= this.chanceToIncreaseEnchant)) {
//					enchantments.put(enchant, Integer.valueOf(enchantLevel + 1));
//
//					// TODO: Check (can must have to remove)
//					player.sendMessage(ChatColor.AQUA + "Your Enchanter class allowed you to get a higher level "
//							+ ap.getTeam().getColor() + enchant.getName() + ChatColor.AQUA + " enchantment!");
//				}
//			}
//		}
//	}
//
//	// Increase the xp gained from mining blocks and potentially gives you an XP
//	// bottle (1% chance)
//	@EventHandler
//	public void onResourceBreak(final ResourceBreakEvent event) {
//		// add
//		if (hasThisKit(event.getPlayer())) {
//			int xp = event.getXP();
//			if (xp > 0) {
//				xp = (int) Math.ceil(xp * this.selfXpMultiplier);
//				event.setXP(xp);
//			}
//
//			// Add EXP_BOTTLE
//			if (rand.nextInt(3) == 1) {
//				final Player pl = event.getPlayer().getPlayer();
//				int i = rand.nextInt(2) + 1;
//				pl.getInventory().addItem(new ItemStack ( UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( ), i));
//			}
//		} else if (isNearActive(event.getPlayer().getPlayer())) {
//			int xp = event.getXP();
//			if (xp > 0) {
//				xp = (int) Math.ceil(xp * this.aoeXpMultiplier);
//				event.setXP(xp);
//			}
//		}
//
//		// Set New Time
////		if (hasThisKit(event.getPlayer())) {
////			final int oldTime = event.getRegenerationTime();
////			event.setRegenerationTime((int) (oldTime / divideRegenerationTimeBy));
////		}
//	}
//
//	private boolean isNearActive(Player player) {
//		for (Player p : Bukkit.getOnlinePlayers()) {
//			if (p != null && !p.getUniqueId().equals(player.getUniqueId()) && p.hasMetadata(META_KEY)
//					&& p.getWorld().equals(player.getWorld())
//					&& p.getLocation().distanceSquared(player.getLocation()) <= this.aoeRadius * this.aoeRadius) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	protected ItemStack specialItem() {
//		ItemStack item = KitUtils.addClassUndropabbleSoulbound(new ItemStack(UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( )));
//		return KitUtils.setName(item, getSpecialItemName() + " " + instance.getReadyPrefix());
//	}
//
//	@Override
//	protected String defaultSpecialItemName() {
//		return ITEM_NAME;
//	}
//
//	@Override
//	protected boolean isSpecialItem(ItemStack stack) {
//		if (KitUtils.isSpecial(stack, true)) {
//			if (KitUtils.itemNameContains(stack, getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
//		if (p.hasTeam()) {
//			// Set Metadata
//			player.setMetadata(META_KEY, new FixedMetadataValue(AnnihilationMain.INSTANCE, Boolean.valueOf(true)));
//
//			// Remove Metadata
//			Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
//				@Override
//				public void run() {
//					player.removeMetadata(META_KEY, AnnihilationMain.INSTANCE);
//				}
//			}, this.aoeDuration * 20);
//
//			// Cancell Event
//			event.setCancelled(true);
//			player.updateInventory();
//			return true;
//		}
//		return true;
//	}
//
//	@Override
//	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
//		return false;
//	}
//
//	@Override
//	protected long getDefaultDelayLength() {
//		return 40000;
//	}
//
//	@Override
//	protected boolean useDefaultChecking() {
//		return true;
//	}
//
//	@Override
//	protected boolean useCustomMessage() {
//		return false;
//	}
//
//	@Override
//	protected String positiveMessage() {
//		return null;
//	}
//
//	@Override
//	protected String negativeMessage() {
//		return null;
//	}
//
//	@Override
//	protected int setInConfig(ConfigurationSection section) {
//		return 0; //Util.setDefaultIfNotSet(section, "Divide-Blocks-Regeneration-Time-By", 1.5);
//	}
//
//	@Override
//	protected String getInternalName() {
//		return "Enchanter";
//	}
//
//	@Override
//	protected ItemStack getDefaultIcon() {
//		return new ItemStack(UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( ));
//	}
//
//	@Override
//	protected void loadFromConfig(ConfigurationSection section) {
////		divideRegenerationTimeBy = section.getDouble("Divide-Blocks-Regeneration-Time-By", 1.5);
//	}
//
//	@Override
//	protected List<String> getDefaultDescription() {
//		List<String> l = new ArrayList<String>();
//		addToList(l, aqua + "You are the master.", "", aqua + "You get Extra Xp", aqua + "resources which enables",
//				aqua + "quicker level succession.", aqua + "", aqua + "There is a small chance",
//				aqua + "to obtain experience bottles", aqua + "when mining ores and chopping", aqua + "wood.");
//		return l;
//	}
//
//	@Override
//	protected Loadout getFinalLoadout() {
//		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(this.getSpecialItem());
//	}
//
//	@Override
//	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
//	}
//
//	@Override
//	public void cleanup(Player p) {
//		if (KitUtils.isValidPlayer(p) && p.hasMetadata(META_KEY)) {
//			p.removeMetadata(META_KEY, AnnihilationMain.INSTANCE);
//		}
//	}
//
//	@Override
//	public boolean onItemClick(Inventory inv, AnniPlayer a) {
//		this.addLoadoutToInventory(inv);
//		return true;
//	}
//}
