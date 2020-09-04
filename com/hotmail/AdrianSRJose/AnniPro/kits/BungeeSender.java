package com.hotmail.AdrianSRJose.AnniPro.kits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class BungeeSender implements PluginMessageListener {
	public BungeeSender(JavaPlugin pl) {
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(pl, "BungeeCord", this);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pl, "BungeeCord");
	}

	public void sendPlayerTo(final Player p, final String serverTo) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				for (String element : new String[] { "ConnectOther", p.getName(), serverTo }) {
					out.writeUTF(element);
				}

				Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
				if (player != null) {
					player.sendPluginMessage(AnnihilationMain.INSTANCE, "BungeeCord", out.toByteArray());
				}
			}
		}, 20L);
	}

	@Override
	public void onPluginMessageReceived(String paramString, Player paramPlayer, byte[] paramArrayOfByte) {
	}
}
