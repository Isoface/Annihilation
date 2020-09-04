package com.hotmail.AdrianSRJose.AnniPro.announcementBar.versions.v1_8_R2;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;

public class Bar implements com.hotmail.AdrianSRJose.AnniPro.announcementBar.Bar, Listener 
{
	private static boolean SEND_BOSS_BAR = true;
	private static boolean SEND_PHASE_BAR = true;
	private HashMap<UUID, WBar> bars;

	public Bar() {
		bars = new HashMap<UUID, WBar>();
		Bukkit.getPluginManager().registerEvents(this, AnnihilationMain.INSTANCE);
	}

	@Override
	public void sendToPlayer(final Player player, final String message, final String bossbarmess, final float percentOfTotal) {
		if (Config.USE_PHASE_BAR.toBoolean() && message != null && SEND_PHASE_BAR) {
			IChatBaseComponent actionComponent = ChatSerializer.a("{\"text\":\"" + cleanMessage(message) + "\"}");
			PacketPlayOutChat actionPacket = new PacketPlayOutChat(actionComponent, (byte) 2);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionPacket);
		}

//		if (scoreBoardNexPhaseTime != null) {
//			if (ScoreboardVars.useNewScoreboard) {
//				ScoreboardAPINEW.setNexPhaseTime(scoreBoardNexPhaseTime);
//			} else {
//				ScoreboardAPI.setNexPhaseTime(scoreBoardNexPhaseTime);
//			}
//		}

		if (Config.USE_BOSS_BAR.toBoolean() && SEND_BOSS_BAR) {
			if (bossbarmess != null && !bossbarmess.equals("")) {
				int phase = Game.getGameMap().getCurrentPhase();

				if (bars.get(player.getUniqueId()) == null) {
					bars.put(player.getUniqueId(), new WBar(player, cleanMessage(bossbarmess), percentOfTotal));
				}

				WBar bar = checkWorld(player, bossbarmess, percentOfTotal);
				if (phase == 5) {
					bar.setProgress(0.00000000000000000000001F, cleanMessage(Lang.BOSSBAR.toStringReplacement(5)));
				} else {
					if (percentOfTotal == 0.0F) {
						bar.setProgress(0.00000000000000000000001F, cleanMessage(bossbarmess));
					} else {
						bar.setProgress(percentOfTotal, cleanMessage(bossbarmess));
					}
				}
				bar.Teleport();
			}
		}
	}

	private WBar checkWorld(final Player p, final String bossbarmess, final float percentOfTotal) {
		WBar b = bars.get(p.getUniqueId());
		if (!p.getWorld().getName().equals(b.getWither().getWorld().getName())) {
			b.remove();
			WBar other = new WBar(p, cleanMessage(bossbarmess), percentOfTotal);
			bars.put(p.getUniqueId(), other);
			return other;
		}
		return b;
	}

	private static String cleanMessage(String message) {
		if (message.length() > 64)
			message = message.substring(0, 63);

		return message;
	}

	@Override
	public void removeBossBar(Player player) {
		if (bars.get(player.getUniqueId()) != null)
			bars.get(player.getUniqueId()).remove();
	}

	@EventHandler
	public void onM(PlayerMoveEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		final Player p = eve.getPlayer();
		if (KitUtils.isValidPlayer(p)) {
			if (bars.get(p.getUniqueId()) != null) {
				bars.get(p.getUniqueId()).Teleport();
			}
		}
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
