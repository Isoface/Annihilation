package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.main.AnniArgument;

public class PluginArgumentManager 
{
	private static final Map<AnniPlugin, List<AnniArgument>> PLUGIN_ARGUMENTS = new HashMap<AnniPlugin, List<AnniArgument>>();
	public static Map<AnniPlugin, List<AnniArgument>> getRegisteredPluginsArguments() {
		return PLUGIN_ARGUMENTS;
	}
	
	public static void registerArgument(final AnniPlugin pluginOwner, final AnniCommandArgument arg) {
		if (pluginOwner != null && arg != null) {
			// check list
			List<AnniArgument> args = PLUGIN_ARGUMENTS.get(pluginOwner);
			if (args == null) {
				args = new ArrayList<AnniArgument>();
			}
			
			// add argument
			final AnniArgument argmt = getArgument(arg);
			if (isRegisteredArgument(pluginOwner, argmt)) {
				args.add(argmt);
			}
			
			// put list
			PLUGIN_ARGUMENTS.put(pluginOwner, args);
		}
	}
	
	public static void unregisterArgument(final AnniPlugin pluginOwner, final AnniCommandArgument arg) {
		if (pluginOwner != null && arg != null) {
			// check list
			final List<AnniArgument> args = PLUGIN_ARGUMENTS.get(pluginOwner);
			if (args == null || args.isEmpty()) {
				return;
			}
			
			// remove argument
			final AnniArgument argmt = getArgument(arg);
			if (isRegisteredArgument(pluginOwner, argmt)) {
				for (AnniArgument other : new ArrayList<AnniArgument>(PLUGIN_ARGUMENTS.get(pluginOwner))) {
					if (equals(argmt, other)) {
						args.remove(other);
					}
				}
			}
			
			// put list
			PLUGIN_ARGUMENTS.put(pluginOwner, args);
		}
	}
	
	public static void refreshArguments() {
		// check arguments map
		if (PLUGIN_ARGUMENTS.isEmpty()) {
			return;
		}
		
		// create and register arguments again
		for (AnniPlugin plugin : PLUGIN_ARGUMENTS.keySet()) {
			// check plugin and his arguments
			if (plugin == null || plugin.getArguments() == null || plugin.getArguments().isEmpty()) {
				continue;
			}
			
			// create list to put
			List<AnniArgument> toPut = new ArrayList<AnniArgument>();
			
			// refresh
			for (AnniCommandArgument arg : plugin.getArguments()) {
				// check argument
				if (arg == null || getArgument(arg) == null) {
					continue;
				}
				
				// create and save
				toPut.add(getArgument(arg));
			}
			
			// put
			PLUGIN_ARGUMENTS.put(plugin, toPut);
		}
	}
	
	private static AnniArgument getArgument(final AnniCommandArgument arg) {
		if (arg != null) {
			return new AnniArgument() {
				@Override
				public String getHelp() {
					return arg.getHelp();
				}

				@Override
				public boolean useByPlayerOnly() {
					return arg.isUseByPlayerOnly();
				}

				@Override
				public String getArgumentName() {
					return arg.getName();
				}

				@Override
				public void executeCommand(CommandSender sender, String label, String[] args) {
					if (arg.getExecutor() != null) {
						arg.getExecutor().onArgument(sender, label, args);
					}
				}

				@Override
				public String getPermission() {
					return "A.anni";
				}

				@Override
				public MenuItem getMenuItem() {
					return null;
				}
			};
		}
		return null;
	}
	
	private static boolean isRegisteredArgument(final AnniPlugin plugin, final AnniArgument arg) {
		if (arg != null && plugin != null && PLUGIN_ARGUMENTS.get(plugin) != null) {
			for (AnniArgument other : PLUGIN_ARGUMENTS.get(plugin)) {
				if (equals(arg, other)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean equals(final AnniArgument arg1, final AnniArgument arg2) {
		if (arg1 == arg2) {
			return true;
		}
		
		if (arg1 == null && arg2 == null) {
			return false;
		}
		
		if ((arg1 == null && arg2 != null) || (arg1 != null && arg2 == null)) {
			return false;
		}
		
		if (arg1.getArgumentName() == null && arg2.getArgumentName() == null) {
			return true;
		}
		
		if ((arg1.getArgumentName() == null && arg2.getArgumentName() != null) 
				|| (arg1.getArgumentName() != null && arg2.getArgumentName() == null)) {
			return false;
		}
		
		return arg1.getArgumentName().equalsIgnoreCase(arg2.getArgumentName());
	}
}
