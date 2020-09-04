package com.hotmail.AdrianSRJose.AnniPro.Title;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;

public class TitleHandler {
	private static TitleHandler instance;

	public static TitleHandler getInstance() {
		if (instance == null) {
			instance = new TitleHandler();
		}
		return instance;
	}

	private TitleAPI titleApi;

	private TitleHandler() {
		try {
			String version = ServerVersion.getVersion().name();
			String name = "com.hotmail.AdrianSRJose.AnniPro.Title.versions." + version + ".title";
			Class<?> cl = Class.forName(name);
			Class<? extends TitleAPI> titleApi = cl.asSubclass(TitleAPI.class);
			TitleAPI manager = titleApi.newInstance();
			this.titleApi = manager;
		} catch (Exception e) {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.LIGHT_PURPLE + "Sorry, Titles not Supported for this spigot version");
		}
	}

	public void SendTitleToPlayer(Player p, String t, String s, int fadeIn, int stay, int fadeOut) {
		if (t != null && s != null)
			titleApi.sendTitleToPlayer(p, t, s, fadeIn, stay, fadeOut);
	}

	public TitleAPI getTitle() {
		return titleApi;
	}
}
