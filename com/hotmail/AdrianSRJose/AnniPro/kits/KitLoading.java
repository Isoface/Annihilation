package com.hotmail.AdrianSRJose.AnniPro.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerDropSoulboudItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerInteractAtAnniItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKitPortalEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils.SoulboundType;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.KitPrevlzerMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.KitsMenu;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniSubDirectory;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class KitLoading implements Listener, CommandExecutor {
	// private final KitMenuItem[] items;
	public static boolean                          shopEnabled = false;
	private static final Map<Integer, String>            NAMES = new HashMap<Integer, String>();

	public KitLoading(final JavaPlugin p) {
		// Get comapasses names
		int pose = 0;
		for (AnniTeam team : AnniTeam.Teams) {
			NAMES.put(Integer.valueOf(pose), (team.getColor().toString() + Lang.COMPASSTEXT.toStringReplacement(team.getExternalColoredName())));
			pose ++;
		}

		// Register events
		if (!Config.LOBBY_MODE.toBoolean()) {
			Bukkit.getPluginManager().registerEvents(this, p);
		}
		
		// Set command Executor
		p.getCommand("Kit").setExecutor(this);

		// Get Classes
		File classes = new File(p.getDataFolder().getAbsolutePath());
		if (!classes.exists() || !classes.isDirectory()) {
			classes.mkdir();
		}

		classes = AnniSubDirectory.KITS_DIRECTORY.getDirectory ( ); // new File (p.getDataFolder().getAbsolutePath() + "/Kits");
		if (classes != null && classes.exists() && classes.isDirectory()) {
			File[] files = classes.listFiles();
			URL[] urls = new URL[files.length];
			for (int x = 0; x < files.length; x++) {
				try {
					urls[x] = files[x].toURI().toURL();
				} catch (Exception e) {
				}
			}

			// Get URL Class Loader
			URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
			for (File file : files) {
				List<String> names = getClassNames(file);
				try {
					for (String name : names) {
						Class<?> cl = null;
						try {
							cl = loader.loadClass(name);
						} catch (Exception e) {
							e.printStackTrace();
							Util.print(ChatColor.RED, "Error loading class: " + name);
							continue;
						}

						// Check and register
						if (cl != null && !Modifier.isAbstract(cl.getModifiers()) && !cl.isAnonymousClass()) {
							if (Kit.class.isAssignableFrom(cl)) {
								@SuppressWarnings("unchecked")
								Class<Kit> k = (Class<Kit>) cl;
								Kit kit = k.newInstance();
								if (kit != null) { 
									if (kit.Initialize()) {
										if (!Kit.existsKit(kit)) {
											Bukkit.getPluginManager().registerEvents(kit, p);
											Kit.registerKit(kit);
										}
									}
								}
							}
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			// Close Loader
			if (loader != null) {
				try {
					loader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Pring Loaded Kits
			Bukkit.getConsoleSender()
					.sendMessage(Util.wc("&a&n[Annihilation]&l&f ---> " + Kit.getKits().size() + " Loaded Kits" + (Config.LOBBY_MODE.toBoolean() ? " for the kit Shop" : "")));
		} else {
			classes.mkdir();
		}
	}

	private List<String> getClassNames(File file) {
		List<String> classNames = new ArrayList<String>();
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(new FileInputStream(file));
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
					StringBuilder className = new StringBuilder();
					for (String part : entry.getName().split("/")) {
						if (className.length() != 0)
							className.append(".");
						className.append(part);
						if (part.endsWith(".class"))
							className.setLength(className.length() - ".class".length());
					}
					classNames.add(className.toString());
				}
			}
		} catch (IOException e) {
			classNames = null;
		} finally {
			try {
				if (zip != null)
					zip.close();
			} catch (IOException e) {
			}
		}
		return classNames;
	}

	public static boolean ShopIsEnabled() {
		return shopEnabled;
	}

	public void openKitMap(Player player) {
		if (KitUtils.isValidPlayer(player) && player.getUniqueId() != null) {
			refreshMenu(player).open(player);
		}
	}

	private KitsMenu refreshMenu(final Player owner) {
		return new KitsMenu(owner);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void ClassChanger(final PlayerPortalEvent event) {
		// Check is from nether portal
		if (event.getCause() != TeleportCause.NETHER_PORTAL) {
			return;
		}

		final Location from = event.getFrom();
		if (Game.isGameRunning() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			final AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
			if (p != null) {
				// Cancell teleport
				event.setCancelled(true);

				// Check has team
				if (p.hasTeam()) {
					// Get player
					final Player pl = event.getPlayer();

					// Check player is on game map
					if (!KitUtils.isGameMap(from.getWorld())) {
						return;
					}

					// to and random spawn
					Location to = null;
					Location rs = p.getTeam().getRandomSpawn();
					to = rs;

					// Call new PlayerKitPortalEvent
					PlayerKitPortalEvent pkpv = new PlayerKitPortalEvent(p, from, to);
					AnniEvent.callEvent(pkpv);

					// Check is not cancelled
					if (!pkpv.isCancelled()) {

						// teleport player to random spawn
						pl.teleport(rs);
						Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
							@Override
							public void run() {
								// Open kits menu
								openKitMap(pl);

								// Play sound
								event.getPlayer().playSound(event.getPlayer().getLocation(),
										UniversalSound.PORTAL_TRAVEL.asBukkit(), 1F, 1F);
							}
						}, 40);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void openKitMenuCheck(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (KitUtils.itemHasName(event.getItem(), CustomItem.KITMAP.getName())) {
				PlayerInteractAtAnniItemEvent eve = new PlayerInteractAtAnniItemEvent(
						AnniPlayer.getPlayer(event.getPlayer()), event.getItem());
				Bukkit.getPluginManager().callEvent(eve);

				if (!eve.isCancelled()) {
					openKitMap(event.getPlayer());
				}

				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void navCompassHubCheckAndKitShopCheck(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			final Player player = event.getPlayer();
			final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
			final ItemStack item = event.getItem();
			if (ap == null || ap.getPlayer() == null) {
				return;
			}

			if (KitUtils.isOnLobby(player)) {
				if (KitUtils.itemHasName(item, CustomItem.GO_TO_LOBBY.getName())) 
				{
					PlayerInteractAtAnniItemEvent eve = new PlayerInteractAtAnniItemEvent(ap, item);
					Bukkit.getPluginManager().callEvent(eve);
					if (!eve.isCancelled()) {
						if (Config.USE_LOBBY_COMMPAS.toBoolean()) 
						{
							if (Config.LOBBY_COMMPAS_KICK_PLAYER.toBoolean()) {
								/* kick player */
								player.kickPlayer("...");
							} else if (Config.LOBBY_COMPAS_USING_COMMAND.toBoolean()) {
								/* get and check the command to perform */
								final String command = Config.LOBBY_COMPAS_COMMAND_TO_PERFORM.toString();
								if (!StringUtils.isBlank(command)) {
									/* get and check command to perform */
									final String command_to_perform = command.replace("/", "");
									if (!command_to_perform.isEmpty()) {
										/* make player to perform the command */
										player.performCommand(Config.LOBBY_COMPAS_COMMAND_TO_PERFORM.toString());
									}
								}
								
							} else {
								String server_to = Config.LOBBY_COMMPAS_SERVER_TO.toString();
								if (server_to != null && !server_to.isEmpty()) {
									// send player to bungee server in config.
									AnnihilationMain.SENDER.sendPlayerTo(player, server_to);
								}
							}
						}
					}
				} else {
					if (KitUtils.itemHasName(item, CustomItem.KIST_SHOT.getName())) {
						PlayerInteractAtAnniItemEvent eve = new PlayerInteractAtAnniItemEvent(ap, item);
						Bukkit.getPluginManager().callEvent(eve);
						if (!eve.isCancelled()) {
							player.performCommand("shop");
						}
					}
				}
			}
		}
	}
	
	// COMPASSES_NAMES
	@EventHandler(priority = EventPriority.HIGHEST)
	public void navCompassCheck(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			final Player player  = event.getPlayer();
			final ItemStack item = event.getItem();
			String name          = null;
			AnniTeam target      = null;
			
			if (KitUtils.itemHasName(item, CustomItem.NAVCOMPASS.getName())) {
				name   = NAMES.get(0);
				target = AnniTeam.Red;
			}
			else {
				// get current name
				final String curr = KitUtils.extractName(item, false);
				for (Integer it : NAMES.keySet()) {
					String st = NAMES.get(it);
					if (curr.equalsIgnoreCase(st)) {
						if ((it.intValue() + 1) >= NAMES.size()) {
							name   = NAMES.get(0);
							target = AnniTeam.Red;
						}
						else {
							name   = NAMES.get(Integer.valueOf(it.intValue() + 1));
							target = AnniTeam.Teams[it.intValue() + 1];
						}
					}
				}
			}
			
			// check
			if (name == null || target == null) {
				return;
			}
			
			// call events
			final PlayerInteractAtAnniItemEvent eve = new PlayerInteractAtAnniItemEvent(AnniPlayer.getPlayer(player),
					event.getItem());
			Bukkit.getPluginManager().callEvent(eve);

			// check is not cancelled
			if (eve.isCancelled()) {
				return;
			}

			// change name and target
			if (name != null && target != null) {
				// set name
				ItemMeta m = item.getItemMeta();
				m.setDisplayName(name);
				item.setItemMeta(m);
				
				// set target
				if (target.getNexus() != null && Util.isValidLoc(target.getNexus().getLocation())) {
					player.setCompassTarget(target.getNexus().getLocation().toLocation());
				}
			}
		}
	}

//	@EventHandler
//	public void navCompassCheck(PlayerMoveEvent event) {
//		final Player player = event.getPlayer();
//		final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
//		if (!Game.isGameRunning()) {
//			return;
//		}
//
//		if (!KitUtils.isOnGameMap(player)) {
//			return;
//		}
//
//		if (ap.getTeam() == null) {
//			return;
//		}
//
//		ItemStack item = null;
//		Loc target = null;
//		String name = null;
//		for (ItemStack stack : player.getInventory()) {
//			if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
//				if (stack.getItemMeta().getDisplayName().contains(redcompass)
//						|| stack.getItemMeta().getDisplayName().contains(bluecompass)
//						|| stack.getItemMeta().getDisplayName().contains(greencompass)
//						|| stack.getItemMeta().getDisplayName().contains(yellowcompass)) {
//					item = stack;
//
//					if (KitUtils.itemNameContains(item, redcompass)) {
//						target = AnniTeam.Red.getNexus().getLocation();
//						int distance = (int) player.getLocation().distance(target.toLocation());
//						name = redcompass;
//
//						ItemMeta m = item.getItemMeta();
//						m.setDisplayName(name + " §8Distance: " + "§7" + distance);
//						item.setItemMeta(m);
//					} else if (KitUtils.itemNameContains(item, bluecompass)) {
//						target = AnniTeam.Blue.getNexus().getLocation();
//						int distance = (int) player.getLocation().distance(target.toLocation());
//						name = bluecompass;
//
//						ItemMeta m = item.getItemMeta();
//						m.setDisplayName(name + " §8Distance: " + "§7" + distance);
//						item.setItemMeta(m);
//					} else if (KitUtils.itemNameContains(item, greencompass)) {
//						target = AnniTeam.Green.getNexus().getLocation();
//						int distance = (int) player.getLocation().distance(target.toLocation());
//						name = greencompass;
//
//						ItemMeta m = item.getItemMeta();
//						m.setDisplayName(name + " §8Distance: " + "§7" + distance);
//						item.setItemMeta(m);
//					} else if (KitUtils.itemNameContains(item, yellowcompass)) {
//						target = AnniTeam.Yellow.getNexus().getLocation();
//						int distance = (int) player.getLocation().distance(target.toLocation());
//						name = yellowcompass;
//
//						ItemMeta m = item.getItemMeta();
//						m.setDisplayName(name + " §8Distance: " + "§7" + distance);
//						item.setItemMeta(m);
//					}
//				}
//		}
//	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void StopDrops(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final Item item = event.getItemDrop();
		if (item != null) {
			ItemStack stack = item.getItemStack();
			if (stack != null) {
				if (KitUtils.isSoulbound(stack) || KitUtils.isClassSoulbound(stack)) {
					SoulboundType type = KitUtils.isSoulbound(stack) ? SoulboundType.Tradicional
							: SoulboundType.ClassSoulbound;
					PlayerDropSoulboudItemEvent pdsiv = new PlayerDropSoulboudItemEvent(player, item, type);
					AnniEvent.callEvent(pdsiv);
					//
					if (!pdsiv.isCancelled()) {
						player.getWorld().playSound(player.getLocation(), UniversalSound.ITEM_BREAK.asBukkit(), 4.0F, 15F);
						for (int x = 1; x < 11; x++) {
							if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1)) {
								player.spawnParticle(Particle.CRIT, player.getLocation().clone().add(0.0D, 1.0D, 0.0D), x);
							} else {
								player.playEffect(player.getLocation().clone().add(0.0D, 1.0D, 0.0D), Effect.valueOf("CRIT"), x);
							}
						}

						event.getItemDrop().remove();
					}
				} else if (KitUtils.isClassUndropabbleSoulbound(stack) && player.getGameMode() != GameMode.CREATIVE) {
					PlayerDropSoulboudItemEvent pdsiv2 = new PlayerDropSoulboudItemEvent(player, item,
							SoulboundType.Undropabble);
					AnniEvent.callEvent(pdsiv2);
					if (!pdsiv2.isCancelled()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void RemoveDeathDrops(PlayerDeathEvent event) {
		for (ItemStack s : new ArrayList<ItemStack>(event.getDrops())) {
			if (KitUtils.isAnySoulbound(s)) {
				event.getDrops().remove(s);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void StopClicking(InventoryClickEvent event) {
		final HumanEntity entity = event.getWhoClicked();
		final ItemStack stack = event.getCurrentItem();
		final Inventory inv = event.getView().getTopInventory();
		final InventoryType top = inv.getType();
		if (stack != null && (entity instanceof Player)) {
			if (event.getInventory() != null) {
				if ( Lang.BOSS_SHOP_NAME.toString ( ).equals ( event.getView ( ).getTitle ( ) ) ) {
					if (event.getClick() == ClickType.NUMBER_KEY) {
						event.setCancelled(true);
					}
				}
			}

			// Anti Boss Star Bug
			if (event.getAction() == InventoryAction.HOTBAR_SWAP && event.getClick() == ClickType.NUMBER_KEY) {
				final ItemStack from = ((Player) entity).getInventory().getItem(event.getSlot());
				final ItemStack moved = ((Player) entity).getInventory().getItem(event.getHotbarButton());
				if (KitUtils.isAnySoulbound(moved) || KitUtils.isAnySoulbound(from)) {
					if (inv.getType() != InventoryType.CRAFTING) {
						event.setCancelled(true);
						return;
					} else {
						if (KitUtils.isClassSoulbound(moved) || KitUtils.isClassSoulbound(from)) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}

			if (top == InventoryType.CRAFTING || top == InventoryType.PLAYER || top == InventoryType.ENDER_CHEST) {
				return;
			}

			if (KitUtils.isAnySoulbound(stack)) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			// Get Player
			final Player player = (Player) sender;
			final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
			
			// When not lobby mode
			if (!Config.LOBBY_MODE.toBoolean()) {
				// Check
				boolean open = false;
				if (player.hasPermission("Anni.ChangeKit") || player.isOp()) {
					open = true;
				}

				if (ap != null) {
					if (!ap.hasTeam() && KitUtils.isOnLobby(player)) {
						open = true;
					}
				}
				
				// Open
				if (open) {
					openKitMap(player);
				} else {
					sender.sendMessage(Lang.WITHOUT_PERMISSION_COMMAND.toString());
				}
			} // When Lobby mode
			else {
				final ItemMenu pre = new ItemMenu(Util.wc("&aPurchased kits!"), Size.fit(Kit.LoadedKits()));
				final List<Kit> kits = Kit.getKitList();
				final List<KitPrevlzerMenuItem> icons = new ArrayList<KitPrevlzerMenuItem>();
				for (int x = 0; (x < pre.getSize().getSize() && x < kits.size()); x++) {
					Kit k = kits.get(x);
					if (k != null && k.hasPermission(player)) {
						icons.add(new KitPrevlzerMenuItem(k));
					}
				}
				
				// Set in menu
				for (int x = 0; (x < pre.getSize().getSize() && x < icons.size()); x++) {
					pre.setItem(x, icons.get(x));
				}
				
				// Open
				pre.open(player);
			}
		} else {
			sender.sendMessage(Lang.MUST_BE_PLAYER_COMMAND.toString());
		}
		return true;
	}

	public class ShopButton extends MenuItem {
		public ShopButton(String displayName, ItemStack icon, String[] lore) {
			super(displayName, icon, lore);
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			eve.getPlayer().performCommand("shop");
		}
	}
}
