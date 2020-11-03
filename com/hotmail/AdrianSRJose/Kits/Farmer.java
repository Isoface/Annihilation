package com.hotmail.AdrianSRJose.Kits;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.RandomUtils;
import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.itemstack.ItemStackUtils;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 22 October, 2020 / 01:48 PM
 */
public class Farmer extends ClassItemKit {
	
	/** our task executor */
	private static final ScheduledExecutorService TASK_EXECUTOR = Executors.newScheduledThreadPool ( 3 );
	
	/** type of items for giving to farmers when breaking tall grass */
	private static final Material [ ] TALLGRASS_DROP_TYPES = 
		{
				UniversalMaterial.WHEAT_SEEDS.getMaterial ( ) , 
				UniversalMaterial.POTATO_ITEM.getMaterial ( ) , 
				UniversalMaterial.CARROT_ITEM.getMaterial ( ) 
		};
	
	/** feast ability range, in blocks */
	private static final int FEAST_RANGE = 10;
	
	/** how much saturation farmer and its teammates within {@link #FEAST_RANGE} will receive when feast */
	private static final float FEAST_SATURATION = 4.0F;
	
	/** chance farmers has to get eating reward */
	private static final double EATING_REWARD_CHANCE = 30.0D;
	
	/** how much points of health eating reward restores */
	private static final double EATING_REWARD_HEALTH = 2.0D;
	
	/** how much points of saturation eating reward gives */
	private static final float EATING_REWARD_SATURATION = 2.0F;
	
	/** key for identifying farmer farmlands  */
	private static final String FARMER_FARMLAND_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	/** metadata for identifying farmer farmlands  */
	private static final MetadataValue FARMER_FARMLAND_METADATA = new FixedMetadataValue ( AnnihilationMain.INSTANCE , true );
	
	/** time in seconds crops broken by a farmer will take to start growing back */
	private static final long CROPS_GROW_BACK_TIME = 6L;
	
	/** time in seconds crops that grew after {@link #CROPS_GROW_BACK_TIME} will take to fully grow */
	private static final long CROPS_FULLY_GROW_BACK_TIME = 40L;
	
	/** crops grow tasks */
	private static final Map < String , CropGrowTask > CROP_GROW_TASKS = new HashMap < > ( );
	
	/** reward items for farmers when harvesting crops */
	private static final ChanceMap CROPS_HARVESTING_REWARDS = new ChanceMap 
			( 
					new ChanceItem ( 3 , UniversalMaterial.GLASS_BOTTLE.getMaterial ( ) ) ,
					new ChanceItem ( 7 , UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( ) ) ,
					new ChanceItem ( 2 , UniversalMaterial.APPLE.getMaterial ( ) ) ,
					new ChanceItem ( 5 , UniversalMaterial.IRON_HOE.getMaterial ( ) ) ,
					new ChanceItem ( 4 , UniversalMaterial.GOLD_ORE.getMaterial ( ) ) ,
					new ChanceItem ( 5 , UniversalMaterial.IRON_ORE.getMaterial ( ) ) ,
					new ChanceItem ( 2 , UniversalMaterial.BOOK.getMaterial ( ) ) ,
					new ChanceItem ( 4 , UniversalMaterial.SOUL_SAND.getMaterial ( ) ) ,
					new ChanceItem ( 7 , UniversalMaterial.NETHER_WART.getMaterial ( ) ) ,
					new ChanceItem ( 2 , UniversalMaterial.GHAST_TEAR.getMaterial ( ) ) ,
					
					// iron nugget available since v1_11_R1
					ServerVersion.serverOlderThan ( ServerVersion.v1_11_R1 ) 
						? new ChanceItem ( 2 , UniversalMaterial.IRON_INGOT.getMaterial ( ) )
						: new ChanceItem ( 6 , UniversalMaterial.IRON_NUGGET.getMaterial ( ) ) ,
					
					new ChanceItem ( 5 , UniversalMaterial.GOLD_NUGGET.getMaterial ( ) )
			);
	
	/** chance to receive a reward when harvesting crops */
	private static final double CROPS_HARVESTING_REWARDS_CHANCE = 20.0D;
	
