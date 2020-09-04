package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_9_R2;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.NetworkManager;
import net.minecraft.server.v1_9_R2.PlayerConnection;

public class NPCPlayerConnection extends PlayerConnection {

	public NPCPlayerConnection(NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(((CraftServer) Bukkit.getServer()).getServer(), networkmanager, entityplayer);
	}
}
