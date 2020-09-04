package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Tinkerer extends ConfigurableKit {
	private List<UUID> pls;
	
	private static final Material            AIR = UniversalMaterial.AIR.getMaterial ( );
	private static final Material     IRON_BLOCK = UniversalMaterial.IRON_BLOCK.getMaterial ( );
	private static final Material REDSTONE_BLOCK = UniversalMaterial.REDSTONE_BLOCK.getMaterial ( );
	private static final Material     COAL_BLOCK = UniversalMaterial.COAL_BLOCK.getMaterial ( );
	private static final Material  DIAMOND_BLOCK = UniversalMaterial.DIAMOND_BLOCK.getMaterial ( );
	private static final Material     GOLD_BLOCK = UniversalMaterial.GOLD_BLOCK.getMaterial ( );
	private static final Material  EMERALD_BLOCK = UniversalMaterial.EMERALD_BLOCK.getMaterial ( );
	private static final Material    STONE_PLATE = UniversalMaterial.STONE_PRESSURE_PLATE.getMaterial ( );

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onP(BlockPlaceEvent eve) {
		if (Game.isNotRunning())
			return;
		if (eve.getBlock().getType() != IRON_BLOCK && eve.getBlock().getType() != COAL_BLOCK
				&& eve.getBlock().getType() != REDSTONE_BLOCK
				&& eve.getBlock().getType() != DIAMOND_BLOCK && eve.getBlock().getType() != GOLD_BLOCK
				&& eve.getBlock().getType() != EMERALD_BLOCK)
			return;

		if (Game.getGameMap().getAreas().getArea(new Loc(eve.getBlockPlaced().getLocation(), false)) != null) {
			eve.setCancelled(true);
			return;
		}
		//
		final Player p = eve.getPlayer();
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		if (ap != null && ap.getTeam() != null && ap.getKit().equals(this)) {
			eve.getBlockPlaced().setMetadata("tink", new FixedMetadataValue(AnnihilationMain.INSTANCE, "tink"));
			eve.getBlockPlaced().setMetadata(p.getName(),
					new FixedMetadataValue(AnnihilationMain.INSTANCE, p.getName()));

			if (ap.getData("tinkBList") == null) {
				List<Block> bls = new ArrayList<Block>();
				ap.setData("tinkBList", bls);
			}

			List<Block> ls = ((List<Block>) ap.getData("tinkBList"));

			if (!ls.contains(eve.getBlockPlaced()))
				ls.add(eve.getBlockPlaced());

			eve.getBlockPlaced().getRelative(BlockFace.UP).setType(STONE_PLATE);
			eve.getBlockPlaced().getRelative(BlockFace.UP).setMetadata("tink",
					new FixedMetadataValue(AnnihilationMain.INSTANCE, "tink"));
			eve.getBlockPlaced().getRelative(BlockFace.UP).setMetadata(p.getName(),
					new FixedMetadataValue(AnnihilationMain.INSTANCE, p.getName()));
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onB(BlockBreakEvent eve) {
		if (Game.isNotRunning())
			return;
		if (eve.getBlock().getType() != IRON_BLOCK && eve.getBlock().getType() != COAL_BLOCK
				&& eve.getBlock().getType() != REDSTONE_BLOCK
				&& eve.getBlock().getType() != DIAMOND_BLOCK && eve.getBlock().getType() != GOLD_BLOCK
				&& eve.getBlock().getType() != EMERALD_BLOCK
				&& eve.getBlock().getType() != STONE_PLATE)
			return;

		if (!eve.getBlock().hasMetadata("tink"))
			return;
		//
		Player p = eve.getPlayer();
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		Block bl = eve.getBlock();
		//
		if (bl == null)
			return;
		//
		if (ap != null && ap.getTeam() != null) {
			if (bl.hasMetadata(p.getName())) {
				eve.setCancelled(true);
				//
				if (eve.getBlock().getType() != STONE_PLATE) {
					p.getInventory().addItem(KitUtils.addSoulbound(new ItemStack(eve.getBlock().getType())));
					eve.getBlock().getRelative(BlockFace.UP).setType(AIR);
				} else {
					p.getInventory().addItem(
							KitUtils.addSoulbound(new ItemStack(eve.getBlock().getRelative(BlockFace.DOWN).getType())));

					eve.getBlock().setType(AIR);
					eve.getBlock().getRelative(BlockFace.DOWN).setType(AIR);
				}
				//
				List<Block> ls = ((List<Block>) ap.getData("tinkBList"));
				if (ls != null)
					ls.remove(eve.getBlock());

				eve.getBlock().setType(AIR);

				bl.removeMetadata("tink", AnnihilationMain.INSTANCE);
				bl.removeMetadata(p.getName(), AnnihilationMain.INSTANCE);
			} else {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (KitUtils.isValidPlayer(all)) {
						if (bl.hasMetadata(all.getName())) {
							eve.setCancelled(true);
							//
							AnniPlayer allP = AnniPlayer.getPlayer(all.getUniqueId());
							//
							if (allP != null && !allP.getTeam().equals(ap.getTeam())) {
								if (bl.getType() != STONE_PLATE) {
									bl.getRelative(BlockFace.UP).setType(AIR);
									bl.setType(AIR);
								} else {
									bl.setType(AIR);
									bl.getRelative(BlockFace.DOWN).setType(AIR);
								}
								//
								bl.removeMetadata("tink", AnnihilationMain.INSTANCE);
								bl.removeMetadata(p.getName(), AnnihilationMain.INSTANCE);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onM(PlayerMoveEvent eve) {
		if (Game.isNotRunning())
			return;
		if (eve.getTo() == null)
			return;
		//
		final Player p = eve.getPlayer();
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		if (ap != null && ap.getTeam() != null) {
			Block down = eve.getTo().getBlock().getRelative(BlockFace.SELF);
			//
			if (!down.hasMetadata("tink"))
				return;
			//
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (KitUtils.isValidPlayer(all)) {
					if (down.hasMetadata(all.getName())) {
						AnniPlayer allP = AnniPlayer.getPlayer(all.getUniqueId());
						//
						if (allP != null && !allP.getTeam().equals(ap.getTeam()))
							return;
					}
				}
			}
			//
			if (!eve.getFrom().getBlock().getRelative(BlockFace.SELF).getLocation().equals(down.getLocation())) {
				if (down.getType() == STONE_PLATE) {
					Block bl = down.getRelative(BlockFace.DOWN);
					//
					if (bl.getType() != IRON_BLOCK && bl.getType() != COAL_BLOCK
							&& bl.getType() != REDSTONE_BLOCK && bl.getType() != DIAMOND_BLOCK
							&& bl.getType() != GOLD_BLOCK && bl.getType() != EMERALD_BLOCK)
						return;

					if (bl.getType() != IRON_BLOCK) {
						p.playSound(p.getLocation(), UniversalSound.BLAZE_BREATH.asBukkit(), 2.0F, 8.0F);
					}

					if (bl.getType() == IRON_BLOCK) {
						p.setVelocity(eve.getTo().getDirection().multiply(4));

						if (pls == null)
							pls = new ArrayList<UUID>();

						if (!pls.contains(p.getUniqueId()))
							pls.add(p.getUniqueId());

						p.playSound(p.getLocation(), UniversalSound.WITHER_SHOOT.asBukkit(), 4.0F, 2.0F);
					} else if (bl.getType() == COAL_BLOCK) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 45 * 20, 0));
					} else if (bl.getType() == REDSTONE_BLOCK) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 45 * 20, 0));
					} else if (bl.getType() == DIAMOND_BLOCK) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 45 * 20, 1));
					} else if (bl.getType() == GOLD_BLOCK) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 45 * 20, 1));
					} else if (bl.getType() == EMERALD_BLOCK) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 45 * 20, 0));
					}
				}
			}
		}
	}

	@EventHandler
	public void onD(EntityDamageEvent eve) {
		if (pls == null || pls.isEmpty())
			return;
		//
		if (Game.isNotRunning())
			return;
		if (eve.getCause() != DamageCause.FALL)
			return;
		//
		if (eve.getEntity() instanceof Player) {
			Player p = (Player) eve.getEntity();
			AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
			//
			if (ap != null && ap.getTeam() != null) {
				if (pls.contains(p.getUniqueId())) {
					pls.remove(p.getUniqueId());
					eve.setCancelled(true);
				}
			}
		}
	}

	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Tinkerer";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(STONE_PLATE);
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the innovator!.", "", aqua + "The tinkerer has focused",
				aqua + "on improving technology", aqua + "in the battlefield and have",
				aqua + "developed PowerPads that buff", aqua + "them selves or their team when",
				aqua + "they walk over them.");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe()
				.addSoulboundItem(new ItemStack(REDSTONE_BLOCK))
				.addSoulboundItem(new ItemStack(COAL_BLOCK));
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player p) {
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		Object lits = ap.getData("tinkBList");
		if (ap != null && lits != null && lits instanceof List) {
			for (Block b : ((List<Block>) ap.getData("tinkBList"))) {
				if (b != null) {
					b.removeMetadata("tink", AnnihilationMain.INSTANCE);
					b.getLocation().getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
					b.getLocation().getWorld().playEffect(b.getLocation(), Effect.SMOKE, 4);
					b.getRelative(BlockFace.UP).setType(AIR);
					b.setType(AIR);
				}
			}
			//
			try {
				Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						((List<Block>) ap.getData("tinkBList")).clear();
						ap.setData("tinkBList", null);
					}
				}, 50);
			} catch (IllegalPluginAccessException e) {
			}
			//
		}
	}

	@Override
	public boolean onItemClick(Inventory paramInventory, AnniPlayer paramAnniPlayer) {
		this.addLoadoutToInventory(paramInventory);
		return true;
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}
}
