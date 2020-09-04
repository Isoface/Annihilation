package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Tank extends ConfigurableKit {
	
	@Override
	protected String getInternalName() {
		return "Tank";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.IRON_CHESTPLATE.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You're The Tank Of Your Team.", "", aqua + "With Your Stone Sword",
				aqua + "You Can Push Your Enemies.", aqua + "You Start with a iron chest plate,",
				aqua + "Slow and damage resistence.", "", aqua + "With this kit you max healt",
				aqua + "is of 12 hearts.", "", aqua + "Your beams but I damage ", aqua + "when hitting an enemy.");

		return l;
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void alMatar(PlayerDeathEvent eve) {
		if (Game.isNotRunning() || eve.getEntity().getKiller() == null) {
			return;
		}

		final Player asesino = eve.getEntity().getKiller();
		final AnniPlayer pb = AnniPlayer.getPlayer(asesino.getUniqueId());
		if (KitUtils.isValidPlayer(asesino)) {
			if (pb != null && pb.getKit().equals(this)) {
				asesino.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (5 * 20), 0));
			}
		}
	}

	@EventHandler
	public void alPegar(EntityDamageByEntityEvent eve) {
		if (eve.getEntity() != null && eve.getDamager() != null) {
			if (eve.getEntity() instanceof Player && eve.getDamager() instanceof Player) {
				final Player p = (Player) eve.getDamager();
				final AnniPlayer pb = AnniPlayer.getPlayer(p.getUniqueId());
				//
				final Player toD = (Player) eve.getEntity();
				final AnniPlayer aD = AnniPlayer.getPlayer(toD.getUniqueId());
				//
				if (pb == null || aD == null)
					return;
				//
				if (Game.isGameRunning() && pb.getKit().equals(this) && aD.getTeam() != null && pb.getTeam() != null
						&& !pb.getTeam().equals(aD.getTeam())) {
					Random r = new Random();
					int x = r.nextInt(2);
					//
					double moreDamage = 0;
					//
					switch (x) {
					case 0:
						moreDamage = 3;
						break;
					case 1:
						moreDamage = 2;
						break;
					case 2:
						moreDamage = 1.5;
						break;
					}
					//
					eve.setDamage((eve.getDamage() + moreDamage));
				}
			}
		}
	}

	@Override
	public void cleanup(Player player) {
		for (PotionEffectType pts : PotionEffectType.values()) {
			if (pts != null) {
				if (pts.equals(PotionEffectType.DAMAGE_RESISTANCE) || pts.equals(PotionEffectType.SLOW)) {
					player.removePotionEffect(pts);
				}
			}
		}
		//
		player.setMaxHealth(20.0D);
	}

	@Override
	protected void setUp() {
	}

	@Override
	protected int setDefaults(ConfigurationSection paramConfigurationSection) {
		return 0;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addWoodShovel().setUseDefaultArmor(true)
				.setArmor(2, KitUtils.addSoulbound ( new ItemStack ( UniversalMaterial.IRON_CHESTPLATE.getMaterial ( ) ) ) );
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		//
		if (ap != null && ap.getPlayer() != null) {
			final Player p = ap.getPlayer();
			ap.getPlayer().setMaxHealth(24D);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		}
		//
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer pb) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
				p.setMaxHealth(24D);
				p.setHealth(24D);
			}
		}.runTask(AnnihilationMain.INSTANCE);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}
}