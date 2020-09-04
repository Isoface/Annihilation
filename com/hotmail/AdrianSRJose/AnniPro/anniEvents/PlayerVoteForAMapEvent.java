package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class PlayerVoteForAMapEvent extends Event implements Cancellable {
	private static final HandlerList list = new HandlerList();
	private boolean cancel;
	private final AnniPlayer player;
	private final String NewMapName;
	private final String OldMapName;

	public PlayerVoteForAMapEvent(AnniPlayer ap, String oldMapName, String newMapName) {
		player = ap;
		NewMapName = newMapName;
		OldMapName = oldMapName;
	}

	public AnniPlayer getPlayer() {
		return player;
	}

	public Player getBukkitPlayer() {
		return player.getPlayer();
	}

	public String getNewVotedMapName() {
		return NewMapName;
	}

	/**
	 * Warning: This Value can be a null Value
	 */
	public String getOldVotedMapName() {
		return OldMapName;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean b) {
		cancel = b;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}
}
