package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.Title.TitleHandler;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.plugin.AnniPlugin;

public class Util {
	public static final Map<Location, BlockData> blockDatas = new HashMap<Location, BlockData>();

	public static void copy(File source, File destination) throws IOException {
		if (source.isDirectory()) {

			// if directory not exists, create it
			if (!destination.exists()) {
				destination.mkdir();
			}

			// list all the directory contents
			String files[] = source.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);
				// recursive copy
				copy(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destination);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
		}
	}

	public static boolean forcedRemoveFile(File destFile) {
		try {
			if (destFile.isDirectory()) {
				return forcedRemoveDir(destFile);
			}

			File dir = destFile;
			System.gc();
			Thread.sleep(1000); // 1000
			FileDeleteStrategy.FORCE.delete(dir);
			dir.delete();
		} catch (Exception e) {
		}
		return true;
	}

	public static boolean forcedRemoveDir(File destFile) {
		try {
			// destFile = new File((System.getProperty("user.dir")+"/FileName"))
			// checks if the directory has any file
			File dir = destFile;
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				if (files != null && files.length > 0) {
					for (File aFile : files) {
						System.gc();
						// Thread.sleep(delay); // 1000
						FileDeleteStrategy.FORCE.delete(aFile);
						// System.out.println("deleting file " + aFile);
					}
				}
				dir.delete();
				// System.out.println("deleted: " + dir);
			} else {
				dir.delete();
			}
		} catch (Exception e) {
			// Util.print(ChatColor.RED, "Exception Occured While Deleting Folder : " + e);
		}
		return true;
	}

	public static void print(String mess) {
		print(ChatColor.GREEN, mess);
	}

	public static final char COLOR_CHAR   = '&';// ;'\u00A7';
	private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	public static String untranslateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
				b[i] = COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	public static String untranslateAlternateColorCodes(String textToTranslate) {
		return untranslateAlternateColorCodes(ChatColor.COLOR_CHAR, textToTranslate);
	}

	public static void printTest(String mess) {
		Bukkit.getConsoleSender()
				.sendMessage(Util.wc(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "[TEST] " + mess));
	}

	public static void print(ChatColor firstColor, String mess) {
		if (firstColor != null) {
			Bukkit.getConsoleSender()
					.sendMessage(Util.wc(firstColor.toString() + ChatColor.BOLD.toString() + "[Annihilation] " + mess));
		}
	}
	
	public static void print(AnniPlugin plugin, ChatColor firstColor, String mess) {
		if (firstColor != null && plugin != null) {
			Bukkit.getConsoleSender()
					.sendMessage(Util.wc(firstColor.toString() + ChatColor.BOLD.toString() + "["+plugin.getName()+"] " + mess));
		}
	}

	private static long lasPrint = 0L;

	public static void print(ChatColor firstColor, String mess, long curentMyll) {
		if (System.currentTimeMillis() - lasPrint >= 3000) {
			print(firstColor, mess);
			lasPrint = System.currentTimeMillis();
		}
	}

	public static String shortenString(String string, int characters) {
		if (string.length() <= characters) {
			return string;
		}
		return string.substring(0, characters);
	}


	public static File getPluginsFolder() {
		File plugins = new File(AnnihilationMain.INSTANCE.getDataFolder().getParent());
		//
		if (!plugins.exists())
			plugins.mkdir();
		//
		return plugins;
	}

	public static BlockData getBlockData(Location loc) {
		if (loc != null) {
			if (!blockDatas.containsKey(loc) || blockDatas.get(loc) == null)
				blockDatas.put(loc, new BlockData(loc));
			//
			return blockDatas.get(loc);
		}
		return null;
	}

	public static boolean tryCreateFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void saveResource(JavaPlugin plugin, String resourceName) {
		if (plugin.getResource(resourceName) != null) {
			plugin.saveResource(resourceName, false);
		}
	}

	public static File getFile(JavaPlugin plugin, String fileName) {
		File f = new File(plugin.getDataFolder().getAbsolutePath(), fileName);
		//
		return f;
	}

	public static Object getHandle(Player player) {
		try {
			Class<? extends Player> c = player.getClass();
			Method getHandle = player.getClass().getDeclaredMethod("getHandle", c);
			return getHandle.invoke(player, getHandle);
		} catch (Exception e) {
			return null;
		}
	}

	public static void setKiller(Player player, Player killer) {
		if (player == null || killer == null)
			return;
		try {
			Field getPlayerKiller = null;
			Object playerHandle = getHandle(player);
			Object killerHandle = killer == null ? null : getHandle(killer);
			Field nmsPlayer = getPlayerKiller == null
					? getPlayerKiller = playerHandle.getClass().getDeclaredField("killer")
					: getPlayerKiller;
			nmsPlayer.set(playerHandle, killerHandle);
		} catch (Exception e) {

		}
	}

	public static void setMetadata(Player player, String metadata) {
		player.setMetadata(metadata, new FixedMetadataValue(AnnihilationMain.INSTANCE, metadata));
	}

	public static void setMetadata(Block bl, String metadata) {
		bl.setMetadata(metadata, new FixedMetadataValue(AnnihilationMain.INSTANCE, metadata));
	}

	public static void removeMetadata(Player player, String metadata) {
		player.removeMetadata(metadata, AnnihilationMain.INSTANCE);
	}

	public static void removeMetadata(Block bl, String metadata) {
		bl.removeMetadata(metadata, AnnihilationMain.INSTANCE);
	}

	public static String getBoldChatColor(ChatColor color) {
		return color.toString() + (ChatColor.BOLD);
	}

	public static void addToList(List<String> l, String... objs) {
		for (String s : objs)
			l.add(s);
	}

	@SuppressWarnings("unchecked")
	public static <T> void addToList(List<T> l, T... add) {
		for (T t : add)
			l.add(t);
	}

	public static void addLastDamager(AnniPlayer ap, Player damager) {
		ap.setData("LastDamagerUUID", damager.getUniqueId());
	}

	public static void removeLastDamager(AnniPlayer ap) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				ap.setData("LastDamagerUUID", null);
			}
		}, 400L); // 500L
	}

	public static ItemStack getOptionsItem(Player player) {
		if (player == null)
			throw new NullPointerException("Invalid Player");
		ItemStack sk = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		sk = KitUtils.addClassSoulbound(sk);
		ItemMeta meta = sk.getItemMeta();
		meta = meta == null ? Bukkit.getItemFactory().getItemMeta(sk.getType()) : meta;
		meta.setDisplayName(Lang.OPTIONSITEM.toString());
		sk.setItemMeta(meta);
		return sk;
	}

	public static void FireWorkEffect(Location loc) {
		Random colour = new Random();
		Firework f = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fMeta = f.getFireworkMeta();
		FireworkEffect.Type fwType = FireworkEffect.Type.BALL_LARGE;

		int c1i = colour.nextInt(17) + 1;
		int c2i = colour.nextInt(17) + 1;

		Color c1 = getColorFromInt(c1i);
		Color c2 = getColorFromInt(c2i);
		FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).build();
		fMeta.addEffect(effect);
		fMeta.setPower(1);
		f.setFireworkMeta(fMeta);
	}

	public static void FireWorkEffect(Location loc, Color col) {
		Firework f = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fMeta = f.getFireworkMeta();
		FireworkEffect.Type fwType = FireworkEffect.Type.BALL_LARGE;

		FireworkEffect effect = FireworkEffect.builder().withFade(col).withColor(col).with(fwType).build();
		fMeta.addEffect(effect);
		fMeta.setPower(1);
		f.setFireworkMeta(fMeta);
	}

	public static void FireWorkEffect(Location loc, Color col, Color col2) {
		Firework f = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fMeta = f.getFireworkMeta();
		FireworkEffect.Type fwType = FireworkEffect.Type.BALL_LARGE;

		Color c1 = col;
		Color c2 = col2;

		FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).build();
		fMeta.addEffect(effect);
		fMeta.setPower(1);
		f.setFireworkMeta(fMeta);
	}

	public static Color getColorFromInt(int c) {
		switch (c) {
		case 1:
			return Color.TEAL;
		case 2:
		default:
			return Color.WHITE;
		case 3:
			return Color.YELLOW;
		case 4:
			return Color.AQUA;
		case 5:
			return Color.BLACK;
		case 6:
			return Color.BLUE;
		case 7:
			return Color.FUCHSIA;
		case 8:
			return Color.GRAY;
		case 9:
			return Color.GREEN;
		case 10:
			return Color.LIME;
		case 11:
			return Color.MAROON;
		case 12:
			return Color.NAVY;
		case 13:
			return Color.OLIVE;
		case 14:
			return Color.ORANGE;
		case 15:
			return Color.PURPLE;
		case 16:
			return Color.RED;
		}
	}

	public static String wc(String g) {
		return g == null ? "null String" : ChatColor.translateAlternateColorCodes('&', g);
	}

	public static String remC(String g) {
		return g == null ? "null string" : ChatColor.stripColor(g);
	}

	public static String replaceWithNothing(String text, String toReplace) {
		return text == null ? "null String" : text.replace(toReplace, "");
	}

	public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
		World world = center.getWorld();
		double increment = (2 * Math.PI) / amount;
		ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < amount; i++) {
			double angle = i * increment;
			double x = center.getX() + (radius * Math.cos(angle));
			double z = center.getZ() + (radius * Math.sin(angle));
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}
	
	/**
	 *
	 * @param centerBlock Define the center of the sphere
	 * @param radius Radius of your sphere
	 * @param hollow If your sphere should be hollow (you only get the blocks outside) just put in "true" here
	 * @return Returns the locations of the blocks in the sphere
	 */
	public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
		// create list
		final List<Location> circleBlocks = new ArrayList<Location>();

		// get x, y, z
		int bx = centerBlock.getBlockX();
		int by = centerBlock.getBlockY();
		int bz = centerBlock.getBlockZ();
		for (int x = bx - radius; x <= bx + radius; x++) {
			for (int y = by - radius; y <= by + radius; y++) {
				for (int z = bz - radius; z <= bz + radius; z++) {
					// get distance
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
					
					// get location and add it
					if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
						Location l = new Location(centerBlock.getWorld(), x, y, z);
						circleBlocks.add(l);
					}
				}
			}
		}
		return circleBlocks;
	}

	public static int setDefaultIfNotSet(ConfigurationSection section, String path, Object value) {
		if (section != null) {
			if (!section.isSet(path)) {
				if (value instanceof String) {
					section.set(path, value);
				} else {
					section.set(path, value);
				}
				return 1;
			}
		}
		return 0;
	}

	public static int setUpdatedIfNotEqual(ConfigurationSection section, String path, Object update) {
		if (section != null && path != null) {
			if (!section.isSet(path)) {
				section.set(path, update);
				return 1;
			} else {
				if (!update.equals(section.get(path))) {
					section.set(path, update);
					return 1;
				}
			}
		}
		//
		return 0;
	}

	public static ConfigurationSection createSectionIfNoExits(ConfigurationSection father, String newSectionName) {
		return father.isConfigurationSection(newSectionName) ? father.getConfigurationSection(newSectionName)
				: father.createSection(newSectionName);
	}

	public static int createSectionIfNoExitsInt(ConfigurationSection father, String newSectionName) {
		if (!father.isConfigurationSection(newSectionName) || father.getConfigurationSection(newSectionName) == null) {
			father.createSection(newSectionName);
			return 1;
		}
		//
		return 0;
	}
	
	public static Material getMaterial(String name, Material def) {
		Material tor = def;
		try {
			Material o = Material.valueOf(name.toUpperCase());
			if (o != null) {
				tor = o;
			}
		} catch (Throwable t) {

		}
		return tor;
	}
	
	public static <E> List<E> getListFromCollection(final Collection<E> collection) {
		if (collection != null) {
			return new ArrayList<E>(collection);
		}
		return null;
	}
	
	/**
	 * A common method for all enums since they can't have another base class
	 * @param <T> Enum type
	 * @param c enum type. All enums must be all caps.
	 * @param string case insensitive
	 * @param def will be returned if the getted enum from string is null
	 * @return corresponding enum, or null
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string, T def) {
	    if( c != null && string != null ) {
	        try {
	        	T tor = Enum.valueOf(c, string.trim().toUpperCase());
	        	if (tor != null) {
	        		return tor;
	        	}
	        	else {
	        		return def;
	        	}
	        } catch(IllegalArgumentException ex) {
	        }
	    }
	    return null;
	}
	
	/**
	 * A common method for all enums since they can't have another base class
	 * @param <T> Enum type
	 * @param c enum type. All enums must be all caps.
	 * @param string case insensitive
	 * @return corresponding enum, or null
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
	    if( c != null && string != null ) {
	        try {
	            return Enum.valueOf(c, string.trim().toUpperCase());
	        } catch(IllegalArgumentException ex) {
	        	return null;
	        }
	    }
	    return null;
	}
	
	public static String getVerifyUniversalPCAddres(String toTransform) {
		String world = toTransform;
		//
		if (world != null) {
			try {
				World Try = Bukkit.getWorld(world);
				//
				if (Try == null || Try.getName() == null) {
					if (world.contains("/"))
						world = world.replace("/", "\\");
					else if (world.contains("\\"))
						world = world.replace("\\", "/");
				}
			} catch (Throwable e) {

			}
		}
		//
		return world;
	}

	@SuppressWarnings("deprecation")
	public static void sendTitle(final Player p, String t, String s, int i1, int i2, int i3) {
		if (t == null && s == null)
			throw new NullPointerException("Invalid titles");

		s = s == null ? "" : s;
		t = t == null ? "" : t;

		if (!KitUtils.isValidPlayer(p))
			return;

		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
			p.sendTitle(t, s);
		} else {
			TitleHandler.getInstance().SendTitleToPlayer(p, t, s, i1, i2, i3);
		}
	}

	public static void sendTitle(final Player p, String t, String s) {
		sendTitle(p, t, s, 6, 26, 6);
	}

	public static boolean isValidLoc(final Loc l) {
		return l != null && l.toLocation() != null && l.toLocation().getWorld() != null;
	}

	public static boolean isValidLoc(final Location l) {
		return l != null && l.getWorld() != null;
	}

	public static boolean isValidWorld(World w) {
		return w != null && w.getName() != null;
	}

	public static void removeAllPotionEffects(Player p) {
		if (p == null)
			return;
		for (PotionEffectType pts : PotionEffectType.values()) {
			if (pts != null)
				p.removePotionEffect(pts);
		}
	}

	public static void setNoAI(LivingEntity ent) {
		if (!(ent instanceof LivingEntity))
			throw new IllegalArgumentException("Invalid LivingEntity, This entity not is a LivingEntity");

		Object localObject1;
		Object localObject2 = null;
		try {
			localObject1 = ent.getClass().getDeclaredMethod("getHandle", new Class[0]).invoke(ent, new Object[0]);
			if (localObject2 == null) {
				localObject2 = getCraftClass("NBTTagCompound").getConstructor(new Class[0]).newInstance(new Object[0]);
			}
			localObject1.getClass().getMethod("c", new Class[] { localObject2.getClass() }).invoke(localObject1,
					new Object[] { localObject2 });
			localObject2.getClass().getMethod("setInt", new Class[] { String.class, Integer.TYPE }).invoke(localObject2,
					new Object[] { "NoAI", Integer.valueOf(1) });
			localObject1.getClass().getMethod("f", new Class[] { localObject2.getClass() }).invoke(localObject1,
					new Object[] { localObject2 });
		} catch (IllegalAccessException ParamException) {
			ParamException.printStackTrace();
		} catch (IllegalArgumentException ParamException) {
			ParamException.printStackTrace();
		} catch (InvocationTargetException ParamException) {
			ParamException.printStackTrace();
		} catch (NoSuchMethodException ParamException) {
			ParamException.printStackTrace();
		} catch (SecurityException ParamException) {
			ParamException.printStackTrace();
		} catch (InstantiationException ParamException) {
			ParamException.printStackTrace();
		}
	}
	
	public static boolean AnniOEEnabled() {
		return Bukkit.getPluginManager().getPlugin("AnniPlayerOptions") != null
				&& Bukkit.getPluginManager().getPlugin("AnniPlayerOptions").isEnabled();
	}

	public Sound getUniversalSound(Sound s) {
		final boolean b = ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1); // VersionUtils.isNewSpigotVersion();
		if (s.name().contains("CHEST_OPEN")) {
			return b ? Sound.BLOCK_CHEST_OPEN : Sound.valueOf("CHEST_OPEN");
		}

		if (s.name().contains("ZOMBIE_INFECT"))
			return b ? Sound.ENTITY_ZOMBIE_INFECT : Sound.valueOf("ZOMBIE_INFECT");

		if (s.name().contains("ENTITY_GENERIC_BIG_FALL") || s.name().contains("FALL_BIG"))
			return b ? Sound.ENTITY_GENERIC_BIG_FALL : Sound.valueOf("FALL_BIG");

		if (s.name().contains("WITHER_SHOOT"))
			return b ? Sound.ENTITY_WITHER_SHOOT : Sound.valueOf("WITHER_SHOOT");

		if (s.name().contains("ENDERMAN_TELEPORT") || s.name().contains("ENDERMEN_TELEPORT")) {
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_13_R1)) {
				return Sound.valueOf("ENTITY_ENDERMAN_TELEPORT");
			} else if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				return Sound.valueOf("ENTITY_ENDERMEN_TELEPORT");
			} else {
				return Sound.valueOf("ENDERMAN_TELEPORT");
			}
		}

		if (s.name().contains("WOLF_GROWL"))
			return b ? Sound.ENTITY_WOLF_GROWL : Sound.valueOf("WOLF_GROWL");

		return null;
	}

	public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
		for (Method m : cl.getMethods())
			if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes()))
				return m;
		return null;
	}

	public static Field getField(Class<?> cl, String field_name) {
		try {
			return cl.getDeclaredField(field_name);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean ClassListEqual(Class<?>[] firstClass, Class<?>[] secondClass) {
		boolean equal = true;
		//
		if (firstClass.length != secondClass.length)
			return false;
		//
		for (int i = 0; i < firstClass.length; i++) {
			if (firstClass[i] != secondClass[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}

	public static Class<?> getCraftClass(String ClassName) {
		try {
			return Class.forName("net.minecraft.server." + ServerVersion.getVersion().name() + "." + ClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Color getColorFromChatColor(ChatColor from) {
		switch (from) {
		case RED:
			return Color.RED;
		//
		case AQUA:
			return Color.AQUA;
		//
		case BLACK:
			return Color.BLACK;
		//
		case BLUE:
			return Color.BLUE;
		//
		case BOLD:
			return null;
		//
		case DARK_AQUA:
			return Color.AQUA;
		//
		case DARK_BLUE:
			return Color.BLUE;
		//
		case DARK_GRAY:
			return Color.GRAY;
		//
		case DARK_GREEN:
			return Color.GREEN;
		//
		case DARK_PURPLE:
			return Color.PURPLE;
		//
		case DARK_RED:
			return Color.RED;
		//
		case GOLD:
			return null;
		//
		case GRAY:
			return Color.GRAY;
		//
		case GREEN:
			return Color.GREEN;
		//
		case ITALIC:
			return null;
		//
		case LIGHT_PURPLE:
			return Color.PURPLE;
		//
		case MAGIC:
			return null;
		//
		case RESET:
			return null;
		//
		case STRIKETHROUGH:
			return null;
		//
		case UNDERLINE:
			return null;
		//
		case WHITE:
			return Color.WHITE;
		//
		case YELLOW:
			return Color.YELLOW;
		}
		return null;
	}

	public static Object[] getArrayFromList(List<?> l) {
		if (l == null)
			return null;
		//
		Object[] obj = new Object[l.size()];
		//
		for (int x = 0; x < l.size(); x++)
			obj[x] = l.get(x);
		//
		return obj;
	}
	
	public static <T> T[] ListAsArray(List<T> list, T[] arrayType) {
		if (list != null) {
			if (list.isEmpty()) {
				return arrayType;
			}
			
			final T[] tor = arrayType;
			for (int x = 0; x < arrayType.length; x++) {
				tor[x] = list.get(x);
			}
			
			return tor;
		}
		return null;
	}

	public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
		List<Block> blocks = new ArrayList<Block>();

		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		//
		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		//
		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		//
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc1.getWorld().getBlockAt(x, y, z);
					blocks.add(block);
				}
			}
		}
		//
		return blocks;
	}

	public static Entity getEntity(World w, UUID id) {
		if (w != null && id != null) {
			for (Entity lv : w.getEntities()) {
				if (lv == null || lv.getUniqueId() == null) {
					continue;
				}

				if (lv.getUniqueId().equals(id)) {
					return lv;
				}
			}
		}
		return null;
	}
	
    public static <T extends Entity> T getEntityByClass(Class<T> clazz, World w, UUID id) {
    	for (Entity ent : w.getEntitiesByClass(clazz)) {
    		if (ent == null) {
    			continue;
    		}
    		
    		Class<?> bukkitClass = ent.getClass();
    		
    		if (ent.getUniqueId().equals(id)) {
    			if (ent.getClass() != null) {
    				if (clazz.isAssignableFrom(bukkitClass)) {
    					return (T) ent;
    				}
    			}
    		}
    	}
        return null;
    }
	
	public static LivingEntity getLivingEntity(final World w, final UUID id) {
		if (w != null && id != null) {
			for (LivingEntity lv : w.getLivingEntities()) {
				if (lv == null || lv.getUniqueId() == null) {
					continue;
				}

				if (lv.getUniqueId().equals(id)) {
					return lv;
				}
			}
		}
		return null;
	}

	public static void convertFileToUFT_8(File f, File newFile) {
		try {
			final FileInputStream fis = new FileInputStream(f);
			final byte[] contents = new byte[fis.available()];
			fis.read(contents, 0, contents.length);
			final String asString = new String(contents, "ISO8859_1");
			final byte[] newBytes = asString.getBytes("UTF8");
			final FileOutputStream fos = new FileOutputStream(newFile);
			if (f.getPath().equalsIgnoreCase(newFile.getPath())) {
				if (f.getName().equalsIgnoreCase(newFile.getName())) {
					f.delete();
				}
			}
			fos.write(newBytes);
			fos.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File createNewUTF8File(File folder, String name) throws IOException {
		Validate.notNull(folder, "The folder cant be null");
		Validate.isTrue(folder.isDirectory(), "The folder must be a Directory");

		final File f = new File(folder, name);
		if (!f.exists()) {
			f.createNewFile();
		}
		final FileInputStream fis = new FileInputStream(f);
		final byte[] contents = new byte[fis.available()];
		fis.read(contents, 0, contents.length);
		final String asString = new String(contents, "ISO8859_1");
		final byte[] newBytes = asString.getBytes("UTF8");
		final FileOutputStream fos = new FileOutputStream(new File(folder, name));
		f.delete();
		fos.write(newBytes);
		fos.close();
		fis.close();
		return new File(folder, name);
	}

	public static void setInvisibleFor(final Entity ent, final Player... pls) {
		if (ent == null || pls == null || pls.length <= 0) {
			return;
		}

		try {
			Object packet = ReflectionUtils.getCraftClass("PacketPlayOutEntityDestroy").getConstructor(int[].class)
					.newInstance(new int[] { ent.getEntityId() });
			for (Player p : pls) {
				if (!KitUtils.isValidPlayer(p)) {
					continue;
				}

				ReflectionUtils.sendPacket(p, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLivingTicks(final Entity ent, long ticks) {
		// Check is not an player
		if (ent instanceof Player) {
			throw new IllegalArgumentException("The players cant be removed!");
		}

		if (ent != null) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					if (ent != null) {
						ent.remove();
					}
				}
			}, ticks);
		}
	}

	public static Location entityLookToLocation(Entity entity, Location to) {
		if (entity.getWorld() != to.getWorld()) {
			return null;
		}

		final Location fromLocation = entity.getLocation();
		double xDiff = (to.getX() - fromLocation.getX());
		double yDiff = (to.getY() - fromLocation.getY());
		double zDiff = (to.getZ() - fromLocation.getZ());

		double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);

		double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
		double pitch = (Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0D);
		if (zDiff < 0.0D) {
			yaw += (Math.abs(180.0D - yaw) * 2.0D);
		}

		Location loc = entity.getLocation();
		loc.setYaw((float) (yaw - 90.0F));
		loc.setPitch((float) (pitch - 90.0F));
		return loc;
	}

	public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST,
			BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

	public static BlockFace getFacing(float yaw) {
		BlockFace bf = yawToFace(yaw);
		if (bf.equals(BlockFace.NORTH)) {
			return BlockFace.SOUTH; //"SOUTH";
		} else if (bf.equals(BlockFace.EAST)) {
			return BlockFace.WEST; //"WEST";
		} else if (bf.equals(BlockFace.SOUTH)) {
			return BlockFace.NORTH; //"NORTH";
		} else
			return BlockFace.EAST; //"EAST";
	}

	public static BlockFace yawToFace(float yaw) {
		return yawToFace(yaw, false);
	}

	public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
		if (useSubCardinalDirections) {
			return radial[Math.round(yaw / 45f) & 0x7];
		} else {
			return axis[Math.round(yaw / 90f) & 0x3];
		}
	}

	public static float getLookAtYaw(Vector motion) {
		double dx = motion.getX();
		double dz = motion.getZ();
		double yaw = 0;
		// Set yaw
		if (dx != 0) {
			// Set yaw start value based on dx
			if (dx < 0) {
				yaw = 1.5 * Math.PI;
			} else {
				yaw = 0.5 * Math.PI;
			}
			yaw -= Math.atan(dz / dx);
		} else if (dz < 0) {
			yaw = Math.PI;
		}
		return (float) (-yaw * 180 / Math.PI - 90);
	}

	public static boolean isInventoryFull(Player p) {
		return p.getInventory().firstEmpty() == -1;
	}

	public static List<String> cloneList(List<String> list) {
		List<String> clone = new ArrayList<String>(list.size());
		for (String item : list) {
			clone.add(item);
		}
		return clone;
	}
	
	
	
//	public static Material getMaterialFromID(int id) {
//		for (Material material : Material.values()) {
//			if (material.getId() == id) {
//				return material;
//			}
//		}
//		return null;
//	}
}
