package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Spy extends ConfigurableKit {
	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Spy";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.GLASS_BOTTLE.getMaterial ( ) );
	}

	@Override
	protected int setDefaults(ConfigurationSection paramConfigurationSection) {
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the deceiver.!", aqua + "", aqua + "Vanish into thin", aqua + "Air when Still and",
				aqua + "Sneaking!");
		return l;
	}

	@EventHandler
	public void alInfiltrarse(final PlayerToggleSneakEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		final Player p = eve.getPlayer();
		final AnniPlayer pb = AnniPlayer.getPlayer(p.getUniqueId());
		if (KitUtils.isOnLobby(p)) {
			return;
		}

		if (!hasThisKit(pb) || !pb.hasTeam()) {
			return;
		}

		if (!p.isSneaking()) {
			for (int x = 1; x <= 4; x++) {
				p.getLocation().getWorld().playEffect(p.getLocation(), Effect.SMOKE, x);
			}
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, 2));
		} else {
			for (int x = 1; x <= 4; x++) {
				if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
					p.getLocation().getWorld().playEffect(p.getLocation(), Effect.valueOf("CLOUD"), x);
				} else {
					p.getLocation().getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), x);
				}
			}
			
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe();
	}

	@Override
	public void cleanup(Player p) {
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player player, AnniPlayer ap) {
		if (player != null) {
			if (!player.isSneaking()) {
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}
}