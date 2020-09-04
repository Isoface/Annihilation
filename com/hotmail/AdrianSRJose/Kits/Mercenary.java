package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Mercenary extends ClassItemKit {
	
	/**
	 * Global class values.
	 */
	private static final Map<UUID, Integer> TASKS = new HashMap<UUID, Integer>();
	private static final List<UUID>       VICTIMS = new ArrayList<UUID>();
	private static final List<UUID>           IND = new ArrayList<UUID>();
	private static       String        MARK_TITLE = ChatColor.RED   + "You has been mark!";
	private static       String     DESMARK_TITLE = ChatColor.GREEN + "You has been desmark!";
	private static final Material SKULL_ITEM = UniversalMaterial.WITHER_SKELETON_SKULL_ITEM.getMaterial ( );

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "mark-title",    MARK_TITLE)
			 + Util.setDefaultIfNotSet(section, "desmark-title", DESMARK_TITLE);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		MARK_TITLE    = section.getString("mark-title",    MARK_TITLE);
		DESMARK_TITLE = section.getString("desmark-title", DESMARK_TITLE);
	}
	
	@Override
	protected void onInitialize() {
		// noting.
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack(SKULL_ITEM);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Mark of Death";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName())
					&& KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		// get and check victim.
		final Player victim = instance.getPlayerInSight(p, 16);
		if (victim != null) {
			// get and check victim Anni Player.
			final AnniPlayer vp = AnniPlayer.getPlayer(victim.getUniqueId());
			if (vp == null || vp.getTeam() == null || ap == null || ap.getTeam() == null) {
				return false;
			}

			// check is not same team.
			if (vp.getTeam().equals(ap.getTeam())) {
				return false;
			}

			// register as victim.
			if (!VICTIMS.contains(victim.getUniqueId())) {
				VICTIMS.add(victim.getUniqueId());
			}

			// register as marked player.
			if (!IND.contains(victim.getUniqueId())) {
				IND.add(victim.getUniqueId());
			}

			// save armor.
			vp.setData("MerceArmorCopy", victim.getInventory().getArmorContents().clone());

			// add potion effect if is not in 1.8, else remove armor.
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1))
				victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15 * 20, 1));
			else {
				// set Team armor
				new Loadout().setTeamArmor(victim);
			}

			// set helmet and update inventory.
			victim.getInventory().setHelmet(KitUtils.addClassSoulbound(new ItemStack(SKULL_ITEM)));
			victim.updateInventory();

			// send marked title.
			Util.sendTitle(victim, MARK_TITLE, ""); // subtitle

			// play line particle effects.
			final Location ploc = p.getLocation();
			final Vector vector = ploc.getDirection(); // new Vector();

			lin(vector, p, victim); // getting line to show particels in his path.

			// start restore armor task.
			TASKS.put(victim.getUniqueId(),
					Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
						@Override
						public void run() {
							// remove from marked players.
							if (IND.contains(victim.getUniqueId())) {
								IND.remove(victim.getUniqueId());
							}

							// send des-marked message.
							Util.sendTitle(victim, DESMARK_TITLE, "");

							// restor armor.
							Object obj = vp.getData("MerceArmorCopy");
							if (obj != null) {
								ItemStack[] armor = (ItemStack[]) obj;
								victim.getInventory().setHelmet(armor[3]);
								victim.getInventory().setChestplate(armor[2]);
								victim.getInventory().setLeggings(armor[1]);
								victim.getInventory().setBoots(armor[0]);
								victim.updateInventory();
								vp.setData("MerceArmorCopy", null);
							}

							// remove tasks from tasks list.
							TASKS.remove(victim.getUniqueId());
						}
					}, 15 * 20).getTaskId());

			// start marked effect task.
			new BukkitRunnable() {
				@Override
				public void run() {
					// check victim and play effect.
					if (victim != null && victim.isOnline() && !victim.isDead() && IND.contains(victim.getUniqueId())) {
						if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
							CompatibleParticles.UNDERWATER.displayNewerVersions().display(0.3F, 1.2F, 0.3F, 6.0F, 10,
									victim.getLocation().clone(), 10000.0D);
						} else {
							CompatibleParticles.UNDERWATER.displayOlderVersions().display(0.3F, 1.2F, 0.3F, 6.0F, 10,
									victim.getLocation().clone(), 10000.0D);
						}
						return;
					}

					// cancel task.
					cancel();
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);

			// shoot firwork at victim location.
			fireworkToLocation(victim.getLocation());

			// play sound effect.
			p.playSound(p.getLocation(), UniversalSound.ZOMBIE_INFECT.asBukkit(), 4.0F, 2.0F);

			// remove victim from victims list.
			Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					if (VICTIMS.contains(victim.getUniqueId())) {
						VICTIMS.remove(victim.getUniqueId());
					}
				}
			}, 10 * 20);
			return true;
		}
		return false;
	}

	
	private static void lin(final Vector vect, Player player, final Player vict) {
		double t = 0;
		final Location loc = player.getLocation().clone();
		final Vector direction = vect.normalize();
		final Location locl = vict.getLocation().clone();

		while (t < player.getLocation().distance(locl)) {
			t = t + 0.5;
			double x = direction.getX() * t;
			double y = direction.getY() * t + 1.25;
			double z = direction.getZ() * t;
			loc.add(x, y, z);
			
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				CompatibleParticles.UNDERWATER.displayNewerVersions().display(0.2F, 0.2F, 0.2F, 0.1F, 6, loc, 10000.0D);
			} else {
				CompatibleParticles.UNDERWATER.displayOlderVersions().display(0.2F, 0.2F, 0.2F, 0.1F, 6, loc, 10000.0D);
			}
			
			loc.subtract(x, y, z);
		}
	}
	
	private static void fireworkToLocation(Location loc) {
		Firework f = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fMeta = f.getFireworkMeta();

		FireworkEffect.Builder builder = FireworkEffect.builder();
		builder.withFlicker();
		builder.withFade(Color.RED);
		builder.withColor(Color.RED);
		builder.with(FireworkEffect.Type.CREEPER);

		fMeta.addEffect(builder.build());
		fMeta.setPower(1);
		f.setFireworkMeta(fMeta);
	}

	/**
	 * On damage marked a
	 * player.
	 */
	@EventHandler
	public void onH(EntityDamageByEntityEvent eve) {
		// check damager and damager are players.
		if (eve.getEntity() instanceof Player 
				&& eve.getDamager() instanceof Player) {
			// get victim and check is marked.
			final Player p = (Player) eve.getEntity();
			if (!VICTIMS.contains(p.getUniqueId())) {
				return;
			}

			// change damage.
			eve.setDamage(eve.getDamage() + (10 * eve.getDamage() / 100));
		}
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
		return "Mercenary";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(SKULL_ITEM);
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the Soldier", "", aqua + "Mark enemies for death,",
				aqua + "encouraging focus fire ", aqua + "from your team.", aqua + "",
				aqua + "Marked enemies take increased", aqua + "damage and glow,", "", aqua + "providing your team the",
				aqua + "ability to easily track", aqua + "them down.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(getSpecialItem())
				.setUseDefaultArmor(true)
				.setArmor(3, KitUtils.addClassSoulbound ( new ItemStack ( UniversalMaterial.CHAINMAIL_HELMET.getMaterial ( ) ) ) );
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player paramPlayer) {
		// check contains restore task.
		if (TASKS.containsKey(paramPlayer.getUniqueId())) {
			// cancel task.
			Bukkit.getScheduler().cancelTask(TASKS.get(paramPlayer.getUniqueId()));
			TASKS.remove(paramPlayer.getUniqueId());
			
			// drop Armor
			final AnniPlayer ap = AnniPlayer.getPlayer(paramPlayer);
			if (ap != null && ap.isOnline()) {
				final Location ploc = ap.getPlayer().getLocation();
				Object obj = ap.getData("MerceArmorCopy");
				if (obj instanceof ItemStack[]) {
					ItemStack[] armor = (ItemStack[]) obj;
					for (ItemStack st : armor) {
						// drop Armor
						ploc.getWorld().dropItem(ploc, st);
					}
				}
			}
		}
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
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