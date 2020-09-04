package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.events.Alien_TeleportEnemyEvent;

public class Alien extends ClassItemKit {
	
	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack esp = KitUtils.addClassUndropabbleSoulbound ( new ItemStack ( UniversalMaterial.ENDER_PEARL.getMaterial ( ) ) );
		ItemMeta meta = esp.getItemMeta();
		meta.setDisplayName(Util.wc(getSpecialItemName()));
		esp.setItemMeta(meta);
		return esp;
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "AlienTeleport";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			String name = stack.getItemMeta().getDisplayName();
			if (name.contains(getSpecialItemName()) && KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void a(PlayerInteractEvent eve) {
		if (eve.getAction() == Action.RIGHT_CLICK_AIR || eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (KitUtils.itemHasName(eve.getItem(), getSpecialItemName())) {
				final Player p = eve.getPlayer();
				if (KitUtils.isValidPlayer(p)) {
					eve.setCancelled(true);
					eve.getPlayer().setItemInHand(getSpecialItem());
					eve.getPlayer().updateInventory();
				}
			}
		}
	}

	@Override
	protected boolean performPrimaryAction(final Player p, final AnniPlayer pb, PlayerInteractEvent event) {
		final Player e = instance.getPlayerInSight(p, 6);
		if (KitUtils.isValidPlayer(e) && e.getUniqueId() != null) {
			final AnniPlayer ep = AnniPlayer.getPlayer(e.getUniqueId());
			if (ep == null)
				return false;
			//
			if (ep.getTeam() != null && !ep.getTeam().equals(pb.getTeam())) {
				final Location from = p.getLocation();
				final Location to = pb.getTeam().getRandomSpawn();
				//
				if (to == null || KitUtils.isLobbyMap(to.getWorld())) {
					Bukkit.getConsoleSender()
							.sendMessage("§cNO SPAWNS SET FOR TEAM " + pb.getTeam().getColoredName().toUpperCase());
					return false;
				}
				//
				Alien_TeleportEnemyEvent eve = new Alien_TeleportEnemyEvent(pb, ep, from, to);
				AnniEvent.callEvent(eve);
				//
				if (eve.isCancelled())
					return false;
				//
				p.teleport(eve.getTo());
				e.teleport(eve.getTo());
				//
				final boolean b = ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1);
				Sound s = null;
				Sound s2 = null;
				//
				s = null; // b ? Sound.ENTITY_ENDERMEN_TELEPORT : Sound.valueOf("ENDERMAN_TELEPORT");
				if (ServerVersion.serverOlderThan(ServerVersion.v1_13_R1)) {
					s = b ? Sound.valueOf("ENTITY_ENDERMEN_TELEPORT") : Sound.valueOf("ENDERMAN_TELEPORT");
				} else {
					s = Sound.valueOf ( "ENTITY_ENDERMAN_TELEPORT" );
				}
				
				s2 = b ? Sound.valueOf ( "ENTITY_WOLF_GROWL" ) : Sound.valueOf("WOLF_GROWL");
				//
				e.playSound(e.getLocation(), s, 1.0F, (float) Math.random());
				e.playSound(e.getLocation(), s2, 1.0F, (float) Math.random());
				p.playSound(p.getLocation(), s, 1.0F, (float) Math.random());
				e.getLocation().getWorld().playEffect(e.getLocation(), Effect.ENDER_SIGNAL, 1, 100);
				p.getLocation().getWorld().playEffect(e.getLocation(), Effect.ENDER_SIGNAL, 1, 100);
				//
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean performSecondaryAction(Player p, AnniPlayer pb, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 50000L;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Alien";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.COBWEB.getMaterial ( ) , 1 );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "Your Are the Assault", aqua + "", aqua + "Telepor Your Enemies", aqua + "To Your Nexus.",
				aqua + "", aqua + "And attack with the help ", aqua + "of All your Team");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe().addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player player) {
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
