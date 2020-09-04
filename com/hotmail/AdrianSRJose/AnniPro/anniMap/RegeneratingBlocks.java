package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.mapBuilder.FutureBlockReplace;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class RegeneratingBlocks implements Listener {
	
	private static Map<Material, Map<Integer, RegeneratingBlock>> blocks;
	private static Map<String, List<BlockData>> BLOCKS_DATA = new HashMap<String, List<BlockData>>();
	private String world;
	private final ScheduledExecutorService executor;
	private final Random rand;
	
	public RegeneratingBlocks(String world) {
		this.world = world;
		blocks = new EnumMap<Material, Map<Integer, RegeneratingBlock>>(Material.class);
		rand = new Random(System.currentTimeMillis());
		executor = Executors.newScheduledThreadPool(3);
	}

	public RegeneratingBlocks(String world, ConfigurationSection configSection) {
		this(world);
		if (configSection != null) {
			for (String key : configSection.getKeys(false)) {
				if (key == null)
					continue;
				//
				ConfigurationSection matSection = configSection.getConfigurationSection(key);
				if (matSection != null) {
					for (String dataKey : matSection.getKeys(false)) {
						if (dataKey == null)
							continue;

						ConfigurationSection dataSection = matSection.getConfigurationSection(dataKey);
						if (dataSection != null) {
							Material mat = Material.getMaterial(dataSection.getString("Type"));
							Integer matData = dataSection.getInt("MaterialData");
							boolean regen = dataSection.getBoolean("Regenerate");
							boolean cobbleReplace = dataSection.getBoolean("CobbleReplace");
							boolean naturalBreak = dataSection.getBoolean("NaturalBreak");
							Integer time = dataSection.getInt("Time");
							
							TimeUnit unit;
							try {
								unit = TimeUnit.valueOf(dataSection.getString("Unit"));
							} catch (IllegalArgumentException e) {
								unit = null;
							}
							
							Integer xp = dataSection.getInt("XP");
							
							Material product;
							try {
								product = Material.getMaterial(dataSection.getString("Product"));
							} catch (Exception e) {
								e.printStackTrace();
								product = null;
							}
							
							String amount = dataSection.getString("Amount");
							Integer productData = dataSection.getInt("ProductData");
							String effect = dataSection.getString("Effect");
							this.addRegeneratingBlock(new RegeneratingBlock(mat, matData, regen, cobbleReplace,
									naturalBreak, time, unit, xp, product, amount, productData, effect, (byte) 0));
						}
					}
				}
			}
		}
	}

	public void addRegeneratingBlock(RegeneratingBlock block) {
		Map<Integer, RegeneratingBlock> datas = blocks.get(block.Type);
		if (datas == null)
			datas = new HashMap<Integer, RegeneratingBlock>();
		datas.put(block.MaterialData, block);
		blocks.put(block.Type, datas);
	}

	public static RegeneratingBlock getRegeneratingBlock(Material type, Integer data) {
		Map<Integer, RegeneratingBlock> datas = blocks.get(type);
		if (datas != null)
			return datas.get(data);
		else
			return null;
	}
	
	public static boolean addBlockToData(String key, final BlockData blockData) {
		// check key
		if (key == null) {
			return false;
		}
		
		// get list
		List<BlockData> data = BLOCKS_DATA.get(key);
		
		// check is not null
		if (data == null) {
			data = new ArrayList<BlockData>();
		}
		
		// add data
		data.add(blockData);
		
		// update data
		BLOCKS_DATA.put(key, data);
		return true;
	}

	public Map<Material, Map<Integer, RegeneratingBlock>> getRegeneratingBlocks() {
		return Collections.unmodifiableMap(blocks);
	}

	public void registerListeners(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterListeners() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void oreBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (!event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(world)) {
				return;
			}

			if (event.isCancelled()) {
				return;
			}

			// Get and Check Player
			final Player p = event.getPlayer();
			if (!KitUtils.isValidPlayer(p)) {
				return;
			}

			// get and check block
			final Block block = event.getBlock();
			if (block == null) {
				return;
			}
			
			boolean returnEvent = false;
			
			// check blocks data
			for (String key : BLOCKS_DATA.keySet()) {
				List<BlockData> datas = BLOCKS_DATA.get(key);
				if (datas == null || datas.isEmpty()) {
					continue;
				}
				
				for (BlockData data : datas) {
					if (data == null) {
						continue;
					}
					
					// get data vals
					Location loc       = data.getLocation();
					boolean canBeBreak = data.isCanBeBreak();
					
					// check
					if (block.getLocation().equals(loc)) {
						if (!canBeBreak) {
							returnEvent = true;
						}
					}
				}
			}
			
			if (returnEvent) {
				return;
			}

			// Get Universal ITEM PICKUP Sound
			final Sound        play = UniversalSound.ITEM_PICKUP.asBukkit();
			final AnniPlayer player = AnniPlayer.getPlayer(p.getUniqueId());
			if (player != null) {

				// Get And Check regeneration Block
				RegeneratingBlock b = getRegeneratingBlock(block.getType(), -1);
				if (b == null) {
					b = getRegeneratingBlock(block.getType(), ((int) block.getData()));
				}
				
				// check is not null
				if (b == null) {
					return;
				}
				
				// set block data
				b.setBlockData(block.getData());

				// Check Area
				if (b != null) {
					if (Game.getGameMap().getAreas() != null) {
						if (Game.getGameMap().getAreas().isInSomeArea(block.getLocation())) {
							if (!Config.MAP_LOADING_CAN_BREAK_REGENERATING_BLOCKS_ON_AREA.toBoolean()) {
								event.setCancelled(true);
								return;
							}
						}
					}
					
					// When is Natural Break
					if (b.NaturalBreak) {
						// Call Event
						ResourceBreakEvent e = new ResourceBreakEvent(player, b, block, 0, (ItemStack[]) null);
						AnniEvent.callEvent(e);

						if (!e.isCancelled()) {
							executor.schedule(new FutureBlockReplace(event.getBlock()),
									Math.max(e.getRegenerationTime(), 0), e.getRegenerationTimeUnit());
						} else {
							event.setCancelled(true);
						}
						return;
					}

					// Cancell Block Break Event
					event.setCancelled(true);
					if (!b.Regenerate)
						return;
					if (b.Effect == null) {
						int amount = 0;
						try {
							amount = Integer.parseInt(b.Amount);
						} catch (NumberFormatException e) {
							try {
								if (b.Amount.contains("RANDOM")) {
									String x, y;
									x = b.Amount.split(",")[0];
									y = b.Amount.split(",")[1];
									x = x.substring(7);
									y = y.substring(0, y.length() - 1);
									try {
										int min = Integer.parseInt(x);
										int max = Integer.parseInt(y);
										amount = min + (int) (Math.random() * ((max - min) + 1));
									} catch (NumberFormatException exx) {
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException ex) {
								return;
							}
						}
						
						ItemStack stack;
						int xp = b.XP;
						if (b.ProductData != -1)
							stack = new ItemStack(b.Product, amount, (byte) b.ProductData);
						else
							stack = new ItemStack(b.Product, amount);
						
						//
						ResourceBreakEvent e = new ResourceBreakEvent(player, b, block, xp, stack);
						AnniEvent.callEvent(e);
						//
						if (!e.isCancelled()) {
							// Play sound and give XP
							p.playSound(p.getLocation(), play, 30, rand.nextFloat() + 1);
							p.giveExp(e.getXP());
//							p.setExp(p.getExp() + e.getXP());

							// Fortune
							final ItemStack hand = p.getItemInHand();
							for (int x = 1; x <= 3; x++) {
								if (hand == null || !Config.USE_FORTUNE.toBoolean()) {
									break;
								}

								// multiply? | and get Material
								if (hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == x) {
									boolean multiply = false;
									Material add = null;

									switch (block.getType()) {
									case DIAMOND_ORE:
										add = Material.DIAMOND;
										multiply = Config.FORTUNE_DIAMOND.toBoolean();
										break;
									case COAL_ORE:
										add = Material.COAL;
										multiply = Config.FORTUNE_COAL.toBoolean();
										break;
									case EMERALD_ORE:
										add = Material.EMERALD;
										multiply = Config.FORTUNE_EMERALD.toBoolean();
										break;
									case IRON_ORE:
										add = Material.IRON_ORE;
										multiply = Config.FORTUNE_IRON.toBoolean();
										break;
									case GOLD_ORE:
										add = Material.GOLD_ORE;
										multiply = Config.FORTUNE_GOLD.toBoolean();
										break;

									default:
										multiply = false;
										add = null;
										break;
									}

									// Add
									if (add != null && multiply) {
										p.getInventory().addItem(new ItemStack(add, x));
										p.updateInventory();
									}
								}
							}

							// Add Productos
							if (e.getProducts() != null) {
								for (ItemStack pd : e.getProducts()) {
									if (pd == null) {
										continue;
									}

									p.getInventory().addItem(pd);
									p.updateInventory();
								}
							}

							// Task and Metadata
							executor.schedule(new FutureBlockReplace(event.getBlock(), b.CobbleReplace),
									Math.max(e.getRegenerationTime(), 0), e.getRegenerationTimeUnit());
							Util.setMetadata(block, "RegeneratingBlock");
						}
					} else if (b.Effect.equalsIgnoreCase("Gravel")) {
						List<ItemStack> l = new ArrayList<ItemStack>();
						for (int x = 0; x < 5; x++) {
							int z;
							//
							switch (x) {
							case 0:
								z = rand.nextInt(2);
								if (z != 0)
									l.add(new ItemStack(Material.BONE, z));
								break;
							case 1:
								z = rand.nextInt(3);
								if (z != 0)
									l.add(new ItemStack(Material.FEATHER, z));
								break;
							case 2:
								z = rand.nextInt(4);
								if (z != 0)
									l.add(new ItemStack(Material.ARROW, z));
								break;
							case 3:
								z = rand.nextInt(2);
								if (z != 0)
									l.add(new ItemStack(Material.STRING, z));
								break;
							case 4:
								z = rand.nextInt(3);
								if (z != 0)
									l.add(new ItemStack(Material.FLINT, z));
								break;
							}
						}
						//
						ResourceBreakEvent e = new ResourceBreakEvent(player, b, block, b.XP,
								l.toArray(new ItemStack[l.size()]));
						AnniEvent.callEvent(e);
						//
						if (!e.isCancelled()) {
							// Play sound and give XP
							p.playSound(p.getLocation(), play, 30, rand.nextFloat() + 1.0F);
							p.giveExp(e.getXP());

							// Add Productos
							if (e.getProducts() != null) {
								for (ItemStack pd : e.getProducts()) {
									if (pd == null) {
										continue;
									}

									p.getInventory().addItem(pd);
									p.updateInventory();
								}
							}

							// Task and Metadata
							executor.schedule(new FutureBlockReplace(event.getBlock(), b.CobbleReplace),
									Math.max(e.getRegenerationTime(), 0), e.getRegenerationTimeUnit());
							Util.setMetadata(block, "RegeneratingBlock");
						}
						return;
					}
				}
			}
		} else {
			if (isRegeneratingBlock(event.getBlock())) {
				if (event.getBlock().hasMetadata("RegeneratingBlock"))
					Util.removeMetadata(event.getBlock(), "RegeneratingBlock");
			}
		}
	}

	@EventHandler
	public void onP(BlockPlaceEvent eve) {
		final Player p = eve.getPlayer();
		final Block block = eve.getBlockPlaced();
		if (hasRegeneratingBlockMetadata(block.getLocation())) {
			if (p.getGameMode() != GameMode.CREATIVE) {
				eve.setCancelled(true);
				Areas.playAntiBuildEffect(block, true);
			} else
				removeRegeneratingBlockMetadata(block);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void cobblestoneBreak(final BlockBreakEvent event) {
		// get and check block.
		final Block block = event.getBlock();
		if (block.getType() == Material.COBBLESTONE
				&& block.hasMetadata(FutureBlockReplace.COBBLESTONE_REGENERATING_BLOCKS_REPLACER_METADATA)) {
			// check is disallowed break.
			if (!Config.MAP_LOADING_CAN_BREAK_REGENERATING_BLOCKS_COBBLESTONE.toBoolean()) {
				// cancell break.
				event.setCancelled(true);
			}
		}
	}

	public static boolean isRegeneratingBlock(Block block) {
		RegeneratingBlock b = getRegeneratingBlock(block.getType(), -1);
		if (b == null)
			b = getRegeneratingBlock(block.getType(), ((int) block.getData()));
		if (b != null)
			return true;
		return false;
	}

	public static boolean isRegeneratingBlock(Location loc) {
		RegeneratingBlock b = getRegeneratingBlock(loc.getBlock().getType(), -1);
		if (b == null)
			b = getRegeneratingBlock(loc.getBlock().getType(), ((int) loc.getBlock().getData()));
		if (b != null)
			return true;
		return false;
	}

	public static boolean hasRegeneratingBlockMetadata(Block block) {
		if (block.hasMetadata("RegeneratingBlock"))
			return true;
		return false;
	}

	public static boolean hasRegeneratingBlockMetadata(Location loc) {
		if (loc != null && loc.getBlock() != null)
			if (loc.getBlock().hasMetadata("RegeneratingBlock"))
				return true;
		//
		return false;
	}

	public static void removeRegeneratingBlockMetadata(Block block) {
		if (block != null)
			if (block.hasMetadata("RegeneratingBlock"))
				block.removeMetadata("RegeneratingBlock", AnnihilationMain.INSTANCE);
	}

	public static void removeRegeneratingBlockMetadata(Location loc) {
		if (loc != null && loc.getBlock() != null)
			if (loc.getBlock().hasMetadata("RegeneratingBlock"))
				loc.getBlock().removeMetadata("RegeneratingBlock", AnnihilationMain.INSTANCE);
	}

	public boolean removeRegeneratingBlock(Material type, Integer data) {
		if (blocks.containsKey(type)) {
			if (data == -1) {
				blocks.remove(type);
				return true;
			}

			Map<Integer, RegeneratingBlock> datas = blocks.get(type);
			if (datas != null) {
				if (datas.containsKey(data)) {
					datas.remove(data);
					return true;
				}
			}
		}
		return false;
	}

	public int saveToConfig(ConfigurationSection configSection) {
		int save = 0;
		//
		if (configSection != null) {
			for (Entry<Material, Map<Integer, RegeneratingBlock>> entry : blocks.entrySet()) {
				if (entry != null) {
					save += Util.createSectionIfNoExitsInt(configSection, entry.getKey().name());
					ConfigurationSection matSection = configSection.createSection(entry.getKey().name());
					//
					for (Entry<Integer, RegeneratingBlock> map : entry.getValue().entrySet()) {
						if (map != null) {
							save += Util.createSectionIfNoExitsInt(matSection, map.getKey().toString());
							ConfigurationSection dataSection = matSection.createSection(map.getKey().toString());
							//
							RegeneratingBlock b = map.getValue();
							if (b != null)
								b.saveToConfig(dataSection);
						}
					}
				}
			}
		}
		//
		return save;
	}
}
