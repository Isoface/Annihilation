package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Samurai extends ClassItemKit {
	private final Map<UUID, Integer> times = new HashMap<UUID, Integer>();
	private final Map<UUID, Integer> totalTimes = new HashMap<UUID, Integer>();
	private final Map<UUID, Long> lasShurikenShoot = new HashMap<UUID, Long>();
	private static String SHURIKENS_NAME = ChatColor.RED + "Shuriken";
	private static String NO_SHURIKENS   = ChatColor.RED + "Not availabe shurikens to shoot!";
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "ShurikensName", Util.untranslateAlternateColorCodes(SHURIKENS_NAME))
				+ Util.setDefaultIfNotSet(section, "NoShurikens", Util.untranslateAlternateColorCodes(NO_SHURIKENS));
	}
	
	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		SHURIKENS_NAME = Util.wc(section.getString("ShurikensName", SHURIKENS_NAME));
		NO_SHURIKENS   = Util.wc(section.getString("NoShurikens",   NO_SHURIKENS));
	}
	
	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack tor = new ItemStack ( UniversalMaterial.FIREWORK_STAR.getMaterial ( ) , 1);
		return KitUtils
				.addClassUndropabbleSoulbound(KitUtils.setName(tor, getSpecialItemName() + instance.getReadyPrefix()));
	}

	private static ItemStack getShurikens(int amm) {
		return KitUtils.addClassUndropabbleSoulbound(
				KitUtils.setName(new ItemStack ( UniversalMaterial.PRISMARINE_CRYSTALS.getMaterial ( ) , amm), SHURIKENS_NAME));
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GRAY + "Smoke Bomb";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			if (KitUtils.itemNameContains(stack, getSpecialItemName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isShurikenslItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			if (KitUtils.itemHasName(stack, SHURIKENS_NAME)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onInt(PlayerInteractEvent eve) {
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (!KitUtils.isValidPlayer(ap)) {
			return;
		}

		if (!hasThisKit(ap)) {
			return;
		}

		final ItemStack it = eve.getItem();
		if (!isShurikenslItem(it)) {
			return;
		}

		// Cancell
		eve.setCancelled(true);

		Long l = lasShurikenShoot.get(p.getUniqueId());
		if (l != null) {
			if (((System.currentTimeMillis() - l) / 1000) < 1.0) {
				return;
			}
		}

		// Shoot
		if (it.getAmount() > 1) {
			lasShurikenShoot.put(p.getUniqueId(), System.currentTimeMillis());
			Shedulers.scheduleSync(new shurikenConsumer(p, it), 15 * 20);
			new shurikenShoot(p);
		} else
			p.sendMessage(NO_SHURIKENS);
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		final Item star = player.getWorld().dropItem(player.getEyeLocation(),
				new ItemStack ( UniversalMaterial.FIREWORK_STAR.getMaterial ( ) , 1));
		star.setPickupDelay(Integer.MAX_VALUE);
		star.setVelocity(player.getEyeLocation().getDirection().multiply(2));
		times.put(player.getUniqueId(), Integer.valueOf(0));
		totalTimes.put(player.getUniqueId(), Integer.valueOf(0));

		new BukkitRunnable() {
			@Override
			public void run() {
				if (totalTimes.get(player.getUniqueId()) > 8) {
					if (star != null) {
						star.remove();
					}
					cancel();
					return;
				}

				if (times.get(player.getUniqueId()) > 1) {
					// Play fizz sound
					star.getWorld().playSound(star.getLocation(), UniversalSound.FIZZ.asBukkit(), 2.0F, 2.0F);

					// Play Smoke Effect
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.LARGE_SMOKE.displayNewerVersions().display(3.5f, 3.5f, 3.5f, 0, 25, star.getLocation(), 10000);
					} else {
						CompatibleParticles.LARGE_SMOKE.displayOlderVersions().display(3.5f, 3.5f, 3.5f, 0, 25, star.getLocation(), 10000);
					}

					// Add Speed
					for (AnniPlayer ap : AnniPlayer.getPlayers()) {
						if (KitUtils.isValidPlayer(ap)) {
							if (ap.hasTeam()) {
								if (ap.getTeam().equals(p.getTeam())) {
									if (ap.getPlayer().getLocation().distance(star.getLocation()) <= 8) {
										ap.getPlayer().addPotionEffect(
												new PotionEffect(PotionEffectType.SPEED, (1 * 20), 1), true);
									}
								} else {
									if (ap.getPlayer().getLocation().distance(star.getLocation()) <= 8) {
										ap.getPlayer().addPotionEffect(
												new PotionEffect(PotionEffectType.SLOW, (1 * 35), 1), true);
										ap.getPlayer().addPotionEffect(
												new PotionEffect(PotionEffectType.BLINDNESS, (1 * 35), 2), true);
									}
								}
							}

						}
					}
					return;
				}

				// Play Sound
				star.getLocation().getWorld().playSound(star.getLocation(), UniversalSound.CLICK.asBukkit(), 1.4F, 2.0F);
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 4L, 4L);

		new BukkitRunnable() {
			@Override
			public void run() {
				// Cancell task
				if (totalTimes.get(player.getUniqueId()) > 8) {
					times.put(player.getUniqueId(), Integer.valueOf(0));
					totalTimes.put(player.getUniqueId(), Integer.valueOf(0));
					this.cancel();
					return;
				}

				// Times
				times.put(player.getUniqueId(), Integer.valueOf(times.get(player.getUniqueId()) + 1));
				totalTimes.put(player.getUniqueId(), Integer.valueOf(totalTimes.get(player.getUniqueId()) + 1));
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 20L, 20L);
		return true;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 40000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
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

	@Override
	protected String getInternalName() {
		return "Samurai";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack ( UniversalMaterial.FIREWORK_STAR.getMaterial ( ) , 1 );
	}

	@Override
	protected List<String> getDefaultDescription() {
		return new ArrayList<String>();
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(getSpecialItem())
				.addItem(getShurikens(4));
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player paramPlayer) {
	}

	@Override
	public boolean onItemClick(Inventory paramInventory, AnniPlayer paramAnniPlayer) {
		this.addLoadoutToInventory(paramInventory);
		return true;
	}

	private static class shurikenConsumer implements Runnable {
		private final UUID a;

		public shurikenConsumer(final Player p, final ItemStack st) {
			this.a = p.getUniqueId();
			st.setAmount(st.getAmount() - 1);
		}

		@Override
		public void run() {
			if (KitUtils.isValidPlayer(Bukkit.getPlayer(a))) {
				AnniPlayer ap = AnniPlayer.getPlayer(a);
				if (ap.getKit() != null && ap.getKit().getName().equals("Samurai")) {
					Bukkit.getPlayer(a).getInventory().addItem(getShurikens(1));
					Bukkit.getPlayer(a).updateInventory();
				}
			}
		}
	}

	private static class shurikenShoot {
		private final UUID a;
		private int shoots = 0;

		public shurikenShoot(final Player p) {
			this.a = p.getUniqueId();
			new BukkitRunnable() {
				@Override
				public void run() {
					if (shoots >= 3) {
						this.cancel();
						return;
					}

					Player pl = Bukkit.getPlayer(a);
					if (!KitUtils.isValidPlayer(pl)) {
						shoots = 3;
					}

					if (shoots < 3) {
						Arrow row = pl.getWorld().spawnArrow(pl.getEyeLocation().clone(),
								pl.getEyeLocation().getDirection().normalize(), 2.0F, 2.0F);

						row.setShooter(pl);
						// row.setTicksLived(2);
						for (Player p : Bukkit.getOnlinePlayers()) {
							Util.setInvisibleFor(row, new Player[] { p });
						}

						new shurikeMonitor(row, p.getWorld());
					}
					shoots++;
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 5L, 5L);
		}
	}

	private static class shurikeMonitor {
		private final UUID id;

		public shurikeMonitor(Arrow arr, final World w) {
			id = arr.getUniqueId();

			new BukkitRunnable() {
				@Override
				public void run() {
					Arrow arr = (Arrow) Util.getEntity(w, id);
					if (arr == null) {
						cancel();
						return;
					}

					if (arr.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
						w.playSound(arr.getLocation(),
								(ServerVersion.serverSameVersion(ServerVersion.v1_8_R1) ? Sound.valueOf("HORSE_WOOD")
										: Sound.valueOf("ENTITY_ITEMFRAME_PLACE")),
								3.5F, ServerVersion.serverSameVersion(ServerVersion.v1_8_R1) ? 2.0F : 0.7F);
						arr.remove();
						cancel();
						return;
					}

					if (arr.isOnGround()) {
						w.playSound(arr.getLocation(),
								(ServerVersion.serverSameVersion(ServerVersion.v1_8_R1) ? Sound.valueOf("HORSE_WOOD")
										: Sound.valueOf("ENTITY_ITEMFRAME_PLACE")),
								3.5F, ServerVersion.serverSameVersion(ServerVersion.v1_8_R1) ? 2.0F : 0.7F);
						this.cancel();
						return;
					} else {
						if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
							CompatibleParticles.CRIT.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 8,
									arr.getLocation(), 10000);
						} else {
							CompatibleParticles.CRIT.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 8,
									arr.getLocation(), 10000);
						}
					}
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 2L, 0L);
		}
	}
}
