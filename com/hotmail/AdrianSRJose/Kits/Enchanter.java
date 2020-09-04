package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Enchanter extends ClassItemKit {
	
	private static final Random rand = new Random(System.currentTimeMillis());
	private int aoeDuration = 30;
	private int aoeRadius = 10;
	private float aoeXpMultiplier = 3.5F;
	private float chanceToIncreaseEnchant = 0.45F;
	private float selfXpMultiplier = 5.0F; // 10.0
//	private double divideRegenerationTimeBy = 1.5;
	private static final String ITEM_NAME = ChatColor.YELLOW + "Intensifier";
	private static final String META_KEY = "ENCHANTER_ACTIVE";

	@Override
	protected void onInitialize() {
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player player = (Player) e.getEntity();
			final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
			if (hasThisKit(ap) && player.getHealth() - e.getDamage() <= 1.0D) {
				int damage = (int) Math.ceil(e.getDamage());
				if (player.getLevel() >= damage) {
					player.setLevel(Math.max((player.getLevel() - 10), 0));
					e.setDamage(0.0D);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerEnchantItemEvent(EnchantItemEvent event) {
		final Player player = event.getEnchanter();
		final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
		if (ap != null && hasThisKit(ap)) {
			final Map<Enchantment, Integer> enchantments = event.getEnchantsToAdd();
			for (Enchantment enchant : enchantments.keySet()) {
				int enchantLevel = enchantments.get(enchant).intValue();
				if ((enchantLevel + 1 <= enchant.getMaxLevel()) && (rand.nextFloat() <= this.chanceToIncreaseEnchant)) {
					enchantments.put(enchant, Integer.valueOf(enchantLevel + 1));

					// TODO: Check (can must have to remove)
					player.sendMessage(ChatColor.AQUA + "Your Enchanter class allowed you to get a higher level "
							+ ap.getTeam().getColor() + enchant.getName() + ChatColor.AQUA + " enchantment!");
				}
			}
		}
	}

	// Increase the xp gained from mining blocks and potentially gives you an XP
	// bottle (1% chance)
	@EventHandler
	public void onResourceBreak(final ResourceBreakEvent event) {
		// add
		if (hasThisKit(event.getPlayer())) {
			int xp = event.getXP();
			if (xp > 0) {
				xp = (int) Math.ceil(xp * this.selfXpMultiplier);
				event.setXP(xp);
			}

			// Add EXP_BOTTLE
			if (rand.nextInt(3) == 1) {
				final Player pl = event.getPlayer().getPlayer();
				int i = rand.nextInt(2) + 1;
				pl.getInventory().addItem(new ItemStack ( UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( ), i));
			}
		} else if (isNearActive(event.getPlayer().getPlayer())) {
			int xp = event.getXP();
			if (xp > 0) {
				xp = (int) Math.ceil(xp * this.aoeXpMultiplier);
				event.setXP(xp);
			}
		}

		// Set New Time
//		if (hasThisKit(event.getPlayer())) {
//			final int oldTime = event.getRegenerationTime();
//			event.setRegenerationTime((int) (oldTime / divideRegenerationTimeBy));
//		}
	}

	private boolean isNearActive(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null && !p.getUniqueId().equals(player.getUniqueId()) && p.hasMetadata(META_KEY)
					&& p.getWorld().equals(player.getWorld())
					&& p.getLocation().distanceSquared(player.getLocation()) <= this.aoeRadius * this.aoeRadius) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack item = KitUtils.addClassUndropabbleSoulbound(new ItemStack(UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( )));
		return KitUtils.setName(item, getSpecialItemName() + " " + instance.getReadyPrefix());
	}

	@Override
	protected String defaultSpecialItemName() {
		return ITEM_NAME;
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isSpecial(stack, true)) {
			if (KitUtils.itemNameContains(stack, getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (p.hasTeam()) {
			// Set Metadata
			player.setMetadata(META_KEY, new FixedMetadataValue(AnnihilationMain.INSTANCE, Boolean.valueOf(true)));

			// Remove Metadata
			Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					player.removeMetadata(META_KEY, AnnihilationMain.INSTANCE);
				}
			}, this.aoeDuration * 20);

			// Cancell Event
			event.setCancelled(true);
			player.updateInventory();
			return true;
		}
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
	protected int setInConfig(ConfigurationSection section) {
		return 0; //Util.setDefaultIfNotSet(section, "Divide-Blocks-Regeneration-Time-By", 1.5);
	}

	@Override
	protected String getInternalName() {
		return "Enchanter";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.EXPERIENCE_BOTTLE.getMaterial ( ));
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
//		divideRegenerationTimeBy = section.getDouble("Divide-Blocks-Regeneration-Time-By", 1.5);
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the master.", "", aqua + "You get Extra Xp", aqua + "resources which enables",
				aqua + "quicker level succession.", aqua + "", aqua + "There is a small chance",
				aqua + "to obtain experience bottles", aqua + "when mining ores and chopping", aqua + "wood.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(this.getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player p) {
		if (KitUtils.isValidPlayer(p) && p.hasMetadata(META_KEY)) {
			p.removeMetadata(META_KEY, AnnihilationMain.INSTANCE);
		}
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer a) {
		this.addLoadoutToInventory(inv);
		return true;
	}
}
