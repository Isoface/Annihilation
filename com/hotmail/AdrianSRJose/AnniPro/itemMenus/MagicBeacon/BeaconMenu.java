package com.hotmail.AdrianSRJose.AnniPro.itemMenus.MagicBeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.BeaconEffect;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.ImprovableBeaconEffect;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.TeamBeacon;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenuHolder;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenuListener;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.GameBeaconLoader;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

/**
 * @author AdrianSR
 */
public class BeaconMenu extends ItemMenu {
	private final TeamBeacon beacon;
	private final Map<BeaconEffect, Map<Integer, MenuItem>> effectItems = new HashMap<BeaconEffect, Map<Integer, MenuItem>>();
	private final Map<BeaconEffect, Integer>                 realLevels = new HashMap<BeaconEffect, Integer>();
	private final List<BeaconEffect>                   purchasedEffects = new ArrayList<BeaconEffect>();

	public BeaconMenu(final AnniTeam team) {
		super((team.getExternalColoredName() + " Beacon"), Size.ONE_LINE);
		beacon = team.getBeacon();

		// Check is not null beacon
		if (beacon != null) {
			// Start beacon
			beacon.start();

			// Set items
			build();
		}
	}

	public void build() {
		// Clear Items
		clearAllItems();

		int slot = 0;

		// Set Items again
		for (BeaconEffect eff : new ArrayList<BeaconEffect>(GameBeaconLoader.EFFECTS)) {
			// Check is not null
			if (eff == null) {
				continue;
			}

			// Check map
			Map<Integer, MenuItem> put = null;
			if (effectItems.get(eff) == null) {
				effectItems.put(eff, new HashMap<Integer, MenuItem>());
			}

			// Get map
			put = effectItems.get(eff);

			// Save Items in map
			if (eff instanceof ImprovableBeaconEffect) {
				// Get Item Vals from the beacon effect
				List<String> increaseLore = eff.coloredIncreaseLore();
				List<String> buyLore      = eff.coloredBuyLore();
				List<String> lore         = null;
				String itemName           = eff.getItemName();
				
				// Cast
				ImprovableBeaconEffect ibf = (ImprovableBeaconEffect) eff;
				
				// Get Lore
				lore = (realLevels.containsKey(eff) && realLevels.get(eff) != null) ? increaseLore : buyLore;
				lore = Util.cloneList(lore);
				if (lore != null && !lore.isEmpty()) {
					for (int x = 0; x < lore.size(); x++) {
						String line = lore.get(x);
						if (line == null) {
							continue;
						}
						
						// Get Level
						Integer level = Integer.valueOf(0);
						if (realLevels.get(eff) != null) {
							level = realLevels.get(eff);
						}
						
						// Get cost and multiplier
						boolean maxLevel = (level.intValue() + 1) > ibf.getMaxLevel();
						Integer cost     = ibf.getCost();
						int multiplier   = level != null ? (level.intValue() < 1 ? 1 : level.intValue() + 1) : 1;
						cost = cost.intValue() * multiplier;
						
						// Check is not max level
						if (maxLevel) {
							lore = ibf.getMaxLevelLore() != null ? ibf.getMaxLevelLore() : new ArrayList<String>();
							break;
						}
						
						// Vars
						line = line.replace("%level", String.valueOf(multiplier));
						line = line.replace("%#",     String.valueOf(cost != null ? cost.intValue() : ibf.getCost()));
						lore.set(x, line
								.replace("%level", String.valueOf(multiplier))
								.replace("%#",     String.valueOf(cost != null ? cost.intValue() : ibf.getCost())));
					}
				}
				
				// Set
				for (int x = ibf.getLevel(); x <= ibf.getMaxLevel(); x++) {
					// Getting level
					Integer level = Integer.valueOf(x == ibf.getLevel() ? Math.max((ibf.getLevel() - 1), 0) : Math.max((x - 1), 0));
					
					// Put Integer.valueOf(x) or level
					put.put(Integer.valueOf(x), new ActionMenuItem(
							(Util.wc(itemName)), new ItemClickHandler() {
								@Override
								public void onItemClick(ItemClickEvent event) {
									Integer cost = ibf.getCost();
									final Integer realLevel = realLevels.get(eff);
									final int multiplier = realLevel != null ? (realLevel.intValue() < 1 ? 1 : realLevel.intValue() + 1) : 1;
									cost = cost.intValue() * multiplier;

									// Check max level
									if (realLevel != null) {
										if ((realLevel.intValue() + 1) > ibf.getMaxLevel()) {
											event.getPlayer().sendMessage(GameBeaconLoader.maxLevelMesage);
											return;
										}
									}

									if (checkPrice(event.getPlayer(), cost, ibf.getCostMaterial())) {
										// Add effect
										beacon.addEffect(ibf, level.intValue());
										final int newLevel = ibf.getCurrentLevel() + 1;
										ibf.setCurrentLevel(newLevel > ibf.getMaxLevel() ? ibf.getMaxLevel() : newLevel);
										
										// Save Current Level
										Integer newLevelToPut = Integer.valueOf(1 + (realLevel != null ? realLevel.intValue() : 0));
										realLevels.put(eff, newLevelToPut);

										// Build
										build();
										event.setWillUpdate(true);
									}
								}
							}, new ItemStack(ibf.getIcon(), 1), lore != null ? lore : new ArrayList<String>()));
				}
			} else if (eff instanceof BeaconEffect) {
				// Get Item Vals from the beacon effect
				List<String> increaseLore = eff.coloredIncreaseLore();
				List<String> buyLore      = eff.coloredBuyLore();
				List<String> lore         = null;
				String itemName           = eff.getItemName();
				
				// Check lore to use
				lore = purchasedEffects.contains(eff) ? increaseLore : buyLore;
				
				// Get Lore
				if (lore != null && !lore.isEmpty()) {
					for (int x = 0; x < lore.size(); x++) {
						String line = lore.get(x);
						if (line == null) {
							continue;
						}
						
						// Vars
						lore.set(x, line.replace("%#", String.valueOf(eff.getCost())));
					}
				}
				
				// Put
				put.put(Integer.valueOf(eff.getLevel()),
						new ActionMenuItem(Util.wc(itemName), new ItemClickHandler() {
							@Override
							public void onItemClick(ItemClickEvent event) {
								if (checkPrice(event.getPlayer(), eff.getCost(), eff.getCostMaterial())) {
									// Add effect
									beacon.addEffect(eff, (Math.max(eff.getLevel() - 1, 0)));
									
									// Add purchased effect
									purchasedEffects.add(eff);
								}
							}
						}, new ItemStack(eff.getIcon(), 1), lore != null ? lore : new ArrayList<String>()));
			}
		}

		// Set Items
		for (BeaconEffect eff : effectItems.keySet()) {
			// Getting and checking map
			Map<Integer, MenuItem> itms = effectItems.get(eff);
			if (itms == null) {
				continue;
			}

			// Getting Item
			MenuItem item = null;
			if (eff instanceof ImprovableBeaconEffect) {
				ImprovableBeaconEffect ibf = (ImprovableBeaconEffect) eff;
				item = itms.get(Integer.valueOf(ibf.getCurrentLevel()));
			} else if (eff instanceof BeaconEffect) {
				item = itms.get(Integer.valueOf(eff.getLevel()));
			}

			// Checking that the item is not null
			if (item == null) {
				continue;
			}

			// Setting in the menu
			if (slot < 5) {
				setItem(slot, item);
				slot++;
			}
		}
	}

