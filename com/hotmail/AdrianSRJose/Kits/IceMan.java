package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.base.Function;
import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.base.Delays;
import com.hotmail.AdrianSRJose.base.StandardItemUpdater;

public class IceMan extends ClassItemKit {
	
	@Override
	protected void setUp() {
		delays      = Delays.getInstance();
		specialItem = specialItem();
		if (delay > 0 && useDefaultChecking()) {
			delays.createNewDelay(getInternalName(), new StandardItemUpdater(getSpecialItemName(),
					specialItem.getType(), new Function<ItemStack, Boolean>() {
						@Override
						public Boolean apply(ItemStack stack) {
							return false; //isSpecialItem(stack);
						}
					}));
		}
		onInitialize();
	}
	
	private final Map<UUID, Boolean> uses = new HashMap<UUID, Boolean>();
	private String enabledName = "&eFreeze &aEnabled";
	// Lily Pad
	private String lilyPadName = "&eLily Pad";
	private String disabledName = "&eFreeze &cDisabled";
	private String enabledMessage = ChatColor.GREEN + "Freeze Enabled";
	private String disabledMessage = ChatColor.RED + "Freeze Disabled";

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "LiliPads-Name", "&eLily Pad")
				+ Util.setDefaultIfNotSet(section, "Enabled-Name", "&eFreeze &aEnabled")
				+ Util.setDefaultIfNotSet(section, "Disabled-Name", "&eFreeze &cDisabled")
				+ Util.setDefaultIfNotSet(section, "Enabled-Message", "&aFreeze Enabled")
				+ Util.setDefaultIfNotSet(section, "Disabled-Message", "&cFreeze Disabled");
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section == null) {
			return;
		}

		lilyPadName = Util.wc(section.getString("LiliPads-Name", lilyPadName));
		enabledName = Util.wc(section.getString("Enabled-Name", enabledName));
		disabledName = Util.wc(section.getString("Disabled-Name", disabledName));
		enabledMessage = Util.wc(section.getString("Enabled-Message", enabledMessage));
		disabledMessage = Util.wc(section.getString("Disabled-Message", disabledMessage));
	}

	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.ICE.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(enabledName);
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}
	
	private ItemStack getLilyPads() {
		ItemStack lp = new ItemStack ( UniversalMaterial.LILY_PAD.getMaterial ( ) , 10);
		return KitUtils.addClassSoulbound(KitUtils.setName(lp, lilyPadName));
	}

	@Override
	protected String defaultSpecialItemName() {
		return "";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			return KitUtils.extractName(stack, false).equalsIgnoreCase(enabledName)
					|| KitUtils.extractName(stack, false).equalsIgnoreCase(disabledName);
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		if (!uses.containsKey(p.getUniqueId())) {
			uses.put(p.getUniqueId(), Boolean.valueOf(true));
		}

		uses.replace(p.getUniqueId(), uses.get(p.getUniqueId()), (!uses.get(p.getUniqueId())));
		if (uses.get(p.getUniqueId())) {
			ItemMeta meta = event.getItem().getItemMeta();
			meta.setDisplayName(enabledName);
			event.getItem().setItemMeta(meta);
			p.updateInventory();
			p.sendMessage(Util.wc(enabledMessage));
		} else {
			ItemMeta meta = event.getItem().getItemMeta();
			meta.setDisplayName(disabledName);
			event.getItem().setItemMeta(meta);
			p.updateInventory();
			p.sendMessage(Util.wc(disabledMessage));
		}
		return true;
	}

