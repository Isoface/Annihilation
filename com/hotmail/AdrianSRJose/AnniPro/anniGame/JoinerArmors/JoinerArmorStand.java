package com.hotmail.AdrianSRJose.AnniPro.anniGame.JoinerArmors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

import lombok.Getter;

public class JoinerArmorStand
{
	/**
	 * Global class values.
	 */
	private static final List<JoinerArmorStand> JOINERS = new ArrayList<JoinerArmorStand>();
	private static BukkitRunnable               UPDATER;
	
	/**
	 * Class values.
	 */
	private final @Getter AnniTeam             team;
	private final Loc                         spawn;
	private @Getter UUID                         id;
	private final @Getter JoinerArmorStandType type;
	private       UUID                    countLine;

	public JoinerArmorStand(final AnniTeam team, final Location spawn) {
		// get data.
		this.team   = team;
		this.spawn  = new Loc(spawn, true);
		this.type   = team != null ? JoinerArmorStandType.TEAM : JoinerArmorStandType.RANDOM;
	}
	
	public JoinerArmorStand(final ConfigurationSection sc) {
		// load data
		this.team   = AnniTeam.getTeamByName(sc.getString("Team"));
		this.spawn  = new Loc(sc.getConfigurationSection("Location"));
		this.type   = team != null ? JoinerArmorStandType.TEAM : JoinerArmorStandType.RANDOM;
	}
	
	public void spawn() {
		// spawn.
		for (int x = 0; x < 2; x++) {
			// check is count line, or normal.
			boolean isCountLine = (x > 0);
			
			// check is not a team stand.
			if (isCountLine && type == JoinerArmorStandType.RANDOM) {
				continue;
			}
			
			// get spawn.
			Location spawn = isCountLine ? this.spawn.toLocation().clone().add(0.0D, 0.3D, 0.0D)
					: this.spawn.toLocation().clone().add(0.0D, -0.07D, 0.0D);
			
			// clear pith.
			spawn.setPitch(0.0F); // avoid weird head poses.
			
			// spawn armor stand.
			final ArmorStand st = spawn.getWorld().spawn(spawn, ArmorStand.class);
			
			// save uuid.
			if (isCountLine) {
				countLine = st.getUniqueId();
			} else {
				id = st.getUniqueId();
			}
			
			// get lang use.
			final Lang langName = isCountLine ? Lang.JOINER_ARMOR_STANDS_COUNT_NAME
					: (team != null ? Lang.JOINER_ARMOR_STAND_NAMES : Lang.RAMDON_JOINER_ARMOR_STAND_NAMES);
			
			// set custom name.
			st.setCustomName(((team != null && !isCountLine) ? team.getColor().toString() : "") 
					+ langName.toStringReplacement((team != null ? team.getExternalName() : "")));
					
			// set modifiers.
			modify(st, !(isCountLine)); // gravity, visibility, metadata, etc...
			
			// add armor.
			if (team != null && !isCountLine) {
				addEquipment(st, getColorFrom(team.getColor()));
			}
		}
		
		// register.
		JOINERS.add(this);
		
		// check is initialized task updater.
		checkUpdater();
	}
	
	public void despawn() {
		// remove main armor stand.
		if (id != null) {
			final ArmorStand main = Util.getEntityByClass(ArmorStand.class, spawn.toLocation().getWorld(), id); // get.
			main.remove(); // remove.
		}
		
		// remove count line.
		if (countLine != null) {
			final ArmorStand count = Util.getEntityByClass(ArmorStand.class, spawn.toLocation().getWorld(), countLine); // get.
			count.remove(); // remove.
		}
		
		// remove this from JOINERS.
		JOINERS.remove(this);
	}
	
	public void saveToConfig(final ConfigurationSection sc) {
		// save team.
		if (team != null) {
			sc.set("Team", team.getName());
		}
		
		// save spawn.
		if (spawn != null) {
			spawn.saveToConfig(sc.createSection("Location"));
		}
	}
	
