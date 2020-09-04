package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

@SuppressWarnings("deprecation")
public class Vampire extends ClassItemKit {
	private static final Map<UUID, UUID> bats = new HashMap<UUID, UUID>();
	private String NO_DARK_MESSAGE = ChatColor.RED + "This Can Only be Used in the Dark";
	private static final List<UUID> hidePlayers = new ArrayList<UUID>();

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "NoDarkMessage", NO_DARK_MESSAGE);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			NO_DARK_MESSAGE = section.getString("NoDarkMessage", NO_DARK_MESSAGE);
		}
	}

	@Override
	protected void onInitialize() {
		// Teleport Task
		new BukkitRunnable() {
			@Override
			public void run() {
				for (UUID id : bats.keySet()) {
					// Get and check player
					Player p = Bukkit.getPlayer(id);
					if (!KitUtils.isValidPlayer(p)) {
						continue;
					}

					// Check is valid entity uuid
					UUID batuuid = bats.get(id);
					if (batuuid == null) {
						continue;
					}

					// Get and Telepot Bat
					LivingEntity bat = Util.getLivingEntity(p.getWorld(), batuuid);
					if (bat != null) {
						if (!bat.isDead()) {
							bat.teleport(p.getEyeLocation().clone().add(-0.5D, 0.0D, -0.5D));
						}
					}
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 0);
	}

	@Override
	protected ItemStack specialItem() {
//		ItemStack stack = KitUtils.addClassUndropabbleSoulbound(new ItemStack(Material.MONSTER_EGG, 1, EntityType.BAT.getTypeId()));
		ItemStack stack = KitUtils.addClassUndropabbleSoulbound(new ItemStack(UniversalMaterial.BAT_SPAWN_EGG.getMaterial ( ), 1, EntityType.BAT.getTypeId()));
		ItemMeta meta = KitUtils.getItemMeta(stack);
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Transform";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			return KitUtils.itemNameContains(stack, getSpecialItemName());
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return toVampire(player);
	}

	private boolean toVampire(final Player p) {
		// Check is in the dark
		if (!this.isInTheDark(p)) {
			p.sendMessage(NO_DARK_MESSAGE);
			return false;
		}

		// Remove old bat
		removeBat(p);

		// Set Bat
		final Bat bat = p.getWorld().spawn(p.getEyeLocation().clone().add(-0.5D, 0.0D, -0.5D), Bat.class);

		// Set Modifies
		bat.setAwake(true);
		Util.setNoAI(bat); // Set cant move

		// Save
		bats.put(p.getUniqueId(), bat.getUniqueId());

		// Set not visible for player
		try {
			Object packet = ReflectionUtils.getCraftClass("PacketPlayOutEntityDestroy").getConstructor(int[].class)
					.newInstance(new int[] { bat.getEntityId() });
			ReflectionUtils.sendPacket(p, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// SET PLAYER MODIFIES
		// Hide player for everybody
		for (Player ot : Bukkit.getOnlinePlayers()) {
			if (!KitUtils.isValidPlayer(ot)) {
				continue;
			}

			// Hide
			ot.hidePlayer(p);
		}

		// Add to hide list
		hidePlayers.add(p.getUniqueId());

		// Set Max Health
		p.setMaxHealth(6.0D);

		// Set can fly
		p.setAllowFlight(true);
		p.setFlying(true);

		// Run Task
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new toHumanTask(p), 15 * 20);
		return true;
	}

	private class toHumanTask implements Runnable {
		final UUID id;

		public toHumanTask(final Player p) {
			id = p.getUniqueId();
		}

		@Override
		public void run() {
			AnniPlayer ap = AnniPlayer.getPlayer(id);
			if (KitUtils.isValidPlayer(ap) && hasThisKit(ap)) {
				toHuman(ap.getPlayer());
			}
		}
	}

	private void toHuman(final Player p) {
		// Remove bat
		removeBat(p);

		// Des-hide
		for (Player ot : Bukkit.getOnlinePlayers()) {
			if (!KitUtils.isValidPlayer(ot)) {
				continue;
			}

			// Hide
			ot.showPlayer(p);
		}

		// Remove from hide list
		hidePlayers.remove(p.getUniqueId());

		// Set cant fly
		p.setAllowFlight(false);
		p.setFlying(false);

		// Reset max helth
		p.setMaxHealth(20.0D);
	}

	private void removeBat(final Player owner) {
		final UUID batID = bats.get(owner.getUniqueId());
		if (batID != null) {
			LivingEntity bat = Util.getLivingEntity(owner.getWorld(), batID);
			if (bat != null && !bat.isDead()) {
				bat.remove();
				bats.remove(owner.getUniqueId());
			}
		}
	}

	private boolean isInTheDark(final Player player) {
		final AnniPlayer ap = AnniPlayer.getPlayer(player);
		if (hasThisKit(ap) && Game.isGameRunning() && KitUtils.isOnGameMap(player)) {
			if (!isDay()) {
				// Remove Luck Potis
				if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
					player.removePotionEffect(PotionEffectType.LUCK);
				}

				// Add DAMAGE_RESISTANCE
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
				return true;
			} else {
				// add DAMAGE_RESISTANCE
				if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 0));
				}
				// Remove DAMAGE_RESISTANCE
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				return false;
			}
		}
		return false;
	}

	private boolean isDay() {
		long time = Game.getGameMap().getWorld().getTime();
		return (time >= 0 && time < 12300);
	}

	/**
	 * Add potion effects and check is in the dark
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		if (ap != null && ap.isOnline() && hasThisKit(ap)) {
			if (!this.isInTheDark(ap.getPlayer())) {
				toHuman(ap.getPlayer());
			}
		}
	}

	/**
	 * damage from player to bat
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDam(EntityDamageByEntityEvent eve) {
		if (!(eve.getEntity() instanceof Bat)) {
			return;
		}

		final Bat bat = (Bat) eve.getEntity();
		UUID ownerID = null;
		for (UUID ot : bats.keySet()) {
			if (bat.getUniqueId().equals(bats.get(ot))) {
				ownerID = ot;
				break;
			}
		}

		if (ownerID != null) {
			final Player ow = Bukkit.getPlayer(ownerID);
			final AnniPlayer op = AnniPlayer.getPlayer(ow);
			if (KitUtils.isValidPlayer(ow) && KitUtils.isValidPlayer(op)) {
				if (eve.getDamager() instanceof Player) {
					final Player dam = (Player) eve.getDamager();
					final AnniPlayer ad = AnniPlayer.getPlayer(dam);
					if (KitUtils.isValidPlayer(dam) && KitUtils.isValidPlayer(ad)) {

						// Check is not team mate
						if (op.getTeam().equals(ad.getTeam())) {
							eve.setCancelled(true);
							return;
						}
					}
				} else if (eve.getDamager() instanceof Projectile) {
					final Projectile pr = (Projectile) eve.getDamager();
					final ProjectileSource src = pr.getShooter();
					if (src instanceof Player) {

						final Player dam = (Player) src;
						final AnniPlayer ad = AnniPlayer.getPlayer(dam);
						if (KitUtils.isValidPlayer(dam) && KitUtils.isValidPlayer(ad)) {

							// Check is not team mate
							if (op.getTeam().equals(ad.getTeam())) {
								eve.setCancelled(true);
								return;
							}

							// Set Damager
							ow.damage(eve.getDamage(), dam);
							return;
						}
					}
				}

				// Damage Owner
				ow.damage(eve.getDamage(), eve.getDamager());
			}
		}
	}

	/*
	 * Bat death, kill bat owner
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBD(EntityDeathEvent eve) {
		// Check is a bat
		if (!(eve.getEntity() instanceof Bat)) {
			return;
		}

		final Bat bat = (Bat) eve.getEntity();
		final Player killer = bat.getKiller();
		for (UUID id : bats.keySet()) {
			Player p = Bukkit.getPlayer(id);
			if (!KitUtils.isValidPlayer(p)) {
				continue;
			}

			UUID batID = bats.get(id);
			if (!bat.getUniqueId().equals(batID)) {
				continue;
			}

			// Kill Player
			if (killer != null) {
				Util.setKiller(p, killer);
			}

			p.setLastDamageCause(new EntityDamageEvent(p, bat.getLastDamageCause().getCause(), 50.00));
			p.setHealth(0.0D);
		}
	}

	/**
	 * Anti SUFFOCATION bat damage.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent eve) {
		// Check is SUFFOCATION
		if (eve.getCause() != DamageCause.SUFFOCATION) {
			return;
		}

		// Check is a bat
		if (eve.getEntity() instanceof Bat) {
			final Bat bat = (Bat) eve.getEntity();
			boolean isKitBat = false;

			// Check with all bats
			for (UUID id : bats.values()) {
				if (bat.getUniqueId().equals(id)) {
					isKitBat = true;
					break;
				}
			}

			// Cancell damage
			if (isKitBat) {
				eve.setCancelled(true);
			}
		}
	}

	/**
	 * Hide player on hide player list!
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJ(PlayerJoinEvent eve) {
		final Player p = eve.getPlayer();
		for (UUID id : hidePlayers) {
			if (id == null) {
				continue;
			}

			Player o = Bukkit.getPlayer(id);
			if (!KitUtils.isValidPlayer(o)) {
				continue;
			}

			p.hidePlayer(o);
		}
	}

	/**
	 * Cancell kick event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent eve) {
		if (Game.isNotRunning()) { 
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		if (ap != null && ap.isOnline() && hasThisKit(ap)) {
			if (eve.getReason().equalsIgnoreCase("Flying is not enabled on this server")
					&& eve.getPlayer().getAllowFlight()) {
				eve.setCancelled(true);
			}
		}
	}

	@Override
	public void cleanup(Player p) {
		if (KitUtils.isValidPlayer(p)) {
			// Remove DAMAGE_RESISTANCE
			p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

			// Remove Luck
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				p.removePotionEffect(PotionEffectType.LUCK);
			}

			// To Human
			toHuman(p);
		}
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
		if (KitUtils.isValidPlayer(p)) {
			toHuman(p);
		}
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 60 * 1000;
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
		return "Vampire";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.REDSTONE.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the fear.!", aqua + "", aqua + "Your resistance makes",
				aqua + "you a feared opponent", aqua + "", aqua + "Transform into a bat", aqua + "for a sneak",
				aqua + "Attack or Escape ", aqua + "a Stake!!!");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe()
				.addSoulboundItem(new Potion(PotionType.NIGHT_VISION).toItemStack(1)).addItem(super.getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer arg1) {
		this.addLoadoutToInventory(inv);
		return false;
	}
}
