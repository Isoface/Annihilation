package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.versions.v1_13_R2;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.IBrewing;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtil;

import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.TileEntityBrewingStand;

class AnniBrewing extends TileEntityBrewingStand implements IBrewing {
	private UUID ownerID;

	public AnniBrewing(final Player p) {
		// Get Owner and World
		EntityPlayer entPlayer = ((CraftPlayer) p).getHandle();
		ownerID = p.getUniqueId();
		world = entPlayer.getWorld();

		// Set Custom Name
		super.setCustomName(ChatSerializer.a("{\"text\":\"" + name + "\"}"));
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
		org.bukkit.block.BrewingStand brewing = new CraftBrewingStand(world.getWorld().getBlockAt(0, 0, 0));
		try {
			ReflectionUtil.setValue(brewing, "brewingStand", this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return brewing;
	}

	@Override
	public void tick() {
		try {
			tick();
		} catch (Throwable t) {
			// ignore.
		}
	}

	@Override
	public void open() {
		final Player p = Bukkit.getPlayer(ownerID);
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}

		final EntityPlayer entP = ((CraftPlayer) p).getHandle();
		if (entP != null) {
			entP.openContainer(this);
			AnniEvent.callEvent(new InventoryOpenEvent(entP.getBukkitEntity().getOpenInventory()));
		}
	}

	@Override
	public void clear() {
		clear();
	}

	@Override
	public BrewingData getBrewingData() {
		return new BrewingData(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(final com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.BrewingData data) {
		// Check
		if (data == null) {
			return;
		}

		// check items and set.
		if (data.getItems() instanceof List) {
			List<ItemStack> items = (List<ItemStack>) data.getItems();
			if (items != null) {
				for (int x = 0; x < 3; x++) {
					if (items.get(x) == null) {
						continue;
					}

					// Set
					this.setItem(x, items.get(x));
				}
			}
		}

		// Set Pro
		this.setProperty(0, data.getBrewTime());
		if (data.getFuelLevel() != -1) {
			this.setProperty(1, data.getFuelLevel());
		}

		update();
	}
}
