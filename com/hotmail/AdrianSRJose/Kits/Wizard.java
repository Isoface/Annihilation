package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.base.KitConfig;

public class Wizard extends ClassItemKit {
	
	private static final List<UUID> inFall = new ArrayList<UUID>();
	private static String BOOK_NAME  = "&6Spellbook";
	private static String BOOK_TITLE = "&0Select Spell";
	private static String MISSILE_SPELL_NAME   = "&6Missile";
	private static String FREEZE_SPELL_NAME    = "&6Freeze";
	private static String DARKNESS_SPELL_NAME  = "&6Darkness";
	private static String FLAME_SPELL_NAME     = "&6Flame";
	private static String WHIRLWIND_SPELL_NAME = "&6Whirlwind";
	private static String NO_SPELL_SELECTED    = "&eYou have to select a spell.";
	
	public static enum WandMode {
		None,
		Missile(
				"&aDamages enemies for",
				"&67.0 " + "&apoints of damage.",
				"&aCooldown: " + "&620" + "&a seconds"
				),
		
		Freeze("&aSlows enemies for",
				"&67 " + "&aseconds.",
				"&aCooldown: " + "&620" + "&a seconds"
				),
		
		Darkness("&aGives enemies Wither",
				"&afor" + "&6 8 " + "&aseconds.",
				"&aCooldown: " + "&620" + "&a seconds"
				),
		
		Flame(
				"&aLights enemies on fire",
				"&afor" + "&6 8 " + "&aseconds.",
				"&aCooldown: " + "&620" + "&a seconds"
				),
		
		Whirlwind("&aBlasts enemies with knockback.",
					"&aCooldown: " + "&620" + "&a seconds"
					);
		
		private String[] icon_lore;
		
		WandMode(String... default_icon_lore) {
			this.icon_lore = default_icon_lore;
		}
	}
	
	@Override
	protected void onInitialize() {
	}
	
	@Override
	protected int setInConfig(ConfigurationSection section) {
		// message.
		Util.setDefaultIfNotSet(section, "no-spell-selected-message", NO_SPELL_SELECTED);
		
		// book data.
		Util.setDefaultIfNotSet(section, "book-name-item-name", BOOK_NAME);
		Util.setDefaultIfNotSet(section, "book-menu-title",     BOOK_TITLE);
		
		// spells names.
		Util.setDefaultIfNotSet(section, "missile-spell-name",   MISSILE_SPELL_NAME);
		Util.setDefaultIfNotSet(section, "freeze-spell-name",    FREEZE_SPELL_NAME);
		Util.setDefaultIfNotSet(section, "darkness-spell-name",  DARKNESS_SPELL_NAME);
		Util.setDefaultIfNotSet(section, "flame-spell-name",     FLAME_SPELL_NAME);
		Util.setDefaultIfNotSet(section, "whirlwind-spell-name", WHIRLWIND_SPELL_NAME);
		
		// save spell defaults.
		for (WandMode mode : WandMode.values()) {
			// check mode.
			if (mode == WandMode.None) {
				continue;
			}
			
			// get ConfigurationSection.
			ConfigurationSection sc = Util.createSectionIfNoExits(section, mode.name());
			
			// set defaults.
			Util.setDefaultIfNotSet(sc, "Lore", Arrays.asList(mode.icon_lore));
		}
		return 1;
	}
	
	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		// message.
		NO_SPELL_SELECTED = Util.wc(section.getString("no-spell-selected-message", NO_SPELL_SELECTED));
		
		// book data.
		BOOK_NAME  = Util.wc(section.getString("book-name-item-name", BOOK_NAME));
		BOOK_TITLE = Util.wc(section.getString("book-menu-title",     BOOK_TITLE));
		
