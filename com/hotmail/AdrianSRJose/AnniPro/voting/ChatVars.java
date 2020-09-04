package com.hotmail.AdrianSRJose.AnniPro.voting;

import org.bukkit.configuration.ConfigurationSection;

public class ChatVars {
	
	public static boolean splitKitName = true;
	
	private static String VariableGlobalMessageFormat = "&7(Global) ";
	private static String VariableTeamMessageFormat = "&7(Team) ";

	private static String VariableOnLobbyFormat = "[&5Lobby&7] ";
	private static String VariableOnTeamFormat = "[%team&7] ";
	private static String Format = "{VARIABLES} {PREFIX} &r&7{PLAYER_NAME}&r&f: {MESSAGE}";

	public static String getFormat() {
		return Format;
	}

	public static String getVariableGlobalMessageFormat() {
		return VariableGlobalMessageFormat;
	}

	public static String getVariableTeamMessageFormat() {
		return VariableTeamMessageFormat;
	}

	public static String getVariableOnLobbyFormat() {
		return VariableOnLobbyFormat;
	}

	public static String getVariableOnTeamFormat() {
		return VariableOnTeamFormat;
	}

	public static void loadChatVars(ConfigurationSection config) {
		if (config != null) {
			//
			splitKitName = config.getBoolean("Split-kit-name", true);
			//
			if (config.isString("Variable-Global-Message-Format"))
				VariableGlobalMessageFormat = config.getString("Variable-Global-Message-Format");
			//
			if (config.isString("Variable-Team-Message-Format"))
				VariableTeamMessageFormat = config.getString("Variable-Team-Message-Format");
			//
			if (config.isString("Variable-On-Lobby-Format"))
				VariableOnLobbyFormat = config.getString("Variable-On-Lobby-Format");
			//
			if (config.isString("Variable-On-Team-Format"))
				VariableOnTeamFormat = config.getString("Variable-On-Team-Format");
			//
			if (config.isString("Format"))
				Format = config.getString("Format");
		}
	}
}
