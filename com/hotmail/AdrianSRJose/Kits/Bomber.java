package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Bomber extends ConfigurableKit {
	private List<UUID> jumpers;
	private Map<UUID, List<Block>> bls = new HashMap<UUID, List<Block>>();

	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Bomber";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) );
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return 0;
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if ( Game.isNotRunning ( ) ) {
			return;
		}
		
		final Player      p = e.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		if (ap != null && ap.hasTeam() && hasThisKit(ap)) {
			if (Game.getGameMap().getAreas().getArea(new Loc(e.getBlockPlaced().getLocation(), false)) != null) {
				e.setCancelled(true);
				return;
			}
			
			final ItemStack hand = e.getItemInHand ( );
			if ( KitUtils.isSoulbound ( hand ) && ( 
					hand.getType ( ) == UniversalMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) || 
					hand.getType ( ) == UniversalMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) ||
					hand.getType ( ) == UniversalMaterial.STONE_PRESSURE_PLATE.getMaterial ( ) ) ) {
				final String meta = ap.getTeam ( ).getName ( );
				
				e.getBlockPlaced().setMetadata(meta, new FixedMetadataValue(AnnihilationMain.INSTANCE, meta));
				e.getBlockPlaced().setMetadata("OWNER_ID",
						new FixedMetadataValue(AnnihilationMain.INSTANCE, p.getUniqueId().toString()));
				e.getBlockPlaced().setMetadata("tinkBlock",
						new FixedMetadataValue(AnnihilationMain.INSTANCE, "tinkBlock"));
				
				if (!bls.containsKey(p.getUniqueId())) {
					bls.put(p.getUniqueId(), new ArrayList<Block>());
				}
				
				bls.get(p.getUniqueId()).add(e.getBlockPlaced());
			}
			
