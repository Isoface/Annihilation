package com.hotmail.AdrianSRJose.AnniPro.anniGame.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtils;

/**
 * An listener that re-implement the Minecraft old enchantment system.
 * <p>
 * @author Varmetek
 * @author AdrianSR / Thursday 29 October, 2020 / 02:27 PM
 */
public class EnchantListener implements Listener {
	
	protected final Map < UUID , Integer > debt_map = new HashMap < > ( );
	
	public EnchantListener ( AnnihilationMain plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onItemPut ( InventoryClickEvent event ) {
		if ( !Config.USE_OLD_ENCHANTMENT_SYSTEM.toBoolean ( ) ) {
			return; // disabled old enchament system.
		}
		
		// here we're reseting enchanment offers like the old enchantment system.
		if ( Config.OLD_ENCHANTMENT_SYSTEM_RESET_OFFERS.toBoolean ( ) 
				&& event.getRawSlot ( ) == 0 /* whether this is the slot where the enchanting item goes */
				&& event.getClickedInventory ( ).getType ( ) == InventoryType.ENCHANTING
				&& ( event.getCurrentItem ( ) == null || event.getCurrentItem ( ).getType ( ) == UniversalMaterial.AIR.getMaterial ( ) ) ) {
			ReflectionUtils.resetOffers ( ReflectionUtils.getOpenContainer ( (Player) event.getWhoClicked ( ) ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onPrepare ( PrepareItemEnchantEvent event ) {
		Player player = event.getEnchanter ( );
		
		if ( !Config.USE_OLD_ENCHANTMENT_SYSTEM.toBoolean ( ) ) {
			return; // disabled old enchament system.
		}
		
		// here we're collecting debt.
		if ( debt_map.containsKey ( player.getUniqueId ( ) ) ) {
			player.giveExpLevels ( -debt_map.get ( player.getUniqueId ( ) ) );
			debt_map.remove ( player.getUniqueId ( ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST , ignoreCancelled = true )
	public void onEnchant ( EnchantItemEvent event ) {
		if ( !Config.USE_OLD_ENCHANTMENT_SYSTEM.toBoolean ( ) ) {
			return; // disabled old enchament system.
		}
		
		// here we're discounting the full cost of the enchantment.
		if ( Config.OLD_ENCHANTMENT_SYSTEM_FULL_COST.toBoolean ( ) ) {
			debt_map.put ( event.getEnchanter ( ).getUniqueId ( ) , event.getExpLevelCost ( ) );
		}
	}
}