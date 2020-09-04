package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_10_R1;

import java.lang.reflect.Field;
import java.net.SocketAddress;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCChannel;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.minecraft.server.v1_10_R1.EnumProtocolDirection;
import net.minecraft.server.v1_10_R1.NetworkManager;

public class NPCNetworkManager extends NetworkManager {

	@SuppressWarnings("unchecked")
	public NPCNetworkManager() {
		super(EnumProtocolDirection.SERVERBOUND);

		try {
			Field channel = NetworkManager.class.getDeclaredField("channel");
			Field address = NetworkManager.class.getDeclaredField("l");

			channel.setAccessible(true);
			address.setAccessible(true);

			Channel parent = new NPCChannel(null);
			try {
				Field protocolVersion = NetworkManager.class.getDeclaredField("protocolVersion"); // can be "c"
				parent.attr(((AttributeKey<Integer>) protocolVersion.get(null))).set(5);
			} catch (NoSuchFieldException ignored) { // This server isn't spigot, we're good.
			}
			channel.set(this, parent);
			address.set(this, new SocketAddress() {
				private static final long serialVersionUID = 6994835504305404545L;
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
