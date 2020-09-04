package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.LivingEntity;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniBoss;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniSubDirectory;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;
import lombok.Setter;

public class GameBoss {
	private static @Getter @Setter BossMap BossMap = null;
	private static @Getter AnniBoss boss = null;
	private static @Getter @Setter UUID currentBossID;
	
	private static Map<String, String> worldNames = new HashMap<String, String>();
	private static Map<String, String> niceNames = new HashMap<String, String>();

	public static World getBossWorld(String name) {
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

	public static String getNiceBossWorldName(String worldName) {
		String name = niceNames.get(worldName.toLowerCase());
		//
		return name != null ? name : worldName;
	}

	public static void setAnniBoss(AnniBoss bosse) {
		if (bosse != null) {
			boss = bosse;
		}
	}

	public static boolean loadBossMap(File worldFolder, boolean isMapBuilder) {
		if (worldFolder.exists() && worldFolder.isDirectory()) {
			File[] MapUUID = worldFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					return name.equalsIgnoreCase("uid.dat");
				}
			});

			if (MapUUID != null && MapUUID.length == 1) {
				try {
					File toDel0 = MapUUID[0];
					if (toDel0 != null)
						toDel0.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			File[] files = worldFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					return name.equalsIgnoreCase("level.dat");
				}
			});

			if ((files != null) && (files.length == 1)) {
				try {
					String path = worldFolder.getPath();
					if (path.contains("plugins")) {
						path = path.substring(path.indexOf("plugins"));
					}
					
					WorldCreator cr = new WorldCreator ( path.replace ( '\\' , '/' ) );

					// Tipo de Dimension
					cr.environment(Environment.NORMAL);

					World mapWorld = Bukkit.createWorld(cr);
					if (mapWorld != null) {
						// Unload Old Map
						if (BossMap != null) {
							BossMap.unLoadMap();
							BossMap = null;
						}
						
						// Get orginal Map Name
						final String orName = mapWorld.getName();
						
						if (isMapBuilder) {
							// Set New values to new Map
							mapWorld.setAutoSave(false);
							mapWorld.setFullTime(15300L);
							mapWorld.setTime(15300L);
							mapWorld.setGameRuleValue("doMobSpawning", "false");
							mapWorld.setGameRuleValue("doFireTick", "false");
							mapWorld.setGameRuleValue("doDaylightCycle", "false");
							
							// Get new Boss Map
							BossMap = new BossMap(mapWorld.getName(), worldFolder, false);
							BossMap.registerListeners(AnnihilationMain.INSTANCE);
							
							// Put New Values
							worldNames.put(worldFolder.getName().toLowerCase(), mapWorld.getName());
							niceNames.put(mapWorld.getName().toLowerCase(), worldFolder.getName());

							// Remove Living Entities in the Boss Map
							for (LivingEntity no : mapWorld.getLivingEntities()) {
								if (no == null) {
									continue;
								}
								
								// Remove
								no.remove();
							}

							// load Config
							BossMap.loadFromConfig(BossMap.getConfig());
							return true;
						}
						

						// Unload Map World
						Bukkit.unloadWorld(mapWorld, false);

						// Create Temp World Folder Copy
						final File fol = new File(worldFolder.getParent(), worldFolder.getName() + "-TempCopy");
						if (fol.exists()) {
							fol.delete();
						}

						// MkDir
						fol.mkdir();

						// Copy Files
						Util.copy(worldFolder, fol);

						// Get new Path
						path = fol.getPath();
						path = path.contains("plugins") ? path.substring(path.indexOf("plugins")) : path;

						// Get new World Creator
						cr = new WorldCreator ( path.replace ( '\\' , '/'  ) );
						cr.environment(Environment.NORMAL);

						// Get new Map World
						mapWorld = Bukkit.createWorld(cr);

						// Set Map rules and disable auto save
						mapWorld.setAutoSave(false);
						mapWorld.setFullTime(15300L);
						mapWorld.setTime(15300L);
						mapWorld.setGameRuleValue("doMobSpawning", "false");
						mapWorld.setGameRuleValue("doFireTick", "false");
						mapWorld.setGameRuleValue("doDaylightCycle", "false");
						final String realMapName = mapWorld.getName();
						final File yf = new File(fol, "AnniBossMapConfig.yml");
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
						
						// Get new Boss Map
						BossMap = new BossMap(realMapName, fol, false);
						BossMap.registerListeners(AnnihilationMain.INSTANCE);

						// Put Nice World Name
						worldNames.put(fol.getName().toLowerCase(), realMapName);
						niceNames.put(realMapName.toLowerCase(), worldFolder.getName());
						
						// Remove Living Entities in the Boss Map
						for (LivingEntity no : mapWorld.getLivingEntities()) {
							if (no == null) {
								continue;
							}
							
							// Remove
							no.remove();
						}
						
						// Load From Config
						BossMap.loadFromConfig(BossMap.getConfig());
						return true;
					}
				} catch (Exception e) {
					Util.print(ChatColor.RED, "Could not load the boss map: ");
					e.printStackTrace();
					BossMap = null;
					return false;
				}
			}

		}
		return false;
	}

	public static boolean loadBossMap ( String mapName , boolean isMapBuilder ) {
		return loadBossMap ( new File ( AnniSubDirectory.BOSS_WORLDS_DIRECTORY.getDirectory ( ) , mapName ) , isMapBuilder );
//		return loadBossMap ( new File ( AnnihilationMain.INSTANCE.getDataFolder ( ).getAbsolutePath ( ) + "/BossWorlds", mapName ) , isMapBuilder );
	}
}
