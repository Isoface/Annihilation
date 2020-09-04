package com.hotmail.AdrianSRJose.AnniPro.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;

public class Loadout {
	private List<ItemStack> stacks;
	private ItemStack[] finalStacks;
	private ItemStack[] armor;
	private boolean useDefaultArmor;

	public Loadout() {
		stacks = new ArrayList<ItemStack>();
		finalStacks = null;
		armor = null;
		useDefaultArmor = true;
	}

	public Loadout addNavCompass() {
		return addItem(CustomItem.NAVCOMPASS.toItemStack());
	}

	public Loadout addWoodSword() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.WOOD_SWORD)));
	}

	public Loadout addStoneSword() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.STONE_SWORD)));
	}

	public Loadout addGoldSword() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.GOLD_SWORD)));
	}

	public Loadout addWoodPick() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.WOOD_PICKAXE)));
	}

	public Loadout addStonePick() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.STONE_PICKAXE)));
	}

	public Loadout addWoodAxe() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.WOOD_AXE)));
	}

	public Loadout addStoneAxe() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.STONE_AXE)));
	}

	public Loadout addWoodShovel() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.WOOD_SPADE)));
	}

	public Loadout addStoneShovel() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.STONE_SPADE)));
	}

	public Loadout addHealthPotion1() {
		return addItem(KitUtils.addSoulbound((new Potion(PotionType.INSTANT_HEAL, 1, false, false).toItemStack(1))));
	}

	public Loadout addBow() {
		return addItem(KitUtils.addSoulbound(new ItemStack(Material.BOW)));
	}

	public Loadout addSoulboundItem(ItemStack stack) {
		return addItem(KitUtils.addSoulbound(stack));
	}

	public Loadout addClassSoulboundItem(ItemStack stack) {
		return addItem(KitUtils.addClassSoulbound(stack));
	}

	public Loadout addClassUndropabbleSoulboundItem(ItemStack stack) {
		return addItem(KitUtils.addClassUndropabbleSoulbound(stack));
	}

	public Loadout addSoulboundEnchantedItem(ItemStack stack, Enchantment enchant, int level) {
		return addEnchantedItem(KitUtils.addSoulbound(stack), enchant, level);
	}

	public Loadout addClassSoulboundEnchantedItem(ItemStack stack, Enchantment enchant, int level) {
		return addEnchantedItem(KitUtils.addClassSoulbound(stack), enchant, level);
	}

	public Loadout addClassUndropabbleSoulboundEnchantedItem(ItemStack stack, Enchantment enchant, int level) {
		return addEnchantedItem(KitUtils.addClassUndropabbleSoulbound(stack), enchant, level);
	}

	public Loadout addEnchantedItem(ItemStack stack, Enchantment enchant, int level) {
		return addItem(KitUtils.addEnchant(stack, enchant, level));
	}

	public Loadout addItem(ItemStack stack) {
		if (finalStacks != null)
			throw new UnsupportedOperationException("Can not add an item to a loadout after finalizing it");
		stacks.add(stack.clone());
		return this;
	}

	public Loadout setUseDefaultArmor(boolean useArmor) {
		useDefaultArmor = useArmor;
		return this;
	}

	public Loadout setArmor(ItemStack[] armor) {
		this.armor = armor;
		return this;
	}

	public Loadout setArmor(int position, ItemStack armor) {
		if (this.armor == null)
			this.armor = new ItemStack[4];
		if (position < 0 || position > 3)
			throw new UnsupportedOperationException(
					"Can not add an armor piece at position less than 0 or greater than 3. Attempted at: " + position);
		this.armor[position] = armor;
		return this;
	}

	public Loadout finalizeLoadout() {
		if (finalStacks != null)
			throw new UnsupportedOperationException("Can not finalize a loadout twice");
		if (stacks.size() < 1)
			throw new UnsupportedOperationException("Can not finalize a loadout with no items");

		finalStacks = new ItemStack[stacks.size()];
		for (int x = 0; x < finalStacks.length; x++)
			finalStacks[x] = stacks.get(x);
		stacks.clear();
		stacks = null;
		return this;
	}

	public static ItemStack[] coloredArmor(AnniTeam team) {
		Color c;
		if (team.getColor() == ChatColor.RED)
			c = Color.RED;
		else if (team.getColor() == ChatColor.BLUE)
			c = Color.BLUE;
		else if (team.getColor() == ChatColor.GREEN)
			c = Color.GREEN;
		else
			c = Color.YELLOW;
		ItemStack[] stacks = KitUtils.getLeatherArmor();
		for (ItemStack stack : stacks) {
			LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
			meta.setColor(c);
			stack.setItemMeta(meta);
		}
		return stacks;
	}

	public static ItemStack[] getTeamArmor(Player player) {
		final AnniPlayer pl = AnniPlayer.getPlayer(player.getUniqueId());
		if (pl != null) {
			final AnniTeam t = pl.getTeam();
			if (t != null) {
				return coloredArmor(t);
			}
		}
		return null;
	}

	public Loadout giveLoadout(final Player player) {
		final ItemStack i8 = player.getInventory().getItem(8);
		//
		for (ItemStack s : finalStacks != null ? Arrays.asList(finalStacks) : stacks) {
			if (KitUtils.itemHasName(s, CustomItem.NAVCOMPASS.getName())) {
				if (i8 == null || i8.getType() == Material.AIR)
					player.getInventory().setItem(8, s);
				else
					player.getInventory().addItem(s);
			} else if (KitUtils.isClassSoulbound(s))
				player.getInventory().addItem(s);
			else if (KitUtils.isClassUndropabbleSoulbound(s))
				player.getInventory().addItem(s);
			else if (KitUtils.isTradicionaItem(s))
				player.getInventory().addItem(s);
			else if (KitUtils.isSoulbound(s))
				player.getInventory().addItem(s);
			else
				player.getInventory().addItem(s);
		}
		//
		ItemStack[] armor = null;
		//
		if (useDefaultArmor)
			armor = getTeamArmor(player);
		if (armor == null)
			armor = new ItemStack[4];
		if (this.armor != null)
			for (int x = 0; x < this.armor.length; x++)
				if (this.armor[x] != null)
					armor[x] = this.armor[x].clone();
		//
		player.getInventory().setHelmet(armor[3]);
		player.getInventory().setChestplate(armor[2]);
		player.getInventory().setLeggings(armor[1]);
		player.getInventory().setBoots(armor[0]);
		player.updateInventory();
		//
		return this;
	}
	
	public void setTeamArmor(final Player player) {
		ItemStack[] armor = null;
		if (useDefaultArmor) {
			armor = getTeamArmor(player);
		}
		
		if (armor == null) {
			armor = new ItemStack[4];
		}
		
		if (this.armor != null) {
			for (int x = 0; x < this.armor.length; x++) {
				if (this.armor[x] != null) {
					armor[x] = this.armor[x].clone();
				}
			}
		}

		// Set
		player.getInventory().setHelmet(armor[3]);
		player.getInventory().setChestplate(armor[2]);
		player.getInventory().setLeggings(armor[1]);
		player.getInventory().setBoots(armor[0]);
		player.updateInventory();
	}

	public ItemStack[] getFinalStacks() {
		return finalStacks;
	}

	public List<ItemStack> ToStacks() {
		return stacks;
	}
	
	public ItemStack[] getArmor() {
		return armor;
	}
}
