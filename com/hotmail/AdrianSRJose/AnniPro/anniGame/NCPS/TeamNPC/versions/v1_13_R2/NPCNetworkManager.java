package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_13_R2;

import net.minecraft.server.v1_13_R2.EnumProtocolDirection;
import net.minecraft.server.v1_13_R2.NetworkManager;

public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() {
		super(EnumProtocolDirection.SERVERBOUND);
	}

}
