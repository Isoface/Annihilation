package com.hotmail.AdrianSRJose.Kits;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.KitConfig;
import com.hotmail.AdrianSRJose.base.ProlongedDelayKit;

public class Bard extends ProlongedDelayKit {
	private int DELAY_PER_EFFECT = 60;
	private String DELAY_MESSAGE = "&cYou must wait &6%# &cseconds before playing that again!";
	// Invigorate
	private String invigorate        = ChatColor.GOLD + "Invigorate";
	private String[] invigorate_lore = new String[] {"&aGives team members regeneration."};
	// Enlighten
	private String enlighten         = ChatColor.GOLD + "Enlighten";
	private String[] enlighten_lore  = new String[] {"&aGives team members speed."};
	// Intimidate
	private String intimidate        = ChatColor.GOLD + "Intimidate";
	private String[] intimidate_lore = new String[] {"&aGives enemies weakness."};
	// Shackle
	private String shackle           = ChatColor.GOLD + "Shackle";
	private String[] shackle_lore    = new String[] {"&aGives enemies slowness."};
	// reclaimer
	private String reclaim_buffbox   = ChatColor.GOLD + "Reclaim BuffBox";
	// Inventory
	private String buffbox_inv_name  = ChatColor.BLACK + "Buff Selector";
	private static ItemMenu BUFF_BOX_MENU;
	private static Material JUKEBOX = UniversalMaterial.JUKEBOX.getMaterial ( ); // Material.JUKEBOX;
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		return    Util.setDefaultIfNotSet(section, "BuffboxInventoryName", Util.untranslateAlternateColorCodes(buffbox_inv_name))
				// Invigorate
				+ Util.setDefaultIfNotSet(section, "InvigorateItem",     Util.untranslateAlternateColorCodes(invigorate))
				+ Util.setDefaultIfNotSet(section, "InvigorateItemLore", invigorate_lore)
				// Enlighten
				+ Util.setDefaultIfNotSet(section, "EnlightenItem",      Util.untranslateAlternateColorCodes(enlighten))
				+ Util.setDefaultIfNotSet(section, "EnlightenItemLore",  enlighten_lore)
				// Intimidate
				+ Util.setDefaultIfNotSet(section, "IntimidateItem",     Util.untranslateAlternateColorCodes(intimidate))
				+ Util.setDefaultIfNotSet(section, "IntimidateItemLore", intimidate_lore)
				// Shackle
				+ Util.setDefaultIfNotSet(section, "ShackleItem",        Util.untranslateAlternateColorCodes(shackle))
				+ Util.setDefaultIfNotSet(section, "ShackleItemLore",    shackle_lore)
				// reclaimer
				+ Util.setDefaultIfNotSet(section, "ReclaimBuffBoxItem", Util.untranslateAlternateColorCodes(reclaim_buffbox))
				// delay
				+ Util.setDefaultIfNotSet(section, "DelayPerEffect", DELAY_PER_EFFECT)
				+ Util.setDefaultIfNotSet(section, "DelayMessage", Util.untranslateAlternateColorCodes(DELAY_MESSAGE));
	}
	
	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		DELAY_MESSAGE    = Util.wc(section.getString("DelayMessage", DELAY_MESSAGE));
		DELAY_PER_EFFECT = section.getInt("DelayPerEffect",          DELAY_PER_EFFECT);
		buffbox_inv_name = Util.wc(section.getString("BuffboxInventoryName", buffbox_inv_name));
		// Invigorate
		invigorate       = Util.wc(section.getString("InvigorateItem", invigorate));
		invigorate_lore  = loadLore(section.getStringList("InvigorateItemLore"), enlighten_lore);
		// Enlighten
		enlighten        = Util.wc(section.getString("EnlightenItem", enlighten));
		enlighten_lore   = loadLore(section.getStringList("EnlightenItemLore"), enlighten_lore);
		// Intimidate
		intimidate       = Util.wc(section.getString("IntimidateItem", intimidate));
		intimidate_lore  = loadLore(section.getStringList("IntimidateItemLore"), intimidate_lore);
		// Shackle
		shackle          = Util.wc(section.getString("ShackleItem", shackle));
		shackle_lore     = loadLore(section.getStringList("ShackleItemLore"), shackle_lore);
		reclaim_buffbox  = Util.wc(section.getString("ReclaimBuffBoxItem", reclaim_buffbox));
	}
	
	private static String[] loadLore(final List<String> list, final String[] def) {
		if (list != null && !list.isEmpty()) {
			// get array
			final String[] tor = new String[list.size()];
			for (int x = 0; (x < tor.length && x < list.size()); x++) {
				tor[x] = Util.wc(list.get(x));
			}
			return tor;
		}
		return def;
	}
	
	class BuffBoxPotion {
		private final PotionEffect effectToTeamMates;
		private final PotionEffect effectToEnemies;
		
		BuffBoxPotion(final PotionEffect effectToTeamMates, final PotionEffect effectToEnemies) {
			this.effectToTeamMates = effectToTeamMates;
			this.effectToEnemies   = effectToEnemies;
		}
	}
	
	static class BuffBox {
		private static final Map<UUID, BuffBox> BOXES = new HashMap<UUID, BuffBox>();
		private final UUID                     id;
		private final AnniTeam               team;
		private final Location                loc;
		private       BuffBoxPotion currentPotion;
		private Integer taskID;
		
		BuffBox(final UUID id, final Location loc, final AnniTeam team) {
			this.id   = id;
			this.loc  = loc;
			this.team = team;
			
			// save 
			BOXES.put(id, this);
		}
		
		public void install() {
			if (loc != null) {
				// set as JUKEBOX
				loc.getBlock().setType(JUKEBOX);
				
				// potion and notes effects
				taskID = Integer.valueOf(new BukkitRunnable() {
					private double pose  = 0;
					private int speed    = randomColor();
					
					@Override
					public void run() {
						// check loc
						if (loc == null || loc.getBlock().getType() != JUKEBOX) {
							cancel();
						}
						
						// add potion effect
						if (currentPotion != null) {
							for (Player p : KitConfig.getNearbyPlayers(loc, 5.0D, 5.0D, 5.0D, 5.0D)) {
								// check
								AnniPlayer ap = AnniPlayer.getPlayer(p);
								if (!KitUtils.isValidPlayer(ap)) {
									continue;
								}
								
								// add potion
								if (ap.getTeam().equals(team)) {
									// when is a team mate
									if (currentPotion.effectToTeamMates != null) {
										p.addPotionEffect(currentPotion.effectToTeamMates);
									}
								}
								else {
									// when is a enemy
									if (currentPotion.effectToEnemies != null) {
										p.addPotionEffect(currentPotion.effectToEnemies);
									}
								}
							}
							
							// play note effects
							for (Location lc : Util.getCircle(loc.clone().add(0.5D, 0.0D, 0.5D), (1 + pose), 30)) {
								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									CompatibleParticles.NOTE.displayNewerVersions().display(0, 0, 0, speed, 8, lc, 20000);
								} else {
									CompatibleParticles.NOTE.displayOlderVersions().display(0, 0, 0, speed, 8, lc, 20000);
								}
							}
							
							for (Location lc : Util.getCircle(loc.clone().add(0.5D, 0.0D, 0.5D), (1.5 + pose), 30)) {
								if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
									CompatibleParticles.NOTE.displayNewerVersions().display(0, 0, 0, speed, 8, lc, 20000);
								} else {
									CompatibleParticles.NOTE.displayOlderVersions().display(0, 0, 0, speed, 8, lc, 20000);
								}
							}
							
							// return
							if (pose == 12) {
								pose = 0;
							}
							
							// increse pose
							pose += 0.5D;
						}
					}
				}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 4L).getTaskId());
			}
		}
		
		public void setEffect(final BuffBoxPotion potion) {
			currentPotion = potion;
		}
		
		public void setPlaying(final Material mat) {
			if (loc != null) {
				final Block b = loc.getBlock();
				if (b.getState() instanceof Jukebox) {
					final Jukebox box = (Jukebox) b.getState();
					if (mat != null) {
						// change playing
						box.setPlaying(mat);
					}
					else {
						box.setPlaying ( UniversalMaterial.AIR.getMaterial ( ) );
					}
				}
				
				// update state
				b.getState().update();
			}
		}
		
		public void reclaim(boolean giveToPlayer, boolean performDelay) {
			// get and check player
			final Player p      = Bukkit.getPlayer(id);
			final AnniPlayer ap = AnniPlayer.getPlayer(p);
			if (p != null && KitUtils.isValidPlayer(ap)) {
				// stop music
				setPlaying(null);
				
				// remove from loc
				if (loc != null) {
					loc.getBlock().setType ( UniversalMaterial.AIR.getMaterial ( ) );
				}
				
				// add item to player inventory
				if (giveToPlayer) {
					p.getInventory().addItem(((Bard) ap.getKit()).getSpecialItem());
				}
				
				// perform delay
				if (performDelay) {
					((Bard) ap.getKit()).performDelay(ap);
				}
			}
			
			// cancell task
			if (taskID != null) {
				Bukkit.getScheduler().cancelTask(taskID.intValue());
				taskID = null;
			}
			
			// destroy
			BOXES.put(id, null);
		}
		
		static boolean isBuffBox(final Location location) {
			return getBuffBox(location) != null;
		}
		
		static BuffBox getBuffBox(final Location location) {
			if (location != null) {
				for (BuffBox box : BOXES.values()) {
					if (box == null || box.loc == null) {
						continue;
					}
					
					if (location.equals(box.loc)) {
						return box;
					}
				}
			}
			return null;
		}
		
		static BuffBox get(final UUID id) {
			return BOXES.get(id);
		}
	}
	
	private static int randomColor() {
		Random r = new Random();
		int i = r.nextInt(255);
		return i;
	}
	
	@Override
	protected void onInitialize ( ) {
		// create inventory
		BUFF_BOX_MENU = new ItemMenu(buffbox_inv_name, Size.ONE_LINE);
		
		// set items
		for (int slot = 0; slot < 5; slot ++) {
			// get item vals
			String name   = invigorate;
			String[] lore = invigorate_lore;
			Material item = UniversalMaterial.MUSIC_DISC_MALL.getMaterial ( );
			BuffBoxPotion potion = new BuffBoxPotion(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0), null);
			switch(slot) {
			case 1:
				name   = enlighten;
				lore   = enlighten_lore;
				item   = UniversalMaterial.MUSIC_DISC_FAR.getMaterial ( );
				potion = new BuffBoxPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 0), null);
				break;
			case 2:
				name   = intimidate;
				lore   = intimidate_lore;
				item   = UniversalMaterial.MUSIC_DISC_MELLOHI.getMaterial ( );
				potion = new BuffBoxPotion(null, new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 1));
				break;
			case 3:
				name   = shackle;
				lore   = shackle_lore;
				item   = UniversalMaterial.MUSIC_DISC_STAL.getMaterial ( );
				potion = new BuffBoxPotion(null, new PotionEffect(PotionEffectType.SLOW, 10 * 20, 0));
				break;
			case 4:
				name   = reclaim_buffbox;
				lore   = new String[0];
				item   = JUKEBOX;
				potion = null;
				break;
			}
			
			// set item
			BUFF_BOX_MENU.setItem(slot, new BuffSelector(name, lore, item, potion));
		}
	}
	
	private class BuffSelector extends MenuItem {
		private final BuffBoxPotion potion;
		public BuffSelector(String name, String[] lore, Material item, BuffBoxPotion potion) {
			super(name, new ItemStack(item), lore);
			this.potion = potion;
		}
		
		@Override
		public void onItemClick(ItemClickEvent eve) {
			// get adn check Player
			final Player p      = eve.getPlayer();
			final AnniPlayer ap = AnniPlayer.getPlayer(p);
			if (p == null || !KitUtils.isValidPlayer(ap) || !(ap.getKit() instanceof Bard)) {
				return;
			}
			
			// get buff box
			final BuffBox box = BuffBox.get(p.getUniqueId());
			if (box == null) {
				return;
			}
			
			// when is a spell
			if (potion != null) {
				// check delay
				if (ap.getData("bard-key-effect-delay-bard-key") instanceof Long) {
					final Long last = (Long) ap.getData("bard-key-effect-delay-bard-key");
					final long curr = System.currentTimeMillis();
					final long total = curr - last.longValue();
					if (total < (DELAY_PER_EFFECT * 1000)) {
						p.sendMessage(DELAY_MESSAGE.replace("%#", String.valueOf((int) (DELAY_PER_EFFECT - (total / 1000)))));
						return;
					}
				}
				
				box.setEffect(potion);
				box.setPlaying(getIcon().getType());
				eve.setWillClose(true);
				ap.setData("bard-key-effect-delay-bard-key", Long.valueOf(System.currentTimeMillis()));
				return;
			}
			else { // when is the reclaimer
				box.reclaim(true, true);
				eve.setWillClose(true);
			}
		}
	}

	@Override
	protected ItemStack specialItem() {
		return KitUtils.addClassUndropabbleSoulbound(KitUtils.setName(new ItemStack(JUKEBOX), getSpecialItemName()));
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + "BuffBox";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		return KitUtils.isClassUndropabbleSoulbound(stack) && KitUtils.itemNameContains(stack, getSpecialItemName());
	}
	
	@Override
	protected void doPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent eve) {
		// check dont have already installed buffbox
		final BuffBox old = BuffBox.get(player.getUniqueId());
		if (old != null) {
			return;
		}
		
		// get block
		final Block b = eve.getClickedBlock().getRelative(eve.getBlockFace());
		
		// check area
		if (Game.getGameMap().getAreas().isInSomeArea(b.getLocation())) {
//			b.setType(Material.AIR);
			return;
		}
		
		// create buff box
		final BuffBox box = new BuffBox(player.getUniqueId(), b.getLocation(), p.getTeam());
		
		// install
		box.install();
		
		// remove item
		for (int x = 0; x < player.getInventory().getSize(); x++) {
			ItemStack st = player.getInventory().getItem(x);
			if (this.isSpecialItem(st)) {
				player.getInventory().setItem(x, null);
				player.updateInventory();
			}
		}
		
		// open inventory
		BUFF_BOX_MENU.open(player);
		
		// update inventory
		player.updateInventory();
	}
	
	// EVENTS
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBreakBuffBox(BlockBreakEvent eve) {
		// get and check player and block
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final Block b       = eve.getBlock();
		if (p == null || !KitUtils.isValidPlayer(ap) || !BuffBox.isBuffBox(b.getLocation())) {
			return;
		}
		
		// get buff box at the block
		final BuffBox box = BuffBox.getBuffBox(b.getLocation());
		if (p.getUniqueId().equals(box.id)) { // when is the box owner
			box.reclaim(true, true);
		}
		else {
			if (ap.getTeam().equals(box.team)) { // when is a team mate
				eve.setCancelled(true);
			}
			else {
				box.reclaim(true, true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onOpenBuffBox(PlayerInteractEvent eve) {
		// get and check player and block
		final Player p      = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final Block b       = eve.getClickedBlock();
		if (p == null || !KitUtils.isValidPlayer(ap) || b == null || !BuffBox.isBuffBox(b.getLocation())) {
			return;
		}
		
		// check action
		if (eve.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		// cancell event
		eve.setCancelled(true);
		
		// get buff box at the block and check box owner
		final BuffBox box = BuffBox.getBuffBox(b.getLocation());
		if (p.getUniqueId().equals(box.id)) { 
			BUFF_BOX_MENU.open(p);
		}
	}

	@Override
	protected void doSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		// do nothing
	}

	@Override
	protected long getDefaultDelayLength() {
		return 10 * 1000;
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
	protected String getInternalName() {
		return "Bard";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(JUKEBOX);
	}

	@Override
	protected List<String> getDefaultDescription() {
		return Arrays.asList(new String[] 
				{
				aqua + "You are the music.", 
				"", 
				aqua + "Use your instrument to",
				aqua + "give buffs to your teammates, ", 
				aqua + "or to cause chaos amongst ", 
				aqua + "your enemies."
				});
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	public void cleanup(Player p) {
		final BuffBox box = BuffBox.get(p.getUniqueId());
		if (box != null) {
			box.reclaim(false, true);
		}
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		addLoadoutToInventory(inv);
		return true;
	}
}
