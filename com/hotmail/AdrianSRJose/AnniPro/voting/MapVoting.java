package com.hotmail.AdrianSRJose.AnniPro.voting;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerInteractAtAnniItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveServerEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.voting.MapVotingMenu;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniSubDirectory;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 10 July, 2020 / 01:23 AM
 */
public final class MapVoting implements CommandExecutor , Listener {
	
	private static MapVoting INSTANCE = null;
	
	private static final List < VotingMap > VOTING_MAPS0 = new ArrayList < > ( );
	public  static final List < VotingMap >  VOTING_MAPS = Collections.unmodifiableList ( VOTING_MAPS0 );
	
	private static final Map < UUID , VotingMap > VOTES = new HashMap < > ( );
	
	public static void prepareVoting ( AnnihilationMain annihilation ) {
		if ( INSTANCE != null ) {
			throw new IllegalStateException ( "handler already initialized!" );
		}
		
		INSTANCE = new MapVoting ( annihilation );
		VOTING_MAPS0.clear ( );
		VOTES.clear ( );
		
		String [ ] worlds = getWorlds ( );
		for ( String world : worlds ) {
			VOTING_MAPS0.add ( new VotingMap ( world ) );
		}
	}
	
	public static void vote ( AnniPlayer player , VotingMap map ) {
		vote ( player.getID ( ) , map );
	}
	
	public static void vote ( Player player , VotingMap map ) {
		vote ( player.getUniqueId ( ) , map );
	}
	
	private static void vote ( UUID uuid , VotingMap map ) {
		int index = VOTING_MAPS0.indexOf ( map );
		if ( index != -1 ) {
			VotingMap   voting = VOTING_MAPS0.get ( index );
			VotingMap old_vote = VOTES.get ( uuid );
			if ( old_vote != null ) {
				if ( old_vote != voting ) {
					old_vote.score --;
					voting.score ++;
					VOTES.put ( uuid , voting );
				}
			} else {
				voting.score ++;
				VOTES.put ( uuid , voting );
			}
		} else {
			throw new IllegalStateException ( "an unknown map has been provided!" );
		}
	}
	
	public static void removeVote ( Player player ) {
		removeVote ( player.getUniqueId ( ) );
	}
	
	public static void removeVote ( AnniPlayer player ) {
		removeVote ( player.getID ( ) );
	}
	
	private static void removeVote ( UUID uuid ) {
		VotingMap map = VOTES.get ( uuid );
		if ( map != null ) {
			int index = VOTING_MAPS0.indexOf ( map );
			if ( index != -1 ) {
				VOTING_MAPS0.get ( index ).score --;
			}
		}
		VOTES.remove ( uuid );
	}
	
	public static VotingMap getVote ( AnniPlayer player ) {
		return getVote ( player.getID ( ) );
	}
	
	public static VotingMap getVote ( Player player ) {
		return getVote ( player.getUniqueId ( ) );
	}
	
	private static VotingMap getVote ( UUID uuid ) {
		return VOTES.get ( uuid );
	}
	
	public static VotingMap getWinningMap ( ) {
		int         score = 0;
		VotingMap winning = null;
		
		for ( VotingMap map : VOTING_MAPS0 ) {
			if ( map.getScore ( ) >= score ) {
				score   = map.getScore ( );
				winning = map;
			}
		}
		return winning;
	}
	
	private static String [ ] getWorlds ( ) {
		File map_folder = AnniSubDirectory.WORLDS_DIRECTORY.getDirectory ( );
		if ( !map_folder.exists ( ) ) {
			map_folder.mkdirs ( );
		}

		File [ ] files = map_folder.listFiles ( new FilenameFilter ( ) {
			@Override public boolean accept ( File file , String name ) {
				return file != null && file.isDirectory() && !name.endsWith("-TempCopy");
			}
		});

		if (files != null && files.length > 0) {
			if (files.length <= Config.MAP_LOADING_MAX_MAPS_FOR_VOTING.toInt()) { // GameVars.maxVotingMaps
				String[] str = new String[files.length];
				for (int x = 0; x < str.length; x++) {
					str[x] = files[x].getName();
				}
				return str;
			} else {
				// we are going to get random maps below.
				List < String > list = new ArrayList < > ( );
				for ( File file : files ) {
					list.add ( file.getName ( ) );
				}
				
				Random random = new Random ( System.currentTimeMillis ( ) );
				while ( list.size ( ) > Config.MAP_LOADING_MAX_MAPS_FOR_VOTING.toInt ( ) ) {
					list.remove ( random.nextInt ( list.size ( ) ) );
				}
				return list.toArray ( new String [ list.size ( ) ] );
			}

		}
		return new String [ 0 ];
	}
	
	/**
	 * TODO: Description
	 * <p>
	 * @author AdrianSR / Friday 10 July, 2020 / 01:26 AM
	 */
	public static final class VotingMap {
		
		private final String name;
		private       int   score;
		
		private VotingMap ( String name ) {
			this.name = name;
		}

		public String getName ( ) {
			return name;
		}

		public int getScore ( ) {
			return score;
		}
		
		@Override
		public String toString ( ) {
			return getName ( );
		}
	}
	
	private MapVoting ( AnnihilationMain plugin ) {
		plugin.getCommand ( "Vote" ).setExecutor ( this );
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler
	public void onClick ( PlayerInteractEvent event ) {
		if ( ( event.getAction ( ) == Action.RIGHT_CLICK_AIR || event.getAction ( ) == Action.RIGHT_CLICK_BLOCK ) 
				&& KitUtils.itemHasName ( event.getItem ( ) , CustomItem.VOTEMAP.getName ( ) ) ) {
			event.setCancelled ( true );
			
			PlayerInteractAtAnniItemEvent eve = new PlayerInteractAtAnniItemEvent ( event );
			Bukkit.getPluginManager ( ).callEvent ( eve );
			
			if ( !eve.isCancelled ( ) ) {
				MapVotingMenu.openMenu ( event.getPlayer ( ) );
			}
		}
	}
	
	@EventHandler
	public void onDisconnect ( PlayerLeaveServerEvent event ) {
		removeVote ( event.getPlayer ( ).getID ( ) );
	}
	
	@Override
	public boolean onCommand ( CommandSender sender , Command command , String label , String [ ] args ) {
		if ( Game.isGameRunning ( ) ) {
			return true;
		}
		
		if ( sender instanceof Player ) {
			if ( VOTING_MAPS0.isEmpty ( ) ) {
				sender.sendMessage ( Lang.NO_MAPS_FOR_VOTING.toString ( ) );
			} else {
				if ( args.length > 0 ) {
					String map_name = args [ 0 ].trim ( );
					boolean    flag = false;
					for ( VotingMap map : VOTING_MAPS0 ) {
						if ( !map.getName ( ).equalsIgnoreCase ( map_name ) ) {
							continue;
						}
						
						vote ( (Player) sender , map );
						flag = true;
						break;
					}
					
					if ( flag ) {
						sender.sendMessage ( Lang.VOTED_FOR_MAP.toStringReplacement ( map_name ) );
					} else {
						sender.sendMessage ( Lang.INVALID_MAP.toStringReplacement ( map_name ) );
					}
				}
			}
			
		} else {
			sender.sendMessage ( Lang.MUST_BE_PLAYER_COMMAND.toString ( ) );
		}
		return true;
	}
}