package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions.v1_8_R1;

import org.bukkit.craftbukkit.v1_8_R1.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.IFurnace;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtil;

import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.TileEntityFurnace;

class Furnace_V1_8_R1 extends TileEntityFurnace implements IFurnace {
	private EntityPlayer owningPlayer;

	public Furnace_V1_8_R1(Player p) {
		try {
			EntityPlayer player = ((CraftPlayer) p).getHandle();
			owningPlayer = player;
			world = player.world;
		} catch (Exception e) {
		}
		//
		try {
			// ReflectionUtil.setSuperValue(this, "m", "Ender Furnace");
			super.a(Lang.NOMBRE_DEL_HORNO_PRIVADOR.toString());
			// this.a(new BlockPosition(0,0,0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return true;
	}

	@Override
	public int g() // used to be p()
	{
		return 0;
	}

	// @Override
	// public Block q()
	// {
	// return Blocks.FURNACE;
	// }

	@Override
	public InventoryHolder getOwner() {
		// int x = 0;
		// org.bukkit.block.Block b = this.world.getWorld().getBlockAt(x, 0, 0);
		// while(b != null && b.getType() != Material.AIR)
		// b = this.world.getWorld().getBlockAt(++x,0,0);
		// Furnace furnace = new CraftFurnace(b);
		org.bukkit.block.Furnace furnace = new CraftFurnace(world.getWorld().getBlockAt(0, 0, 0));
		try {
			ReflectionUtil.setValue(furnace, "furnace", this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return furnace;
	}

	// as of Minecraft 1.8 the bottom slot of a furnace only allows fuel to be put
	// in it.
	// We want to allow any item to be stored in the bottom slot
	// @Override
	// public boolean b(int i, net.minecraft.server.v1_8_R1.ItemStack itemstack)
	// {
	// Bukkit.getLogger().info("This was called");
	// return i != 2;
	// }

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
			this.setItem(x, org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack.asNMSCopy(items[x]));
		this.b(0, data.getBurnTime());
		this.b(1, data.getTicksForCurrentFuel());
		this.b(2, data.getCookTime());
	}

	@Override
	public void clear() {
		this.l();
	}
}
