package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Vine;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Spider extends ClassItemKit {
	
	private final String FBS_METADATA = "falling_spider_block_meta";
	private String itemName = ChatColor.DARK_GREEN + "Web";
	private String web      = ChatColor.DARK_GREEN + "Cobweb";
	private String noWebs   = ChatColor.RED + "You dont have any Cobwebs left!";
	private String disabled = ChatColor.DARK_GREEN + "Web disabled";
	private String enabled  = ChatColor.DARK_GREEN + "Web enabled";
	private boolean canBeUsedInAreas = false;
	private int vineLifeSeconds = 10;
	private int webRestoreDelay = 25;
	private int wallHeight      = 3;
	private int wallWidth       = 2;
	private transient HashSet<String> enabledVines           = new HashSet<String>();
	private transient HashMap<String, VineWall> wallsByOwner = new HashMap<String, VineWall>();
	private final HashMap<UUID, List<BlockState>> webs       = new HashMap<UUID, List<BlockState>>();
	private final Map<UUID, List<BlockState>> fbs            = new HashMap<UUID, List<BlockState>>();
	private static final transient Material BLOCK_TYPE       = UniversalMaterial.VINE.getMaterial ( );// Material.VINE;
	private static final Material WEB = UniversalMaterial.COBWEB.getMaterial ( );
	private static final Material AIR = UniversalMaterial.AIR.getMaterial ( );
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "Web",         Util.untranslateAlternateColorCodes(web))
				+ Util.setDefaultIfNotSet(section, "NoWebs",      Util.untranslateAlternateColorCodes(noWebs))
				+ Util.setDefaultIfNotSet(section, "WebDisabled", Util.untranslateAlternateColorCodes(disabled))
				+ Util.setDefaultIfNotSet(section, "WebEnabled",  Util.untranslateAlternateColorCodes(enabled))
				+ Util.setDefaultIfNotSet(section, "CanBeUsedInAreas", false);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		web      = Util.wc(section.getString("Web", web));
		noWebs   = Util.wc(section.getString("NoWebs", noWebs));
		enabled  = Util.wc(section.getString("WebEnabled", enabled));
		disabled = Util.wc(section.getString("WebDisabled", disabled));
		canBeUsedInAreas = section.getBoolean("CanBeUsedInAreas");
	}

	private class UnVineTask implements Runnable {
		private UnVineTask() {
		}

		@Override
		public void run() {
			for (VineWall wall : wallsByOwner.values()) {
				wall.thawOldBlocks();
			}
		}
	}

	private class VineTask implements Runnable {
		private VineTask() {
		}

		@Override
		public void run() {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!KitUtils.isOnLobby(player)) {
					if (enabledVines.contains(player.getName())) {
						if (Game.getGameMap() != null) {
							// check area
							if (!canBeUsedInAreas) {
								if (Game.getGameMap().getAreas().getArea(new Loc(player.getLocation(), false)) != null) {
									continue;
								}
							}

							// get facing and block base
							BlockFace facing = yawToFace90(player.getLocation().getYaw());
							Block base = player.getLocation().getBlock();
							if (!player.isDead() && player.getLocation().getY() > 1.0D) {
								for (int i = 0; i < wallWidth; i++) {
									for (int y = -2; y < wallHeight; y++) {
										Block left = i == 0 ? base.getRelative(BlockFace.UP, y)
												: getLeft(base.getRelative(BlockFace.UP, y), facing, i);
										Block right = null;
										if (i != 0) {
											right = getRight(base.getRelative(BlockFace.UP, y), facing, i);
										}

										if (left.getRelative(facing).getType().isSolid()) {
											if (getWall(player).addBlock(left, facing)) {
												
												if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
													CompatibleParticles.HAPPY_VILLAGER.displayNewerVersions().display(0.1F, 0.1F, 0.1F, 0.1F, 1,
															player.getLocation().add(0.0D, 0.2D, 0.0D), 100000);
												} else {
													CompatibleParticles.HAPPY_VILLAGER.displayOlderVersions().display(0.1F, 0.1F, 0.1F, 0.1F, 1,
															player.getLocation().add(0.0D, 0.2D, 0.0D), 100000);
												}
												
											}
										}

										if (right != null && right.getRelative(facing).getType().isSolid()) {
											if (getWall(player).addBlock(right, facing)) {
												
												if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
													CompatibleParticles.HAPPY_VILLAGER.displayNewerVersions().display(0.1F, 0.1F, 0.1F, 0.1F, 1,
															player.getLocation().add(0.0D, 0.2D, 0.0D), 100000);
												} else {
													CompatibleParticles.HAPPY_VILLAGER.displayOlderVersions().display(0.1F, 0.1F, 0.1F, 0.1F, 1,
															player.getLocation().add(0.0D, 0.2D, 0.0D), 100000);
												}
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static final BlockFace[] axis90 = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private static BlockFace yawToFace90(float yaw) {
		int index = (int) Math.round((wrapAngle(yaw) + 180.0F) / 90.0D);
		if (index > 3) {
			index = 0;
		}

		return axis90[index];
	}

	private static Block getLeft(Block block, BlockFace direction, int numBlocks) {
		BlockFace bf = getLeftFace(direction);
		return block.getRelative(bf.getModX() * numBlocks, bf.getModY() * numBlocks, bf.getModZ() * numBlocks);
	}

	private static Block getRight(Block block, BlockFace direction, int numBlocks) {
		BlockFace bf = getRightFace(direction);
		return block.getRelative(bf.getModX() * numBlocks, bf.getModY() * numBlocks, bf.getModZ() * numBlocks);
	}

	private static BlockFace getRightFace(BlockFace direction) {
		return getLeftFace(direction).getOppositeFace();
	}

	private static BlockFace getLeftFace(BlockFace direction) {
		switch (direction) {
		case SOUTH:
			return BlockFace.EAST;
		case EAST:
			return BlockFace.NORTH;
		case NORTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.SOUTH;
		default:
			break;
		}
		return BlockFace.NORTH;
	}

	private static float wrapAngle(float angle) {
		float wrappedAngle = angle;
		while (wrappedAngle <= -180.0F) {
			wrappedAngle += 360.0F;
		}

		while (wrappedAngle > 180.0F) {
			wrappedAngle -= 360.0F;
		}

		return wrappedAngle;
	}

	private class VineWall {
		private final String owner;
		private HashMap<Long, List<BlockState>> states = new HashMap<Long, List<BlockState>>();
		private HashMap<Location, BlockState> statesByLocation = new HashMap<Location, BlockState>();
		long counter = 0L;

		public VineWall(Player player) {
			this.owner = player.getName();
		}

		public boolean addBlock(Block block, BlockFace facing) {
			if (block.getType().isSolid()) {
				return false;
			}

			if (block.getType() == BLOCK_TYPE) {
				return false;
			}

			long key = this.counter + vineLifeSeconds;
			if (!this.states.containsKey(Long.valueOf(key))) {
				this.states.put(Long.valueOf(key), new ArrayList<BlockState>());
			}

			BlockState state = block.getState();
			this.states.get(Long.valueOf(key)).add(state);
			this.statesByLocation.put(block.getLocation(), state);
			Vine vine = new Vine(facing);
			BlockState blockState = block.getState();
			blockState.setType(BLOCK_TYPE);
			blockState.setData(vine);
			blockState.update(true, false);
			return true;
		}

		public void breakPlatformBlock(Location location) {
			BlockState state = this.statesByLocation.remove(location);
			if (state != null) {
				state.update(true);
			}
		}

		public void destroy() {
			this.states.clear();
			for (BlockState state : this.statesByLocation.values()) {
				if (state.getBlock().getType() == BLOCK_TYPE) {
					state.update(true);
				}
			}

			this.statesByLocation.clear();
		}

		public boolean isPlatformBlock(Location location) {
			return this.statesByLocation.containsKey(location);
		}

		public void thawOldBlocks() {
			this.counter += 1L;
			if (this.states.containsKey(Long.valueOf(this.counter))) {
				long future = this.counter + vineLifeSeconds;
				for (BlockState state : this.states.remove(Long.valueOf(this.counter))) {
					if (state.getBlock().getType() == BLOCK_TYPE) {
						Player player = Bukkit.getPlayer(this.owner);
						if ((player != null) && (state.getLocation().getWorld().equals(player.getWorld()))
								&& (state.getLocation().distanceSquared(player.getLocation()) < 9.0D)) {
							if (!this.states.containsKey(Long.valueOf(future))) {
								this.states.put(Long.valueOf(future), new ArrayList<BlockState>());
							}
							this.states.get(Long.valueOf(future)).add(state);
						} else {
							state.getWorld().playEffect(state.getLocation(), Effect.STEP_SOUND, BLOCK_TYPE);
							state.update(true);
							this.statesByLocation.remove(state.getLocation());
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void shootWebs(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		final ItemStack item = event.getItem();
		if (item == null) {
			return;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
		{
			if (!hasThisKit(ap) || !isCobWeb(item)) {
				return;
			}
			
			// Cancell event
			event.setCancelled(true);
			
			// Check is not clicking block
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			
			if (item.getAmount() == 1) {
				p.sendMessage(noWebs);
				return;
			}

			final FallingBlock fb = p.getWorld().spawnFallingBlock(p.getEyeLocation(), WEB, (byte) 0);
			fb.setMetadata(FBS_METADATA, new FixedMetadataValue(AnnihilationMain.INSTANCE, p.getUniqueId().toString()));
			fb.setDropItem(false);
			fb.setHurtEntities(false);
			fb.setVelocity(p.getLocation().getDirection().multiply(1.5D));
			p.getInventory().removeItem(web(1));
			p.updateInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void placeCobwebs(final BlockPlaceEvent event) {
		final Player p = event.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		final ItemStack st = event.getItemInHand();
		if (!hasThisKit(ap) || !isCobWeb(st)) {
			return;
		}
		
		// Cancell
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onP(EntityChangeBlockEvent eve) {
		if (!(eve.getEntity() instanceof FallingBlock)) {
			return;
		}

		final FallingBlock fb = (FallingBlock) eve.getEntity();
		if (!fb.hasMetadata(FBS_METADATA)) {
			return;
		}

		final Block b = eve.getBlock();
		final BlockState original = b.getState();
		final UUID id = UUID.fromString(fb.getMetadata(FBS_METADATA).get(0).asString());
		if (Game.getGameMap() != null
				&& Game.getGameMap().getAreas().getArea(new Loc(b.getLocation(), false)) != null) {
			b.setType(AIR);
			eve.setCancelled(true);
			final Player p = Bukkit.getPlayer(id);
			if (p != null && p.isOnline()) {
				p.getInventory().addItem(web(1));
				p.updateInventory();
			}
			return;
		}

		if (fbs.get(id) == null) {
			fbs.put(id, new ArrayList<BlockState>());
		}
		
		final List<BlockState> states = fbs.get(id);
		states.add(original);

		// Update List
		fbs.put(id, states);
		b.setMetadata(FBS_METADATA, new FixedMetadataValue(AnnihilationMain.INSTANCE, id.toString()));

		// Restore Web Task
		Shedulers.scheduleSync((webRestoreDelay * 20), new Runnable() {
			@Override
			public void run() {
				final Player own = Bukkit.getPlayer(id);
				if (own == null) {
					return;
				}
				
				// Remove Web
				if (original.getBlock().getType() == WEB) {
					original.getBlock().setType(AIR);
				}
				
				// Remove Metadata
				original.getBlock().removeMetadata(FBS_METADATA, AnnihilationMain.INSTANCE);
				
				// Remove From Map
				final List<BlockState> states = fbs.get(id);
				states.remove(original);
				fbs.put(id, states);
				
				// Add Web
				if (own.isOnline()) {
					// update inventory
					own.updateInventory();
					
					// calcule total webs in player inventory
					int totalInvWebs = 0;
					for (ItemStack s : own.getInventory().getContents()) {
						if (isCobWeb(s)) {
							totalInvWebs += s.getAmount();
						}
					}
					
					// check is not already have 15 webs
					if (totalInvWebs < 15) {
						own.getInventory().addItem(web(1));
						own.updateInventory();
					}
				}
			}
		});
	}

	@Override
	public void cleanup(Player p) {
		destroyPlatform(p.getName());
		cleanupWebs(p);
		this.enabledVines.remove(p.getName());
	}

	private void cleanupWebs(Player player) {
		if (this.webs.containsKey(player.getUniqueId())) {
			for (BlockState blockState : this.webs.remove(player.getUniqueId())) {
				if (blockState.getLocation().getBlock().getType() == WEB) {
					blockState.getWorld().playEffect(blockState.getLocation(), Effect.STEP_SOUND, WEB.getId());
					blockState.update(true, false);
				}
			}
		}

		if (fbs.get(player.getUniqueId()) != null) {
			for (BlockState state : fbs.get(player.getUniqueId())) {
				if (state == null) {
					continue;
				}

				state.setType(AIR);
				state.update(true, false);
				state.getBlock().setType(AIR);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == BLOCK_TYPE) {
			for (VineWall platform : this.wallsByOwner.values()) {
				if (platform.isPlatformBlock(e.getBlock().getLocation())) {
					e.setCancelled(true);
					platform.breakPlatformBlock(e.getBlock().getLocation());
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		final AnniPlayer ap = AnniPlayer.getPlayer(e.getEntity().getUniqueId());
		if (hasThisKit(ap)) {
			cleanupWebs(e.getEntity());
			if (this.wallsByOwner.containsKey(e.getEntity().getName())) {
				destroyPlatform(e.getEntity().getName());
			}
		}
	}

	@Override
	public void onPlayerSpawn(Player player) {
		player.setMaxHealth(20.0D);
		super.onPlayerSpawn(player);
	}

	@EventHandler
	public void onStuff(PlayerKickEvent e) {
		final AnniPlayer ap = AnniPlayer.getPlayer(e.getPlayer().getUniqueId());
		if (hasThisKit(ap)) {
			cleanupWebs(e.getPlayer());
		}
	}

	@EventHandler
	public void onStuff(PlayerQuitEvent e) {
		final AnniPlayer ap = AnniPlayer.getPlayer(e.getPlayer().getUniqueId());
		if (hasThisKit(ap)) {
			cleanupWebs(e.getPlayer());
		}
	}

	@EventHandler
	public void onStuff(PlayerRespawnEvent e) {
		final AnniPlayer ap = AnniPlayer.getPlayer(e.getPlayer().getUniqueId());
		if (hasThisKit(ap)) {
			cleanupWebs(e.getPlayer());
		}
	}

	private void destroyPlatform(String owner) {
		if (!this.wallsByOwner.containsKey(owner)) {
			return;
		}

		this.wallsByOwner.get(owner).destroy();
	}

	private VineWall getWall(Player player) {
		if (!this.wallsByOwner.containsKey(player.getName())) {
			this.wallsByOwner.put(player.getName(), new VineWall(player));
		}

		return this.wallsByOwner.get(player.getName());
	}
	
	private ItemStack web(int amm) {
		final ItemStack item = new ItemStack(WEB, amm);
		return KitUtils.addClassSoulbound(KitUtils.setName(item, web));
	}
	
	private boolean isCobWeb(ItemStack stack) {
		return KitUtils.isSpecial(stack, false) 
				&& KitUtils.isClassSoulbound(stack)
				&& web.equals(stack.getItemMeta().getDisplayName());
	}
	
	@Override
	protected String defaultSpecialItemName() {
		return this.itemName;
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the web.", "", aqua + "Toggle the web ability", aqua + "to create a wall of vines",
				aqua + "in front and above you.", "", aqua + "These vines can be climed ",
				aqua + "by friend and foe alike!", "", aqua + "Spray sticky spider webs",
				aqua + "to slow down your foes");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel().addItem(getSpecialItem())
				.addItem(web(15));
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(BLOCK_TYPE);
	}

	@Override
	protected String getInternalName() {
		return "Spider";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onInitialize() {
		syncRepeating(new UnVineTask(), 10);
		syncRepeating(new VineTask(), 5);
		for (Player p : Bukkit.getOnlinePlayers()) {
			fbs.put(p.getUniqueId(), new ArrayList<BlockState>());
		}
	}

	private BukkitTask syncRepeating(Runnable runnable, int ticks) {
		return Bukkit.getScheduler().runTaskTimer(AnnihilationMain.INSTANCE, runnable, ticks, ticks);
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack item = KitUtils.addClassUndropabbleSoulbound(new ItemStack(BLOCK_TYPE));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getSpecialItemName());
		item.setItemMeta(meta);
		return item;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(final PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(player.getUniqueId());
		if (hasThisKit(ap) && isSpecialItem(e.getItem())) {
			// cancell
			e.setCancelled(true);
			
			// toggle
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				toggleVines(player);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(final BlockPlaceEvent eve) {
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (hasThisKit(ap) && isSpecialItem(eve.getItemInHand())) {
			// cancell
			eve.setCancelled(true);
			
			// set no build
			eve.setBuild(false);
		}
	}

	private void toggleVines(Player player) {
		if (!this.enabledVines.contains(player.getName())) {
			this.enabledVines.add(player.getName());
		} else {
			this.enabledVines.remove(player.getName());
		}
		if (player.isOnline()) {
			player.sendMessage((this.enabledVines.contains(player.getName()) ? " " + enabled : " " + disabled));
		}
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 0;
	}

	@Override
	protected boolean useDefaultChecking() {
		return false;
	}

	@Override
	protected boolean useCustomMessage() {
		return false;
	}

	@Override
	protected String positiveMessage() {
		return null;
	}

	@Override
	protected String negativeMessage() {
		return null;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer arg1) {
		this.addLoadoutToInventory(inv);
		return false;
	}
}
