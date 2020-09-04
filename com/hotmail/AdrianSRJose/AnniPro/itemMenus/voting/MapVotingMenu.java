package com.hotmail.AdrianSRJose.AnniPro.itemMenus.voting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting.VotingMap;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 10 July, 2020 / 08:55 PM
 */
public final class MapVotingMenu extends ItemMenu {
	
	private static final MapVotingMenu MAP_VOTING_MENU;
	static {
		MAP_VOTING_MENU = new MapVotingMenu ( Lang.VOTING_MAP_MENU.toString ( ) , 
				Size.fit ( MapVoting.VOTING_MAPS.size ( ) ) );
		
		// we suppose the voting hander is already initialized at this point.
		for ( int i = 0 ; i < MapVoting.VOTING_MAPS.size ( ) ; i ++ ) {
			final VotingMap map = MapVoting.VOTING_MAPS.get ( i );
			MAP_VOTING_MENU.setItem ( i , new ActionMenuItem ( 
					Lang.VOTING_MAP_MENU_FORMAT.toStringReplacement ( map.getName ( ) ) , new ItemClickHandler ( ) {
				@Override public void onItemClick ( ItemClickEvent event ) {
					event.getPlayer ( ).performCommand ( "Vote " + map );
					event.setWillClose ( true );
				}
			}, new ItemStack ( Material.GRASS ) ) );
		}
	}
	
	public static void openMenu ( Player player ) {
		MAP_VOTING_MENU.open ( player );
	}
	
	public static void openMenu ( AnniPlayer player ) {
		openMenu ( player.getPlayer ( ) );
	}

	private MapVotingMenu ( String title , Size size ) {
		super ( title , size );
	}
}