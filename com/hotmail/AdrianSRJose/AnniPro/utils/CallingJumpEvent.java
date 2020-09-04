package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerGetDownFromJumpEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJumpEvent;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class CallingJumpEvent implements Listener {
	private final Map<UUID, Integer> jumps;
	private final Map<UUID, Integer> tasks;

	public CallingJumpEvent(AnnihilationMain main) {
		// Check
		if (main == null) {
			throw new IllegalArgumentException();
		}

		// Set
		Bukkit.getPluginManager().registerEvents(this, main);
		jumps = new HashMap<UUID, Integer>();
		tasks = new HashMap<UUID, Integer>();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		jumps.put(e.getPlayer().getUniqueId(), e.getPlayer().getStatistic(Statistic.JUMP));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		jumps.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(final PlayerMoveEvent e) {
		final Player player = e.getPlayer();
		final UUID id = player.getUniqueId();
		final Location from = e.getFrom();
		final Location to = e.getTo();

		if (e.getFrom().getY() < e.getTo().getY()) {
			int current = player.getStatistic(Statistic.JUMP);
			int last = jumps.getOrDefault(player.getUniqueId(), -1);

			if (last != current) {
				jumps.put(player.getUniqueId(), current);
				double yDif = (long) ((e.getTo().getY() - e.getFrom().getY()) * 1000) / 1000d;

				if ((yDif < 0.035 || yDif > 0.037) && (yDif < 0.116 || yDif > 0.118)) {
					final PlayerJumpEvent jumpEvent = new PlayerJumpEvent(player, from, to);
					AnniEvent.callEvent(jumpEvent);

					// Get PlayerJumpEvent vals
					e.setCancelled(jumpEvent.isCancelled());
					e.setTo(jumpEvent.getTo());
					e.setFrom(jumpEvent.getFrom());

					// Call PlayerGetDownFromJumpEvent
					callGettingDow(id);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void callGettingDow(final UUID id) {
		// Check
		if (id == null) {
			return;
		}

		if (tasks.containsKey(id) || tasks.get(id) != null) {
			return;
		}

		// Start Task
		tasks.put(id, new BukkitRunnable() {
			@Override
			public void run() {
				Player p = Bukkit.getPlayer(id);
				if (p == null || !p.isOnline()) {
					cancel();
					return;
				}

				if (p.isOnGround()) {
					// Get Block
					final Block down1 = p.getLocation().getBlock().getRelative(BlockFace.SELF);
					final Block down = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
					final Block finalBlock = down1.getType() == Material.AIR ? down : down1;
					
					// Call
					AnniEvent.callEvent(new PlayerGetDownFromJumpEvent(p, finalBlock.getLocation(), finalBlock));

					// Remove This Task from map
					tasks.remove(id);

					// Cancell Task
					cancel();
					return;
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L).getTaskId());
	}
}