	/**
	 * Our crops grow system.
	 * <p>
	 * @author AdrianSR / Friday 23 October, 2020 / 11:21 AM
	 */
	private static class CropGrowTask implements Runnable {
		
		/** crops to grow */
		private final Block block;
		/** our handle */
		private ScheduledFuture < ? > handle;
		
		private CropGrowTask ( Block block ) {
			Validate.isTrue ( block.getState ( ).getData ( ) instanceof Crops , "block must be a crop!" );
			this.block = block;
		}
		
		private void start ( long grow_time ) {
			long period = ( grow_time / ( CropState.values ( ).length - 1 /* decreasing 1 to exclude first state */ ) ) + 1L;
			handle      = TASK_EXECUTOR.scheduleAtFixedRate ( this , period , period , TimeUnit.SECONDS );
		}
		
		private void cancel ( ) {
			if ( handle != null ) {
				handle.cancel ( true );
				handle = null;
			}
		}

		@SuppressWarnings ( "deprecation" ) @Override
		public void run ( ) {
			if ( !( block.getState ( ).getData ( ) instanceof Crops ) ) {
				// it is not a crop anymore for an unknown reason.
				handle.cancel ( true );
				return;
			}
			
			final CropState [ ] states = CropState.values ( );
			final CropState    current = CropState.getByData ( block.getData ( ) );
			
			if ( current != CropState.RIPE ) {
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( AnnihilationMain.INSTANCE , new Runnable ( ) {
					@Override public void run ( ) {
						// ((Crops) block.getState ( ).getData ( )).setState() is not actually working :(.
						block.setData ( states [ ArrayUtils.indexOf ( states , current ) + 1 ].getData ( ) );
					}
				} );
			} else {
				handle.cancel ( true );
			}
		}
	}
	
	/**
	 * Getting random {@link ChanceItem}s.
	 * <p>
	 * @author AdrianSR / Friday 23 October, 2020 / 03:15 PM
	 */
	private static class ChanceMap {
		
		private ChanceItem map [ ];
		
		private ChanceMap ( ChanceItem... items ) {
			this.map = new ChanceItem [ items.length ];
			
			for ( ChanceItem item : items ) {
				for ( int x = 0 ; x < item.chance ; x ++ ) {
					this.map = ( ChanceItem [ ] ) ArrayUtils.add ( map , item );
				}
			}
		}
		
		/**
		 * Returns a random {@link ChanceItem}.
		 * <p>
		 * @return the next random chance item.
		 */
		private ChanceItem next ( ) {
			if ( map.length > 0 ) {
				return map [ RandomUtils.RANDOM.nextInt ( map.length ) ];
			} else {
				throw new IllegalStateException ( "there is no chance items!" );
			}
		}
		
