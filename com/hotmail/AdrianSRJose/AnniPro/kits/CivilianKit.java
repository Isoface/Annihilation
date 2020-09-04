package com.hotmail.AdrianSRJose.AnniPro.kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerInteractAtSoulboundItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

public class CivilianKit extends Kit {
	
	public ItemStack workBrench() {
		ItemStack workBrench = new ItemStack(Material.CLAY_BRICK);
		ItemMeta meta = workBrench.getItemMeta();
		meta.setDisplayName(Lang.CIVILIAN_CRAFT_ITEM.toString());
		workBrench.setItemMeta(meta);
		//
		return KitUtils.addClassSoulbound(workBrench);
	}

	public CivilianKit() {
		Bukkit.getPluginManager().registerEvents(this, AnnihilationMain.INSTANCE);
		this.Initialize();
	}

	private Loadout loadout;

	@Override
	public boolean Initialize() {
		loadout = new Loadout().addWoodSword().addStonePick().addStoneAxe().addStoneShovel().addItem(workBrench())
				.addSoulboundItem(new ItemStack(Material.CHEST)).addNavCompass().finalizeLoadout();
		return true;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		if (ap != null && ap.isOnline()) {
			if (!KitUtils.itemHasName(event.getItem(), workBrench().getItemMeta().getDisplayName())) {
				return;
			}

			if (Game.isNotRunning() && !ap.getKit().equals(this) && !ap.hasTeam()) {
				return;
			}

			if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}

			PlayerInteractAtSoulboundItemEvent eve = new PlayerInteractAtSoulboundItemEvent(event);
			Bukkit.getPluginManager().callEvent(eve);
			if (!eve.isCancelled()) {
				// Open Work Brench
				p.openWorkbench(p.getLocation(), true);
			}
		}
	}

	@Override
	public String getDisplayName() {
		return Lang.CIVILIANNAME.toString();
	}

	@Override
	public IconPackage getIconPackage() {
		return new IconPackage(new ItemStack(Material.WORKBENCH), Lang.CIVILIANLORE.toStringArray());
	}

	@Override
	public void onPlayerSpawn(Player player) {
		loadout.giveLoadout(player);
	}

	@Override
	public void cleanup(Player player) {
	}

	@Override
	public boolean hasPermission(Player player) {
		return true;
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		inv.addItem(loadout.getFinalStacks());
		return true;
	}

	@Override
	public String getOfficialName() {
		return "Civilian";
	}
}
