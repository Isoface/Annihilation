package com.hotmail.AdrianSRJose.AnniPro.main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.bobacadodl.imgmessage.ImageChar;
import com.bobacadodl.imgmessage.ImageMessage;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerInteractAtAnniItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJoinTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Nexus;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class TeamCommand implements CommandExecutor, Listener 
{
	public static ItemMenu menu;
	private static Map<AnniTeam, ActionMenuItem> items = new HashMap<AnniTeam, ActionMenuItem>();
	private ItemStack barrier;
	private static Map<ChatColor, ImageMessage> messages;
	public TeamCommand(JavaPlugin plugin) {
		// Set Variables
		menu     = new ItemMenu(Lang.TEAM_MENU_NAME.toString(), Size.ONE_LINE);
		messages = new EnumMap<ChatColor, ImageMessage>(ChatColor.class);

		// Get Command And Register Events
		plugin.getCommand("Team").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);

		// Get Universal close item
		barrier = new ItemStack(Material.BARRIER, 1);
	
		// Items Pos int
		int itPos = 0;
		
		// Set
		for (AnniTeam team : AnniTeam.Teams) {
			// Get BufferedImage and Lore
			BufferedImage image = AnnihilationMain.API.getTeamBufferedImage(team);

			// Get ImageMessage and append Text With Lore
			ImageMessage message = new ImageMessage(image, 10, ImageChar.MEDIUM_SHADE);
			message.appendTextToLines(5, Lang.JOINTEAM.toStringReplacement(team.getExternalColoredName())
					.replace("%tc", team.getColor().toString()).split("%n"));

			// Save
			messages.put(team.getColor(), message);
			
			// Put Action Items
			items.put(team, new ActionMenuItem(team.getExternalColoredName(), new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					menu.update(event.getPlayer());
					event.getPlayer().performCommand("Team " + team.getName());
					// items.get(team).setIcon(getColor(team).toItemStack((VersionUtils.getIntVersion() >= 12 ? Math.max(1, team.getPlayerCount()) : team.getPlayerCount())));
					items.get(team).setIcon(new WoolItemStack ( getWoolColor ( team ) , Math.max ( 1 , team.getPlayerCount ( ) ) ));
				}
			}, new WoolItemStack ( getWoolColor ( team ) , Math.max ( 1 , team.getPlayerCount ( ) ) ), 
					new String[] { team.getColor().toString() + "" }));
			
			// action.setIcon ( new WoolItemStack ( getWoolColor ( team ) , Math.max ( 1 , team.getPlayerCount ( ) ) ) );
			
			// Set
			menu.setItem(itPos, items.get(team));
			
			// Set Action Items
			itPos ++;

			// Leave team button
			menu.setItem(5,
					new ActionMenuItem(Lang.TEAM_LEAVE_BUTTON_NAME.toString(), new ItemClickHandler() {
						@Override
						public void onItemClick(ItemClickEvent event) {
							event.getPlayer().performCommand("Team Leave");
							AnniPlayer v = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
							leave(v);
						}
					}, new WoolItemStack ( WoolColor.BLACK , 1 ) , new String[] {}));

			// Random Team Selector
			if (Config.USE_AUTO_TEAM_SELECTOR.toBoolean()) {
				menu.setItem(6, new ActionMenuItem(ChatColor.GREEN + Lang.AUTO_TEAM_BUTTON_NAME.toString(),
						new ItemClickHandler() {
							@Override
							public void onItemClick(ItemClickEvent event) {
								final AnniTeam random = getRandomTeam();
								if (random != null) {
									event.getPlayer().performCommand("Team " + random.getName());
								}
							}
						}, new WoolItemStack ( WoolColor.WHITE , 1 ) , new String[] {}));
			}

			// Exit menu button
			menu.setItem(8, new ActionMenuItem(Lang.EXIT_BUTTON.toString(), new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					event.setWillClose(true);
				}
			}, barrier, new String[] {}));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void voteGUIcheck(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			final Player player = event.getPlayer();
			if (KitUtils.itemHasName(event.getItem(), CustomItem.TEAMMAP.getName())) {
				PlayerInteractAtAnniItemEvent e = new PlayerInteractAtAnniItemEvent(AnniPlayer.getPlayer(player),
						event.getItem());
				Bukkit.getPluginManager().callEvent(e);

				if (!e.isCancelled()) {
					openTeamGUI(player);
				}

				event.setCancelled(true);
			}
		}
	}

	public static void openTeamGUI ( final Player player ) {
		if (menu != null) {
			menu.open(player);

			new BukkitRunnable() {
				@Override
				public void run() {
					if (player.getOpenInventory() != null) {
						if ( Objects.equals ( Lang.TEAM_MENU_NAME.toString ( ) , player.getOpenInventory ( ).getTitle ( ) ) ) {
//						if (player.getOpenInventory().getTopInventory().getTitle()
//								.contains(Lang.NOMBRE_DEL_MENU_DE_EQUIPOS.toString())) {
							for (AnniTeam team : AnniTeam.Teams) {
								// Get Item
								ActionMenuItem action = items.get(team);
								
								// Update Icon
//								action.setIcon ( getColor ( team ).toItemStack ( Math.max ( 1 , team.getPlayerCount ( ) ) ) );
								action.setIcon ( new WoolItemStack ( getWoolColor ( team ) , Math.max ( 1 , team.getPlayerCount ( ) ) ) );
							}

							// Update Menu
							menu.update(player);
						}
					} else {
						cancel();
					}
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final AnniPlayer p = AnniPlayer.getPlayer(((Player) sender).getUniqueId());
			if (args.length == 0) {
				String[] messages = new String[8];
				for (int x = 0; x < 4; x++) {
					AnniTeam t = AnniTeam.Teams[x];
					int cat = x * 2;
					messages[cat] = t.getColor() + "/Team " + t.getExternalName() + ":";
					int y = t.getPlayerCount();
					messages[cat + 1] = t.getColor() + Lang.TEAMCHECK.toStringReplacement(y, t.getExternalName());
				}
				sender.sendMessage(messages);
			} else if (args.length > 0) {
				if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("exit")) {
					if (!Game.isGameRunning()) {
						if (p.getTeam() != null) {
							PlayerLeaveTeamEvent e = new PlayerLeaveTeamEvent(p, p.getTeam());
							AnniEvent.callEvent(e);
							//
							if (!e.isCancelled()) {
								sender.sendMessage(
										Lang.LEAVETEAM.toStringReplacement(p.getTeam().getExternalColoredName()));
								p.getTeam().leaveTeam(p);
							}
						} else
							sender.sendMessage(Lang.NOTEAM.toString());
					} else
						sender.sendMessage(Lang.CANNOTLEAVE.toString());
				} else if (args[0].equalsIgnoreCase("set")) {
					if (Game.isGameRunning()) {
						if (args.length > 1) {
							if (args[1].equalsIgnoreCase("health")) {
								if (sender.hasPermission("Anni.SetTeamHealth")) {
									if (args.length > 2) {
										AnniTeam team = AnniTeam.getTeamByName(args[2]);
										if (team != null) {
											if (args.length > 3) {
												try {
													// check health
													int health = Integer.valueOf(args[3]);
													if (!(health <= 99)) {
														sender.sendMessage(ChatColor.RED + "The Max health cannot be max to 99!");
														return true;
													}

													// check game is runnig
													if (Game.isNotRunning()) {
														sender.sendMessage(ChatColor.RED + "The Game is not Running!");
														return true;
													}
													
													// old health
													final int oldHealth = team.getHealth();
													
													// set health and send setted message
													AnnihilationMain.API.setTeamHealth(team, Integer.valueOf(health));
													
													// if was dead
													if (oldHealth <= 0 && !team.isTeamDead()) {
														for (AnniPlayer ap : team.getPlayers()) {
															if (ap == null) {
																continue;
															}
															
															if (ap.isOnDeathLobby()) {
																Player pl = ap.getPlayer();
																pl.setGameMode(GameMode.SURVIVAL);
																pl.getInventory().clear();
																pl.setHealth(0.0D);
																pl.updateInventory();
															}
														}
													}
													
													// when is destroy
													if (health <= 0) {
														for (Player pp : Bukkit.getOnlinePlayers()) {
															sendDestroyedImage(pp, team);
														}
													} else {
														sender.sendMessage(ChatColor.GREEN + "Setted!");
													}
													
													// end game
													Nexus.gameOverCheck();
													return true;
												}
												catch(IllegalArgumentException e) {
													sender.sendMessage(ChatColor.RED + "Invalid Health!");
													return true;
												}
											}
											else {
												sender.sendMessage(ChatColor.RED + "Usage: /team set health [health number] [team]");
											}
										}
										else {
											sender.sendMessage(ChatColor.RED + "Invalid Team!");
										}
									} 
									else {
										sender.sendMessage(ChatColor.RED + "Usage: /team set health [health number] [team]");
									}
								} 
								else {
									sender.sendMessage(Lang.WITHOUT_PERMISSION_COMMAND.toString());	
								}
								return true;
							}
						}
						
						if (sender.hasPermission("Anni.ChangeTeam")) {
							if (args.length > 1) {
								Player t = Bukkit.getPlayer(args[1]);
								AnniPlayer at = AnniPlayer.getPlayer(t);
								if (at != null && at.isOnline()) {
									if (args.length > 2) {
										AnniTeam team = AnniTeam.getTeamByName(args[2]);
										if (team != null) {
											if (at.hasTeam()) {
												changeTeam(at, team);
												sender.sendMessage(ChatColor.GREEN + "Changed!");
											} else {
												joinTeam(at, team, false, true);
												sender.sendMessage(ChatColor.GREEN + "Setted!");
											}
										} else
											sender.sendMessage(ChatColor.RED + "Invalid Team!");
									} else
										sender.sendMessage(ChatColor.RED + "Usage: /team set [Player] [team]");
								} else
									sender.sendMessage(ChatColor.RED + "Invalid Player!");
							} else
								sender.sendMessage(ChatColor.RED + "Usage: /team set [Player] [team]");
						} else
							sender.sendMessage(Lang.WITHOUT_PERMISSION_COMMAND.toString());
					} else
						sender.sendMessage(Lang.GAME_IS_NOT_RUNNING.toString());
				
				} else {
					AnniTeam t = AnniTeam.getTeamByName(args[0]);
					if (t != null) {
						this.joinCheck(t, p, false);
					} else
						sender.sendMessage(Lang.INVALIDTEAM.toString());
				}
			} else
				sender.sendMessage(Lang.TEAMHELP.toString());
		} else
			sender.sendMessage(Lang.MUST_BE_PLAYER_COMMAND.toString());
		return true;
	}
	
	private void sendDestroyedImage(final Player p, final AnniTeam team) {
		try {
			// Get Buffered Image Message
			final BufferedImage image = AnnihilationMain.API.getTeamBufferedImage(team);

			// Get ImageMessage and append Text With Lore
			ImageMessage message = new ImageMessage(image, 10,
					ImageChar.MEDIUM_SHADE);
			String s = Lang.TEAMDESTROYED.toStringReplacement(team.getExternalColoredName())
					.replace("%PLAYER%", p.getName());
			String[] o = (Util.untranslateAlternateColorCodes(s)).split("%n");

			// Replace team Colors
			for (int ii = 0; ii < o.length; ii++) {
				o[ii] = Util.wc(o[ii]).replace("%tc", team.getColor().toString());
			}

			// Append Text
			message.appendTextToLines(5, o);

			// Send message and play explode sound
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (KitUtils.isValidPlayer(pl)) {
					// Check enabled
					if (Config.USE_CHAT_TEAM_IMAGES.toBoolean()) {
						// Send Image Message
						message.sendToPlayer(pl);
					}

					// Play Explod Sound
					pl.getWorld().playSound(pl.getLocation(), UniversalSound.EXPLODE.asBukkit(), 1F, .8F);
				}
			}

			// Send Team destroyed Titles
			for (AnniPlayer anpt : team.getPlayers()) {
				if (anpt != null && KitUtils.isValidPlayer(anpt.getPlayer())) {
					Util.sendTitle(anpt.getPlayer(), Lang.TITLE_EQUIPO_DESTRUIDO.toString(),
							Lang.SUBTITLE_EQUIPO_DESTRUIDO.toString());
				}
			}
		} catch (Throwable t) {
		}
	}

	private void joinCheck(AnniTeam team, AnniPlayer p, boolean isChanging) {
		if (p != null && team != null) {
			Object obj = p.getData("TeamDelay");
			if (obj == null || System.currentTimeMillis() >= (long) obj) {
				p.setData("TeamDelay", System.currentTimeMillis() + 1000);
				Player player = p.getPlayer();

				if (Game.isNotRunning()) {
					if (p.getTeam() == null)
						checkTeam(team, p);
					else {
						if (!p.getTeam().equals(team))
							checkTeam(team, p);
					}
				} else {
					if (p.getTeam() == null) {
						checkTeam(team, p);
					} else {
						if (isChanging) {
							p.getTeam().leaveTeam(p);
							joinTeam(p, team, false, true);
						} else
							player.sendMessage(Lang.ALREADYHAVETEAM.toString());
					}
				}
			}
		}
	}

	public void checkTeam(AnniTeam team, AnniPlayer p) {
		Player player = p.getPlayer();

		if (p.getTeam() != null)
			p.getTeam().leaveTeam(p);

		if (team.isTeamDead()) {
			player.sendMessage(Lang.DESTROYEDTEAM.toString());
			return;
		}

		if (team.isFull() && Config.TEAM_BALANCING_MAX_PLAYERS.toInt() > 0) {
			player.sendMessage(Lang.JOIN_ON_FULL_TEAM_DENIED.toString());
			return;
		}

		if (Game.getGameMap() != null) {
			int phase = Game.getGameMap().getCurrentPhase();
			if (phase > 2) {
				boolean allowed = false;
				for (int x = phase; x < 6; x++) {
					if (player.hasPermission("Anni.JoinPhase." + x) || player.isOp()) {
						allowed = true;
						break;
					}
				}

				if (!allowed) {
					player.sendMessage(Lang.WRONGPHASE.toString());
					return;
				}
			}
		}

		if (player.hasPermission("Anni.BypassJoin")) {
			joinTeam(p, team, false, true);
			return;
		}

		if (Config.USE_TEAM_BALANCING.toBoolean()) {
			int currentTeamsPlayers = team.getPlayerCount();
			int smallest = Integer.MAX_VALUE;
			for (int x = 0; x < 4; x++) {
				AnniTeam t = AnniTeam.Teams[x];
				if (!t.isTeamDead() && t.getPlayerCount() < smallest)
					smallest = t.getPlayerCount();
			}

			if (currentTeamsPlayers - smallest > Config.TEAM_BALANCING_TOLERANCE.toInt()) {
				player.sendMessage(Lang.JOINANOTHERTEAM.toString());
				return;
			}
		}

		joinTeam(p, team, false, true);
	}

	public static void joinTeam(AnniPlayer player, AnniTeam team, boolean ignoreCancelled, boolean sendImageMessage) {
		PlayerJoinTeamEvent e = new PlayerJoinTeamEvent(player, team);
		AnniEvent.callEvent(e);
		//
		if (!e.isCancelled() || ignoreCancelled) {
			for (AnniTeam t : AnniTeam.Teams) {
				ActionMenuItem it = getItem(t);
				List<String> lor = it.getLore();
				if (lor == null)
					lor = new ArrayList<String>();
				if (t.equals(team)) {
					if (!lor.contains(ChatColor.GOLD + player.getName())) {
						lor.add(ChatColor.GOLD + player.getName());
					}
				} else {
					lor.remove(ChatColor.GOLD + player.getName());
				}
			}

			// Join and get player
			team.joinTeam(player);
			Player p = player.getPlayer();
			
			// Send image message
			if (sendImageMessage) {
				if (Config.USE_CHAT_TEAM_IMAGES.toBoolean()) {
					ImageMessage m = messages.get(team.getColor());
					m.sendToPlayer(p);
				}
			}
			
			// Kill
			if (Game.isGameRunning()) {
				p.setHealth(0);
			}
			
			// Registre on team
			registerOnTeams(player, team);
		}
		//
		menu.update(player.getPlayer());
	}

	@EventHandler
	public void leaveEvent(PlayerLeaveTeamEvent eve) {
		AnniPlayer p = eve.getPlayer();
		leave(p);
	}

	private static ActionMenuItem getItem(AnniTeam team) {
		if (team == null) {
			return null;
		}

		return items.get(team);
	}
	
	public static AnniTeam getRandomTeam() {
		// get random team.
		Random r = new Random();
		int i = r.nextInt(4);
		int smallest = Integer.MAX_VALUE;
		AnniTeam t = AnniTeam.Teams[i];
		for (AnniTeam te : AnniTeam.Teams) {
			if (!t.isTeamDead() && t.getPlayerCount() < smallest) {
				smallest = t.getPlayerCount();
			}

			if (t.getPlayerCount() - smallest <= te.getPlayerCount()) {
				return t;
			}
		}
		return null;
	}

	public static void leave(AnniPlayer v) {
		for (AnniTeam team : AnniTeam.Teams) {
			// Get Item
			ActionMenuItem action = items.get(team);
			
			// Update lore
			action.getLore().remove(ChatColor.GOLD + v.getName());
			
			// Update Icon
//			action.setIcon(new ItemStack(Material.WOOL, (VersionUtils.is1_11_or_1_12() ? Math.max(1, team.getPlayerCount()) : team.getPlayerCount()), getColor(team)));
//			action.setIcon(getColor(team).toItemStack((ServerVersion.serverNewerEqualsThan(ServerVersion.v1_12_R1) 
//					? Math.max(1, team.getPlayerCount()) : team.getPlayerCount())));
			action.setIcon ( new WoolItemStack ( getWoolColor ( team ) , Math.max ( 1 , team.getPlayerCount ( ) ) ) );
		}
	}
	
	private static WoolColor getWoolColor ( AnniTeam team ) {
		return WoolColor.valueOf ( team.getName ( ).toUpperCase ( ) );
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onDis(PlayerQuitEvent eve) {
		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		if (ap != null && ap.isOnline() && ap.hasTeam()) {
			ap.getPlayer().performCommand("team leave");
		}
	}

	@SuppressWarnings("deprecation")
	private static void registerOnTeams(AnniPlayer ap, AnniTeam team) {
		try {
			final Player p = ap.getPlayer();
			if (p == null) {
				return;
			}

//			if (Game.isGameRunning()) {
//				Scoreboard b = Game.getScoreboard();
//				Team t = LobbyScoreboard.getLobbyTeam(b);
//				t.removePlayer(p);
//			}

//			LobbyScoreboard.getLobbyTeam().removePlayer(p);
			team.verifyInBoardTeam(p);
//			if (!LobbyScoreboard.teams.get(team).hasPlayer(p)) {
//				LobbyScoreboard.teams.get(team).addPlayer(p);
//			}
			
//			LobbyScoreboard.getLobbyTeam().removePlayer(p);
//			team.verifyInBoardTeam(p);
//			if (!LobbyScoreboard.teams.get(team).hasPlayer(p)) {
//				LobbyScoreboard.teams.get(team).addPlayer(p);
//			}
			
		} catch (Throwable e) {
			
		}
	}

	public void changeTeam(AnniPlayer ap, AnniTeam team) {
		if (Game.isNotRunning()) {
			return;
		}

		if (ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}

		if (team == null) {
			return;
		}

		final Player p = ap.getPlayer();
		this.joinCheck(team, ap, true);
		p.setHealth(0.0D);
	}
}
