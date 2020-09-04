package com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameStartEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.GameBeaconLoader;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class GameBeacon implements Listener {
	private static Loc mapBeaconLoc = null;

	public GameBeacon(AnnihilationMain main) {
		// Register Events
		Bukkit.getPluginManager().registerEvents(this, main);

		// Start Particles task
		new BukkitRunnable() {
			@Override
			public void run() {
				// Check is game running
				if (Game.isNotRunning()) {
					return;
				}

				// Check Game map
				final GameMap map = Game.getGameMap();
				if (map == null) {
					return;
				}

				// Get Beacon Loc
				Loc finalLoc = null;
				if (map.getBeaconLoc() != null) {
					finalLoc = map.getBeaconLoc();
				}

				if (mapBeaconLoc != null && finalLoc == null) {
					finalLoc = mapBeaconLoc;
				}

				// Play Particles
				if (Util.isValidLoc(finalLoc) && finalLoc.toLocation().getBlock().getType() == Material.BEACON) {
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.ENCHANTED_HIT.displayNewerVersions().display(0.7F, 0.8F, 0.7F, 0.1F, 60,
								finalLoc.toLocation().clone().add(0.5D, 0.2D, 0.5D), 90000);
					} else {
						CompatibleParticles.ENCHANTED_HIT.displayOlderVersions().display(0.7F, 0.8F, 0.7F, 0.1F, 60,
								finalLoc.toLocation().clone().add(0.5D, 0.2D, 0.5D), 90000);
					}
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 10L, 10L);
	}

	// Check Block
	@EventHandler(priority = EventPriority.NORMAL)
	public void onS(GameStartEvent eve) {
		// Get Game Map
		final GameMap map = Game.getGameMap();

		// Check Game Map Beacon block
		if (Util.isValidLoc(map.getBeaconLoc())) {
			map.getBeaconLoc().toLocation().getBlock().setType(Material.BEACON);
		}

		// Check team beacon Blocks
		for (AnniTeam teams : AnniTeam.Teams) {
			if (teams.getBeacon() != null) {
				teams.getBeacon().checkBlock();
			}
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(priority = EventPriority.HIGH)
	public void onB(BlockBreakEvent eve) {
		// Check is game running
		if (Game.isNotRunning()) {
			return;
		}

		// Get and check Game Map
		final GameMap map = Game.getGameMap();
		if (map == null) {
			return;
		}

		// Get and check game beacon loc
		final Loc bealoc = map.getBeaconLoc();
		if (!Util.isValidLoc(bealoc)) {
			return;
		}

		// Check is breaking game beacon
		final Block b = eve.getBlock();
		final Location loc = b.getLocation();
		final Player p = eve.getPlayer();
		if (!bealoc.equals(loc)) {
			return;
		}

		// Cancell
		eve.setCancelled(true);

		// Check Player
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// check is capturable phase
		if (GameBeaconLoader.breakableFromPhase > map.getCurrentPhase()) {
			p.sendMessage(GameBeaconLoader.noCapturableMessage);
			return;
		}

		// check is not team dead
		if (ap.getTeam().isTeamDead()) {
			p.sendMessage(Lang.MAGIC_BEACON_CANNOT_CAPTURE.toString());
			return;
		}

		// send captured message
		for (AnniPlayer ot : AnniPlayer.getPlayers()) {
			if (ot == null || !ot.isOnline()) {
				continue;
			}

			// send
			ot.sendMessage(Lang.MAGIC_BEACON_CAPTURED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}

		// set to air
		b.setType(Material.AIR);

		// save and remove map baecon loc
		mapBeaconLoc = map.getBeaconLoc();
		map.setBeaconLoc(null);

		// when is breaking game beacon
		if (!Util.isInventoryFull(p)) {
			p.getInventory().addItem(CustomItem.BEACON.toItemStack());
		} else {
			p.getWorld().dropItem(loc.clone().add(0.0D, 1.0D, 0.0D), CustomItem.BEACON.toItemStack());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPA(BlockPlaceEvent eve) {
		// Check is game running
		if (Game.isNotRunning()) {
			return;
		}

		// Get and check Game Map
		final GameMap map = Game.getGameMap();
		if (map == null) {
			return;
		}

		// Get and check game beacon loc
		final Loc bealoc = map.getBeaconLoc();
		if (!Util.isValidLoc(bealoc)) {
			return;
		}

		// Check is same world
		final Block b = eve.getBlock();
		if (!b.getWorld().getName().equals(bealoc.getBukkitWorld().getName())) {
			return;
		}

		// Check range
		if (b.getLocation().distance(bealoc.toLocation()) <= GameBeaconLoader.protectedRange) {
			eve.setCancelled(true);
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(priority = EventPriority.HIGH)
	public void onP(BlockPlaceEvent eve) {
		// Check is game running
		if (Game.isNotRunning()) {
			return;
		}

		// Get and check Game Map
		final GameMap map = Game.getGameMap();
		if (map == null) {
			return;
		}

		// Get values and check player
		final Block b = eve.getBlock();
		final Location loc = b.getLocation();
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// Check is beacon in hand
		if (!KitUtils.itemHasName(eve.getItemInHand(), CustomItem.BEACON.getName())) {
			return;
		}

		// Cancell
		eve.setCancelled(true);

		// Check is placing on his team beacon loc
		if (ap.getTeam().getBeacon() == null || ap.getTeam().getBeacon().getBeaconLocation() == null) {
			return;
		}

		if (!ap.getTeam().getBeacon().getBeaconLocation().equals(loc)) {
			p.sendMessage(Lang.MAGIC_BEACON_CANNOT_BE_PLACED.toString());
			return;
		}

		// Remove from inv
		p.getInventory().remove(CustomItem.BEACON.toItemStack());
		p.updateInventory();

		// set to beacon
		eve.setBuild(true);
		eve.setCancelled(false);
		ap.getTeam().getBeacon().getBeaconLocation().toLocation().getBlock().setType(Material.BEACON);
		ap.getTeam().getBeacon().getBeaconLocation().toLocation().getBlock().getState().update(true);
		ap.getTeam().getBeacon().setActivated(true);
		ap.getTeam().getBeacon().start();

		// sheduler
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				ap.getTeam().getBeacon().getBeaconLocation().toLocation().getBlock().setType(Material.BEACON);
			}
		}, 5L);

		// send placed message
		for (AnniPlayer ot : AnniPlayer.getPlayers()) {
			if (ot == null || !ot.isOnline()) {
				continue;
			}

			// send
			ot.sendMessage(Lang.MAGIC_BEACON_HAS_BEEN_PLACED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onD(PlayerQuitEvent eve) {
		// get and check player
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// check have the beacon in his inventory
		if (!p.getInventory().containsAtLeast(CustomItem.BEACON.toItemStack(), 1)) {
			return;
		}

		// remove
		p.getInventory().remove(CustomItem.BEACON.toItemStack());
		p.updateInventory();

		// drop Item
		p.getWorld().dropItem(p.getLocation(), CustomItem.BEACON.toItemStack());

		// send droped message
		for (AnniPlayer ot : AnniPlayer.getPlayers()) {
			if (ot == null || !ot.isOnline()) {
				continue;
			}

			// send
			ot.sendMessage(Lang.MAGIC_BEACON_DROPED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDD(PlayerDropItemEvent eve) {
		// get and check player
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// get and check item
		final Item item = eve.getItemDrop();
		if (item == null || !CustomItem.BEACON.toItemStack().equals(item.getItemStack())) {
			return;
		}

		// send droped message
		for (AnniPlayer ot : AnniPlayer.getPlayers()) {
			if (ot == null || !ot.isOnline()) {
				continue;
			}

			// send
			ot.sendMessage(Lang.MAGIC_BEACON_DROPED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}

		// checker task
		new livingTask(item.getUniqueId(), item.getWorld()).runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDD(final PlayerDeathEvent eve) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				onBeaconDrop(eve.getEntity(), eve.getDrops());
			}
		}, 10L);
	}
	
	private void onBeaconDrop(final Player p, final List<ItemStack> drops) {
		// get and check player
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// get and check item
		ItemStack stack = null;
		for (ItemStack st : drops) {
			if (!CustomItem.BEACON.toItemStack().equals(st)) {
				continue;
			}

			stack = st;
			break;
		}
		
		// check stack
		if (stack == null) {
			return;
		}
		
		// get entity
		Item item = null;
		for (Item ent : p.getWorld().getEntitiesByClass(Item.class)) {
			if (ent == null) {
				continue;
			}
			
			if (stack.equals(ent.getItemStack())) {
				item = ent;
				break;
			}
		}

		// check Item Entity
		if (item == null || !CustomItem.BEACON.toItemStack().equals(item.getItemStack())) {
			return;
		}

		// send droped message
		for (AnniPlayer ot : AnniPlayer.getPlayers()) {
			if (ot == null || !ot.isOnline()) {
				continue;
			}

			// send
			ot.sendMessage(Lang.MAGIC_BEACON_DROPED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}

		// checker Task
		new livingTask(item.getUniqueId(), item.getWorld()).runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);
	}

	private static class livingTask extends BukkitRunnable {
		private final UUID id;
		private final World w;

		livingTask(final UUID itemID, final World w) {
			this.id = itemID;
			this.w = w;
		}

		private static Item getItem(UUID id, World w) {
			if (id != null) {
				Entity ent = Util.getEntity(w, id);
				if (ent instanceof Item) {
					return (Item) ent;
				} else {
					return null;
				}
			}
			return null;
		}

		@Override
		public void run() {
			final Item item = getItem(id, w);
			if (item == null) {
				// Check is not in a player inventory
				for (Player p : w.getPlayers()) {
					if (!KitUtils.isValidPlayer(p)) {
						continue;
					}
					
					PlayerInventory inv = p.getInventory();
					if (inv.containsAtLeast(CustomItem.BEACON.toItemStack(), 1)) {
						// Cancell
						cancel();
						return;
					}
				}
				
				// Set
				Loc l = mapBeaconLoc;
				if (l == null) {
					l = Game.getGameMap().getBeaconLoc();
				}

				if (l != null) {
					if (l.toLocation().getBlock().getType() != Material.BEACON) {
						l.toLocation().getBlock().setType(Material.BEACON);
						Game.getGameMap().setBeaconLoc(l);
					}
				}

				// Cancell
				cancel();
				return;
			}
		}
	}

	// When Combust the droped beacon
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onC(final EntityCombustEvent eve) {
		// Get and check Game Map
		final GameMap map = Game.getGameMap();
		if (map == null) {
			return;
		}

		// Check Entity
		final Entity ent = eve.getEntity();
		if (!(ent instanceof Item)) {
			return;
		}

		final Item item = (Item) ent;
		if (item == null || !CustomItem.BEACON.toItemStack().equals(item.getItemStack())) {
			return;
		}

		// Set Block in
		if (map.getBeaconLoc() == null) {
			map.setBeaconLoc(mapBeaconLoc);
		}

		// Respawn Beacon
		map.getBeaconLoc().toLocation().getBlock().setType(Material.BEACON);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onP(PlayerPickupItemEvent eve) {
		// Get and check player
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// Get and check item
		final Item item = eve.getItem();
		if (item == null || !CustomItem.BEACON.toItemStack().equals(item.getItemStack())) {
			return;
		}

		// Send captured message
		for (AnniPlayer ot : AnniPlayer.getPlayers()) {
			if (ot == null || !ot.isOnline()) {
				continue;
			}

			// Send
			ot.sendMessage(Lang.MAGIC_BEACON_CAPTURED.toStringReplacement(ap.getTeam().getExternalColoredName()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSB(InventoryClickEvent event) {
		// get player and iventory and the item
		final HumanEntity entity = event.getWhoClicked();
		final Inventory inv = event.getView().getTopInventory();
		final ItemStack stack = event.getCurrentItem();

		// check is not a player inventory
		if (inv instanceof PlayerInventory) {
			return;
		}

		// check action
		if (event.getAction() == InventoryAction.HOTBAR_SWAP && event.getClick() == ClickType.NUMBER_KEY) {
			final ItemStack from  = ((Player) entity).getInventory().getItem(event.getSlot());
			final ItemStack moved = ((Player) entity).getInventory().getItem(event.getHotbarButton());
			if (CustomItem.BEACON.toItemStack().equals(from) || CustomItem.BEACON.toItemStack().equals(moved)) {
				if (inv.getType() != InventoryType.CRAFTING) {
					event.setCancelled(true);
				}
			}
		}

		// check item
		if (CustomItem.BEACON.toItemStack().equals(stack)) {
			if (inv.getType() != InventoryType.CRAFTING) {
				event.setCancelled(true);
			}
		}
	}
}
