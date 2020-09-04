package com.hotmail.AdrianSRJose.AnniPro.announcementBar.versions.v1_13_R2;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.announcementBar.BossBarHandler;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;

public class Bar implements com.hotmail.AdrianSRJose.AnniPro.announcementBar.Bar 
{
	private static boolean SEND_BOSS_BAR = true;
	private static boolean SEND_PHASE_BAR = true;
	
	@Override
	public void sendToPlayer(final Player player, final String message, final String bossbarmess, final float percentOfTotal) {
		if (Config.USE_PHASE_BAR.toBoolean() && SEND_PHASE_BAR) {
			
			IChatBaseComponent actionComponent = ChatSerializer.a("{\"text\":\"" + cleanMessage(message) + "\"}");
			PacketPlayOutChat actionPacket = new PacketPlayOutChat(actionComponent, ChatMessageType.GAME_INFO);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionPacket);
		}

//		if (scoreBoardNexPhaseTime != null) {
//			if (ScoreboardVars.useNewScoreboard) {
//				ScoreboardAPINEW.setNexPhaseTime(scoreBoardNexPhaseTime);
//			} else {
//				ScoreboardAPI.setNexPhaseTime(scoreBoardNexPhaseTime);
//			}
//		}

		if (percentOfTotal <= 1.0 && bossbarmess != null && !bossbarmess.equals("")) {
			if (Config.USE_BOSS_BAR.toBoolean() && SEND_BOSS_BAR) {
				try {
					BossBarHandler.sendBar(player, cleanMessage(bossbarmess), percentOfTotal);
				} catch (ClassNotFoundException ParamException) {
					// ParamException.printStackTrace();
				}
				BossBarHandler.updater(false, true, true, cleanMessage(bossbarmess), percentOfTotal);
			}
		}
	}

	private static String cleanMessage(String message) {
		if (message.length() > 64)
			message = message.substring(0, 63);

		return message;
	}

	@Override
	public void removeBossBar(Player player) {
	}
	
	@Override
	public void destroyBossBar() {
		SEND_BOSS_BAR = false;
	}

	@Override
	public void destroyPhaseBar() {
		SEND_PHASE_BAR = false;
	}
}
