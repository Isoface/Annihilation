package com.hotmail.AdrianSRJose.AnniPro.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class Shedulers {
	private static JavaPlugin plugin = AnnihilationMain.INSTANCE;

	public static void doNeverInMainThread(Runnable run) {
		if (!Bukkit.isPrimaryThread()) {
			run.run();
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), run);
	}

	public static void doNeverOutOfMainThread(Runnable run) {
		if (Bukkit.isPrimaryThread()) {
			run.run();
			return;
		}
		Bukkit.getScheduler().runTask(getPlugin(), run);
	}

	public static int scheduleSync(Runnable task, int ticks) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), task, ticks);
	}

	public static int scheduleSync(int ticks, Runnable task) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), task, ticks);
	}

	public static void scheduleAsync(Runnable task, int ticks) {
		if (!plugin.isEnabled()) {
			task.run();
		} else {
			Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), task, ticks);
		}
	}

	public static void scheduleAsync(int ticks, Runnable task) {
		if (!plugin.isEnabled()) {
			task.run();
		} else {
			Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), task, ticks);
		}
	}

	public static void asyncRepeating(Runnable runnable, int ticks) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, ticks, ticks);
	}

	public static void asyncRepeating(int ticks, Runnable task) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), task, ticks, ticks);
	}

	public static BukkitTask syncRepeating(Runnable runnable, int ticks) {
		return Bukkit.getScheduler().runTaskTimer(getPlugin(), runnable, ticks, ticks);
	}

	public static BukkitTask syncRepeating(int ticks, Runnable task) {
		return syncRepeating(task, ticks);
	}

	public static void async(Runnable runnable) {
		if (!plugin.isEnabled()) {
			runnable.run();
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), runnable);
		}
	}

	public static void cancel(int taskId) {
		Bukkit.getScheduler().cancelTask(taskId);
	}

	private static JavaPlugin getPlugin() {
		return plugin;
	}
}
