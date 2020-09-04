package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class TeamNPCManager implements Listener {
	public TeamNPCManager(JavaPlugin pl) {
		try {
			final String className = "com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions."
					+ ServerVersion.getVersion().toString() + ".NPCFactory";
			Class<?> cl = Class.forName(className);
			Class<? extends Listener> pack = cl.asSubclass(Listener.class);
			Bukkit.getPluginManager().registerEvents(pack.getConstructor(Plugin.class).newInstance(pl), pl);
		} catch (Throwable t) {
			Util.print(ChatColor.RED, "Unsupported Server Version for the NPCs:");
			t.printStackTrace();
		}
	}
}
