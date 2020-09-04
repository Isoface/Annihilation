package com.hotmail.AdrianSRJose.AnniPro.utils.TPS.versions.v1_12_R1;

import com.hotmail.AdrianSRJose.AnniPro.utils.TPS.ITPS;
import com.hotmail.AdrianSRJose.AnniPro.utils.TPS.TPS;

import net.minecraft.server.v1_12_R1.MinecraftServer;

public class TPSGetter implements ITPS {
	
	/**
	 * Execute query in the 1.12 bukkit version.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String executeTPSQuery() {
		StringBuilder sb = new StringBuilder();
		for (double tps : MinecraftServer.getServer().recentTps) {
			sb.append(TPS.format(tps));
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}
}
