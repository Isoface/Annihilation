package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;
import com.hotmail.AdrianSRJose.base.DelayUpdate;
import com.hotmail.AdrianSRJose.base.Delays;
import com.hotmail.AdrianSRJose.events.AcrobatJumpEvent;

public class Acrobat extends ConfigurableKit {
	
	private String reuseMessage = ChatColor.DARK_GRAY + "Now you can " + ChatColor.GOLD + "jump again!";
	private boolean useReuseMessage = true;
	private double yMultipler = 1.6D;

	//
	@Override
	protected void setUp() {
		final Acrobat k = this;
		Delays.getInstance().createNewDelay(getInternalName(), new DelayUpdate() {
			@Override
			public void update ( AnniPlayer ap , int secondsLeft ) {
				if ( !ap.isOnline ( ) ) {
					return;
				}
				
				Player player = ap.getPlayer ( );
				if (secondsLeft <= 0) {
					AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());

					if (Game.isGameRunning() && p != null && p.getKit().equals(k)) {
						boolean c = true;
						boolean bm = true;
						String msg = null;
						Object obj = p.getData("key-ajmv-anni-kit");
						//
						if (obj != null && obj instanceof AcrobatJumpEvent) {
							AcrobatJumpEvent ajv = ((AcrobatJumpEvent) obj);
							c = ajv.isCancelled() ? false : true;
							bm = ajv.getUseReuseMessage();
							ajv.setSetReuseMessage(reuseMessage);
							msg = ajv.getReuseMessage();
						}
						//
						if (!c)
							return;
						//
						//
						player.setAllowFlight(true);
						//
						player.playSound(player.getLocation(), UniversalSound.WITHER_SHOOT.asBukkit(), 4.0F, 2.0F);
						//
						//
						bm = msg == null ? false : true;
						//
						if (bm && useReuseMessage)
							player.sendMessage(msg);
						//
						p.setData("key-ajmv-anni-kit", null);
					}
				}
			}
		});
	}

	@Override
	protected String getInternalName() {
		return "Acrobat";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.FEATHER.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the feather.", "", aqua + "Soar through the air with ",
				aqua + "a graceful double jump, ", aqua + "shoot from the skies with ", aqua + "your bow, ", "",
				aqua + "and run for longer than ", aqua + "others with increased stamina.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addBow()
				.addSoulboundItem(new ItemStack ( UniversalMaterial.ARROW.getMaterial ( ) , 10 ));
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player player) {
		assert player != null;
		//
		player.setAllowFlight(false);
		player.setFlying(false);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void AcrobatDoubleJump(PlayerToggleFlightEvent event) {
		final Player player = event.getPlayer();
		final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
		if (!KitUtils.isValidPlayer(player) || p == null || !p.getKit().equals(this) || KitUtils.isOnLobby(player))
			return;
		//
		if (Game.isGameRunning() && p.getKit().equals(this)) {
			if (p.getTeam() != null) {
				if (player.getGameMode() != GameMode.CREATIVE) {
					final AcrobatJumpEvent ajv = new AcrobatJumpEvent(p);
					ajv.setMultiplicatedHeight(yMultipler);
					AnniEvent.callEvent(ajv);
					//
					if (!ajv.isCancelled()) {
						event.setCancelled(true);
						//
						p.setData("key-ajmv-anni-kit", ajv);
						Delays.getInstance().addDelay(player, System.currentTimeMillis() + 13000, getInternalName());
						//
						player.setAllowFlight(false);
						player.setFlying(false);
						//
						player.setVelocity(player.getLocation().getDirection().setY(1).multiply(ajv.getHeight()));
						//
						player.playSound(player.getLocation(), UniversalSound.ZOMBIE_INFECT.asBukkit(), 4.0F, 2.0F);
						//
						if (Util.isValidLoc(player.getLocation())) {
							final Location ploc = player.getLocation();
							//
							ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.SMOKE, 1, 600);
							ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.SMOKE, 2, 600);
							ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.SMOKE, 3, 600);
							ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.SMOKE, 4, 600);
							
							if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
								ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.valueOf("EXPLOSION"), 4, 100);
								ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.valueOf("SMALL_SMOKE"), 4, 200);
								ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.valueOf("SMALL_SMOKE"), 1, 200);
								ploc.getWorld().playEffect(ploc.clone().add(0.0D, -1.0D, 0.0D), Effect.valueOf("SMALL_SMOKE"), 2, 200);
							}
						}
					}
				}
			} else {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void fallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL) {
			final Player p = (Player) event.getEntity();
			if (KitUtils.isValidPlayer(p)) {
				final AnniPlayer pl = AnniPlayer.getPlayer(p.getUniqueId());
				if (pl != null && pl.getKit().equals(this)) {
					event.setCancelled(true);
					p.playSound(p.getLocation(), UniversalSound.FALL_BIG.asBukkit(), 1.0F, 2.0F);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void AcrobatJumpMonitor(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (!KitUtils.isValidPlayer(player)) {
			return;
		}

		final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
		if (Game.isGameRunning() && p != null && p.getTeam() != null && p.getKit().equals(this)
				&& !KitUtils.isOnLobby(player)) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				if (player.isFlying()) {
					player.setAllowFlight(false);
					player.setFlying(false);
					return;
				}

				if (!player.getAllowFlight()) {
					if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != UniversalMaterial.AIR.getMaterial ( )
							&& !Delays.getInstance().hasActiveDelay(player, this.getInternalName())) {
						player.setAllowFlight(true);
						return;
					}
				}
			}
		}
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		int save = 0;
		save += Util.setDefaultIfNotSet(section, "Use-Jump-Reuse-Message", true);
		save += Util.setDefaultIfNotSet(section, "Jump-Reuse-Message", reuseMessage.replace("§", "&"));
		save += Util.setDefaultIfNotSet(section, "Multipler-Y", 1.6D);
		return save;
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section.isString("Jump-Reuse-Message"))
			reuseMessage = Util.wc(section.getString("Jump-Reuse-Message"));

		this.yMultipler = section.getDouble("Multipler-Y", yMultipler);
		this.useReuseMessage = section.getBoolean("Use-Jump-Reuse-Message");
	}
}
