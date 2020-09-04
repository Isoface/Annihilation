package com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Passes inventory click events to their menus for handling.
 */
public class ShopMenuListener implements Listener {
	private Plugin plugin = null;
	private static final ShopMenuListener INSTANCE = new ShopMenuListener();

	private ShopMenuListener() {
	}

	/**
	 * Gets the {@link ninja.amp.ampmenus.MenuListener} instance.
	 *
	 * @return The {@link ninja.amp.ampmenus.MenuListener} instance.
	 */
	public static ShopMenuListener getInstance() {
		return INSTANCE;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null)
			return;
		if (event.getClickedInventory() == null)
			return;
		if (event.getClickedInventory().getType() == InventoryType.FURNACE)
			return;
		if (event.getInventory().getType() == InventoryType.FURNACE)
			return;

		try {
			final ItemStack st = event.getCurrentItem();
			if (event.getWhoClicked() instanceof Player && event.getInventory().getHolder() != null
					&& event.getInventory().getHolder() instanceof ShopMenuHolder) 
			{
				if (st != null && st.getType() != Material.AIR) {
					if (((ShopMenuHolder) event.getInventory().getHolder()).isPluginItem(event.getSlot())) {
						event.setCancelled(true);
						((ShopMenuHolder) event.getInventory().getHolder()).getMenu().onInventoryClick(event);
					}
				}
			}
		}
		catch(Throwable t) {
			event.setCancelled(true);
		}
	}

	/**
	 * Registers the events of the {@link ninja.amp.ampmenus.MenuListener} to a
	 * plugin.
	 *
	 * @param plugin
	 *            The plugin used to register the events.
	 */
	public void register(JavaPlugin plugin) {
		if (!isRegistered(plugin)) {
			plugin.getServer().getPluginManager().registerEvents(INSTANCE, plugin);
			this.plugin = plugin;
		}
	}

	/**
	 * Checks if the {@link ninja.amp.ampmenus.MenuListener} is registered to a
	 * plugin.
	 *
	 * @param plugin
	 *            The plugin.
	 * @return True if the {@link ninja.amp.ampmenus.MenuListener} is registered to
	 *         the plugin, else false.
	 */
	public boolean isRegistered(JavaPlugin plugin) {
		if (plugin.equals(this.plugin)) {
			for (RegisteredListener listener : HandlerList.getRegisteredListeners(plugin)) {
				if (listener.getListener().equals(INSTANCE)) {
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().equals(plugin)) {
			closeOpenMenus();
			plugin = null;
		}
	}

	/**
	 * Closes all {@link ninja.amp.ampmenus.menus.ItemMenu}s currently open.
	 */
	public static void closeOpenMenus() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory() != null) {
				Inventory inventory = player.getOpenInventory().getTopInventory();
				if (inventory.getHolder() instanceof ShopMenuHolder) {
					player.closeInventory();
				}
			}
		}
	}
}