	private static void checkUpdater() {
		// check is not already initialized.
		if (UPDATER != null) {
			return;
		}
		
		// make updater task.
		UPDATER = new BukkitRunnable() {
			@Override
			public void run() {
				// get armors.
				for (JoinerArmorStand stand : JOINERS) {
					// update the two armors.
					for (int x = 0; x < 2; x++) {
						// get and check id.
						UUID uuid = (x == 0 ? stand.id : stand.countLine);
						if (uuid == null) {
							continue;
						}
						
						// check is countLine
						boolean isCountLine = (x > 0);
						
						// check is valid random armor stand.
						if (!isCountLine && stand.team != null) {
							continue;
						}
						
						// check is valid count line armor stand.
						if (isCountLine && stand.team == null) {
							continue;
						}
						
						// get armor.
						final ArmorStand st = Util.getEntityByClass(ArmorStand.class,
								stand.spawn.toLocation().getWorld(), uuid);
						
						// check armor.
						if (st == null || st.isDead()) { // stop task.
							continue;
						}
						
						// get name to set.
						final Lang langName = isCountLine ? Lang.JOINER_ARMOR_STANDS_COUNT_NAME
								: Lang.RAMDON_JOINER_ARMOR_STAND_NAMES;
						
						// get name.
						String name = isCountLine ? langName.toStringReplacement(getTeamCount(stand.team))
								: langName.toString();
						
						// get random color.
						final ChatColor color = getRandomChatColor();
						
						// change name if is random.
						if (!isCountLine) {
							name = (color.toString() + name);
						}
						
						// update name.
						st.setCustomName(name);
						
						// add armor.
						if (!isCountLine) {
							// add armor.
							addEquipment(st, getColorFrom(color));
						}
					}
				}
			}
		};
		
		// start.
		UPDATER.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 15L); // 15L to avoid lag...
	}
	
	public ArmorStand getArmorStand() {
		return (id != null ? (Util.getEntityByClass(ArmorStand.class, spawn.toLocation().getWorld(), id)) : null); 
	}
	
	private static int getTeamCount(final AnniTeam team) {
		// get count.
		int count = 0;
		
		// count players.
		for (AnniPlayer ap : team.getPlayers()) {
			// check is valid player.
			if (ap != null && ap.isOnline()) {
				count++; // count.
			}
		}
		return count;
	}
	
	private static ChatColor getRandomChatColor() {
		// make array.
		final ChatColor[] array = new ChatColor[] 
				{
						ChatColor.RED,
						ChatColor.BLUE,
						ChatColor.GREEN,
						ChatColor.YELLOW
				};
		// return from random.
		return array[RandomUtils.nextInt(array.length)];
	}
	
	private static Color getColorFrom(ChatColor chatColor) {
		switch(chatColor) {
		case BLUE:
			return Color.BLUE;
		case GREEN:
			return Color.GREEN;
		case YELLOW:
			return Color.YELLOW;
		default:
			return Color.RED;
		}
	}
	
	private void modify(final ArmorStand st, final boolean visible) {
		// change visibility.
		st.setVisible(visible);
		
		// custom name.
		st.setCustomNameVisible(true);
		
		// change gravity.
		st.setGravity(false);
	}
	
	private static void addEquipment(final ArmorStand stand, final Color color) {
		// add sword.
		stand.setArms(true);
		stand.setItemInHand(new ItemStack(Material.IRON_SWORD));
		
		// add armor.
		ItemStack pech     = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack casco    = new ItemStack(Material.LEATHER_HELMET);
		ItemStack pantalon = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack botas    = new ItemStack(Material.LEATHER_BOOTS);

		// get armors meta
		LeatherArmorMeta meta = (LeatherArmorMeta) pech.getItemMeta();
		
		// set color.
		meta.setColor(color);
		
		// set item meta.
		pech.setItemMeta(meta);
		casco.setItemMeta(meta);
		pantalon.setItemMeta(meta);
		botas.setItemMeta(meta);

		// set armor.
		stand.setChestplate(pech);
		stand.setHelmet(casco);
		stand.setLeggings(pantalon);
		stand.setBoots(botas);
	}
}