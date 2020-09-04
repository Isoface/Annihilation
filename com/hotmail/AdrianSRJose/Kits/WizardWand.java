package com.hotmail.AdrianSRJose.Kits;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.Kits.Wizard.WandMode;

public class WizardWand {
	private static final Map<AnniPlayer, WizardWand> wands = new ConcurrentHashMap<AnniPlayer, WizardWand>();
	public AnniPlayer ap;
	public UUID id;
	public WandMode mode;

	public WizardWand(Player owner, WandMode mode) {
		ap = AnniPlayer.getPlayer(owner.getUniqueId());
		id = owner.getUniqueId();
		this.mode = mode;
	}

	public static void CreateWand(AnniPlayer owner, WandMode mode) {
		WizardWand wand = new WizardWand(owner.getPlayer(), mode);
		wands.put(owner, wand);
	}

	public static void RemoveWand(AnniPlayer owner) {
		wands.remove(owner);
	}

	public static WizardWand getWizardWand(Player pl) {
		final AnniPlayer ap = AnniPlayer.getPlayer(pl);
		if (ap == null) {
			return null;
		}
		return wands.get(ap);
	}

	public static WizardWand getWizardWand(AnniPlayer pl) {
		return wands.get(pl);
	}

	public UUID getId() {
		return id;
	}

	public AnniPlayer getOwner() {
		return ap;
	}

	public void setMode(Player inv, WandMode mode) {
		this.mode = mode;
	}

	public WandMode getMode() {
		return mode;
	}

	public void PlayCircleEffect(CompatibleParticles eff, Location l, double range) {
		if (!Util.isValidLoc(l))
			return;
		//
		new BukkitRunnable() {
			double phi = 0;

			@Override
			public void run() {
				Location loc = l.clone();
				//
				phi += Math.PI / 10;
				for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
					double r = 1.5;
					double x = r * Math.cos(theta) * Math.sin(phi);
					double y = r * Math.cos(phi) + 1.5;
					double z = r * Math.sin(theta) * Math.sin(phi);
					loc.add(x, y, z);
					
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						eff.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 1, loc, range);
					} else {
						eff.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 1, loc, range);
					}
					
					loc.subtract(x, y, z);

					if (phi > 2 * Math.PI)
						cancel();
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 1);
	}

	public void CircleEffect(Location l) {
		if (!Util.isValidLoc(l)) {
			return;
		}
		//
		new BukkitRunnable() {
			double phi = 0;

			@Override
			public void run() {
				Location loc = l.clone();
				phi += Math.PI / 10;
				for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
					double r = 1.5;
					double x = r * Math.cos(theta) * Math.sin(phi);
					double y = r * Math.cos(phi) + 1.5;
					double z = r * Math.sin(theta) * Math.sin(phi);
					loc.add(x, y, z);
					
					if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
						CompatibleParticles.ENTITY_EFFECT.displayNewerVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 1, loc, 50.0D);
					} else {
						CompatibleParticles.ENTITY_EFFECT.displayOlderVersions().display(0.0F, 0.0F, 0.0F, 0.1F, 1, loc, 50.0D);
					}
					
					loc.subtract(x, y, z);

					if (phi > 2 * Math.PI) {
						cancel();
					}
				}
			}
		}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 1);
	}
}
