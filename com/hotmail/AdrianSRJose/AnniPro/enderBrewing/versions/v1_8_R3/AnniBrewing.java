package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.versions.v1_8_R3;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniEvent;
import com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.IBrewing;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtil;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.TileEntityBrewingStand;

class AnniBrewing extends TileEntityBrewingStand implements IBrewing {
	private UUID ownerID;

	public AnniBrewing(final Player p) {
		// Get Owner and World
		EntityPlayer entPlayer = ((CraftPlayer) p).getHandle();
		ownerID = p.getUniqueId();
		world = entPlayer.getWorld();

		// Set Custom Name
		super.a(name);
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return true;
	}

	@Override
	public int g() {
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
			c();
		} catch (Throwable t) {
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
		l();
	}

	@Override
	public BrewingData getBrewingData() {
		return new BrewingData(this);
	}

	@Override
	public void load(final com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.BrewingData data) {
		ItemStack[] items = (ItemStack[]) data.getItems();
		for (int x = 0; x < 4; x++)
			setItem(x, org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(items[x]));
		b(0, data.getBrewTime());
	}
}
