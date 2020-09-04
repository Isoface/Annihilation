package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;
import com.hotmail.AdrianSRJose.base.DelayUpdate;
import com.hotmail.AdrianSRJose.base.Delays;
import com.hotmail.AdrianSRJose.base.Direction;

public class Scorpio extends ConfigurableKit {
	
	private ItemStack hookItem;
	private String hookItemName;
	private List<UUID> fs;

	@Override
	protected void setUp() {
		hookItem = KitUtils.addClassUndropabbleSoulbound(getDefaultIcon().clone());
		ItemMeta m = hookItem.getItemMeta();
		fs = new ArrayList<>();
		m.setDisplayName(hookItemName);
		hookItem.setItemMeta(m);
		Delays.getInstance().createNewDelay(getInternalName(), new DelayUpdate() {
			@Override
			public void update(Player player, int secondsLeft) {
				// Do nothing
			}
		});
	}

	private boolean isHookItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(hookItemName) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected String getInternalName() {
		return "Scorpio";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.NETHER_STAR.getMaterial ( ) );
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "HookItemName", "Hook");
	}

	@Override
	protected void loadKitStuff(ConfigurationSection section) {
		super.loadKitStuff(section);
		hookItemName = section.getString("HookItemName");
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the hook.", "", aqua + "Use your hook to quickly",
				aqua + "reach allies by pulling", aqua + "yourself to them, or use", aqua + "it on enemies to pull",
				aqua + "the enemy to you."

		);
		return l;
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void specialItemActionCheck(final PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Player player = event.getPlayer();
			final AnniPlayer pl = AnniPlayer.getPlayer(player.getUniqueId());
			//
			if (pl != null && pl.getKit().equals(this)) {
				if (this.isHookItem(player.getItemInHand())) {
					if (!Delays.getInstance().hasActiveDelay(player, this.getInternalName())) {
						Delays.getInstance().addDelay(player, System.currentTimeMillis() + 8000,
								this.getInternalName());// kits.addDelay(player.getName(), DelayType.SCORPIO, 10,
														// TimeUnit.SECONDS);
						Item item = player.getWorld().dropItem(player.getEyeLocation(),
								new ItemStack ( UniversalMaterial.NETHER_STAR.getMaterial ( ) , 1 ) );
						item.setPickupDelay(Integer.MAX_VALUE);
						item.setVelocity(player.getEyeLocation().getDirection().multiply(2));
						Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE,
								new HookTracer(item, pl, 90, this.getName()), 1);
						//
						if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1))
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 2F, 4F);
						else
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("SHOOT_ARROW"), 2F, 4F);
					}
				}
			}
		}
	}

	private class HookTracer implements Runnable {
		private final String scorpioName;
		private final AnniPlayer owner;
		private final Item item;
		private final int maxTicks;

		public HookTracer(Item item, AnniPlayer owner, int maxTicks, String scorpioName) {
			this.item = item;
			this.owner = owner;
			this.maxTicks = maxTicks;
			this.scorpioName = scorpioName;
		}

		@Override
		public void run() {
			// maxTicks--;
			if (maxTicks <= 0 || !owner.getKit().getName().equals(scorpioName)) {
				item.remove();
				return;
			}

			for (Entity entity : item.getNearbyEntities(1, 1, 1)) {
				if (entity.getType() == EntityType.PLAYER) {
					// get player.
					Player target = (Player) entity;
					AnniPlayer p = AnniPlayer.getPlayer(target.getUniqueId());
					
					// check player.
					if (p != null && !p.equals(owner)) {
						Player user = owner.getPlayer();
						if (user != null) {
							if (owner.getTeam() == p.getTeam()) {
								Location loc1 = user.getLocation();
								Location loc2 = target.getLocation();
								if (loc2.getY() >= loc1.getY()) {
									if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1)) {
										target.getWorld().playSound(target.getLocation(), Sound.valueOf("DOOR_OPEN"),
												1F, 0.1F);
										user.getWorld().playSound(user.getLocation(), Sound.valueOf("DOOR_OPEN"), 1F,
												0.1F);
									} else {
										target.getWorld().playSound(target.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN,
												1F, 0.1F);
										user.getWorld().playSound(user.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1F,
												0.1F);
									}
									loc2.setY(loc1.getY());
									Vector vec = loc2.toVector().subtract(loc1.toVector()).setY(.08D).multiply(7);
									user.setVelocity(vec);
								}
							} else {
								if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1)) {
									target.getWorld().playSound(target.getLocation(), Sound.valueOf("DOOR_OPEN"), 1F,
											0.1F);
									user.getWorld().playSound(user.getLocation(), Sound.valueOf("DOOR_OPEN"), 1F, 0.1F);
								} else {
									target.getWorld().playSound(target.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1F,
											0.1F);
									user.getWorld().playSound(user.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1F,
											0.1F);
								}

								// DamageControl.addTempImmunity(target,
								// DamageCause.FALL,System.currentTimeMillis()+8000); //8 second fall immunity

								if (!fs.contains(target.getUniqueId())) {
									fs.add(target.getUniqueId());
								}

								// remove anti fall
								removeAntiFall(target);

								// get user direction.
								Location loc = user.getLocation();
								
								// get teleport location.
								Location tele;
								
								// get direction.
								Direction dec = Direction.getDirection(loc.getDirection());
								if (dec == Direction.North) {
									tele = loc.getBlock().getRelative(BlockFace.NORTH).getLocation();
								} else if (dec == Direction.South) {
									tele = loc.getBlock().getRelative(BlockFace.SOUTH).getLocation();
								} else if (dec == Direction.East) {
									tele = loc.getBlock().getRelative(BlockFace.EAST).getLocation();
								} else if (dec == Direction.West) {
									tele = loc.getBlock().getRelative(BlockFace.WEST).getLocation();
								} else if (dec == Direction.NorthWest) {
									tele = loc.getBlock().getRelative(BlockFace.NORTH_WEST).getLocation();
								} else if (dec == Direction.NorthEast) {
									tele = loc.getBlock().getRelative(BlockFace.NORTH_EAST).getLocation();
								} else if (dec == Direction.SouthEast) {
									tele = loc.getBlock().getRelative(BlockFace.SOUTH_EAST).getLocation();
								} else {
									tele = loc.getBlock().getRelative(BlockFace.SOUTH_WEST).getLocation();
								}
								
								// check is not a wall.
								if (tele.getBlock().getType().isSolid()) {
									tele = loc.getBlock().getRelative(BlockFace.UP).getType().isSolid()
											? loc.getBlock().getRelative(BlockFace.UP).getLocation()
											: loc;
								}
								
								// change pitch and yaw.
								tele.setPitch(0);
								tele.setYaw(loc.getYaw() + 180);
								
								// teleport.
								target.teleport(tele);
							}
						}
						item.remove();
						return;
					}
				}
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE,
					new HookTracer(item, owner, maxTicks - 1, scorpioName), 1);
		}
	}

	private void removeAntiFall(Player p) {
		Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (fs.contains(p.getUniqueId()))
					fs.remove(p.getUniqueId());
			}
		}, 20 * 8);
	}

	@EventHandler
	public void onD(EntityDamageEvent eve) {
		if (Game.isNotRunning())
			return;

		if (eve.getCause() != DamageCause.FALL)
			return;

		if (eve.getEntity() instanceof Player) {
			Player p = (Player) eve.getEntity();
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());

			if (ap != null && ap.getTeam() != null)
				if (fs.contains(p.getUniqueId()))
					eve.setCancelled(true);
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe().addItem(hookItem);
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
