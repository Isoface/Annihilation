package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerKilledEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Enginner extends ClassItemKit {
	private static final Map<AnniTeam, List<Location>> blocks = new HashMap<AnniTeam, List<Location>>();

	@Override
	protected void onInitialize() {
		for (AnniTeam team : AnniTeam.Teams) {
			blocks.put(team, new ArrayList<Location>());
		}
	}

	@Override
	protected ItemStack specialItem() {
		final ItemStack tor = new ItemStack ( UniversalMaterial.TNT.getMaterial ( ), 1);
		return KitUtils
				.addClassUndropabbleSoulbound(KitUtils.setName(tor, getSpecialItemName() + instance.getReadyPrefix()));
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Bunker Buster";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			return KitUtils.itemNameContains(stack, getSpecialItemName());
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) 
		{
			// Get and Check Block
			final Block b = event.getClickedBlock();
			if (b != null && b.getType().isSolid()) {
				// Get TNT
				final TNTPrimed tnt = p.getWorld().spawn(b.getLocation().clone().add(0.0D, 1.0D, 0.0D),
						TNTPrimed.class);
				tnt.setIsIncendiary(true);
				tnt.setMetadata("Engineer", new FixedMetadataValue(AnnihilationMain.INSTANCE, ap.getTeam().getName()));

				// Start Effects Task
				new BukkitRunnable() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						if (tnt == null || tnt.isDead()) {
							cancel();
							return;
						}

						// Play Effect
						if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
							CompatibleParticles.ANGRY_VILLAGER.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 1.0F, 4,
									tnt.getLocation().clone().add(0.5D, 1.0D, 0.5D), 120D);
						} else {
							CompatibleParticles.ANGRY_VILLAGER.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 1.0F, 4,
									tnt.getLocation().clone().add(0.5D, 1.0D, 0.5D), 120D);
						}

						tnt.getLocation().getWorld().playSound(tnt.getLocation(), UniversalSound.FIZZ.asBukkit(),
								4.0F, 2.0F);
					}
				}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);

				// Update Player inv
				p.updateInventory();
			}
			// READY
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onE(final EntityExplodeEvent eve) {
		// Check is TNT
		if (!(eve.getEntity() instanceof TNTPrimed)) {
			return;
		}

		// Get and Check TNT
		final TNTPrimed tnt = (TNTPrimed) eve.getEntity();
		if (!tnt.hasMetadata("Engineer")) {
			return;
		}

		// Get and Check team
		final AnniTeam team = AnniTeam.getTeamByName(tnt.getMetadata("Engineer").get(0).asString());
		if (team == null) {
			return;
		}

		// Create list
		final List<Block> toAdd = new ArrayList<Block>();
		for (Block b : eve.blockList()) {
			if (b == null) {
				continue;
			}

			for (AnniTeam t : AnniTeam.Teams) {
				List<Location> ls = blocks.get(t);
				if (ls == null) {
					continue;
				}

				if (!t.equals(team) && ls.contains(b.getLocation())) {
					toAdd.add(b);
				}
			}
		}

		// Update list
		eve.blockList().clear();
		eve.blockList().addAll(toAdd);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onD(PlayerKilledEvent eve) {
		final AnniPlayer ap = eve.getKiller();
		final AnniPlayer ep = eve.getPlayer();
		if (!ap.hasTeam() || !ep.hasTeam()) {
			return;
		}

		if (hasThisKit(ap)) {
			ep.getPlayer().getWorld().playSound(ep.getPlayer().getLocation(), UniversalSound.EXPLODE.asBukkit(),
					2.0F, 0.0F);

			for (Entity ent : ep.getPlayer().getNearbyEntities(4.0D, 4.0D, 4.0D)) {
				if (!(ent instanceof Player)) {
					continue;
				}

				Player e = (Player) ent;
				AnniPlayer p = AnniPlayer.getPlayer(e);
				if (p != null && p.isOnline() && !ep.getTeam().equals(p.getTeam())) {
					e.damage(2.0D, ep.getPlayer());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onE(EntityDamageByEntityEvent eve) {
		if (!(eve.getDamager() instanceof TNTPrimed)) {
			return;
		}

		if (!(eve.getEntity() instanceof Player)) {
			return;
		}

		final Player p = (Player) eve.getEntity();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final TNTPrimed tnt = (TNTPrimed) eve.getDamager();
		if (!tnt.hasMetadata("Engineer") || ap == null || !ap.isOnline() || !ap.hasTeam()) {
			return;
		}

		final AnniTeam tntTeam = AnniTeam.getTeamByName(tnt.getMetadata("Engineer").get(0).asString());
		if (ap.getTeam().equals(tntTeam)) {
			eve.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onP(BlockPlaceEvent eve) {
		if (eve.isCancelled()) {
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		final Block b = eve.getBlock();
		if (!b.getType().isSolid() || ap == null || !ap.hasTeam()) {
			return;
		}

		final AnniTeam team = ap.getTeam();
		if (!blocks.get(team).contains(b.getLocation())) {
			blocks.get(team).add(b.getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onB(BlockBreakEvent eve) {
		if (eve.isCancelled()) {
			return;
		}

		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		final Block b = eve.getBlock();
		if (ap == null || !ap.hasTeam()) {
			return;
		}

		final AnniTeam team = ap.getTeam();
		blocks.get(team).remove(b.getLocation());
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 30 * 1000;
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
		return 0;
	}

	@Override
	protected String getInternalName() {
		return "Enginner";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.REDSTONE_BLOCK.getMaterial ( ) );
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are destruction.", "", aqua + "An expert in controlled demolitions,",
				aqua + "you are responsible for the destruction ", aqua + "of enemy defenses.", "",
				aqua + "Use your Bunker Buster", aqua + "to destroy blocks placed by enemies.", "",
				aqua + "If you are killed, have the", aqua + "last laugh with Martyrdom.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addItem(getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player p) {
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer arg1) {
		addLoadoutToInventory(inv);
		return true;
	}
}
