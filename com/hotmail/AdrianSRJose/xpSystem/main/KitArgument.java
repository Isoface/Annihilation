package com.hotmail.AdrianSRJose.xpSystem.main;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniArgument;
import com.hotmail.AdrianSRJose.AnniPro.utils.IDTools;
import com.google.common.base.Predicate;

public class KitArgument implements AnniArgument {
	private XPSystem xpSystem;

	public KitArgument(XPSystem system) {
		this.xpSystem = system;
	}

	@Override
	public void executeCommand(final CommandSender sender, String label, final String[] args) {
		if (args != null && args.length > 2) {
			IDTools.getUUID(args[2], new Predicate<UUID>() {
				@Override
				public boolean apply(UUID id) {
					if (id != null) {
						Player p = Bukkit.getPlayer(id);
						if (p == null) {
							return false;
						}

						Kit kit = Kit.getKit(args[1]);
						if (kit != null) {
							if (args[0].equalsIgnoreCase("add")) {
								sender.sendMessage(ChatColor.GREEN + "Kit added.");
								xpSystem.addKit(id, p.getName(), kit);
								sender.sendMessage("Added kit " + kit.getName());
							} else if (args[0].equalsIgnoreCase("remove")) {
								sender.sendMessage(ChatColor.RED + "Kit removed.");
								xpSystem.removeKit(id, kit);
								sender.sendMessage("Removed kit " + kit.getName());
							} else
								sender.sendMessage(ChatColor.RED + "Operation " + ChatColor.GOLD + args[0]
										+ ChatColor.RED + " is not supported.");
						} else
							sender.sendMessage(ChatColor.RED + "Could not locate the kit you specified.");
					} else
						sender.sendMessage(ChatColor.RED + "Could not locate the player you specified.");
					return false;
				}
			});
		}
	}

	@Override
	public String getArgumentName() {
		return "Kit";
	}

	@Override
	public String getHelp() {
		return ChatColor.LIGHT_PURPLE + "Kit [add,remove] <kit> <player>--" + ChatColor.GREEN
				+ "adds or removes a kit from a player.";
	}

	@Override
	public MenuItem getMenuItem() {
		return null;
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public boolean useByPlayerOnly() {
		return false;
	}
}
