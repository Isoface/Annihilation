package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class NPCDeathEvent extends Event
{
	private static final HandlerList list = new HandlerList();
	private final @Getter LivingEntity   NCP;
	private final @Getter Player         Killer;
	private @Getter @Setter List<ItemStack> drops;
	
	public NPCDeathEvent(final LivingEntity npc, final Player killer, final List<ItemStack> drops) {
		this.NCP    = npc;
		this.Killer = killer;
		this.drops  = drops;
	}
	
	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
