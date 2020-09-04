package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Inmobilezer extends ClassItemKit {
	private ArrayList<UUID> imn;
	private Integer seconds = Integer.valueOf(4);

	@Override
	protected void onInitialize() {
		imn = new ArrayList<UUID>();
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack special = KitUtils.addSoulbound(new ItemStack(UniversalMaterial.SLIME_BALL.getMaterial()));
		ItemMeta m = special.getItemMeta();
		m.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		special.setItemMeta(m);
		return special;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Inmobilezer";
	}

	@Override
	protected boolean isSpecialItem(ItemStack paramItemStack) {
		if (paramItemStack != null && paramItemStack.hasItemMeta() && paramItemStack.getItemMeta().hasDisplayName()) {
			String str = paramItemStack.getItemMeta().getDisplayName();
			//
			if (str.contains(getSpecialItemName()) && KitUtils.isSoulbound(paramItemStack))
				return true;
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (KitUtils.isValidPlayer(player) && p != null && p.getTeam() != null) {
			Player e = instance.getPlayerInSight(player, 3);
			if (e != null) {
				AnniPlayer pl = AnniPlayer.getPlayer(e.getUniqueId());
				if (pl != null && !pl.getTeam().equals(p.getTeam())) {
					if (!imn.contains(e.getUniqueId()))
						imn.add(e.getUniqueId());

					e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * seconds), 10));
					e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (20 * seconds), 10));
					// player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 4,
					// 2));
					e.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (20 * seconds), 200));

					e.setGameMode(GameMode.ADVENTURE);
					player.setGameMode(GameMode.ADVENTURE);

					final Location ploc = player.getLocation();
					final Vector vector = new Vector();

					double rotX = ploc.getYaw();
					double rotY = ploc.getPitch();
					vector.setY(-Math.sin(Math.toRadians(rotY)));
					double xz = Math.cos(Math.toRadians(rotY));
					vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
					vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

					new BukkitRunnable() {
						@Override
						public void run() {
							if (!imn.contains(player.getUniqueId()) || !imn.contains(e.getUniqueId()))
								cancel();

							lin(vector, player, e);
						}
					}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 0);

					Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
						@Override
						public void run() {
							imn.remove(player.getUniqueId());
							imn.remove(e.getUniqueId());

							e.setGameMode(GameMode.SURVIVAL);
							player.setGameMode(GameMode.SURVIVAL);
						}
					}, (20 * seconds));

					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * seconds), 10));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (20 * seconds), 10));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (20 * seconds), 200));

					if (!imn.contains(player.getUniqueId()))
						imn.add(player.getUniqueId());
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent eve) {
		if (eve.getDamager() == null || eve.getEntity() == null)
			return;

		if (Game.isNotRunning())
			return;
		//
		if (eve.getEntity() instanceof Player && eve.getDamager() instanceof Player) {
			final Player hit = (Player) eve.getEntity();
			final Player dama = (Player) eve.getDamager();
			final AnniPlayer ap = AnniPlayer.getPlayer(hit.getUniqueId());
			final AnniPlayer dp = AnniPlayer.getPlayer(dama.getUniqueId());
			if (ap != null && dp != null && ap.getTeam() != null && imn.contains(hit.getUniqueId())) {
				if (dp.getTeam() != null) {
					if (imn.contains(dama.getUniqueId())) {
						eve.setCancelled(true);
						return;
					}
				}

				imn.remove(hit.getUniqueId());
				hit.setGameMode(GameMode.SURVIVAL);

				if (hit.hasPotionEffect(PotionEffectType.SLOW))
					hit.removePotionEffect(PotionEffectType.SLOW);

				if (hit.hasPotionEffect(PotionEffectType.BLINDNESS))
					hit.removePotionEffect(PotionEffectType.BLINDNESS);

				if (hit.hasPotionEffect(PotionEffectType.WEAKNESS))
					hit.removePotionEffect(PotionEffectType.WEAKNESS);

				if (hit.hasPotionEffect(PotionEffectType.JUMP))
					hit.removePotionEffect(PotionEffectType.JUMP);
			}
		}
	}

	public void lin(final Vector vect, Player player, final Player vict) {
		new BukkitRunnable() {
			double t = 0;
			final Location loc = player.getLocation();
			final Vector direction = vect.normalize();

			@Override
			public void run() {
				t = t + 0.5;
				double x = direction.getX() * t;
				double y = direction.getY() * t + 1.0;
				double z = direction.getZ() * t;
				loc.add(x, y, z);
				
				if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
					CompatibleParticles.CRIT.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 1, loc, 10000);
				} else {
					CompatibleParticles.CRIT.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 1, loc, 10000);
				}
				
				loc.subtract(x, y, z);

				if (t > player.getLocation().distance(vict.getLocation()))
					cancel();

				if (!imn.contains(player.getUniqueId()) || !imn.contains(vict.getUniqueId()))
					cancel();

				if (player == null || vict == null || !vict.isOnline() || !player.isOnline())
					cancel();
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 0);
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 30000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Inmobilizer";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.SLIME_BALL.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are The End!!", aqua + "", aqua + "Some People Like", aqua + "to Move.", aqua + "",
				aqua + "The Immobilizer Doesn't", aqua + "", aqua + "Stop an enemy", aqua + "dead in Their",
				aqua + "tracks for 4 Seconds");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addHealthPotion1().addItem(getSpecialItem());
	}

	@Override
	public void cleanup(Player arg0) {
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
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "StopSeconds", this.seconds);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section == null) {
			return;
		}

		seconds = section.getInt("StopSeconds", seconds);
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