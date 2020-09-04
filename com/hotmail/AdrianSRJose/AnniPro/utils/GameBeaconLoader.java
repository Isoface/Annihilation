package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.BeaconEffect;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.MagicBeacon.ImprovableBeaconEffect;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class GameBeaconLoader
{
	public static List<BeaconEffect> EFFECTS = new ArrayList<BeaconEffect>();
	public static String  maxLevelMesage     = "&cThis effect is at the highest level!";
	public static int breakableFromPhase     = 1;
	public static String noCapturableMessage = "&cYou cannot capture the Magic beacon in this phase!";
	public static double protectedRange      = 5;
	
	public static void load(AnnihilationMain instance) {
		// Setting and checking the yml.
		final Utf8YamlConfiguration YML = getConfig(instance);
		int saveDefaults                = 0;
		if (YML == null) {
			return;
		}
		
		// Check Config section
		saveDefaults += Util.createSectionIfNoExitsInt(YML, "Config");
		saveDefaults += Util.setDefaultIfNotSet(YML.getConfigurationSection("Config"), "capturable-from-phase", 1);
		saveDefaults += Util.setDefaultIfNotSet(YML.getConfigurationSection("Config"), "max-level-message:", "&cThis effect is at the highest level!");
		saveDefaults += Util.setDefaultIfNotSet(YML.getConfigurationSection("Config"), "no-capturable-message:", "&cYou cannot capture the Magic beacon in this phase!");
		saveDefaults += Util.setDefaultIfNotSet(YML.getConfigurationSection("Config"), "protected-range", 5);
		
		// Checking effects Section
		saveDefaults += Util.createSectionIfNoExitsInt(YML, "Effects");
		
		// Saving Defautls when the section was not exists
		if (saveDefaults > 0) {
			int counter = 1;
			for (ImprovableBeaconEffect eff : defaults()) {
				saveDefaults += eff.save(YML.getConfigurationSection("Effects").createSection("" + counter));
				counter ++;
			}
		}
		
		// Saving Config 
		if (saveDefaults > 0) {
			try {
				// Create if not exits
				final File file = new File(instance.getDataFolder(), "MagicBeaconConfig.yml");
				if (!file.exists()) {
					file.mkdir();
				}
				
				// Save
				YML.save(file);
			} catch (IOException e) {
				Util.print(ChatColor.RED, "Coult not save defaults for 'MagicBeaconConfig.yml':");
				e.printStackTrace();
				return;
			}
		}
		
		// Loading Config
		final ConfigurationSection effects = YML.getConfigurationSection("Effects");
		if (effects != null) {
			for (String key : effects.getKeys(false)) {
				ConfigurationSection effect = effects.getConfigurationSection(key);
				if (effect != null) {
					// Checking if is improvable or not
					if (effect.getBoolean("improvable")) {
						ImprovableBeaconEffect ibe = new ImprovableBeaconEffect(effect);
						if (ibe != null) {
							EFFECTS.add(ibe);
						}
					}
					else {
						BeaconEffect be = new BeaconEffect(effect);
						if (be != null) {
							EFFECTS.add(be);
						}
					}
				}
			}
		}
		
		final ConfigurationSection config = YML.getConfigurationSection("Config");
		maxLevelMesage      = Util.wc(config.getString("max-level-message", "&cThis effect is at the highest level!"));
		breakableFromPhase  = config.getInt("capturable-from-phase");
		noCapturableMessage = Util.wc(config.getString("no-capturable-message:", "&cYou cannot capture the Magic beacon in this phase!"));
		protectedRange      = Math.max(config.getInt("protected-range", 5), 0);
	}
	
	private static List<ImprovableBeaconEffect> defaults() {
		final List<ImprovableBeaconEffect> list = new ArrayList<ImprovableBeaconEffect>();
		// effect, level, range, icon, cost, costMaterial, costMultiplier, maxLevel
		list.add(new ImprovableBeaconEffect(PotionEffectType.SPEED, 1, 30.0D, Material.FEATHER, 180,
				Material.GOLD_INGOT, 3, "&6&lSpeed")
				.addLine(false, "", "&7Buy Speed", "&7Cost: %#")
				.addLine(true,  "", "&7Increase level to %level", "&7Cost: %#")
				.addLineMaxLore("", "&aMAX LEVEL"));
		
		list.add(new ImprovableBeaconEffect(PotionEffectType.INCREASE_DAMAGE, 1, 30.0D, Material.DIAMOND_SWORD, 300,
				Material.GOLD_INGOT, 2, "&6&lStrenght")
				.addLine(false, "", "&7Buy Strenght", "&7Cost: %#")
				.addLine(true,  "", "&7Increase level to %level", "&7Cost: %#")
				.addLineMaxLore("", "&aMAX LEVEL"));
		
		list.add(new ImprovableBeaconEffect(PotionEffectType.REGENERATION, 1, 30.0D, Material.GOLDEN_APPLE, 250,
				Material.GOLD_INGOT, 2, "&6&lRegeneration")
				.addLine(false, "", "&7Buy Regeneration", "&7Cost: %#")
				.addLine(true,  "", "&7Increase level to %level", "&7Cost: %#")
				.addLineMaxLore("", "&aMAX LEVEL"));
		
		list.add(new ImprovableBeaconEffect(PotionEffectType.JUMP, 1, 30.0D, Material.SLIME_BLOCK, 100,
				Material.GOLD_INGOT, 3, "&6&lJump")
				.addLine(false, "", "&7Buy Jump", "&7Cost: %#")
				.addLine(true,  "", "&7Increase level to %level", "&7Cost: %#")
				.addLineMaxLore("", "&aMAX LEVEL"));
		
		list.add(new ImprovableBeaconEffect(PotionEffectType.NIGHT_VISION, 1, 30.0D, Material.PUMPKIN, 150,
				Material.GOLD_INGOT, 1, "&6&lNight Vision").setImprovable(false)
				.addLine(false, "", "&7Buy Night Vision", "&7Cost: %#")
				.addLine(true,  "", "&7Purchased")
				.addLineMaxLore("", "&aMAX LEVEL"));
		return list;
	}
	
	public static Utf8YamlConfiguration getConfig(AnnihilationMain instance) {
		// Check File
		final File file = new File(instance.getDataFolder(), "MagicBeaconConfig.yml");
		if (!file.exists()) {
			try {
				instance.getDataFolder().mkdir();
				file.createNewFile();
			} catch (IOException e) {
				Util.print(ChatColor.RED, "Could not create the Magic Beacon config file:");
				e.printStackTrace();
				return null;
			}
		}
		
		// Return yml
		return Utf8YamlConfiguration.loadConfiguration(file);
	}
}
