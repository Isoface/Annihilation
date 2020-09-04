package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.UniversalSound;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.base.ClassItemKit;
import com.hotmail.AdrianSRJose.events.Builder_BlockPlaceEvent;
import com.hotmail.AdrianSRJose.events.Builder_OpenResurceDropBookEvent;

public class Builder extends ClassItemKit {
	@Override
	protected void onInitialize() {
	}

	@Override
	protected ItemStack specialItem() {
		ItemStack stack = new ItemStack ( UniversalMaterial.ENCHANTED_BOOK.getMaterial ( ) );
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.getSpecialItemName() + instance.getReadyPrefix());
		stack.setItemMeta(meta);
		return KitUtils.addClassUndropabbleSoulbound(stack);
	}

	@Override
	protected String defaultSpecialItemName() {
		return ChatColor.YELLOW + "ResourceDrop";
	}

	@Override
	protected boolean isSpecialItem(ItemStack stack) {
		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
			if (stack.getItemMeta().getDisplayName().contains(getSpecialItemName())
					&& KitUtils.isClassUndropabbleSoulbound(stack))
				return true;
		return false;
	}

	@Override
	protected boolean performPrimaryAction(Player p, AnniPlayer ap, PlayerInteractEvent event) {
		Inventory MenuDelConstructor = Bukkit.createInventory(null, 27, "§4Resources");
		//
		Builder_OpenResurceDropBookEvent eve = new Builder_OpenResurceDropBookEvent(ap, MenuDelConstructor);
		AnniEvent.callEvent(eve);
		//
		if (eve.isCancelled())
			return false;
		//
		MenuDelConstructor = eve.getResourceDropInvenotry();
		if (MenuDelConstructor == null)
			throw new NullPointerException("Invalid Inventory");
		//
		Random rf = new Random();
		int randomf = rf.nextInt(20);

		ItemStack item1 = new ItemStack(UniversalMaterial.BRICK.getMaterial(), 34);

		ItemStack item2 = new ItemStack(UniversalMaterial.OAK_WOOD.getMaterial(), 28);

		ItemStack item3 = new ItemStack(UniversalMaterial.DIRT.getMaterial(), 21);

		ItemStack item4 = new WoolItemStack ( WoolColor.WHITE , 14 ); // new ItemStack(UniversalMaterial.WHITE_WOOL, 14);

		ItemStack item5 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 37);

		ItemStack item6 = new ItemStack(UniversalMaterial.DIRT.getMaterial(), 15);

		ItemStack item7 = new WoolItemStack ( WoolColor.WHITE , 7 ); // new ItemStack(UniversalMaterial.WHITE_WOOL, 7);

		ItemStack item8 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 38);

		ItemStack item9 = new ItemStack(UniversalMaterial.BRICK.getMaterial(), 24);

		ItemStack item10 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 64);

		ItemStack item11 = new ItemStack(UniversalMaterial.DIRT.getMaterial(), 29);

		ItemStack item12 = new ItemStack(UniversalMaterial.BRICK.getMaterial(), 49);

		ItemStack item13 = new WoolItemStack ( WoolColor.WHITE , 15 ); // new ItemStack(UniversalMaterial.WHITE_WOOL, 15);

		ItemStack item14 = new WoolItemStack ( WoolColor.WHITE , 6 ); // new ItemStack(UniversalMaterial.WHITE_WOOL, 6);

		ItemStack item15 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 27);

		ItemStack item16 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 51);

		ItemStack item17 = new ItemStack(UniversalMaterial.BRICK.getMaterial(), 44);

		ItemStack item18 = new ItemStack(UniversalMaterial.OAK_WOOD.getMaterial(), 30);

		ItemStack item19 = new ItemStack(UniversalMaterial.OAK_WOOD.getMaterial(), 19);

		ItemStack item20 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 14);

		ItemStack item21 = new ItemStack(UniversalMaterial.BRICK.getMaterial(), 54);

		ItemStack item22 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 61);

		ItemStack item23 = new ItemStack(UniversalMaterial.DIRT.getMaterial(), 54);

		ItemStack item24 = new ItemStack(UniversalMaterial.BRICK.getMaterial(), 35);

		ItemStack item25 = new ItemStack(UniversalMaterial.OAK_WOOD.getMaterial(), 14);

		ItemStack item26 = new ItemStack(UniversalMaterial.DIRT.getMaterial(), 25);

		ItemStack item27 = new ItemStack(UniversalMaterial.STONE.getMaterial(), 22);

		ItemStack item28 = new WoolItemStack ( WoolColor.WHITE , 30 ); // new ItemStack(UniversalMaterial.WHITE_WOOL, 30);

		ItemStack itemob1 = new ItemStack(UniversalMaterial.OBSIDIAN.getMaterial(), 1);

		if (randomf == 0) {
			MenuDelConstructor.setItem(9, item1);
			MenuDelConstructor.setItem(1, item2);
			MenuDelConstructor.setItem(5, item3);
			MenuDelConstructor.setItem(6, itemob1);
			MenuDelConstructor.setItem(15, item5);
		}

		if (randomf == 1) {
			MenuDelConstructor.setItem(9, item6);
			MenuDelConstructor.setItem(8, item7);
			MenuDelConstructor.setItem(26, item8);
			MenuDelConstructor.setItem(23, item9);
			MenuDelConstructor.setItem(19, item10);
		}

		if (randomf == 2) {
			MenuDelConstructor.setItem(7, item11);
			MenuDelConstructor.setItem(6, item12);
			MenuDelConstructor.setItem(15, itemob1);
			MenuDelConstructor.setItem(3, item14);
			MenuDelConstructor.setItem(10, item15);
		}

		if (randomf == 3) {
			MenuDelConstructor.setItem(10, item16);
			MenuDelConstructor.setItem(18, item17);
			MenuDelConstructor.setItem(20, item15);
			MenuDelConstructor.setItem(16, item19);
			MenuDelConstructor.setItem(9, item20);
		}

		if (randomf == 4) {
			MenuDelConstructor.setItem(9, item21);
			MenuDelConstructor.setItem(6, item22);
			MenuDelConstructor.setItem(14, item23);
			MenuDelConstructor.setItem(19, item24);
			MenuDelConstructor.setItem(5, item25);
		}

		if (randomf == 5) {
			MenuDelConstructor.setItem(19, item26);
			MenuDelConstructor.setItem(26, itemob1);
			MenuDelConstructor.setItem(7, item28);
			MenuDelConstructor.setItem(20, item2);
			MenuDelConstructor.setItem(12, item5);
		}

		if (randomf == 6) {
			MenuDelConstructor.setItem(26, item8);
			MenuDelConstructor.setItem(15, item10);
			MenuDelConstructor.setItem(4, item15);
			MenuDelConstructor.setItem(3, item8);
			MenuDelConstructor.setItem(5, item28);
		}

		if (randomf == 7) {
			MenuDelConstructor.setItem(8, item15);
			MenuDelConstructor.setItem(6, item5);
			MenuDelConstructor.setItem(19, item3);
			MenuDelConstructor.setItem(26, itemob1);
			MenuDelConstructor.setItem(10, item10);
			MenuDelConstructor.setItem(15, item27);
		}

		if (randomf == 8) {
			MenuDelConstructor.setItem(9, item8);
			MenuDelConstructor.setItem(12, item22);
			MenuDelConstructor.setItem(16, item4);
			MenuDelConstructor.setItem(26, item16);
			MenuDelConstructor.setItem(6, item20);
		}

		if (randomf == 9) {
			MenuDelConstructor.setItem(4, item6);
			MenuDelConstructor.setItem(10, item3);
			MenuDelConstructor.setItem(8, itemob1);
			MenuDelConstructor.setItem(15, item14);
			MenuDelConstructor.setItem(23, item6);
		}

		if (randomf == 10) {
			MenuDelConstructor.setItem(6, item12);
			MenuDelConstructor.setItem(15, item3);
			MenuDelConstructor.setItem(20, item16);
			MenuDelConstructor.setItem(26, itemob1);
			MenuDelConstructor.setItem(2, item20);
		}

		if (randomf == 11) {
			MenuDelConstructor.setItem(9, item1);
			MenuDelConstructor.setItem(20, item20);
			MenuDelConstructor.setItem(2, item9);
			MenuDelConstructor.setItem(19, itemob1);
			MenuDelConstructor.setItem(23, item25);
		}

		if (randomf == 12) {
			MenuDelConstructor.setItem(18, item15);
			MenuDelConstructor.setItem(26, item23);
			MenuDelConstructor.setItem(20, item4);
			MenuDelConstructor.setItem(7, item7);
			MenuDelConstructor.setItem(19, item9);
		}

		if (randomf == 13) {
			MenuDelConstructor.setItem(18, item6);
			MenuDelConstructor.setItem(26, item18);
			MenuDelConstructor.setItem(8, itemob1);
			MenuDelConstructor.setItem(13, item3);
			MenuDelConstructor.setItem(1, item1);
		}

		if (randomf == 14) {
			MenuDelConstructor.setItem(14, itemob1);
			MenuDelConstructor.setItem(9, item6);
			MenuDelConstructor.setItem(15, item18);
			MenuDelConstructor.setItem(2, item20);
			MenuDelConstructor.setItem(26, item13);
		}

		if (randomf == 15) {
			MenuDelConstructor.setItem(23, item1);
			MenuDelConstructor.setItem(1, item2);
			MenuDelConstructor.setItem(9, itemob1);
			MenuDelConstructor.setItem(16, item4);
			MenuDelConstructor.setItem(26, item5);
		}

		if (randomf == 16) {
			MenuDelConstructor.setItem(5, item28);
			MenuDelConstructor.setItem(8, item9);
			MenuDelConstructor.setItem(18, item13);
			MenuDelConstructor.setItem(15, itemob1);
			MenuDelConstructor.setItem(26, item16);
		}

		if (randomf == 17) {
			MenuDelConstructor.setItem(9, item1);
			MenuDelConstructor.setItem(5, item16);
			MenuDelConstructor.setItem(15, item3);
			MenuDelConstructor.setItem(26, item4);
			MenuDelConstructor.setItem(6, item5);
		}
		if (randomf == 18) {
			MenuDelConstructor.setItem(18, item12);
			MenuDelConstructor.setItem(10, item21);
			MenuDelConstructor.setItem(6, item1);
			MenuDelConstructor.setItem(1, itemob1);
		}
		if (randomf == 19) {
			MenuDelConstructor.setItem(4, item4);
			MenuDelConstructor.setItem(21, item20);
			MenuDelConstructor.setItem(20, item16);
			MenuDelConstructor.setItem(7, item7);
			MenuDelConstructor.setItem(19, item10);
		}
		if (randomf == 20) {
			MenuDelConstructor.setItem(8, item12);
			MenuDelConstructor.setItem(12, item23);
			MenuDelConstructor.setItem(15, item8);
			MenuDelConstructor.setItem(18, itemob1);
			MenuDelConstructor.setItem(3, item6);
		}

		p.openInventory(MenuDelConstructor);
		p.playSound(p.getLocation(), UniversalSound.CHEST_OPEN.asBukkit(), 1.0F, 4.0F);
		return true;
	}

	@EventHandler
	public void onPlc(BlockPlaceEvent eve) {
		if (Game.isNotRunning())
			return;
		//
		Player p = eve.getPlayer();
		AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		//
		if (ap != null && ap.getTeam() != null && ap.getKit().equals(this)) {
			if (!eve.isCancelled()) {
				Builder_BlockPlaceEvent beve = new Builder_BlockPlaceEvent(ap);
				AnniEvent.callEvent(beve);
				//
				p.setExp(p.getExp() + beve.getGivedExp());
			}
		}
	}

	@Override
	protected boolean performSecondaryAction(Player player, AnniPlayer p, PlayerInteractEvent event) {
		return false;
	}

	@Override
	protected long getDefaultDelayLength() {
		return 120000;
	}

	@Override
	protected boolean useDefaultChecking() {
		return true;
	}

	@Override
	protected String getInternalName() {
		return "Builder";
	}

	@Override
	protected ItemStack getDefaultIcon() {
		return new ItemStack(UniversalMaterial.BRICK.getMaterial());
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You are The stone.", "", aqua + "The Backbone of Any", aqua + "Good defense,", "",
				aqua + "Your Supply kits", aqua + "enable you to ", aqua + "Quickly build",
				aqua + "Defensive Walls and", aqua + "Del Que Puedes Sacar,", aqua + "Structures to Guard",
				aqua + "your Nexus");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addWoodShovel().addItem(getSpecialItem());
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		addLoadoutToInventory(inv);
		return true;
	}

	@Override
	public void cleanup(Player p) {
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
