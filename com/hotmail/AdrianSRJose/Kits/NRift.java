package com.hotmail.AdrianSRJose.Kits;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;

public class NRift implements Runnable {
	
	private final UUID player;
	private final AnniTeam target;
	private final int range = 3;
	private final Location location;
	private int warmUp = 10;
	private boolean cancelled = false;
	private PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 1);

	public NRift(Player player, AnniTeam target) {
		this.location = player.getLocation();
		this.player = player.getUniqueId();
		this.target = target;
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, this, 1);
	}

	@Override
	public void run() {
		if (cancelled) {
			return;
		}

		try {
			if (!Bukkit.getPlayer(player).isOnline() || Bukkit.getPlayer(player).isDead()) {
				cancel(RiftWalker.leftRiftMessage);
			}
		}
		catch(Throwable t) {
			cancel(RiftWalker.leftRiftMessage);
		}
		
		if (warmUp > 0) {
			for (double x = -range; x <= range; x++)
				for (double y = 0; y <= range; y += .3)
					for (double z = -range; z <= range; z++) {
						final double total = Math.abs(x) + Math.abs(y) + Math.abs(z);
						if (total > range * 2 && total < range * 3) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.HAPPY_VILLAGER.displayNewerVersions().display(.01f, .01f, .01f,
										.01f, 1, location.clone().add(x, y, z), 999999);
							} else {
								CompatibleParticles.HAPPY_VILLAGER.displayOlderVersions().display(.01f, .01f, .01f,
										.01f, 1, location.clone().add(x, y, z), 999999);
							}
						}
					}

			Player player = Bukkit.getPlayer(this.player);
			AnniTeam plTeam = AnniPlayer.getPlayer(player).getTeam();
			String name = target.equals(plTeam) ? target.getColor() + RiftWalker.Base : target.getColoredName();

			final List<Entity> entities = player.getNearbyEntities(range * 2, 2, range * 2);
			
			
//			player.sendMessage(ChatColor.GOLD + "Rift to " + name + ChatColor.GOLD + " opens in " + warmUp);
			player.sendMessage(RiftWalker.openingRift.replace("%w", target.getExternalColoredName()).replace("%#", String.valueOf(warmUp)));
			
			for (Entity entity : entities) {
				AnniPlayer user = AnniPlayer.getPlayer(player.getUniqueId());
				AnniPlayer team = AnniPlayer.getPlayer(entity.getUniqueId());
				if (entity instanceof Player && contains(entity.getLocation())
						&& user.getTeam().equals(team.getTeam())) {
					Player other = (Player) entity;
//					other.sendMessage(ChatColor.GOLD + "Rift to " + name + ChatColor.GOLD + " opens in " + warmUp
//							+ ", hold shift to travel in this rift!");
					
					other.sendMessage(RiftWalker.openingRift.replace("%w", target.getExternalColoredName()).replace("%#", String.valueOf(warmUp)) + RiftWalker.openingRiftOther);
				}
			}
			warmUp--;
			Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, this, 20);
		} else
			teleport();
	}

	private void teleport() {
		Player player = Bukkit.getPlayer(this.player);

		int count = 0;
		final List<Entity> entities = player.getNearbyEntities(range * 2, 2, range * 2);
		for (Entity entity : entities) {
			AnniPlayer user = AnniPlayer.getPlayer(player.getUniqueId());
			AnniPlayer team = AnniPlayer.getPlayer(entity.getUniqueId());
			if (entity instanceof Player && contains(entity.getLocation()) && user.getTeam().equals(team.getTeam())) {
				Player other = (Player) entity;

				if (other.isSneaking()) {
					// Do Rift
//					if (count < 2) {
						// Telerport and Add potis
						weakness.apply(other);
						other.sendMessage(RiftWalker.youAreWeak);
						other.teleport(target.getRandomSpawn());
						count++;
//					} else
//						other.sendMessage(
//								ChatColor.RED + "The rift is not strong enough to send more than three players.");
				} else
					other.sendMessage(RiftWalker.noSneaking);
			}
		}

		player.teleport(target.getRandomSpawn());
		weakness.apply(player);
		player.sendMessage(RiftWalker.youAreWeak);
		Kit pkit = AnniPlayer.getPlayer(player).getKit();
		if (pkit instanceof RiftWalker) {
			RiftWalker rfk = (RiftWalker) pkit;
			rfk.doDelay(player);
		}

		player.removeMetadata("rift", AnnihilationMain.INSTANCE);
	}

	public void cancel(String message) {
		Player player = Bukkit.getPlayer(this.player);
		if (player != null) {
			player.removeMetadata("rift", AnnihilationMain.INSTANCE);
			player.sendMessage(ChatColor.RED + message);
		}
		cancelled = true;
	}

	public static NRift fromPlayer(Player player) {
		return (NRift) player.getMetadata("rift").get(0).value();
	}

	public Location getLocation() {
		return location;
	}

	public boolean contains(Location location) {
		return location.distanceSquared(getLocation()) < range * range;
	}
}
