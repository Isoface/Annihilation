package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.EnderChestOpenEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class KeyRing extends ClassItemKit {
	private static final Map<UUID, PED> CHESTS = new HashMap<UUID, PED>();
	private int            MAX_PLAYERS_TO_OPEN = 3;
	private int                   QUIT_SECONDS = 5;
	private String                FULL_MESSAGE = "&cThis ender chest can be opened by only 3 players!";
//	private static Material        ENDER_CHEST = Material.ENDER_CHEST;
//	private static Material                AIR = Material.AIR;

	private static class PED {
		private final Location  location;
		private final List<UUID> viewers = new ArrayList<UUID>();
		private final AnniTeam     team;

		PED(final Location loc, final AnniTeam team) {
			location  = loc;
			this.team = team;
		}

		void addViewer(final Player p) {
			viewers.add(p.getUniqueId());
		}

		void remViewer(final Player p) {
			viewers.remove(p.getUniqueId());
		}
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "Max-Player-Can-Open", 3)
				+ Util.setDefaultIfNotSet(section, "Chest-Quit-Seconds", 3)
				+ Util.setDefaultIfNotSet(section, "Full-Message", FULL_MESSAGE);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			MAX_PLAYERS_TO_OPEN = section.getInt("Max-Player-Can-Open", 3);
			QUIT_SECONDS        = section.getInt("Chest-Quit-Seconds", 3);
			FULL_MESSAGE        = Util.wc(section.getString("Full-Message", FULL_MESSAGE));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInt(final PlayerInteractEvent eve) {
		// Check Values
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final Block chest   = eve.getClickedBlock();
		if (p == null || ap == null || chest == null) {
			return;
		}

		// Check Ender Chest
		if (chest.getType() != UniversalMaterial.ENDER_CHEST.getMaterial()) {
			return;
		}
		
		// Check is PED
		for (UUID id : new HashSet<UUID>(CHESTS.keySet())) {
			// Get and check PED
			PED ped = CHESTS.get(id);
			if (ped == null) {
				continue;
			}
			
			// Check is not same Team
			if (ap.getTeam().equals(ped.team)) {
				continue;
			}
			
			if (chest.getLocation().equals(ped.location)) {
				// Cancell block break event
				eve.setCancelled(true);
				
				// Remove PED
				remPED(ped, id);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(final BlockBreakEvent eve) {
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final Block chest   = eve.getBlock();
		// Check Player
		if (p == null || ap == null || !ap.hasTeam()) {
			return;
		}

		// Check Ender Chest
		if (chest.getType() != UniversalMaterial.ENDER_CHEST.getMaterial()) {
			return;
		}

		// Check is PED
		for (UUID id : new HashSet<UUID>(CHESTS.keySet())) {
			// Get and check PED
			PED ped = CHESTS.get(id);
			if (ped == null) {
				continue;
			}
			
			// Cancell block break event
			eve.setCancelled(true);
			
			// Check is not same Team
			if (ap.getTeam().equals(ped.team)) {
				continue;
			}
			
			// Remove PED
			if (chest.getLocation().equals(ped.location)) {
				// Remove PED
				remPED(ped, id);
				break;
			}
		}
	}

	private void remPED(final PED ped, final UUID ownerID) {
		// Get and check PED
		if (ped == null || ped.location == null) {
			return;
		}

		// Get Block
		final Block chest = ped.location.getBlock();

		// When is a ped
		if (chest.getType() == UniversalMaterial.ENDER_CHEST.getMaterial()) {
			// Set to AIR and update state
			chest.setType(UniversalMaterial.AIR.getMaterial());
			chest.getState().update(true, false);
		}

		// Close Viewers
		for (UUID other : ped.viewers) {
			Player vw = Bukkit.getPlayer(other);
			AnniPlayer vp = AnniPlayer.getPlayer(vw);
			if (vw == null || vp == null) {
				return;
			}

			if (vw.getOpenInventory() != null) {
				// Call new Close Event
				vw.getOpenInventory().close();
				vw.closeInventory();
			}
		}
		
		// Remove From Map
		CHESTS.remove(ownerID);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onOpen(final EnderChestOpenEvent eve) {
		// Get Player
		final AnniPlayer ap = eve.getPlayer();
		if (ap == null || !ap.isOnline()) {
			return;
		}

		// Get and check location
		final Location chestLoc = eve.getChestLocation();
		if (chestLoc == null) {
			return;
		}

		for (UUID id : CHESTS.keySet()) {
			// Check
			PED ped = CHESTS.get(id);
			if (ped == null) {
				continue;
			}

			// Check team
			if (!ap.getTeam().equals(ped.team)) {
				eve.setCancelled(true);
				continue;
			}

			// Get and Check Loc
			Location another = ped.location;
			if (another != null && chestLoc.equals(another)) {
				Block block = another.getBlock();
				if (block.getType() == UniversalMaterial.ENDER_CHEST.getMaterial()) {
					// Check viewers
					if (ped.viewers.size() >= MAX_PLAYERS_TO_OPEN) {
						// Cancell and send full message
						eve.setCancelled(true);
						ap.sendMessage(FULL_MESSAGE);
					} else {
						ped.addViewer(ap.getPlayer());
					}
				}
				break;
			}
		}
	}

	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		final ItemStack tor = new ItemStack(UniversalMaterial.ENDER_CHEST.getMaterial(), 1);
		return KitUtils.addClassSoulbound(KitUtils.setName(tor, getSpecialItemName() + instance.getReadyPrefix()));
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Portable Chest";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		return KitUtils.isClassSoulbound(stack) && KitUtils.itemNameContains(stack, getSpecialItemName());
	}

	@Override
	protected boolean performPrimaryAction(final Player player, final AnniPlayer p, final PlayerInteractEvent event) {
		// Get ad Check Block
		final Block block = event.getClickedBlock();
		if (block == null) {
			return false;
		}

		// Check is not click a Ender Chest
		if (block.getType() == UniversalMaterial.ENDER_CHEST.getMaterial()) {
			return false;
		}

		// Get and Check the new ChestBlock
		final Block chest = block.getRelative(event.getBlockFace());
		final Location block1 = block.getLocation();
		final Location block2 = chest.getLocation();
		if (chest.getType() == UniversalMaterial.ENDER_CHEST.getMaterial()) {
			return false;
		}

		// Check is not on an Area
		if (Game.getGameMap().getAreas().isInSomeArea(block1) || Game.getGameMap().getAreas().isInSomeArea(block2)) {
			return false;
		}

		// Set to Ender Chest
		chest.setType(UniversalMaterial.ENDER_CHEST.getMaterial());
		chest.getState().update(true, false);

		// Get PED and save Ped
		final PED ped = new PED(block2, p.getTeam());
		CHESTS.put(player.getUniqueId(), ped);

		// Close Task
		Shedulers.scheduleSync(new Runnable() {
			@Override
			public void run() {

				// Set to Air
				final Block chest = ped.location.getBlock();
				if (chest.getType() == UniversalMaterial.ENDER_CHEST.getMaterial()) {
					// Set to AIR and update state
					chest.setType(UniversalMaterial.AIR.getMaterial());
					chest.getState().update(true, false);

					// Remove Map
					CHESTS.remove(player.getUniqueId());
				}

				for (UUID id : new ArrayList<UUID>(ped.viewers)) {
					Player vw = Bukkit.getPlayer(id);
					AnniPlayer vp = AnniPlayer.getPlayer(vw);
					if (vw == null || vp == null) {
						continue;
					}

					if (vw.getOpenInventory() != null) {
						// Call new Close Event
						vw.getOpenInventory().close();
						vw.closeInventory();

						// Remove Viewer
						ped.remViewer(vw);
					}
				}
			}
		}, (QUIT_SECONDS * 20));
		
		// Add Potion Effect
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, (QUIT_SECONDS * 20)));
		return true;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 25 * 1000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
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

	@Override
	protected String getInternalName() {
		return "KeyRing";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.ENDER_CHEST.getMaterial(), 1);
	}

	@Override
	protected List<String> getDefaultDescription() {
		return Arrays.asList(new String[] {
				aqua + "You are the insurance", 
				aqua + "", 
				aqua + "This kit gives you the ability", 
				aqua + "to use the ender chest for 5 ",
				aqua + "seconds 5 segundos, After placing",
				aqua + "it be aware that you will", 
				aqua + "only have 4, open yours, and", 
				aqua + "other 3 companions.",
				aqua + "",
				aqua + "The ender chest will not be destroyed",
				aqua + "but care that has a disadvantage.",
				aqua + "", 
				aqua + "You will have constant weakness, ",
				aqua + "and you will get slow 3 for 5 seconds.",
				aqua + "", 
				aqua + "Couldown: 25 seconds",
				});
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodAxe().addWoodPick().addWoodShovel().addItem(getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, (Integer.MAX_VALUE)));
	}

	@Override
	public void cleanup(Player p) {
		final UUID id = p.getUniqueId();
		if (Util.isValidLoc(CHESTS.get(id) != null ? CHESTS.get(id).location : null)) {
			final Block chest = CHESTS.get(id).location.getBlock();
			if (chest.getType() == UniversalMaterial.ENDER_CHEST.getMaterial()) {

				// Set to AIR and update state
				chest.setType(UniversalMaterial.AIR.getMaterial());
				chest.getState().update(true, false);

				// Remove Map
				CHESTS.remove(id);
			}
		}
		
		// Remove potion Effects
		if (p.isOnline()) {
			p.removePotionEffect(PotionEffectType.WEAKNESS);
			p.removePotionEffect(PotionEffectType.SLOW);
		}
	}

	@Override
	public boolean onItemClick(Inventory paramInventory, AnniPlayer paramAnniPlayer) {
		paramAnniPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, (Integer.MAX_VALUE)));
		this.addLoadoutToInventory(paramInventory);
		return true;
	}
}
