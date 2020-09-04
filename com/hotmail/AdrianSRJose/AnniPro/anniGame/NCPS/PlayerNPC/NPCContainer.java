package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.PlayerNPC;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class NPCContainer 
{
	private final @Getter UUID ownerID;
	private @Getter @Setter ItemStack itemInHand = null;
	private @Getter @Setter ItemStack[] armor = null;
	private @Getter @Setter ItemStack[] InventoryContents = null;
	
	public NPCContainer(final UUID ownerID) {
		this.ownerID = ownerID;
	}
}
