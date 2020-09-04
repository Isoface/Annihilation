package com.hotmail.AdrianSRJose.AnniPro.Title.versions.v1_8_R2;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.Title.TitleAPI;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R2.PlayerConnection;

public class title implements TitleAPI {
	@Override
	public void sendTitleToPlayer(Player paramPlayer, String paramTitle, String paramSubtitle, int paramFadeIn,
			int paramStay, int paramFadeOut) {
		if (!KitUtils.isValidPlayer(paramPlayer))
			return;
		//
		PlayerConnection plcn = ((CraftPlayer) paramPlayer).getHandle().playerConnection;
		if (plcn == null)
			return;
		//
		PacketPlayOutTitle ppot = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, paramFadeIn,
				paramStay, paramFadeOut);
		plcn.sendPacket(ppot);

		String t = paramTitle;
		String s = paramSubtitle;

		if (t != null) {
			IChatBaseComponent title = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + t + "\"}");
			PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title);
			plcn.sendPacket(titlePacket);
		}

		if (s != null) {
			IChatBaseComponent subtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}");
			PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
					subtitle);
			plcn.sendPacket(subtitlePacket);
		}
	}
}
