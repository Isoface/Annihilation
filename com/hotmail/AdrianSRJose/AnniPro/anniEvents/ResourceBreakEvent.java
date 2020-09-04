package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import java.util.concurrent.TimeUnit;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.RegeneratingBlock;

import lombok.Getter;
import lombok.Setter;

public class ResourceBreakEvent extends Event implements Cancellable {
	
	private static final HandlerList list = new HandlerList();
	private RegeneratingBlock resource;
	private int xp;
	private ItemStack[] endresult;
	private boolean cancelled;
	private int regenerationTime;
	private TimeUnit regenerationTimeUnit;
	private final Block block;

	private AnniPlayer player;

	public ResourceBreakEvent(AnniPlayer player, RegeneratingBlock resource, Block block, int XP, ItemStack... endResult) {
		this.player = player;
		this.resource = resource;
		xp = XP;
		endresult = endResult;
		regenerationTime = resource.Time;
		regenerationTimeUnit = resource.TimeUnit;
		this.block = block;
	}

	public AnniPlayer getPlayer() {
		return player;
	}
	
	public int getRegenerationTime ( ) {
		return regenerationTime;
	}
	
	public void setRegenerationTime ( int time ) {
		this.regenerationTime = time;
	}
	
	public TimeUnit getRegenerationTimeUnit ( ) {
		return regenerationTimeUnit;
	}
	
	public void setRegenerationTimeUnit ( TimeUnit unit ) {
		this.regenerationTimeUnit = unit;
	}
	
	public Block getBlock ( ) {
		return block;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean b) {
		cancelled = b;
	}
	
	public RegeneratingBlock getResource() {
		return resource;
	}

	public int getXP() {
		return xp;
	}

	public void setXP(int XP) {
		xp = XP;
	}

	public ItemStack[] getProducts() {
		return endresult;
	}

	public void setProducts(ItemStack[] results) {
		endresult = results;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
