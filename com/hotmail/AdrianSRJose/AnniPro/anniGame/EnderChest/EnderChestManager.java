package com.hotmail.AdrianSRJose.AnniPro.anniGame.EnderChest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.EnderChestCloseEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.EnderChestOpenEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class EnderChestManager implements Listener, CommandExecutor {
	private static final Map<UUID, EnderChest> enderChests = new HashMap<UUID, EnderChest>();
	private static EnderChestConfig config;
	
	public EnderChestManager(JavaPlugin pl) {
		// Create EnderChestConfig instance
		try {
			config = new EnderChestConfig(pl);
		} catch (IOException e) {
			Util.print(ChatColor.RED, Util.wc("&cCould not load the Ender Chest Config!"));
			config = null;
			return;
		}

		// Check
		if (!config.isEnabled()) {
			return;
		}

		// Get Command
		if (config.isUseCommand()) {
			pl.getCommand("EnderChest").setExecutor(this);
		}

		// Register Events
		Bukkit.getPluginManager().registerEvents(this, pl);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (config == null) {
			sender.sendMessage(Util.wc("&cCould not load the Ender Chest Config!"));
			return true;
		}

		if (Game.isNotRunning()) {
			sender.sendMessage(Lang.CANT_OPEN_ENDER_CHEST_GAME_NOT_RUNNING.toString());
			return true;
		}

		if (args.length == 0) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (!p.hasPermission("Anni.EnderChest.Command") && !p.isOp()) {
					p.sendMessage(Lang.WITHOUT_PERMISSION_COMMAND.toString());
					return true;
				}

				if (!KitUtils.isOnGameMap(p)) {
					p.sendMessage(Lang.CANT_OPEN_ENDER_CHEST_ON_LOBBY.toString());
					return true;
				}

				// Open
				getEnderChest(p).open();
			} else
				sender.sendMessage(Lang.MUST_BE_PLAYER_COMMAND.toString());
		} else
			sender.sendMessage(Lang.INVALID_COMMAND_SYNTAX.toString());
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCloseChest ( InventoryCloseEvent eve ) {
		// Get inventory and player
		final Inventory inventory = eve.getInventory();
		final Player p = (Player) eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap == null) {
			return;
		}
		
		// Get Player Ender Chest and check
		final EnderChest ec = getEnderChest(p);
		if (ec == null) {
			return;
		}
		
		// Call Event
		if ( inventory.getType ( ) == ec.getInventory ( ).getType ( ) 
				&& Objects.equals ( ec.getInventoryName ( ) , eve.getView ( ).getTitle ( ) ) ) {
//		if (inv.getType() == ec.getInventory().getType() && inv.getTitle() == ec.getInventory().getTitle()) {
			final EnderChestCloseEvent event = new EnderChestCloseEvent(ap, inventory);
			AnniEvent.callEvent(event);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onClickChest(PlayerInteractEvent eve) {
		if (eve.getClickedBlock() == null) {
			return;
		}

		if (eve.getClickedBlock().getType() != Material.ENDER_CHEST) {
			return;
		}

		if (eve.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		// Cancell Interaction
		eve.setCancelled(true);

		// Get player
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap == null) {
			return;
		}
		
		// Get Chest
		final EnderChest inv = getEnderChest(p);
		
		// Call Event
		final EnderChestOpenEvent event = new EnderChestOpenEvent(ap, eve.getClickedBlock().getLocation(), inv.getInventory());
		AnniEvent.callEvent(event);
		
		// Check is cancelled
		if (event.isCancelled()) {
			return;
		}

		// Open and play sound
		eve.setCancelled(true);
		p.getOpenInventory().close();
		p.closeInventory();
		inv.open();
		p.updateInventory();
		
		if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_13_R1)) {
			p.playSound(p.getLocation(),
					Sound.valueOf("BLOCK_ENDER_CHEST_OPEN"), 10.0F,
					1.0F);
		} else if (ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1)) { // OLD: VersionUtils.isNewSpigotVersion()
			p.playSound(p.getLocation(),
					Sound.valueOf("BLOCK_ENDERCHEST_OPEN"), 10.0F,
					1.0F);
		} else {
			p.playSound(p.getLocation(),
					Sound.valueOf("CHEST_OPEN"), 10.0F,
					1.0F);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick(InventoryClickEvent event) {
		// Get HumanEntity and Check
		final HumanEntity entity = event.getWhoClicked();
		final Inventory inv = event.getView().getTopInventory();
		if (!(entity instanceof Player) || inv == null) {
			return;
		}

		// Get
		final Player p = (Player) entity;
		final EnderChest ec = getEnderChest(p);
//		final Inventory pinv = ec.getInventory();
		final ItemStack stack = event.getCurrentItem();
		
		if ( !Objects.equals ( ec.getInventoryName ( ) , event.getView ( ).getTitle ( ) ) ) {
			return;
		}
		
		if ( ec.getInventory ( ).getSize ( ) != inv.getSize ( ) ) {
			return;
		}

//		try {
//			if (!pinv.getTitle().equals(inv.getTitle())) {
//				return;
//			}
//
//			if (pinv.getSize() != inv.getSize()) {
//				return;
//			}
//		} catch (Throwable t) {
//			return;
//		}

		// Cancell movement
		if (event.getAction() == InventoryAction.HOTBAR_SWAP && event.getClick() == ClickType.NUMBER_KEY) {
			// System.out.println(1);
			final ItemStack moved = ((Player) entity).getInventory().getItem(event.getHotbarButton());
			final ItemStack from = ((Player) entity).getInventory().getItem(event.getSlot());
			if (isItemFromConfig(p, event.getHotbarButton(), moved) || isItemFromConfig(p, event.getSlot(), from)) {
				event.setCancelled(true);
			}
		}

		// Cancell movement
		if (isItemFromConfig(p, event.getSlot(), stack) && event.getSlot() == event.getRawSlot()) {
			boolean cancell = true;
			if (stack == null) {
				cancell = false;
			} else {
				if (stack.getType() == Material.AIR) {
					cancell = false;
				} else {
					cancell = true;
				}
			}

			// Cancell
			event.setCancelled(cancell);
		}
	}

	public static EnderChest getEnderChest(final Player p) {
		if (!enderChests.containsKey(p.getUniqueId()) || enderChests.get(p.getUniqueId()) == null) {
			enderChests.put(p.getUniqueId(), new EnderChest(AnniPlayer.getPlayer(p), config));
		}

		return enderChests.get(p.getUniqueId());
	}
	
	public static EnderChest getEnderChest(final UUID id) {
		if (!enderChests.containsKey(id) || enderChests.get(id) == null) {
			enderChests.put(id, new EnderChest(AnniPlayer.getPlayer(id), config));
		}

		return enderChests.get(id);
	}
	
	private boolean isItemFromConfig(final Player p, int slot, ItemStack stackInSLot) {
		final EnderChest ec = getEnderChest(p);
		for (ItemFromSection item : ec.getItems()) {
			if (item == null) {
				continue;
			}

			if (item.getSlot() == slot) {
				if (item.toItemStack() != null && item.toItemStack().getType() != null
						&& item.toItemStack().getType() != Material.AIR) {
					if (stackInSLot != null) {
						if (stackInSLot.getType() == item.toItemStack().getType()) {
							return true;
						} else
							continue;
					} else
						continue;
				} else
					continue;
			} else
				continue;
		}
		return false;
	}
}
