package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.mapBuilder.TeamBlock;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.MapKey;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shop.ShopMenu;

public final class Signs implements Iterable<AnniSign>, Listener {
	private Map<MapKey, AnniSign> signs;

	//
	public Signs() {
		signs = new HashMap<MapKey, AnniSign>();
	}

	public Signs(ConfigurationSection configSection) {
		this();
		//
		if (configSection != null) {
			for (String key : configSection.getKeys(false)) {
				if (key != null) {
					ConfigurationSection sign = configSection.getConfigurationSection(key);
					//
					if (sign != null)
						addSign(new AnniSign(sign));
				}
			}
		}
	}

	public void registerListener(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public Iterator<AnniSign> iterator() {
		return Collections.unmodifiableMap(signs).values().iterator();
	}

	public boolean addSign(AnniSign sign) {
		ChatColor g = ChatColor.DARK_GRAY;
		MapKey key = MapKey.getKey(sign.getLocation());
		//
		if (key != null) {
			if (!signs.containsKey(key)) {
				String[] lore;
				//
				if (sign.getType().equals(SignType.Brewing)) {
					lore = new String[] { g + "[" + Lang.SHOP.toString() + g + "]", Lang.BREWINGSIGN.toString() };
				} else if (sign.getType().equals(SignType.Weapon)) {
					lore = new String[] { g + "[" + Lang.SHOP.toString() + g + "]", Lang.WEAPONSIGN.toString() };
				} else {
					AnniTeam team = sign.getType().getTeam();
					lore = Lang.TEAMSIGN.toStringArray(team.getExternalColoredName());
				}
				//
				placeSignInWorld(sign, lore);
				signs.put(key, sign);
				//
				return true;
			}
		}
		return false;
	}

	private void placeSignInWorld(AnniSign asign, String[] lore) {
		Location loc = asign.getLocation().toLocation();
		//
		if (loc == null || loc.getWorld() == null)
			return;
		//
		Block block = loc.getWorld().getBlockAt(loc);
		//
		if (block != null) {
			if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST)
				block.getWorld().getBlockAt(loc).setType(asign.isSignPost() ? Material.SIGN_POST : Material.WALL_SIGN);
			//
			BlockState state = block.getState();
			//
			if (state == null || !(state instanceof Sign))
				return;
			//
			Sign sign = (Sign) block.getState();
			if (sign != null) {
				for (int x = 0; x < lore.length; x++) {
					if (x < 4)
						if (lore[x] != null)
							sign.setLine(x, lore[x]);
				}
				//
				org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(block.getType());
				if (matSign != null) {
					matSign.setFacingDirection(asign.getFacingDirection());
					sign.setData(matSign);
					sign.update(true);
				}
				//
			}
		}
	}

	public boolean removeSign(Loc sign) {
		return removeSign(sign.toLocation());
	}

	public boolean removeSign(Location sign) {
		boolean b = signs.remove(MapKey.getKey(sign)) == null ? false : true;
		if (b)
			sign.getWorld().getBlockAt(sign).setType(Material.AIR);
		//
		return b;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void clickingSigns(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK 
				|| ( block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST ) ) {
			return;
		}
		
		Player player = event.getPlayer();
		AnniSign sign = signs.get(MapKey.getKey(block.getLocation()));
		if (sign == null) {
			return;
		}
		
		switch(sign.getType().getID()) {
		case 1: /* weapon shop sign */
			ShopMenu.WEAPON_SHOP.get().open(player);
			break;
		case 2: /* brewing shop sign */
			ShopMenu.BREWING_SHOP.get().open(player);
			break;
		case 3: /* team sign */
			if (sign.getType().getTeam() != null) {
				player.performCommand("team " + sign.getType().getTeam().getName());
			}
			break;
		}
		
		/* cancel interaction */
		event.setCancelled(true);
	}

//	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
//	public void signClickCheck(PlayerInteractEvent event) {
//		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
//			Block b = event.getClickedBlock();
//			if (b != null) {
//				if (b.getType() == Material.WALL_SIGN.toBukkit() || b.getType() == Material.SIGN_POST.toBukkit()) {
//					Location  loc = b.getLocation();
//					Player      p = event.getPlayer();
//					AnniSign sign = signs.get(MapKey.getKey(loc));
//					if (sign != null) {
//						event.setCancelled(true);
//						if (sign.getType().equals(SignType.Team)) {
//							AnniTeam team = sign.getType().getTeam();
//							if (team != null) {
//								p.performCommand("team " + team.getName());
//							}
//						} else if (sign.getType().equals(SignType.Brewing)) {
//							ShopMenu.BREWING_SHOP.get().open(p);
//						} else if (sign.getType().equals(SignType.Weapon)) {
//							ShopMenu.WEAPON_SHOP.get().open(p);
//						}
//					}
//				}
//			}
//		}
//	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void signBreakCheck(BlockBreakEvent event) {
		final Player player = event.getPlayer();
		//
		if (KitUtils.isValidPlayer(player)) {
			if (event.getBlock() != null) {
				if (event.getBlock().getType() == Material.WALL_SIGN
						|| event.getBlock().getType() == Material.SIGN_POST) {
					MapKey key = MapKey.getKey(event.getBlock().getLocation());
					//
					TeamBlock t = null;
					if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Red.getName()))
						t = TeamBlock.Red;
					else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Blue.getName()))
						t = TeamBlock.Blue;
					else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Green.getName()))
						t = TeamBlock.Green;
					else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Yellow.getName()))
						t = TeamBlock.Yellow;
					//
					if (key != null && signs.containsKey(key))
						if (t == null)
							event.setCancelled(true);
				}
			}
		}
	}

	////
	public int saveToConfig(ConfigurationSection configSection) {
		int save = 0;
		//
		if (configSection != null) {
			int counter = 1;
			//
			for (AnniSign sign : this) {
				if (sign != null) {
					save += sign.saveToConfig(Util.createSectionIfNoExits(configSection, counter + ""));
					counter++;
				}
			}
		}
		//
		return save > 0 ? 1 : 0;
	}
}
