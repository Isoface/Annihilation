package com.hotmail.AdrianSRJose.AnniPro.announcementBar.versions.v1_8_R3;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.MobEffectList;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class WBar {
	private EntityWither wit;
	private Player owner;
	private final double dist;

	public WBar(Player player, String mess, float h) {
		WorldServer w = ((CraftWorld) player.getWorld()).getHandle();

		wit = new EntityWither(w);
		owner = player;
		wit.setCustomName(mess);
		wit.setHealth(h);
		wit.setInvisible(true);
		wit.motX = 0;
		wit.motY = 0;
		wit.motZ = 0;
		
		wit.attachedToPlayer = false;
		dist = getDistance(player);
		Location loc = getWitherLoc(player, dist);
		wit.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		wit.removeAllEffects();
		wit.getBukkitEntity().setMetadata("WitBar", new FixedMetadataValue(AnnihilationMain.INSTANCE, "WitBar"));
		wit.addEffect(new MobEffect(MobEffectList.INVISIBILITY.getId(), Integer.MAX_VALUE));
		Wither wither = (Wither) wit.getBukkitEntity();
		wither.setCanPickupItems(false);

		// Watcher Datas
		wit.getDataWatcher().watch(0, (byte) 0x20);
		wit.getDataWatcher().watch(2, mess);
		wit.getDataWatcher().watch(3, (byte) 0);
		wit.getDataWatcher().watch(6, h);
		wit.getDataWatcher().watch(8, (byte) 0);
		wit.getDataWatcher().watch(17, (int) 0);
		wit.getDataWatcher().watch(18, (int) 0);
		wit.getDataWatcher().watch(19, (int) 0);
		wit.getDataWatcher().watch(20, (int) 881);
		
		PacketPlayOutSpawnEntityLiving pack = new PacketPlayOutSpawnEntityLiving(wit);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(pack);
	}
	
	private double getDistance(final Player p) {
		if (AnnihilationMain.versionHandler != null) {
			int ver = AnnihilationMain.versionHandler.getVersion(p);
			return ver > 47 ? 35 : 20;
		}
		return 20;
	}

	public Wither getWither() {
		return (Wither) wit.getBukkitEntity();
	}

	public void setProgress(float progress, String name) {
		// check wither.
		if (wit == null) {
			return;
		}

		// check owner.
		if (owner == null) {
			return;
		}

		// set progress float
		if (progress > 0.0F) {
			wit.setHealth(progress * wit.getMaxHealth());
		}
		
		// set name.
		wit.setCustomName(name);
		
		// send update metadata packet.
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wit.getId(), wit.getDataWatcher(), true);
		((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
	}

	public void Teleport() {
		if (wit == null)
			return;

		if (owner == null) {
			return;
		}

		Location loc = getWitherLoc(owner, dist); // 35

		PacketPlayOutEntityTeleport pack = new PacketPlayOutEntityTeleport(wit.getId(), loc.getBlockX() * 32,
				loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360),
				(byte) ((int) loc.getPitch() * 256 / 360), false);
		((CraftPlayer) owner).getHandle().playerConnection.sendPacket(pack);
	}

	// private Location getWitLoc(Location loc) {
	// Location s = FindCood.getFindCood2D(owner, 50);
	// return s = FindCood.findCoodY(s, owner.getLocation().getPitch());
	// }

	private Location getWitherLoc(final Player p, double distance) {
		// get direction of length 1. This is the direction the player is looking in
		Vector direction = p.getEyeLocation().getDirection().multiply(distance).clone()
				.add(new Vector(0.0D, -1.0D, 0.0D));

		// add direction to the player's head location
		return p.getEyeLocation().clone().add(direction).add(0, 6.0D, 0);
	}

	public void remove() {
		if (wit == null) {
			return;
		}

		if (owner == null) {
			return;
		}

		PacketPlayOutEntityDestroy pack = new PacketPlayOutEntityDestroy(wit.getId());
		((CraftPlayer) owner).getHandle().playerConnection.sendPacket(pack);
	}
}
