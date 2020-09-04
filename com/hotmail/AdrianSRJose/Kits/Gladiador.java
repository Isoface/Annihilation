package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

public class Gladiador extends ClassItemKit {
	private String noEnemiesFound = "&aNo Found enemies on your Nexus";
	private String foundEnemies   = ChatColor.GOLD + (ChatColor.GRAY + "Found " + ChatColor.RED + "%#" + ChatColor.YELLOW + " enemies on your Nexus");

	private static UniversalMaterial CHAINMAIL_CHESTPLATE = UniversalMaterial.CHAINMAIL_CHESTPLATE;
	private static UniversalMaterial CHAINMAIL_BOOTS = UniversalMaterial.CHAINMAIL_BOOTS;
	private static UniversalMaterial TORCH = UniversalMaterial.TORCH;
	
	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack(TORCH.getMaterial());
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + "REVEALER";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && KitUtils.isSpecial(stack, false))
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName())
					&& KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent eve) {
		List<UUID> plson = new ArrayList<UUID>();

		if (p == null || p.getLocation() == null || ap.getTeam() == null || ap.getTeam().getNexus() == null
				|| ap.getTeam().getNexus().getLocation() == null
				|| ap.getTeam().getNexus().getLocation().toLocation() == null)
			return false;
		//
		if (p.getLocation().distanceSquared(ap.getTeam().getNexus().getLocation().toLocation()) > 50 * 50)
			return false;
		//
		for (Entity nearbysOfNexus : KitConfig.getNearbyEntities(ap.getTeam().getNexus().getLocation().toLocation(), 8,
				8, 8, 8)) {
			if (nearbysOfNexus != null && nearbysOfNexus instanceof Player) {
				Player nearby = (Player) nearbysOfNexus;
				//
				if (!KitUtils.isValidPlayer(nearby))
					return false;
				//
				AnniPlayer nearbyAp = AnniPlayer.getPlayer(nearby.getUniqueId());
				//
				if (nearbyAp != null && !nearbyAp.getTeam().equals(ap.getTeam())) {
					p.playSound(p.getLocation(), UniversalSound.WITHER_SHOOT.asBukkit(), 4.0F, 2.0F);
					//
					//
					nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7 * 20, 2));
					nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 7 * 20, 2));
					//
					Object cs = nearbyAp.getData("key-gladiador-helmet-backup-key");
					if (cs == null && nearby.getInventory() != null) {
						ItemStack h = nearby.getInventory().getHelmet();
						if (h != null)
							nearbyAp.setData("key-gladiador-helmet-backup-key", h);
						//
						nearby.getInventory().setHelmet(new ItemStack(UniversalMaterial.PACKED_ICE.getMaterial(), 1));
						//
						Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
							@Override
							public void run() {
								if (nearbyAp != null && nearbyAp.isOnline()) {
									Object o = nearbyAp.getData("key-gladiador-helmet-backup-key");
									if (o != null && o instanceof ItemStack) {
										ItemStack bh = (ItemStack) o;
										if (bh != null)
											nearbyAp.getPlayer().getInventory().setHelmet(bh);
									}
								}
							}
						}, 7 * 20);
					}
					//
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.ITEM_SNOWBALL.displayNewerVersions().display(1.0F, 1.0F, 0.0F, 0.6F, 200,
								nearby.getLocation().clone(), 10000.0D);
					} else {
						CompatibleParticles.ITEM_SNOWBALL.displayOlderVersions().display(1.0F, 1.0F, 0.0F, 0.6F, 200,
								nearby.getLocation().clone(), 10000.0D);
					}
					//
					//
					nearby.getLocation().getWorld().playSound(nearby.getLocation(),
							UniversalSound.ZOMBIE_UNFECT.asBukkit(), 2.0F, 6.0F);
					//
					//
					if (!plson.contains(nearby.getUniqueId()))
						plson.add(nearby.getUniqueId());

					if (plson.size() >= 0) {
//						p.sendMessage(ChatColor.GOLD + (ChatColor.GRAY + "Found " + ChatColor.RED + plson.size()
//								+ ChatColor.YELLOW + " enemies on your Nexus"));
						
						p.sendMessage(foundEnemies.replace("%#", String.valueOf(plson.size())));
						//
						try {
							for (UUID toR : plson) {
								if (toR != null)
									plson.remove(toR);
							}
							//
							plson.clear();
						} catch (ConcurrentModificationException e) {
						}
						//
						return true;
					}
					break;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent eve) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 60000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Gladiador";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(TORCH.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are the Hope", "", aqua + "With your revealing,", "",
				aqua + "freeze for a few seconds", aqua + "to the enemies", aqua + "in your nexus",
				aqua + "for a few seconds,", aqua + "", aqua + "And eliminate them", aqua + "easily.", aqua + "",
				aqua + "Coutdown:" + ChatColor.GOLD + " 60s");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodAxe().addWoodPick().addHealthPotion1()
				.addItem(getSpecialItem());
	}

	@Override
	public void cleanup(Player p) {
		if (p.hasPotionEffect(PotionEffectType.SLOW))
			p.removePotionEffect(PotionEffectType.SLOW);
	}

	@Override
	public boolean onItemClick(Inventory paramInventory, AnniPlayer ap) {
		this.addLoadoutToInventory(paramInventory);

		if (KitUtils.isSoulbound(ap.getPlayer().getInventory().getHelmet()) 
				|| KitUtils.isClassSoulbound(ap.getPlayer().getInventory().getHelmet())
				|| KitUtils.isClassUndropabbleSoulbound(ap.getPlayer().getInventory().getHelmet())) {
			ap.getPlayer().getInventory().setHelmet((ItemStack) null);
		}

		if (ap.getPlayer().getInventory().getBoots() == null
				|| KitUtils.isSoulbound(ap.getPlayer().getInventory().getBoots())
				|| KitUtils.isClassUndropabbleSoulbound(ap.getPlayer().getInventory().getBoots())) {
			ap.getPlayer().getInventory().setBoots(KitUtils.addSoulbound(new ItemStack(CHAINMAIL_BOOTS.getMaterial(), 1, (short) 96)));
		}
		else {
			paramInventory.addItem(KitUtils.addSoulbound(new ItemStack(CHAINMAIL_BOOTS.getMaterial(), 1, (short) 96)));
		}

		if (ap.getPlayer().getInventory().getChestplate() == null
				|| KitUtils.isSoulbound(ap.getPlayer().getInventory().getChestplate())
				|| KitUtils.isClassUndropabbleSoulbound(ap.getPlayer().getInventory().getChestplate())) {
			ap.getPlayer().getInventory().setChestplate(KitUtils.addSoulbound(new ItemStack(CHAINMAIL_CHESTPLATE.getMaterial())));
		}
		else {
			paramInventory.addItem(KitUtils.addSoulbound(new ItemStack(CHAINMAIL_CHESTPLATE.getMaterial())));
		}

		ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
		AnnihilationMain.INSTANCE.getServer().getScheduler().runTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (KitUtils.isValidPlayer(p))
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
			}
		});
	}

	@Override
	protected boolean useCustomMessage() {
		return true;
	}

	@Override
	protected String positiveMessage() {
		return null;
	}

	@Override
	protected String negativeMessage() {
		return noEnemiesFound;
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "NoEnemiesFound", "&aNo Found enemies on your Nexus")
				+ Util.setDefaultIfNotSet(section, "FoundEnemies", Util.untranslateAlternateColorCodes(foundEnemies));
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			if (section.isString("NoEnemiesFound")) {
				noEnemiesFound = Util.wc(section.getString("NoEnemiesFound", noEnemiesFound));
			}
			
			if (section.isString("FoundEnemies")) {
				foundEnemies = Util.wc(section.getString("FoundEnemies"));
			}
		}
	}
}
