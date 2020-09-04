package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Warrior extends ConfigurableKit {
	private static int SWORD_PUNCH_LEVEL = 2;
	
	@Override
	protected int setDefaults(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "SwordPunchLevel", 2);
	}
	
	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			SWORD_PUNCH_LEVEL = Math.max(0, (section.getInt("SwordPunchLevel", SWORD_PUNCH_LEVEL) / 2));
		}
	}
	
	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Warrior";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.STONE_SWORD.getMaterial());
	}
	
	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the sword.", "", aqua + "You deal +1 damage with", aqua + "any melee weapon.", "",
				aqua + "Spawn with a sword and", aqua + "a health potion which", aqua + "enable you to immediately",
				aqua + "move on the enemy and", aqua + "attack!", "", aqua + "If you do not have a good",
				aqua + "support back at base gathering", aqua + "better gear for you, you",
				aqua + "will be useless in the", aqua + "late game.");
		return l;
	}

	@Override
	public void cleanup(Player player) {
	}

	// Adds the +1 melee damage. May need to be changed to work just for melee
	// WEAPONS and not every melee attack. idk.
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void damageListener(final EntityDamageByEntityEvent event) {
		if (event.getDamager() == null)
			return;

		Entity one = event.getDamager();
		if (one instanceof Player) {
			final Player damager = (Player) one;
			//
			final AnniPlayer d = AnniPlayer.getPlayer(damager.getUniqueId());
			//
			if (d != null && d.getKit().equals(this))
				event.setDamage(event.getDamage() + 1);
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addSoulboundEnchantedItem(new ItemStack(UniversalMaterial.WOODEN_SWORD.getMaterial()), Enchantment.KNOCKBACK, SWORD_PUNCH_LEVEL)
				.addWoodPick().addWoodAxe().addHealthPotion1();
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

}