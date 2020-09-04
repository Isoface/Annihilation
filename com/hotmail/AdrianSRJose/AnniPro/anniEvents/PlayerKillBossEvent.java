package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public final class PlayerKillBossEvent extends Event {
	private static final HandlerList list = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	private AnniPlayer killer;
	private LivingEntity Boss;
	private int RespawnTime;

	public PlayerKillBossEvent(AnniPlayer killer, LivingEntity boss, int respawnTime) {
		Boss = boss;
		this.killer = killer;
		RespawnTime = respawnTime;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	public AnniPlayer getKiller() {
		return killer;
	}

	public LivingEntity getBoss() {
		return Boss;
	}

	public int getBossRespawnTime() {
		return RespawnTime;
	}
}
