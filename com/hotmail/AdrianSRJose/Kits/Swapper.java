package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.base.ClassItemKit;

public class Swapper extends ClassItemKit {
	private List<UUID> pld;

	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack swapper = KitUtils.addSoulbound(new ItemStack(UniversalMaterial.MUSIC_DISC_CAT.getMaterial()));
		ItemMeta meta = swapper.getItemMeta();
		meta.setDisplayName(getSpecialItemName());
		swapper.setItemMeta(meta);
		return swapper;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.AQUA + "Swapper";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isSoulbound(stack))
				return true;
		}
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		if (p.getTeam() != null) {
			Player e = instance.getPlayerInSight(player, 16);
			if (e != null) {
				AnniPlayer pl = AnniPlayer.getPlayer(e.getUniqueId());
				if (pl != null && !pl.getTeam().equals(p.getTeam())) {
					Location playerLoc = player.getLocation().clone();
					Location entityLoc = e.getLocation().clone();

					Vector playerLook = playerLoc.getDirection();
					Vector playerVec = playerLoc.toVector();
					Vector entityVec = entityLoc.toVector();
					Vector toVec = playerVec.subtract(entityVec).normalize();

					fireworkEffect(player);
					fireworkEffect(e);
					
					// old:
//					for (int i = 15; i > 0; i--) {
//						LoveEffect le = new LoveEffect(new EffectManager(AnnihilationMain.INSTANCE));
//						le.setLocation(player.getLocation());
//						le.particle = Particle.FIREWORKS_SPARK;
//						le.color = Color.WHITE;
//						le.duration = Integer.valueOf(1);
//						le.asynchronous = true;
//						le.particleOffsetY = 1;
//						le.particleCount = 4;
//						le.particleOffsetX = 1;
//						le.particleOffsetZ = 1;
//						le.type = EffectType.INSTANT;
//						le.setDynamicOrigin(new DynamicLocation(player.getLocation()));
//						le.setDynamicTarget(new DynamicLocation(player.getLocation().clone().add(0.0D, 2.0D, 0.0D)));
//						le.start();
//
//						LoveEffect l = new LoveEffect(new EffectManager(AnnihilationMain.INSTANCE));
//						l.setLocation(e.getLocation());
//						l.particle = Particle.FIREWORKS_SPARK;
//						l.color = Color.WHITE;
//						l.duration = Integer.valueOf(1);
//						l.asynchronous = true;
//						l.particleOffsetY = 1;
//						l.particleCount = 4;
//						l.particleOffsetX = 1;
//						l.particleOffsetZ = 1;
//						l.type = EffectType.INSTANT;
//						l.setDynamicOrigin(new DynamicLocation(e.getLocation()));
//						l.setDynamicTarget(new DynamicLocation(e.getLocation().clone().add(0.0D, 2.0D, 0.0D)));
//						l.start();
//					}

					e.teleport(playerLoc.setDirection(playerLook.normalize()));
					player.teleport(entityLoc.setDirection(toVec));

					player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 1));
					e.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 1));
					e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 1));

					if (ServerVersion.serverSameVersion(ServerVersion.v1_8_R1)) {
						player.getLocation().getWorld().playSound(player.getLocation(),
								Sound.valueOf("ENDERMAN_TELEPORT"), 4F, 2.0F);
						e.getLocation().getWorld().playSound(e.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 4F,
								2.0F);
					} else {
						if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
							player.getLocation().getWorld().playSound(player.getLocation(), Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 4F, 2.0F);
							e.getLocation().getWorld().playSound(e.getLocation(), Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 4F, 2.0F);
						} else {
							player.getLocation().getWorld().playSound(player.getLocation(), Sound.valueOf ( "ENTITY_ENDERMAN_TELEPORT" ), 4F, 2.0F);
							e.getLocation().getWorld().playSound(e.getLocation(), Sound.valueOf ( "ENTITY_ENDERMAN_TELEPORT" ), 4F, 2.0F);
						}
					}

					if (pld == null)
						pld = new ArrayList<UUID>();

					pld.add(player.getUniqueId());
					RemoveDamage8(player);
					return true;
				}
			}
		}
		return false;
	}
	
	private static void fireworkEffect(final Player player) {
		CompatibleParticles.FIREWORK.display(0.5F, 0.8F, 0.5F, 0, 90, player.getLocation().clone().add(0, 0.5, 0),
				100000);
	}

	public void RemoveDamage8(Player p) {
		Bukkit.getScheduler().runTaskLater(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				pld.remove(p.getUniqueId());
			}
		}, 8 * 20);
	}

	@EventHandler
	public void d(EntityDamageEvent eve) {
		if (pld == null || pld.isEmpty())
			return;
		//
		if (!(eve.getEntity() instanceof Player))
			return;
		if (eve.getCause() != DamageCause.FALL)
			return;
		//
		Player s = (Player) eve.getEntity();
		//
		if (KitUtils.isValidPlayer(s)) {
			if (pld.contains(s.getUniqueId())) {
				pld.remove(s.getUniqueId());
				eve.setCancelled(true);
			}
		}
	}

	@Override
	protected long getDefaultDelayLength() {
		return 20000;
	}

	@Override
	protected String getInternalName() {
		return "Swapper";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.MUSIC_DISC_CAT.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are the substitute", "", aqua + "The swapper is able to",
				aqua + "swap places with a nearby", aqua + "enemy every 20 seconds.", "",
				aqua + "The enemy that is swapped has", aqua + "absorption 2 applied for five", aqua + "seconds.", "",
				aqua + "You will also have 8", aqua + "seconds of no fall damage.", "",
				aqua + "Can be used to bring enemies", aqua + "to a location or bring",
				aqua + "yourself to a more advantageous", aqua + "position once held by", aqua + "your enemy.");
		return l;
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(super.getSpecialItem());
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
	protected int setInConfig(ConfigurationSection section) {
		return 0;
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
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