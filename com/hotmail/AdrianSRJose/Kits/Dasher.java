package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.BlockData;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.RegeneratingBlocks;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.events.Dasher_PointToBlockEvent;
import com.hotmail.AdrianSRJose.events.Dasher_TeleportEvent;

public class Dasher extends ClassItemKit {
	
	private String SNEAKIN_MESSAGE = ChatColor.RED + "You must be sneaking to blink!";
	private boolean canBeUsedInAreas = true;
	private static final Map<UUID, dashBlock> last = new HashMap<UUID, dashBlock>();
	private static final Map<UUID, Location> POITING = new HashMap<UUID, Location>();
	
	private static final Material DASHER_BLOCK = UniversalMaterial.EMERALD_BLOCK.getMaterial ( );

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "MustBeSneakingMessage", SNEAKIN_MESSAGE)
				+ Util.setDefaultIfNotSet(section, "CanBeUsedInAreas", true);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		SNEAKIN_MESSAGE  = section.getString("MustBeSneakingMessage", SNEAKIN_MESSAGE);
		canBeUsedInAreas = section.getBoolean("CanBeUsedInAreas", canBeUsedInAreas);
	}

	@Override
	protected void onInitialize() {
		// Dashers target block updater
		new BukkitRunnable() {
			@Override
			public void run() {
				for (AnniPlayer ap : AnniPlayer.getPlayers()) {
					// check player
					if (!KitUtils.isValidPlayer(ap) || !hasThisKit(ap)) {
						continue;
					}

					Player p = ap.getPlayer();
					if (!KitUtils.isValidPlayer(p)) {
						continue;
					}
					
					// check is sneaking
					if (!p.isSneaking()) {
						continue;
					}
					
					// Check has special item in hand
					if (!isSpecialItem(p.getItemInHand())) {
						continue;
					}

					if (delays.hasActiveDelay(p, getInternalName()) && isSpecialItem(p.getItemInHand())) {
						continue;
					}

					// Get Target Block
					final Block target = p.getTargetBlock((Set<org.bukkit.Material>) null, 30);
					if (target == null) {
						continue;
					}

					// get Original Block material;
					final Material  original = target.getType();
					final Location targetLoc = target.getLocation();
					final byte          data = target.getData();
					
					// check area
					if (!canBeUsedInAreas) {
						if (Game.getGameMap() != null) {
							if (Game.getGameMap().getAreas().getArea(new Loc(targetLoc, false)) != null) {
								return;
							}
						}
					}

					// Check is not pointing to a nexus
					for (AnniTeam team : AnniTeam.Teams) {
						if (team.getNexus() != null) {
							if (Util.isValidLoc(team.getNexus().getLocation())) {
								if (team.getNexus().getLocation().equals(targetLoc)) {
									return;
								}
							}
						}
					}
					
					if (isSolid(target) && !isSolid(target.getRelative(BlockFace.UP))
							&& target.getLocation().distance(p.getLocation()) > 4
							// check is not a portal block
							&& target.getType() != UniversalMaterial.OBSIDIAN.getMaterial ( ) 
							&& target.getType() != UniversalMaterial.END_PORTAL_FRAME.getMaterial ( )
							&& target.getType() != UniversalMaterial.CHEST.getMaterial ( )
							&& target.getType() != UniversalMaterial.ENDER_CHEST.getMaterial ( )
							&& !target.getType().name().endsWith("_ORE")
							&& !RegeneratingBlocks.isRegeneratingBlock(target)
							&& target.getType() != UniversalMaterial.ENCHANTING_TABLE.getMaterial ( )
							&& target.getType() != UniversalMaterial.CRAFTING_TABLE.getMaterial ( )
							&& target.getType() != UniversalMaterial.FURNACE.getMaterial ( )
							&& !target.getType().name().contains("FURNACE")
							&& target.getType() != UniversalMaterial.BREWING_STAND.getMaterial ( )
							&& !target.getType().name().equals("ARMOR_STAND")
							&& target.getType() != UniversalMaterial.JUKEBOX.getMaterial ( )
							&& target.getType() != UniversalMaterial.DROPPER.getMaterial ( )
							&& target.getType() != UniversalMaterial.DISPENSER.getMaterial ( )
							&& target.getType() != UniversalMaterial.CAULDRON.getMaterial ( )
							&& target.getType() != UniversalMaterial.WHEAT.getMaterial ( )
							&& !target.getType().name().contains("SEEDS")
							&& target.getType() != UniversalMaterial.TORCH.getMaterial ( )
							&& !target.getType().name().equals("CROPS")) {
						
						Dasher_PointToBlockEvent beve = new Dasher_PointToBlockEvent(ap, target);
						AnniEvent.callEvent(beve);
						if (!beve.playEffect()) {
							return;
						}

						// regenerating block
						RegeneratingBlocks.addBlockToData("dasherblockkey",
								new BlockData(targetLoc).setCanBeBreak(false));

						// Restore old
						final dashBlock old = last.get(p.getUniqueId());
						if (old != null && old.blockLoc != null && old.original != null) {
							old.restore();
						}

						// Start restore Task
						if (original != null && original != null) {
							Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE,
									new emeralBlockTask(p.getUniqueId(), original, targetLoc, data), 8);
						}
					}
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 2L, 2L);
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.ENDER_PEARL.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	private class emeralBlockTask implements Runnable {
		private final Location blockLoc;
		private final Material original;
		private final byte     data;
		private final UUID     owner;

		public emeralBlockTask(final UUID owner, final Material original, final Location blockLoc, final byte data) {
			this.blockLoc = blockLoc;
			this.original = original;
			this.owner    = owner;
			this.data     = data;
			blockLoc.getBlock().setType(DASHER_BLOCK);
//			blockLoc.getBlock().getState().update(true, true);
			blockLoc.getBlock().setMetadata("dasherblockkey",
					new FixedMetadataValue(AnnihilationMain.INSTANCE, "dasherblockkey"));
			
			// save current player poiting block
			POITING.put(owner, blockLoc);
		}

		@Override
		public void run() {
			if (blockLoc == null) {
				return;
			}

			Block b = blockLoc.getBlock();
			if (b == null || b.getType() != DASHER_BLOCK) {
				return;
			}

			// remove metadata
			Shedulers.scheduleSync(new Runnable() {
				@Override
				public void run() {
					b.removeMetadata("dasherblockkey", AnnihilationMain.INSTANCE);
				}
			}, 20 * 2);
			
			// change type
			b.setType(original);
//			b.setData(data);
			CompatibleUtils.setData(b, data);
			// b.getState().update(true, true);

			// make save as last dash block
			final dashBlock db = new dashBlock(original, blockLoc, b.getData());
			last.put(owner, db);
		}
	}

	private static class dashBlock {
		private final Location blockLoc;
		private final Material original;
		private final byte data;

		public dashBlock(final Material original, final Location blockLoc, final byte data) {
			this.blockLoc = blockLoc;
			this.original = original;
			this.data = data;
		}

		public void restore() {
			if (original != null && original != null) {
				blockLoc.getBlock().setType(original);
//				blockLoc.getBlock().setData(data);
				CompatibleUtils.setData(blockLoc.getBlock(), data);
			}
			// blockLoc.getBlock().getState().update(true, true);
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onD(PlayerKilledEvent eve) {
		final AnniPlayer ap = eve.getPlayer();
		final AnniPlayer kp = eve.getKiller();
		if (ap != null && kp != null && ap.isOnline() && kp.isOnline()) {
			if (kp.getTeam() != null && ap.getTeam() != null && !ap.getTeam().equals(kp.getTeam())
					&& this.hasThisKit(kp)) {
				final Player k = kp.getPlayer();
				if (k != null) {
					k.setFoodLevel((k.getFoodLevel() + 3));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(final BlockBreakEvent eve) {
		final Block b = eve.getBlock();
		if (b.hasMetadata("dasherblockkey")) {
			eve.setCancelled(true);
		}
		else {
			final Location loc = b.getLocation();
			for (UUID id : POITING.keySet()) {
				// check is not the same player
				if (eve.getPlayer().getUniqueId().equals(id)) {
					continue;
				}
				
				Location blc = POITING.get(id);
				if (blc == null) {
					continue;
				}
				
				if (loc.equals(blc)) {
					AnniPlayer ap = AnniPlayer.getPlayer(id);
					if (ap != null && KitUtils.isValidPlayer(ap)) {
						if (hasThisKit(ap) && isSpecialItem(ap.getPlayer().getItemInHand())) {
							if (blc.getBlock().getType() == DASHER_BLOCK) {
								eve.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.AQUA + "Blink";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName())
					&& KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return false;
	}

	public boolean isSolid ( Block b ) {
		if ( b.getType ( ) == UniversalMaterial.AIR.getMaterial ( ) || b.isLiquid ( ) ) {
			return false;
		}
//		if (b.getType() == UniversalMaterial.AIR.getMaterial ( )
//				|| b.getType() == UniversalMaterial.WATER 
//				|| b.getType() == UniversalMaterial.STATIONARY_WATER)
//			return false;
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		// check player
		if (!KitUtils.isValidPlayer(p) || ap == null) {
			return false;
		}

		// set item in hand
		p.setItemInHand(getSpecialItem());
		p.updateInventory();

		// get and check target block
		final Block target = p.getTargetBlock((Set<org.bukkit.Material>) null, 30);
		if (target == null) {
			return false;
		}

		// check is sneaking
		if (!p.isSneaking()) {
			p.sendMessage(SNEAKIN_MESSAGE);
			return false;
		}

		// check is not pointing to a nexus
		for (AnniTeam t : AnniTeam.Teams) {
			if (t.getNexus() == null || !Util.isValidLoc(t.getNexus().getLocation())) {
				continue;
			}

			// check
			if (t.getNexus().getLocation().equals(target.getLocation())) {
				return false;
			}
		}
		
		// check area
		if (!canBeUsedInAreas) {
			if (Game.getGameMap() != null) {
				if (Game.getGameMap().getAreas().getArea(new Loc(target.getLocation(), false)) != null) {
					return false;
				}
			}
		}

		// check distance
		if (!(target.getLocation().distance(p.getLocation()) > 4)) {
			return false;
		}

		// check blocks
		if (!isSolid(target) || isSolid(target.getRelative(BlockFace.UP))) {
			return false;
		}

		// get location and food to quit
		final Location ploc = p.getLocation();
		final int i = (int) p.getLocation().distance(target.getLocation());
		int r = 0;
		if (i >= 5 && i < 10) {
			r = 1;
		} else if (i >= 10 && i < 15) {
			r = 2;
		} else if (i >= 15 && i < 20) {
			r = 3;
		} else if (i >= 20 && i < 25) {
			r = 4;
		} else if (i >= 25) {
			r = 5;
		}

		final Location to = target.getLocation().clone().add(0.0D, 1.0D, 0.0D);
		final Dasher_TeleportEvent eve = new Dasher_TeleportEvent(ap, ploc, to, r);
		eve.addPotion(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 1));
		eve.addPotion(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 1));
		AnniEvent.callEvent(eve);

		// check is not cancelled
		if (eve.isCancelled()) {
			return false;
		}

		// Play Path Effect
		if (eve.playPathEffect()) {
			final Vector vector = p.getLocation().getDirection();
			lin(vector, p, target.getLocation());
		}

		// Teleport
		p.teleport(eve.getTo());

		// add Potions
		if (eve.addPotionsOnTeleport() && !eve.getPotions().isEmpty()) {
			for (PotionEffect effect : eve.getPotions()) {
				if (effect != null) {
					p.addPotionEffect(effect);
				}
			}
		}

		// change Food Level
		p.setFoodLevel(Math.max(p.getFoodLevel() - eve.getFoodLost(), 0));

		// Play a Sound
		p.getWorld().playSound(p.getLocation(),
				ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) 
				? (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1) 
						? Sound.valueOf("ENTITY_ENDERMEN_TELEPORT") : Sound.valueOf ( "ENTITY_ENDERMAN_TELEPORT" ) ) 
						: Sound.valueOf("ENDERMAN_TELEPORT"),
				1F, (float) Math.random());

		// Play Circle Effect
		if (eve.playCircleEffect()) {
			for (Location l2 : Util.getCircle(p.getLocation().clone().add(0.0D, 2.0D, 0.0D), 1.0, 15)) {
				if (l2 == null) {
					continue;
				}
				
				for (double x = 0.0D; x <= 2.5; x += 0.5) {
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						if (x == 0.0D) {
							CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.0F, 1, l2, 100.0D);
						} else {
							CompatibleParticles.FIREWORK.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.0F, 1,
									l2.clone().add(0.0D, -(x), 0.0D), 100.0D);
						}
					} else {
						if (x == 0.0D) {
							CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.0F, 1, l2, 100.0D);
						} else {
							CompatibleParticles.FIREWORK.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.0F, 1,
									l2.clone().add(0.0D, -(x), 0.0D), 100.0D);
						}
					}
				}
			}
		}
		return true;
	}

	private void lin(final Vector vect, final Player player, final Location locl) {
		double t = 0;
		if (KitUtils.isValidPlayer(player) && vect != null && Util.isValidLoc(locl)) {
			final Location loc = player.getLocation().clone();
			final Vector direction = vect.normalize();
			if (loc != null) {
				while (t < player.getLocation().distance(locl)) {
					t = t + 0.5;
					double x = direction.getX() * t;
					double y = direction.getY() * t + 1.25;
					double z = direction.getZ() * t;
					loc.add(x, y, z);
					
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.CLOUD.displayNewerVersions().display(0.2F, 0.2F, 0.2F, 0.0F, 4, loc, 10000.0D);
					} else {
						CompatibleParticles.CLOUD.displayOlderVersions().display(0.2F, 0.2F, 0.2F, 0.0F, 4, loc, 10000.0D);
					}
					
					loc.subtract(x, y, z);
				}
			}
		}
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 6000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Dasher";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.ENDER_PEARL.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the confusion.", aqua + "Blink away to far distances",
				aqua + "or evade your enemies,", "", aqua + "or blink behind them to", aqua + "get the jump on them!",
				aqua + "Teleporting takes a", aqua + "lot of energy,", "", aqua + "you are briefly weakened when",
				aqua + "you blink, additionally,", "", aqua + "you lose hunger depending",
				aqua + "on distance traveled.", "", aqua + "Slaying an enemy grants a",
				aqua + "small amount of hunger back to you!");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(getSpecialItem())
				.addItem(KitUtils.addSoulbound ( new ItemStack ( UniversalMaterial.BREAD.getMaterial ( ) , 6 ) ) );
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player p) {
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
