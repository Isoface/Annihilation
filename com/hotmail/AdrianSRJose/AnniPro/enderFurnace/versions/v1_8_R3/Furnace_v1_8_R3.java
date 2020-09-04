package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.IFurnace;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtil;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;

class Furnace_v1_8_R3 extends TileEntityFurnace implements IFurnace {
	private EntityPlayer owningPlayer;

	public Furnace_v1_8_R3(Player p) {
		try {
			EntityPlayer player = ((CraftPlayer) p).getHandle();
			owningPlayer = player;
			world = player.world;
		} catch (Exception e) {
		}

		super.a(Lang.NOMBRE_DEL_HORNO_PRIVADOR.toString());
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
		org.bukkit.block.Furnace furnace = new CraftFurnace(world.getWorld().getBlockAt(0, 0, 0));
		try {
			ReflectionUtil.setValue(furnace, "furnace", this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return furnace;
	}

	@Override
	public void open() {
		owningPlayer.openContainer(this);
	}

	@Override
	public void tick() {
		try {
			c();// this used to be h();
		} catch (Throwable t) {
			// Do nothing since this seems to be a byproduct of having a furnace that doesnt
			// actually exist in the world
		}
	}

	public void setItemStack(int i, ItemStack itemstack) {
		setItem(i, CraftItemStack.asNMSCopy(itemstack));
	}

	public ItemStack getItemStack(int i) {
		return CraftItemStack.asBukkitCopy(getItem(i));
	}

	@Override
	public FurnaceData getFurnaceData() {
		return new FurnaceData(this);
	}

	@Override
	public void load(final com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.FurnaceData data) {
		ItemStack[] items = data.getItems();
		for (int x = 0; x < 3; x++)
			setItem(x, org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(items[x]));
		b(0, data.getBurnTime());
		b(1, data.getTicksForCurrentFuel());
		b(2, data.getCookTime());
	}

	@Override
	public void clear() {
		this.l();
	}
}
