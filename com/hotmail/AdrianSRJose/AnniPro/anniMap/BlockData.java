package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;

public class BlockData {
	private final   @Getter Location location;
	private @Getter         boolean canBeBreak = true;
	private @Getter @Setter boolean canBePlace = true;
	
	public BlockData(final Location block) {
		this.location = block;
	}
	
	public BlockData setCanBeBreak(boolean canBeBreak) {
		this.canBeBreak = canBeBreak;
		return this;
	}
}
