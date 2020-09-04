package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.base.Direction;

public class Thor extends ClassItemKit {
	
	private static int DAMAGE = 3;
	private static int RANGE  = 3;

	//
	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack hammer = KitUtils.addSoulbound ( new ItemStack ( UniversalMaterial.GOLDEN_AXE.getMaterial ( ) ) );
		ItemMeta meta = hammer.getItemMeta();
		meta.setDisplayName(getSpecialItemName());
		hammer.setItemMeta(meta);
		return hammer;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + "Hammer";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isSoulbound(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		Vector vec = Direction.getDirection(player.getLocation().getDirection()).getVector().multiply(2);
		Location loc = player.getLocation().clone().add(vec);
		for (Entity ent : loc.getWorld().strikeLightningEffect(loc).getNearbyEntities(RANGE, RANGE, RANGE)) {
			if (ent != null && ent.getType() == EntityType.PLAYER) {
				Player entP = (Player) ent;
				if (KitUtils.isValidPlayer(entP)) {
					AnniPlayer pl = AnniPlayer.getPlayer(ent.getUniqueId());
					//
					if (pl != null && !pl.equals(p) && !pl.getTeam().equals(p.getTeam())) {
						Object obj = pl.getData("TH");
						if (obj != null) {
							Long l = (Long) obj;
							if (System.currentTimeMillis() - l <= getThorImmunity()) {
								pl.setData("TH", null);
								continue;
							}
						}
						//
						pl.setData("TH", System.currentTimeMillis());
						((Player) ent).damage( (DAMAGE * 2) );
					}
				}
			}
		}
		return true;
	}

	private long getThorImmunity() {
		return 30000;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 20000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Thor";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.GOLDEN_AXE.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the hammer.", aqua + "", aqua + "You are not afraid of",
				aqua + "lava and fire because", aqua + "you are immune, but your", aqua + "enemies are not.", aqua + "",
				aqua + "Every hit you land has", aqua + "a chance of igniting your", aqua + "enemy.");
		return l;
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addItem(super.getSpecialItem());
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
		return Util.setDefaultIfNotSet(section, "HammerDamage", 3) 
			 + Util.setDefaultIfNotSet(section, "HammerRange",  3);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			DAMAGE = Math.max(section.getInt("HammerDamage", 3), 1);
			RANGE  = Math.max(section.getInt("HammerRange", 3),  3);
		}
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