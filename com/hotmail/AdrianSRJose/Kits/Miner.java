package com.hotmail.AdrianSRJose.Kits;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import com.hotmail.AdrianSRJose.base.ClassItemKit;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 27 October, 2020 / 02:47 PM
 */
public class Miner extends ClassItemKit {
	
	/** key for identifying rushing miners */
	protected static final String RUSHING_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	/**
	 * Miner kit configuration.
	 * <p>
	 * @author AdrianSR / Tuesday 27 October, 2020 / 03:06 PM
	 */
	protected static enum Configuration {
		
		DOUBLE_DROP_CHANCE ( "double-drop-chance" , 80.0D ) ,
		RUSH_DOUBLE_DROP_CHANCE ( "rush-double-drop-chance" , 100.0D ) ,
		RUSH_TRIPLE_DROP_CHANCE ( "rush-triple-drop-chance" , 33.0D ),
		RUSH_DURATION ( "rush-duration" , 10L ) ,
		
		/** whether the drop chance will affect only ore resources */
		AFFECT_ONLY_ORES ( "affect-only-ores" , true ) {
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
		};
		
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
		
		public double getAsDouble ( ) {
			if ( default_value instanceof Number ) {
				return value instanceof Number 
						? ((Number) getValue ( )).doubleValue ( ) 
						: ((Number) default_value).doubleValue ( );
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
		return ChatColor.YELLOW + "Rush";
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
		// here we're saving the millis of the instant when the player actives the rush.
		player.setMetadata ( RUSHING_METADATA_KEY , new FixedMetadataValue ( AnnihilationMain.INSTANCE , System.currentTimeMillis ( ) ) );
		return true;
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onResourceBreak ( ResourceBreakEvent event ) {
		Player        player = event.getPlayer ( ).getPlayer ( );
		ItemStack [ ] stacks = event.getProducts ( );
		
		if ( hasThisKit ( player ) ) {
			boolean double_drop = false;
			boolean triple_drop = false;
			
			if ( isRushing ( player ) ) {
				// when is rushing.
				double double_drop_chance = Math.max ( 
						Math.min ( Configuration.RUSH_DOUBLE_DROP_CHANCE.getAsDouble ( ) , 100.0D ) , 0.0D );
				double triple_drop_chance = Math.max ( 
						Math.min ( Configuration.RUSH_TRIPLE_DROP_CHANCE.getAsDouble ( ) , 100.0D ) , 0.0D );
				
				if ( double_drop_chance > 0.0D 
						&& RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= double_drop_chance ) {
					double_drop = true;
				}
				
				if ( triple_drop_chance > 0.0D 
						&& RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= triple_drop_chance ) {
					triple_drop = true;
				}
			} else {
				// when is not rushing.
				double double_drop_chance = Math.max ( 
						Math.min ( Configuration.DOUBLE_DROP_CHANCE.getAsDouble ( ) , 100.0D ) , 0.0D );
				
				if ( double_drop_chance > 0.0D 
						&& RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= double_drop_chance ) {
					double_drop = true;
				}
			}
			
			if ( stacks != null && ( triple_drop || double_drop ) ) {
				if ( event.getResource ( ).Type.name ( ).contains ( "_ORE" ) 
						|| !Configuration.AFFECT_ONLY_ORES.getAsBoolean ( ) ) {
					for ( int x = 0 ; x < stacks.length ; x ++ ) {
						ItemStack stack = stacks [ x ];
						
						if ( stack != null ) {
							if ( triple_drop ) {
								stack.setAmount ( stack.getAmount ( ) * 3 );
							} else if ( double_drop ) {
								stack.setAmount ( stack.getAmount ( ) * 2 );
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets whether the desired {@code player} is rushing. 
	 * <p>
	 * @param player the player to check.
	 * @return true if rushing.
	 */
	protected boolean isRushing ( Player player ) {
		if ( player.hasMetadata ( RUSHING_METADATA_KEY ) ) {
			List < MetadataValue > metadata = player.getMetadata ( RUSHING_METADATA_KEY );
			
			if ( metadata.size ( ) > 0 ) {
				MetadataValue value = metadata.get ( 0 );
				if ( TimeUnit.MILLISECONDS.toSeconds ( 
						System.currentTimeMillis ( ) - value.asLong ( ) ) <= Configuration.RUSH_DURATION.getAsLong ( ) ) {
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
		return 60 * 1000; // 60 seconds
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
		return "Miner";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.STONE_PICKAXE.getMaterial ( ) , 1 );
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
						aqua + "You are the backbone.", 
						"",
						aqua + "You support the war effort", 
						aqua + "by gathering the raw materials",
						aqua + "your teammates need.",
						"",
						aqua + "You get a chance for double",
						aqua + "ore and a special ability",
						aqua + "that gives you more ores."
				);
	}

	@Override
	protected Loadout getFinalLoadout ( ) {
		return new Loadout ( )
				.addWoodSword ( )
				.addSoulboundEnchantedItem ( new ItemStack ( UniversalMaterial.STONE_PICKAXE.getMaterial ( ) ) , Enchantment.DIG_SPEED , 1 )
				.addWoodAxe ( )
				.addItem ( getSpecialItem ( ) )
				.addItem ( new ItemStack ( UniversalMaterial.FURNACE.getMaterial ( ) , 1 ) )
				.addItem ( new ItemStack ( UniversalMaterial.COAL.getMaterial ( ) , 8 ) );
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