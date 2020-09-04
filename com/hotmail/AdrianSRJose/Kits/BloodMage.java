package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class BloodMage extends ClassItemKit {

	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.FERMENTED_SPIDER_EYE.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Corrupt";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		for (Entity e : p.getNearbyEntities(3.0D, 3.0D, 3.0D)) {
			if (e != null && e instanceof Player) {
				Player vict = (Player) e;
				//
				if (vict != null) {
					AnniPlayer vp = AnniPlayer.getPlayer(vict.getUniqueId());
					//
					if (vp != null && vp.getTeam() != null && !vp.getTeam().equals(ap.getTeam())) {
						vict.setMaxHealth(16.0D);
						vict.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 0));
						//
						Shedulers.scheduleSync(new Runnable() {
							@Override
							public void run() {
								vict.setMaxHealth(20.0D);
							}
						}, 5 * 20);
						return true;
					}
				}
			}
		}
		return false;
	}

	@EventHandler
	public void d(EntityDamageByEntityEvent eve) {
		if (eve.getDamager() == null)
			return;

		if (eve.getEntity() instanceof Player && eve.getDamager() instanceof Player) {
			Player v = (Player) eve.getEntity();
			Player d = (Player) eve.getDamager();
			//
			if (KitUtils.isValidPlayer(v) && KitUtils.isValidPlayer(d)) {
				AnniPlayer vp = AnniPlayer.getPlayer(v.getUniqueId());
				AnniPlayer dp = AnniPlayer.getPlayer(d.getUniqueId());
				//
				if (vp != null && dp != null) {
					if (Game.isGameRunning() && vp.getTeam() != null && dp.getTeam() != null) {
						if (dp.getKit().equals(this) && !vp.getTeam().equals(dp.getTeam())) {
							Random r = new Random();
							int x = r.nextInt(99);
							//
							if (x >= 10 && x <= 20)
								v.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4 * 20, 0));
						}
					}
				}
			}
		}
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
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
		return "BloodMage";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.MUSIC_DISC_STAL.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the Danger", "", aqua + "You may not punch", aqua + "people to death,", "",
				aqua + "but that doesn't make", aqua + "you any less potent.", "", aqua + "Support your teammates",
				aqua + "by poisoning enemies", aqua + "and reducing their maximum", aqua + "health with your Corrupt",
				aqua + "ability.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodAxe().addWoodPick().addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player player) {
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
