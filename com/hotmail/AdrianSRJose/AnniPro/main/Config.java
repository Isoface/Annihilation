package com.hotmail.AdrianSRJose.AnniPro.main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;

import com.hotmail.AdrianSRJose.AnniPro.utils.Utf8YamlConfiguration;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public enum Config {
	
	// LOBBY
	LOBBY_MODE("Lobby.lobby-mode", false),
	GAME_MODE("Lobby.game-mode", "ADVENTURE"),
	USE_SPECTATOR_MODE("Lobby.use-spectator-mode", true),
	USE_KITS_SHOP_ITEM("Lobby.use-kits-shop-item", true),
	USE_STRIKE_LIGHTNING_EFFECT("Lobby.use-strike-lightning-effect", true),
		// COMMPAS TO LOBBy
		USE_LOBBY_COMMPAS("Lobby.LobbyCommpas.use", true),
		LOBBY_COMMPAS_SERVER_TO("Lobby.LobbyCommpas.server-to", "bungee server name"),
		LOBBY_COMMPAS_KICK_PLAYER("Lobby.LobbyCommpas.kick-player", false),
		LOBBY_COMPAS_USING_COMMAND("Lobby.LobbyCommpas.using-command", false),
		LOBBY_COMPAS_COMMAND_TO_PERFORM("Lobby.LobbyCommpas.command-to-perform", "command to perform here"),
		// FORTUNE_
		USE_FORTUNE("Lobby.Fortune.use", true),
		FORTUNE_DIAMOND("Lobby.Fortune.diamond", true),
		FORTUNE_EMERALD("Lobby.Fortune.emerald", true),
		FORTUNE_COAL("Lobby.Fortune.coal", true),
		FORTUNE_IRON("Lobby.Fortune.iron", true),
		FORTUNE_GOLD("Lobby.Fortune.gold", true),
	
	// GAME
	USE_MOTD("Game.use-motd", false),
	USE_OLD_ENCHANTMENT_SYSTEM("Game.use-old-enchant-system", true),
	USE_OWNER_MARK("Game.use-owner-mark", true),
	USE_AUTO_LAPIZ("Game.use-auto-lapiz", true),
	USE_SPECIFIC_TOOL("Game.use-specific-tool", true),
	USE_PERMISSIONS_PREFIX("Game.use-Permissions-prefix", true),
	USE_PHASE_TITLES("Game.use-phase-titles", true),
	USE_ANTI_SPEED_MINE_HACK("Game.use-anti-speed-mine-hack", true),
	SCOREBOARDS_REFRESH_DELAY ( "Game.scoreboards-refresh-delay" , 20 ) ,
	SEND_PLAYERS_TO_HIS_NEXUS_ON_RECONNECT("Game.send-players-to-his-nexus-on-reconnect", false),
	REMOVE_INVISIBILITY_ON_BLOCK_BREAK("Game.remove-invisibility-on-block-break", true),
	REMOVE_INVISIBILITY_ON_DAMAGE_NEXUS("Game.remove-invisibility-on-damage-nexus", true),
	REMOVE_INVISIBILITY_ON_GET_DAMAGE("Game.remove-invisibility-on-damage", true),
	USE_TEAM_DEATH_TITLES("Game.use-title-on-team-dead", true),
//	USE_BLOOD_EFFECT("Game.use-blood-effect", true),
	KILL_PLAYERS_ON_CHANGE_KIT("Game.kill-players-on-change-kit", false),
	SPAWN_IMMUNITY_SECONDS("Game.spawn-immunity-seconds", 0),
	
//		// Game Backup
//		USE_GAME_BACKUP("Game.GameBackup.Use", true),
//		GAME_BACKUP_PERIOD("Game.GameBackup.period", 2),
//		GAME_BACKUP_PERIOD_UNIT("Game.GameBackup.period-unit", TimeUnit.MINUTES.name()),
	
		// Resuscitator
		USE_RESUSCITATOR("Game.Resuscitator.Use", true),
		RESUSCITATOR_PHASE("Game.Resuscitator.add-in-phase", 5),
		RESUSCITATOR_APPLE_AMMOUNT("Game.Resuscitator.apple-cost-ammount", 3),
	
	END_OF_GAME_COUNTDOWN("Game.end-of-game-countdown", 120),
	END_OF_GAME_COMMANDS_PLAYERS("Game.EndGameCommands.ForPlayers", Arrays.asList(new String[] { "lobby" })),
	END_OF_GAME_COMMANDS("Game.EndGameCommands.ForConsole", Arrays.asList(new String[] { "stop" })),
	
	// -- GAME VARS -- //
	// ANNOUNCEMENT
	USE_CHAT_PHASE_CHANGE_IMAGES("GameVars.Announcement.use-chat-phase-change-images", true),
	USE_GAME_END_IMAGE("GameVars.Announcement.use-game-end-image", true),
	USE_CHAT_TEAM_IMAGES("GameVars.Announcement.use-chat-team-images", true),
	USE_BOSS_RESPAWN_MESSAGE("use-boss-respawn-message", true),
	USE_BOSS_DEATH_MESSAGE("use-boss-death-message", true),
		// BOSS BAR
		USE_BOSS_BAR("GameVars.Announcement.BossBar.use", true),
		BOSS_BAR_REMOVE_IN_PHASE_5("GameVars.Announcement.BossBar.delete-in-phase-5", false),
		// PHASE BAR
		USE_PHASE_BAR("GameVars.Announcement.PhaseBar.use", true),
		PHASE_BAR_REMOVE_IN_PHASE_5("GameVars.Announcement.PhaseBar.delete-in-phase-5", false),
		
	// Teams
	TEAMS_INITIALI_HEALTH ( "GameVars.Teams.initial-health" , 75 ) ,
	TEAMS_USE_NAME_TAGS ( "GameVars.Teams.use-custom-name-tags" , true ) ,
	
	// NPCs
	USE_NPC("GameVars.NPCs.use", false),
	NPC_COMBAT_MODE_SECONDS("GameVars.NPCs.combat-mode-seconds", 10),
	NPC_SPAWN_ONLY_IN_COMBAT("GameVars.NPCs.spawn-only-in-combat", false),
	NPC_CHANGE_PLAYER_HEALTH_TO_NPC_HEALTH("GameVars.NPCs.change-player-health-to-npc-health", true),
	NPC_DROP_ITEMS("GameVars.NPCs.drop-items-on-death", true),
	
	// AUTO START
	USE_AUTO_START("GameVars.AutoStart.use", false),
	AUTO_START_PLAYERS_TO_START("GameVars.AutoStart.players-to-start", 4),
	AUTO_START_COUNTDOWN_TIME("GameVars.AutoStart.countdown-time", 30),
	
	// AUTO RESTART
	USE_AUTO_RESTART("GameVars.AutoRestart.use", false),
	AUTO_RESTART_COMMAND("GameVars.AutoRestart.command", "stop"),
	AUTO_RESTART_PLAYERS_TO_AUTO_RESTART("GameVars.AutoRestart.players-to-auto-restart", 0),
	AUTO_RESTART_MINIMAL_PHASE_TO_RESTART("GameVars.AutoRestart.minimal-phase-to-restart", 2),
	AUTO_RESTART_COUNTDOWN_TIME("GameVars.AutoRestart.countdown-time", 15),
	
	// MAP_LOADING_
	MAP_LOADING_VOTING("GameVars.MapLoading.voting", true),
	MAP_LOADING_MAX_MAPS_FOR_VOTING("GameVars.MapLoading.max-maps-for-voting", 3),
	MAP_LOADING_USE_MAP("GameVars.MapLoading.use-map", "plugins/Annihilation/" + AnniSubDirectory.WORLDS_DIRECTORY.getName ( ) + "/Test"),
	MAP_LOADING_CAN_BREAK_REGENERATING_BLOCKS_ON_AREA("GameVars.MapLoading.can-break-regenerating-blocks-on-area", true),
	MAP_LOADING_CAN_BREAK_REGENERATING_BLOCKS_COBBLESTONE("GameVars.MapLoading.can-break-regenerating-blocks-cobblestone", true),
	MAP_LOADING_ALWAYS_DAY("GameVars.MapLoading.always-day", false),
	MAP_LOADING_DISABLE_RAIN("GameVars.MapLoading.disable-rain", false),
	MAP_LOADING_DIAMONDS_SPAWN_PHASE("GameVars.MapLoading.diamonds-spawn-phase", 3),
	
	// BOSS_MAP_LOADING_
	BOSS_MAP_LOADING_DEFAULT_BOSS_MAP("GameVars.Boss-MapLoading.default-boss-map", "Map Folder Name"),
	BOSS_MAP_LOADING_USE_BOSS_MAP("GameVars.Boss-MapLoading.use-boss-map", false),
	BOSS_MAP_LOADING_SEND_PLAYER_FROM_PORTAL_TO_BASE("GameVars.Boss-MapLoading.send-player-from-portal-to-base", true),
	BOSS_MAP_LOADING_BOSS_SPAWN_PHASE("GameVars.Boss-MapLoading.boss-spawn-phase", 4),
	
	// TEAM_BALANCING_
	USE_TEAM_BALANCING("GameVars.TeamBalancing.use", true),
	TEAM_BALANCING_TOLERANCE("GameVars.TeamBalancing.tolerance", 2),
	TEAM_BALANCING_MAX_PLAYERS("GameVars.TeamBalancing.max-players", 0),
	USE_AUTO_TEAM_SELECTOR("GameVars.TeamBalancing.use-auto-team-selector", true),
	USE_TEAM_SELECTOR("GameVars.TeamBalancing.use-team-selector", true),
	
	// END
	;

	// Variables
	private final String path;
	private final Object def;
	private static Utf8YamlConfiguration file;
	private static File rawFile;

	Config(String path, Object def) {
		this.path = path;
		this.def = def;
	}

	public static void set(String path, Object val) {
		if (file != null) {
			file.set(path, val);
		}
	}

	public static void save() {
		if (rawFile != null && file != null) {
			try {
				file.save(rawFile);
			} catch (IOException e) {
			}
		}
	}

	public String getPath() {
		return path;
	}

	public Object getDefault() {
		return def;
	}

	/**
	 * Set the {@code YamlConfiguration} to use.
	 * 
	 * @param config
	 *            The config to set.
	 */
	public static void setFile(Utf8YamlConfiguration config) {
		file = config;
	}

	public static void setRawFile(File f) {
		rawFile = f;
	}
	

	@Override
	public String toString() {
		if (!(def instanceof String)) {
			return null;
		}

		final String gg = file.getString(path, (String) def);
		return Util.wc(gg != null ? gg : "" + def);
	}
	
	public GameMode toGameMode() {
		try {
			GameMode tor = GameMode.valueOf(file.getString(path));
			if (tor != null) {
				return tor;
			}
		}
		catch(Throwable t) {
			return GameMode.ADVENTURE;
		}
		return GameMode.ADVENTURE;
	}
	
	public File toFile() {
		return new File(file.getString(path));
	}

	public int toInt() {
		if (!(def instanceof Integer)) {
			return 0;
		}

		return file.getInt(path, (Integer) def);
	}

	public boolean toBoolean() {
		if (!(def instanceof Boolean)) {
			return false;
		}

		return file.getBoolean(path, (Boolean) def);
	}

	@SuppressWarnings("unchecked")
	public List<String> toStringList() {
		if (!(def instanceof List))
			return null;
		//
		final List<String> list = file.getStringList(path);
		//
		return list != null ? list : (List<String>) def;
	}
}
