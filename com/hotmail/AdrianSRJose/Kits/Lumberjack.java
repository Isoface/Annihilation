package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Lumberjack extends ClassItemKit {
	
//	private static Material     STONE_AXE = Material.STONE_AXE;
	private static final String KEY_FORCE = "key_inforce_key_(lumberjack)";
	private static String BRUTE_FORCE_ON_MESSAGE = ChatColor.GOLD + "!!!!!Brute Force!!!!!";
	
	@Override
	protected void onInitialize() {
	}
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "brute-force-message", BRUTE_FORCE_ON_MESSAGE);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		BRUTE_FORCE_ON_MESSAGE = Util.wc(section.getString("brute-force-message", BRUTE_FORCE_ON_MESSAGE));
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.BRICK.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Brute Force";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName()) && KitUtils.isClassSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		Object o = ap.getData(KEY_FORCE);
		if (o != null && o instanceof String)
			return false;
		//
		ap.setData(KEY_FORCE, "true");
		p.sendMessage(BRUTE_FORCE_ON_MESSAGE);
		//
		Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				ap.setData(KEY_FORCE, null);
			}
		}, 15 * 20);
		return true;
	}

	@EventHandler
	public void onResourceBreak(ResourceBreakEvent event) {
		if (event.getPlayer().getKit().equals(this)) {
			if ( event.getResource ( ).Type.name ( ).equals ( "LOG" ) || event.getResource ( ).Type.name ( ).contains ( "_LOG" ) ) {
//			if (event.getResource().Type == UniversalMaterial.LOG) {
				ItemStack[] stacks = event.getProducts();
				for (int x = 0; x < stacks.length; x++)
					stacks[x].setAmount(stacks[x].getAmount() * 2);
				event.setProducts(stacks);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onH(EntityDamageByEntityEvent eve) {
		if (Game.isNotRunning() || eve.getDamager() == null)
			return;
		//
		if (eve.getEntity() instanceof Player && eve.getDamager() instanceof Player) {
			final Player p = (Player) eve.getEntity();
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			//
			final Player d = (Player) eve.getDamager();
			AnniPlayer dp = AnniPlayer.getPlayer(d.getUniqueId());
			//
			if (ap != null && dp != null && isOnForce(dp) && ap.getTeam() != null && dp.getTeam() != null) {
				if (!ap.getTeam().equals(dp.getTeam()) && dp.getKit().equals(this)) {
					ItemStack inHand = d.getItemInHand();
					if (inHand != null && inHand.getType() != null && inHand.getType().name().contains("_AXE")) {
						ItemStack b = p.getInventory().getBoots();
						ItemStack c = p.getInventory().getHelmet();
						ItemStack pe = p.getInventory().getChestplate();
						ItemStack pa = p.getInventory().getLeggings();
						//
						if (b != null) {
							b.setDurability((short) (b.getDurability() + (short) 10));
							p.getInventory().setBoots(b);
						}
						//
						if (c != null) {
							c.setDurability((short) (c.getDurability() + (short) 10));
							p.getInventory().setHelmet(c);
						}
						//
						if (pe != null) {
							pe.setDurability((short) (pe.getDurability() + (short) 10));
							p.getInventory().setChestplate(pe);
						}
						//
						if (pa != null) {
							pa.setDurability((short) (pa.getDurability() + (short) 10));
							p.getInventory().setLeggings(pa);
						}
						//
						p.updateInventory();
					}
				}
			}
		}
	}

	private boolean isOnForce(final AnniPlayer ap) {
		return ap.getData(KEY_FORCE) instanceof String;
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
		return "Lumberjack";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack ( UniversalMaterial.STONE_AXE.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the wedge.", "", aqua + "Gather wood with", aqua + "an Efficiency axe",
				aqua + "and With the Chance", aqua + "of Gaining double", aqua + "Yield", "",
				aqua + "Ensuring Quick Work", aqua + "of any Trees in Your way!");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick()
				.addSoulboundEnchantedItem(new ItemStack ( UniversalMaterial.STONE_AXE.getMaterial ( ) ), Enchantment.DIG_SPEED, 1)
				.addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player paramPlayer) {
		AnniPlayer ap = AnniPlayer.getPlayer(paramPlayer);
		if (ap != null) { 
			ap.setData(KEY_FORCE, null);
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