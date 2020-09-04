package com.hotmail.AdrianSRJose.AnniPro.kits.Menu;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.KitChangeEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class KitMenuItem extends MenuItem {
	private final Kit kit;
	private final UUID ownerID;

	public KitMenuItem(final Kit kit, final Player owner) {
		super(Util.wc(kit.getColoredName()), kit.getIconPackage().getIcon(), kit.getIconPackage().getLore());
		this.kit = kit;
		this.ownerID = owner.getUniqueId();
	}

	public Kit getKit() {
		return kit;
	}

	@Override
	public String[] getLoreArray() {
		final String[] finalLore = KitsMenuConfig.FINAL_LORE.toStringArray();
		final String perm = kit.hasPermission(Bukkit.getPlayer(ownerID)) ? KitsMenuConfig.UNLOCKED_KIT.toString()
				: KitsMenuConfig.LOCKED_KIT.toString();
		final boolean usePermLore = perm != null && !perm.isEmpty();
		final String[] tor = new String[(getLore().size() + finalLore.length) + (usePermLore ? 1 : 0)];
		for (int x = 0; x < tor.length; x++) {
			if (x < getLore().size()) {
				tor[x] = getLore().get(x);
			}
		}

		for (int g = 0; g < finalLore.length; g++) {
			tor[(tor.length - finalLore.length - 1) + g] = finalLore[g];
		}

		if (usePermLore) {
			tor[tor.length - 1] = perm;
		}

		return tor;
	}

	@Override
	public void onItemClick(final ItemClickEvent event) {
		// Get Player
		final Player p = event.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (p == null || ap == null || kit == null) {
			return;
		}

		// Close Kits Menu
		event.setWillClose(true);

		// Chech Has Permission
		if (!kit.hasPermission(p)) {
			p.sendMessage(Lang.CANT_SELECT_KIT.toString());
			return;
		}

		// Check no equal Kit
		final Kit old = ap.getKit();
		final PlayerInventory inv = p.getInventory();
		if (kit.equals(old)) {
			p.sendMessage(Lang.ALREADY_USING_KIT.toStringReplacement(kit.getName()));
			return;
		}

		// Call a KitChangeEvent
		final KitChangeEvent eve = new KitChangeEvent(ap, old, kit);
		AnniEvent.callEvent(eve);
		if (eve.isCancelled()) {
			return;
		}

		// Cleanup Old Kit
		if (Game.isGameRunning() && old != null 
				&& ap.hasTeam() && KitUtils.isOnGameMap(p)) {
			old.cleanup(p);
		}

		// Set Kit
		ap.setKit(kit);

		if (Game.isGameRunning() && ap.hasTeam()) {
			// Without Kit Sub-Menus
			if (Config.KILL_PLAYERS_ON_CHANGE_KIT.toBoolean()) {
				p.setHealth(0.0D);
			} else { // With Kit Sub-Menus
				// If player is not on The Lobby
				if (KitUtils.isOnGameMap(p)) {
					// Remove Souldbounds
					for (int x = 0; x < inv.getSize(); x++) {
						ItemStack st = inv.getItem(x);
						if (KitUtils.isAnySoulbound(st) && !KitUtils.itemHasName(st, CustomItem.NAVCOMPASS.getName())) {
							inv.setItem(x, null);
							p.updateInventory();
						}
					}

					// Restore Null Armors
					final ItemStack[] armorContents = inv.getArmorContents();
					final ItemStack[] teamArmor = Loadout.getTeamArmor(p);
					if (teamArmor != null) {
						for (int x = 0; x < inv.getArmorContents().length; x++) {
							ItemStack geted = inv.getArmorContents()[x];
							if (geted == null || geted.getType() == Material.AIR) {
								armorContents[x] = teamArmor[x];
							}
						}

						// Set Armor Contents and Update inventory
						p.getInventory().setArmorContents(armorContents);
						p.updateInventory();
					}

					// Open Kit Sub Menu
					openSubKitMenu(p.getUniqueId(), kit, p.getLocation());
				}
			}
		}

		// Send Kit Selected Message
		p.sendMessage(Lang.KIT_SELECTED.toStringReplacement(kit.getName()));
	}

	private void openSubKitMenu(final UUID playerID, final Kit k, final Location from) {
		if (playerID == null || k == null) {
			return;
		}

		if (!KitUtils.isValidPlayer(Bukkit.getPlayer(playerID))) {
			return;
		}

		final Player e = Bukkit.getPlayer(playerID);
		if (!KitUtils.isOnGameMap(e)) {
			return;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				// Get And Check Player
				final Player p = Bukkit.getPlayer(playerID);
				final AnniPlayer ap = AnniPlayer.getPlayer(p);
				if (ap == null || !KitUtils.isValidPlayer(p)) {
					return;
				}
				
				// Check has Team
				if (!ap.hasTeam()) {
					return;
				}

				// Anti Death-Open-Menu
				final Location loc = p.getLocation();
				if (loc.distance(from) > 2) {
					return;
				}

				// Get Player Inventory
				final PlayerInventory pinv = p.getInventory();
				final boolean addCompass = !pinv.contains(CustomItem.NAVCOMPASS.toItemStack());

				// Create Inventory and Add Items
				final Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST,
						Lang.KITS_SUB_MENU_TITLE.toStringReplacement(k.getColoredName()));
				kit.onItemClick(inv, ap);

				// Add Compass
				if (addCompass) {
					inv.addItem(CustomItem.NAVCOMPASS.toItemStack());
				}

				// Open
				p.openInventory(inv);
				p.updateInventory();
			}
		}, 3);
	}
}
