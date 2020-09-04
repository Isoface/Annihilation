package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer.AnniGameMode;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.BookItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;

public class RiftSelector {
	private static String menuTitle;
	private static String UnableToBeLocated;
	private static String toBase;
	private static String Base;
	private static String ProximityToANexus;

	public RiftSelector(String menuTitle, String UnableToBeLocated, String toBase, String Base, String ProximityToANexus) {
		this.menuTitle         = menuTitle;
		this.UnableToBeLocated = UnableToBeLocated;
		this.toBase            = toBase;
		this.Base              = Base;
		this.ProximityToANexus = ProximityToANexus;
	}

	public void open(final Player p) {
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (!ap.hasTeam()) {
			return;
		}

		List<MenuItem> icons = new ArrayList<MenuItem>();

		// Add Teams Rift
		for (AnniTeam team : AnniTeam.Teams) {
			icons.add(new teamSelector(p, team));
		}

		// Add Player Rift
		for (AnniPlayer comp : ap.getTeam().getPlayers()) {
			if (comp == null || !comp.isOnline()) {
				continue;
			}

			// Check is not equal
			if (ap.equals(comp)) {
				continue;
			}

			// Check is not a spectator
			if (comp.getAnniGameMode() == AnniGameMode.Spectator) {
				continue;
			}

			// add as Player Selector Item
			icons.add(new playerSelector(comp.getPlayer()));
		}

		// Open
		new BookItemMenu(menuTitle, icons, false, true).open(p);
	}

	private static class teamSelector extends MenuItem {
		private final AnniTeam target;
		private final String name;

		public teamSelector(final Player owner, final AnniTeam target) {
			super((ChatColor.GOLD
					+ (target.equals(AnniPlayer.getPlayer(owner).getTeam()) ? target.getColor() + toBase 
							: target.getExternalColoredName())), 
					
					!target.equals(AnniPlayer.getPlayer(owner).getTeam()) ? getWoolFromTeam(target)
							: new ItemStack ( UniversalMaterial.WHITE_BED.getMaterial ( ), 1));
			this.target = target;
			name = target.equals(AnniPlayer.getPlayer(owner).getTeam()) ? Base : target.getExternalColoredName();
		}

		@Override
		public void onItemClick(ItemClickEvent event) {
			// Get Player and target
			final Player p = event.getPlayer();

			// Beggin
			if (target != null) {
				p.setMetadata("rift", new FixedMetadataValue(AnnihilationMain.INSTANCE, new NRift(p, target)));
				event.setWillClose(true);
			} else
				p.sendMessage(UnableToBeLocated.replace("%w", name));
		}
	}

	private static ItemStack getWoolFromTeam(AnniTeam team) {
//		byte dam = (byte) 0;
//		if (AnniTeam.Red.equals(team)) {
//			dam = (byte) 14;
//		} else if (AnniTeam.Green.equals(team)) {
//			dam = (byte) 13;
//		} else if (AnniTeam.Blue.equals(team)) {
//			dam = (byte) 11;
//		} else if (AnniTeam.Yellow.equals(team)) {
//			dam = (byte) 4;
//		}
//		return new ItemStack(Material.WHITE_WOOL, 1, dam);
		return new WoolItemStack ( WoolColor.valueOf ( team.getName ( ).toUpperCase ( ) ) , 1 );
	}

	private static class playerSelector extends MenuItem {
		private final UUID targetID;
		private final String name;

		public playerSelector(final Player target) {
			super(ChatColor.GOLD + target.getDisplayName(), new ItemStack ( UniversalMaterial.CREEPER_HEAD.getMaterial ( ) , 1, (short) 3));
			targetID = target.getUniqueId();
			name = target.getName();
		}

		@Override
		public void onItemClick(ItemClickEvent event) {
			// Get Player and target
			final Player p = event.getPlayer();
			final Player target = Bukkit.getPlayer(targetID);

			// Beggin
			if (target != null) {
				final boolean canBuild = Game.getGameMap().getAreas().getArea(new Loc(p.getLocation(), false)) == null;
				if (canBuild) {
					p.setMetadata("rift", new FixedMetadataValue(AnnihilationMain.INSTANCE, new Rift(p, target)));
					event.setWillClose(true);
				} else
					p.sendMessage(ProximityToANexus.replace("%w", name));
			} else
				p.sendMessage(UnableToBeLocated.replace("%w", name));
		}
	}
}
