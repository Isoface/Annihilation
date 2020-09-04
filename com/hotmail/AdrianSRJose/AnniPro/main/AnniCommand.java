package com.hotmail.AdrianSRJose.AnniPro.main;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.plugin.AnniPlugin;
import com.hotmail.AdrianSRJose.AnniPro.plugin.PluginArgumentManager;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class AnniCommand {
	private static boolean registered = false;
	private static Map<String, AnniArgument> arguments;
	private static ItemMenu menu;

	public static void register(JavaPlugin plugin) {
		if (!registered) {
			registered = true;
			arguments = new TreeMap<String, AnniArgument>();
			recalcItemMenu();
			plugin.getCommand("Anni").setExecutor(new Executor());
			registerArgument(new AnniArgument() {
				@Override
				public String getHelp() {
					return ChatColor.RED + "Help--" + ChatColor.GOLD + "Returns the help for the /Anni command";
				}

				@Override
				public boolean useByPlayerOnly() {
					return false;
				}

				@Override
				public String getArgumentName() {
					return "Help";
				}

				@Override
				public void executeCommand(CommandSender sender, String label, String[] args) {
					sendHelp(sender);
				}

				@Override
				public String getPermission() {
					return null;
				}

				@Override
				public MenuItem getMenuItem() {
					return new ActionMenuItem(Util.wc("&6&lHelp"), new ItemClickHandler() {
						@Override
						public void onItemClick(ItemClickEvent event) {
							executeCommand(event.getPlayer(), null, null);
							event.setWillClose(true);
						}
					}, new ItemStack(Material.BOOK),
							ChatColor.GREEN + Util.wc("&6Click to show help for the /Anni command"));
				}
			});
		}
	}

	private static void recalcItemMenu() {
		menu = new ItemMenu(Util.wc("&4&lAnnihilation | Commands"),
				arguments.isEmpty() ? Size.ONE_LINE : Size.fit(arguments.size()));
		int x = 0;
		for (AnniArgument arg : arguments.values()) {
			if (arg.getMenuItem() != null) {
				menu.setItem(x, arg.getMenuItem());
				x++;
			}
		}
	}

	public static void registerArgument(AnniArgument argument) {
		if (argument != null) {
			if (argument.getPermission() != null) {
				Permission perm = new Permission(argument.getPermission());
				Bukkit.getPluginManager().addPermission(perm);
				perm.recalculatePermissibles();
			}
			arguments.put(argument.getArgumentName().toLowerCase(), argument);
			recalcItemMenu();
		}
	}

	public static boolean isRegisteredArgument(AnniArgument argument) {
		return argument != null && !arguments.containsKey(argument.getArgumentName().toLowerCase());
	}
	
	private static void sendHelp(CommandSender sender) {
		if (arguments.isEmpty())
			sender.sendMessage(ChatColor.RED + "There are currently no registered arguments for the /Anni command!");
		else {
			for (AnniArgument arg : arguments.values())
				sender.sendMessage(arg.getHelp());
		}
	}

	private static void openMenu(Player player) {
		recalcItemMenu();
		menu.open(player);
	}

	private static class Executor implements CommandExecutor {
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (!(sender instanceof Player) || sender.hasPermission("A.anni")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						openMenu((Player) sender);
					} else {
						sender.sendMessage(Lang.MUST_BE_PLAYER_COMMAND.toString());
					}
				} else {
					// check registered arguments
					AnniArgument arg = arguments.get(args[0].toLowerCase());
					
					// check if is a argument from an anni plugin 
					if (arg == null) {
						// refreshe plugins arguments
						PluginArgumentManager.refreshArguments();
						
						// check plugins arguments
						for (AnniPlugin plugin : PluginArgumentManager.getRegisteredPluginsArguments().keySet()) {
							// check plugin and his arguments
							List<AnniArgument> pluginArgs = PluginArgumentManager.getRegisteredPluginsArguments().get(plugin);
							if (plugin == null || !plugin.isEnabled()
									|| pluginArgs == null || pluginArgs.isEmpty()) {
								continue;
							}
							
							// check if is using an argument from the plugin
							for (AnniArgument plArg : pluginArgs) {
								// check not null arg
								if (plArg == null) {
									continue;
								}
								
								// get arg from arg name
								if (args[0].toLowerCase().equalsIgnoreCase(plArg.getArgumentName())) {
									// set arg to execute it
									arg = plArg;
									break;
								}
							}
						}
					}
					
					if (arg != null) {
						if (arg.useByPlayerOnly()) {
							if (!(sender instanceof Player)) {
								sender.sendMessage(ChatColor.RED + "The argument " + ChatColor.GOLD
										+ arg.getArgumentName() + ChatColor.RED + " must be used by a player.");
								return true;
							}
						}
						arg.executeCommand(sender, label, excludeFirstArgument(args));
					} else
						sendHelp(sender);
				}
			} else
				sender.sendMessage(Lang.WITHOUT_PERMISSION_COMMAND.toString());
			return true;
		}

		private String[] excludeFirstArgument(String[] args) {
			String[] r = new String[args.length - 1];
			if (r.length == 0)
				return r;
			for (int x = 1; x < args.length; x++)
				r[x - 1] = args[x];
			return r;
		}
	}
}