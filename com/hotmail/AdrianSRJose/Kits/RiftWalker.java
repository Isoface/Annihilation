package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.base.Delays;

public class RiftWalker extends ClassItemKit {
	public static String riftItemName      = ChatColor.GOLD + "Open Rift";
	public static String oneRiftAtTime     = ChatColor.RED + "You may only open one rift at a time!";
	public static String leftRiftMessage   = "Rift closed: rift walker left the rift.";
	public static String menuTitle         = "Open Rift To Whom?";
	public static String toBase            = "To base";
	public static String Base              = "base";
	public static String UnableToBeLocated = ChatColor.RED + "%w's rift was unable to be located.";
	public static String CannotBeLocated   = ChatColor.RED + "%w's rift cannot be located!";
	public static String ProximityToANexus = ChatColor.RED + "%w's rift is being obscured by their proximity to a nexus!";
	public static String openingRift       = ChatColor.GOLD + "Rift to %w" + ChatColor.GOLD + " opens in %#";
	public static String openingRiftOther  = ", hold shift to travel in this rift!";
	public static String youAreWeak        = ChatColor.GOLD + "You are weak from travelling across the rift.";
	public static String noSneaking        = ChatColor.RED + "You did not travel with the rift since you were not sneaking.";
	public static int cooldownPerPlayer    = 30;
	public static transient final String META_KEY = "rift";
	private RiftSelector riftSelector;
	
	public RiftWalker() {
	}

