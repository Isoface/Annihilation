package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.base.Function;
import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;
import com.hotmail.AdrianSRJose.base.Delays;
import com.hotmail.AdrianSRJose.base.StandardItemUpdater;

public class Succubus extends ConfigurableKit {
	private ItemStack sucItem;
	private String sucItemName;

	@Override
	protected void setUp() {
		sucItem = KitUtils.addSoulbound(getDefaultIcon().clone());
		ItemMeta m = sucItem.getItemMeta();
		m.setDisplayName(sucItemName);
		sucItem.setItemMeta(m);
		Delays.getInstance().createNewDelay(getInternalName(),
				new StandardItemUpdater(sucItemName, sucItem.getType(), new Function<ItemStack, Boolean>() {
					@Override
					public Boolean apply(ItemStack stack) {
						return isSuccubusItem(stack);
					}
				}));
	}

	private boolean isSuccubusItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(sucItemName) && KitUtils.isSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected String getInternalName() {
		return "Succubus";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		final ItemStack icon = new ItemStack(UniversalMaterial.INK_SAC.getMaterial ( ), 1, (short) 0);
		icon.setData(new MaterialData(icon.getType(), (byte) 1));
		
		return icon;
//		return new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 1);
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "SuccubusItemName", "&eLife Drain");
	}

	@Override
	protected void loadKitStuff(ConfigurationSection section) {
		if (section != null) {
			super.loadKitStuff(section);
			//
			if (section.isString("SuccubusItemName"))
				sucItemName = Util.wc(section.getString("SuccubusItemName"));
		}
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the hunter.", "", aqua + "Once every 2 minutes the",
				aqua + "Succubus can attempt to", aqua + "suck the remaining life", aqua + "out of an enemy player.",
				"", aqua + "If the enemy player has", aqua + "less than 40% health,",
				aqua + "they are killed immediately", aqua + "and the remaining health", aqua + "is transferred to the",
				aqua + "Succubus.", "", aqua + "However, if the enemy", aqua + "player has more than 40%",
				aqua + "health, the Succubus is", aqua + "dealt the enemy player's", aqua + "remaining health in true",
				aqua + "damage.");
		return l;
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void lifeDrain(PlayerInteractEntityEvent event) {
		if (Game.isGameRunning() && event.getRightClicked().getType() == EntityType.PLAYER) {
			Player user = event.getPlayer();
			AnniPlayer anniUser = AnniPlayer.getPlayer(user.getUniqueId());
			if (anniUser != null && anniUser.getKit().equals(this)) {
				if (!Delays.getInstance().hasActiveDelay(user, this.getInternalName())) {
					Player target = (Player) event.getRightClicked();
					AnniPlayer t = AnniPlayer.getPlayer(target.getUniqueId());
					if (t != null && !t.getTeam().equals(anniUser.getTeam())) {
						Delays.getInstance().addDelay(user, System.currentTimeMillis() + 120000,
								this.getInternalName());
						double health = ((Damageable) target).getHealth();
						double maxHealth = ((Damageable) target).getMaxHealth();
						if ((health / maxHealth) <= .42) {
							double newHealth = health + maxHealth;
							if (newHealth > health)
								newHealth = maxHealth;
							user.setHealth(newHealth);
							// target.setHealth(0);
							target.damage(100, user);
							if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1))
								user.playSound(user.getLocation(), Sound.valueOf("BLAZE_BREATH"), 4F, 1.2F);
							else
								user.playSound(user.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 4F, 1.2F);
						} else {
							double newHealth = ((Damageable) user).getHealth() - ((Damageable) user).getHealth();
							if (newHealth < 0)
								newHealth = 0;
							user.setHealth(user.getHealth() + newHealth);
						}
					}
				}
			}
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addItem(sucItem);
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
	protected void loadFromConfig(ConfigurationSection section) {
	}
}