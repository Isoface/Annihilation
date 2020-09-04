package com.hotmail.AdrianSRJose.AnniPro.announcementBar;

public final class Announcement {
	private String message;
	private Runnable callBack;
	private int time;
	private boolean permanent;
	private String bossBarMessage = "";

	public Announcement(String message) {
		this.message = message;
		permanent = true;
		callBack = null;
	}

	public Announcement(String message, String bossBarMessage) {
		this.message = message;
		permanent = true;
		callBack = null;
		this.bossBarMessage = bossBarMessage;
	}

	public String getMessage() {
		return message;
	}

	public String getBossBarMessage() {
		return bossBarMessage;
	}

	public Announcement setMessage(String str) {
		message = str;
		return this;
	}

	public Announcement setBossBarMessage(String str) {
		bossBarMessage = str;
		BossBarHandler.setMessage(str);
		return this;
	}

	public Announcement setProgress(double newProgress) {
		BossBarHandler.setProgress(newProgress);
		return this;
	}

	public Announcement setTime(int time)//
	{
		this.time = time;
		if (this.time > 0)
			permanent = false;
		return this;
	}

	public Announcement setPermanent(boolean permanent) {
		this.permanent = permanent;
		if (permanent)
			time = 0;

		return this;
	}

	public Announcement setCallback(Runnable callBack) {
		this.callBack = callBack;
		return this;
	}

	public Runnable getCallBack() {
		return callBack;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public int getTime() {
		return time;
	}

	public void destroy() {
		message = null;
		callBack = null;
	}
}
