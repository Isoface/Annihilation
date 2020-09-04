package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.TeamBeacon;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("deprecation")
public final class AnniTeam {
	public static final AnniTeam Red = new AnniTeam(ChatColor.RED, false);
	public static final AnniTeam Blue = new AnniTeam(ChatColor.BLUE, false);
	public static final AnniTeam Green = new AnniTeam(ChatColor.GREEN, false);
	public static final AnniTeam Yellow = new AnniTeam(ChatColor.YELLOW, false);
	public static final AnniTeam[] Teams = new AnniTeam[] { Red, Blue, Green, Yellow };
	private static final AnniTeam HideRed = new AnniTeam(ChatColor.DARK_RED, true);
	private static final AnniTeam HideBlue = new AnniTeam(ChatColor.DARK_BLUE, true);
	private static final AnniTeam HideGreen = new AnniTeam(ChatColor.DARK_GREEN, true);
	private static final AnniTeam HideYellow = new AnniTeam(ChatColor.GOLD, true);

	public static AnniTeam getHideTeam(AnniTeam team) {
		switch (team.getName()) {
		case "Red":
			return HideRed;
		case "Blue":
			return HideBlue;
		case "Green":
			return HideGreen;
		case "Yellow":
			return HideYellow;
		}
		return null;
	}

	public static AnniTeam getTeamByName(String name) {
		if (name == null) {
			return null;
		}
		
		if (name.equalsIgnoreCase(Red.getName()) || name.equalsIgnoreCase(Red.getExternalName()))
			return Red;
		else if (name.equalsIgnoreCase(Blue.getName()) || name.equalsIgnoreCase(Blue.getExternalName()))
			return Blue;
		else if (name.equalsIgnoreCase(Green.getName()) || name.equalsIgnoreCase(Green.getExternalName()))
			return Green;
		else if (name.equalsIgnoreCase(Yellow.getName()) || name.equalsIgnoreCase(Yellow.getExternalName()))
			return Yellow;
		else
			return null;
	}

	//
	public static AnniTeam getTeamByColor(ChatColor color) {
		if (color.equals(ChatColor.RED))
			return Red;
		else if (color.equals(ChatColor.BLUE))
			return Blue;
		else if (color.equals(ChatColor.GREEN))
			return Green;
		else if (color.equals(ChatColor.YELLOW))
			return Yellow;
		else
			return null;
	}

	public static AnniTeam getRandomTeam() {
		return Teams[rand.nextInt(Teams.length)];
	}

	//
	private final ChatColor Color;
	private final Nexus Nexus;

	private List<AnniPlayer> players;
	private final Team scoreboardTeam;
	// private Team scoreboardLobbyTeam;

	private final String externalName;
	private final String name;
	private int Health = 75;
	private LinkedList<Loc> spawns;
	private static Random rand;
	private Loc spectatorLocation;
	private Loc witchLocation;
	private int witchRespawnTime;
	private @Getter @Setter TeamBeacon beacon;
	// private TimeUnit witchRespawnUnit;
	private String nameOnBoard;
	private int scoreOnBoard;
	// private String prefix = "§f["+getColor()+"§l"+getExternalName().substring(0,
	// 1).toUpperCase()+"§r§f] "+getColor();
	//

	private AnniTeam(ChatColor color, boolean isHide) {
		rand = new Random(System.currentTimeMillis());
		players = new ArrayList<AnniPlayer>();
		Color = color;
		spawns = new LinkedList<Loc>();
		Nexus = new Nexus(this);
		// -----Load Nexuses-------------
		if (!Game.Nexus.contains(Nexus)) {
			Game.Nexus.add(Nexus);
		}
		// ------------------------------
		spectatorLocation = null;
		witchLocation = null;
		witchRespawnTime = 0;
		Bukkit.getPluginManager().registerEvents(Nexus, AnnihilationMain.INSTANCE);
		name = Color.name().substring(0, 1) + Color.name().substring(1).toLowerCase();
		//
		if (color == ChatColor.RED)
			externalName = Lang.REDTEAM.toString();
		else if (color == ChatColor.BLUE)
			externalName = Lang.BLUETEAM.toString();
		else if (color == ChatColor.GREEN)
			externalName = Lang.GREENTEAM.toString();
		else
			externalName = Lang.YELLOWTEAM.toString();

		scoreboardTeam = Game.getScoreboard().registerNewTeam(name);
		if (isHide) {
			scoreboardTeam.setNameTagVisibility(NameTagVisibility.NEVER);
		}

		scoreboardTeam.setAllowFriendlyFire(false);
		scoreboardTeam.setCanSeeFriendlyInvisibles(true);
		checkPrefixes();
	}

