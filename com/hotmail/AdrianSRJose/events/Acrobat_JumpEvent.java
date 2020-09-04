package com.hotmail.AdrianSRJose.events;

import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public class Acrobat_JumpEvent extends AnniPlayerEvent implements Cancellable {
	private static final HandlerList h = new HandlerList();
	private boolean isCancelled;

	private boolean sendReuseMessage = true;
	private double height = 1.6;
	private String message = ChatColor.DARK_GRAY + "Now You Can reuse the Acrobat " + ChatColor.GOLD + "§lJump";

	public Acrobat_JumpEvent(AnniPlayer who) {
		super(who);
	}

	public Acrobat_JumpEvent(AnniPlayer who, double MultiplicatedHeight) {
		super(who);
		this.height = MultiplicatedHeight;
	}

	public boolean getUseReuseMessage() {
		return sendReuseMessage;
	}

	public double getHeight() {
		return height;
	}

	public String getReuseMessage() {
		return message;
	}

	// ----Voids
	public void setSendReuseMessage(boolean b) {
		sendReuseMessage = b;
	}

	public void setSetReuseMessage(String newMessage) {
		message = newMessage != null ? newMessage : "";
	}

	public void setMultiplicatedHeight(double h) {
		height = h;
	}

	@Override
	public HandlerList getHandlers() {
		return h;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	protected static HandlerList getHandlerList() {
		return h;
	}
}
