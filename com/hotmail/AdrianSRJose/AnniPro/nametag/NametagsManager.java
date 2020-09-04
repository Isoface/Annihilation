package com.hotmail.AdrianSRJose.AnniPro.nametag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.manager.CustomPluginManager;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJoinTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.nametagedit.plugin.NametagManager;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Sunday 12 July, 2020 / 06:36 PM
 */
public class NametagsManager extends CustomPluginManager {
	
	public static NametagsManager getInstance ( ) {
		return (NametagsManager) CustomPluginManager.MANAGER_INSTANCES.get ( NametagsManager.class );
	}
	
	private final Map < UUID , Boolean > name_tag_states = new HashMap < > ( );
	
	protected final NametagManager handle;

	public NametagsManager ( AnnihilationMain plugin ) {
		super ( plugin );
		this.handle = new NametagManager ( );
		
		// we are initializing this manager, but this is really useful only when the
		// name tags are enabled in config.
		if ( Config.TEAMS_USE_NAME_TAGS.toBoolean ( ) ) {
			registerEvents ( );
			
			// if this is a 1.8 server, we start a task that hides the team name tags when a
			// player is invisible.
			if ( ServerVersion.serverSameVersion ( ServerVersion.v1_8_R1 ) ) {
				Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , new Runnable ( ) {
					@Override public void run ( ) {
						for ( Player player : Bukkit.getOnlinePlayers ( ) ) {
							final AnniPlayer ap = AnniPlayer.getPlayer ( player );
							if ( !ap.hasTeam ( ) ) {
								continue;
							}
						
							final boolean invisible = player.hasPotionEffect ( PotionEffectType.INVISIBILITY );
							if ( name_tag_states.containsKey ( player.getUniqueId ( ) ) ) {
								if ( invisible != name_tag_states.get ( player.getUniqueId ( ) ).booleanValue ( ) ) {
									if ( invisible ) {
										clearNametag ( player );
									} else {
										setNametag ( ap );
									}
								}
							} else {
								if ( invisible ) {
									clearNametag ( player );
								} else {
									setNametag ( ap );
								}
							}
							name_tag_states.put ( player.getUniqueId ( ) , invisible );
						}
					}
				} , 5 , 5 );
			}
		}
	}
	
	@EventHandler
	public void onJoinTeam ( PlayerJoinTeamEvent event ) {
		setNametag ( event.getPlayer ( ) , event.getTeam ( ) );
	}
	
	@EventHandler
	public void onLeaveTeam ( PlayerLeaveTeamEvent event ) {
		clearNametag ( event.getPlayer ( ) );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
    public void onPlayerJoin ( PlayerJoinEvent event ) {
		final AnniPlayer player = AnniPlayer.getPlayer ( event.getPlayer ( ) );
		if ( player.hasTeam ( ) ) {
			setNametag ( player );
		}
		
		handle.sendTeams ( event.getPlayer ( ) );
    }
	
    @EventHandler
    public void onQuit ( PlayerQuitEvent event ) {
    	clearNametag ( event.getPlayer ( ) );
    }
    
    @EventHandler
    public void onWorldChange ( PlayerChangedWorldEvent event ) {
    	final AnniPlayer player = AnniPlayer.getPlayer ( event.getPlayer ( ) );
    	if ( player.hasTeam ( ) ) {
    		setNametag ( player );
    	}
    }
    
    protected void setNametag ( AnniPlayer player , AnniTeam team ) {
    	handle.setNametag ( player.getName ( ) , team.getPrefix ( ) , "" );
    }
    
    protected void setNametag ( AnniPlayer player ) {
    	setNametag ( player , player.getTeam ( ) );
    }
    
    protected void clearNametag ( AnniPlayer player ) {
    	handle.reset ( player.getName ( ) );
    }
    
    protected void clearNametag ( Player player ) {
    	handle.reset ( player.getName ( ) );
    }
}