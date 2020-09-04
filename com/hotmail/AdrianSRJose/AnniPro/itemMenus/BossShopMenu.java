package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

public class BossShopMenu implements Listener {

	private static final Material POTIONS_MATERIAL = (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)
			? UniversalMaterial.SPLASH_POTION
			: UniversalMaterial.POTION).getMaterial();

	@SuppressWarnings("deprecation")
	public BossShopMenu(Player p) {
		ItemStack potion = new ItemStack(POTIONS_MATERIAL);

		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setMainEffect(PotionEffectType.INCREASE_DAMAGE);
		meta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (4 * 60) * 20, 1), true);
		potion.setItemMeta(meta);
		ItemStack item0 = potion;
		ItemMeta meta0 = item0.getItemMeta();
		meta0.setDisplayName("§6Strength 2");
		meta0.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item0.setItemMeta(meta0);

		ItemStack potion1 = new ItemStack(POTIONS_MATERIAL);
		PotionMeta pmeta1 = (PotionMeta) potion1.getItemMeta();
		pmeta1.setMainEffect(PotionEffectType.REGENERATION);
		pmeta1.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 1 * 60 * 20, 1), true);
		potion1.setItemMeta(pmeta1);
		ItemStack item1 = potion1;
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName("§6Regeneration II");
		meta1.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item1.setItemMeta(meta1);

		ItemStack potion2 = new ItemStack(POTIONS_MATERIAL);
		PotionMeta pmeta2 = (PotionMeta) potion2.getItemMeta();
		pmeta2.setMainEffect(PotionEffectType.SPEED);
		pmeta2.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 60 * 20, 0), true);
		potion2.setItemMeta(pmeta2);
		ItemStack item2 = potion2;
		ItemMeta meta2 = item2.getItemMeta();
		meta2.setDisplayName("§6Speed");
		meta2.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item2.setItemMeta(meta2);

		ItemStack potion3 = new ItemStack(POTIONS_MATERIAL);
		PotionMeta pmeta3 = (PotionMeta) potion3.getItemMeta();
		pmeta3.setMainEffect(PotionEffectType.SPEED);
		pmeta3.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 2 * 60 * 20, 1), true);
		potion3.setItemMeta(pmeta3);
		ItemStack item3 = potion3;
		ItemMeta meta3 = item3.getItemMeta();
		meta3.setDisplayName("§6Speed II");
		meta3.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item3.setItemMeta(meta3);

		ItemStack potion4 = new ItemStack(UniversalMaterial.POTION.getMaterial());
		PotionMeta pmeta4 = (PotionMeta) potion4.getItemMeta();
		pmeta4.setMainEffect(PotionEffectType.INCREASE_DAMAGE);
		pmeta4.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2 * 60 * 20, 1), true);
		potion4.setItemMeta(pmeta4);
		ItemStack item4 = potion4;
		ItemMeta meta4 = item4.getItemMeta();
		meta4.setDisplayName("§6Strength II");
		meta4.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item4.setItemMeta(meta4);

		ItemStack item5 = new ItemStack(UniversalMaterial.IRON_HELMET.getMaterial());
		ItemMeta meta5 = item5.getItemMeta();
		meta5.setDisplayName("§6Protection IV");
		meta5.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item5.setItemMeta(meta5);
		item5.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item6 = new ItemStack(UniversalMaterial.IRON_CHESTPLATE.getMaterial());
		ItemMeta meta6 = item6.getItemMeta();
		meta6.setDisplayName("§6Protection IV");
		meta6.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item6.setItemMeta(meta6);
		item6.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item7 = new ItemStack(UniversalMaterial.IRON_LEGGINGS.getMaterial());
		ItemMeta meta7 = item7.getItemMeta();
		meta7.setDisplayName("§6Protection IV");
		meta7.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item7.setItemMeta(meta7);
		item7.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item8 = new ItemStack(UniversalMaterial.IRON_BOOTS.getMaterial());
		ItemMeta meta8 = item8.getItemMeta();
		meta8.setDisplayName("§6Protection IV");
		meta8.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item8.setItemMeta(meta8);
		item8.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack item9 = new ItemStack ( Material.GOLD_SWORD );
		ItemMeta meta9 = item9.getItemMeta();
		meta9.setDisplayName("§6Sharpness VI");
		meta9.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item9.setItemMeta(meta9);
		item9.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);

		ItemStack item10 = new ItemStack(UniversalMaterial.DIAMOND_SWORD.getMaterial());
		ItemMeta meta10 = item10.getItemMeta();
		meta10.setDisplayName("§6Sharpness II");
		meta10.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item10.setItemMeta(meta10);
		item10.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

		ItemStack item11 = new ItemStack(UniversalMaterial.BOW.getMaterial());
		ItemMeta meta11 = item11.getItemMeta();
		meta11.setDisplayName("§6Power IV and Punch II");
		meta11.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item11.setItemMeta(meta11);
		item11.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
		item11.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);

		ItemStack item12 = new ItemStack ( Material.GOLD_PICKAXE);
		ItemMeta meta12 = item12.getItemMeta();
		meta12.setDisplayName("§6Efficiency V");
		meta12.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item12.setItemMeta(meta12);
		item12.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);

		ItemStack item13 = new ItemStack(Material.GOLD_PICKAXE);
		ItemMeta meta13 = item13.getItemMeta();
		meta13.setDisplayName("§6Fortune III");
		meta13.setLore(Arrays.asList(new String[] { "", Lang.BOSS_OBJECT.toString() }));
		item13.setItemMeta(meta13);
		item13.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);

		Inventory inventario = Bukkit.createInventory(null, 36, Lang.BOSS_SHOP_NAME.toString());

		inventario.setItem(0, item0);
		inventario.setItem(1, item1);
		inventario.setItem(2, item2);
		inventario.setItem(3, item3);
		inventario.setItem(4, item4);
		inventario.setItem(9, item5);
		inventario.setItem(10, item6);
		inventario.setItem(11, item7);
		inventario.setItem(12, item8);
		inventario.setItem(18, item9);
		inventario.setItem(19, item10);
		inventario.setItem(20, item11);
		inventario.setItem(27, item12);
		inventario.setItem(28, item13);

		p.openInventory(inventario);
	}
}
