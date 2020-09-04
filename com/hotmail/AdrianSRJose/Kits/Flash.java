package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Flash extends ClassItemKit {
	private int seconds = 10;

	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack(UniversalMaterial.SUGAR.getMaterial());
		stack = KitUtils.setName(stack, getSpecialItemName() + instance.getReadyPrefix());
		return KitUtils.addClassSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + ChatColor.BOLD.toString() + ChatColor.ITALIC + "Speed Increase";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassSoulbound(stack) && KitUtils.itemNameContains(stack, getSpecialItemName()))
			return true;
		//
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (KitUtils.isValidPlayer(player)) {
			final UUID id = player.getUniqueId();
			final float realSpeed = player.getWalkSpeed();
			player.setWalkSpeed(0.38777F);
			//
			Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					Player p = Bukkit.getPlayer(id);
					if (KitUtils.isValidPlayer(p)) {
						p.setWalkSpeed(realSpeed);
					}
				}
			}, (seconds * 20L));
			//
			return true;
		}
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 25 * 1000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Flash";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.SUGAR.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the speed!", aqua + "", aqua + "With your sugar,",
				aqua + "you can get speed for 10 seconds", aqua + "Use this ability to surround ",
				aqua + "your enemies and make them ", aqua + "lose sight of you.", aqua + "",
				aqua + "CoutDown: " + ChatColor.GOLD + " 25 s");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel().addItem(getSpecialItem());
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent eve) {
		final Player p = eve.getPlayer();
		cleanup(p);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent eve) {
		final Player p = eve.getPlayer();
		cleanup(p);
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player p) {
		if (p != null) {
			p.setWalkSpeed(0.217777779F);
		}
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer arg1) {
		addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "Speed-Seconds", 10);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		seconds = section.getInt("Speed-Seconds", 10);
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
