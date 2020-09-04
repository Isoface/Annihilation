package com.hotmail.AdrianSRJose.AnniPro.anniGame.autoRespawn.versions;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.autoRespawn.RespawnPacket;

import net.minecraft.server.v1_9_R2.PacketPlayInClientCommand;
import net.minecraft.server.v1_9_R2.PacketPlayInClientCommand.EnumClientCommand;

public class v1_9_R2Packet implements RespawnPacket {
	private final PacketPlayInClientCommand packet;

	public v1_9_R2Packet() {
		packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
	}

	@Override
	public void sendToPlayer(final Player player) {
		if (player != null && player.isOnline()) {
			CraftPlayer p = (CraftPlayer) player;
			//
			if (p != null && p.isOnline())
				p.getHandle().playerConnection.a(packet);
		}
	}
}
