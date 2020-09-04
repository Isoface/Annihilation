package com.hotmail.AdrianSRJose.AnniPro.mapBuilder.JoinerArmors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.JoinerArmors.JoinerArmorStand;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.LobbyMap;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class RandomJoinerArmorsCreator implements Listener {
	
	public RandomJoinerArmorsCreator(final AnnihilationMain plugin) {
		// register events.
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Removing joiners.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void addingRandomArmors(final EntityDamageByEntityEvent eve) {
		// check entities.
		if (!(eve.getDamager() instanceof Player) 
				|| !(eve.getEntity() instanceof ArmorStand)) {
			return;
		}
		
		// get and check lobby.
		final LobbyMap map = Game.LobbyMap;
		if (map == null) {
			return;
		}
		
		// get entities.
		final ArmorStand stand = (ArmorStand) eve.getEntity();
		final Player p         = (Player) eve.getDamager();
		
		// check item in hand.
		if (!KitUtils.itemHasName(p.getItemInHand(), CustomItem.REMOVE_JOINER_ARMOR_STAND.getName())) {
			return;
		}
		
		// check is joiner.
		if (!map.isJoinerArmor(stand)) {
			return;
		}
		
		// get joiner.
		JoinerArmorStand joiner = map.getJoinerArmor(stand);
		
		// remove from map.
		map.removeJoinerArmor(joiner);
		
		// despawn.
		joiner.despawn();
		
		// send removed message.
		p.sendMessage(ChatColor.GOLD + "Joiner ArmorStand removed!");
	}
	
	/**
	 * Adding random joiners.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void addingRandomArmors(final PlayerInteractEvent eve) {
		// get player.
		final Player p = eve.getPlayer();
		
		// get lobby.
		final LobbyMap map = Game.LobbyMap;
		
		// check item.
		if (!KitUtils.itemHasName(eve.getItem(), 
				CustomItem.ADD_RANDOM_JOINER_ARMOR_STAND.getName())) {
			return;
		}
		
		// check lobby.
		if (map == null) {
			p.sendMessage(ChatColor.RED + "The lobby does not exists!");
			return;
		}
		
		// cancell interaction.
		eve.setCancelled(true);
		
		// get data.
		final Location spawn = p.getLocation(); // p.getWorld().getBlockAt(p.getLocation()).getLocation();
		
		// add random armor.
		final JoinerArmorStand joiner = new JoinerArmorStand(null, spawn);
		
		// register joiner.
		Game.LobbyMap.addJoinerArmor(joiner);
		
		// spawn.
		joiner.spawn();
		
		// send message.
		p.sendMessage(ChatColor.GOLD + "Random Joiner ArmorStand added!");
	}
}
