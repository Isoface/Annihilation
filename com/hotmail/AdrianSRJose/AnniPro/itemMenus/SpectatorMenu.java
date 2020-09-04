package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer.AnniGameMode;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class SpectatorMenu implements Listener, CommandExecutor {
	
	public static ItemMenu menu;
	private ItemStack barrier = new ItemStack(Material.BARRIER, 1);

	public SpectatorMenu(JavaPlugin p) {
		// Set Executor
		p.getCommand("Spectator").setExecutor(this);
		// Register Events
		Bukkit.getPluginManager().registerEvents(this, p);
		// Set New ItemMenu
		menu  = new ItemMenu(Lang.SPECTATORMAP.toString(), Size.ONE_LINE);
		int x = 0;
		for (final AnniTeam team : AnniTeam.Teams) {
			menu.setItem(x,
					new SpectatorMenuButton(team,
							"" + Lang.SPECTATOR_BUTTON.toStringReplacement(team.getExternalColoredName()),
							new WoolItemStack ( WoolColor.valueOf ( team.getName ( ).toUpperCase ( ) ) , 1 ), new String[] {}));
			x++;
		}

		menu.setItem(8, new ActionMenuItem(ChatColor.RED + "Exit", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				event.setWillClose(true);
			}
		}, barrier, new String[] {}));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void voteGUIcheck(final PlayerInteractEvent event) {
		// Check is Rigth Click
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			// Get and Check Player
			final Player player = event.getPlayer();
			if (player != null && KitUtils.itemHasName(player.getItemInHand(), CustomItem.SPECTATORMAP.getName())) {
				// Check is spectator mode enabled
				if (!Config.USE_SPECTATOR_MODE.toBoolean()) {
					return;
				}

				// Cancell
				event.setCancelled(true);
				menu.open(player);
			}
		}
	}

	public static boolean isSpectator(final AnniPlayer ap) {
		if (!Config.USE_SPECTATOR_MODE.toBoolean()) {
			return false;
		}

		if (ap == null) {
			return false;
		}

		if (ap.getPlayer() == null) {
			return false;
		}

		if (ap.hasTeam() && !ap.getTeam().isTeamDead()) {
			return false;
		}

		if (ap.getAnniGameMode() != AnniGameMode.Spectator) {
			return false;
		}

		if (KitUtils.isOnLobby(ap.getPlayer())) {
			return false;
		}
		return true;
	}

	public class SpectatorMenuButton extends MenuItem {
		private AnniTeam team;

		public SpectatorMenuButton(AnniTeam team, String displayName, ItemStack icon, String[] lore) {
			super(displayName, icon, lore);
			this.team = team;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			// Get and check player
			final Player p = eve.getPlayer();
			final AnniPlayer ap = AnniPlayer.getPlayer(p);
			if (p == null || ap == null) {
				return;
			}

			// Check is spectator mode enabled
			if (!Config.USE_SPECTATOR_MODE.toBoolean()) {
				return;
			}

			// Check
			if (!ap.hasTeam() || ap.getTeam().isTeamDead()) {
				if (team.getSpectatorLocation() != null) {
					// Teleport, send message, and change gamemode
					p.setGameMode(GameMode.SPECTATOR);
					p.teleport(team.getSpectatorLocation().toLocation());
					p.setGameMode(GameMode.SPECTATOR);
					ap.setGameMode(AnniGameMode.Spectator);
					p.sendMessage(Lang.ENTERONSPECTATORMODE.toString());

					// Change Game Mode task
					Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
						@Override
						public void run() {
							try {
								p.setGameMode(GameMode.SPECTATOR);
							} catch (Throwable t) {
								// Ignore
							}
						}
					}, 5L);

					// Task
					new BukkitRunnable() {
						@Override
						public void run() {
							if (KitUtils.isValidPlayer(p) && isSpectator(ap)) {
								p.sendMessage(Lang.ENTERONSPECTATORMODE.toString());
							} else {
								cancel();
							}
						}
					}.runTaskTimer(AnnihilationMain.INSTANCE, 600L, 600L);
				} else {
					p.sendMessage(ChatColor.RED + "The Spectator mode is not enabled.");
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("exit") || args[0].equalsIgnoreCase("quit")) {
					// Get and Check Player
					Player player = (Player) sender;
					AnniPlayer ap = AnniPlayer.getPlayer(player);
					if (ap != null) {
						if (SpectatorMenu.isSpectator(ap)) {
							// Clear Player Inventory
							player.getInventory().clear();
							player.updateInventory();

							// Send to Spawn
							Game.LobbyMap.sendToSpawn(player);

							// Change GameModes
							player.setGameMode(Config.GAME_MODE.toGameMode());
							ap.setGameMode(AnniGameMode.Normal);
						} else {
							player.sendMessage(Util.wc("&cYou cant use this command on lobby"));
						}
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
		}
		return true;
	}
}
