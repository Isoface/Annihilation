package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameStartEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.LobbyMap;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniSubDirectory;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting;
import com.hotmail.AdrianSRJose.AnniPro.voting.MapVoting.VotingMap;

import lombok.Getter;

public class Game {
	private static GameMap GameMap = null;
	public static LobbyMap LobbyMap = null;

	private static final @Getter Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private static final @Getter Objective objetive = scoreboard.registerNewObjective("CAT", "MEOW MEOW");

	private static Map<String, String> worldNames = new HashMap<String, String>();
	private static Map<String, String> niceNames = new HashMap<String, String>();
	public static final List<Nexus> Nexus = new ArrayList<Nexus>();
	private static long StartTime = 0;
	private static boolean GameRunning = false;

	public static boolean isGameRunning() {
		return GameRunning;
	}

	public static boolean isNotRunning() {
		return !GameRunning;
	}

	/**
	 * @return The Played Game in Seconds;
	 */
	public static long getPlayingTime ( ) {
		return System.currentTimeMillis ( ) - StartTime;
	}

	/**
	 * @return the Played Game in format: hours:minutes:seconds
	 */
	public static String getPlayingTimeMessage ( ) {
		return DurationFormatUtils.formatDuration ( getPlayingTime ( ) , "H:mm:ss" );
//		return DurationFormatUtils.formatDuration((Game.getPlayingTime() * 1000), "H:mm:ss");
	}

	public static World getWorld(String name) {
		if (name == null) {
			return null;
		}

		final String universalAddres = Util.getVerifyUniversalPCAddres(name);
		World w = Bukkit.getWorld(universalAddres);
		if (w == null) {
			w = Bukkit.getWorld(worldNames.get(universalAddres.toLowerCase()));
		}
		return w;
	}

	public static String getNiceWorldName(String worldName) {
		String name = niceNames.get(worldName.toLowerCase());
		if (name == null) {
			name = worldName;
		}
		return name;
	}

	public static GameMap getGameMap() {
		return GameMap;
	}

