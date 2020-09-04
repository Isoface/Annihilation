package com.hotmail.AdrianSRJose.AnniPro.anniGame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.utils.PlayerPrefix;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.Custom.StringUtils;
import com.hotmail.AdrianSRJose.AnniPro.voting.ChatVars;

/**
 * Represents the annihilation
 * chat.
 * <p>
 * @author AdrianSR
 */
public final class ChatListener implements Listener {

	/**
	 * Construct new annihilation
	 * chat.
	 * <p>
	 * @param plugin the Annihilation plugin instance.
	 */
	public ChatListener(final AnnihilationMain plugin) {
		// register events in this class.
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void annihilationChat(AsyncPlayerChatEvent event) {
		// check is asynchronous.
		if (!event.isAsynchronous()) {
			return;
		}
		
		// check no empty message.
		if (event.getMessage().isEmpty()) {
			return;
		}
		
		// get player and his anniplayer.
		final Player p      = event.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		
		// booleans.
		boolean team_chat   = ap.hasTeam();
		boolean global_chat = false;
		
		// check chat message.
		if (event.getMessage().startsWith("!")) {
			// set is global chat and is not team chat.
			global_chat = true;
			team_chat   = false;
			
			// check length.
			if (event.getMessage().length() == 1) {
				event.setCancelled(true); // cancell because is a empty message.
				return;
			}
			
			// remove '!' from message.
			event.setMessage(event.getMessage().substring(1));
		}
		
		// check player is on lobby.
		if (!ap.hasTeam()) {
			global_chat = true;
		}
		
		// get variables.
		String variables = "";
		
		// add global variable.
		if (global_chat) {
			// add global variable.
			variables += ChatVars.getVariableGlobalMessageFormat();
			
			// check team.
			if (ap.hasTeam()) {
				// add team name to format.
				if (!team_chat) {
					variables += ChatVars.getVariableOnTeamFormat().replace("%team", ap.getTeam().getExternalColoredName());
				}
			}
		}
		
		// add team chat variable and team name.
		if (team_chat) {
			variables += ChatVars.getVariableTeamMessageFormat();
			variables += ChatVars.getVariableOnTeamFormat().replace("%team", ap.getTeam().getExternalColoredName());
		}
		
		// add on lobby variable.
		if (!ap.hasTeam()) {
			variables += ChatVars.getVariableOnLobbyFormat();
		}
		
		// get prefix.
		String prefix = "";
		final PlayerPrefix player_prefix = new PlayerPrefix(p);
		if (Config.USE_PERMISSIONS_PREFIX.toBoolean()) {
			if (player_prefix.getPermissionsExPrefix() != null) {
				prefix = player_prefix.getPermissionsExPrefix();
			} else if (player_prefix.getPowerfulPermsPrefix() != null) {
				prefix = player_prefix.getPowerfulPermsPrefix();
			} else if (player_prefix.getUltraPermissionsPrefix() != null) {
				prefix = player_prefix.getUltraPermissionsPrefix();
			} else if ( player_prefix.getLuckPermsPrefix ( ) != null ) {
				prefix = player_prefix.getLuckPermsPrefix ( );
			} else {
				prefix = "";
			}
		}
		
		// get formats.
//		final String event_format  = event.getFormat();
		final String config_format = ChatVars.getFormat();
		
		// get final format.
		String final_format = config_format;
		
		// check obni message.
		if (ap.getData("Message-Obni-Added") instanceof String) {
			String obni_message = (String) ap.getData("Message-Obni-Added");
			if (obni_message != null && !obni_message.isEmpty() && !StringUtils.isBlank(obni_message)) {
				obni_message += " ";
				final_format = final_format.replace("{PLAYER_NAME}", obni_message + "{PLAYER_NAME}");
			}
		}
		
		// replace format variables.
		final_format        = final_format.replace("{VARIABLES}", variables);
		final_format        = final_format.replace("{PREFIX}", prefix);
		final_format        = final_format.replace("{PLAYER_NAME}", p.getDisplayName());
		final_format        = final_format.replace("{MESSAGE}", event.getMessage());
		final_format        = final_format.replace("  ", " "); // remove double spaces.
		
		// change format.
		event.setFormat(Util.wc(final_format));
		
		// message for team mates.
		if (!global_chat) {
			// clear default recipients.
			event.getRecipients().clear();
			
			// add team mate recipients.
			for (AnniPlayer team_mate : ap.getTeam().getPlayers()) {
				if (team_mate != null && team_mate.isOnline()) {
					event.getRecipients().add(team_mate.getPlayer());
				}
			}
		}
	}
}
