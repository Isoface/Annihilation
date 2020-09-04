package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_12_R1;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.TileEntityFurnace;

public class Furnace implements Listener {
	public static HashMap<UUID, TileFurnace> furnaces;
	// public static HashMap<UUID, List<ItemStack>> stacks;

	public static Furnace instance;

	public Furnace(Player p) {
		instance = this;

		if (furnaces == null)
			furnaces = new HashMap<UUID, TileFurnace>();

		// if (stacks == null)
		// stacks = new HashMap<UUID, List<ItemStack>>();

		EntityPlayer player = ((CraftPlayer) p).getHandle();

		if (!furnaces.containsKey(p.getUniqueId()) || furnaces.get(p.getUniqueId()) == null)
			furnaces.put(p.getUniqueId(), new TileFurnace(player));

		TileEntityFurnace toOpen = furnaces.get(p.getUniqueId());

		if (toOpen.getOwner() != null)

			player.openContainer(toOpen);
	}

	public static void clear(AnniPlayer ap) {
		if (ap == null || ap.getID() == null) {
			return;
		}

		TileFurnace fur = furnaces.get(ap.getID());
		if (fur != null) {
			fur.clear();
			furnaces.put(ap.getID(), fur);
		}
	}

	class TileFurnace extends TileEntityFurnace {
		public TileFurnace instance;

		public TileFurnace getInstance() {
			return instance;
		}

		public TileFurnace(EntityPlayer pl) {
			world = pl.getWorld();
			super.setCustomName(Lang.NOMBRE_DEL_HORNO_PRIVADOR.toString());

			Bukkit.getScheduler().runTaskTimer(AnnihilationMain.INSTANCE, new Runnable() {
				@Override
				public void run() {
					tick();
				}
			}, 3L, 3L);
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
		}

		@Override
		public int h() {
			return 0;
		}

		@Override
		public InventoryHolder getOwner() {
			return new InventoryHolder() {
				@Override
				public Inventory getInventory() {
					return new CraftInventoryFurnace(TileFurnace.this);
				}
			};
		}

		public void tick() {
			try {
				e();
			} catch (Throwable t) {

			}
		}
	}
}