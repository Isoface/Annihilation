package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Berserker extends ConfigurableKit {
	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Berserker";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.CHAINMAIL_CHESTPLATE.getMaterial ( ) );
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the power.", "", aqua + "Start each life with only",
				aqua + "9 hearts of health, for", aqua + "every kill you make you", aqua + "gain a heart to a maximum",
				aqua + "health of 13 hearts.");
		return l;
	}

	@Override
	public void cleanup(Player player) {
		if (KitUtils.isValidPlayer(player)) {
			player.setMaxHealth(20.0D);
		}
	}

	@Override
	public void onPlayerSpawn(Player player) {
		player.setMaxHealth(18);
		super.onPlayerSpawn(player);
	}

	// If the berserker is below 40% health
	@EventHandler(priority = EventPriority.MONITOR)
	public void damageListener(final EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			final Player one = (Player) event.getDamager();
			if (KitUtils.isValidPlayer(one)) {
				final AnniPlayer p = AnniPlayer.getPlayer(one.getUniqueId());
				if (p != null && p.getKit().equals(this)) {
					double health = ((Damageable) one).getHealth();
					double maxHealth = ((Damageable) one).getMaxHealth();
					if ((health / maxHealth) <= .42) {
						event.setDamage(event.getDamage() + 1);
					}
				}
			}
		}
	}

	// checks for player death and increments max health
	@EventHandler(priority = EventPriority.MONITOR)
	public void damageListener(final PlayerDeathEvent event) {
		final Player killer = event.getEntity().getKiller();
		if (killer != null) {
			final AnniPlayer p = AnniPlayer.getPlayer(killer.getUniqueId());
			if (p != null && p.getKit().equals(this)) {
				double maxHealth = ((Damageable) killer).getMaxHealth();
				if (maxHealth <= 24) {
					killer.setMaxHealth(maxHealth + 2);
				}
			}
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addHealthPotion1();
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		ap.getPlayer().setMaxHealth(18.0D);
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}
}