//			if ( KitUtils.isSoulbound ( hand ) && hand.getType ( ).name ( ).contains ( "_PLATE" ) && !Material.WOOD_PLATE.equals ( hand.getType ( ) ) ) {
//				final String meta = ap.getTeam().getName();
//				//
//				e.getBlockPlaced().setMetadata(meta, new FixedMetadataValue(AnnihilationMain.INSTANCE, meta));
//				e.getBlockPlaced().setMetadata("OWNER_ID",
//						new FixedMetadataValue(AnnihilationMain.INSTANCE, p.getUniqueId().toString()));
//				e.getBlockPlaced().setMetadata("tinkBlock",
//						new FixedMetadataValue(AnnihilationMain.INSTANCE, "tinkBlock"));
//				//
//				if (!bls.containsKey(p.getUniqueId()))
//					bls.put(p.getUniqueId(), new ArrayList<Block>());
//
//				bls.get(p.getUniqueId()).add(e.getBlockPlaced());
//			}
		}
	}

	@EventHandler
	public void onM(PlayerMoveEvent eve) {
		if ( Game.isNotRunning ( ) ) {
			return;
		}

		final Block plate = eve.getTo().getBlock().getRelative(BlockFace.SELF);
		final Block down = eve.getTo().getBlock().getRelative(BlockFace.SELF);

		if (plate.getType() != UniversalMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) 
				&& plate.getType() != UniversalMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getMaterial ( )
				&& plate.getType() != UniversalMaterial.STONE_PRESSURE_PLATE.getMaterial ( ) ) {
			return;
		}
		
		if (eve.getFrom().getBlock().getRelative(BlockFace.SELF).getLocation().equals(down.getLocation())) {
			return;
		}
		
		String         type = "";
		PotionEffectType pt = null;
		int x = 0;

		if ( plate.getType ( ) == UniversalMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) ) {
			type = "gold";
		} else if ( plate.getType ( ) == UniversalMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) ) {
			pt = PotionEffectType.CONFUSION;
			type = "iron";
			x = 2;
		} else if ( plate.getType() == UniversalMaterial.STONE_PRESSURE_PLATE.getMaterial ( ) ) {
			pt = PotionEffectType.POISON;
			type = "stone";
		}

		if (type.equals("")) {
			return;
		}

		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());

		if (!plate.hasMetadata(AnniTeam.Red.getName()) && !plate.hasMetadata(AnniTeam.Blue.getName())
				&& !plate.hasMetadata(AnniTeam.Green.getName()) && !plate.hasMetadata(AnniTeam.Yellow.getName()))
			return;

		if (!plate.hasMetadata("tinkBlock"))
			return;

		if (ap.hasTeam()) {
			for (AnniTeam teams : AnniTeam.Teams) // Verify teams
			{
				if (plate.hasMetadata(teams.getName())) // verify if has meta data
				{
					if (teams.equals(ap.getTeam())) // verify if is the ap team or if is enemy team
					{
						p.setVelocity(p.getLocation().getDirection().setY(1).multiply(1.1));
						if (!type.equals("gold")) {
							plate.getWorld().playEffect(plate.getLocation(), Effect.MOBSPAWNER_FLAMES, 3);
							
							if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
								p.getLocation().getWorld().playEffect(p.getLocation(), Effect.valueOf("EXPLOSION"), 6);
							}
							
							jumpers = new ArrayList<UUID>();
							jumpers.add(p.getUniqueId());
							return;
						}
					} else {
						if (!type.equals("gold")) {
							p.addPotionEffect(new PotionEffect(pt, 200, x));
							plate.getWorld().playEffect(plate.getLocation(), Effect.MOBSPAWNER_FLAMES, 3);
						} else {
							AnniPlayer ape = AnniPlayer.getPlayer(p.getUniqueId());

							for (Player all : Bukkit.getOnlinePlayers()) {
								if (plate.hasMetadata(all.getName()))
									ape.setData("LastFallDamagerUUID", all.getUniqueId());
							}

							p.setFireTicks(300);
							plate.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 3);
							
							if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
								p.getLocation().getWorld().playEffect(plate.getLocation(), Effect.valueOf("EXPLOSION"), 6);
								p.getLocation().getWorld().playEffect(plate.getLocation(), Effect.valueOf("EXPLOSION_HUGE"), 6);
								p.getLocation().getWorld().playEffect(plate.getLocation(), Effect.valueOf("EXPLOSION_LARGE"), 6);
							}
							
							p.setVelocity(p.getLocation().getDirection().setY(1).multiply(1.5));
							p.getLocation().getWorld().playSound(p.getLocation(), UniversalSound.EXPLODE.asBukkit(),
									10, 0);
						}

						// Remove
						plate.setType ( UniversalMaterial.AIR.getMaterial ( ) );
					}
				}
			}
		}
	}

	@EventHandler
	public void d(EntityDamageEvent eve) {
		if (jumpers == null || jumpers.isEmpty())
			return;

		if (!(eve.getEntity() instanceof Player))
			return;
		if (eve.getCause() != DamageCause.FALL)
			return;

		final Player s = (Player) eve.getEntity();
		if (jumpers.contains(s.getUniqueId())) {
			jumpers.remove(s.getUniqueId());
			eve.setCancelled(true);
		}
	}

	@EventHandler
	public void onB(BlockBreakEvent eve) {
		if (Game.isNotRunning()) {
			return;
		}

		// OWNER_ID
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		final Block bl = eve.getBlock();
		if (ap.hasTeam()) {
			AnniTeam plateTeam = null;
			for (AnniTeam teams : AnniTeam.Teams) {
				if (bl.hasMetadata(teams.getName())) {
					bl.removeMetadata(teams.getName(), AnnihilationMain.INSTANCE);
					plateTeam = teams;
				}
			}

			UUID plateOwner = null;
			if (bl.hasMetadata("OWNER_ID")) {
				plateOwner = UUID.fromString(bl.getMetadata("OWNER_ID").get(0).asString());
			}

			if (ap.getTeam().equals(plateTeam)) {
				if (plateOwner != null) {
					// Cancell
					eve.setCancelled(true);

					// When is the owner
					if (p.getUniqueId().equals(plateOwner)) {
						p.getInventory().addItem(KitUtils.addSoulbound(new ItemStack(bl.getType(), 1)));
						p.updateInventory();
						bl.removeMetadata(ap.getTeam().getName(), AnnihilationMain.INSTANCE);
						bl.removeMetadata("OWNER_ID", AnnihilationMain.INSTANCE);
						bl.removeMetadata("tinkBlock", AnnihilationMain.INSTANCE);
						bl.setType ( UniversalMaterial.AIR.getMaterial ( ) );
					} else {
						// return
						return;
					}
				}
			}

			if (bls.containsKey(p.getUniqueId()) && bls.get(p.getUniqueId()) != null
					&& !bls.get(p.getUniqueId()).isEmpty()) {
				bls.get(p.getUniqueId()).remove(bl);
			}
		}
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are a Terrorist!!!", "", aqua + "Pleace Gold and Stone Iron Mines", "",
				aqua + "Iron Mine Causes Nausea to", aqua + "Your Enemies.", "", aqua + "The Gold Mine Explodes",
				aqua + "And through Your Enemies on Air", aqua + "The Stone add poisons your Enemies",
				aqua + "The Plates Causes", aqua + "A Super Jump for Your Team");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addStoneSword().addWoodPick().addWoodAxe()
				.addSoulboundItem(new ItemStack ( UniversalMaterial.STONE_PRESSURE_PLATE.getMaterial ( ) , 2 ) )
				.addSoulboundItem(new ItemStack ( UniversalMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) ) )
				.addSoulboundItem(new ItemStack ( UniversalMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.getMaterial ( ) ) );
		
	}

	@Override
	public void cleanup(Player p) {
		if (p != null && bls.containsKey(p.getUniqueId())) {
			if (!bls.get(p.getUniqueId()).isEmpty()) {
				for (Block torem : bls.get(p.getUniqueId()))
					if (torem != null && torem.getType().name().contains("_PLATE")) {
						torem.setType ( UniversalMaterial.AIR.getMaterial ( ) );
					}
				//
				bls.get(p.getUniqueId()).clear();
			}
		}
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
