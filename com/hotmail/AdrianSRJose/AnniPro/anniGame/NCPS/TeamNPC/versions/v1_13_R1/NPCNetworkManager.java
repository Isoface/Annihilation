package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_13_R1;

import net.minecraft.server.v1_13_R1.EnumProtocolDirection;
import net.minecraft.server.v1_13_R1.NetworkManager;

public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() {
		super(EnumProtocolDirection.SERVERBOUND);
	}

}