		/**
		 * Returns a random {@link ChanceItem} only if: {@code RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= chance}.
		 * <p>
		 * @param chance the chance that must be {@code < 100.0D}.
		 * @return the next random chance item, or null.
		 */
		private ChanceItem nextChance ( double chance ) {
			if ( RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= chance ) {
				return next ( );
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Wrappes a {@link ItemStack} with a chance.
	 * <p>
	 * @author AdrianSR / Friday 23 October, 2020 / 03:15 PM
	 */
	private static class ChanceItem {
		
		private final int     chance;
		private final ItemStack item;
		
		private ChanceItem ( int chance , ItemStack item ) {
			this.chance = chance;
			this.item   = item;
		}
		
		private ChanceItem ( int chance , Material item ) {
			this ( chance , new ItemStack ( item , 1 ) );
		}
	}
	
	@Override
	protected int setInConfig ( ConfigurationSection section ) {
		// TODO: make configurable some values.
		return 0;
	}
	
	@Override
	protected void loadFromConfig ( ConfigurationSection section ) {
		// TODO: make configurable some values.
	}

	@Override
	protected ItemStack specialItem ( ) {
		return ItemStackUtils.setName ( 
				KitUtils.addClassUndropabbleSoulbound ( new ItemStack ( UniversalMaterial.GOLDEN_CARROT.getMaterial ( ) ) ) , 
				getSpecialItemName ( ) + instance.getReadyPrefix ( ) );
	}

	@Override
	protected String defaultSpecialItemName ( ) {
		return ChatColor.YELLOW + "Feast";
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
	protected boolean performPrimaryAction ( Player player , AnniPlayer anni , PlayerInteractEvent event ) {
		final Location feast_location = player.getLocation ( );
		
		anni.getTeam ( ).getPlayers ( ).stream ( ).filter ( AnniPlayer :: isOnline ).forEach ( teammate -> {
			Player mate = teammate.getPlayer ( );
			
			// this affects only the farmer and teammates within the FEAST_RANGE
			if ( mate.getUniqueId ( ).equals ( player.getUniqueId ( ) ) 
					|| feast_location.distance ( mate.getLocation ( ) ) <= FEAST_RANGE ) {
				// TODO: distance or distance squared ( check which is the right one )
				mate.removePotionEffect ( PotionEffectType.HUNGER );
				mate.setFoodLevel ( 20 );
				mate.setSaturation ( mate.getSaturation ( ) + FEAST_SATURATION );
			}
		} );
		return true;
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onTallGrassBreaking ( BlockBreakEvent event ) {
		AnniPlayer player = AnniPlayer.getPlayer ( event.getPlayer ( ) );
		Block       block = event.getBlock ( );
		
		if ( ( block.getType ( ) != UniversalMaterial.TALL_GRASS.getMaterial ( ) 
				&& !block.getType ( ).name ( ).equals ( "DOUBLE_PLANT" ) ) 
				|| !hasThisKit ( player ) ) {
			return;
		}
		
		// as we're going to use custom drops and bukkit API doesn't allow to change it
		// directly, we've to disable this option, and drop the custom stuff ourselves.
		if ( ServerVersion.getVersion ( ).isOlderThan ( ServerVersion.v1_12_R1 ) ) {
			// versions older than 1_12_R1 doesn't have the option to cancel the drops, so
			// we have to improvise.
			event.setCancelled ( true );
			event.getBlock ( ).setType ( Material.AIR );
		} else {
			event.setDropItems ( false );
		}
		
		// according to https://minecraft.gamepedia.com/Drops, there are 12.5% of chance to get drop.
		if ( RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= 12.5D ) {
			block.getWorld ( ).dropItemNaturally ( event.getBlock ( ).getLocation ( ) , 
					new ItemStack ( TALLGRASS_DROP_TYPES [ RandomUtils.RANDOM.nextInt ( TALLGRASS_DROP_TYPES.length ) ] , 1 ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onHarvesting ( BlockBreakEvent event ) {
		final AnniPlayer  player = AnniPlayer.getPlayer ( event.getPlayer ( ) );
		final Block        block = event.getBlock ( );
		final Block     farmland = block.getRelative ( BlockFace.DOWN );
		final Material crop_type = block.getType ( );
		
		if ( farmland.getType ( ) == UniversalMaterial.FARMLAND.getMaterial ( ) ) {
			farmland.setMetadata ( FARMER_FARMLAND_METADATA_KEY , FARMER_FARMLAND_METADATA );
		} else {
			// this is to avoid a bug (it could be unnecesary, but it doesn't make harm).
			return;
		}
		
		if ( hasThisKit ( player ) && ( block.getType ( ) == Material.CROPS 
				|| UniversalMaterial.match ( block.getType ( ).name ( ) ) == UniversalMaterial.POTATO_ITEM 
				|| UniversalMaterial.match ( block.getType ( ).name ( ) ) == UniversalMaterial.CARROT ) ) {
			
			// dropping special items, only if the crop is ripe.
			Crops crops = (Crops) block.getState ( ).getData ( );
			if ( crops.getState ( ) == CropState.RIPE ) {
				ChanceItem harvesting_reward = CROPS_HARVESTING_REWARDS.nextChance ( CROPS_HARVESTING_REWARDS_CHANCE );
				
				if ( harvesting_reward != null ) {
					block.getWorld ( ).dropItemNaturally ( block.getLocation ( ) , harvesting_reward.item );
				}
			}
			
			// here we're cancelling the corresponding grow task if any.
			final String grow_task_key = block.getLocation ( ).toString ( );
			
			if ( CROP_GROW_TASKS.containsKey ( grow_task_key ) ) {
				CropGrowTask task = CROP_GROW_TASKS.get ( grow_task_key );
				if ( task != null ) {
					task.cancel ( );
				}
				
				CROP_GROW_TASKS.remove ( grow_task_key );
			}
			
			// ok, let's grow it back.
			TASK_EXECUTOR.schedule ( new Runnable ( ) {
				@Override public void run ( ) {
					Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( AnnihilationMain.INSTANCE , new Runnable ( ) {
						@Override public void run ( ) {
							// if it still being air, means that the farmer is waiting it to start growing automatically.
							if ( block.getType ( ) == UniversalMaterial.AIR.getMaterial ( ) ) {
								block.setType ( crop_type );
								block.getState ( ).update ( true );
								
								// now we start the grow task.
								CropGrowTask grow_task = new CropGrowTask ( block );
								
								grow_task.start ( CROPS_FULLY_GROW_BACK_TIME );
								CROP_GROW_TASKS.put ( grow_task_key , grow_task );
							}
						}
					} );
				}
			} , CROPS_GROW_BACK_TIME, TimeUnit.SECONDS );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onGrow ( BlockGrowEvent event ) {
		Block    block = event.getBlock ( );
		Block farmland = block.getRelative ( BlockFace.DOWN );
		
		if ( block.getState ( ).getData ( ) instanceof Crops 
				&& farmland.getType ( ) == UniversalMaterial.FARMLAND.getMaterial ( ) 
				&& farmland.hasMetadata ( FARMER_FARMLAND_METADATA_KEY ) ) {
			// as we're implementing our crop grow system, we cancell this to avoid weird behaviors.
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onFarmlandDamage ( PlayerInteractEvent event ) {
		AnniPlayer player = AnniPlayer.getPlayer ( event.getPlayer ( ) );
		Block       block = event.getClickedBlock ( );
		
		if ( event.getAction ( ) == Action.PHYSICAL && block.getType ( ) == UniversalMaterial.FARMLAND.getMaterial ( ) 
				&& hasThisKit ( player ) ) {
			// here we avoid the farmland from being damaged by a farmer that
			// walks/jumps on it.
			event.setCancelled ( true );
		}
	}
	
	@SuppressWarnings ( "deprecation" ) @EventHandler ( priority = EventPriority.NORMAL , ignoreCancelled = true )
	public void onEating ( PlayerItemConsumeEvent event ) { 
		Player player = event.getPlayer ( );
		
		if ( hasThisKit ( player ) && event.getItem ( ).getType ( ).isEdible ( ) ) {
			// there are EATING_REWARD_CHANCE of chance to get a reward when eating.
			if ( RandomUtils.RANDOM.nextDouble ( ) * 100.0D <= EATING_REWARD_CHANCE ) {
				player.setSaturation ( player.getSaturation ( ) + EATING_REWARD_SATURATION );
				player.setHealth ( Math.min ( player.getHealth ( ) + EATING_REWARD_HEALTH , player.getMaxHealth ( ) ) );
			}
		}
	}

	@Override
	protected long getDefaultDelayLength ( ) {
		return 30 * 1000; // 30 seconds
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
		return "Farmer";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.WHEAT_SEEDS.getMaterial ( ) );
	}

	@Override
	protected List < String > getDefaultDescription ( ) {
		return Arrays.asList
				( 
				aqua + "You are the supplier" ,
				aqua + "" ,
				aqua + "Keep your team fed" ,
				aqua + "and at maximum health." ,
				aqua + "" ,
				aqua + "Your Feast ability instantly" ,
				aqua + "replenishes the hunger" ,
				aqua + "of any allies near you." ,
				aqua + "" ,
				aqua + "You also find items" ,
				aqua + "while breaking grass."
				);
	}

	@Override
	protected Loadout getFinalLoadout ( ) {
		return new Loadout ( )
				.addWoodSword ( )
				.addWoodPick ( )
				.addWoodAxe ( )
				.addItem ( getSpecialItem ( ) )
				.addItem ( new ItemStack ( Material.STONE_HOE ) )
				.addItem ( new ItemStack ( Material.BONE , 5 ) );
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