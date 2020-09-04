package com.hotmail.AdrianSRJose.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public abstract class AnniPlayerEvent extends Event {
	protected AnniPlayer player;

	public AnniPlayerEvent(AnniPlayer who) {
		player = who;
	}

	AnniPlayerEvent(AnniPlayer who, boolean async) {
		super(async);
		player = who;
	}

	public final AnniPlayer getPlayer() {
		return player;
	}

	public final Player getBukkitPlayer() {
		return player != null ? player.getPlayer() : null;
	}
}
