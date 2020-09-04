package com.hotmail.AdrianSRJose.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

/**
 * @author AdrianSR 12:41:55 p. m./ 2017
 */
public class Alien_TeleportEnemyEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList h = new HandlerList();
	private boolean isCancelled;
	private final AnniPlayer enemy;
	private final Location from;
	private Location to;

	public Alien_TeleportEnemyEvent(AnniPlayer who, AnniPlayer enemy, Location from, Location to) {
		super(who);
		this.enemy = enemy;
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return h;
	}

	public AnniPlayer getTeleportedEnemy() {
		return enemy;
	}

	public Player getBukkitTeleportedEnemyPlayer() {
		return enemy != null ? enemy.getPlayer() : null;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

	// ---- Voids

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	public void setTo(Location l) {
		if (l == null || l.getWorld() == null)
			throw new NullPointerException("Invalid Location");
		to = l;
	}

	public static HandlerList getHandlerList() {
		return h;
	}
}
