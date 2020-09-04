package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Ninja extends ClassItemKit {
	
	public final ArrayList<UUID> inmunePlayers = new ArrayList<UUID>();
	private boolean giveInmunity = true;

	@Override
	protected ItemStack specialItem() {
		ItemStack firestorm = KitUtils.addSoulbound ( new ItemStack ( UniversalMaterial.GOLDEN_BOOTS.getMaterial ( ) ) );
		ItemMeta meta = firestorm.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + " " + instance.getReadyPrefix());
		firestorm.setItemMeta(meta);
		return firestorm;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.AQUA + "Ninja Ability";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 40000;
	}

	@Override
	protected String getInternalName() {
		return "Ninja";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.GOLDEN_BOOTS.getMaterial ( ) , 1 );
	}

	@Override
	protected List<String> getDefaultDescription() {
		final List<String> toReturn = new ArrayList<String>();
		final ChatColor aqua = ChatColor.AQUA;
		toReturn.add(aqua + "You are the ninja.");
		toReturn.add(" ");
		toReturn.add(aqua + "Use your special abilities");
		toReturn.add(aqua + "to bypass");
		toReturn.add(aqua + "your enemies' defenses and gain");
		toReturn.add(aqua + "an advantage over the enemy");
		toReturn.add(" ");
		toReturn.add(aqua + "You have been trained well");
		toReturn.add(aqua + "and you are now immune to");
		toReturn.add(aqua + "fall damage");
		toReturn.add(aqua + "Your boots allow you to give");
		toReturn.add(aqua + "yourself a jump boost");
		return toReturn;
	}

	@Override
	public void cleanup(Player player) {
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			final AnniPlayer aP = AnniPlayer.getPlayer(p.getUniqueId());
			if (aP == null) {
				return;
			}
			
			if (aP.getKit().equals(this) || inmunePlayers.contains(p.getUniqueId())) {
				if (e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
					inmunePlayers.remove(p.getUniqueId());
				}
			}
		}
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (p.getTeam() != null) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 2));
			return true;
		} else
			return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (!giveInmunity) {
			return false;
		}

		if (p.getTeam() != null) {
			player.sendMessage(ChatColor.GREEN + "You have given fall damage inmunity to players around you!");
			for (Entity e : player.getNearbyEntities(3, 3, 3)) {
				if (!(e instanceof Player)) {
					return false;
				}
				//
				final Player pl = (Player) e;
				if (player != pl) {
					this.inmunePlayers.add(pl.getUniqueId());
					pl.sendMessage(
							ChatColor.GREEN + "You have been given inmunity to your next fall damage by the ninja "
									+ ChatColor.DARK_GREEN + player.getName() + ChatColor.GREEN + "!");
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onInitialize() {
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
		return Util.setDefaultIfNotSet(section, "GiveInmunity", true);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			giveInmunity = section.getBoolean("GiveInmunity");
		}
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer arg1) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(super.getSpecialItem());
	}

}