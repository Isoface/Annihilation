package com.hotmail.AdrianSRJose.AnniPro.anniEvents;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

/**
 * Called when a player respawns.
 */
public class AnniPlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private Location respawnLocation;

    public AnniPlayerRespawnEvent(final Player respawnPlayer, final Location respawnLocation) {
        super(respawnPlayer);
        this.respawnLocation = respawnLocation;
    }
    
    public AnniPlayer getAnniPlayer() {
    	return AnniPlayer.getPlayer(player);
    }

    /**
     * Gets the current respawn location
     *
     * @return Location current respawn location
     */
    public Location getRespawnLocation() {
        return this.respawnLocation;
    }

    /**
     * Sets the new respawn location
     *
     * @param respawnLocation new location for the respawn
     */
    public void setRespawnLocation(Location respawnLocation) {
        Validate.notNull(respawnLocation, "Respawn location can not be null");
        Validate.notNull(respawnLocation.getWorld(), "Respawn world can not be null");

        this.respawnLocation = respawnLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
