package com.hotmail.AdrianSRJose.base;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

public abstract class ProlongedDelayKit extends ClassItemKit
{
	@Override
	@EventHandler(priority = EventPriority.HIGHEST)
	public void specialItemActionCheck(final PlayerInteractEvent event) {
		// check is using default checking, game is running, and is not on the lobby
		if (!useDefaultChecking() || Game.isNotRunning() || KitUtils.isOnLobby(event.getPlayer())) {
			return;
		}
		
		// get item and check
		final ItemStack item = event.getItem();
		if (item == null || specialItem == null || item.getType() != specialItem.getType() || !isSpecialItem(item)) {
			return;
		}
		
		// get player and anniplayer and check
		final Player p      = event.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (!KitUtils.isValidPlayer(ap) || !hasThisKit(ap)) {
			return;
		}
		
		// cancell interaction
		event.setCancelled(true);
		
		// check delay
		if (delays.hasActiveDelay(p, getInternalName())) {
			return;
		}
		
		// when is Primary Action
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// perform action
			performPrimaryAction(p, ap, event);
			
			// updeate inventory
			p.updateInventory();
			return;
		}

		// when is Secondary Action
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			// perform action
			performSecondaryAction(p, ap, event);
			
			// updeate inventory
			p.updateInventory();
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void antiBlockPlace(final BlockPlaceEvent eve) {
		// check is using default checking, game is running, and is not on the lobby
		if (!useDefaultChecking() || Game.isNotRunning() || KitUtils.isOnLobby(eve.getPlayer())) {
			return;
		}
		
		// get item and check
		final ItemStack item = eve.getItemInHand();
		if (item == null || specialItem == null || item.getType() != specialItem.getType() || !isSpecialItem(item)) {
			return;
		}
		
		// get player and anniplayer and check
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (!KitUtils.isValidPlayer(ap) || !hasThisKit(ap)) {
			return;
		}
		
		// cancell interaction
		eve.setCancelled(true);
		eve.setBuild(false);
	}
	
	@Override
	protected boolean useDefaultChecking() {
		return true;
	}
	
	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		doPrimaryAction(player, p, event);
		return true;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		doSecondaryAction(player, p, event);
		return true;
	}
	
	protected abstract void doPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event);
	
	protected abstract void doSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event);
	
	protected boolean performDelay(AnniPlayer ap) {
		return performCustomDelay(ap, delay);
	}
	
	protected boolean performCustomDelay(AnniPlayer ap, long customDelay) {
		if (KitUtils.isValidPlayer(ap) && hasThisKit(ap)) {
			// add delay
			if (customDelay > 0) {
				delays.addDelay(ap.getPlayer(), System.currentTimeMillis() + customDelay, getInternalName());
				return true;
			}
		}
		return false;
	}
	
	protected boolean sendPositiveMessage(Player p, AnniPlayer ap) {
		if (KitUtils.isValidPlayer(p) && hasThisKit(ap)) {
			if (this.useCustomMessage() && this.positiveMessage() != null) {
				p.sendMessage(positiveMessage());
			}
		}
		return false;
	}
	
	protected boolean sendNegativeMessage(Player p, AnniPlayer ap) {
		if (KitUtils.isValidPlayer(p) && hasThisKit(ap)) {
			if (this.useCustomMessage() && this.negativeMessage() != null) {
				p.sendMessage(negativeMessage());
			}
		}
		return false;
	}
}
