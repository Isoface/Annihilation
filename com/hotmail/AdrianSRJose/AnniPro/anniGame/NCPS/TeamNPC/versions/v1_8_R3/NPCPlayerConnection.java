package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_8_R3;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class NPCPlayerConnection extends PlayerConnection {

	public NPCPlayerConnection(NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(((CraftServer) Bukkit.getServer()).getServer(), networkmanager, entityplayer);
	}
}