	private boolean checkPrice(final Player player, final int cost, final Material costMaterial) {
		final PlayerInventory p = player.getInventory();
		// Check contains at least
		if (p.containsAtLeast(new ItemStack(costMaterial), cost)) {
			// Get Total ingots in inventory
			int total = 0;
			for (ItemStack s : p.all(costMaterial).values()) {
				total += s.getAmount();
			}

			// Remove
			p.remove(costMaterial);

			// Add rest ingots
			if (total - cost > 0) {
				p.addItem(new ItemStack(costMaterial, (total - cost)));
			}

			// Send Purchased potion message
			player.sendMessage(Lang.BEACON_PURCHASED_POTION.toString());
			return true;
		} else {
			// Send Could not Purchased potion message
			player.sendMessage(Lang.BEACON_COULD_NOT_PURCHASE.toString());
			return false;
		}
	}

	@Override
	public void open(Player player) {
		// Reset items
		build();

		// Check Player
		if (KitUtils.isValidPlayer(player)) {
			if (!ItemMenuListener.getInstance().isRegistered(AnnihilationMain.INSTANCE)) {
				ItemMenuListener.getInstance().register(AnnihilationMain.INSTANCE);
			}

			// Check name
			if (name == null) {
				name = "";
			}

			// Create Inventory
			Inventory inventory = Bukkit.createInventory(
					new ItemMenuHolder(this, Bukkit.createInventory(player, InventoryType.HOPPER)),
					InventoryType.HOPPER, Util.shortenString(name, 32));

			// Apply Items
			apply(inventory, player);

			// Open Inventory
			player.openInventory(inventory);
		}
	}

	@Override
	protected void apply(Inventory inventory, Player player) {
		for (int i = 0; i < items.length && i < inventory.getMaxStackSize(); i++) {
			if (items[i] != null) {
				inventory.setItem(i, items[i].getFinalIcon(player));
			}
		}
	}
}