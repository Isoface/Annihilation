package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.events.Archer_ArrowDropAbilityEvent;

public class Archer extends ClassItemKit {
	@Override
	protected void onInitialize() {
		ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack ( UniversalMaterial.ARROW.getMaterial ( ) , 3 ))
				.addIngredient ( UniversalMaterial.FLINT.getMaterial ( ) )
				.addIngredient ( UniversalMaterial.STICK.getMaterial ( ) );
		Bukkit.addRecipe(recipe);
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.BOOK.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "Arrow Drop";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		Archer_ArrowDropAbilityEvent eve = new Archer_ArrowDropAbilityEvent(p);
		AnniEvent.callEvent(eve);
		//
		if (eve.isCancelled())
			return false;
		//
		if (eve.getDropedArrows() != null)
			player.getInventory().addItem(eve.getDropedArrows());
		//
		return true;
	}

	// Stops non-archers from crafting arrows using the archer recipe
	@EventHandler(priority = EventPriority.HIGHEST)
	public void arrowCraftingStopper(CraftItemEvent event) {
		final HumanEntity h = event.getWhoClicked();
		if (h != null && h.getUniqueId() != null) {
			if (event.getRecipe().getResult().getType() == UniversalMaterial.ARROW.getMaterial ( )
					&& event.getRecipe().getResult().getAmount() == 3) {
				final AnniPlayer player = AnniPlayer.getPlayer(h.getUniqueId());
				if (player != null && !hasThisKit(player)) {
					event.setCancelled(true);
				}
			}
		}
	}

	// Adds the +1 arrow damage
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void damageListener(final EntityDamageByEntityEvent event) {
		if (event.getDamager().getType() == EntityType.ARROW) {
			final ProjectileSource s = ((Projectile) event.getDamager()).getShooter();
			if (s != null && s instanceof Player) {
				final AnniPlayer shooter = AnniPlayer.getPlayer(((Player) s).getUniqueId());
				if (shooter != null && shooter.getKit() != null && shooter.getKit().equals(this))
					event.setDamage(event.getDamage() + 1);
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
		return "Archer";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.ARROW.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are the Arrow.", "", aqua + "The last word in", aqua + "Ranged combat,", "",
				aqua + "deal +1 Damage with", aqua + "any Bow.", "", aqua + "Make 3 arrows for ",
				aqua + "the Price of One!", "", aqua + "Activate your special", aqua + "ability to get 16 arrows ",
				aqua + "once a minute.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel()
				.addSoulboundEnchantedItem(new ItemStack ( UniversalMaterial.BOW.getMaterial ( ) ), Enchantment.ARROW_KNOCKBACK, 1)
				.addSoulboundItem(new ItemStack ( UniversalMaterial.ARROW.getMaterial ( ) , 16 ) ).addHealthPotion1().addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player arg0) {
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