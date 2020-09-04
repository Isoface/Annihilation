package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;
import com.hotmail.AdrianSRJose.base.DelayUpdate;
import com.hotmail.AdrianSRJose.base.Delays;

import net.md_5.bungee.api.ChatColor;

public class Electrizador extends ConfigurableKit {
	private ItemStack NieveItem;
	private String NieveItemName;

	@Override
	protected void setUp() {
		NieveItem = KitUtils.addSoulbound(getDefaultIcon().clone());
		NieveItemName = ChatColor.AQUA + "SuperBall";
		ItemMeta m = NieveItem.getItemMeta();
		if (m == null)
			m = Bukkit.getItemFactory().getItemMeta(NieveItem.getType());
		m.setDisplayName(NieveItemName);
		NieveItem.setItemMeta(m);
		//
		Delays.getInstance().createNewDelay(getInternalName(), new DelayUpdate() {
			@Override
			public void update(Player player, int secondsLeft) {
			}
		});
	}

	private boolean isNieveItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(NieveItemName) && KitUtils.isSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected String getInternalName() {
		return "Electrizer";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.SNOWBALL.getMaterial ( ) );
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();

		addToList(l, aqua + "You are the Electrizer", aqua + "", aqua + "With your super SnowBall,",
				aqua + "shoot your enemies", aqua + "and give them an electric", aqua + "shock terrorize ",
				aqua + "frauds");
		return l;
	}

	@EventHandler
	public void a(PlayerInteractEvent eve) {
		if (isNieveItem(eve.getItem()))
			eve.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void eshoot(PlayerInteractEvent eve) {
		Player p = eve.getPlayer();
		//
		if (!KitUtils.isValidPlayer(p))
			return;
		//
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		if (ap != null && ap.getTeam() != null && ap.getKit().equals(this)) {
			if (this.isNieveItem(eve.getItem())) {
				eve.setCancelled(true);
				//
				if (eve.getAction() == Action.RIGHT_CLICK_AIR || eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
					p.setItemInHand(NieveItem);
					eve.setCancelled(true);
					//
					if (Delays.getInstance().hasActiveDelay(p, getInternalName())) {
						p.sendMessage(ChatColor.RED + "Wait a few seconds to reuse");
						return;
					}
					//
					Delays.getInstance().addDelay(p, System.currentTimeMillis() + 10000, this.getInternalName());
					Item b = Game.getGameMap().getWorld().dropItem(p.getEyeLocation(), NieveItem);
					b.setPickupDelay(Integer.MAX_VALUE);
					b.setVelocity(p.getEyeLocation().getDirection().multiply(2));
					//
					Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
						@Override
						public void run() {
							for (Entity entity : b.getNearbyEntities(2, 2, 2)) {
								if (entity != null && entity instanceof Player) {
									if (AnniPlayer.getPlayer(((Player) entity)) == null 
											|| AnniPlayer.getPlayer(((Player) entity).getUniqueId()).getTeam().equals(ap.getTeam()))
										return;
									//
									Player target = (Player) entity;
									//
									target.getLocation().getWorld().strikeLightning(target.getLocation());
									target.getLocation().getWorld().strikeLightningEffect(target.getLocation());
									
									if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
										target.getLocation().getWorld().playEffect(target.getLocation(),
												Effect.valueOf("EXPLOSION_LARGE"), 1, 200);
										target.getLocation().getWorld().playEffect(target.getLocation(),
												Effect.valueOf("FIREWORKS_SPARK"), 1, 200);
										target.getLocation().getWorld().playEffect(target.getLocation(), Effect.valueOf("LARGE_SMOKE"),
												1, 200);
										target.getLocation().getWorld().playEffect(target.getLocation(), Effect.valueOf("TILE_BREAK"),
												UniversalMaterial.GLASS.getMaterial ( ).getId ( ), 200);
									}
									else {
										target.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, target.getLocation(), 1);
										target.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, target.getLocation(), 1);
										target.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, target.getLocation(), 1);
										target.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, target.getLocation(), 1);
									}
									
									target.damage(6.0D, p);
								}
							}
						}
					}, 10);

					new BukkitRunnable() {
						@Override
						public void run() {
							if (!b.isDead() && !b.isOnGround()) {
								
								if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
									b.getLocation().getWorld().playEffect(b.getLocation(), Effect.valueOf("SNOWBALL_BREAK"), 1, 600);
									b.getLocation().getWorld().playEffect(b.getLocation(), Effect.valueOf("FIREWORKS_SPARK"), 1, 600);
									b.getLocation().getWorld().playEffect(b.getLocation(), Effect.valueOf("CRIT"), 1, 600);
								} else {
									b.getLocation().getWorld().spawnParticle(Particle.SNOWBALL, b.getLocation(), 1);
									b.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation(), 1);
									b.getLocation().getWorld().spawnParticle(Particle.CRIT, b.getLocation(), 1);
								}
								
							} else {
								
								if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
									b.getLocation().getWorld().playEffect(b.getLocation(), Effect.valueOf("TILE_BREAK"),
											UniversalMaterial.GLASS.getMaterial ( ).getId ( ), 600);
								}
								
								cancel();
							}
						}
					}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);
				}
			}
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(NieveItem);
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}
}