package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.KitChangeEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Nexus;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.RegeneratingBlocks;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Transporter extends ConfigurableKit {
	
	private ItemStack transporterItem;
	private String transporterItemName;
	private Map<UUID, Teleporter> teleporters;

	private String broken = ChatColor.AQUA + "Your teleporter was broken by %tc%PLAYER%";
	private String tpBy = ChatColor.AQUA + "This is a teleporter owned by " + ChatColor.WHITE + "%PLAYER%"
			+ ChatColor.AQUA + ", Sneak to go through it.";
	private String canPlaceHere = ChatColor.RED + "You cant place that here.";
	private String canPlaceOnNexus = ChatColor.RED + "You cant place a portal on a nexus.";
	private String canPlaceOnRegenerationBlock = ChatColor.RED + "You cant place a portal on a regenerable block.";

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "TransporterItemName", "&6Portal Maker")
				+ Util.setDefaultIfNotSet(section, "TeleporterBy",
						"&bThis is a teleporter owned by &f%PLAYER%&b, Sneak to go through it.")
				+ Util.setDefaultIfNotSet(section, "ProtalBroken",
						"&bYour teleporter was broken by %tc%PLAYER%")
				+ Util.setDefaultIfNotSet(section, "CanPlaceHere", "&cYou cant place that here.")
				+ Util.setDefaultIfNotSet(section, "CanPlaceOnNexus", "&cYou cant place a portal on a nexus.")
				+ Util.setDefaultIfNotSet(section, "CanPlaceOnRegenerableBlock",
						"&cYou cant place a portal on a regenerating block.");
	}

	@Override
	protected void loadKitStuff(ConfigurationSection section) {
		if (section != null) {
			super.loadKitStuff(section);
			//
			if (section.isString("TransporterItemName"))
				transporterItemName = Util.wc(section.getString("TransporterItemName"));

			if (section.isString("TeleporterBy")) {
				tpBy = Util.wc(section.getString("TeleporterBy", tpBy));
			}

			if (section.isString("ProtalBroken")) {
				broken = Util.wc(section.getString("ProtalBroken", broken));
			}

			if (section.isString("CanPlaceHere")) {
				canPlaceHere = Util.wc(section.getString("CanPlaceHere", canPlaceHere));
			}

			if (section.isString("CanPlaceOnNexus")) {
				canPlaceOnNexus = Util.wc(section.getString("CanPlaceOnNexus", canPlaceOnNexus));
			}

			if (section.isString("CanPlaceOnRegenerableBlock")) {
				canPlaceOnRegenerationBlock = Util
						.wc(section.getString("CanPlaceOnRegenerableBlock", canPlaceOnRegenerationBlock));
			}
		}
	}

	public void setBlockOwner(Block b, UUID player) {
		MetadataValue val = new FixedMetadataValue(AnnihilationMain.INSTANCE, player.toString());
		b.setMetadata("Owner", val);
	}

	public UUID getBlocksOwner(Block b) {
		List<MetadataValue> list = b.getMetadata("Owner");
		if (list == null || list.isEmpty())
			return null;
		return UUID.fromString(list.get(0).asString());
	}

	@Override
	protected void setUp() {
		teleporters = new HashMap<UUID, Teleporter>();
		transporterItem = KitUtils.addSoulbound(new ItemStack(UniversalMaterial.QUARTZ.getMaterial()));
		ItemMeta m = transporterItem.getItemMeta();
		m.setDisplayName("" + transporterItemName);
		transporterItem.setItemMeta(m);
	}

	@Override
	protected String getInternalName() {
		return "Transporter";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.QUARTZ.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, new String[] { aqua + "You are the snake.", "", aqua + "Link two parts of the",
				aqua + "battlefield with portals", aqua + "that your team can use", aqua + "to get the one up on the",
				aqua + "enemy.", "", aqua + "Your portals are removed", aqua + "when you die.", });
		return l;
	}

	@Override
	public void cleanup(Player player) {
	}

	@EventHandler
	public void onChange(final KitChangeEvent eve) {
		if (Game.isGameRunning() && eve.getPlayer() != null && eve.getPlayer().hasTeam()) {
			if (eve.getOldKit().equals(this)) {
				final Teleporter tele = teleporters.remove(eve.getNormalPlayer().getUniqueId());
				//
				if (tele != null) {
					tele.clear();
				}
			}
		}
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(transporterItem);
	}

	private boolean isTransporterItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(transporterItemName) && KitUtils.isSoulbound(stack))
				return true;
		}
		return false;
	}

	private Location getMiddle(Location loc) {
		if (loc == null)
			return null;
		//
		Location k = loc.clone();
		k.setX(k.getBlockX() + 0.5);
		k.setZ(k.getBlockZ() + 0.5);
		return k;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void Teleport(PlayerToggleSneakEvent event) {
		if (event.isSneaking()) {
			final Player player = event.getPlayer();
			//
			if (!KitUtils.isValidPlayer(player))
				return;
			//
			final Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (b != null && b.getType() == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial()) {
				UUID owner = getBlocksOwner(b);
				if (owner != null) {
					final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
					if (p != null) {
						Teleporter tele = teleporters.get(owner);
						if (tele != null && tele.getOwner() != null && tele.isLinked()
								&& tele.getOwner().getTeam() == p.getTeam() && tele.canUse()) {
							Location loc;
							//
							if (tele.getLoc1().equals(b.getLocation()))
								loc = tele.getLoc2().toLocation();
							else
								loc = tele.getLoc1().toLocation();
							//
							loc.setY(loc.getY() + 1);
							Player own = tele.getOwner().getPlayer();
							player.teleport(this.getMiddle(loc));
							loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1);
							//
							if (!player.getUniqueId().equals(own.getUniqueId()))
								own.setExp(own.getExp() + 0.05F);
							//
							//
							if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1)) {
								tele.getLoc1().toLocation().getWorld().playSound(tele.getLoc1().toLocation(),
										Sound.valueOf("ENDERMAN_TELEPORT"), 1F, (float) Math.random());
								tele.getLoc2().toLocation().getWorld().playSound(tele.getLoc2().toLocation(),
										Sound.valueOf("ENDERMAN_TELEPORT"), 1F, (float) Math.random());
							} else {
								if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
									tele.getLoc1().toLocation().getWorld().playSound(tele.getLoc1().toLocation(),
											Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 1F, (float) Math.random());
									tele.getLoc2().toLocation().getWorld().playSound(tele.getLoc2().toLocation(),
											Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 1F, (float) Math.random());
								} else {
									tele.getLoc1().toLocation().getWorld().playSound(tele.getLoc1().toLocation(),
											Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"), 1F, (float) Math.random());
									tele.getLoc2().toLocation().getWorld().playSound(tele.getLoc2().toLocation(),
											Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"), 1F, (float) Math.random());
								}
							}
							//
							tele.delay();
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void specialItemActionCheck(final PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() != UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) && event.getItem() != null
					&& event.getItem().getType() == transporterItem.getType()
					&& event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
				if (p != null && p.getKit().equals(this) && isTransporterItem(event.getItem())
						&& Game.getGameMap() != null
						&& Game.getGameMap().getAreas()
								.getArea(new Loc(event.getClickedBlock().getLocation(), false)) == null
						&& event.getClickedBlock() != null && KitUtils.isOnGameMap(event.getPlayer())) {
					event.setCancelled(true);
					// ------------------------------------------
					Block b = event.getClickedBlock();

					if (RegeneratingBlocks.isRegeneratingBlock(b)
							|| RegeneratingBlocks.hasRegeneratingBlockMetadata(b)) {
						p.getPlayer().sendMessage(canPlaceOnRegenerationBlock);
						return;
					}
					//
					Block other = b.getRelative(BlockFace.UP);
					Block other2 = other.getRelative(BlockFace.UP);
					//
					if (other != null && other.getType() != null && other2 != null && other2.getType() != null
							&& other.getType() == UniversalMaterial.AIR.getMaterial ( ) && other2.getType() == UniversalMaterial.AIR.getMaterial ( ) && b != null
							&& b.getType() != null && canPlace(b.getType().name())) {
						for (AnniTeam t : AnniTeam.Teams) {
							Nexus n = t.getNexus();
							//
							if (n != null && n.getLocation() != null) {
								if (n.getLocation().equals(b.getLocation())) {
									event.getPlayer().sendMessage(canPlaceOnNexus);
									event.setCancelled(true);
									//
									return;
								}
							}
						}
						//
						Teleporter tele = teleporters.get(event.getPlayer().getUniqueId());
						if (tele == null) {
							tele = new Teleporter(p);
							teleporters.put(event.getPlayer().getUniqueId(), tele);
						}
						//
						if (tele.isLinked()) {
							tele.clear();
							tele.setLoc1(b.getLocation(), b.getState());
						} else if (tele.hasLoc1())
							tele.setLoc2(b.getLocation(), b.getState());
						else
							tele.setLoc1(b.getLocation(), b.getState());
						//
						//
						setBlockOwner(b, p.getID());
						//
						event.getPlayer().playSound(b.getLocation(), UniversalSound.BLAZE_HIT.asBukkit(), 1F, 1.9F);
						//
						if (tele.hasLoc1() && tele.getLoc2() != null) {
							Location l2 = tele.getLoc2().toLocation();
							Location l1 = tele.getLoc1().toLocation();
							//
							new BukkitRunnable() {
								boolean b1 = false;
								boolean b2 = false;

								//
								@Override
								public void run() {
									if (b1 && b2)
										cancel();

									if (l1.getBlock().getType() == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) ) {
										Location l1center = l1.clone().add(0.5D, 0.0D, 0.5D);
										
										if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
											CompatibleParticles.CLOUD.displayNewerVersions().display(0.0F, 0.2F, 0.0F, 0.0F, 2,
													l1center.clone().add(0.0D, 1.5D, 0.0D), 100.0D);
										} else {
											CompatibleParticles.CLOUD.displayOlderVersions().display(0.0F, 0.2F, 0.0F, 0.0F, 2,
													l1center.clone().add(0.0D, 1.5D, 0.0D), 100.0D);
										}
									} else
										b1 = true;

									if (l2.getBlock().getType() == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) ) {
										Location l2center = l2.clone().add(0.5D, 0.0D, 0.5D);
										
										if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
											CompatibleParticles.CLOUD.displayNewerVersions().display(0.0F, 0.2F, 0.0F, 0.0F, 2,
													l2center.clone().add(0.0D, 1.5D, 0.0D), 100.0D);
										} else {
											CompatibleParticles.CLOUD.displayOlderVersions().display(0.0F, 0.2F, 0.0F, 0.0F, 2,
													l2center.clone().add(0.0D, 1.5D, 0.0D), 100.0D);
										}
									} else
										b2 = true;
								}
							}.runTaskTimer(AnnihilationMain.INSTANCE, 4L, 0L);
						}
						if (tele.getLoc2() == null) {
							Location l = b.getLocation();
							//
							Teleporter tel = tele;
							new BukkitRunnable() {
								@Override
								public void run() {
									if (tel.getLoc1() != null && tel.getLoc2() != null) {
										cancel();
									} else if (l.getBlock().getType() == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) )
										l.getWorld().playEffect(l.clone().add(0.0D, 1.0D, 0.0D), Effect.SMOKE, 4);
								}
							}.runTaskTimer(AnnihilationMain.INSTANCE, 2L, 0L);
						}
						//
						//
						new BukkitRunnable() {
							@Override
							public void run() {
								Location l = b.getLocation();
								//
								if (l != null)
									Game.getGameMap().getWorld().getBlockAt(l).setType ( UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) );
							}
						}.runTaskLater(AnnihilationMain.INSTANCE, 2);
						//
						event.setCancelled(true);
					}
					// ------------------------------------------
				} else
					event.getPlayer().sendMessage(canPlaceHere);
			} else if (event.getClickedBlock().getType() == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( )
					&& event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				final AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
				//
				if (p != null) {
					event.setCancelled(true);
					UUID owner = getBlocksOwner(event.getClickedBlock());
					if (owner != null) {
						Teleporter tele = teleporters.get(owner);
						//
						if (tele != null) {
							if (owner.equals(event.getPlayer().getUniqueId())) {
								tele.clear();
								return;
							} else if (p.getTeam() != tele.getOwner().getTeam()) {
								tele.clear();
								// tele.getOwner().sendMessage(ChatColor.AQUA+"Your teleporter was broken by
								// "+p.getTeam().getColor()+p.getName());
								tele.getOwner()
										.sendMessage(this.broken.replace("%tc", p.getTeam().getColor().toString())
												.replace("%PLAYER%", p.getName()));
							}
						}
					}
				}
			}
		}
	}

	private boolean canPlace(String materialName) {
		if (materialName == null)
			return false;
		//
		// final String name = type.name();
		// This tells if a transporter block can be placed at this type of block
		switch (materialName) {
		default:
			return true;
		//
		case "BEDROCK":
		case "OBSIDIAN":
		case "CHEST":
		case "TRAPPED_CHEST":
		case "FURNACE":
		case "DISPENSER":
		case "DROPPER":
		case "WORKBENCH":
		case "BURNING_FURNACE":
		case "HOPPER":
		case "BEACON":
		case "ANVIL":
		case "SIGN_POST":
		case "WALL_SIGN":
		case "ENDER_PORTAL":
		case "QUARTZ_ORE":
			return false;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void MoveListeners(PlayerMoveEvent event) {
		if (event.getTo() != null) {
			final Block to = event.getTo().getBlock().getRelative(BlockFace.DOWN);
			//
			if ( to.getType ( ) == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) ) {
				Location x = event.getTo();
				Location y = event.getFrom();
				//
				if (x.getBlockX() != y.getBlockX() || x.getBlockY() != y.getBlockY()
						|| x.getBlockZ() != y.getBlockZ()) {
					AnniPlayer user = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
					UUID owner = getBlocksOwner(to);
					//
					if (owner != null && user != null) {
						Teleporter tele = teleporters.get(owner);
						if (tele != null && tele.isLinked() && tele.getOwner().getTeam().equals(user.getTeam())) {
							event.getPlayer().sendMessage(tpBy.replace("%PLAYER%", tele.getOwner().getName()));
							// event.getPlayer().sendMessage(ChatColor.AQUA+"This is a teleporter owned by
							// "+ChatColor.WHITE+tele.getOwner().getName()+ChatColor.AQUA+", Sneak to go
							// through it.");
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void TeleporterProtect(BlockBreakEvent event) {
		final Player player = event.getPlayer();
		//
		if (!KitUtils.isValidPlayer(player))
			return;
		//
		final Block b = event.getBlock();
		//
		if (b != null && b.getType() == UniversalMaterial.NETHER_QUARTZ_ORE.getMaterial ( ) && player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
			final AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
			//
			if (p != null) {
				UUID owner = getBlocksOwner(b);
				if (owner == null)
					return;
				//
				Teleporter tele = teleporters.get(owner);
				//
				if (tele != null) {
					if (p.getID().equals(owner)) {
						tele.clear();
					} else if (!p.getTeam().equals(tele.getOwner().getTeam())) {
						tele.clear();
						tele.getOwner().sendMessage(this.broken.replace("%tc", p.getTeam().getColor().toString())
								.replace("%PLAYER%", p.getName()));
						// tele.getOwner().sendMessage(ChatColor.AQUA+"Your teleporter was broken by
						// "+p.getTeam().getColor()+p.getName());
					}
				}
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

class Teleporter {
	private AnniPlayer owner;
	private Loc loc1;
	private BlockState state1;
	private Loc loc2;
	private BlockState state2;
	private long nextUse;

	public Teleporter(AnniPlayer owner) {
		this.owner = owner;
		loc1 = null;
		loc2 = null;
		nextUse = System.currentTimeMillis();
	}

	public void setLoc1(Loc loc, BlockState old) {
		loc1 = loc;
		state1 = old;
	}

	public void setLoc1(Location loc, BlockState old) {
		loc1 = new Loc(loc, false);
		state1 = old;
	}

	public void setLoc2(Loc loc, BlockState old) {
		loc2 = loc;
		state2 = old;
	}

	public void setLoc2(Location loc, BlockState old) {
		loc2 = new Loc(loc, false);
		state2 = old;
	}

	public Loc getLoc1() {
		return loc1;
	}

	public Loc getLoc2() {
		return loc2;
	}

	public void clear() {
		loc1 = null;
		loc2 = null;
		nextUse = System.currentTimeMillis();
		if (state1 != null) {
			World w = state1.getWorld();
			w.playEffect(state1.getLocation(), Effect.STEP_SOUND, 153);
			// w.playSound(state1.getLocation(), sound, volume, pitch) The sound
			state1.update(true);
			state1 = null;
		}
		if (state2 != null) {
			World w = state2.getWorld();
			w.playEffect(state2.getLocation(), Effect.STEP_SOUND, 153);
			// w.playSound(state2.getLocation(), sound, volume, pitch) The sound
			state2.update(true);
			state2 = null;
		}
	}

	public void delay() {
		nextUse = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5000, TimeUnit.MILLISECONDS); // Added this
																											// incase I
																											// ever want
																											// to change
																											// the delay
																											// for the
																											// transporter.
	}

	public boolean canUse() {
		return System.currentTimeMillis() >= nextUse;
	}

	public boolean hasLoc1() {
		return loc1 != null;
	}

	public AnniPlayer getOwner() {
		return owner;
	}

	public boolean isLinked() {
		return loc1 != null && loc2 != null;
	}
}
