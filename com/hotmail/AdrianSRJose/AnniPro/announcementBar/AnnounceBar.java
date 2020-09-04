package com.hotmail.AdrianSRJose.AnniPro.announcementBar;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PhaseChangeEvent;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;

import lombok.Getter;
import lombok.Setter;

public class AnnounceBar implements Listener
{
	private static AnnounceBar instance;
	private Bar bar;
	private Announcement announcement;
	private @Getter @Setter Integer   timer;
	private @Getter @Setter long   timeLeft;
	private @Getter @Setter long lastUpdate;
	private String sec;
	private String bss;
//	private @Setter boolean bossBarEnabled = true;
//	private @Setter boolean phaseBarEnabled = true;
	
	public static AnnounceBar getInstance() {
		if (instance == null) {
			instance = new AnnounceBar();
		}
		return instance;
	}
	
	public Announcement getAnnouncement() {
		return announcement;
	}

	public String getAnnoun() {
		return sec;
	}

	public String getBossAnnoun() {
		return bss;
	}

	private AnnounceBar() {
		try {
			String           version = ServerVersion.getVersion().name();
			String              name = "com.hotmail.AdrianSRJose.AnniPro.announcementBar.versions." + version + ".Bar";
			Class<?>              cl = Class.forName(name);
			Class<? extends Bar> bar = cl.asSubclass(Bar.class);
			Bar manager = bar.newInstance();
			this.bar = manager;
			Bukkit.getPluginManager().registerEvents(this, AnnihilationMain.INSTANCE);
		}
		catch(InstantiationException e) {
			bar = new FakeBar();
			try {
				throw new InstantiationException(e.getMessage());
			} catch (InstantiationException e1) {
//				e1.printStackTrace();
			} 
		}
		catch(IllegalAccessException ee) {
			bar = new FakeBar();
			try {
				throw new IllegalAccessException(ee.getMessage());
			} catch (IllegalAccessException e) {
//				e.printStackTrace();
			}
		}
		catch(ClassNotFoundException eee) {
			bar = new FakeBar();
			try {
				throw new ClassNotFoundException(eee.getMessage());
			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
			}
		}
	}
	
	// Cancell Announcements
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPhaseStart(final PhaseChangeEvent eve) {
		// Get new phase
		final int phase = eve.getNewPhase();
		if (phase != 5 || bar == null) {
			return;
		}
		
		// Cancell Announcement
		if (Config.BOSS_BAR_REMOVE_IN_PHASE_5.toBoolean() && Config.PHASE_BAR_REMOVE_IN_PHASE_5.toBoolean()) {
			// Cancell Updater
			cancelUpdater();
			
			// Remove Bars
			for (Player player : Bukkit.getOnlinePlayers()) {
				bar.removeBossBar(player);
			}
			
			// Destroy Bar
			BossBarHandler.destroyBar();
			return;
		}
		
		// Destroy Boss Bar
		if (Config.BOSS_BAR_REMOVE_IN_PHASE_5.toBoolean()) {
			bar.destroyBossBar();
		}
		
		// Destroy Phase Bar
		if (Config.PHASE_BAR_REMOVE_IN_PHASE_5.toBoolean()) {
			bar.destroyPhaseBar();
		}
	}

	public TempData getData() {
		TempData d = new TempData();
		d.announcement = announcement;
		d.timeLeft = timeLeft;
		return d;
	}

	public void countDown(TempData d) {
		announcement = d.announcement;
		timeLeft = d.timeLeft;
		lastUpdate = System.currentTimeMillis();
		scheduleUpdater();
	}

	public void countDown(Announcement announcement) {
		this.announcement = announcement;
		timeLeft = announcement.getTime() * 1000;
		lastUpdate = System.currentTimeMillis();
		String mess = ChatColor.translateAlternateColorCodes('&', announcement.getMessage()).replace("{#}",
				format(timeLeft));
		String bmess = announcement.getBossBarMessage();

		if (bmess != null && !bmess.equals(""))
			bmess = ChatColor.translateAlternateColorCodes('&', announcement.getBossBarMessage()).replace("{#}",
					format(timeLeft));
		else
			bmess = null;
		//
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (KitUtils.isValidPlayer(pl)) {
				bar.sendToPlayer(pl, mess, bmess, 100);
			}
		}
		//
		scheduleUpdater();
	}

	private void scheduleUpdater() {
		// cancel updater.
		cancelUpdater();
		
		// start timer.
		timer = Bukkit.getScheduler().runTaskTimer(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				// get message.s
				String m = ChatColor.translateAlternateColorCodes('&', announcement.getMessage());
				String bm = announcement.getBossBarMessage();

				// check boss bar messge.
				if (bm != null && !bm.equals("")) {
					bm = ChatColor.translateAlternateColorCodes('&', announcement.getBossBarMessage());
				} 
				else {
					bm = null;
				}
					
				// check is permanent.
				if (announcement.isPermanent()) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (KitUtils.isValidPlayer(player)) {
							bar.sendToPlayer(player, m, bm, 100);
						}
					}
					return;
				}

				// update time left, and last update.
				timeLeft -= (System.currentTimeMillis() - lastUpdate);
				lastUpdate = System.currentTimeMillis();
				
				// check time left.
				if (timeLeft <= 100) {
					// restart timeLeft
					timeLeft = 0;
					
					// cancel updater.
					cancelUpdater();
					
					// run call back runnable.
					if (announcement.getCallBack() != null) {
						announcement.getCallBack().run();
					}
				} 
				else {
					// get boss bar message.
					String BossBarMess = bm;
					if (bm != null && !bm.equals("")) {
						BossBarMess = bm.replace("{#}", format(timeLeft));
					}
					else {
						BossBarMess = null;
					}

					// get bar message and parcent
					String mess   = m.replace("{#}", format(timeLeft));
					float percent = (timeLeft / 1000) / ((float) announcement.getTime());

					for (Player player : Bukkit.getOnlinePlayers()) {
						if (KitUtils.isValidPlayer(player)) {
							bar.sendToPlayer(player, mess, BossBarMess, percent);
						}
					}
					
					// save messages.
					sec = mess;
					bss = BossBarMess;
				}
			}
		}, 20L, 20L).getTaskId();
	}

	private void cancelUpdater() {
		if (timer != null) {
			Bukkit.getScheduler().cancelTask(timer);
			timer = null;
		}
	}

	public static String format(long miliseconds) {
		return DurationFormatUtils.formatDuration(miliseconds, "mm:ss");
	}

	public Bar getBar() {
		return bar;
	}
}