		// spells names.
		MISSILE_SPELL_NAME   = Util.wc(section.getString("missile-spell-name", MISSILE_SPELL_NAME));
		FREEZE_SPELL_NAME    = Util.wc(section.getString("freeze-spell-name", FREEZE_SPELL_NAME));
		DARKNESS_SPELL_NAME  = Util.wc(section.getString("darkness-spell-name", DARKNESS_SPELL_NAME));
		FLAME_SPELL_NAME     = Util.wc(section.getString("flame-spell-name", FLAME_SPELL_NAME));
		WHIRLWIND_SPELL_NAME = Util.wc(section.getString("whirlwind-spell-name", WHIRLWIND_SPELL_NAME));
		
		// load spells lore.
		for (WandMode mode : WandMode.values()) {
			// check mode.
			if (mode == WandMode.None) {
				continue;
			}
			
			// get and chec kConfigurationSection.
			ConfigurationSection sc = section.getConfigurationSection(mode.name());
			if (sc == null) {
				continue;
			}
			
			// load lore.
			List<String> lore = sc.getStringList("Lore");
			mode.icon_lore    = lore.toArray(new String[lore.size()]);
			
			// translate lore colors.
			for (int x = 0; x < mode.icon_lore.length; x++) {
				mode.icon_lore[x] = Util.wc(mode.icon_lore[x]);
			}
		}
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.STICK.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassSoulbound(stack);
	}

	public ItemStack getWizardBook() {
		ItemStack stack = new ItemStack ( UniversalMaterial.ENCHANTED_BOOK.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.getWizardBookName());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.GOLD + "Wand";
	}

	public String getWizardBookName() {
		return BOOK_NAME;
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (!KitUtils.isClassSoulbound(stack)) {
			return false;
		}
		
		if (!KitUtils.itemNameContains(stack, getSpecialItemName())) {
			return false;
		}
		return true;
	}

	public boolean isWizardBook(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(this.getWizardBookName())
					&& KitUtils.isClassUndropabbleSoulbound(stack))
				return true;

		return false;
	}

	@EventHandler
	public void onInter(PlayerInteractEvent eve) {
		final Player p = eve.getPlayer();
		AnniPlayer ap = AnniPlayer.getPlayer(p);
		//
		if (ap != null && Game.isGameRunning() && ap.getTeam() != null && ap.getKit().equals(this)) {
			if (eve.getAction() == Action.RIGHT_CLICK_AIR || eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (isWizardBook(p.getItemInHand())) {
					eve.setCancelled(true);
					//
					Inventory inv = Bukkit.createInventory(p, 9, BOOK_TITLE);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					WizardWand.CreateWand(ap, WandMode.None);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					ItemStack ms = new ItemStack(UniversalMaterial.NETHER_STAR.getMaterial());
					ItemMeta msMeta = ms.getItemMeta();
					msMeta.setDisplayName(MISSILE_SPELL_NAME);
					msMeta.setLore(Arrays.asList(WandMode.Missile.icon_lore));

					ms.setItemMeta(msMeta);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					ItemStack fe = new ItemStack(UniversalMaterial.SNOWBALL.getMaterial());
					ItemMeta feMeta = fe.getItemMeta();
					feMeta.setDisplayName(FREEZE_SPELL_NAME);
					feMeta.setLore(Arrays.asList(WandMode.Freeze.icon_lore));

					fe.setItemMeta(feMeta);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					ItemStack ds = new ItemStack(UniversalMaterial.COAL.getMaterial());
					ItemMeta dsMeta = ds.getItemMeta();
					dsMeta.setDisplayName(DARKNESS_SPELL_NAME);
					dsMeta.setLore(Arrays.asList(WandMode.Darkness.icon_lore));

					ds.setItemMeta(dsMeta);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					ItemStack fm = new ItemStack(UniversalMaterial.MAGMA_CREAM.getMaterial());
					ItemMeta fmMeta = fm.getItemMeta();
					fmMeta.setDisplayName(FLAME_SPELL_NAME);
					fmMeta.setLore(Arrays.asList(WandMode.Flame.icon_lore));

					fm.setItemMeta(fmMeta);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					ItemStack wd = new ItemStack(UniversalMaterial.STRING.getMaterial());
					ItemMeta wdMeta = wd.getItemMeta();
					wdMeta.setDisplayName(WHIRLWIND_SPELL_NAME);
					wdMeta.setLore(Arrays.asList(WandMode.Whirlwind.icon_lore));

					wd.setItemMeta(wdMeta);
					/////////////////////// )
					/////////////////////////////////////////////////////////////////////////// )
					/////////////////////// )
					inv.setItem(0, ms);
					inv.setItem(1, fe);
					inv.setItem(2, ds);
					inv.setItem(3, fm);
					inv.setItem(4, wd);
					//
					p.openInventory(inv);
				}
			}
		}
	}

	@EventHandler
	public void onCl(InventoryClickEvent eve) {
		if (eve.getInventory() == null) {
			return;
		}
		
		if (eve.getClickedInventory() == null) {
			return;
		}
		
		if ( !Objects.equals ( BOOK_TITLE , eve.getView ( ).getTitle ( ) ) ) {
			return;
		}
		
//		if (!BOOK_TITLE.startsWith(eve.getClickedInventory().getTitle())) {
//			return;
//		}

		ItemStack cli = eve.getCurrentItem();
		Material type = cli.getType();
		final Player p = (Player) eve.getWhoClicked();
		final WizardWand wand = WizardWand.getWizardWand(p);
		//
		eve.setCancelled(true);
		//
		if (wand == null) {
			return;
		}
		//
		if (type.equals(UniversalMaterial.NETHER_STAR.getMaterial())) {
			for (ItemStack ww : p.getInventory()) {
				if (isSpecialItem(ww)) {
					wand.setMode(p, WandMode.Missile);
				}
			}
			return;
		}

		if (type.equals(UniversalMaterial.SNOWBALL.getMaterial())) {
			for (ItemStack ww : p.getInventory()) {
				if (isSpecialItem(ww)) {
					wand.setMode(p, WandMode.Freeze);
				}
			}
			return;
		}

		if (type.equals(UniversalMaterial.COAL.getMaterial())) {
			for (ItemStack ww : p.getInventory()) {
				if (isSpecialItem(ww)) {
					wand.setMode(p, WandMode.Darkness);
				}
			}
			return;
		}

		if (type.equals(UniversalMaterial.MAGMA_CREAM.getMaterial())) {
			for (ItemStack ww : p.getInventory()) {
				if (isSpecialItem(ww)) {
					wand.setMode(p, WandMode.Flame);
				}
			}
			return;
		}

		if (type.equals(UniversalMaterial.STRING.getMaterial())) {
			for (ItemStack ww : p.getInventory()) {
				if (isSpecialItem(ww)) {
					wand.setMode(p, WandMode.Whirlwind);
				}
			}
			return;
		}
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer pb, PlayerInteractEvent eve) {
		WizardWand wand = WizardWand.getWizardWand(p);

		if (wand == null || wand.mode == WandMode.None) {
			p.sendMessage(NO_SPELL_SELECTED);
			return false;
		}

		if (wand != null && !wand.getMode().name().equalsIgnoreCase(WandMode.None.name())) {
			WandMode md = wand.getMode();
			Material m = null;
			byte b = (byte) 0;
			String s = "";

			if (md.equals(WandMode.Missile)) {
				m = UniversalMaterial.QUARTZ_BLOCK.getMaterial ( );
				b = (byte) 15;
				s = "Mi";
			}

			if (md.equals(WandMode.Freeze)) {
				m = UniversalMaterial.PACKED_ICE.getMaterial ( );
				b = (byte) 0;
				s = "Fr";
			}

			if (md.equals(WandMode.Darkness)) {
				m = UniversalMaterial.COAL_BLOCK.getMaterial ( );
				b = (byte) 0;
				s = "Da";
			}

			if (md.equals(WandMode.Flame)) {
				m = UniversalMaterial.FIRE.getMaterial ( );
				b = (byte) 0;
				s = "Fl";
			}

			if (md.equals(WandMode.Whirlwind)) {
				m = UniversalMaterial.COBWEB.getMaterial ( );
				b = (byte) 0;
				s = "Wh";
			}

			final FallingBlock bl = p.getWorld().spawnFallingBlock(p.getEyeLocation(), m, b);
			bl.setVelocity(p.getEyeLocation().getDirection().multiply(1.5));
			//
			try {
				bl.setDropItem(false);
				bl.setHurtEntities(false);
				bl.setMetadata("spell", new FixedMetadataValue(AnnihilationMain.INSTANCE, "spell"));
			} catch (Throwable t) {
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					if (bl != null && bl.getLocation() != null) {
						Block down = bl.getLocation().getBlock().getRelative(BlockFace.DOWN);
						if (down != null && down.getType().isSolid()) {
							bl.remove();
							cancel();
						}
					}
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 0, 0);

			for (Entity all : KitConfig.getNearbyEntities(p.getLocation(), 7, 7, 7, 7)) {
				if (all != null && all instanceof Player) {
					Player np = (Player) all;
					//
					if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1))
						np.playSound(p.getLocation(), Sound.valueOf("BLAZE_BREATH"), 2.0F, 4.0F);
					else
						np.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2.0F, 4.0F);
				}
			}

			Block target = p.getTargetBlock(null, 80);
			if (target == null)
				return false;

			String sr = s;

			new BukkitRunnable() {
				@Override
				public void run() {
					if (bl.isDead() || bl.isOnGround()
							|| bl.getLocation().getBlock().getRelative(BlockFace.SELF).getType() != UniversalMaterial.AIR.getMaterial ( )) {
						if (sr.equals("Mi")) {
							/////////////////////////////////////////////// Damage
							for (Entity ent : bl.getNearbyEntities(4, 4, 4)) {
								if (ent != null && ent instanceof Player) {
									Player toD = (Player) ent;
									AnniPlayer apD = AnniPlayer.getPlayer(toD.getUniqueId());
									//
									if (apD != null && Game.isGameRunning() && apD.getTeam() != null
											&& !apD.getTeam().getName().equals(pb.getTeam().getName())) {
										if (toD.getInventory().getChestplate() != null && toD.getInventory()
												.getChestplate().getType() != UniversalMaterial.LEATHER_CHESTPLATE.getMaterial ( )) {
											if (toD.getInventory().getLeggings() == null) {
												toD.damage(6.0, p);
											} else {
												if (toD.getInventory().getLeggings()
														.getType() != UniversalMaterial.LEATHER_LEGGINGS.getMaterial ( ))
													toD.damage(4.5, p);
											}
										} else
											toD.damage(7.0, p);
									}
								}
							}
							/////////////////////////////////////////////// Damage
							//
							//
							bl.getLocation().getWorld().playSound(bl.getLocation(),
									UniversalSound.EXPLODE.asBukkit(), 3.0F, 6.0F);

							wand.PlayCircleEffect(CompatibleParticles.WITCH, bl.getLocation(), 900.0D);
						}
						/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (sr.equals("Fr")) {
							/////////////////////////////////////////////// Damage
							for (Entity ent : bl.getNearbyEntities(4, 4, 4)) {
								if (ent != null && ent instanceof Player) {
									Player toD = (Player) ent;
									AnniPlayer apD = AnniPlayer.getPlayer(toD.getUniqueId());
									//
									if (apD != null && Game.isGameRunning() && apD.getTeam() != null
											&& !apD.getTeam().equals(pb.getTeam())) {
										toD.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7 * 20, 2));
										toD.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 7 * 20, 2));
									}
								}
							}
							/////////////////////////////////////////////// Damage
							//
							if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1))
								bl.getLocation().getWorld().playSound(bl.getLocation(), Sound.valueOf("ZOMBIE_UNFECT"),
										2.0F, 6.0F);
							else
								bl.getLocation().getWorld().playSound(bl.getLocation(),
										Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 6.0F);
							//
							wand.PlayCircleEffect(CompatibleParticles.ITEM_SNOWBALL, bl.getLocation(), 900.0D);
						}
						///////////////////////////////////////////////////////////////////////////
						else if (sr.equals("Da")) {
							/////////////////////////////////////////////// Damage
							for (Entity ent : bl.getNearbyEntities(4, 4, 4)) {
								if (ent != null && ent instanceof Player) {
									Player toD = (Player) ent;
									AnniPlayer apD = AnniPlayer.getPlayer(toD.getUniqueId());
									//
									if (apD != null && Game.isGameRunning() && apD.getTeam() != null
											&& !apD.getTeam().equals(pb.getTeam()))
										toD.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 8 * 20, 0));
								}
							}
							/////////////////////////////////////////////// Damage
							//
							if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1))
								bl.getLocation().getWorld().playSound(bl.getLocation(), Sound.valueOf("GHAST_MOAN"),
										2.0F, 6.0F);
							else
								bl.getLocation().getWorld().playSound(bl.getLocation(), Sound.ENTITY_GHAST_WARN, 2.0F,
										6.0F);
							//
							wand.CircleEffect(bl.getLocation());
						} else if (sr.equals("Fl")) {

							/////////////////////////////////////////////// Damage
							for (Entity ent : bl.getNearbyEntities(4, 4, 4)) {
								if (ent != null && ent instanceof Player) {
									Player toD = (Player) ent;
									AnniPlayer apD = AnniPlayer.getPlayer(toD.getUniqueId());
									//
									if (apD != null && Game.isGameRunning() && apD.getTeam() != null
											&& !apD.getTeam().equals(pb.getTeam()))
										toD.setFireTicks(8 * 20);
								}
							}
							/////////////////////////////////////////////// Damage
							wand.PlayCircleEffect(CompatibleParticles.FLAME, bl.getLocation(), 900.0D);
						} else if (sr.equals("Wh")) {
							/////////////////////////////////////////////// Damage
							for (Entity ent : bl.getNearbyEntities(4, 4, 4)) {
								if (ent != null && ent instanceof Player) {
									Player     toD = (Player) ent;
									AnniPlayer apD = AnniPlayer.getPlayer(toD.getUniqueId());
									
									if ( !Objects.equals ( apD.getID ( ) , pb.getID ( ) ) 
											&& Objects.equals ( apD.getTeam ( ) , pb.getTeam ( ) ) ) {
										continue;
									}
									
									//
									if (apD != null && apD.getTeam() != null) {

										if (!apD.getTeam().equals(pb.getTeam())) {
											toD.damage(0.1D, p);
										}

										if (toD.getEyeLocation().getPitch() > 60) {
											toD.setVelocity(toD.getLocation().getDirection().clone().multiply(-1.1));
										} else {
											toD.setVelocity(toD.getLocation().getDirection().clone().multiply(-1.7));
										}
										//
										inFall.add(p.getUniqueId());
										//
										if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1))
											bl.getLocation().getWorld().playSound(bl.getLocation(),
													Sound.valueOf("ZOMBIE_INFECT"), 4.0F, 6.0F);
										else
											bl.getLocation().getWorld().playSound(bl.getLocation(),
													Sound.ENTITY_ZOMBIE_INFECT, 4.0F, 6.0F);

										Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
											@Override
											public void run() {
												if (inFall.contains(p.getUniqueId()))
													inFall.remove(p.getUniqueId());
											}
										}, 5 * 20);
									}
								}
							}
							/////////////////////////////////////////////// Damage
							wand.PlayCircleEffect(CompatibleParticles.SPELL, bl.getLocation(), 900.0D);
						}
						//
						//
						bl.remove();
						cancel();
					} else {
						if (sr.equals("Mi")) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.WITCH.displayNewerVersions().display(1.0F, 1.0F, 1.0F, 0.0F, 15,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							} else {
								CompatibleParticles.WITCH.displayOlderVersions().display(1.0F, 1.0F, 1.0F, 0.0F, 15,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							}
						}
						else if (sr.equals("Fr")) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.CLOUD.displayNewerVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 5,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							} else {
								CompatibleParticles.CLOUD.displayOlderVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 5,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							}
						}
						else if (sr.equals("Da")) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.ENTITY_EFFECT.displayNewerVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 15,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							} else {
								CompatibleParticles.ENTITY_EFFECT.displayOlderVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 15,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							}
						}
						else if (sr.equals("Fl")) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.FLAME.displayNewerVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 5,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							} else {
								CompatibleParticles.FLAME.displayOlderVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 5,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							}
						}
						else if (sr.equals("Wh")) {
							if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
								CompatibleParticles.CLOUD.displayNewerVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 8,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							} else {
								CompatibleParticles.CLOUD.displayOlderVersions().display(0.5F, 0.5F, 0.5F, 0.0F, 8,
										bl.getLocation().clone().add(0.5D, 0.0D, 0.5D), 10.0D);
							}
						}
					}
				}
			}.runTaskTimer(AnnihilationMain.INSTANCE, 0L, 0L);

			if (target.getType() != UniversalMaterial.AIR.getMaterial ( )) {
				int i = (int) (p.getLocation().distance(target.getLocation())) / 2;
				Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						bl.remove();
					}
				}, i);
			} else {
				Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
					@Override
					public void run() {
						bl.remove();
					}
				}, 28);
			}

			return true;
		}

		return false;
	}

	@EventHandler
	public void onF(EntityDamageEvent eve) {
		if (Game.isNotRunning())
			return;
		if (eve.getCause() != DamageCause.FALL)
			return;
		if (!(eve.getEntity() instanceof Player))
			return;

		final Player p = (Player) eve.getEntity();
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());

		if (ap != null && ap.getTeam() != null && ap.getKit().equals(this) && inFall.contains(p.getUniqueId()))
			eve.setCancelled(true);
	}

	@EventHandler
	public void onFD(EntityDamageByEntityEvent eve) {
		if (Game.isNotRunning())
			return;
		if (!(eve.getEntity() instanceof Player))
			return;
		//
		final Player p = (Player) eve.getEntity();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		final Entity d = eve.getDamager();
		//
		if (ap != null && ap.hasTeam() && d != null) {
			if (d instanceof FallingBlock) {
				FallingBlock f = (FallingBlock) d;
				//
				if (f.hasMetadata("spell")) {
					eve.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void Plc(BlockPhysicsEvent eve) {
		if (eve.getBlock().hasMetadata("spell")) {
			eve.setCancelled(true);
			eve.getBlock().setType(UniversalMaterial.AIR.getMaterial ( ));
		}
	}

	@EventHandler
	public void Plc(BlockFormEvent eve) {
		if (eve.getBlock().hasMetadata("spell")) {
			eve.setCancelled(true);
			eve.getBlock().setType(UniversalMaterial.AIR.getMaterial ( ));
		}
	}

	@EventHandler
	public void Plc(BlockFromToEvent eve) {
		if (eve.getBlock().hasMetadata("spell")) {
			eve.setCancelled(true);
			eve.getBlock().setType(UniversalMaterial.AIR.getMaterial ( ));
		}
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent eve) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 20000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Wizard";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.STICK.getMaterial ( ));
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the knowledge.", "", aqua + "Use your understanding",
				aqua + "of the dark arts to change ", aqua + "the tide of battle!.", "",
				aqua + "Use your spell book to ", aqua + "study one of five available spells. ",
				aqua + "Using your wand will cast the ", aqua + "spell you studied.");

		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel().addItem(getSpecialItem())
				.addItem(getWizardBook());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
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
}
