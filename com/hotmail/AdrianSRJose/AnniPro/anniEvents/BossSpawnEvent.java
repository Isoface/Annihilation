package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public class BossSpawnEvent extends Event implements Cancellable
{
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;
	private final @Getter LivingEntity         boss;
	private final @Getter Location            spawn;
	private @Getter @Setter boolean   useSpawnSound = true;
	private @Getter @Setter boolean  willOpenPortal = true;
	private @Getter @Setter boolean useSpawnMessage = true;
	
	public BossSpawnEvent(final LivingEntity boss, final Location spawn) {
		this.boss  = boss;
		this.spawn = spawn;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancell) {
		cancelled = cancell;
	}
	
	@Override
	public HandlerList getHandlers() {
		return list;
	}
	
	public static HandlerList getHandlerList() {
		return list;
	}
}
