package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class AnniPluginArgumentYamlParser {

	public static List<AnniCommandArgument> parse(AnniPlugin AnniPlugin) {
		// create command list to return
		final List<AnniCommandArgument> pluginCmds = new ArrayList<AnniCommandArgument>();
		
		// get arguments Map and check it
		Map<String, Map<String, Object>> map = AnniPlugin.getDescription().getArguments();
		if (map == null) {
			return pluginCmds;
		}

		// load arguments and values
		for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
			if (entry.getKey().contains(":")) {
				Bukkit.getServer().getLogger().severe("Could not load argument " + entry.getKey() + " for AnniPlugin " + AnniPlugin.getName() + ": Illegal Characters");
				continue;
			}

			// command name = entry.getKey()
			// get command values
			AnniCommandArgument newArg = new AnniCommandArgument(entry.getKey());
			Object description         = entry.getValue().get("description");
			Object aliases             = entry.getValue().get("aliases");

			// set loaded description
			if (description != null) {
				newArg.setDescription(description.toString());
			}

			// set loaded aliases
			if (aliases != null) {
				List<String> aliasList = new ArrayList<String>();
				if (aliases instanceof List) {
					for (Object o : (List<?>) aliases) {
						if (o.toString().contains(":")) {
							Bukkit.getServer().getLogger().severe("Could not load argument alias " + o.toString()
									+ " for AnniPlugin " + AnniPlugin.getName() + ": Illegal Characters");
							continue;
						}
						aliasList.add(o.toString());
					}
				} else {
					if (aliases.toString().contains(":")) {
						Bukkit.getServer().getLogger().severe("Could not load argument alias " + aliases.toString()
								+ " for AnniPlugin " + AnniPlugin.getName() + ": Illegal Characters");
					} else {
						aliasList.add(aliases.toString());
					}
				}

				newArg.setAliases(aliasList);
			}

			// add command
			pluginCmds.add(newArg);
		}
		return pluginCmds;
	}
}
