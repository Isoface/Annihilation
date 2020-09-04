package com.hotmail.AdrianSRJose.AnniPro.main;

import org.bukkit.ChatColor;

import com.hotmail.AdrianSRJose.AnniPro.utils.Utf8YamlConfiguration;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public enum Lang {
	// %# will be replaced with the a number
	// %w will be replaced with a word when needed
	// %n will be the line separator
	// Modelo : (Aqui El Nombre)("", ChatColor.GOLD+(ChatColor.BOLD+"")),

	COMPASSTEXT("compass-text", "Pointing to the %w Nexus"),

	SMHD("speed-mine-hack-kick-message", "&c&lSpeed Mine Hack Detected!"),
	
	SOULBOUND("Soulbound-Lore", ChatColor.GOLD + "Soulbound"),
	CLASS_ITEM("Class-Item", ChatColor.GOLD + "Class Item"),
	BOSS_OBJECT("Boss-Object", ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "BossObject"),
	UNDROPABBLE("Undropabble-Lore", ChatColor.GOLD + "Undropabble"),
	// SCOREBOARDMAP("scoreboard-mapa-Mundo","Map:"),

	// Scoreboard phase
	// SCOREBOARDEVOTACION("Scoreboard-Vote-Name",
	// ChatColor.GOLD+(ChatColor.BOLD+"/Vote [Map Name] To Vote")),
	// PLAYERS_PREFIX_LOBBY("Players-Prefix-Lobby",
	// ChatColor.WHITE+"["+ChatColor.DARK_PURPLE+ChatColor.BOLD+"L"+ChatColor.RESET.toString()+ChatColor.WHITE+"]"+ChatColor.DARK_PURPLE+"
	// "),
	// PLAYERS_PREFIX_LOBBY("Players-Prefix-Lobby", "&f[&5&lL&r&f]&5 "),
	// PLAYERS_PREFIX_TEAM("Players-Prefix-Team",
	// ChatColor.RESET.toString()+ChatColor.WHITE+"["+"%tc"+ChatColor.BOLD+"%w"+ChatColor.RESET.toString()+ChatColor.WHITE+"]"+"%tc
	// "),

	MOTDINLOBBY("Motd-In-Lobby", ChatColor.GREEN + (ChatColor.BOLD + "In Lobby")), 
	MOTDINGAME("Motd-In-Game",
			ChatColor.GOLD + (ChatColor.BOLD + "Phase: " + (ChatColor.UNDERLINE + "%#") + ChatColor.RESET + "%n"
					+ ChatColor.GOLD + (ChatColor.BOLD + "Map: " + (ChatColor.UNDERLINE + "%w")))),

	// SCOREBOARDPHASE("Scoreboard-Phase-Name",ChatColor.DARK_PURPLE+"Current
	// Phase"),

	TITLE_FASE_NOMBRE("Title-on-Phase-Start", ChatColor.GOLD + (ChatColor.BOLD + "Phase %#")), 
	STARTING_TITLE("Title-starting-game", ChatColor.GREEN + ChatColor.BOLD.toString() + "Starting"),
	SUBTITLE_FASE_1("Subtitle-Phase-1", ChatColor.GOLD + (ChatColor.BOLD + "The Game Has Begun")), 
	SUBTITLE_FASE_2("Subtitle-Phase-2", ChatColor.GOLD + (ChatColor.BOLD + "The Phase 2 Has Begun")), 
	SUBTITLE_FASE_3("Subtitle-Phase-3", ChatColor.GOLD + (ChatColor.BOLD + "The Phase 3 Has Begun")), 
	SUBTITLE_FASE_4("Subtitle-Phase-4", ChatColor.GOLD + (ChatColor.BOLD + "The Phase 4 Has Begun")), 
	SUBTITLE_FASE_5("Subtitle-Phase-5", ChatColor.GOLD + (ChatColor.BOLD + "The Phase 5 Has Begun")),

	TITLE_EQUIPO_DESTRUIDO("Title-On-Team-Dead",
			ChatColor.DARK_RED + (ChatColor.BOLD + "Game Over")), 
	
	SUBTITLE_EQUIPO_DESTRUIDO("Subtitle-On-Team-Dead",
					ChatColor.RED + (ChatColor.BOLD + "Your Nexus Has Been Destroyed")),

	TITLE_PARTIDA_PERDIDA("Title-On-Lost-Game", ChatColor.DARK_RED + (ChatColor.BOLD + "Your Team Have Lost the Game")),

	TITLE_PARTIDA_GANADA("Title-On-Win-Game", ChatColor.GREEN + (ChatColor.BOLD + "!VICTORY!")),

	GAME_END_BROADCAST_MESSAGE("Winnig-Team-BroadCast-Message",
			ChatColor.DARK_PURPLE + "The game has ended!. The %w Team has won!"),

	BOSS_RESPAWN_MESSAGE("Boss-Respawn-Message",
			ChatColor.GRAY + (ChatColor.BOLD + "The Boss Has been " + ChatColor.GOLD
					+ (ChatColor.BOLD + "Respawned"))), 
	
	WITCH_RESPAWN_MESSAGE("Witch-Respawn-Message",
							ChatColor.GOLD.toString() + ChatColor.BOLD + "The Witch %w " + ChatColor.GOLD.toString()
							+ ChatColor.BOLD + "has been respawned!"),

	TEAM("the-word-team", "Team"),

	// Message on Join on Spectator Mode
	ENTERONSPECTATORMODE("Message-on-join-on-spectator-mode", ChatColor.GREEN + "§lNow You Are a Spectator!. Use: "
			+ ChatColor.GOLD + "/Spectator [exit or quit]" + ChatColor.GREEN + "  |  to return to Lobby"),

	// Team destroyed message
	TEAMDESTROYED("team-destroyed", "%tcThe %w Team Has Been %n&cDestroyed &fby &6%PLAYER%!."),

	// Death messages
	DEATHPHRASE("killed", ChatColor.GRAY + "Killed"), 
	
	DEATHPHRASESHOT("killed-shot",
			ChatColor.GRAY + "shot"), 
	
	DEATHPHRASEVOID("killed-void",
					ChatColor.GRAY + "threw the void to"), 

	NPC_KILLED("NPC-Killed", ChatColor.RED + "Your NPC was killed by %PLAYER%"),

	// BOSSDEATHMESSAGE("boss-death-message", ChatColor.GOLD + (ChatColor.BOLD + "The %w Team&r" + ChatColor.GOLD + (ChatColor.BOLD + " has Been Killed the Boss"))), 
	BOSSDEATHMESSAGE("boss-death-message", ChatColor.GRAY + "The %w Team&r" + ChatColor.GRAY + " has Been Killed the Boss"),

	REMEMBRANCE(
			"kill-with-dead-team",
			ChatColor.GRAY + "In Remembrance Of His Team"), 
	
	NEXUSKILL("kill-near-nexus",
					ChatColor.GRAY + "In Defense of His Nexus"),

	NEXUS_DAMAGED_PHASE1("nexus-damaged-phase1", ChatColor.RED + "You cannot hit a nexus in Phase 1"),
	NEXUS_DAMAGED("nexus-damaged", ChatColor.GRAY + "%PLAYER% Has Damaged %hit_team"),

	// Civilian kit name
	CIVILIANNAME("civilian-kit-name", "Civilian"), 
	
	CIVILIAN_CRAFT_ITEM("civilian-item-name",
			ChatColor.YELLOW + "Craft O' Matic"), 
	
	CIVILIANLORE("civilian-kit-lore",
					"&bYou are the backbone.%n%n&bFuel all facets of the%n&bwar machine with your%n&bset of wooden tools and%n&bprepare for battle!"),

	// Kits Lore
	// KITS_FINAL_LORE("FinalLore", ChatColor.GOLD + "--------------------------"),
	// LOCKED_KIT("LockedKit", ChatColor.RED+"LOCKED"),
	// UNLOCKED_KIT("UnlockedKit", ChatColor.GREEN+"UNLOCKED"),
	// &b is the color aqua

	NAME_ALDEANO_JOINERS("Villager-Joiners-Name", "&l&nJoin on the %w Team"),

	JOINER_ARMOR_STAND_NAMES("Armor-Stands-Joiner-Names",
			ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Join on %w Team"), 
	
	RAMDON_JOINER_ARMOR_STAND_NAMES(
					"Armor-Stands-Random-Joiner-Names",
					ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Join on a Random Team"),
	
	JOINER_ARMOR_STANDS_COUNT_NAME("armor-stands-joiner-count-name", "%# " + ChatColor.GREEN + "Members"),

	BOSS_SHOP_NAME("Boss-Shop-Name", ChatColor.DARK_RED + (ChatColor.BOLD + (ChatColor.UNDERLINE + "Boss Shop"))),
	MENSAJE_DEL_BOSS_SHOP("Boss-Shop-Open", ChatColor.DARK_RED + "Select"),
	// NOMBRE_DEL_MENU_DE_KITS("Kits-Menu-Name",ChatColor.GOLD+(ChatColor.BOLD+"%PLAYER%
	// Kits")),
	TEAM_MENU_NAME("Team-Menu-Name",
			ChatColor.GOLD + (ChatColor.BOLD + "Join a Team")), 
	
	TEAM_LEAVE_BUTTON_NAME(
					"Team-Leave-Button-Name", ChatColor.AQUA + "Leave a Team"),
	
	AUTO_TEAM_BUTTON_NAME(
							"Auto-Team-Button", ChatColor.AQUA + "Auto Join"),

	EXIT_BUTTON("exit-button-name",
									ChatColor.RED + "Exit"), 
	
	BOTON_OPEN_KIT_SHOP("Kit-Shop-Button",
											ChatColor.GOLD + (ChatColor.BOLD + "$ Kits Shop $")), 
	
	VOTING_MAP_MENU(
													"Maps-Menu-Name",
													ChatColor.GOLD + (ChatColor.BOLD
															+ "Vote for a Map")),
	VOTING_MAP_MENU_FORMAT ( "Maps-Menu-Format" , ChatColor.GOLD.toString ( ) + ChatColor.BOLD + "%w" ),
	NOMBRE_DEL_COFRE_PRIVADO(
																	"Private-Chest-Name", ChatColor.DARK_RED
																	+ (ChatColor.BOLD + "Private Chest")),

	NOMBRE_DEL_HORNO_PRIVADOR("Private-Furnace-Name",
			ChatColor.DARK_RED + (ChatColor.BOLD + "Ender Furnace")), 
	
	PRIVATE_BREWING_NAME("Private-Brewing-Name",
					ChatColor.DARK_RED + (ChatColor.BOLD + "Ender Brewing")),

	// Custom Items
	NAVCOMPASS("nexus-compass-name", ChatColor.DARK_PURPLE + "Right Click To Change Target Nexus"), 
	
	KITMAP(
			"kit-book-name", ChatColor.AQUA + "Right Click To Choose A Kit"), 
	
	VOTEMAP("vote-item-name",
					ChatColor.AQUA + "Right Click To Vote For A map"), 
	
	TEAMMAP("team-iteam-name",
							ChatColor.AQUA + "Right Click To Join A Team"), 
	
	SPECTATORMAP("Spectator-Menu-Name",
									ChatColor.AQUA + "Right Click To View a Team"), 
	
	SPECTATOR_BUTTON(
											"Spectator-Items-Name",
											ChatColor.AQUA + ChatColor.BOLD.toString()
											+ "View the %w Team"), 
	
	OPTIONSITEM(
													"Options-Item-Name",
													ChatColor.AQUA
													+ "Right Click to Open the OptionsMenu"), 
	
	GO_TO_LOBBY(
															"Teleport-Lobby-Item-Name",
															ChatColor.DARK_RED
															+ "Right Click to Go to Lobby"),
	
	BOSS_STAR("BossStar-Item-Name", ChatColor.DARK_RED + (ChatColor.BOLD + "BossStar")),
	
	KITS_SHOP(
																	"Kits-Shop-Item-Name",
																	ChatColor.GOLD
																	+ "Right Click to Buy a Kit"),

	// Invis reveal
	INVISREVEAL("invis-reveal", ChatColor.GOLD + "You Have Been Revealed!"),

	// Resuscitator
	RESUSCITATOR_ITEM("resuscitator-item-name", ChatColor.GOLD + "Resuscitator"),
	RESUSCITATOR_INVENTORY("resuscitator-inventory-name", "Resuscitator"),
	RESUSCITATOR_SUCCESSFULLY("resuscitator-successfully", ChatColor.GREEN + "%w has ben resurrected!"),
	RESUSCITATOR_NO_APPLES("resuscitator-no-apples", ChatColor.RED + "You don't have enough gold apples to resurrect your team mate."),
	RESUSCITATOR_LIVING_TEAM("resuscitator-living-team", ChatColor.RED + "Your team must be dead to bring a dead team mate back to life."),
	RESUSCITATOR_EMPTY_TEAM("resuscitator-no-team-mates", ChatColor.RED + "No team mates to resurrect"),

	// Magic beacon
	MAGIC_BEACON_CAPTURED("magic-beacon-captured", ChatColor.LIGHT_PURPLE + "The %w Team" + ChatColor.LIGHT_PURPLE + " has captured the Magic Beacon!"),
	MAGIC_BEACON_YOUR_BEACON_IS_CAPTURED("magic-beacon-your-beacon-captured", ChatColor.RED + "Your Magic Beacon has been captured by the %w Team"),
	MAGIC_BEACON_CANNOT_CAPTURE("magic-beacon-cannot-capture", ChatColor.RED + "You cannot capture the magic beacon while your team is dead."),
	MAGIC_BEACON_HAS_BEEN_PLACED("magic-beacon-placed", ChatColor.LIGHT_PURPLE + "The %w Team " + ChatColor.LIGHT_PURPLE + "has placed the Magic Beaon on its base."),
	MAGIC_BEACON_CANNOT_BE_PLACED("magic-beacon-cannot-be-placed", ChatColor.RED + "You can only place this block in the beacon support of your base."),
	MAGIC_BEACON_DROPED("magic-beacon-droped", ChatColor.LIGHT_PURPLE + "The %w Team " + ChatColor.LIGHT_PURPLE + "has droped the Magic Beacon."),
	MAGIC_BEACON_CANNOT_DESTROY("magic-beacon-cannot-destroy-your-beacon", ChatColor.RED + "You cannot destroy your own Magic Beacon!"),
	MAGIC_BEACON_ALREADY_DESTROY("magic-beacon-already-destroyed", ChatColor.RED + "This beacon is already destroyed!"),
	MAGIC_BEACON_NOT_ENABLED("magic-beacon-not-destroyed", ChatColor.RED + "This beacon is not enabled!"),
	
	// Signs
	SHOP("the-word-shop", ChatColor.DARK_PURPLE + "Shop"), 
	
	TEAMSIGN("team-join-sign",
			ChatColor.DARK_PURPLE + "Right Click%n" + ChatColor.DARK_PURPLE + "To Join:%n" + "%w Team"), 
	
	BREWINGSIGN(
					"brewing-shop-sign",
					ChatColor.BLACK + "Brewing"), 
	
	WEAPONSIGN("weapon-shop-sign", ChatColor.BLACK + "Weapon"),
	
	// Beacon
	BEACON_COULD_NOT_PURCHASE("beacon-could-not-purchase", ChatColor.RED + "Could Not Purchase This Potion. Please Check The Price Again."),
	BEACON_PURCHASED_POTION("beacon-purchased-potion", ChatColor.GREEN + "Potion Purchased!"),

	// Shop strings
	PURCHASEDITEM("shop-item-purchased", ChatColor.GREEN + "Item Purchased!"),
	
	COULDNOTPURCHASE(
			"shop-item-not-purchased", ChatColor.RED + "Could Not Purchase The Item. Please Check The Price Again."),

	// Cant Messages
	GAME_IS_NOT_RUNNING("the-game-is-not-running", "&cThe Game is not Running!"), 
	
	INVALID_COMMAND_SYNTAX(
			"invalid-command-syntax", "&cInvalid Command Syntax!"), 
	
	INVALID_PLAYER("invalid-player",
					"&cInvalid Player!"), 
	
	WITHOUT_PERMISSION_COMMAND("without-permission-command",
							"&cYou do not have permission to use this command!"), 
	
	MUST_BE_PLAYER_COMMAND(
									"must-be-player-command",
									"&cYou must be a player to use this command!"), 
	
	CANT_OPEN_ENDER_FURNACE_GAME_NOT_RUNNING(
											"cant-open-ender-furnace-game-not-running",
											ChatColor.RED
											+ "You cant open your ender furnace when the game is not running!"),
	
	CANT_OPEN_ENDER_BREWING_GAME_NOT_RUNNING(
													"cant-open-ender-brewing-game-not-running",
													ChatColor.RED
													+ "You cant open your ender brewing when the game is not running!"), 
	
	CANT_OPEN_ENDER_CHEST_GAME_NOT_RUNNING(
															"cant-open-ender-chest-game-not-running",
															ChatColor.RED
															+ "You cant open your ender chest when the game is not running!"), 
	
	CANT_OPEN_ENDER_CHEST_ON_LOBBY(
																	"cant-open-ender-chest-on-lobby",
																	"&cYou can open your ender chest on the lobby!"),

	// Team Command things
	TEAMCHECK("team-check-players", "There Are %# Players On The %w Team."),

	JOINTEAM("team-join", ChatColor.GRAY + "You Have Joined The %n%tc%w Team"),

	LEAVETEAM("team-leave", ChatColor.DARK_PURPLE + "You Left %w Team"),
	
	NOTEAM("team-none",
			ChatColor.RED + "You Do Not Have A Team To Leave."),

	CANNOTLEAVE("team-cannot-leave", ChatColor.RED + "You cannot Leave A Team While The Game Is Running."), 
	
	INVALIDTEAM(
			"team-invalid", ChatColor.RED + "Invalid Team Specified!"), 
	
	TEAMHELP("team-help",
					"/Team [Team] (Teams: [Red, Blue, Green, Yellow])"), 
	
	WRONGPHASE("team-join-wrong-phase",
							ChatColor.RED + "You cannot join during this phase."), 
	
	DESTROYEDTEAM("team-join-destroyed",
									ChatColor.RED
									+ "You cannot join a team whose nexus is destroyed!"), 
	
	JOINANOTHERTEAM(
											"team-join-another",
											ChatColor.RED
											+ "Please join another team until the player counts even out!"), 
	
	JOIN_ON_FULL_TEAM_DENIED(
													"team-full",
													ChatColor.RED
													+ "This Team is full, Please select another team."), 
	
	ALREADYHAVETEAM(
															"team-already-have", ChatColor.RED
															+ "You can't Change of Team!"),

	// Team
	YELLOWTEAM("yellow-team", "Yellow"), 
	
	REDTEAM("red-team", "Red"), 
	
	GREENTEAM("green-team",
			"Green"), 
	
	BLUETEAM("blue-team", "Blue"),

	// Bars
	PHASEBAR("phase-bar-name", ChatColor.GOLD + (ChatColor.BOLD + "Phase %#")), 
	
	BOSSBAR("boss-bar-name", ChatColor.WHITE + (ChatColor.BOLD + "Phase %#")), 
	
	LOBBY_BAR("Lobby-Bar-Name",
					ChatColor.GRAY + (ChatColor.BOLD + "Starting In: {#}")), 
	
	WINNER_GAME_MAP("winner-game-map",
							ChatColor.GREEN + "%w Selected! Loading Map."), 
	
	GAME_END_BAR("Game-End-Bar",
									ChatColor.GOLD.toString() + ChatColor.BOLD + "Game ends in: {#}"),
	
	NEXUS_HIT_BAR(
											"Nexus-Hit-Bar", "%w Nexus: %#"),

	KITS_SUB_MENU_TITLE("kit-sub-menu-title",
			ChatColor.DARK_RED.toString() + ChatColor.UNDERLINE.toString() + "%w"), 
	
	KIT_SELECTED("kit-selected",
					ChatColor.DARK_PURPLE + "%w Selected."), CANT_SELECT_KIT("cant-select-kit",
							ChatColor.RED + "Sorry, You Can Not Select This Kit."), 
	
	ALREADY_USING_KIT(
									"kit-already-using", ChatColor.RED + "You are Already %w"),

	// Vote
	VOTED_FOR_MAP("player-vote-for-map", ChatColor.DARK_PURPLE + "You Voted For %w"), 
	
//	CHANGE_YOUR_VOTE(
//			"player-change-his-vote",
//			ChatColor.DARK_PURPLE + "You Changed Your Vote From " + ChatColor.GOLD + "%w" + ChatColor.DARK_PURPLE
//			+ " To " + ChatColor.GOLD + "%2w"), 
	
	INVALID_MAP("player-vote-for-invalid-map",
					ChatColor.GOLD + "%w" + ChatColor.RED + " Is not a Valid Map."), 
	
	NO_MAPS_FOR_VOTING(
							"no-maps-for-voting", ChatColor.RED + "There Are No Maps For Voting!"),

	// Phase Start
	PHASESTART("phase-start", ChatColor.GOLD + "Phase %#" + ChatColor.GRAY + " Has Started"),

	// Phase messages
	PHASE1MESSAGE("phase-1-message", ChatColor.GRAY + "The Nexus Is " + ChatColor.GOLD + "Invincible"), 
	
	PHASE2MESSAGE(
			"phase-2-message",
			ChatColor.GRAY + "The Nexus Has " + ChatColor.GOLD + "Lost" + ChatColor.GRAY + " Its " + ChatColor.GOLD
			+ "Invincibility"), 
	
	PHASE3MESSAGE(
					"phase-3-message",
					ChatColor.AQUA + "Diamonds " + ChatColor.GRAY + "Have Spawned In The Middle" + "%n"
							+ ChatColor.GRAY + "Of The Map"), 
	
	PHASE4MESSAGE(
									"phase-4-message",
									ChatColor.GOLD + "Blaze Powder " + ChatColor.GRAY + "Is Now Available"
											+ "%n" + ChatColor.GRAY + "From The Brewing Shop" + ChatColor.GRAY
											+ " And " + ChatColor.GOLD + "Boss Spawn"), 
	
	PHASE5MESSAGE(
													"phase-5-message",
													ChatColor.GOLD + "Extra Damage " + ChatColor.GRAY
													+ "Is Inflicted On The " + ChatColor.GOLD + "Nexus"
													+ "%n" + ChatColor.GRAY + "When Breaking It" + "%n"
													+ ChatColor.RED + "x2 " + ChatColor.GOLD
													+ "Extra Damage"),
	
	;// END,

	private final String path;
	private final String def;
	private static Utf8YamlConfiguration LANG;

	/**
	 * Lang enum constructor.
	 * 
	 * @param path
	 *            The string path.
	 * @param start
	 *            The default string.
	 */
	Lang(String path, String start) {
		this.path = path;
		def = start;
	}

	/**
	 * Set the {@code YamlConfiguration} to use.
	 * 
	 * @param config
	 *            The config to set.
	 */
	public static void setFile(Utf8YamlConfiguration config) {
		LANG = config;
	}

	@Override
	public String toString() {
		final String gg = LANG.getString(path, def);
		return Util.wc(gg != null ? gg : "" + def);
	}

	public String toStringUnstranslated() {
		return Util.untranslateAlternateColorCodes(ChatColor.COLOR_CHAR, toString());
	}

	public String toShortenString(int characters) {
		final String string = toString();
		//
		if (string.length() <= characters)
			return string;
		//
		return string.substring(0, characters);
	}

	public String toStringReplaceAll(String oldValue, String newValue) {
		return toString().replaceAll(oldValue, newValue);
	}

	public String toStringReplacement(String word) {
		return replaceWord(toString(), word);
	}

	public String toStringReplaceTeamColor(String word) {
		return toString().replace("%tc", word);
	}

	public String toStringReplacement(int number) {
		return replaceNumber(toString(), number);
	}

	public String toStringReplacement(int number, String word) {
		return replaceWord(replaceNumber(toString(), number), word);
	}

	private String replaceWord(String string, String word) {
		return string.replace("%w", word);
	}

	private String replacePlayerName(String string, String word) {
		return string.replace("%PLAYER%", word);
	}

	public String toStringPlayerNameReplacement(String word) {
		return replacePlayerName(toString(), word);
	}

	private String replaceNumber(String string, int number) {
		return string.replace("%#", "" + number);
	}

	public String[] toStringArray() {
		String s = toString();
		//
		if (s.contains("%n"))
			return s.split("%n");
		else
			return new String[] { s };
	}

	public String[] toStringArray(int number) {
		String s = this.toStringReplacement(number);
		if (s.contains("%n"))
			return s.split("%n");
		else
			return new String[] { s };
	}

	public String[] toStringArray(int number, String word) {
		String s = this.toStringReplacement(number, word);
		if (s.contains("%n"))
			return s.split("%n");
		else
			return new String[] { s };
	}

	public String[] toStringArray(String word) {
		String s = this.toStringReplacement(word);
		if (s.contains("%n"))
			return s.split("%n");
		else
			return new String[] { s };
	}

	/**
	 * Get the default patch value.
	 * 
	 * @return The default patch value.
	 */
	public String getDefault() {
		return def == null ? def : def.replaceAll("\\xa7", "&");
	}
	
	public static String getCodeColor(String str) {
		return str != null ? str.replaceAll("\\xa7", "&") : null;
	}

	/**
	 * Get the String path.
	 * 
	 * @return The String patch.
	 */
	public String getPath() {
		return path;
	}
}
