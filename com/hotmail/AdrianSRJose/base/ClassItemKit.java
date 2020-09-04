package com.hotmail.AdrianSRJose.base;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public abstract class ClassItemKit extends ConfigurableKit {
	protected ItemStack specialItem;
	protected String specialItemName;
	protected Delays delays;
	protected long delay;

	@Override
	protected void setUp() {
		delays = Delays.getInstance();
		specialItem = specialItem();
		if (delay > 0 && useDefaultChecking()) {
			delays.createNewDelay(getInternalName(), new StandardItemUpdater(getSpecialItemName(),
					specialItem.getType(), new Function<ItemStack, Boolean>() {
						@Override
						public Boolean apply(ItemStack stack) {
							return isSpecialItem(stack);
						}
					}));
		}
		onInitialize();
	}

	protected abstract void onInitialize();

	// getSpecialItem() has a guarantee that the special item name
	// has a value
	protected abstract ItemStack specialItem();

	protected abstract String defaultSpecialItemName();

	protected abstract boolean isSpecialItem(ItemStack stack);

	protected abstract boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event);

	protected abstract boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event);

	protected abstract long getDefaultDelayLength();

	protected abstract boolean useDefaultChecking();

	protected abstract boolean useCustomMessage();

	protected abstract String positiveMessage();

	protected abstract String negativeMessage();

	protected abstract int setInConfig(ConfigurationSection section);

	public ItemStack getSpecialItem() {
		return specialItem;
	}

	// This will be called before setUp
	@Override
	protected void loadKitStuff(ConfigurationSection section) {
		if (section != null) {
			super.loadKitStuff(section);

			// Get Special Item Name
			if (section.isString("SpecialItemName")) {
				specialItemName = Util.wc(section.getString("SpecialItemName"));
			}

			// Get Long
			delay = Long.valueOf(section.getInt("Coutdown", (int) delay));
			if (delay < 0)
				delay = Long.valueOf(0);
			else
				delay = ((int) delay) * 1000;
		}
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		if (section == null) {
			return 0;
		}
		
		int save = 0;
		//
		save += defaultSpecialItemName().isEmpty() ? 0
				: Util.setDefaultIfNotSet(section, "SpecialItemName",
						(defaultSpecialItemName().replace("§", "&")));
		save += Util.setDefaultIfNotSet(section, "Coutdown", (getDefaultDelayLength() / 1000));
		save += setInConfig(section);
		//
		return save;
	}

	public String getSpecialItemName() {
		return specialItemName;
	}

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
			if (performPrimaryAction(p, ap, event)) {
				// add delay
				if (delay > 0) {
					delays.addDelay(p, System.currentTimeMillis() + delay, getInternalName());
				}
				
				// custom positive message
				if (this.useCustomMessage() && this.positiveMessage() != null) {
					p.sendMessage(positiveMessage());
				}
			} else {
				// custom negative message
				if (this.useCustomMessage() && this.negativeMessage() != null) {
					p.sendMessage(negativeMessage());
				}
			}
			return;
		}

		// when is Secondary Action
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (performSecondaryAction(p, ap, event)) {
				// add delay
				if (delay > 0) {
					delays.addDelay(p, System.currentTimeMillis() + delay, getInternalName());
				}
				
				// custom positive message
				if (this.useCustomMessage() && this.positiveMessage() != null) {
					p.sendMessage(positiveMessage());
				}
			} else {
				// custom negative message
				if (this.useCustomMessage() && this.negativeMessage() != null) {
					p.sendMessage(negativeMessage());
				}
			}
			return;
		}
	}
	
	protected long getDelay() {
		return delay;
	}

	protected void setDelay(final AnniPlayer kitOwner, Long newDelay) {
		if (hasThisKit(kitOwner)) {
			ClassItemKit k = (ClassItemKit) kitOwner.getKit();
			kitOwner.setData(k.getOfficialName() + "-old-delay", Long.valueOf(k.getDelay()));
			k.delay = newDelay;
		}
	}

	protected void increaseDelay(final AnniPlayer kitOwner, Long adition) {
		if (hasThisKit(kitOwner)) {
			setDelay(kitOwner, (((ClassItemKit) kitOwner.getKit()).delay + adition));
		}
	}

	protected void decreaseeDelay(final AnniPlayer kitOwner, Long adition) {
		increaseDelay(kitOwner, Long.valueOf(adition.longValue() * -1));
	}

	protected void restoreDelay(final AnniPlayer kitOwner) {
		if (hasThisKit(kitOwner)) {
			ClassItemKit k = (ClassItemKit) kitOwner.getKit();
			Object od = kitOwner.getData(k.getOfficialName() + "-old-delay");
			if (od instanceof Long) {
				k.delay = ((Long) od).longValue();
				kitOwner.setData(k.getOfficialName() + "-old-delay", null);
			}
		}
	}
}