package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.events.Assasin_LeapEvent;

public class Assassin extends ClassItemKit {
	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack leap = KitUtils.addClassUndropabbleSoulbound ( new ItemStack ( UniversalMaterial.FEATHER.getMaterial ( ) ) );
		ItemMeta meta = leap.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + " " + instance.getReadyPrefix());
		leap.setItemMeta(meta);
		return leap;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.AQUA + "Leap";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void damageHandler(EntityDamageByEntityEvent event) {
		if (event.getDamager().getType() == EntityType.PLAYER) {
			final Player player = (Player) event.getDamager();
			final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
			if (p != null && p.getKit().equals(this) && p.getData("Cur") != null)
				endLeap(player, p);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void damageHandler(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final AnniPlayer p = AnniPlayer.getPlayer(((Player) event.getEntity()).getUniqueId());
			if (p != null && p.getKit().equals(this) && p.getData("Cur") != null) {
				if (event.getCause() == DamageCause.FALL)
					event.setCancelled(true);
				else
					endLeap((Player) event.getEntity(), p);
			}
		}
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		final ItemStack[] ab = new ItemStack[4];
		final PlayerInventory inv = player.getInventory();
		for (int x = 0; inv != null && x < 4; x++) {
			switch (x) {
				case 0: {
					ab[x] = inv.getHelmet();
					break;
				}
				case 1: {
					ab[x] = inv.getChestplate();
					break;
				}
				case 2: {
					ab[x] = inv.getLeggings();
					break;
				}
				case 3: {
					ab[x] = inv.getBoots();
					break;
				}
			}
		}
		//
		p.setData("Arm", ab);
		p.setData("Cur", true);
		//
		Assasin_LeapEvent eve = new Assasin_LeapEvent(p, ab);
		eve.addPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 8 * 20, 0));
		eve.addPotion(new PotionEffect(PotionEffectType.SPEED, 8 * 20, 0));
		eve.addPotion(new PotionEffect(PotionEffectType.FAST_DIGGING, 8 * 20, 1));
		AnniEvent.callEvent(eve);
		if (eve.isCancelled())
			return false;

		if (eve.addPotions() && !eve.getPotions().isEmpty())
			player.addPotionEffects(eve.getPotions());

		player.getInventory().setArmorContents(null);
		player.updateInventory();
		player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 4);
		player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
		player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 2);
		player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 3);
		player.setVelocity(player.getLocation().getDirection().setY(1).multiply(eve.getHeight()));
		new EndLeap(player, p).runTaskLater(AnnihilationMain.INSTANCE, eve.getBukupRestoreCoutdown() * 20);
		p.setData("leapeventkey", eve);

		return true;
	}

	private void endLeap(Player player, AnniPlayer p) {
		if (player != null && p != null && p.getData("Cur") != null) {
			Object vd = p.getData("leapeventkey");
			if (vd == null)
				return;
			//
			Assasin_LeapEvent eve = ((Assasin_LeapEvent) vd);
			//
			if (!eve.isCancelled()) {
				Object obj = p.getData("Arm");
				if (eve.getRestoreBackup() && obj != null && obj instanceof ItemStack[]) {
					final PlayerInventory inv = player.getInventory();
					if (inv != null) {
						ItemStack[] ab = ((ItemStack[]) obj);
						if (ab != null) {
							if (inv.getHelmet() == null && ab[0] != null)
								inv.setHelmet(ab[0]);
							if (inv.getChestplate() == null && ab[1] != null)
								inv.setChestplate(ab[1]);
							if (inv.getLeggings() == null && ab[2] != null)
								inv.setLeggings(ab[2]);
							if (inv.getBoots() == null && ab[3] != null)
								inv.setBoots(ab[3]);
						}
					}
				}
				//
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
				player.removePotionEffect(PotionEffectType.SPEED);
				player.removePotionEffect(PotionEffectType.FAST_DIGGING);
			}
			p.setData("Arm", null);
			p.setData("Cur", null);
		}
	}

	private class EndLeap extends BukkitRunnable {
		private final Player player;
		private final AnniPlayer p;

		public EndLeap(Player player, AnniPlayer p) {
			this.player = player;
			this.p = p;
		}

		@Override
		public void run() {
			endLeap(player, p);
		}
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
	protected String getInternalName() {
		return "Assassin";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.GOLDEN_SWORD.getMaterial ( ) , 1 );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are the Shadow!.", "", aqua + "Use your Ability to Leap",
				aqua + "Forward While Gaining", aqua + "Speed and Invisibility", aqua + "For Six Seconds.");
		return l;
	}

	@Override
	public void cleanup(Player player) {
		if (player != null && player.isOnline()) {

			// Remove Potions Effect
			for (PotionEffectType pts : PotionEffectType.values()) {
				if (pts == null) {
					continue;
				}

				if (pts.equals(PotionEffectType.SPEED) || pts.equals(PotionEffectType.INVISIBILITY)
						|| pts.equals(PotionEffectType.FAST_DIGGING)) {
					player.removePotionEffect(pts);
				}
			}

			// Remove Data
			final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
			if (ap != null) {
				ap.setData("Arm", null);
				ap.setData("Cur", null);
			}
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(getSpecialItem());
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
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
