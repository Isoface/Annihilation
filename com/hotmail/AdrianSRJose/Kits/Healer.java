package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.base.KitConfig;

public class Healer extends ClassItemKit {
	private static final Map<UUID, Long> lasSounds = new HashMap<UUID, Long>();

	@Override
	protected void onInitialize ( ) {
		
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack tor = new ItemStack(
				(ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) ? UniversalMaterial.POPPED_CHORUS_FRUIT.getMaterial ( )
						: UniversalMaterial.PAPER.getMaterial ( ) ), 1);
		return KitUtils.addClassUndropabbleSoulbound(
				KitUtils.setName(tor, (getSpecialItemName()) + instance.getReadyPrefix()));
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "BloodBag";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (KitUtils.isClassUndropabbleSoulbound(stack)) {
			return KitUtils.itemNameContains(stack, getSpecialItemName());
		}
		return false;
	}

	private final Sound play = ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) ? Sound.ENTITY_ELDER_GUARDIAN_HURT
			: Sound.valueOf("IRONGOLEM_THROW");

	@Override
	protected boolean performPrimaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		for (Entity ent : player.getNearbyEntities(6.0, 6.0, 6.0)) {
			if (ent instanceof Player) {
				Player f = (Player) ent;
				if (f != null) {
					final AnniPlayer af = AnniPlayer.getPlayer(f);
					if (KitUtils.isValidPlayer(af) && p.getTeam().equals(af.getTeam())) {
						if (hasThisKit(af)) { // when is another healer
							f.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 2));
						} else {
							f.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2));
						}

						// restore delay
						restoreDelay(p);

						// play Special Effects
						heartEffect(f);
						lineEffect(player, f);
						player.getWorld().playSound(player.getLocation(), play, 2.0F, 1.0F);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean canPlay(final UUID id) {
		if (!lasSounds.containsKey(id) || lasSounds.get(id) == null) {
			lasSounds.put(id, System.currentTimeMillis());
			return true;
		}

		if (System.currentTimeMillis() - lasSounds.get(id).longValue() >= 3000) {
			lasSounds.put(id, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		final Player f = KitConfig.getInstance().getPlayerInSight(player, 6);
		if (f != null) {
			final AnniPlayer af = AnniPlayer.getPlayer(f);
			if (KitUtils.isValidPlayer(af) && p.getTeam().equals(af.getTeam())) {
				final double life = f.getHealth();
				if (hasThisKit(af)) { // when is another healer
					final double toAdd = 14.0;
					final double todo = (life + toAdd);
					f.setHealth(todo > 20.0D ? 20.0D : todo);
				} else {
					final double toAdd = 15.0;
					final double todo = (life + toAdd);
					f.setHealth(todo > 20.0D ? 20.0D : todo);
				}

				// set cooldown to 45 s
				if (getDelay() < 45000L) {
					setDelay(p, Long.valueOf(45000L));
				}

				// play Special Effects
				heartEffect(f);
				lineEffect(player, f);
				if (canPlay(player.getUniqueId())) {
					player.getWorld().playSound(player.getLocation(), play, 2.0F, 1.0F);
				}
				return true;
			}
		}
		return false;
	}

	private void heartEffect(final Player vict) {
		CompatibleParticles.HEART.display(0.3F, 0.8F, 0.3F, 0, 80, vict.getLocation(), 100000);
	}
	
	private static void lineEffect(Player player, final Player vict) {
		double t = 0;
		final Location loc = player.getLocation().clone();
		final Vector direction = player.getLocation().getDirection().normalize();
		final Location locl = vict.getLocation().clone();

		while (t < player.getLocation().distance(locl)) {
			t = t + 0.5;
			double x = direction.getX() * t;
			double y = direction.getY() * t + 1.25;
			double z = direction.getZ() * t;
			loc.add(x, y, z);
			
			if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
				CompatibleParticles.DUST.displayNewerVersions().display(loc, Color.RED, 9999999);
			} else {
				CompatibleParticles.DUST.displayOlderVersions().display(
						new com.hotmail.AdrianSRJose.AnniPro.utils.OldParticleEffect.OrdinaryColor(Color.RED), loc, 9999999);
			}
			
			loc.subtract(x, y, z);
		}
	}

	// OLD:
//	private void lineEffect(final Player player, final Player vict) {
//		LineEffect le = new LineEffect(new EffectManager(AnnihilationMain.INSTANCE));
//		le.particles = 40;
//		le.particle = Particle.REDSTONE;
//		le.color = Color.RED;
//		le.asynchronous = true;
//		le.particleOffsetY = 6;
//		le.type = EffectType.INSTANT;
//		le.setDynamicOrigin(new DynamicLocation(player.getLocation().clone().add(0.0D, 1.2D, 0.0D)));
//		le.setDynamicTarget(new DynamicLocation(vict.getLocation().clone().add(0.0D, 1.4D, 0.0D)));
//		le.start();
//	}
	
	@Override
	protected long getDefaultDelayLength() {
		return 15000;
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
		return 0;
	}

	@Override
	protected String getInternalName() {
		return "Healer";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack((ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) ? UniversalMaterial.POPPED_CHORUS_FRUIT.getMaterial ( )
				: UniversalMaterial.PAPER.getMaterial ( ) ), 1);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> tor = new ArrayList<String>();
		addToList(tor, aqua + "You are the medic.", aqua + "", aqua + "Healers are not as skilled",
				aqua + "in combat, but instead provide", aqua + "support to their team.", aqua + "",
				aqua + "Right click your blood bag item", aqua + "to heal 3 members in an area",
				aqua + "around you for a small amount.", aqua + "", aqua + "Left click your blood bag item",
				aqua + "while looking at an ally", aqua + "near you to heal them a", aqua + "great amount.");
		return tor;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addItem(getSpecialItem());
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
		p.getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);
		p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
	}

	@Override
	public void cleanup(Player p) {
		if (p != null) {
			p.removePotionEffect(PotionEffectType.WEAKNESS);
		}
	}

	@Override
	public boolean onItemClick(Inventory paramInventory, AnniPlayer paramAnniPlayer) {
		paramAnniPlayer.getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);
		paramAnniPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
		this.addLoadoutToInventory(paramInventory);
		return true;
	}
}
