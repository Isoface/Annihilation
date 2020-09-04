package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils.SoulboundType;

public class PlayerDropSoulboudItemEvent extends PlayerDropItemEvent implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancelled;
	private final AnniPlayer player;
	private final SoulboundType type;

	public AnniPlayer getAnniPlayer() {
		return player;
	}

	public SoulboundType getSoulboundType() {
		return type;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean b) {
		cancelled = b;
	}

	public PlayerDropSoulboudItemEvent(Player player, Item soulboudDrop, SoulboundType type) {
		super(player, soulboudDrop);
		AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
		this.player = ap;
		this.type = type;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
