package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Predicate;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class IDTools {
	public static void getName(final UUID ID, final Predicate<String> callback) {
		Player p = Bukkit.getPlayer(ID);
		if (p != null) {
			callback.apply(p.getName());
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				String line = null;
				try {
					URL url = new URL(
							"https://api.mojang.com/user/profiles/" + ID.toString().replaceAll("-", "") + "/names");
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					line = in.readLine();
					line = line.replace("[\"", "");
					line = line.replace("\"]", "");
				} catch (Exception ex) {

				}
				final String s = line;
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						callback.apply(s);
					}
				});
			}
		});
	}

	public static void getUUID(final String player, final Predicate<UUID> callback) {
		Player p = Bukkit.getPlayer(player);
		if (p != null) {
			callback.apply(p.getUniqueId());
			return;
		}

		Bukkit.getScheduler().runTaskAsynchronously(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				String uuid = null;
				try {
					URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + player);
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String Line;
					while ((Line = in.readLine()) != null) {
						uuid = Line.substring(7, 39);
						uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-"
								+ uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
						break;
					}
					in.close();
				} catch (Exception ex) {

				}
				UUID id = null;
				try {
					id = UUID.fromString(uuid);
				} catch (Exception e) {
					id = null;
				}
				final UUID d = id;
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						callback.apply(d);
					}
				});
			}
		});
	}

	public static void getName(String uuid, Predicate<String> callback) {
		getName(UUID.fromString(uuid), callback);
	}
}
