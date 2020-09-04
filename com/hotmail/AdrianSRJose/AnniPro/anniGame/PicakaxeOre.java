package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.ResourceBreakEvent;

public class PicakaxeOre implements Listener {

	public PicakaxeOre(final Plugin plugin) {
		// register events.
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onBreak(ResourceBreakEvent eve) {
		// get and check item in hand.
		final ItemStack item = eve.getPlayer().getPlayer().getItemInHand();
		if (item == null) {
			return;
		}
		
		// get and check resource.
		if (!eve.getResource().Type.name().endsWith("_ORE")) {
			return;
		}
		
		// check item is a pickaxe.
		final Material pick_material = item.getType();
		if (!pick_material.name().endsWith("_PICKAXE")) {
			eve.setCancelled(true);
			return;
		}
		
		// get pickaxe type.
		final PickaxeType pick_type = PickaxeType.valueOf(pick_material.name());
		
		// allow break depending pickaxe and resource.
		boolean cancell = false;
		switch(eve.getResource().Type) {
			case IRON_ORE: {
				if (pick_type.getLevel() < 2) {
					cancell = true;
				}
				break;
			}
			
			case DIAMOND_ORE: {
				if (pick_type.getLevel() < 3) {
					cancell = true;
				}
				break;
			}
			
			case EMERALD_ORE: {
				if (pick_type.getLevel() < 3) {
					cancell = true;
				}
				break;
			}
			
			case REDSTONE_ORE: {
				if (pick_type.getLevel() < 3) {
					cancell = true;
				}
				break;
			}
			
			case GLOWING_REDSTONE_ORE: {
				if (pick_type.getLevel() < 3) {
					cancell = true;
				}
				break;
			}
			
			case LAPIS_ORE: {
				if (pick_type.getLevel() < 2) {
					cancell = true;
				}
				break;
			}
			default:
				break;
		}
		
		// cancell event.
		if (cancell) {
			eve.setCancelled(true);
		}
	}
	
	private enum PickaxeType {
		
		WOOD_PICKAXE(0),
		GOLD_PICKAXE(1),
		STONE_PICKAXE(2),
		IRON_PICKAXE(3),
		DIAMOND_PICKAXE(4);
		
		private int level;
		
		PickaxeType(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
	}
}
