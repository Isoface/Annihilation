package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent.KillAttribute;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Nexus;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;
import com.hotmail.AdrianSRJose.events.Defender_DefendHisNexusEvent;
import com.hotmail.AdrianSRJose.events.Defender_KillPlayersOnHisNexusEvent;

public class Defender extends ConfigurableKit {
	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Defender";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.WOODEN_SWORD.getMaterial ( ) );
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are the Last Line of Defense!.", "", aqua + "When All else Fails,",
				aqua + "You are the Only", aqua + "Thing Standing Between", aqua + "The Enemy and Your Nexus", "",
				aqua + "You gain more", aqua + "Health as Your", aqua + "Nexus Health Decreases,");
		return l;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void checkXP(PlayerKilledEvent event) {
		final AnniPlayer killer = event.getKiller();
		if (killer != null && killer.getKit().equals(this)
				&& event.getAttributes().contains(KillAttribute.NEXUSDEFENSE)) {
			Defender_KillPlayersOnHisNexusEvent eve = new Defender_KillPlayersOnHisNexusEvent(event.getKiller(),
					event.getPlayer());
			AnniEvent.callEvent(eve);
			//
			if (eve.isCancelled()) {
				return;
			}
			//
			if (killer.isOnline())
				killer.getPlayer().giveExp(eve.getGivedExp());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void damageHandler(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			final AnniPlayer p = AnniPlayer.getPlayer(event.getEntity().getUniqueId());
			if (p != null && p.getTeam() != null && !p.getTeam().isTeamDead() && p.getKit().equals(this)) {
				final Nexus nexus = p.getTeam().getNexus();
				if (nexus != null && nexus.getLocation() != null) {
					final Player player = (Player) event.getEntity();
					if (KitUtils.isValidPlayer(player)
							&& player.getLocation().distanceSquared(nexus.getLocation().toLocation()) <= 20 * 20) {
						Defender_DefendHisNexusEvent eve = new Defender_DefendHisNexusEvent(p);
						eve.addPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 0));
						AnniEvent.callEvent(eve);
						// ---
						if (eve.isCancelled())
							return;
						// ---
						if (eve.addPotions() && !eve.getPotions().isEmpty())
							player.addPotionEffects(eve.getPotions());
						// ---
					}
				}
			}
		}
	}

	@Override
	public void cleanup(Player p) {
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel()
				.addItem(new ItemStack ( UniversalMaterial.ENDER_PEARL.getMaterial ( ) ) ).setUseDefaultArmor(true)
				.setArmor(2, KitUtils.addSoulbound ( new ItemStack ( UniversalMaterial.CHAINMAIL_CHESTPLATE.getMaterial ( ) ) ) );
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
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