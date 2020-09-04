package com.hotmail.AdrianSRJose.AnniPro.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;

public class AnniBossMapCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("anniboss")) {
			if (args[0].equalsIgnoreCase("save")) {
				if (args[1].equalsIgnoreCase("map")) {
					if (sender instanceof Player) {
						if (GameBoss.getBossMap() != null) {
							Bukkit.getWorld(GameBoss.getBossMap().getWorldName()).save();
							sender.sendMessage(ChatColor.GREEN + "Boss Map Saved!");
							GameBoss.getBossMap().getConfig().set("WorldName", GameBoss.getBossMap().getWorldName());

							if (GameBoss.getBoss() != null) {
								ConfigurationSection Boss = GameBoss.getBossMap().getConfig()
										.createSection("BossConfiguration");
								ConfigurationSection spawn = Boss.createSection("BossSpawnPoint");

								if (GameBoss.getBoss().getSpawn() != null)
									GameBoss.getBoss().getSpawn().saveToConfig(spawn);

								if (GameBoss.getBoss().getName() != null)
									Boss.set("Name", GameBoss.getBoss().getName());
								//
								Boss.set("Respawn Time", GameBoss.getBoss().getRespawnTime());
								//
								if (GameBoss.getBoss().getRespawnUnit() != null)
									Boss.set("Unit", GameBoss.getBoss().getRespawnUnit().name());
								//
								Boss.set("Health", GameBoss.getBoss().getVida());

								if (GameBoss.getBoss().getType() != null) {
									if (GameBoss.getBoss().getType().equals(Wither.class))
										Boss.set("Type", "Wither");
									else
										Boss.set("Type", "IronGolem");
								}
								//
								GameBoss.getBossMap().saveTheConfig();
								((AnnihilationMain) AnnihilationMain.INSTANCE)
								.checkAnniMapsCacheFile(GameBoss.getBossMap().getNiceBossWorldName(), false);
							}
						} else {
							sender.sendMessage(ChatColor.RED + "You do not Have a Boss Map Loaded!");
						}
					} else
						sender.sendMessage(Lang.MUST_BE_PLAYER_COMMAND.toString());
				}
			}
			return true;
		}
		return false;
	}

}