	public static boolean loadGameMap ( File worldFolder , boolean isMapBuilder ) {
		if ( worldFolder.exists ( ) && worldFolder.isDirectory ( ) ) {
			// File Filter
			final File[] files = worldFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					return name.equalsIgnoreCase("level.dat");
				}
			});

			// Check
			if (files != null && files.length == 1) {
				try {
					// Get Path
					String path = worldFolder.getPath();
					if (path.contains("plugins")) {
						path = path.substring(path.indexOf("plugins"));
					}

					// Get World Creator and check
					WorldCreator cr = new WorldCreator ( path.replace ( '\\' , '/' ) );
					cr.environment(Environment.NORMAL);
					World mapWorld = Bukkit.createWorld ( cr );
					if (mapWorld != null) {
						// Unload Old GameMap
						if (GameMap != null) {
							GameMap.unLoadMap();
							GameMap = null;
						}
						
						// Get orginal Map Name
						final String orName = mapWorld.getName();

						// When is Map Builder
						if (!isMapBuilder) {
							// Unload Map World
							Bukkit.unloadWorld(mapWorld, false);

							// Create Temp World Folder Copy
							final File fol = new File(worldFolder.getParent(), worldFolder.getName() + "-TempCopy");
							if (fol.exists()) {
//								if (!GameBackup.isLOADING_BACKUP()) {
									FileUtils.forceDelete(fol);
									fol.delete ( );
//								}
							}

//							if (!GameBackup.isLOADING_BACKUP()) {
								// MkDir
								fol.mkdir();

								// Copy Files
								Util.copy(worldFolder, fol);
//							}
							
							// Get new Path
							path = fol.getPath();
							path = path.contains("plugins") ? path.substring(path.indexOf("plugins")) : path;

							// Get new World Creator
							cr = new WorldCreator ( path.replace ( '\\' , '/' ) );
							cr.environment(Environment.NORMAL);

							// Get new Map World
							mapWorld = Bukkit.createWorld(cr);

							// Set Map rules and disable auto save
							mapWorld.setAutoSave(false);
							mapWorld.setGameRuleValue("doMobSpawning", "false");
							mapWorld.setGameRuleValue("doFireTick", "false");
							
							// Disble time cycles
							if (Config.MAP_LOADING_ALWAYS_DAY.toBoolean()) {
								mapWorld.setTime(500L);
								mapWorld.setGameRuleValue("doDaylightCycle", "false");
							}
							
							// Load
							final String realMapName = mapWorld.getName();
//							if (!GameBackup.isLOADING_BACKUP()) {
								final File yf = new File(fol, "AnniMapConfig.yml");
								yf.setWritable(true);
								String[] lins = null;
								if (yf.exists() && yf.isFile()) {
									BufferedReader br = new BufferedReader(new FileReader(yf));
									try {
										StringBuilder sb = new StringBuilder();
										String line = br.readLine();

										while (line != null) {
											sb.append(line);
											sb.append("%n%");
											line = br.readLine();
										}

										String everything = sb.toString();
										lins = everything.split("%n%");
										for (int x = 0; x < lins.length; x++) {
											lins[x] = lins[x].replace(orName, realMapName);
										}

									} finally {
										br.close();
									}
								}

								Writer writer = null;
								try {
									writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(yf), "UTF-8"));
									for (String l : lins) {
										writer.write(l);
										writer.append(System.lineSeparator());
									}
								} catch (IOException ex) {
									// Ignore
								} finally {
									try {
										writer.close();
									} catch (Exception ex) {
										// Ignore
									}
								}
//							}

							// New GameMap
							GameMap = new GameMap(realMapName, fol, true);

							// Check Game Map World
							if (GameMap.getWorld() == null) {
								GameMap.setWorldName(realMapName);
							}
							
							// Register Listeners
							GameMap.registerListeners(AnnihilationMain.INSTANCE);

							// Put Nice World Name
							worldNames.put(fol.getName().toLowerCase(), realMapName);
							niceNames.put(realMapName.toLowerCase(), worldFolder.getName());
							// String name = niceNames.get(worldName.toLowerCase());
							return true;
						}
	
						// Set Map rules and disable auto save
						mapWorld.setAutoSave(false);
						mapWorld.setGameRuleValue("doMobSpawning", "false");
						mapWorld.setGameRuleValue("doFireTick", "false");
						
						// New GameMap
						GameMap = new GameMap(mapWorld.getName(), worldFolder, true);

						// Check Game Map World
						if (GameMap.getWorld() == null) {
							GameMap.setWorldName(mapWorld.getName());
						}

						// Register Listeners
						GameMap.registerListeners(AnnihilationMain.INSTANCE);
						
						// Put Nice World Name
						worldNames.put(worldFolder.getName().toLowerCase(), mapWorld.getName());
						niceNames.put(mapWorld.getName().toLowerCase(),worldFolder.getName());
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					GameMap = null;
					return false;
				}
			}
		}
		return false;
	}

	public static boolean loadGameMap ( String mapName , boolean isMapBuilder ) {
		return loadGameMap ( new File ( AnniSubDirectory.WORLDS_DIRECTORY.getDirectory ( ) , mapName ) , isMapBuilder );
	}
	
	public static boolean startGame(String mapName) {
		try {
			if (!isGameRunning()) {
				if (Game.getGameMap() == null) {
					if (Game.loadGameMap(mapName, false)) {
						GameRunning = true;
						AnniEvent.callEvent(new GameStartEvent());
						StartTime = System.currentTimeMillis ( );
						return true;
					}
				} else {
					GameRunning = true;
					AnniEvent.callEvent(new GameStartEvent());
					StartTime = System.currentTimeMillis ( );
					return true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			GameRunning = false;
			Bukkit.getConsoleSender().sendMessage(Util.wc("&c[Annihilation] The game was not started!"));
			//
			if (Game.getGameMap() != null && Game.getGameMap().getWorld() == null) {
				Bukkit.getConsoleSender().sendMessage(Util.wc("&c[Annihilation] Could not found the Game Map World!"));
			}
			//
			return false;
		}
		return false;
	}

	public static boolean startGame ( ) {
		try {
			if (!isGameRunning()) {
				if (Game.getGameMap() == null) {
					if (Config.MAP_LOADING_VOTING.toBoolean()) {
						VotingMap winner = MapVoting.getWinningMap ( );
						if ( winner != null && Game.loadGameMap ( winner.getName ( ) , false ) ) {
							GameRunning = true;
							AnniEvent.callEvent ( new GameStartEvent ( ) );
							StartTime = System.currentTimeMillis ( );
							return true;
						}
					} else if (Game.loadGameMap(Config.MAP_LOADING_USE_MAP.toFile(), false)) {
						GameRunning = true;
						AnniEvent.callEvent(new GameStartEvent());
						StartTime = System.currentTimeMillis ( );
						return true;
					}
				} else {
					GameRunning = true;
					AnniEvent.callEvent(new GameStartEvent());
					StartTime = System.currentTimeMillis ( );
					return true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			GameRunning = false;
			Bukkit.getConsoleSender().sendMessage(Util.wc("&c[Annihilation] The game was not started!"));
			//
			if (Game.getGameMap() != null && Game.getGameMap().getWorld() == null) {
				Bukkit.getConsoleSender().sendMessage(Util.wc("&c[Annihilation] Could not found the Game Map World!"));
			}
			//
			return false;
		}
		return false;
	}

	/*
	 * private static final int MINUTES_IN_AN_HOUR = 60; private static final int
	 * SECONDS_IN_A_MINUTE = 60;
	 * 
	 * private static String timeConversion(int totalSeconds) { int hours =
	 * totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE; int minutes =
	 * (totalSeconds - (hoursToSeconds(hours))) / SECONDS_IN_A_MINUTE; int seconds =
	 * totalSeconds - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));
	 * 
	 * return hours + " hours " + minutes + " minutes " + seconds + " seconds"; }
	 * 
	 * private static int hoursToSeconds(int hours) { return hours *
	 * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE; }
	 * 
	 * private static int minutesToSeconds(int minutes) { return minutes *
	 * SECONDS_IN_A_MINUTE; }
	 */
}