	@Override
	protected void onInitialize() {
		riftSelector = new RiftSelector(menuTitle, UnableToBeLocated, toBase, Base, ProximityToANexus);
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "OneRiftAtTime", Util.untranslateAlternateColorCodes(oneRiftAtTime))
				+ Util.setDefaultIfNotSet(section, "LeftRiftMessage", Util.untranslateAlternateColorCodes(leftRiftMessage))
				+ Util.setDefaultIfNotSet(section, "MenuTitle", menuTitle)
				+ Util.setDefaultIfNotSet(section, "ToBase", toBase)
				+ Util.setDefaultIfNotSet(section, "Base", Base)
				+ Util.setDefaultIfNotSet(section, "CannotBeLocated", Util.untranslateAlternateColorCodes(CannotBeLocated))
				+ Util.setDefaultIfNotSet(section, "UnableToBeLocated", Util.untranslateAlternateColorCodes(UnableToBeLocated))
				+ Util.setDefaultIfNotSet(section, "ProximityToANexus", Util.untranslateAlternateColorCodes(ProximityToANexus))
				+ Util.setDefaultIfNotSet(section, "OpeningRift", Util.untranslateAlternateColorCodes(openingRift))
				+ Util.setDefaultIfNotSet(section, "OpeningRiftOtherPlayers", openingRiftOther)
				+ Util.setDefaultIfNotSet(section, "YouAreWeak", Util.untranslateAlternateColorCodes(youAreWeak))
				+ Util.setDefaultIfNotSet(section, "NoSneaking", Util.untranslateAlternateColorCodes(noSneaking));
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		oneRiftAtTime     = Util.wc(section.getString("OneRiftAtTime", oneRiftAtTime));
		leftRiftMessage   = Util.wc(section.getString("LeftRiftMessage", leftRiftMessage));
		menuTitle         = Util.wc(section.getString("MenuTitle", menuTitle));
		toBase            = Util.wc(section.getString("ToBase", toBase));
		Base              = Util.wc(section.getString("Base", Base));
		UnableToBeLocated = Util.wc(section.getString("UnableToBeLocated", UnableToBeLocated));
		CannotBeLocated   = Util.wc(section.getString("CannotBeLocated", CannotBeLocated));
		ProximityToANexus = Util.wc(section.getString("ProximityToANexus", ProximityToANexus));
		openingRift       = Util.wc(section.getString("OpeningRift", openingRift));
		openingRiftOther  = Util.wc(section.getString("OpeningRiftOtherPlayers", openingRiftOther));
		youAreWeak        = Util.wc(section.getString("YouAreWeak", youAreWeak));
		noSneaking        = Util.wc(section.getString("NoSneaking", noSneaking));
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack riftOpen = new ItemStack ( UniversalMaterial.BLAZE_ROD.getMaterial ( ) , 1);
		return KitUtils.addClassUndropabbleSoulbound(
				KitUtils.setName(riftOpen, getSpecialItemName() + instance.getReadyPrefix()));
	}

	@Override
	protected String defaultSpecialItemName() {
		return riftItemName;
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			if (KitUtils.itemNameContains(stack, this.getSpecialItemName())) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteract(final PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		final AnniPlayer user = AnniPlayer.getPlayer(player.getUniqueId());
		if (hasThisKit(user)) {
			if (isSpecialItem(e.getItem())) {
				e.setCancelled(true);

				// Check dont have delay
				if (Delays.getInstance().hasActiveDelay(player, getInternalName())) {
					return;
				}

				// add Metadata
				if (player.hasMetadata(META_KEY)) {
					player.sendMessage(oneRiftAtTime);
					return;
				}

				riftSelector.open(e.getPlayer());
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						player.updateInventory();
					}
				}, 3);
			}
		}
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (p.hasMetadata(META_KEY)) {
			Object riftObj = Rift.fromPlayer(p);
			Rift r1 = null;
			NRift r2 = null;

			if (riftObj instanceof Rift) {
				r1 = (Rift) riftObj;
			} else if (riftObj instanceof NRift) {
				r2 = (NRift) riftObj;
			}

			if (r1 == null && r2 == null) {
				return;
			}

			if (r1 != null) {
				if (!r1.contains(p.getLocation())) {
					r1.cancel(leftRiftMessage);
				}
			} else if (r2 != null) {
				if (!r2.contains(p.getLocation())) {
					r2.cancel(leftRiftMessage);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(final PlayerDeathEvent e) {
		final Player p = e.getEntity();
		if (p.hasMetadata(META_KEY)) {
			Object riftObj = Rift.fromPlayer(p);
			Rift r1 = null;
			NRift r2 = null;

			if (riftObj instanceof Rift) {
				r1 = (Rift) riftObj;
			} else if (riftObj instanceof NRift) {
				r2 = (NRift) riftObj;
			}

			if (r1 == null && r2 == null) {
				return;
			}

			if (r1 != null) {
				r1.cancel(leftRiftMessage);
			} else if (r2 != null) {
				r2.cancel(leftRiftMessage);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCraft(final CraftItemEvent eve) {
		final ItemStack result = eve.getRecipe().getResult();
		if (result == null || result.getType() != UniversalMaterial.BLAZE_POWDER.getMaterial ( ) ) {
			return;
		}

		for (ItemStack itemStack : eve.getInventory().getContents()) {
			if (itemStack == null) {
				continue;
			}

			if (isSpecialItem(itemStack)) {
				eve.setResult(Event.Result.DENY);
				eve.setCancelled(true);
			}
		}
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer user, PlayerInteractEvent event) {
		if (player != null && user == null && event == null) {
			if (!(delay > 0)) {
				return true;
			}

			delays.addDelay(player, System.currentTimeMillis() + delay, getInternalName());
			return true;
		}
		return false;
	}

	public void doDelay(final Player p) {
		this.performPrimaryAction(p, null, null);
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 50000;
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
		return "RiftWalker";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack ( UniversalMaterial.BLAZE_ROD.getMaterial ( ) , 1);
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> tor = new ArrayList<String>();
		this.addToList(tor, aqua + "You are the traveller.", aqua + "", aqua + "Interdimensional travel",
				aqua + "is a cakewalk for you.", aqua + "", aqua + "You have the ability to",
				aqua + "teleport to any teammates", aqua + "not near an enemy Nexus", aqua + "and bring up to 2 other",
				aqua + "allies with you.", aqua + "", aqua + "You'll be weak after rifting.");
		return tor;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addItem(super.getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
		if (p.hasMetadata(META_KEY)) {
			p.removeMetadata(META_KEY, AnnihilationMain.INSTANCE);
		}
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	public boolean onItemClick(Inventory i, AnniPlayer arg1) {
		this.addLoadoutToInventory(i);
		return true;
	}
}
