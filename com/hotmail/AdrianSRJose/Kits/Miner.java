package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Miner extends ClassItemKit {
	
	private final List<String> rushers = new ArrayList<String>();

	@Override
	protected void onInitialize ( ) {
		
	}

	@Override
	protected ItemStack specialItem ( ) {
		ItemStack stack = new ItemStack ( UniversalMaterial.GOLD_NUGGET.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Gold Rusher";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName()) && KitUtils.isClassSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (!rushers.contains(player.getUniqueId().toString())) {
			rushers.add(player.getUniqueId().toString());
			Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					rushers.remove(player.getUniqueId().toString());
				}
			}, 12 * 20);
			return true;
		}
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	// Does the double loot from regenerating resources
	@EventHandler
	public void onResourceBreak(ResourceBreakEvent event) {
		if (event.getPlayer() != null && event.getPlayer().getKit().equals(this)) {
			if ( !event.getResource().Type.name ( ).contains ( "LOG" ) 
					&& event.getResource().Type != UniversalMaterial.MELON.getMaterial ( )
					&& event.getResource().Type != UniversalMaterial.GRAVEL.getMaterial ( ) ) {
				ItemStack[] stacks = event.getProducts();
				if (stacks != null) {
					for (int x = 0; x < stacks.length; x++) {
						Random r = new Random();
						int i = r.nextInt(80);

						if (rushers.contains(event.getPlayer().getPlayer().getUniqueId().toString())) {
							stacks[x].setAmount(stacks[x].getAmount() * 2);
						}
						else {
							if (i < 80)
								stacks[x].setAmount(stacks[x].getAmount() * 2);
						}
					}
				}
				event.setProducts(stacks);
			}
		}
	}

	@Override
	protected long getDefaultDelayLength() {
		return 90000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Miner";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.STONE_PICKAXE.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are the Backbone.", "", aqua + "You support the war", aqua + "Effort by gathering",
				aqua + "The Raw Materials", aqua + "your soldiers'", aqua + "Gear Needs.", "",
				aqua + "You get a chance", aqua + "for double ore", aqua + "and a special ability",
				aqua + "that makes ore", aqua + "respawn faster!");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout ( ) {
		return new Loadout().addWoodSword()
				.addSoulboundEnchantedItem(new ItemStack ( UniversalMaterial.STONE_PICKAXE.getMaterial ( ) ), Enchantment.DIG_SPEED, 1)
				.addWoodAxe()
				.addItem(new ItemStack ( UniversalMaterial.COAL.getMaterial ( ) , 9 ) ).addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player paramPlayer) {
		rushers.remove(paramPlayer.getUniqueId().toString());
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
