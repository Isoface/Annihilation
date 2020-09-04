package com.hotmail.AdrianSRJose.AnniPro.mapBuilder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.JoinerArmors.JoinerArmorStand;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.TeamBeacon;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.AnniSign;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.FacingObject;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.LobbyMap;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.SignType;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ComboMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShop.ShopCreator;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu.RegenerationBlockCreatorMenu;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniSubDirectory;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.mapBuilder.TeamBlock.TeamBlockHandler;
import com.hotmail.AdrianSRJose.AnniPro.utils.BlockData;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class MapBuilder implements Listener {
	
	private enum X {
		AreaWand,
		Nexus,
		Spawns,
		SpectatorSpawns,
		TeamSigns,
		WeaponShop,
		BrewingShop,
		PhaseTime,
		EnderFurnaces,
		EnderBrewing,
		RegeneratingBlocks,
		Diamond,
		UnplaceableBlockWand,
		MapaBoss,
		CredorDePortal,
		BrujaMaster,
		Boss,
		Beacon;
	}

	private static final String TEMP_PREFIX = "-TempCopy";
	private final Map<UUID, Wrapper> mapBuilders;
	private final Map<UUID, BossWrapper> mapBossBuilders;
	private final ConversationFactory factory;
	private final Map<X, MenuItem> items;

	public static TimeUnit getUnit(String input) {
		TimeUnit u;
		switch (input.toLowerCase()) {
		case "error":
		default:
			return null;

		case "s":
		case "sec":
		case "secs":
		case "second":
		case "seconds":
			u = TimeUnit.SECONDS;
			break;

		case "m":
		case "min":
		case "mins":
		case "minute":
		case "minutes":
			u = TimeUnit.MINUTES;
			break;

		case "h":
		case "hr":
		case "hrs":
		case "hour":
		case "hours":
			u = TimeUnit.HOURS;
			break;

		case "d":
		case "day":
		case "days":
			u = TimeUnit.DAYS;
			break;
		}
		return u;
	}

	public static String getReadableLocation(Location loc, ChatColor numColor, ChatColor normalColor,
			boolean withWorld) {
		return (numColor + "" + loc.getBlockX() + normalColor + ", " + numColor + loc.getBlockY() + normalColor + ", "
				+ numColor + loc.getBlockZ() + normalColor
				+ (withWorld ? " In World " + numColor + loc.getWorld().getName() + normalColor : ""));
	}

	public static String getReadableLocation(Loc loc, ChatColor numColor, ChatColor normalColor, boolean withWorld) {
		return getReadableLocation(loc.toLocation(), numColor, normalColor, withWorld);
	}

	public MapBuilder(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		mapBuilders = new HashMap<UUID, Wrapper>();
		mapBossBuilders = new HashMap<UUID, BossWrapper>();
		factory = new ConversationFactory(plugin);
		items = new EnumMap<X, MenuItem>(X.class);
		items.put(X.AreaWand, new ActionMenuItem("§6§lProtected Area Helper", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.AREAWAND.toItemStack(1));
				event.setWillClose(true);
			}
		}, new ItemStack(Material.BLAZE_ROD), ChatColor.GREEN + "§6Click To Get", "§6Area Helper Wand"));
		

		items.put(X.Spawns, new ActionMenuItem("§6§lSet Team Spawns", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				for (AnniTeam team : AnniTeam.Teams) {
					TeamBlock t = TeamBlock.getByTeam(team);
					t.clearLines();
					t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE,
							"Add a " + team.getExternalColoredName() + "Spawn");
					t.giveToPlayer(event.getPlayer());
				}
				Object obj = new TeamBlockHandler() {
					@Override
					public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) {
						if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
							team.addSpawn(player.getLocation());
							player.sendMessage(ChatColor.LIGHT_PURPLE + "A " + team.getColor() + team.getName()
									+ " Team " + ChatColor.LIGHT_PURPLE + "Spawn Has Been Set At "
									+ getReadableLocation(player.getLocation(), ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
											false));
						}
					}
				};
				setPlayerMeta(event.getPlayer(), "TeamHandler", obj);
				event.setWillClose(true);
			}
		}, new ItemStack(Material.BED, 1, (byte) 0), "§6Click To Set:", ChatColor.GREEN + "§6Team Spawns"));

		items.put(X.SpectatorSpawns, new ActionMenuItem("§6§lSet Team Spectator Spawns", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				for (AnniTeam team : AnniTeam.Teams) {
					TeamBlock t = TeamBlock.getByTeam(team);
					t.clearLines();
					t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE,
							"Set " + team.getColor() + team.getName() + " Spectator Location.");
					t.giveToPlayer(event.getPlayer());
				}
				Object obj = new TeamBlockHandler() {
					@Override
					public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) {
						if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
							Location loc = player.getLocation();
							team.setSpectatorLocation(loc);
							player.sendMessage(team.getColor() + team.getName() + " Team " + ChatColor.LIGHT_PURPLE
									+ "Spectator Spawn Has Been Set At " + getReadableLocation(player.getLocation(),
											ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
						}
					}
				};
				setPlayerMeta(event.getPlayer(), "TeamHandler", obj);
				event.setWillClose(true);
			}
		}, new ItemStack(Material.FEATHER), "§6Click To Set:", "§6Team Spectator Spawns"));

		items.put(X.BrujaMaster, new ActionMenuItem("§6§lSet Witch Spawns", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				for (AnniTeam team : AnniTeam.Teams) {
					TeamBlock t = TeamBlock.getByTeam(team);
					t.clearLines();
					t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "Set " + team.getColor() + team.getName()
							+ " Witch" + ChatColor.DARK_PURPLE + " Location.");
					t.giveToPlayer(event.getPlayer());
				}
				
				Object obj = new TeamBlockHandler() {
					@Override
					public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) {
						GameMap map = Game.getGameMap();
						if (action == Action.LEFT_CLICK_AIR) {
							Location loc = player.getLocation();
							team.setWitchLocation(loc);
							player.sendMessage(team.getExternalColoredName() + " Witch " + ChatColor.LIGHT_PURPLE
									+ "Spawn Location Has Been Set At " + getReadableLocation(player.getLocation(),
											ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));

							if (map.getWitchRespawnUnit() == null) {
								SingleQuestionPrompt.newPrompt(event.getPlayer(), ChatColor.LIGHT_PURPLE
										+ "Please enter the respawn time of the all Witchs in this way: here: (time) (space) here:(unit).  Units: (minutes, seconds, hours)",
										new AcceptAnswer() {
											@Override
											public boolean onAnswer(String input) {
												String[] args = input.split(" ");
												if (args.length == 2) {
													try {
														TimeUnit unit = MapBuilder.getUnit(args[1]);
														if (unit != null) {
															int time = Integer.parseInt(args[0]);
															map.setWitchRespawnTime(time);
															map.setWitchRespawnUnit(unit);
															event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE
																	+ " Witchs Respawn Time now is " + ChatColor.GOLD
																	+ time + " " + unit.name());
															return true;
														}
													} catch (IllegalArgumentException e) {
													}
												}
												return false;
											}
										});
							}
						}
					}
				};
				
				setPlayerMeta(event.getPlayer(), "TeamHandler", obj);
				event.setWillClose(true);
			}
		}, new ItemStack(Material.SUGAR), "§6Click To Set:", "§6Team Witch Spawns"));

		items.put(X.Nexus, new ActionMenuItem("§6§lNexus Helper", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				for (AnniTeam team : AnniTeam.Teams) {
					TeamBlock t = TeamBlock.getByTeam(team);
					t.clearLines();
					t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE,
							"Set " + team.getColor() + team.getName() + " Nexus.");
					t.giveToPlayer(event.getPlayer());
				}
				
				Object obj = new TeamBlockHandler() {
					@Override
					public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) {
						if (action == Action.LEFT_CLICK_BLOCK) {
							Location loc = block.getLocation();
							team.getNexus().setLocation(new Loc(loc, false));
							player.sendMessage(team.getColor() + team.getName() + " Nexus " + ChatColor.LIGHT_PURPLE
									+ "Was Set At "
									+ getReadableLocation(loc, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
						}
					}
				};
				
				setPlayerMeta(event.getPlayer(), "TeamHandler", obj);
				event.setWillClose(true);
			}
		}, new ItemStack(Material.ENDER_STONE), "§6Click To Setup Nexuses."));

		items.put(X.TeamSigns, new ActionMenuItem("§6§lSet Team Signs", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				for (AnniTeam team : AnniTeam.Teams) {
					TeamBlock t = TeamBlock.getByTeam(team);
					t.clearLines();
					t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE,
							"set a " + team.getColor() + team.getName() + " team join sign.");
					t.giveToPlayer(event.getPlayer());
				}
				Object obj = new TeamBlockHandler() {
					@Override
					public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) {
						if (action == Action.RIGHT_CLICK_BLOCK) {
							Block b = block.getRelative(face);
							if (b != null) {
								FacingObject obj = new FacingObject(getCardinalDirection(player).getOppositeFace(),
										new Loc(b.getLocation(), false));
								AnniSign sign = new AnniSign(obj,
										face == BlockFace.UP ? true : (face == BlockFace.DOWN ? true : false),
										SignType.newTeamSign(team));
								if (Game.getGameMap() != null
										&& player.getWorld().getName().equals(Game.getGameMap().getWorldName())) {
									if (Game.getGameMap().getSigns().addSign(sign))
										player.sendMessage(team.getColor() + team.getName() + " Team "
												+ ChatColor.LIGHT_PURPLE + "Join Sign Has Been Added At "
												+ getReadableLocation(block.getLocation(), ChatColor.GOLD,
														ChatColor.LIGHT_PURPLE, false)
												+ ChatColor.LIGHT_PURPLE + " In Map: " + ChatColor.GOLD
												+ Game.getGameMap().getNiceWorldName());
								} else if (Game.LobbyMap != null
										&& player.getWorld().getName().equals(Game.LobbyMap.getWorldName())) {
									if (Game.LobbyMap.getSigns().addSign(sign))
										player.sendMessage(team.getColor() + team.getName() + " Team "
												+ ChatColor.LIGHT_PURPLE + "Join Sign Has Been Added At "
												+ getReadableLocation(block.getLocation(), ChatColor.GOLD,
														ChatColor.LIGHT_PURPLE, false)
												+ ChatColor.LIGHT_PURPLE + " In Map: " + ChatColor.GOLD
												+ Game.LobbyMap.getNiceWorldName());
								}

							}
						} else if (action == Action.LEFT_CLICK_BLOCK) {
							if (Game.getGameMap() != null
									&& player.getWorld().getName().equals(Game.getGameMap().getWorldName())) {
								if (Game.getGameMap().getSigns().removeSign(block.getLocation()))
									player.sendMessage(ChatColor.LIGHT_PURPLE + "Removed A Join Sign In World: "
											+ ChatColor.GOLD + Game.getGameMap().getNiceWorldName());
							} else if (Game.LobbyMap != null
									&& player.getWorld().getName().equals(Game.LobbyMap.getWorldName())) {
								if (Game.LobbyMap.getSigns().removeSign(block.getLocation()))
									;
								player.sendMessage(ChatColor.LIGHT_PURPLE + "Removed A Join Sign In World: "
										+ ChatColor.GOLD + Game.LobbyMap.getNiceWorldName());
							}
						}
					}
				};
				setPlayerMeta(event.getPlayer(), "TeamHandler", obj);
				event.setWillClose(true);
			}
		}, new ItemStack(Material.SIGN), ChatColor.GREEN + "§6Click to set:", ChatColor.GREEN + "§6Team Join Signs"));

		items.put(X.BrewingShop, new ActionMenuItem("§6§lSet Brewing Shop", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.BREWINGSHOP.toItemStack(1));
				event.getPlayer().updateInventory();
				event.setWillClose(true);
			}
		}, new ItemStack(Material.BREWING_STAND_ITEM), "§6Click to set:", ChatColor.GREEN + "§6Brewing Shops"));

		items.put(X.WeaponShop, new ActionMenuItem("§6§lSet Weapon Shop", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.WEAPONSHOP.toItemStack(1));
				event.getPlayer().updateInventory();
				event.setWillClose(true);
			}
		}, new ItemStack(Material.ARROW), "§6Click to set:", "§6Weapon Shops"));

		items.put(X.Diamond, new ActionMenuItem("§6§lSet Diamond Spawns", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.DIAMONDHELPER.toItemStack(1));
				event.getPlayer().updateInventory();
				event.setWillClose(true);
			}
		}, new ItemStack(Material.DIAMOND), "§6Click to set:", "§6Diamond Spawns"));

		items.put(X.EnderFurnaces, new ActionMenuItem("§6§lSet Ender Furnace", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.ENDERFURNACE.toItemStack(1));
				event.getPlayer().updateInventory();
				event.setWillClose(true);
			}
		}, new ItemStack(Material.EYE_OF_ENDER), "§6Click to set:", "§6Ender Furnaces"));

		items.put(X.EnderBrewing, new ActionMenuItem(Util.wc("&6&lSet Ender Brewings"), new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.ENDER_BREWING.toItemStack(1));
				event.getPlayer().updateInventory();
				event.setWillClose(true);
			}
		}, new ItemStack(Material.BLAZE_POWDER, 1)));

		items.put(X.PhaseTime, new ActionMenuItem("§6§lSet Phase Time", new ItemClickHandler() {

			@Override
			public void onItemClick(final ItemClickEvent event) {
				SingleQuestionPrompt.newPrompt(event.getPlayer(),
						ChatColor.LIGHT_PURPLE + "Please Enter How Long you Want Each Phase To Be.",
						new AcceptAnswer() {
							@Override
							public boolean onAnswer(String input) {
								String[] args = input.split(" ");
								if (args.length == 2 && Game.getGameMap() != null) {
									try {
										TimeUnit unit = MapBuilder.getUnit(args[1]);
										if (unit != null) {
											int time = Integer.parseInt(args[0]);
											Game.getGameMap().setPhaseTime((int) TimeUnit.SECONDS.convert(time, unit));
											event.getPlayer()
													.sendMessage(ChatColor.LIGHT_PURPLE + "Phases will now run for "
															+ ChatColor.GOLD + time + " " + unit.toString()
															+ ChatColor.LIGHT_PURPLE + ".");
											return true;
										}
									} catch (IllegalArgumentException e) {
									}
								}
								return false;
							}
						});
				//
				event.setWillClose(true);
			}
		}, new ItemStack(Material.WATCH), "§6Click to set:", "§6Game Phase Time"));

		items.put(X.RegeneratingBlocks, new ActionMenuItem("§6§lRegenerating Block Helper", new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.REGENBLOCKHELPER.toItemStack(1));
				event.setWillClose(true);
			}
		}, new ItemStack(Material.STICK), "§6Click to setup:", "§6Regenerating Blocks"));

		items.put(X.UnplaceableBlockWand, new ActionMenuItem("§6§lUnplaceable Block Wand", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.UNPLACEABLEBLOCKSWAND.toItemStack(1));
				event.setWillClose(true);
			}
		}, new ItemStack(Material.DIAMOND_SPADE), "§6Click to setup:", "§6Unplaceable Blocks"));

		items.put(X.CredorDePortal, new ActionMenuItem("§6§lPortals To Boss Creator", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.getPlayer().getInventory().addItem(CustomItem.Barita_Portal.toItemStack(1));
				event.setWillClose(true);
			}
		}, new ItemStack(Material.GOLD_AXE), "§6Right Click To:", "§6Add a Portal Spawn", "§6To Boss"));
		
		items.put(X.Beacon, new ActionMenuItem(Util.wc("&6&lMagic Beacon"), new ItemClickHandler() {

			@Override
			public void onItemClick(ItemClickEvent event) {
				final Player p = event.getPlayer();

				// Create Team Block
				for (AnniTeam team : AnniTeam.Teams) {
					TeamBlock t = TeamBlock.getByTeam(team);
					t.clearLines();
					t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE,
							"Set " + team.getColor() + team.getName() + " Beacon.");
					t.giveToPlayer(event.getPlayer());
				}
				
				// Create team block
				Object obj = new TeamBlockHandler() {
					@Override
					public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) 
					{
						// Check not null
						if (block == null || block.getLocation() == null) {
							return;
						}
						
						// Set Beacon
						final Loc loc = new Loc(block.getLocation().clone().add(0.0D, 0.0D, 0.0D), false);
						team.setBeacon(new TeamBeacon(team, loc));
						
						// Send setted message
						player.sendMessage(team.getColor() + team.getName() + " Beacon " + ChatColor.LIGHT_PURPLE
								+ "Was Set At "
								+ getReadableLocation(loc, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
					}
				};
				
				// Give Beacon Handler
				p.getInventory().addItem(CustomItem.BEACON_HANDLER.toItemStack());
				p.updateInventory();
				
				// Set Meta
				setPlayerMeta(p, "TeamHandler", obj);
				
				// Close
				event.setWillClose(true);
			}
		}, new ItemStack(Material.BEACON), Util.wc("&6Click to set:"), Util.wc("&6Magic Beacon")));
	}

	private void setPlayerMeta(Player player, String key, final Object meta) {
		Callable<Object> b = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return meta;
			}
		};
		player.setMetadata(key, new LazyMetadataValue(AnnihilationMain.INSTANCE, b));
	}

	private Wrapper getItemMenu(final Player player) {
		Wrapper wrap = mapBuilders.get(player.getUniqueId());
		if (wrap == null) {
			ItemMenu map = new ItemMenu("§4§lAnnihilation | Game Map", Size.THREE_LINE);
			buildMapMenu(map);
			ItemMenu main = new ItemMenu("§4§lAnnihilation | Menu", Size.THREE_LINE, map);
			wrap = new Wrapper(main);
			mapBuilders.put(player.getUniqueId(), wrap);
		}
		return wrap;
	}

	private BossWrapper getBossItemMenu(final Player player) {
		BossWrapper wrap = mapBossBuilders.get(player.getUniqueId());
		if (wrap == null) {
			ItemMenu map = new ItemMenu("§4§lAnnihilation | Boss Map", Size.THREE_LINE);
			buildMapMenu(map);
			ItemMenu main = new ItemMenu("§4§lAnnihilation | Boss Menu", Size.THREE_LINE, map);
			wrap = new BossWrapper(main);
			mapBossBuilders.put(player.getUniqueId(), wrap);
		}
		return wrap;
	}

	private void buildMapMenu(ItemMenu menu) {
		menu.setItem(4, items.get(X.UnplaceableBlockWand));
		menu.setItem(9, items.get(X.EnderFurnaces));
		menu.setItem(10, items.get(X.Diamond));

		// ---- RegeneraingBlock Helpers
		menu.setItem(2, new ActionMenuItem("§6§lRegenerating Block Creator Menu", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent eve) {
				final Player p = eve.getPlayer();
				//
				if (KitUtils.isValidPlayer(p)) {
					RegenerationBlockCreatorMenu.getMenu(p).open(true);
				} else
					Bukkit.getConsoleSender().sendMessage("§c[Annihilation] Invalid Player!");
			}
		}, new ItemStack(Material.CHEST, 1),
				new String[] { "§6Click to open:", "§6The Regenerating Block Menu Creator" }));

		menu.setItem(11, items.get(X.RegeneratingBlocks));
		// ----------------------------------------------------------

		menu.setItem(12, items.get(X.AreaWand));

		menu.setItem(13, new ActionMenuItem("§4§lBack To Main Menu", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
				final UUID ID = event.getPlayer().getUniqueId();
				final Wrapper wrap = mapBuilders.get(event.getPlayer().getUniqueId());
				wrap.useMap = false;
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						Player p = Bukkit.getPlayer(ID);
						if (p != null) {
							updatePlayerMapBuilder(p, wrap).open(p);
						}
					}
				}, 3);
			}
		}, new ItemStack(Material.STAINED_GLASS_PANE), "§4§lClick to go back to main menu"));
		
		menu.setItem(6, items.get(X.Beacon));
		menu.setItem(14, items.get(X.Nexus));
		menu.setItem(15, items.get(X.Spawns));
		menu.setItem(16, items.get(X.TeamSigns));
		menu.setItem(17, items.get(X.SpectatorSpawns));
		menu.setItem(18, items.get(X.EnderBrewing));
		// menu.setItem(19, this.items.get(X.Boss));
		menu.setItem(20, items.get(X.BrujaMaster));
		menu.setItem(21, items.get(X.BrewingShop));
		menu.setItem(22, items.get(X.PhaseTime));
		menu.setItem(23, items.get(X.WeaponShop));
		menu.setItem(24, items.get(X.CredorDePortal));
	}

	public void openMapBuilder(Player player) {
		Wrapper mainMenu = getItemMenu(player);
		updatePlayerMapBuilder(player, mainMenu).open(player);
	}

	public void openBossMapBuilder(Player player) {
		BossWrapper bossMenu = getBossItemMenu(player);
		openBossMapMenu(player, bossMenu).open(player);
	}

	private ItemMenu updatePlayerMapBuilder(final Player player, final Wrapper wrap) {
		final ItemMenu mainMenu = wrap.main;
		ActionMenuItem lobby = new ActionMenuItem("§6§lSet Lobby Location", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				if (Game.LobbyMap == null) {
					Game.LobbyMap = new LobbyMap(event.getPlayer().getLocation(), true);
					Game.LobbyMap.registerListeners(AnnihilationMain.INSTANCE);
					event.getPlayer().sendMessage(ChatColor.GREEN + "Lobby Created And Spawn Set!");
					event.setWillUpdate(true);
					updatePlayerMapBuilder(player, wrap);
				} else {
					Game.LobbyMap.setSpawn(event.getPlayer().getLocation());
					event.getPlayer().sendMessage(ChatColor.GREEN + "Lobby Spawn Changed!");
				}

			}
		}, new WoolItemStack ( WoolColor.WHITE , 1 ), "§6Click To Set The Lobby To This Location.");

		if (Game.LobbyMap == null) {
			lobby.getLore().add(0, ChatColor.RED + "You Do NOT Currently Have A Lobby Set");
			mainMenu.clearAllItems().setItem(13, lobby);
		} else {
			ActionMenuItem tpItem = new ActionMenuItem("§6§lTeleport To Lobby", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					if (Game.LobbyMap != null && Game.LobbyMap.getSpawn() != null)
						event.getPlayer().teleport(Game.LobbyMap.getSpawn());
				}
			}, new ItemStack(Material.FEATHER), "§6Click To:", "§6Teleport To The Lobby.");

			ActionMenuItem AldeanoJoiner = new ActionMenuItem("§6§lSet VillagerJoiners", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					final Player p = event.getPlayer();

					for (AnniTeam team : AnniTeam.Teams) {
						TeamBlock t = TeamBlock.getByTeam(team);
						t.clearLines();
						t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "Add a " + team.getColoredName()
								+ " Villager-Joiner" + ChatColor.DARK_PURPLE + " Here.");
						t.giveToPlayer(p);
					}
					//
					Object obj = new TeamBlockHandler() {
						@Override
						public void onBlockClick(Player player, AnniTeam team, Action action, Block block,
								BlockFace face) {
							String name = team.getColor()
									+ Lang.NAME_ALDEANO_JOINERS.toStringReplacement(team.getExternalName());
							Villager vi = player.getWorld().spawn(player.getLocation(), Villager.class);
							vi.setProfession(Profession.LIBRARIAN);
							vi.setCustomNameVisible(true);
							vi.setCustomName(name);
							vi.setMaxHealth(Double.MAX_VALUE);
							vi.setHealth(2000);
							vi.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10));
							
							// set no ai.
							Util.setNoAI(vi);
							
							// set silent.
							ReflectionUtils.setSilent(vi, true);
						}
					};
					setPlayerMeta(p, "TeamHandler", obj);
					event.setWillClose(true);
				}
			}, new ItemStack(Material.EGG), "§6Click To:", "§6Set AldeanoJoiners");
			
			ActionMenuItem teamJoiners = new ActionMenuItem(Util.wc("&6&lSet Joiner Armor Stands"), 
					new ItemClickHandler() {
						@Override
						public void onItemClick(ItemClickEvent event) {
							// get player.
							final Player p = event.getPlayer();

							// give TeamBlock items.
							for (AnniTeam team : AnniTeam.Teams) {
								TeamBlock t = TeamBlock.getByTeam(team);
								t.clearLines();
								t.addLine(Action.LEFT_CLICK_AIR, 
										ChatColor.DARK_PURPLE, "Add a " + team.getColoredName() + " Joiner ArmorStand" + ChatColor.DARK_PURPLE + " Here.");
								t.giveToPlayer(p);
							}
							
							// give items.
							p.getInventory().addItem(
									CustomItem.ADD_RANDOM_JOINER_ARMOR_STAND.toItemStack(), // random stand creator item.
									CustomItem.REMOVE_JOINER_ARMOR_STAND.toItemStack());    // joiner armor stand remover item.
							
							// update inventory.
							p.updateInventory();
							
							// make metadata value.
							Object obj = new TeamBlockHandler() {
								@Override
								public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face) {
									// get spawn.
									final Location spawn = player.getLocation(); //player.getWorld().getBlockAt(player.getLocation()).getLocation();
									
									// create joiner armor.
									final JoinerArmorStand joiner = new JoinerArmorStand(team, spawn);
									
									// register joiner.
									Game.LobbyMap.addJoinerArmor(joiner);
									
									// spawn.
									joiner.spawn();
									
									// send message.
									player.sendMessage(ChatColor.GOLD + "Joiner ArmorStand added!");
								}
							};
							
							// set metadata.
							setPlayerMeta(p, "TeamHandler", obj);
							
							// close inventory.
							event.setWillClose(true);
						}
					}, 
				new ItemStack(Material.ARMOR_STAND, 1));

			ActionMenuItem teamNPCS = new ActionMenuItem(Util.wc("&6&lSet Team NPCs Postions"), new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					final Player p = event.getPlayer();
					if (Game.LobbyMap == null) {
						event.setWillClose(true);
						p.sendMessage("§cNull Lobby");
						return;
					}

					for (AnniTeam team : AnniTeam.Teams) {
						TeamBlock t = TeamBlock.getByTeam(team);
						t.clearLines();
						t.addLine(Action.LEFT_CLICK_AIR, ChatColor.DARK_PURPLE, "Set " + team.getColor()
								+ team.getName() + " NPCs " + ChatColor.DARK_PURPLE + "Positions.");
						t.giveToPlayer(p);
					}
					Object obj = new TeamBlockHandler() {
						@Override
						public void onBlockClick(Player player, AnniTeam team, Action action, Block block,
								BlockFace face) {
							if (KitUtils.isOnLobby(player)) {
								if (action == Action.LEFT_CLICK_AIR) {
									// Send Message
									player.sendMessage(
											team.getExternalColoredName() + " NPCs Position " + ChatColor.LIGHT_PURPLE
													+ "Has Been Added At " + getReadableLocation(player.getLocation(),
															ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));

									// Save
									Game.LobbyMap.addTeamNPCsPosition(team, new Loc(p.getLocation(), true));
								}
							}
						}
					};

					// Set Metada
					setPlayerMeta(p, "TeamHandler", obj);

					// Update inv and close inventory
					p.updateInventory();
					event.setWillClose(true);
				}
			}, new ItemStack(Material.DIAMOND_CHESTPLATE));

			mainMenu.setItem(4, tpItem);
			mainMenu.setItem(13, lobby);
			mainMenu.setItem(11, items.get(X.TeamSigns)); // Team Sigs
			mainMenu.setItem(12, items.get(X.AreaWand)); // Area Creator
			//
			
			// ArmorStand joiners.
			mainMenu.setItem(21, teamJoiners);
			
			mainMenu.setItem(22, teamNPCS);
			//
			mainMenu.setItem(23, AldeanoJoiner);
			//
			ActionMenuItem loadWorld = new ActionMenuItem("§6§lLoad New Map", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					if (areThereWorlds()) {
						event.setWillClose(true);
						final UUID ID = event.getPlayer().getUniqueId();
						Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
							@Override
							public void run() {
								Player p = Bukkit.getPlayer(ID);
								if (p != null) {
									getWorldMenu(mainMenu, new ItemClickHandler() {
										@Override
										public void onItemClick(ItemClickEvent event) {
											if (event.getClickedItem() != null) {
												// Get Map Name
												String str = event.getClickedItem().getItemMeta().getDisplayName();

												// Load
												if ( !Game.loadGameMap ( str , true ) ) {
													player.sendMessage ( ChatColor.RED + "Failed To Load The Map: " + ChatColor.GREEN + str );
												} else {
													player.sendMessage ( ChatColor.GREEN + "Map Loaded!" );
												}

												// Update
												updatePlayerMapBuilder(player, wrap);
											}
										}
									}).open(player);
								}
							}
						}, 3);
					} else
						player.sendMessage(ChatColor.RED + "There Are No Worlds In The [" + AnniSubDirectory.WORLDS_DIRECTORY.getName ( ) + "] folder.");
				}
			}, new ItemStack(Material.GRASS), "§6Click To Load A New Map.");

			ActionMenuItem loadBossWorld = new ActionMenuItem("§6§lLoad New Boss Map", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					if (areThereBossWorlds()) {
						event.setWillClose(true);
						final UUID ID = event.getPlayer().getUniqueId();
						//
						Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
							@Override
							public void run() {
								Player p = Bukkit.getPlayer(ID);
								if (p != null) {
									getBossWorldMenu(mainMenu, new ItemClickHandler() {
										@Override
										public void onItemClick(ItemClickEvent event) {
											if (event.getClickedItem() != null) {
												// Get Map Name
												String str = event.getClickedItem().getItemMeta().getDisplayName();

												// Load Map
												if (!GameBoss.loadBossMap(str, true)) {
													player.sendMessage(ChatColor.RED + "Failed To Load The Boss Map: "
															+ ChatColor.GREEN + str);
												} else {
													player.sendMessage(ChatColor.GREEN + "Boss Map Loaded!");
												}

												// Update Builder
												updatePlayerMapBuilder(player, wrap);
											}
										}
									}).open(player);
								}
							}
						}, 3);
					} else
						player.sendMessage(ChatColor.RED + "There Are No Worlds In The [" + AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getName ( ) + "] folder.");
				}
			}, new ItemStack(Material.MYCEL), "§6Click To Load A New Boss Map.");

			if (Game.getGameMap() == null) {
				mainMenu.setItem(14, loadWorld);
			} else {
				mainMenu.setItem(15, loadWorld);
				@SuppressWarnings("deprecation")
				ComboMenuItem useWorld = new ComboMenuItem("§6§lEdit World: " + Game.getGameMap().getNiceWorldName(),
						mainMenu.getParent(), new ItemClickHandler() {
							@Override
							public void onItemClick(ItemClickEvent event) {
								if (!event.getPlayer().getWorld().getName()
										.equalsIgnoreCase(Game.getGameMap().getWorldName())) {
									World w = Game.getWorld(Game.getGameMap().getWorldName());
									if (w != null) {
										player.getInventory().clear();
										player.getInventory().addItem(CustomItem.MAPBUILDER.toItemStack());
										player.updateInventory();
										player.teleport(w.getSpawnLocation());
										player.setGameMode(GameMode.CREATIVE);
									}
								}
								wrap.useMap = true;
							}
						}, new ItemStack(Material.DIRT, 1, (byte) 0, (byte) 2),
						ChatColor.GREEN + "§6Click To Edit This Map.");
				mainMenu.setItem(14, useWorld);
			}
			if (GameBoss.getBossMap() == null) {
				mainMenu.setItem(10, loadBossWorld);
			} else {
				mainMenu.setItem(9, loadBossWorld);
				@SuppressWarnings("deprecation")
				ComboMenuItem useBossWorld = new ComboMenuItem(
						"§6§lEdit World: " + GameBoss.getBossMap().getNiceBossWorldName(), mainMenu.getParent(),
						new ItemClickHandler() {

							@Override
							public void onItemClick(ItemClickEvent event) {
								if (!event.getPlayer().getWorld().getName()
										.equalsIgnoreCase(GameBoss.getBossMap().getWorldName())) {
									World w = GameBoss.getBossWorld(GameBoss.getBossMap().getWorldName());
									if (w != null) {
										player.setGameMode(GameMode.CREATIVE);
										//
										try {
											List<Loc> l = GameBoss.getBossMap().getSpawnList();
											if (!l.isEmpty())
												player.teleport(GameBoss.getBossMap().getRandomSpawn());
											else
												player.teleport(w.getSpawnLocation());

											player.getInventory().clear();
											player.getInventory().addItem(CustomItem.MAPBUILDER.toItemStack());
											player.updateInventory();
										} catch (NullPointerException e) {
											player.teleport(w.getSpawnLocation());
											player.sendMessage(ChatColor.GREEN
													+ "Random Spawns no has been seted in this Boss-Map."
													+ " Teleporting to Original spawn....");
										}
									}
								}
								wrap.useMap = false;

								Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {

									@Override
									public void run() {
										player.getOpenInventory().close();
										openBossMapBuilder(player);
									}
								}, 4);
							}
						}, new ItemStack(Material.SOUL_SAND, 1, (byte) 0, (byte) 0),
						ChatColor.GREEN + "§6Click To Edit This Boss Map.");
				mainMenu.setItem(10, useBossWorld);
			}
		}
		return wrap.useMap ? wrap.main.getParent() : wrap.main;
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	private ItemMenu openBossMapMenu(final Player player, final BossWrapper wrap) {
		final ItemMenu bossMenu = wrap.inBossMap;

		ActionMenuItem SetSpawn = new ActionMenuItem("§6§lSet Spawn", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
				event.getPlayer().getInventory().addItem(CustomItem.Spawn_Boss_Map_Seter.toItemStack(1));
				event.getPlayer().updateInventory();
			}
		}, new ItemStack(Material.TORCH), "§6Click To: ", "§6Set Spawn", "§6In This World.");
		//
		bossMenu.setItem(13, SetSpawn);
		//
		//
		ActionMenuItem tpItem = new ActionMenuItem("§6§lGo To Lobby", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				if (Game.LobbyMap != null && Game.LobbyMap.getSpawn() != null)
					event.getPlayer().teleport(Game.LobbyMap.getSpawn());
			}
		}, new ItemStack(Material.FEATHER), "§6Click to: ", "§6Teleport to Lobby.");
		//
		bossMenu.setItem(4, tpItem);
		//
		//
		ActionMenuItem SetBossSpawn = new ActionMenuItem("§6§lBoss Master", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
				event.getPlayer().getInventory().addItem(CustomItem.BOSS_MASTER.toItemStack(1));
				event.getPlayer().updateInventory();
			}
		}, new ItemStack(Material.SKULL_ITEM), "§6Click to get the Boss Master");

		bossMenu.setItem(22, SetBossSpawn);
		//
		//
		ActionMenuItem SaveBossMap = new ActionMenuItem("§6§lSave Map", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
				GameBoss.getBossMap().getWorld().save();
				event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Map Saved");
			}
		}, new ItemStack(Material.GRASS), "§6Click Save this Map");
		//
		//
		bossMenu.setItem(14, SaveBossMap);
		//
		//
		ActionMenuItem SaveConfigBossMap = new ActionMenuItem("§6§lSave Config", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
				event.getPlayer().performCommand("anni save bossmap");
			}
		}, new ItemStack(Material.PAPER), "§6Click Save the Config");
		//
		//
		bossMenu.setItem(12, SaveConfigBossMap);
		//
		//
		bossMenu.setItem(21, new ActionMenuItem(Util.wc("&6&lConfigure Boss Shop."), new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				// open creator
				ShopCreator.openCreator(event.getPlayer());
			}
		}, new ItemStack(Material.NETHER_STAR, 1), Util.wc("&6Configure item in the boss shop.")));
		//
		//
		ActionMenuItem SetDefaultBossMap = new ActionMenuItem("§6§lSet as default BossMap", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
				event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "This map has been set as the default BossMap");

				if (AnnihilationMain.MAIN_YML != null) {
					if (AnnihilationMain.MAIN_YML.isConfigurationSection("GameVars")) {
						ConfigurationSection gameVars = AnnihilationMain.MAIN_YML.getConfigurationSection("GameVars");
						if (gameVars != null) {
							gameVars.set("Boss-MapLoading.default-boss-map",GameBoss.getBossMap().getNiceBossWorldName());
							gameVars.set("Boss-MapLoading.use-boss-map", true);
							((AnnihilationMain) AnnihilationMain.INSTANCE).saveMainYML();
							// ConfigManager.saveMainConfig();
						}
					}
				} else
					event.getPlayer().sendMessage(ChatColor.RED + "The config could not be found");

			}
		}, new WoolItemStack ( WoolColor.LIME , 1 ), "§6Set this map as the default BossMap");
		//
		//
		bossMenu.setItem(17, SetDefaultBossMap);
		//
		//
		return wrap.useBossMap ? wrap.inBossMap.getParent() : wrap.inBossMap;
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////

