package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Sniper extends ClassItemKit {
	private static String ITEM_NAME   = ChatColor.GOLD + "Compound Bow";
	private static String NORMAL_MODE = "(Normal)";
	private static String SNIPER_MODE = "(Sniper)";

	@Override
	protected void onInitialize() {
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		ITEM_NAME   = Util.wc(section.getString("SpecialItemName", ITEM_NAME));
		NORMAL_MODE = Util.wc(section.getString("NormalMode", NORMAL_MODE)).trim();
		SNIPER_MODE = Util.wc(section.getString("SniperMode", SNIPER_MODE)).trim();
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "NormalMode", NORMAL_MODE)
				+ Util.setDefaultIfNotSet(section, "SniperMode", SNIPER_MODE);
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack item = KitUtils.addClassSoulbound(new ItemStack(UniversalMaterial.BOW.getMaterial()));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getSpecialItemName());
		item.setItemMeta(meta);
		return item;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ITEM_NAME;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		final AnniPlayer ap = AnniPlayer.getPlayer(e.getPlayer().getUniqueId());
		if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && hasThisKit(ap)) {

			if (e.getItem() == null) {
				return;
			}

			// Check is a bow
			if (e.getItem().getType() != UniversalMaterial.BOW.getMaterial()) {
				return;
			}

			// Check is Assasin or Suppression mode
			if (sniperMode(e.getItem())) {
				e.getPlayer().setItemInHand(KitUtils.setName(e.getItem(), ChatColor.GOLD + ITEM_NAME + " " + NORMAL_MODE));
			} else {
				e.getPlayer().setItemInHand(KitUtils.setName(e.getItem(), ChatColor.GOLD + ITEM_NAME + " " + SNIPER_MODE));
			}

			// Cancell interaction
			e.setCancelled(true);

			// Update Inventory
			e.getPlayer().updateInventory();
		}
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		// Check is Player
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		// Check is full Force
		final Player player = (Player) e.getEntity();
		final ItemStack bow = e.getBow();
		final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());

		// Check is Assasin Shoot
		if (hasThisKit(ap) && sniperMode(bow)) {
			// Check is full Force
			if (e.getForce() < 1.0) {
				e.setCancelled(true);
				player.updateInventory();
				return;
			}

			// Get Target Block
			final Entity pr = e.getProjectile();
			if (pr == null || pr.isDead()) {
				return;
			}

			// Set Assasin Mode
			setConstantVelocity(pr.getLocation(), pr, pr.getVelocity());

			// Get Bow Damage
			short bowDamage = 0;
			if (bow.getEnchantments() != null && !bow.getEnchantments().isEmpty()) {
				if (bow.containsEnchantment(Enchantment.ARROW_INFINITE)) {
					bowDamage += 10;
				}

				if (bow.containsEnchantment(Enchantment.DURABILITY)) {
					int enlev = bow.getEnchantmentLevel(Enchantment.DURABILITY);
					bowDamage += enlev;
				}
			} else {
				bowDamage += 5;
			}

			// Set durability
			bow.setDurability((short) (bow.getDurability() + bowDamage));
			player.updateInventory();
		}
	}

	public void setConstantVelocity(final Location startLoc, final Entity pro, final Vector vel) {
		new BukkitRunnable() {
			private int lasDistance = 0;

			@Override
			public void run() {
				// Chcek is not null
				if (pro == null) {
					cancel();
					return;
				}

				// Check is not on ground
				if (pro.isOnGround()) {
					cancel();
					return;
				}

				// Get Distance
				int distance = Integer.valueOf((int) startLoc.distance(pro.getLocation())).intValue();
				if (distance >= 500) {
					pro.remove();
					cancel();
					return;
				}

				// Check is could not saw
				if ((distance > 10) && distance == lasDistance) {
					pro.remove();
					cancel();
					return;
				}

				// Set velocity and save last distance
				lasDistance = distance;
				pro.setVelocity(vel);
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 0);
	}

	private boolean sniperMode(ItemStack item) {
		if (!KitUtils.isSpecial(item, false)) {
			return false;
		}

		String[] split = item.getItemMeta().getDisplayName().split(" ");
		if (split.length > 2) {
			String s = split[2];
			return SNIPER_MODE.equals(s);
		}
		return false;
	}
	
	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassSoulbound(stack) && KitUtils.itemNameContains(stack, getSpecialItemName())) {
			return true;
		}
		return false;
	}

	@Override
	protected String getInternalName() {
		return "Sniper";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.BOW.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> tor = new ArrayList<String>();
		this.addToList(tor, aqua + "You are the rain.", aqua + "", aqua + "A sniper is trained in the",
				aqua + "art of picking off their foes", aqua + "from a distance.", aqua + "",
				aqua + "Fire arrows with increased", aqua + "velocity and without the", aqua + "effects of gravity.",
				aqua + "", aqua + "Using the class ability will", aqua + "increase arrow usage and durability",
				aqua + "cost on your bow.");
		return tor;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel().addItem(getSpecialItem())
				.addItem(KitUtils.addClassSoulbound(new ItemStack(UniversalMaterial.ARROW.getMaterial(), 32)));
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer arg1) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 0;
	}

	@Override
	protected boolean useDefaultChecking() {
		return false;
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
