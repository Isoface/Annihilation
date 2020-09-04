package com.hotmail.AdrianSRJose.xpSystem.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.KitsMenuConfig;
import com.hotmail.AdrianSRJose.xpSystem.main.XPMain;
import com.hotmail.AdrianSRJose.xpSystem.main.XPSystem;
import com.hotmail.AdrianSRJose.xpSystem.utils.Acceptor;

public class KitShopMenuItem extends MenuItem {
	private final KitWrapper wrapper;
	private final XPSystem system;
	private final Map<String, Long> confirmingPlayers;

	public KitShopMenuItem(final KitWrapper wrapper, final XPSystem system) {
		super(wrapper.kit.getName(), wrapper.kit.getIconPackage().getIcon(), wrapper.kit.getIconPackage().getLore());
		this.wrapper = wrapper;
		this.system = system;
		confirmingPlayers = new HashMap<String, Long>();
	}

	public KitShopMenuItem(final KitWrapper wrapper, final XPSystem system, final String[] lore) {
		super(wrapper.kit.getName(), wrapper.kit.getIconPackage().getIcon(), lore);
		this.wrapper = wrapper;
		this.system = system;
		confirmingPlayers = new HashMap<String, Long>();
	}
	
	public KitWrapper getWrapper() {
		return this.wrapper;
	}

	public XPSystem getSystem() {
		return this.system;
	}

	public Map<String, Long> getConfirmingPlayers() {
		return this.confirmingPlayers;
	}

	public Kit getKit() {
		return wrapper.kit;
	}

	@Override
	public ItemStack getFinalIcon(Player player) {
		List<String> str = new ArrayList<String>(getLore());

		// str.add(ChatColor.GOLD+"--------------------------");
		final String[] finalLore = KitsMenuConfig.FINAL_LORE.toStringArray();
		for (String lin : finalLore) {
			str.add(lin);
		}

		if (wrapper.kit.hasPermission(player)) {
			str.add(Shop.purchasedMessage);
		} else {
			Long l = confirmingPlayers.get(player.getName());
			if (l == null) {
				str.add(XPMain.formatString(Shop.forsaleMessage, wrapper.price));
			} else {
				if (System.currentTimeMillis() - l.longValue() > 2000) {
					str.add(XPMain.formatString(Shop.forsaleMessage, wrapper.price));
					confirmingPlayers.remove(player.getName());
				} else {
					str.add(XPMain.formatString(Shop.confirmMessage, wrapper.price));
				}
			}
		}
		return setNameAndLore(getIcon().clone(), getDisplayName(), str);
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		final Player player = event.getPlayer();
		if (player != null && !wrapper.kit.hasPermission(player)) {
			player.performCommand("shop");
			event.setWillUpdate(true);
			final AnniPlayer anniplayer = AnniPlayer.getPlayer(player.getUniqueId());
			if (anniplayer != null) {
				Long l = confirmingPlayers.get(player.getName());
				if (l != null) {
					confirmingPlayers.remove(player.getName());
					if (System.currentTimeMillis() - l.longValue() > 2000) {
						player.sendMessage(Shop.confirmExpired);
					} else {
						event.setWillUpdate(false);
						event.setWillClose(true);
						this.system.getXP(player.getUniqueId(), new Acceptor<Integer>() {
							@Override
							public void accept(Integer xp) {
								if (xp >= wrapper.price) {
									system.addKit(player.getUniqueId(), player.getName(), wrapper.kit);
									system.removeXP(player.getUniqueId(), wrapper.price);
									player.sendMessage(Shop.kitPurchased.replace("%w", wrapper.kit.getName()));
								} else
									player.sendMessage(Shop.notEnoughXP);

							}
						});
					}
				} else
					confirmingPlayers.put(player.getName(), System.currentTimeMillis());
			}
		}
	}
}
