package com.hotmail.AdrianSRJose.AnniPro.utils.Shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class ShopMenuItem extends MenuItem {
	private final ItemStack display;
	private final ItemStack product;
	private final int cost;

	public ShopMenuItem(ItemStack displayStack, ItemStack productStack, int cost, List<String> l) {
		super(null, null, new String[0]);
		display = displayStack.clone();
		product = productStack.clone();

		ArrayList<String> lor = new ArrayList<String>();
		if (l != null && !l.isEmpty()) {
			for (String lore : l) {
				lore = lore.replace("%c", String.valueOf(cost));
				lore = lore.replace("%q", String.valueOf(product.getAmount()));
				lore = Util.wc(lore);
				//
				synchronized (lore) {
					lor.add(lore);
				}
			}
		} else {
			l.add(ChatColor.GOLD + "Cost: %# Gold Ingots");
			l.add(ChatColor.GOLD + "Quantity: %#");
		}

		ItemMeta m = display.getItemMeta();
		m.setLore(lor);
		display.setItemMeta(m);
		this.cost = cost;
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		Player player = event.getPlayer();
		PlayerInventory p = player.getInventory();
		if (p.containsAtLeast(new ItemStack(Material.GOLD_INGOT), cost)) {
			int total = 0;
			for (ItemStack s : p.all(Material.GOLD_INGOT).values()) {
				total += s.getAmount();
			}
			p.remove(Material.GOLD_INGOT);
			if (total - cost > 0)
				p.addItem(new ItemStack(Material.GOLD_INGOT, total - cost));
			p.addItem(product);
			player.sendMessage(Lang.PURCHASEDITEM.toString());
		} else
			player.sendMessage(Lang.COULDNOTPURCHASE.toString());
	}

	@Override
	public ItemStack getFinalIcon(Player player) {
		return display;
	}
}
