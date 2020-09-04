package com.hotmail.AdrianSRJose.AnniPro.itemMenus.BossShop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class ShopCreator {
	private static final Map<UUID, Container> MENUS = new HashMap<UUID, Container>();

	public static void openCreator(final Player p) {
		// get
		Container menu = MENUS.get(p.getUniqueId());
		if (menu == null) {
			menu = new Container(p.getUniqueId(), Size.SIX_LINE);
			MENUS.put(p.getUniqueId(), menu);
		}
		
		// open
		menu.open(p);
	}

	private static class Container extends ItemMenu {
		private final UUID owner;
		private final Map<Integer, ItemStack> otherItems;
		private Inventory lastInventory;
		private final Container instance;
		
		Container(final UUID owner, final Size size) {
			this(owner, size, null);
		}
		
		Container(final UUID owner, final Size size, Map<Integer, ItemStack> items) {
			super(Util.wc("&6Add boss rewards here."), size);
			instance   = this;
			this.owner = owner;
			otherItems = new HashMap<Integer, ItemStack>();
			
			// add
			if (items != null && !items.isEmpty()) {
				otherItems.clear(); // to fix
				for (Integer slot : items.keySet()) {
					if (slot != null) {
						ItemStack item = items.get(slot);
						if (item != null) {
							otherItems.put(slot, item);
						}
					}
				}
			}

			// set items
			// change size item
			setItem(size.getSize() - 3, new ActionMenuItem(Util.wc("&6Change Size"), new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					new sizeChanger(instance).open(event.getPlayer());
				}
			}, new WoolItemStack ( WoolColor.WHITE , 1 ) ));
			
			// save item
			setItem(size.getSize() - 2, new ActionMenuItem(Util.wc("&6Save"), new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					// update last inventory
					lastInventory = event.getInventory();
					
					// get size and rewards
					final ItemStack[] rewards = new ItemStack[lastInventory.getSize() - 3];
					final int     invSize     = lastInventory.getSize();
					
					// clear
					otherItems.clear(); // to fix
					
					// save inventory items
					for (int x = 0; x < lastInventory.getSize(); x++) {
						if (x == (size.getSize() - 4)) {
							break;
						}
						
						ItemStack getted = event.getInventory().getItem(x);
						if (getted != null) {
							// put to other items
							otherItems.put(Integer.valueOf(x), getted);
							
							// set in rewards
							if (x < rewards.length) {
								rewards[x] = getted;
							}
						}
					}
					
					// Save in the config
					if (GameBoss.getBoss() != null) {
						// save container value
						MENUS.put(owner, instance);
						
						// save rewards and inventory size
						GameBoss.getBoss().setRewards(rewards);
						GameBoss.getBoss().setRewardsInventorySize(invSize);
						
						// send saved message
						event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Saved!");
					
						// save container value
						event.setWillClose(true);
						
						// Save map config
						event.getPlayer().performCommand("anni save BossMap");
					}
					else {
						// send cant be saved message
						event.getPlayer().sendMessage(ChatColor.RED + "You have to set the boss to set the rewards.");
					}
				}
			}, new WoolItemStack ( WoolColor.WHITE , 1 ) ));

			// discard item
			setItem(size.getSize() - 1, new ActionMenuItem(Util.wc("&cClose"), new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					event.setWillClose(true);
				}
			}, new WoolItemStack ( WoolColor.WHITE , 1 )));
		}

		@Override // 16.3
		public void open(Player player) {
			// Check Player
			if (KitUtils.isValidPlayer(player)) {
				if (!ShopMenuListener.getInstance().isRegistered(AnnihilationMain.INSTANCE)) {
					ShopMenuListener.getInstance().register(AnnihilationMain.INSTANCE);
				}
				// Check name
				if (name == null) {
					name = "";
				}
				// Create Inventory
				Inventory inventory = Bukkit.createInventory(
						new ShopMenuHolder(this, Bukkit.createInventory(player, size.getSize())), size.getSize(),
						Util.shortenString(name, 32));
				// check loadad rewards
				final ItemStack[] lrw = checkLoadedRewards();
				if (lrw != null && lrw.length > 0) {
					// set load rewards from config button
					setItem(size.getSize() - 4, new ActionMenuItem(Util.wc("&6Load last saved rewards."), new ItemClickHandler() {
						@Override
						public void onItemClick(ItemClickEvent event) {
							// load item
							final Map<Integer, ItemStack> Items = new HashMap<Integer, ItemStack>();
							for (int x = 0; x < lrw.length; x++) {
								ItemStack item = lrw[x];
								if (item != null) {
									Items.put(Integer.valueOf(x), item);
								}
							}
							
							// create
							final Container con = new Container(owner, Size.fit(Math.max(GameBoss.getBoss().getRewardsInventorySize(), lrw.length)), Items);
							// save
							MENUS.put(owner, con);
							// open
							con.open(event.getPlayer());
							// update inv
							event.getPlayer().updateInventory();
						}
					}, new WoolItemStack ( WoolColor.WHITE , 1 )));
				}
				
				// add items
				if (!otherItems.isEmpty()) {
					// get max slot
					int maxSlot = 0;
					for (Integer slot : otherItems.keySet()) {
						if (slot != null) {
							if (slot.intValue() >= maxSlot) {
								maxSlot = slot.intValue();
							}
						}
					}
					
					// check and clear
					if (maxSlot > inventory.getSize()) { // to fix
						inventory.clear(); // to fix
					} // to fix
					
					// set item
					for (Integer slot : otherItems.keySet()) {
						if (slot != null) {
							ItemStack item = otherItems.get(slot);
							if (item != null) {
								// check size
								if (maxSlot > inventory.getSize()) {
									inventory.addItem(item);
								}
								else {
									inventory.setItem(slot.intValue(), item);
								}
							}
						}
					}
				}
				// Apply Items
				apply(inventory, player);
				// Open Inventory
				player.openInventory(inventory);
				lastInventory = inventory;
			}
		}
		
		public ItemStack[] checkLoadedRewards() {
			if (GameBoss.getBoss() != null) {
				return GameBoss.getBoss().getRewards();
			}
			return null;
		}
	}
	
	private static class sizeChanger extends ItemMenu {
		public sizeChanger(final Container menu) {
			super(Util.wc("&6Changing Shop Menu Size"), Size.THREE_LINE);
			
			// set items
			int slot = 10;
			for (Size item : Size.values()) {
				setItem(slot, new ActionMenuItem(Util.wc("&6Change to " + item.getSize() + " slots."), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						// get player
						final Player p = event.getPlayer();
						// create
						final Container tor = new Container(menu.owner, item, menu.otherItems);
						// save
						MENUS.put(p.getUniqueId(), tor);
						// open
						tor.open(p);
						// update inv
						p.updateInventory();
						// send changed message
						p.sendMessage(ChatColor.LIGHT_PURPLE + "Size Changed!");
					}
				}, new ItemStack(Material.CHEST, item.getSize())));
				slot ++;
			}
		}
	}
}
