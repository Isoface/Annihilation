package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Pyro extends ClassItemKit {
	
	/**
	 * Enable/disble fire to invisible players.
	 */
	private static boolean FIRE_INVISIBLES = true;
	
	@Override
	protected void onInitialize() {
		// noting.
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack firestorm = KitUtils.addSoulbound ( new ItemStack ( UniversalMaterial.FIRE_CHARGE.getMaterial ( ) ) );
		ItemMeta meta = firestorm.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		firestorm.setItemMeta(meta);
		return firestorm;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.AQUA + "Firestorm";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		// check is not null performer and if has team.
		if (ap != null && ap.hasTeam()) {
			// get nearby players
			for (Entity e : p.getNearbyEntities(5, 5, 5)) {
				// check is a player.
				if (e instanceof Player) {
					// get victim
					final Player v     = (Player) e;
					final AnniPlayer d = AnniPlayer.getPlayer(v.getUniqueId());
					
					// check victim.
					if (d == null) {
						continue;
					}
					
					if (!d.hasTeam()) {
						continue;
					}
					
					if (d.getTeam().equals(ap.getTeam())) {
						continue;
					}
					
					// check can see.
					if (!p.canSee(v)) {
						continue;
					}
					
					// check game mode.
					if (v.getGameMode() == GameMode.SPECTATOR) {
						continue;
					}
					
					// check invisiblity.
					if (v.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						if (!FIRE_INVISIBLES) {
							continue;
						}
					}
					
					// fire !!!!!!!!!
					e.setFireTicks(40);
					
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.FLAME.displayNewerVersions().display(0.6F, 0.3F, 0.6F, 0.1F, 30,
								e.getLocation(), 200.0D);
					} else {
						CompatibleParticles.FLAME.displayOlderVersions().display(0.6F, 0.3F, 0.6F, 0.1F, 30,
								e.getLocation(), 200.0D);
					}
					
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 40000;
	}

	@Override
	protected String getInternalName() {
		return "Pyro";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack ( UniversalMaterial.FLINT_AND_STEEL.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the flame.", aqua + "", aqua + "A psychopath obsessed", aqua + "with fire.",
				aqua + "", aqua + "You Are Immune to", aqua + "the Burning Effects", aqua + "of Fire and Lava.",
				aqua + "", aqua + "With Every hit", aqua + "you Have a Chance", aqua + "of Setting your Enemies",
				aqua + "On fire.", aqua + "Use Firestorm to", aqua + "watch nearby", aqua + "Enemies burn!");
		return l;
	}

	@Override
	public void cleanup(Player player) {
		if (player != null) {
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		}
	}

	@Override
	public void onPlayerSpawn(final Player player) {
		if (player != null) {
			// give kit loadout.
			super.onPlayerSpawn(player);
			
			// add fire resistence.
			Shedulers.scheduleSync(() -> {
				// delayed.
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
			}, 2);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void arrowLaunch(final ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Arrow) {
			final ProjectileSource shooter = event.getEntity().getShooter();
			if (shooter != null && shooter instanceof Player) {
				final AnniPlayer p = AnniPlayer.getPlayer(((Player) shooter).getUniqueId());
				if (p != null && p.getKit().equals(this)) {
					event.getEntity().setFireTicks(999999);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void damageListener(final EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			final AnniPlayer d = AnniPlayer.getPlayer(((Player) event.getDamager()).getUniqueId());
			if (d != null && d.getKit().equals(this)) {
				if (RandomUtils.nextInt(100) < 37) {
					event.getEntity().setFireTicks(40);
				}
			}
		}
	}
	
	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}
	
	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addHealthPotion1()
				.addItem(super.getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		// give load out.
		this.addLoadoutToInventory(inv);
		
		// give potion effect.
		if (ap != null && ap.isOnline()) {
			ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
		}
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player player, AnniPlayer ap) {
		if (player != null) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
		}
	}
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "fire-invisible", true);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		FIRE_INVISIBLES = section.getBoolean("fire-invisible", FIRE_INVISIBLES);
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