//	public boolean noisInWater(Player p) {
//		if (p.getLocation().getBlock().getType() == Material.AIR
//				&& p.getLocation().getBlock().getType() != Material.STATIONARY_WATER
//				&& p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.STATIONARY_WATER
//				&& p.getLocation().getBlock().getRelative(BlockFace.SELF).getType() != Material.STATIONARY_WATER)
//			if (p.getLocation().getBlock().getType() != Material.WATER
//					&& p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.WATER
//					&& p.getLocation().getBlock().getRelative(BlockFace.SELF).getType() != Material.WATER)
//				return true;
//		return false;
//	}
	
	public boolean noisInWater(Player p) {
		if (p.getLocation().getBlock().getType() == UniversalMaterial.AIR.getMaterial ( )
				&& !p.getLocation().getBlock().getType().name().equals("STATIONARY_WATER")
				&& !p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().name().equals("STATIONARY_WATER")
				&& !p.getLocation().getBlock().getRelative(BlockFace.SELF).getType().name().equals("STATIONARY_WATER"))
			if (!p.getLocation().getBlock().getType().name().equals("WATER")
					&& !p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().name().equals("WATER")
					&& !p.getLocation().getBlock().getRelative(BlockFace.SELF).getType().name().equals("WATER"))
				return true;
		return false;
	}
	
	public boolean freezable(Block block) {
		return block.getType().name().equals("WATER") || block.getType().name().equals("STATIONARY_WATER");
	}

	@EventHandler
	public void Freeze(PlayerMoveEvent eve) {
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (eve.getTo() == null) {
			return;
		}
		
		if (ap.isSpectator() || p.getGameMode() == GameMode.SPECTATOR) {
			return;
		}

		final Block to = eve.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (!KitUtils.isOnLobby(p)) {
			if (Game.isGameRunning() && noisInWater(p)
					&& p.getLocation().getWorld().getName().equals(Game.getGameMap().getWorldName())) {
				if (!uses.containsKey(p.getUniqueId())
						|| uses.containsKey(p.getUniqueId()) && uses.get(p.getUniqueId())) {
					Location t = eve.getTo();
					Location f = eve.getFrom();

					if (t.getBlockX() != f.getBlockX() || t.getBlockY() != f.getBlockY()
							|| t.getBlockZ() != f.getBlockY()) {
						if (ap != null && ap.getTeam() != null && ap.getKit().equals(this)) {
							final int x = eve.getTo().getBlockX();
							final int y = eve.getTo().getBlockY();
							final int z = eve.getTo().getBlockZ();

//							final Material nw = UniversalMaterial.WATER.getMaterial ( );
//							final Material sw = UniversalMaterial.WATER.getMaterial ( );
							final Material i = UniversalMaterial.ICE.getMaterial ( );
							
							if (freezable(to.getWorld().getBlockAt(x, y - 1, z))) {
								to.getWorld().getBlockAt(x, y - 1, z).setType(i);
								to.getWorld().getBlockAt(x, y - 1, z).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x, y - 1, z).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 1, y - 1, z))) {
								to.getWorld().getBlockAt(x + 1, y - 1, z).setType(UniversalMaterial.ICE.getMaterial ( ));
								
								to.getWorld().getBlockAt(x + 1, y - 1, z).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 1, y - 1, z).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 2, y - 1, z))) {
								to.getWorld().getBlockAt(x + 2, y - 1, z).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 2, y - 1, z).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 2, y - 1, z).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 2, y - 1, z + 1))) {
								to.getWorld().getBlockAt(x + 2, y - 1, z + 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 2, y - 1, z + 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 2, y - 1, z + 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 2, y - 1, z - 1))) {
								to.getWorld().getBlockAt(x + 2, y - 1, z - 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 2, y - 1, z - 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 2, y - 1, z - 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 1, y - 1, z))) {
								to.getWorld().getBlockAt(x - 1, y - 1, z).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 1, y - 1, z).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 1, y - 1, z).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 2, y - 1, z))) {
								to.getWorld().getBlockAt(x - 2, y - 1, z).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 2, y - 1, z).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 2, y - 1, z).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 2, y - 1, z - 1))) {
								to.getWorld().getBlockAt(x - 2, y - 1, z - 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 2, y - 1, z - 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 2, y - 1, z - 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 2, y - 1, z + 1))) {
								to.getWorld().getBlockAt(x - 2, y - 1, z + 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 2, y - 1, z + 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 2, y - 1, z + 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x, y - 1, z + 1))) {
								to.getWorld().getBlockAt(x, y - 1, z + 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x, y - 1, z + 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x, y - 1, z + 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x, y - 1, z + 2))) {
								to.getWorld().getBlockAt(x, y - 1, z + 2).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x, y - 1, z + 2).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x, y - 1, z + 2).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 1, y - 1, z + 2))) {
								to.getWorld().getBlockAt(x + 1, y - 1, z + 2).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 1, y - 1, z + 2).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 1, y - 1, z + 2).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 1, y - 1, z + 2))) {
								to.getWorld().getBlockAt(x - 1, y - 1, z + 2).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 1, y - 1, z + 2).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 1, y - 1, z + 2).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x, y - 1, z - 2))) {
								to.getWorld().getBlockAt(x, y - 1, z - 2).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x, y - 1, z - 2).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x, y - 1, z - 2).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 1, y - 1, z - 2))) {
								to.getWorld().getBlockAt(x - 1, y - 1, z - 2).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 1, y - 1, z - 2).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 1, y - 1, z - 2).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 1, y - 1, z - 2))) {
								to.getWorld().getBlockAt(x + 1, y - 1, z - 2).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 1, y - 1, z - 2).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 1, y - 1, z - 2).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x, y - 1, z - 1))) {
								to.getWorld().getBlockAt(x, y - 1, z - 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x, y - 1, z - 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x, y - 1, z - 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 1, y - 1, z + 1))) {
								to.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 1, y - 1, z + 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 1, y - 1, z + 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x + 1, y - 1, z - 1))) {
								to.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x + 1, y - 1, z - 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x + 1, y - 1, z - 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 1, y - 1, z + 1))) {
								to.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 1, y - 1, z + 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 1, y - 1, z + 1).getLocation());
							}

							if (freezable(to.getWorld().getBlockAt(x - 1, y - 1, z - 1))) {
								to.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(UniversalMaterial.ICE.getMaterial ( ));
								to.getWorld().getBlockAt(x - 1, y - 1, z - 1).setMetadata("ice",
										new FixedMetadataValue(AnnihilationMain.INSTANCE, "ice"));
								desFreeze(p.getUniqueId(), to.getWorld().getBlockAt(x - 1, y - 1, z - 1).getLocation());
							}
						}
					}
				}
			}
		}
	}

	/* OMG - O My God */ // Is Very Long
	public void desFreeze(UUID uuid, Location l) {
		if (uuid == null || l == null)
			return;
		//
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				Player p = Bukkit.getPlayer(uuid);
				final Block to = l.getBlock();
				//
				Location pb = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
				//
				if (pb == null)
					return;
				//
				if (to.getType() == UniversalMaterial.ICE.getMaterial ( )) {
					if (!to.getLocation().equals(pb)) {
						if (!to.getLocation().equals(pb.getBlock().getRelative(BlockFace.EAST).getLocation())
								&& !l.equals(pb.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST)
										.getLocation())) {
							if (!to.getLocation().equals(pb.getBlock().getRelative(BlockFace.WEST).getLocation())
									&& !l.equals(pb.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST)
											.getLocation())) {
								if (!to.getLocation().equals(pb.getBlock().getRelative(BlockFace.SOUTH).getLocation())
										&& !l.equals(pb.getBlock().getRelative(BlockFace.SOUTH)
												.getRelative(BlockFace.SOUTH).getLocation())) {
									if (!to.getLocation()
											.equals(pb.getBlock().getRelative(BlockFace.NORTH).getLocation())
											&& !l.equals(pb.getBlock().getRelative(BlockFace.NORTH)
													.getRelative(BlockFace.NORTH).getLocation())) {
										if (!to.getLocation().equals(
												pb.getBlock().getRelative(BlockFace.NORTH_EAST).getLocation())) {
											if (!to.getLocation().equals(pb.getBlock()
													.getRelative(BlockFace.NORTH_NORTH_EAST).getLocation())) {
												if (!to.getLocation()
														.equals(pb.getBlock().getRelative(BlockFace.NORTH_EAST)
																.getRelative(BlockFace.EAST).getLocation())) {
													if (!to.getLocation().equals(pb.getBlock()
															.getRelative(BlockFace.SOUTH_EAST).getLocation())) {
														if (!to.getLocation()
																.equals(pb.getBlock()
																		.getRelative(BlockFace.SOUTH_SOUTH_EAST)
																		.getLocation())) {
															if (!to.getLocation().equals(pb.getBlock()
																	.getRelative(BlockFace.SOUTH_EAST)
																	.getRelative(BlockFace.EAST).getLocation())) {
																if (!to.getLocation()
																		.equals(pb.getBlock()
																				.getRelative(BlockFace.SOUTH_WEST)
																				.getLocation())) {
																	if (!to.getLocation()
																			.equals(pb.getBlock()
																					.getRelative(
																							BlockFace.SOUTH_SOUTH_WEST)
																					.getLocation())) {
																		if (!to.getLocation().equals(pb.getBlock()
																				.getRelative(BlockFace.SOUTH_WEST)
																				.getRelative(BlockFace.WEST)
																				.getLocation())) {
																			if (!to.getLocation()
																					.equals(pb.getBlock().getRelative(
																							BlockFace.NORTH_WEST)
																							.getLocation())) {
																				if (!to.getLocation().equals(pb
																						.getBlock()
																						.getRelative(
																								BlockFace.NORTH_NORTH_WEST)
																						.getLocation())) {
																					if (!to.getLocation().equals(pb
																							.getBlock()
																							.getRelative(
																									BlockFace.NORTH_WEST)
																							.getRelative(BlockFace.WEST)
																							.getLocation())) {
																						
																						// desfreeze:
																						to.setType ( UniversalMaterial.WATER.getMaterial ( ) );
																						to.removeMetadata("ice", AnnihilationMain.INSTANCE);
																						
																						try {
																							to.getLocation().getWorld().playEffect(to.getLocation(), Effect.STEP_SOUND, UniversalMaterial.SNOW.getMaterial());
																						} catch(Throwable t) {
																							System.out
																									.println("desfreeze effect error: ");
																							t.printStackTrace();
																						}
																					} 
																					else
																						desFreeze(uuid, l);
																				} else
																					desFreeze(uuid, l);
																			} else
																				desFreeze(uuid, l);
																		} else
																			desFreeze(uuid, l);
																	} else
																		desFreeze(uuid, l);
																} else
																	desFreeze(uuid, l);
															} else
																desFreeze(uuid, l);
														} else
															desFreeze(uuid, l);
													} else
														desFreeze(uuid, l);
												} else
													desFreeze(uuid, l);
											} else
												desFreeze(uuid, l);
										} else
											desFreeze(uuid, l);
									} else
										desFreeze(uuid, l);
								} else
									desFreeze(uuid, l);
							} else
								desFreeze(uuid, l);
						} else
							desFreeze(uuid, l);
					} else
						desFreeze(uuid, l);
				}
			}
		}, 28L);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void d(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			if (event.getEntity() != null && event.getEntity() instanceof Player) {
				final Player p = (Player) event.getEntity();
				final AnniPlayer pl = AnniPlayer.getPlayer(p.getUniqueId());
				if (pl != null && pl.getKit().equals(this)) {
					
					if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
						p.getLocation().getWorld().playEffect(p.getLocation(), Effect.valueOf("SNOWBALL_BREAK"), 100);
						p.getLocation().getWorld().playEffect(p.getLocation(), Effect.valueOf("SNOWBALL_BREAK"), 10);
						p.getLocation().getWorld().playEffect(p.getLocation(), Effect.valueOf("SNOWBALL_BREAK"), 1);
					}
					
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.ITEM_SNOWBALL.displayNewerVersions().display(0.5F, 0.5F, 0.5F, 0.1F, 3, p.getLocation().clone(), 100000D);
					} else {
						CompatibleParticles.ITEM_SNOWBALL.displayOlderVersions().display(0.5F, 0.5F, 0.5F, 0.1F, 3, p.getLocation().clone(), 100000D);
					}
				}
			}
		}
		//
		if (event.getCause() != DamageCause.FALL)
			return;
		//
		if (event.getEntity() != null && event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			AnniPlayer pl = AnniPlayer.getPlayer(p.getUniqueId());
			//
			if (pl != null && pl.getKit().equals(this)
					&& p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == UniversalMaterial.ICE.getMaterial ( ))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void antiB(BlockBreakEvent eve) {
		if (eve.getBlock().getType() != UniversalMaterial.ICE.getMaterial ( ))
			return;
		if (!eve.getBlock().hasMetadata("ice"))
			return;
		//
		Player p = eve.getPlayer();
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		if (ap != null && Game.isGameRunning() && ap.getTeam() != null && ap.getKit().equals(this)) {
			if (eve.getBlock().getType() == UniversalMaterial.ICE.getMaterial ( ) && eve.getBlock().hasMetadata("ice"))
				eve.setCancelled(true);
		}
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 0;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "IceMan";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.ICE.getMaterial ( ));
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are The Chill.", "", aqua + "You really hate things", aqua + "That move Quickly.", "",
				aqua + "Hitting Your Enemies", aqua + "Slows Them.", "", aqua + "You also freeze",
				aqua + "Water When you", aqua + "Walk on it.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addItem(getSpecialItem()).addItem(getLilyPads());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player p) {
		if (uses.containsKey(p.getUniqueId()))
			uses.replace(p.getUniqueId(), uses.get(p.getUniqueId()), true);
		else
			uses.put(p.getUniqueId(), true);
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	protected boolean useCustomMessage() {
		return false;
	}

	@Override
	protected String positiveMessage() {
		return null;
	}

	@Override
	protected String negativeMessage() {
		return null;
	}
}