	public void checkPrefixes() {
		// if (GameVars.useTeamPrefix) {
		scoreboardTeam.setPrefix(getPrefix());
		// }
	}

	public String getPrefix ( ) {
//		String prefix = ChatColor.RESET + ChatColor.BOLD.toString ( ) + "[" + getColor ( ) + ChatColor.BOLD 
//				+ getExternalName ( ).substring ( 0 , 1 ) + ChatColor.RESET + ChatColor.BOLD + "]" + getColor ( );
//		
//		System.out.println ( "---------------------- > " + prefix );
//		return ChatColor.RESET + ChatColor.BOLD.toString ( ) + "[" + getColor ( ) + ChatColor.BOLD 
//				+ getExternalName ( ).substring ( 0 , 1 ) + ChatColor.RESET + ChatColor.BOLD + "]" + getColor ( );
		return Util.wc(
				"&f[" + getColor() + "&l" + getExternalName().substring(0, 1).toUpperCase() + "&r&f] " + getColor());
	}

	public String getNameOnBoard() {
		return nameOnBoard;
	}

	public int getScoreOnBoard() {
		return scoreOnBoard;
	}

	public ChatColor getColor() {
		return Color;
	}

	public Nexus getNexus() {
		return Nexus;
	}

	public void verifyInBoardTeam(final Player p) {
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap != null && !scoreboardTeam.hasPlayer(p)) {
			scoreboardTeam.addPlayer(p);
		}
	}

	public void joinTeam(AnniPlayer player) {
		if (player.getTeam() != null) {
			player.getTeam().leaveTeam(player);
		}

		// Set
		player.setTeam(this);
		players.add(player);
		scoreboardTeam.addPlayer(player.getPlayer());
		getHideTeam().scoreboardTeam.removePlayer(player.getPlayer());
		getHideTeam().scoreboardTeam.removeEntry(player.getPlayer().getName());
		// player.setData("key-team-key", getName());
	}

	public void leaveTeam(AnniPlayer player) {
		player.setTeam(null);
		players.remove(player);
		scoreboardTeam.removePlayer(player.getPlayer());
		scoreboardTeam.removeEntry(player.getPlayer().getName());
		getHideTeam().scoreboardTeam.removePlayer(player.getPlayer());
		getHideTeam().scoreboardTeam.removeEntry(player.getPlayer().getName());
		//
		Object o = player.getData("key-onhide-key");
		if (o != null && o instanceof Integer) {
			Bukkit.getScheduler().cancelTask(((Integer) o).intValue());
			player.setData("key-onhide-key", null);
		}

		// Remove Data
		// player.setData("key-team-key", null);
	}

	public Team getScoreboardTeam ( ) {
		return this.scoreboardTeam;
	}

	public List<AnniPlayer> getPlayers ( ) {
		return Collections.unmodifiableList(players);
	}

	public Loc getSpectatorLocation() {
		return spectatorLocation;
	}

	public Loc getWitchLocation() {
		return witchLocation;
	}

	public int getWitchsRespawnTime() {
		return witchRespawnTime;
	}

	public int getPlayerCount() {
		return players.size();
	}

	public String setNameOnBoard(String s) {
		this.nameOnBoard = s;
		return s;
	}

	public void setScoreOnBoard(int newScore) {
		scoreOnBoard = newScore;
	}

	public void setSpectatorLocation(Loc loc) {
		spectatorLocation = loc;
	}

	public void setWitchLocation(Loc loc) {
		witchLocation = loc;
	}

	public void setWitchsRespawnTime(int l) {
		witchRespawnTime = l;
	}

	public void setSpectatorLocation(Location loc) {
		this.setSpectatorLocation(new Loc(loc, true));
	}

	public void setWitchLocation(Location loc) {
		this.setWitchLocation(new Loc(loc, true));
	}

	public int addSpawn(Loc loc) {
		int val = spawns.size();
		spawns.addLast(loc);
		return val + 1;
	}

	public int addSpawn(Location loc) {
		return addSpawn(new Loc(loc, true));
	}

	public boolean removeSpawn(int index) {
		if (spawns.size() >= index + 1) {
			spawns.remove(index);
			return true;
		}
		return false;
	}

	public int getHealth() {
		return Health;
	}

	public void setHealth(int Health) {
		if (Health < 0)
			Health = 0;

		if (this.Health > 0) {
			this.Health = Health;
		}
	}

	public void clearSpawns() {
		spawns.clear();
	}

	public boolean isTeamDead() {
		return Health <= 0;
	}

	public boolean isFull() {
		return getPlayerCount() >= Config.TEAM_BALANCING_MAX_PLAYERS.toInt();
	}

	public Location getRandomSpawn ( ) {
		if ( spawns.isEmpty ( ) ) {
			Util.print ( ChatColor.RED, 
					"NO SPAWNS SET FOR TEAM " + getName ( ).toUpperCase ( ) );
			return null;
		} else {
			Loc loc = spawns.get ( rand.nextInt ( spawns.size ( ) ) );
//			System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>>" + loc );
//			System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>>" + loc.toLocation ( ) );
			return loc.toLocation ( );
		}
	}

