package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
import com.hotmail.AdrianSRJose.base.KitConfig;

public class Hulk extends ClassItemKit {

	@Override
	protected void onInitialize() {
		
	}

	@Override
	protected ItemStack specialItem ( ) {
		ItemStack stack = new ItemStack ( UniversalMaterial.BONE_MEAL.getMaterial ( ), 1, (short) 15);
		KitUtils.setName(stack, this.getSpecialItemName() + instance.getReadyPrefix());
		return KitUtils.addClassSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + "§lPUNCH";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && KitUtils.isClassSoulbound(stack))
			if (KitUtils.isSpecial(stack, false) && KitUtils.itemNameContains(stack, getSpecialItemName()))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (playHonda(player.getLocation(), player))
			return true;
		else
			player.sendMessage(ChatColor.RED + "There are no enemies around you");
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 25000;
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
		return "Hulk";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.BONE_MEAL.getMaterial(), 1, (short) 15);
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l,

				aqua + "You Are the Explosion", "", aqua + "If you are surrounded and desperate,",
				aqua + "just use your PUNCH to", aqua + "push the enemies around", aqua + "you and cause nausea.", "",
				aqua + "Do not worry if there are your", aqua + "teammates around you,",
				aqua + "because they will not get hurt,", aqua + "they will receive regeneration",
				aqua + "for 30 seconds.", "", aqua + "Coutdown: " + ChatColor.GOLD + "25s");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodAxe().addWoodPick().addWoodShovel().addItem(getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	public boolean onItemClick(Inventory arg0, AnniPlayer arg1) {
		this.addLoadoutToInventory(arg0);
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean playHonda(Location l, Player owner) {
		if (!KitUtils.isValidPlayer(owner) || !Util.isValidLoc(l)) {
			return false;
		}
		
		final AnniPlayer ap = AnniPlayer.getPlayer(owner.getUniqueId());
		if (ap == null || ap.getTeam() == null) {
			return false;
		}
		
		for (Entity ent : KitConfig.getNearbyEntities(l, 9D, 9D, 9D, 9D)) {
			if (ent != null && ent instanceof Player) {
				Player tp = (Player) ent;
				
				if (!KitUtils.isValidPlayer(tp)) {
					return false;
				}
				
				AnniPlayer at = AnniPlayer.getPlayer(tp.getUniqueId());
				if (at != null && ap != null) {
					if (ap.getTeam() != null && at.getTeam() != null) {
						if (!at.getTeam().equals(ap.getTeam())) {
							l.getWorld().playSound(l, UniversalSound.EXPLODE.asBukkit(), 4F, 1F);
							
							tp.setVelocity(tp.getLocation().getDirection().setY(1).multiply(16).multiply(-1));
							tp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 25 * 20, 1));
							tp.damage(2.0, owner);
							
							float speed = 0.1F;
							
							for (Location lc : Util.getCircle(l.clone().add(0.0D, 1.0D, 0.0D), 3, 50)) {
								if (lc != null) {
									if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
										CompatibleParticles.CLOUD.displayNewerVersions().display(0, 0, 0, speed, 5, lc, 20000);
									} else {
										CompatibleParticles.CLOUD.displayOlderVersions().display(0, 0, 0, speed, 5, lc, 20000);
									}
								}
							}
							
							Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
								@Override
								public void run() {
									for (Location lc : Util.getCircle(l.clone().add(0.0D, 1.0D, 0.0D), 6, 50)) {
										if (lc != null) {
											if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
												CompatibleParticles.CLOUD.displayNewerVersions().display(0, 0, 0, speed, 5, lc, 20000);
											} else {
												CompatibleParticles.CLOUD.displayOlderVersions().display(0, 0, 0, speed, 5, lc, 20000);
											}
										}
									}
								}
							}, 3L);
							return true;
						} else {
							tp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 0));
							return true;
						}
					}
				}
			} else
				return false;
		}
		return false;
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}
}