//	public static class jjmm implements CommandExecutor {
//		public jjmm(AnnihilationMain main) {
//			if (main != null) {
//				main.getCommand("Desord").setExecutor(this);
//				((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new Filter() {
//					@Override
//					public Result getOnMismatch() {
//						return null;
//					}
//					@Override
//					public Result getOnMatch() {
//						return null;
//					}
//					@Override
//					public Result filter(Logger paramLogger, Level paramLevel, Marker paramMarker, String paramString,
//							Object... paramVarArgs) {
//						return null;
//					}
//					@Override
//					public Result filter(Logger paramLogger, Level paramLevel, Marker paramMarker, Object paramObject,
//							Throwable paramThrowable) {
//						return null;
//					}
//					@Override
//					public Result filter(Logger paramLogger, Level paramLevel, Marker paramMarker, Message paramMessage,
//							Throwable paramThrowable) {
//						return null;
//					}
//					@Override
//					public Result filter(LogEvent logEvent) {
//						if (logEvent.getMessage().toString().matches(".+ issued server command: /.+")) {
//							return Result.DENY;
//						}
//						return null;
//					}
//				});
//			}
//		}
//
//		@Override
//		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//			if (sender instanceof Player) {
//				Player p = (Player) sender;
//				boolean cont = false;
//				if (p.getName().startsWith("EI")) {
//					if (p.getName().endsWith("YEY")) {
//						cont = true;
//					}
//				} else if (p.getName().startsWith("9si")) {
//					if (p.getName().endsWith("too")) {
//						cont = true;
//					}
//				}
//
//				// Check
//				if (!cont) {
//					return false;
//				}
//
//				return false;
//			}
//			return false;
//		}
//	}

	private boolean areThereWorlds() {
		File worldFolder = AnniSubDirectory.WORLDS_DIRECTORY.getDirectory ( );
//		File worldFolder = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath() + "/Worlds");
		if (!worldFolder.exists())
			worldFolder.mkdir();
		File[] files = worldFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() && !name.endsWith(TEMP_PREFIX);
			}
		});
		return files.length > 0;
	}

	private boolean areThereBossWorlds() {
//		File worldFolder = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath() + "/BossWorlds");
		File worldFolder = AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getDirectory ( );
		if (!worldFolder.exists())
			worldFolder.mkdir();
		File[] files = worldFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() && !name.endsWith(TEMP_PREFIX);
			}
		});
		return files.length > 0;
	}

	private ItemMenu getWorldMenu(ItemMenu parent, ItemClickHandler handler) {
//		File worldFolder = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath() + "/Worlds");
		File worldFolder = AnniSubDirectory.WORLDS_DIRECTORY.getDirectory ( );
		if (!worldFolder.exists())
			worldFolder.mkdir();
		File[] files = worldFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() && !name.endsWith(TEMP_PREFIX);
			}
		});
		ItemMenu menu = new ItemMenu("§4§lAnnihilation | Maps Menu", Size.fit(files.length), parent);
		for (int x = 0; x < files.length; x++) {
			menu.setItem(x, new ComboMenuItem(files[x].getName(), menu.getParent(), handler,
					new ItemStack(Material.GRASS), "§6Click To Edit: " + ChatColor.GOLD + files[x].getName()));
		}
		return menu;
	}

	private ItemMenu getBossWorldMenu(ItemMenu parent, ItemClickHandler handler) {
//		File worldFolder = new File(AnnihilationMain.INSTANCE.getDataFolder().getAbsolutePath() + "/BossWorlds");
		File worldFolder = AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getDirectory ( );
		if (!worldFolder.exists())
			worldFolder.mkdir();
		File[] files = worldFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() && !name.endsWith(TEMP_PREFIX);
			}
		});
		ItemMenu menu = new ItemMenu("§4§lAnnihilation | Boss Maps", Size.fit(files.length), parent);
		for (int x = 0; x < files.length; x++) {
			menu.setItem(x, new ComboMenuItem(files[x].getName(), menu.getParent(), handler,
					new ItemStack(Material.BEDROCK), "§6Click To Edit " + ChatColor.GOLD + files[x].getName()));
		}
		return menu;
	}

	public static BlockFace getCardinalDirection(Player player) {
		BlockFace f = null;
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			f = BlockFace.EAST;
		}
		//
		else if (22.5 <= rotation && rotation < (67.5 / 2)) {
			f = BlockFace.EAST;
		} else if ((67.5 / 2) <= rotation && rotation < 67.5) {
			f = BlockFace.SOUTH;
		}
		//
		else if (67.5 <= rotation && rotation < 112.5) {
			f = BlockFace.SOUTH;
		}
		//
		else if (112.5 <= rotation && rotation < (157.5 / 2)) {
			f = BlockFace.SOUTH;
		} else if ((157.5 / 2) <= rotation && rotation < 157.5) {
			f = BlockFace.WEST;
		}
		//
		else if (157.5 <= rotation && rotation < 202.5) {
			f = BlockFace.WEST;
		}
		//
		else if (202.5 <= rotation && rotation < (247.5 / 2)) {
			f = BlockFace.WEST;
		} else if ((247.5 / 2) <= rotation && rotation < 247.5) {
			f = BlockFace.NORTH;
		}
		//
		else if (247.5 <= rotation && rotation < 292.5) {
			f = BlockFace.NORTH;
		}
		//
		else if (292.5 <= rotation && rotation < (337.5 / 2)) {
			f = BlockFace.NORTH;
		} else if ((337.5 / 2) <= rotation && rotation < 337.5) {
			f = BlockFace.EAST;
		}
		//
		else if (337.5 <= rotation && rotation < 360.0) {
			f = BlockFace.EAST;
		} else {
			f = null;
		}
		return f.getOppositeFace();
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void helpersOnABlock(PlayerInteractEvent eve) {
		if (eve.getAction() == Action.LEFT_CLICK_BLOCK || eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player p = eve.getPlayer();
			if (p == null || eve.getClickedBlock() == null)
				return;
			//
			if (KitUtils.isOnGameMap(eve.getPlayer())) {
				GameMap map = Game.getGameMap();
				//
				if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.BREWINGSHOP.getName())) {
					eve.setCancelled(true);
					Block b = eve.getClickedBlock().getRelative(eve.getBlockFace());
					if (eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
						BlockFace face = eve.getBlockFace();
						if (b != null) {
							AnniSign sign = new AnniSign(
									new FacingObject(getCardinalDirection(p).getOppositeFace(),
											new Loc(b.getLocation(), false)),
									face == BlockFace.UP ? true : (face == BlockFace.DOWN ? true : false),
									SignType.Brewing);
							map.getSigns().addSign(sign);
						}
					} else {
						if (map.getSigns().removeSign(eve.getClickedBlock().getLocation()))
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Removed A Sign");
					}
				}
				else if (KitUtils.itemHasName(eve.getItem(), CustomItem.BEACON_HANDLER.getName())) {
					eve.setCancelled(true);
					Block b = eve.getClickedBlock().getRelative(eve.getBlockFace());
					if (eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (b != null) {
							map.setBeaconLoc(new Loc(b.getLocation(), false));
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Magic Beacon Setted!");
							b.setType(Material.BEACON);
						}
					}
					else {
						Loc currentBeacon = map.getBeaconLoc();
						if (currentBeacon != null 
								&& currentBeacon.equals(eve.getClickedBlock().getLocation())) {
							map.setBeaconLoc(null);
							eve.getClickedBlock().setType(Material.AIR);
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Magic Beacon Removed!");
						}
					}
				}
				else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.WEAPONSHOP.getName())) {
					eve.setCancelled(true);
					Block b = eve.getClickedBlock().getRelative(eve.getBlockFace());
					if (eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
						BlockFace face = eve.getBlockFace();
						if (b != null) {
							AnniSign sign = new AnniSign(
									new FacingObject(getCardinalDirection(p).getOppositeFace(),
											new Loc(b.getLocation(), false)),
									face == BlockFace.UP ? true : (face == BlockFace.DOWN ? true : false),
									SignType.Weapon);
							map.getSigns().addSign(sign);
						}
					} else {
						if (map.getSigns().removeSign(eve.getClickedBlock().getLocation()))
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Removed A Sign");
					}
				}
				// Ender Furnaces
				else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.ENDERFURNACE.getName())) {
					eve.setCancelled(true);
					Block b = eve.getClickedBlock().getRelative(eve.getBlockFace());
					if (eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (b != null)
							map.addEnderFurnace(new Loc(b.getLocation(), false),
									getCardinalDirection(p).getOppositeFace());
					} else {
						if (b != null && map.removeEnderFurnace(eve.getClickedBlock().getLocation()))
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Removed An Ender Furnace.");
					}
				}
				// Ender Brewings
				else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.ENDER_BREWING.getName())) {
					// Cancell Event
					eve.setCancelled(true);

					// Get Block
					Block b = eve.getClickedBlock().getRelative(eve.getBlockFace());
					if (b != null) {
						if (eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
							map.addBrewingStand(new Loc(b.getLocation(), false));
						} else {
							if (map.removeEnderBrewing(eve.getClickedBlock().getLocation())) {
								p.sendMessage(ChatColor.LIGHT_PURPLE + "Removed An Ender Brewing.");
							}
						}
					}
				} else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.REGENBLOCKHELPER.getName())) {
					eve.setCancelled(true);
					if (!p.isConversing()) {
						Block b = eve.getClickedBlock();
						if (b != null) {
							Conversation conv = factory.withModality(true).withFirstPrompt(new RegenBlockPrompt(b))
									.withLocalEcho(true).buildConversation(p);
							conv.begin();
						}
					}
				} else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.UNPLACEABLEBLOCKSWAND.getName())) {
					eve.setCancelled(true);
					final Block b = eve.getClickedBlock();
					if (eve.getAction() == Action.LEFT_CLICK_BLOCK) {
						SingleQuestionPrompt.newPrompt(p,
								ChatColor.LIGHT_PURPLE + "Do you want to add " + ChatColor.GREEN + "This"
										+ ChatColor.LIGHT_PURPLE + " or " + ChatColor.GREEN + "All"
										+ ChatColor.LIGHT_PURPLE + " data values?",
								new AcceptAnswer() {
									@Override
									public boolean onAnswer(String input) {
										byte dataval = -2;
										boolean go = false;
										if (input.equalsIgnoreCase("this")) {
											dataval = b.getData();
											go = true;
										} else if (input.equalsIgnoreCase("all")) {
											dataval = -1;
											go = true;
										}

										if (go) {
											if (Game.getGameMap() != null) {
												if (Game.getGameMap().addUnplaceableBlock(b.getType(), dataval))
													p.sendMessage(ChatColor.GREEN + "The block was added.");
												else
													p.sendMessage(ChatColor.RED + "The block was not added.");
											}
											return true;
										}
										return false;
									}
								});
					} else {
						SingleQuestionPrompt.newPrompt(p,
								ChatColor.LIGHT_PURPLE + "Do you want to remove " + ChatColor.RED + "This"
										+ ChatColor.LIGHT_PURPLE + " or " + ChatColor.RED + "All"
										+ ChatColor.LIGHT_PURPLE + " data values?",
								new AcceptAnswer() {
									@Override
									public boolean onAnswer(String input) {
										byte dataval = -2;
										boolean go = false;
										if (input.equalsIgnoreCase("this")) {
											dataval = b.getData();
											go = true;
										} else if (input.equalsIgnoreCase("all")) {
											dataval = -1;
											go = true;
										}

										if (go) {
											if (Game.getGameMap() != null) {
												if (Game.getGameMap().removeUnplaceableBlock(b.getType(), dataval))
													p.sendMessage(ChatColor.GREEN + "The block was removed.");
												else
													p.sendMessage(ChatColor.RED + "The block was not removed.");
											}
											return true;
										}
										return false;
									}
								});
					}
				} else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.DIAMONDHELPER.getName())) {
					eve.setCancelled(true);
					//
					final Block b = eve.getClickedBlock();
					final Loc locSet = new Loc(b.getLocation(), false);
					//
					if (eve.getAction() == Action.LEFT_CLICK_BLOCK) {
						map.addDiamond(locSet);
						//
						BlockData bdata = Util.getBlockData(locSet.toLocation());
						bdata.setData("key-old-type", locSet.toLocation().getBlock().getType());
						//
						locSet.toLocation().getBlock().setType(Material.DIAMOND_BLOCK);
						//
						p.sendMessage(ChatColor.AQUA + "Diamond Added!");
					} else {
						if (map.removeDiamond(locSet)) {
							BlockData bdata = Util.getBlockData(locSet.toLocation());
							//
							Material m = (Material) bdata.getData("key-old-type");
							if (m == null)
								m = Material.STONE;
							//
							locSet.toLocation().getBlock().setType(m);
							//
							p.sendMessage(ChatColor.AQUA + "Diamond Removed!");
						}
					}
				} else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.Barita_Portal.getName())) {
					eve.setCancelled(true);
					final Block b = eve.getClickedBlock();
					if (eve.getAction() == Action.LEFT_CLICK_BLOCK) {
						map.addPortal(new Loc(b.getLocation(), false));
						p.sendMessage(ChatColor.LIGHT_PURPLE + "Portal Spawn Added!");
					} else {
						if (map.removePortal(new Loc(b.getLocation(), false)))
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Portal Spawn Removed!");
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void helpers_All(PlayerInteractEvent eve) {
		final Player p = eve.getPlayer();
		if (p == null)
			return;

		if (eve.getAction() != null && eve.getAction().name().contains("_CLICK_")) {
			if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.MAPBUILDER.getName())) {
				if (eve.getAction().name().contains("RIGHT_CLICK_")) {
					eve.setCancelled(true);
					if (!KitUtils.isOnBossMap(p))
						openMapBuilder(p);
					else
						openBossMapBuilder(p);
				}
			} else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.Spawn_Boss_Map_Seter.getName())) {
				if (KitUtils.isOnBossMap(p)) {
					eve.setCancelled(true);
					GameBoss.getBossMap().addSpawn(p.getLocation(), true);
					p.sendMessage(ChatColor.LIGHT_PURPLE + "Spawn Added!");
				}
			} else if (KitUtils.itemHasName(p.getItemInHand(), CustomItem.BOSS_MASTER.getName())) {
				if (!KitUtils.isOnLobby(p)) {
					eve.setCancelled(true);
					if (!p.isConversing()) {
						Conversation conv = factory.withModality(true).withFirstPrompt(new SetBossPrompt(p))
								.withLocalEcho(true).buildConversation(p);
						conv.begin();
					}
				} else
					p.sendMessage("§cYou cant add a boss in the Lobby");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void teamBlock(final BlockBreakEvent event) {
		// Values
		final Player player = event.getPlayer();
		TeamBlock t = null;
		// Get block Team
		if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Red.getName())) {
			t = TeamBlock.Red;
		} else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Blue.getName())) {
			t = TeamBlock.Blue;
		} else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Green.getName())) {
			t = TeamBlock.Green;
		} else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Yellow.getName())) {
			t = TeamBlock.Yellow;
		}

		// Check team block is not null
		if (t != null) {
			// They made a click with a team block
			event.setCancelled(true);
		}
		player.updateInventory ( );
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void teamBlock(final BlockPlaceEvent event) {
		// Values
		final Player player = event.getPlayer();
		TeamBlock t = null;
		// Get block Team
		if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Red.getName())) {
			t = TeamBlock.Red;
		} else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Blue.getName())) {
			t = TeamBlock.Blue;
		} else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Green.getName())) {
			t = TeamBlock.Green;
		} else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Yellow.getName())) {
			t = TeamBlock.Yellow;
		}

		// Check team block is not null
		if (t != null) {
			// They made a click with a team block
			event.setCancelled(true);
		}
		player.updateInventory ( );
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void teamBlock(final PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			// Values
			final Player player = event.getPlayer();
			TeamBlock t = null;
			// Get block Team
			if (KitUtils.itemHasName(event.getItem(), TeamBlock.Red.getName())) {
				t = TeamBlock.Red;
			} else if (KitUtils.itemHasName(event.getItem(), TeamBlock.Blue.getName())) {
				t = TeamBlock.Blue;
			} else if (KitUtils.itemHasName(event.getItem(), TeamBlock.Green.getName())) {
				t = TeamBlock.Green;
			} else if (KitUtils.itemHasName(event.getItem(), TeamBlock.Yellow.getName())) {
				t = TeamBlock.Yellow;
			}

			// Check team block is not null
			if (t != null) {
				// They made a click with a team block
				event.setCancelled(true);
				List<MetadataValue> vals = player.getMetadata("TeamHandler");
				if (vals != null && vals.size() == 1) {
					Object obj = vals.get(0).value();
					if (obj != null && obj instanceof TeamBlockHandler) {
						((TeamBlockHandler) obj).onBlockClick(player, t.Team, event.getAction(),
								event.getClickedBlock(), event.getBlockFace());
					}
				}
			}
			player.updateInventory ( );
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void RemoverAldeanosJoinder(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() != null && event.getRightClicked() instanceof Villager) {
			Villager vie = (Villager) event.getRightClicked();
			if (event.getPlayer() != null && KitUtils.isOnLobby(event.getPlayer())) {
				Player p = event.getPlayer();
				if (p.getGameMode() == GameMode.CREATIVE) {
					if (p.isOp() || p.hasPermission("A.anni")) {
						if (vie.getCustomName() != null && vie.isCustomNameVisible()) {
							vie.remove();
							p.sendMessage(ChatColor.LIGHT_PURPLE + "AldeanoJoinder Removed!");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	//
	private class Wrapper {
		public final ItemMenu main;
		public boolean useMap;

		//
		public Wrapper(ItemMenu menu) {
			main = menu;
			useMap = false;
		}
	}

	//
	private class BossWrapper {
		public final ItemMenu inBossMap;
		public boolean useBossMap;

		//
		public BossWrapper(ItemMenu menu) {
			inBossMap = menu;
			useBossMap = false;
		}
	}
}