//	public Location getSecureRandomSpawn() {
//		// Create new list
//		final List<Loc> secureLocs = new ArrayList<Loc>();
//		for (Loc s : spawns) {
//			// Check is not null
//			if (!Util.isValidLoc(s)) {
//				continue;
//			}
//
//			// Add
//			secureLocs.add(s);
//		}
//
//		// Return secure
//		if (!secureLocs.isEmpty()) {
//			return secureLocs.get(rand.nextInt(secureLocs.size())).toLocation();
//		}
//
//		// Returns null and throw message if is null
//		// Bukkit.getLogger().warning("NO SPAWNS SET FOR TEAM
//		// "+this.getName().toUpperCase()+". SENDING TO LOBBY IF POSSIBLE.");
//		Util.print(ChatColor.RED,
//				"NO SPAWNS SET FOR TEAM " + this.getName().toUpperCase() + ". SENDING TO LOBBY IF POSSIBLE.");
//		return null;
//	}

	public List<Loc> getSpawnList() {
		return Collections.unmodifiableList(spawns);
	}

	public String getName() {
		return name;
	}

	public String getColoredName() {
		return Color.toString() + getName();
	}

	public String getExternalName() {
		return externalName;
	}

	public String getExternalColoredName() {
		return Color.toString() + getExternalName();
	}

	public AnniTeam getHideTeam() {
		return getHideTeam(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnniTeam)
			return ((AnniTeam) obj).Color == Color;
		else
			return false;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public AnniTeam loadFromConfig(ConfigurationSection configSection) {
		if (configSection != null) {
			Loc nexusloc = new Loc(configSection.getConfigurationSection("Nexus.Location"));
			if (nexusloc != null) {
				Nexus.setLocation(nexusloc);
			}

			ConfigurationSection witchSection = configSection.getConfigurationSection("WitchLocation");
			if (witchSection != null) {
				Loc witchspawn = new Loc(witchSection);
				if (witchspawn != null)
					setWitchLocation(witchspawn);

				if (witchSection.getString("WitchsRespawnTime") != null)
					setWitchsRespawnTime(
							Integer.parseInt(witchSection.getString("WitchsRespawnTime").replaceAll("'", "")));
				else
					setWitchsRespawnTime(5);
			}

			Loc spectatorspawn = new Loc(configSection.getConfigurationSection("SpectatorLocation"));
			if (spectatorspawn != null)
				setSpectatorLocation(spectatorspawn);

			ConfigurationSection spawns = configSection.getConfigurationSection("Spawns");
			if (spawns != null) {
				for (String key : spawns.getKeys(false)) {
					Loc loc = new Loc(spawns.getConfigurationSection(key));
					if (loc != null) {
						addSpawn(loc);
					}
				}
			}
		}
		return this;
	}

	public void saveToConfig(ConfigurationSection configSection) {
		if (configSection != null) {
			Loc nexusLoc = Nexus.getLocation();
			if (nexusLoc != null)
				nexusLoc.saveToConfig(configSection.createSection("Nexus.Location"));

			Loc spectatorspawn = getSpectatorLocation();
			if (spectatorspawn != null)
				spectatorspawn.saveToConfig(configSection.createSection("SpectatorLocation"));

			Loc witchspawn = getWitchLocation();
			if (witchspawn != null)
				witchspawn.saveToConfig(configSection.createSection("WitchLocation"));

			ConfigurationSection spawnSection = configSection.createSection("Spawns");
			List<Loc> spawns = getSpawnList();
			if (spawns != null && !spawns.isEmpty()) {
				for (int x = 0; x < spawns.size(); x++) {
					spawns.get(x).saveToConfig(spawnSection.createSection(x + ""));
				}
			}
		}
	}
}