package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShopMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShop.BossRewards;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

public class BossStar implements Listener {
	private static boolean CONFIG_REWARDS;

	public BossStar(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getItemInHand() != null && KitUtils.itemHasName(p.getItemInHand(), CustomItem.BossStar.getName())) {

				// BossRewards
				if (GameBoss.getBoss() != null) {
					final AnniBoss boss = GameBoss.getBoss();
					if (boss != null) {
						final ItemStack[] rewards = boss.getRewards();
						if (rewards != null && rewards.length > 0) {
							// open
							new BossRewards(rewards, Size.fit(boss.getRewardsInventorySize())).open(p);

							// set is loaded config from reward.
							CONFIG_REWARDS = true;
						} else {
							// open default
							new BossShopMenu(p);
							// set is not loaded config from reward.
							CONFIG_REWARDS = false;
							// print unvalidated rewards configuration
							// Util.print(ChatColor.RED, "An unvalidated configuration of the boss rewards
							// is detected.");
							Util.print(ChatColor.RED, "No valid boss rewards configuration detected!");
						}
					}
				}
				// open message
				p.sendMessage(Lang.MENSAJE_DEL_BOSS_SHOP.toString());
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void e(InventoryClickEvent e) {
		if (e.getInventory() == null) {
			return;
		}

		if (e.getClickedInventory() == null) {
			return;
		}

		if ( !Objects.equals ( Lang.BOSS_SHOP_NAME.toString ( ) , e.getView ( ).getTitle ( ) ) ) {
			return;
		}
//		if (!e.getInventory().getName().equals(Lang.BOSS_SHOP_NAME.toString())) {
//			return;
//		}

		if (CONFIG_REWARDS) {
			return;
		}

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		if (!KitUtils.isSpecial(e.getCurrentItem(), false)) {
			return;
		}

		e.setCancelled(true);

		ItemStack i = CustomItem.BossStar.toItemStack(1);
		Player p = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		if (KitUtils.itemHasName(clicked, CustomItem.BossStar.getName())) {
			return;
		}

		if (!KitUtils.isBossObject(clicked)) {
			return;
		}

		ItemStack item5 = new ItemStack(Material.IRON_HELMET);
		item5.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item6 = new ItemStack(Material.IRON_CHESTPLATE);
		item6.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item7 = new ItemStack(Material.IRON_LEGGINGS);
		item7.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item8 = new ItemStack(Material.IRON_BOOTS);
		item8.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item9 = new ItemStack(Material.GOLD_SWORD);
		item9.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);

		ItemStack item10 = new ItemStack(Material.DIAMOND_SWORD);
		item10.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

		ItemStack item11 = new ItemStack(Material.BOW);
		item11.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
		item11.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);

		ItemStack item12 = new ItemStack(Material.GOLD_PICKAXE);
		item12.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);

		ItemStack item13 = new ItemStack(Material.GOLD_PICKAXE);
		item13.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);

		if (clicked.getType().getId() == item5.getType().getId()
				&& clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Protection IV")) {
			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item5);
			p.updateInventory();
		} else if (clicked.getType().getId() == item6.getType().getId()
				&& clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Protection IV")) {

			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item6);
			p.updateInventory();
		} else if (clicked.getType().getId() == item7.getType().getId()
				&& clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Protection IV")) {

			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item7);
			p.updateInventory();
		} else if (clicked.getType().getId() == item8.getType().getId()
				&& clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Protection IV")) {

			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item8);
			p.updateInventory();
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Sharpness VI")) {

			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item9);
			p.updateInventory();
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Sharpness II")) {

			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item10);
			p.updateInventory();
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Power IV and Punch II")) {
			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item11);
			p.updateInventory();
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Efficiency V")) {
			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item12);
			p.updateInventory();
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase("§6Fortune III")) {
			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(item13);
			p.updateInventory();
		} else {
			p.closeInventory();
			p.getInventory().removeItem(i);
			p.getInventory().addItem(clicked);
			p.updateInventory();
		}
	}
}
