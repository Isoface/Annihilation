package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Scout extends ConfigurableKit {
	
	private ItemStack grapple;
	private String grappleName;
	private boolean allowGrappleDamage = true;
	private boolean CanBeUsedInWater   = false;
	private double lifeDivider         = 2;
	private double xMultipler          = 0.22D; // 0.01D
	private double yMultipler          = 0.5D;// 0.20D
	private boolean infiniteGrapple    = true;
	private Set<String> grapples       = new HashSet<String>();
	private String grappleDetached     = ChatColor.GOLD + "Grapple detached";
	private String delayMessge         = ChatColor.RED + "&cYou must wait &6%# &cseconds before use this again!";
	private int grappleDetachedDelay   = 0;
	private int grappleDelay           = 0;
//	private static 

	@Override
	protected void loadKitStuff(ConfigurationSection section) {
		if (section != null) {
			super.loadKitStuff(section);
			
			if (section.isString("GrappleItemName")) {
				grappleName = Util.wc(section.getString("GrappleItemName"));
			}
			
			if (section.isBoolean("AllowGrappleDamage")) {
				allowGrappleDamage = section.getBoolean("AllowGrappleDamage");
			}

			delayMessge     = Util.wc(section.getString("GrappleDelayMessage", delayMessge));
			grappleDelay    = section.getInt("GrappleDelay");
			grappleDetached = Util.wc(section.getString("GrappleDetached", grappleDetached));
			grappleDetachedDelay = Math.max(section.getInt("GrappleDetachedMessageDelaySeconds", grappleDetachedDelay), 0);
			infiniteGrapple = section.getBoolean("InfiniteGrapple", infiniteGrapple);
			yMultipler = section.getDouble("Multipler-Y", yMultipler);
			xMultipler = section.getDouble("Multipler-X", xMultipler);
			lifeDivider = section.getDouble("lifeDivider", 2.0);
			CanBeUsedInWater = section.getBoolean("CanBeUsedInWater");
		}
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		int save = 0;
		//
		save += Util.setDefaultIfNotSet(section, "GrappleItemName", "&6Grapple");
		save += Util.setDefaultIfNotSet(section, "GrappleDelay", 0);
		save += Util.setDefaultIfNotSet(section, "GrappleDelayMessage", Util.untranslateAlternateColorCodes(delayMessge));
		save += Util.setDefaultIfNotSet(section, "GrappleDetached", Util.untranslateAlternateColorCodes(grappleDetached));
		save += Util.setDefaultIfNotSet(section, "GrappleDetachedMessageDelaySeconds", 0);
		save += Util.setDefaultIfNotSet(section, "AllowGrappleDamage", true);
		save += Util.setDefaultIfNotSet(section, "InfiniteGrapple", true);
		save += Util.setDefaultIfNotSet(section, "CanBeUsedInWater", false);
		save += Util.setDefaultIfNotSet(section, "DivideDamageWith", 2.0);
		save += Util.setDefaultIfNotSet(section, "Multipler-X", 0.22D);
		save += Util.setDefaultIfNotSet(section, "Multipler-Y", 0.5D);
		return save;
	}
	
	@Override
	protected void setUp() {
		grapple = KitUtils.addClassUndropabbleSoulbound(getDefaultIcon().clone());
		ItemMeta m = grapple.getItemMeta();
		m.setDisplayName(grappleName);
		grapple.setItemMeta(m);
	}

	@Override
	protected String getInternalName() {
		return "Scout";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.FISHING_ROD.getMaterial());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamageByHook(EntityDamageByEntityEvent eve) {
		if (!allowGrappleDamage) {
			final Entity d = eve.getDamager();
			if (d != null && d instanceof FishHook) {
				final FishHook f = (FishHook) d;
				final ProjectileSource ps = f.getShooter();
				if (ps != null && ps instanceof Player) {
					final AnniPlayer ap = AnniPlayer.getPlayer(((Player) ps).getUniqueId());
					if (ap != null && ap.isOnline() && ap.hasTeam() && ap.getKit().equals(this)) {
						if (isGrappleItem(ap.getPlayer().getItemInHand())) {
							eve.setCancelled(true);
						}
					}
				}
			}
		}
	}

	private boolean isGrappleItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(grappleName) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the feet.", "", aqua + "Use your permanent", aqua + "speed boost to maneuver",
				aqua + "around the battlefield,", aqua + "and your grapple to", aqua + "ascend to new heights",
				aqua + "and gain perspective on the", aqua + "battlefield!");
		return l;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void fallDamage(EntityDamageEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == DamageCause.FALL) {
			final Player p = (Player) event.getEntity();
			final AnniPlayer pl = AnniPlayer.getPlayer(p.getUniqueId());
			if (pl != null && pl.getKit().equals(this)) {
				event.setDamage(event.getDamage() / lifeDivider);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onToggle(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
		if (isGrappleItem(e.getItem()) && ap.getKit().equals(this)
				&& (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			e.setCancelled(true);
			toggleGrapple(player);
			player.updateInventory();
		}
	}

	private void toggleGrapple(Player player) {
		String name = player.getName();
		if (!this.grapples.contains(name)) {
			this.grapples.add(name);
		} else {
			this.grapples.remove(name);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void grappler(PlayerFishEvent event) {
		try {
			// get and check player
			final Player player = event.getPlayer();
			final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
			if (!hasThisKit(p)) {
				return;
			}
			
			// check state
			if (event.getState() == State.FISHING) {
				return;
			}
			
			// check item in hand
			if (!isGrappleItem(player.getItemInHand())) {
				return;
			}
			
			// get and check state
			final PlayerFishEvent.State state = event.getState();
			if (!(state == PlayerFishEvent.State.IN_GROUND || state == PlayerFishEvent.State.FAILED_ATTEMPT)) {
				return;
			}
			
			// getn boober and player locs
//			final Entity      bobber = (Entity) event.getClass().getMethod("getHook").invoke(event);
			final Entity      bobber = event.getHook ( );
			final Location bobberLoc = bobber.getLocation ( );
			final Location playerLoc = player.getLocation ( );

			// Check Worlds
			if (KitUtils.isOnGameMap(player)) {
				if (!KitUtils.isGameMap(bobberLoc.getWorld())) {
					return;
				}
			} else if (KitUtils.isOnBossMap(player)) {
				if (!KitUtils.isBossMap(bobberLoc.getWorld())) {
					return;
				}
			}

			// set infinite
			if (infiniteGrapple) {
				player.getItemInHand().setDurability((short) 0);
			}
			
			// check delay
			final long curr = System.currentTimeMillis();
			if (grappleDelay > 0 && p.getData("scout-last-grapple-use-scout") instanceof Long) {
				final long last = ((Long) p.getData("scout-last-grapple-use-scout")).longValue();
				final long tota = (curr - last);
				if (tota < (this.grappleDelay * 1000)) {
					player.sendMessage(delayMessge.replace("%#", String.valueOf((int) (tota / 1000))));
					return;
				}
			}
			
			org.bukkit.Material type = bobberLoc.getBlock().getRelative(BlockFace.DOWN).getType();
			if (type == null) {
				return;
			} else if (!type.isSolid() && !isWater(type)) {
				return;
			} else if (isWater(type)) {
				if (!CanBeUsedInWater) {
					return;
				}
			}
			
			// punch player to bobber
			if (grapples.contains(player.getName())) {
				final boolean bobberArriba = ((int) bobberLoc.getY()) > ((int) playerLoc.getY());
				double x = bobberLoc.getX() - playerLoc.getX();
				double y = bobberLoc.getY() - playerLoc.getY();
				double z = bobberLoc.getZ() - playerLoc.getZ();
				Vector vec = new Vector(x, y, z).multiply(xMultipler);

				if (bobberArriba) {
					if (playerLoc.distance(bobberLoc) < 1.0D) {
						pullPlayerSlightly(player, bobberLoc);
					} else {
						pullEntityToLocation(player, bobberLoc);
					}
				} else {
					player.setVelocity(playerLoc.getDirection().setY(yMultipler).add(vec));
				}
			} else {
				Location playerloc = player.getLocation();
				Location loc = bobber.getLocation();
				if (playerloc.distance(loc) < 1.0D) {
					pullPlayerSlightly(player, loc);
				} else {
					pullEntityToLocation(player, loc);
				}
			}

			
			// play sound
			player.playSound(player.getLocation(), UniversalSound.ZOMBIE_INFECT.asBukkit(), 4.0F, 4.0F);
			
			// save data
			p.setData("scout-last-grapple-use-scout", Long.valueOf(curr));
		
			// update player inventory
			player.updateInventory();
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	@EventHandler
	public void onChangeItem(PlayerItemHeldEvent e) {
		final AnniPlayer ap = AnniPlayer.getPlayer(e.getPlayer().getUniqueId());
		final ItemStack item = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
		if (ap != null && this.isGrappleItem(item) && ap.getKit().equals(this)) {
			// check data
			if (ap.getData("scout-last-grapple-detached-scout") instanceof Number) {
				if (grappleDetachedDelay > 0) {
					final long curr = System.currentTimeMillis();
					final long last = (long) ap.getData("scout-last-grapple-detached-scout");
					final long tota = (curr - last);
					if (tota < (this.grappleDetachedDelay * 1000)) {
						return;
					}
				}
			}

			// play sound
			e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(),
					(ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)
							? (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1) ? Sound.valueOf("BLOCK_NOTE_HAT")
									: Sound.valueOf ( "BLOCK_NOTE_BLOCK_HAT" ))
							: Sound.valueOf("NOTE_STICKS")),
					4.0F, 0.7F);

			// send message
			e.getPlayer().sendMessage(grappleDetached);
			
			// set data
			ap.setData("scout-last-grapple-detached-scout", System.currentTimeMillis());
		}
	}

	private boolean isWater(org.bukkit.Material m) {
		return m != null ? ( m.name().equals("WATER") || m.name().contains("STATIONARY_WATER") ) : false;
	}

	private void pullEntityToLocation(Entity e, Location loc) {
		Location entityLoc = e.getLocation();

		entityLoc.setY(entityLoc.getY() + 0.8D);
		e.teleport(entityLoc);

		double g = -0.08D;
		double d = loc.distance(entityLoc);
		double t = d;
		double v_x = (1.0D + 0.07D * t) * (loc.getX() - entityLoc.getX()) / t;
		double v_y = (1.0D + 0.03D * t) * (loc.getY() - entityLoc.getY()) / t - 0.5D * g * t;
		double v_z = (1.0D + 0.07D * t) * (loc.getZ() - entityLoc.getZ()) / t;

		Vector v = e.getVelocity();
		v.setX(v_x);
		v.setY(v_y);
		v.setZ(v_z);
		e.setVelocity(v);
	}

	private void pullPlayerSlightly(Player p, Location loc) {
		if (loc.getY() > p.getLocation().getY()) {
			p.setVelocity(new Vector(0.0D, 0.25D, 0.0D));
			return;
		}

		Location playerLoc = p.getLocation();
		Vector vector = loc.toVector().subtract(playerLoc.toVector());
		
		
		p.setVelocity(vector);
	}

	// Dont Delete!!
	// protected void pullEntityToLocation(Entity e, Location loc)
	// {
	// Location entityLoc = e.getLocation();
	//
	// entityLoc.setY(entityLoc.getY() + 0.5D);
	// e.teleport(entityLoc);
	//
	// double g = -0.08D;
	// double d = loc.distance(entityLoc);
	// double t = d;
	// double v_x = (1.0D + 0.07000000000000001D * t)
	// * (loc.getX() - entityLoc.getX()) / t;
	// double v_y = (1.0D + 0.03D * t) * (loc.getY() - entityLoc.getY()) / t
	// - 0.5D * g * t;
	// double v_z = (1.0D + 0.07000000000000001D * t)
	// * (loc.getZ() - entityLoc.getZ()) / t;
	//
	// Vector v = e.getVelocity();
	// v.setX(v_x);
	// v.setY(v_y);
	// v.setZ(v_z);
	// e.setVelocity(v);
	// }

	@Override
	public void cleanup(Player player) {
		player.removePotionEffect(PotionEffectType.SPEED);
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(grapple);
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
		return true;
	}

	@EventHandler
	public void onM(PlayerMoveEvent eve) {
		if (Game.isGameRunning()) {
			AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer().getUniqueId());
			//
			if (hasThisKit(ap))
				if (!eve.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
					ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
		}
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
		if (ap.getKit().equals(this)) {
			AnnihilationMain.INSTANCE.getServer().getScheduler().runTask(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
					if (!grapples.contains(p.getName())) {
						grapples.add(p.getName());
					}
				}
			});
		}
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		
	}
}