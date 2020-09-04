package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public final class PlayerKilledEvent extends Event {
	private static final HandlerList list = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public enum KillAttribute {
		REMEMBRANCE, NEXUSDEFENSE, NEXUSATTACK,
	}

	private AnniPlayer player;
	private AnniPlayer killer;
	private List<ItemStack> drops;
	private boolean dropXP;
	private final List<KillAttribute> attributes;

	public PlayerKilledEvent(AnniPlayer killer, AnniPlayer player, List<ItemStack> drops) {
		this.player = player;
		this.killer = killer;
		this.drops = drops;
		dropXP = false;
		List<KillAttribute> att = new ArrayList<KillAttribute>();
		if (killer.getTeam() != null) {
			if (killer.getTeam().isTeamDead())
				att.add(KillAttribute.REMEMBRANCE);
			else {
				Location killerLoc = killer.getPlayer().getLocation();
				if (killer.getTeam().getNexus().getLocation() != null && killerLoc.getWorld().getName()
						.equalsIgnoreCase(killer.getTeam().getNexus().getLocation().getWorld())) {
					if (killer.getTeam().getNexus().getLocation().toLocation().distanceSquared(killerLoc) <= 20 * 20)
						att.add(KillAttribute.NEXUSDEFENSE);
				}
			}
		}
		this.attributes = Collections.unmodifiableList(att);
	}

	public AnniPlayer getPlayer() {
		return player;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

	public AnniPlayer getKiller() {
		return killer;
	}

	public boolean shouldDropXP() {
		return dropXP;
	}

	public void setShouldDropXP(boolean dropXP) {
		this.dropXP = dropXP;
	}

	public List<KillAttribute> getAttributes() {
		return this.attributes;
	}

	public List<ItemStack> getDrops() {
		return drops;
	}
}
