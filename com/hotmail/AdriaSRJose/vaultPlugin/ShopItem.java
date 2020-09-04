package com.hotmail.AdriaSRJose.vaultPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Menu.KitsMenuConfig;

import net.milkbowl.vault.economy.EconomyResponse;

public class ShopItem extends MenuItem {
	private final KitWrapper wrapper;
	private final Map<String, Long> confirmingPlayers;
	private final Shop shop;
	
	public ShopItem(KitWrapper wrapper, Shop shop) {
		super(wrapper.kit.getName(), wrapper.kit.getIconPackage().getIcon(), wrapper.kit.getIconPackage().getLore());
		this.shop         = shop;
		this.wrapper      = wrapper;
		confirmingPlayers = new HashMap<String, Long>();
	}
	
	public KitWrapper getWrapper ( ) {
		return wrapper;
	}
 	
	@Override
	public ItemStack getFinalIcon(Player player)
	{
		List<String> str = new ArrayList<String>(getLore());
//		str.add(ChatColor.GOLD+"--------------------------");
		
		final String[] finalLore = KitsMenuConfig.FINAL_LORE.toStringArray();
		for (String lin : finalLore) {
			str.add(lin);
		}
		
		if(wrapper.kit.hasPermission(player))
			str.add(Shop.purchasedMessage);
		else
		{
			Long l = confirmingPlayers.get(player.getName());
			if(l == null) {
				str.add(VaultHook.formatString(Shop.forsaleMessage, wrapper.price));
			}
			else
			{
				if(System.currentTimeMillis() - l.longValue() > 2000)
				{
					str.add(VaultHook.formatString(Shop.forsaleMessage, wrapper.price));
					confirmingPlayers.remove(player.getName());
				}
				else
					str.add(VaultHook.formatString(Shop.confirmMessage, wrapper.price));
			}
		}
		return setNameAndLore(getIcon().clone(), getDisplayName(), str);
	}
	
	@Override
	public void onItemClick(ItemClickEvent event)
	{
		final Player player = event.getPlayer();
		if(player != null && !wrapper.kit.hasPermission(player))
		{
			event.setWillUpdate(true);
			final AnniPlayer anniplayer = AnniPlayer.getPlayer(player.getUniqueId());
			if(anniplayer != null)
			{
				Long l = confirmingPlayers.get(player.getName());
				if(l != null)
				{
					confirmingPlayers.remove(player.getName());
					if(System.currentTimeMillis() - l.longValue() > 2000) {
						player.sendMessage(Shop.confirmExpired);
					}
					else
					{
						event.setWillUpdate(false);
						event.setWillClose(true);
						final double banace = shop.instance.getBalance(player);
						if (banace >= wrapper.price) {
							EconomyResponse rem = shop.instance.removeMoney(player, wrapper.price);
							if (rem.transactionSuccess()) {
								// add kit and send receive message
								shop.sql.addKit(player.getUniqueId(), wrapper.kit);
								player.sendMessage(Shop.kitPurchased.replace("%w", wrapper.kit.getName()));
							}
						}
						else player.sendMessage(Shop.notEnoughXP);
					}	
				}
				else confirmingPlayers.put(player.getName(), System.currentTimeMillis());
			}
		}	
	}
}
