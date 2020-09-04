package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.events.Alchemist_OpenAlchemistsTomeEvent;

public class Alchemist extends ClassItemKit {
	
	private static Material STRING = UniversalMaterial.STRING.getMaterial ( ); // Material.STRING;
	private static Material FERMENTED_SPIDER_EYE = UniversalMaterial.FERMENTED_SPIDER_EYE.getMaterial ( ); // Material.FERMENTED_SPIDER_EYE;
	private static String ownerNameMessage = "&cBrewing Stand by &6%w";
	public static String PRIVATE_BREWING_STAND_NAME = "&bAlchemist's Stand";
	public static String ALCHEMIST_TOME_INVENTORY_NAME = "&bAlchemist";
	
	//
	@Override
	protected String getInternalName() {
		return "Alchemist";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.ENCHANTED_BOOK.getMaterial ( ) );
	}
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "BrewingStandOwnerMessage", "&cBrewing Stand by &6%w")
				+ Util.setDefaultIfNotSet(section, "BrewingStandName", PRIVATE_BREWING_STAND_NAME)
				+ Util.setDefaultIfNotSet(section, "AlchemistTomeInventoryName", ALCHEMIST_TOME_INVENTORY_NAME);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			if (section.isString("BrewingStandOwnerMessage")) {
				ownerNameMessage = Util.wc(section.getString("BrewingStandOwnerMessage"));
			}
			
			PRIVATE_BREWING_STAND_NAME = Util.wc(section.getString("BrewingStandName", PRIVATE_BREWING_STAND_NAME));
			ALCHEMIST_TOME_INVENTORY_NAME = Util.wc(section.getString("AlchemistTomeInventoryName", ALCHEMIST_TOME_INVENTORY_NAME));
		}
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<>();

		addToList(l, aqua + "You are the brew.", "", aqua + "Set up your own personal brewing",
				aqua + "stand anywhere to brew in peace,", "", aqua + "but keep it away from enemies",
				aqua + "as they can break it.", "", aqua + "Your alchemist's tome will",
				aqua + "provide you with potion ingredients", aqua + "every 90 seconds.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(PrivateBrewingStand.getBrewingStand())
				.addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player pl) {
		PrivateBrewingStand.removeBrewing(pl);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent eve) {
		final Player p = eve.getPlayer();
		if (eve.isCancelled() || Game.isNotRunning() || !KitUtils.isOnGameMap(p)) {
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		final Location loc = eve.getBlock().getLocation();
		if (ap != null && ap.isOnline() && Util.isValidLoc(loc) && ap.getTeam() != null && hasThisKit(ap)) {
			if (KitUtils.itemHasName(eve.getItemInHand(),
					PrivateBrewingStand.getBrewingStand().getItemMeta().getDisplayName())) {
				if (Game.getGameMap().getAreas().getArea(new Loc(eve.getBlockPlaced().getLocation(), false)) != null) {
					eve.setCancelled(true);
					return;
				}
				//
				eve.getPlayer().setItemInHand(null);
				eve.getPlayer().updateInventory();
				PrivateBrewingStand.createPrivateBrewingStand(loc, ap);
				//
				new BukkitRunnable() {
					@Override
					public void run() {
						if (PrivateBrewingStand.isBrewingStand(loc)
								&& loc.getBlock().getType() == UniversalMaterial.BREWING_STAND.getMaterial ( ) ) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.WITCH.displayNewerVersions().display(0.2F, 0.0F, 0.2F, 0.0F,
										4, loc.clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							} else {
								CompatibleParticles.WITCH.displayOlderVersions().display(0.2F, 0.0F, 0.2F, 0.0F,
										4, loc.clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							}
						}
						else {
							cancel();
						}
					}
				}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onOpen(PlayerInteractEvent eve) {
		final Player p = eve.getPlayer();
		if (Game.isNotRunning() || !KitUtils.isOnGameMap(p)) {
			return;
		}

		if (eve.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		// Check
		final Location clicked = eve.getClickedBlock().getLocation();
		final PrivateBrewingStand br = PrivateBrewingStand.getBrewingStand(clicked);
		if (br == null) {
			return;
		}
		
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		if (p.getUniqueId().equals(br.getOwnerID())) {
			if (hasThisKit(ap)) {
				return;
			}
			else {
				// Cancell
				eve.setCancelled(true);
			}
		}
		else {
			// Cancell
			eve.setCancelled(true);
			
			// Get Player
			final AnniPlayer APowner = br.getOwner();
			if (APowner != null && APowner.isOnline()) {
				// Owner
				final Player owner = APowner.getPlayer();
				if (APowner.getTeam().equals(ap.getTeam())) {
					p.sendMessage("" + ownerNameMessage.replace("%w",
							(owner.getName() != null ? owner.getName() : "Unknown")));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent eve) {
		final Player p = eve.getPlayer();
		if (Game.isNotRunning() || !KitUtils.isOnGameMap(p)) {
			return;
		}

		// Get anni player and check
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		final Location bk = eve.getBlock().getLocation();
		if (ap != null && bk != null && PrivateBrewingStand.isBrewingStand(bk)) {
			final PrivateBrewingStand br = PrivateBrewingStand.getBrewingStand(bk);
			if (br == null) {
				return;
			}

			// Cancell
			eve.setCancelled(true);

			final Player owner = Bukkit.getPlayer(br.getOwnerID());
			final AnniPlayer aPowner = AnniPlayer.getPlayer(br.getOwnerID());
			if (KitUtils.isValidPlayer(owner) && aPowner != null) {
				if (ap.getTeam().equals(aPowner.getTeam())) {
					if (p.getUniqueId().equals(br.getOwnerID())) {
						PrivateBrewingStand.removeBrewing(owner);
						owner.getInventory().addItem(PrivateBrewingStand.getBrewingStand());
						owner.updateInventory();
					}
				} else {
					PrivateBrewingStand.removeBrewing(owner);
					owner.getInventory().addItem(PrivateBrewingStand.getBrewingStand());
					owner.updateInventory();
				}
			}
		}
	}

	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = KitUtils.addClassUndropabbleSoulbound ( new ItemStack ( UniversalMaterial.BOOK.getMaterial ( ) ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.AQUA + "Alchemist's Tome";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		}
		//
		return false;
	}

	public boolean power(Player p, AnniPlayer ap) {
		if (p == null || ap == null)
			return false;
		//
		Inventory g = Bukkit.getServer().createInventory(null, 9, "");
		//
		Alchemist_OpenAlchemistsTomeEvent eve = new Alchemist_OpenAlchemistsTomeEvent(ap, g);
		eve.setAlchemistTomeInventoryName(eve.getAlchemistTomeInventoryName());
		AnniEvent.callEvent(eve);
		//
		if (eve.isCancelled())
			return false;

		Inventory inv = eve.getTomeInventory();
		//
		if (inv == null)
			return false;
		//
		Random r = new Random();
		int total = r.nextInt(105);

		// 32%
		if (total >= 0 && total <= 32) {
			Random a = new Random();
			int ai = a.nextInt(35);

			if (ai >= 0 && ai <= 8) {
				inv.setItem(3, new ItemStack(FERMENTED_SPIDER_EYE));
				inv.setItem(8, new ItemStack(STRING));
			}
			if (ai >= 9 && ai <= 17) {
				inv.setItem(7, new ItemStack(FERMENTED_SPIDER_EYE));
				inv.setItem(1, new ItemStack(UniversalMaterial.SNOWBALL.getMaterial()));
				inv.setItem(2, new ItemStack(UniversalMaterial.WHEAT_SEEDS.getMaterial()));
			}
			if (ai >= 18 && ai <= 26) {
				inv.setItem(6, new ItemStack(FERMENTED_SPIDER_EYE));
				inv.setItem(0, new ItemStack(STRING));
			}
			if (ai >= 27 && ai <= 35) {
				inv.setItem(0, new ItemStack(FERMENTED_SPIDER_EYE));
				inv.setItem(5, new ItemStack(UniversalMaterial.COOKIE.getMaterial()));
				inv.setItem(8, new ItemStack(UniversalMaterial.MELON_SEEDS.getMaterial()));
			}
			// Fermented Spider Eye
		}

		// 28%
		if (total >= 33 && total <= 61) {
			Random a = new Random();
			int ai = a.nextInt(31);

			// Glistering Melon
			if (ai >= 0 && ai <= 7) {
				inv.setItem(5, new ItemStack(UniversalMaterial.GLISTERING_MELON_SLICE.getMaterial()));
				inv.setItem(3, new ItemStack(UniversalMaterial.STRING.getMaterial()));
			}

			// Golden Carrot
			if (ai >= 8 && ai <= 15) {
				inv.setItem(0, new ItemStack(UniversalMaterial.GOLDEN_CARROT.getMaterial()));
				inv.setItem(5, new ItemStack(UniversalMaterial.WHEAT_SEEDS.getMaterial()));
			}

			// Sugar
			if (ai >= 16 && ai <= 23) {
				inv.setItem(8, new ItemStack(UniversalMaterial.SUGAR.getMaterial()));
				inv.setItem(3, new ItemStack(UniversalMaterial.STICK.getMaterial()));
				inv.setItem(7, new ItemStack(UniversalMaterial.SNOWBALL.getMaterial()));
			}

			// Spider Eye, Magma Cream
			if (ai >= 24 && ai <= 31) {
				inv.setItem(3, new ItemStack(UniversalMaterial.SPIDER_EYE.getMaterial()));
				inv.setItem(5, new ItemStack(UniversalMaterial.ROTTEN_FLESH.getMaterial()));
				inv.setItem(1, new ItemStack(UniversalMaterial.MAGMA_CREAM.getMaterial()));
			}
		}

		// 15%
		if (total >= 62 && total <= 77) {
			Random f = new Random();
			int ai = f.nextInt(16);

			// Glowstone Dust
			if (ai >= 0 && ai <= 8) {
				inv.setItem(5, new ItemStack(UniversalMaterial.GLOWSTONE_DUST.getMaterial()));
				inv.setItem(8, new ItemStack(UniversalMaterial.SNOWBALL.getMaterial()));
			}

			// Gunpowder
			if (ai >= 9 && ai <= 16) {
				inv.setItem(1, new ItemStack(UniversalMaterial.BLAZE_POWDER.getMaterial()));
				inv.setItem(6, new ItemStack(UniversalMaterial.STRING.getMaterial()));
				inv.setItem(2, new ItemStack(UniversalMaterial.COOKIE.getMaterial()));
			}
		}
		// 7%
		if (total >= 78 && total <= 85) {
			Random f = new Random();
			int ai = f.nextInt(8);

			if (ai >= 0 && ai <= 3) {
				inv.setItem(6, new ItemStack(UniversalMaterial.GHAST_TEAR.getMaterial()));
				inv.setItem(5, new ItemStack(UniversalMaterial.SNOWBALL.getMaterial()));
			}

			if (ai >= 4 && ai <= 8) {
				inv.setItem(3, new ItemStack(UniversalMaterial.GHAST_TEAR.getMaterial()));
				inv.setItem(8, new ItemStack(UniversalMaterial.COOKIE.getMaterial()));
				inv.setItem(6, new ItemStack(UniversalMaterial.STRING.getMaterial()));
			}
			// Ghast Tear
		}

		// 3% and is > that phase 4
		if (total >= 86 && total <= 89 && Game.getGameMap().getCurrentPhase() >= 4) {
			// Blaze Powder
			inv.setItem(5, new ItemStack(UniversalMaterial.BLAZE_POWDER.getMaterial()));
			inv.setItem(8, new ItemStack(UniversalMaterial.POTATO_ITEM.getMaterial()));
			inv.setItem(1, new ItemStack(UniversalMaterial.SNOWBALL.getMaterial()));
		}

		// 15%
		if (total >= 90 && total <= 105) {
			Random f = new Random();
			int ai = f.nextInt(16);

			// Rotten Flesh Poisonous Potato
			if (ai >= 0 && ai <= 8) {
				inv.setItem(1, new ItemStack(UniversalMaterial.ROTTEN_FLESH.getMaterial()));
				inv.setItem(5, new ItemStack(UniversalMaterial.POTATO_ITEM.getMaterial()));
			}

			// SNOW_BALL, String.
			if (ai >= 9 && ai <= 16) {
				inv.setItem(8, new ItemStack(UniversalMaterial.STRING.getMaterial()));
				inv.setItem(2, new ItemStack(UniversalMaterial.SNOWBALL.getMaterial()));
			}
		}
		//
		p.openInventory(inv);
		return true;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 90000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return power(player, p);
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
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
