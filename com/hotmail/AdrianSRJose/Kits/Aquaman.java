package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Aquaman extends ClassItemKit {
	
	@Override
	protected String getInternalName() {
		return "AquaMan";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.COD.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the Fish", "", aqua + "With your super boots",
				aqua + "you can walk under the water,", aqua + "", aqua + "and with your power",
				aqua + "you will receive a few", aqua + "seconds of breathing", aqua + "under the water.",
				aqua + "Use this kit in a strategic way,", aqua + "", aqua + "go to the middle and",
				aqua + "camouflage with the water.", aqua + "", aqua + "Activate your super",
				aqua + "power and attack!");
		return l;
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.COD.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + "Depth Buster";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		if (p != null) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 20, 0));
			//
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				CompatibleParticles.CLOUD.displayNewerVersions().display(0.3F, 0.2F, 0.3F, 0.0F, 5,
						p.getLocation().clone().add(0.0D, 1.5D, 0.0D), 100.0D);
				CompatibleParticles.CLOUD.displayNewerVersions().display(0.3F, 0.2F, 0.3F, 0.0F, 10,
						p.getLocation().clone().add(0.0D, 1.5D, 0.0D), 100.0D);
				CompatibleParticles.CLOUD.displayNewerVersions().display(0.3F, 0.2F, 0.3F, 0.0F, 20,
						p.getLocation().clone().add(0.0D, 1.5D, 0.0D), 100.0D);
			} else {
				CompatibleParticles.CLOUD.displayOlderVersions().display(0.3F, 0.2F, 0.3F, 0.0F, 5,
						p.getLocation().clone().add(0.0D, 1.5D, 0.0D), 100.0D);
				CompatibleParticles.CLOUD.displayOlderVersions().display(0.3F, 0.2F, 0.3F, 0.0F, 10,
						p.getLocation().clone().add(0.0D, 1.5D, 0.0D), 100.0D);
				CompatibleParticles.CLOUD.displayOlderVersions().display(0.3F, 0.2F, 0.3F, 0.0F, 20,
						p.getLocation().clone().add(0.0D, 1.5D, 0.0D),	100.0D);
			}
		}
		return true;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 80000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodAxe().addWoodPick().addWoodShovel().addItem(getSpecialItem())
				.setUseDefaultArmor(true).setArmor(0, KitUtils
						.addSoulboundEnchantedItem(new ItemStack ( UniversalMaterial.GOLDEN_BOOTS.getMaterial ( ) ), Enchantment.DEPTH_STRIDER, 2));
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player player) {
		// check player.
		if (player != null) {
			// remove water breathin,
			player.removePotionEffect(PotionEffectType.WATER_BREATHING);

			// remove kit boots.
			if (KitUtils.isSoulbound(player.getInventory().getBoots())) {
				if ( player.getInventory().getBoots().getType() == UniversalMaterial.GOLDEN_BOOTS.getMaterial ( ) ) {
					if (player.getInventory().getBoots().getItemMeta().hasEnchant(Enchantment.DEPTH_STRIDER)) {
						player.getInventory().setBoots(null);
						player.updateInventory();
					}
				}
			}
		}
	}

	@Override
	protected void onInitialize() {
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}

	@Override
	protected boolean useCustomMessage() {
		return false;
	}

	@Override
	protected String positiveMessage() {
		return null;
	}

	@Override
	protected String negativeMessage() {
		return null;
	}
}
