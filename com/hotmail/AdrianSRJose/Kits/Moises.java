package com.hotmail.AdrianSRJose.Kits;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.base.KitConfig;

public class Moises extends ClassItemKit {
	
	private static Random random;
	private static boolean GIVE_SPEED = true;
	private static int SPEED_SECONDS = 6;
	private static String ON_SHORE_MESSAGE = ChatColor.RED + "You have to stand on the shore to use this power!";
	
	@Override
	protected void onInitialize() {
		random = new Random();
	}

	@Override
	protected ItemStack specialItem() {
		final ItemStack stack = new ItemStack ( UniversalMaterial.STICK.getMaterial ( ) , 1);
		return KitUtils.addClassUndropabbleSoulbound(
				KitUtils.setName(stack, getSpecialItemName() + instance.getReadyPrefix()));
	}

	@Override
	protected String defaultSpecialItemName() {
		return aqua + "Water Separator";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		return KitUtils.isClassUndropabbleSoulbound(stack) && KitUtils.itemNameContains(stack, getSpecialItemName());
	}
	
	private final Sound play = ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) ? Sound.ENTITY_ELDER_GUARDIAN_HURT
			: Sound.valueOf("IRONGOLEM_THROW");

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		// Get down and front block
		final Block down = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		final BlockFace face = Util.getFacing(player.getEyeLocation().getYaw());
		final Block w1 = down.getRelative(face);
		final Block w2 = down.getRelative(face, 2);
		
		// Check Block in front is water
		if (!isWater(w1) && !isWater(w2)) {
			player.sendMessage(ON_SHORE_MESSAGE);
			return false;
		}
		
		// Give Poton Effect
		if (GIVE_SPEED) {
			for (Entity ent : KitConfig.getNearbyEntities(player.getLocation(), 3)) {
				// Check if entity is player
				if (!(ent instanceof Player)) {
					continue;
				}
				
				// Get and check player
				Player pp = (Player)ent;
				AnniPlayer ppap = AnniPlayer.getPlayer(pp);
				if (ppap == null || pp == null || !ppap.hasTeam()) {
					continue;
				}
				
				// Check is team mate
				if (!p.getTeam().equals(ppap.getTeam())) {
					continue;
				}
				
				// Give potion effect
				pp.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_SECONDS * 20, 0));
				
				// Play sound effect
				pp.playSound(pp.getLocation(), play, 2.0F, 1.0F);
				
				// Play Particle Effect
				if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
					CompatibleParticles.ITEM_SNOWBALL.displayNewerVersions().display(0.1F, 0.6F, 0.1F, 0.0F, 60,
							pp.getLocation(), 90000);
				} else {
					CompatibleParticles.ITEM_SNOWBALL.displayOlderVersions().display(0.1F, 0.6F, 0.1F, 0.0F, 60,
							pp.getLocation(), 90000);
				}
			}
		}

		// Water
		for (int y = 1; y < 10000; y++) {
			Block front = down.getRelative(face, y);
			if (!isWater(front)) {
				if (y == 1) {
					continue;
				}
				
				break;
			}

			// Set to air
			Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new MOISES_WATER_TASK(front.getLocation()), 10 * 20L);
			
			for (int x = 0; x < 4; x++) {
				Block water = getLeft(front, face, x);
				if (isWater(water)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new MOISES_WATER_TASK(water.getLocation()), 10 * 20L);
				}
			}
			
			for (int x = 0; x < 4; x++) {
				Block water = getRight(front, face, x);
				if (isWater(water)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new MOISES_WATER_TASK(water.getLocation()), 10 * 20L);
				}
			}
		}
		
		return true;
	}
	
	private static class MOISES_WATER_TASK implements Runnable {
		private final Location loc;
		private final Material original;
		
		MOISES_WATER_TASK(final Location loc) {
			this.loc = loc;
			final Block b = loc.getBlock();
			original = b.getType();
			b.setMetadata("MOISES_BLOCK", new FixedMetadataValue(AnnihilationMain.INSTANCE, "true"));
			b.setType ( UniversalMaterial.AIR.getMaterial ( ) );
			
			if (random.nextInt(10) <= 1) {
				loc.getWorld().playEffect(loc, Effect.STEP_SOUND, UniversalMaterial.SNOW.getMaterial ( ));
			}
			
			for (int x = 1; x < 256; x++) {
				Block down = b.getRelative(BlockFace.DOWN, x);
				if (!isWater(down)) {
					break;
				}
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new MOISES_WATER_TASK(down.getLocation()), 10 * 20L);
			}
		}
		
		@Override
		public void run() {
			// Get Block
			final Block b = loc.getBlock();
			b.removeMetadata("MOISES_BLOCK", AnnihilationMain.INSTANCE);
			
			// Change Type
			if (!isWater(b)) {
				b.setType(original);
			}
		}
	}
	
	// Cancell water flow to moises blocks
	@EventHandler(priority = EventPriority.MONITOR)
	public void flow(final BlockFromToEvent event) {
		final Block b = event.getBlock();
		final Block c = event.getToBlock();
		if (!c.isLiquid() && !b.isLiquid()) {
			return;
		}
		
		if (isMoisesBlock(b)) {
			if (b.isLiquid()) {
				// Cancell
				event.setCancelled(true);
			}
		} else if (isMoisesBlock(c)) {
			if (c.isLiquid()) {
				// Cancell
				event.setCancelled(true);
			}
		}
	}
	
	private static Block getLeft(Block block, BlockFace direction, int numBlocks) {
		BlockFace bf = getLeftFace2(direction);
		return block.getRelative(bf.getModX() * numBlocks, bf.getModY() * numBlocks, bf.getModZ() * numBlocks);
	}

	private static Block getRight(Block block, BlockFace direction, int numBlocks) {
		BlockFace bf = getRightFace2(direction);
		return block.getRelative(bf.getModX() * numBlocks, bf.getModY() * numBlocks, bf.getModZ() * numBlocks);
	}

	private static BlockFace getRightFace2(BlockFace direction) {
		return getLeftFace2(direction).getOppositeFace();
	}

	private static BlockFace getLeftFace2(BlockFace direction) {
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
	
	private static boolean isWater ( Block b ) {
		// true when the block is water or stationary water
		return b != null && b.getType ( ).name ( ).contains ( "WATER" );
	}
	
	private static boolean isMoisesBlock(final Block b) {
		return isWater(b) && b.hasMetadata("MOISES_BLOCK");
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 25 * 1000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
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
	protected int setInConfig(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "give-speed", true) 
			  + Util.setDefaultIfNotSet(section, "speed-seconds", 6)
			  + Util.setDefaultIfNotSet(section, "no-on-shore", ON_SHORE_MESSAGE);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section != null) {
			GIVE_SPEED = section.getBoolean("give-speed", true);
			SPEED_SECONDS = section.getInt("speed-seconds", 6);
			ON_SHORE_MESSAGE = section.getString("no-on-shore", ON_SHORE_MESSAGE);
		}
	}
	
	@Override
	protected String getInternalName() {
		return "Moises";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.WATER_BUCKET.getMaterial ( ) );
	}
	
	@Override
	protected List<String> getDefaultDescription() {
		return Arrays.asList(new String[] 
				{
						aqua + "You are the water separator!",
						aqua + "",
						aqua + "This kit gives you the",
						aqua + "ability to separate the seas",
						aqua + "for 10 seconds in the",
						aqua + "direction you aim.",
						aqua + "",
						aqua + "The companions around you",
						aqua + "will receive speed",
						aqua + "for 6 seconds.",
						aqua + "",
						aqua + "Coutdown: 25 seconds.",
				});
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodShovel().addItem(getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player d) {
	}

	@Override
	public boolean onItemClick(Inventory arg0, AnniPlayer arg1) {
		this.addLoadoutToInventory(arg0);
		return true;
	}
}
