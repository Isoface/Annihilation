package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_13_R2;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.EquipmentSlot;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPC;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCAnimation;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCDamageEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCInteractEvent;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityDamageSource;
import net.minecraft.server.v1_13_R2.EntityDamageSourceIndirect;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_13_R2.PacketPlayOutBed;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_13_R2.Pathfinder;
import net.minecraft.server.v1_13_R2.PathfinderNormal;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.WorldServer;

public class NPCEntity extends EntityPlayer implements NPC {
	private boolean entityCollision = true;
	private boolean godmode = true;
	private boolean gravity = true;
	private boolean lying = false;
	private AnniTeam team = null;
	private final Pathfinder pathFinder;
	private org.bukkit.entity.Entity target;
	public Location otherLoc;

	public NPCEntity(World world, Location location, NPCProfile profile, NPCNetworkManager networkManager,
			final AnniTeam team) {
		// Super
		super(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) world).getHandle(), profile.getHandle(),
				new PlayerInteractManager(((CraftWorld) world).getHandle()));

		otherLoc = location;
		// Variables
		this.team = team;
		this.playerConnection = new NPCPlayerConnection(networkManager, this);
		this.fauxSleeping = true;
		this.bukkitEntity = new CraftPlayer((CraftServer) Bukkit.getServer(), this);
		PathfinderNormal normal = new PathfinderNormal();
		normal.a(true);
		this.pathFinder = new Pathfinder(normal);

		WorldServer worldServer = ((CraftWorld) world).getHandle();
		setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		worldServer.addEntity(this);
		worldServer.players.remove(this);
	}

	public void teleport(Player p, Location location) {
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
		setValue(packet, "a", this.getId());
		setValue(packet, "b", location.getX());
		setValue(packet, "c", location.getY());
		setValue(packet, "d", location.getZ());
		setValue(packet, "e", getFixRotation(location.getYaw()));
		setValue(packet, "f", getFixRotation(location.getPitch()));

		setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		headRotation(location.getYaw(), location.getPitch(), p);
		sendPacket(packet, p);
	}

	public void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void headRotation(float yaw, float pitch, Player p) {
		PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(this.getId(), getFixRotation(yaw),
				getFixRotation(pitch), true);
		PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
		setValue(packetHead, "a", this.getId());
		setValue(packetHead, "b", getFixRotation(yaw));

		sendPacket(packet, p);
		sendPacket(packetHead, p);
	}

	public byte getFixRotation(float yawpitch) {
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}

	public void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
		}
	}

	Pathfinder getPathfinder() {
		return pathFinder;
	}

	@Override
	public CraftPlayer getBukkitEntity() {
		return (CraftPlayer) bukkitEntity;
	}

	@Override
	public boolean isGravity() {
		return gravity;
	}

	@Override
	public void setGravity(boolean gravity) {
		this.gravity = gravity;
	}

	@Override
	public boolean isGodmode() {
		return godmode;
	}

	@Override
	public void setGodmode(boolean godmode) {
		this.godmode = godmode;
	}

	@Override
	public boolean isLying() {
		return lying;
	}

	/**
	 * Pathfinding
	 */

	@Override
	public boolean pathfindTo(Location location) {
		return pathfindTo(location, 0.2);
	}

	@Override
	public boolean pathfindTo(Location location, double speed) {
		return pathfindTo(location, speed, 30.0);
	}

	@Override
	public boolean pathfindTo(Location location, double speed, double range) {
		NPCPath path = NPCPath.find(this, location, range, speed);
		return (path) != null;
	}

	/**
	 * Look at functions
	 */

	@Override
	public void setTarget(org.bukkit.entity.Entity target) {
		this.target = target;
		lookAt(target.getLocation());
	}

	@Override
	public org.bukkit.entity.Entity getTarget() {
		return target;
	}

	@Override
	public void lookAt(Location location) {
		setYaw(getLocalAngle(new Vector(locX, 0, locZ), location.toVector()));
	}

	@Override
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	private final float getLocalAngle(Vector point1, Vector point2) {
		double dx = point2.getX() - point1.getX();
		double dz = point2.getZ() - point1.getZ();
		float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
		if (angle < 0) {
			angle += 360.0F;
		}
		return angle;
	}

	/**
	 * Packet methods
	 */
	@Override
	public void setLying(double x, double y, double z) {
		if (!lying) {
			broadcastLocalPacket(new PacketPlayOutBed(this, new BlockPosition(this)));
			lying = true;
		} else if (((Double) x == null && (Double) y == null && (Double) z == null) && lying) {
			broadcastLocalPacket(new PacketPlayOutAnimation(this, 2));
			lying = false;
		}
	}

	@Override
	public void playAnimation(NPCAnimation animation) {
		broadcastLocalPacket(new PacketPlayOutAnimation(this, animation.getId()));
	}

	@Override
	public void setEquipment(EquipmentSlot slot, ItemStack item) {
		broadcastLocalPacket(new PacketPlayOutEntityEquipment(getId(),
				EnumItemSlot.valueOf(slot.toEnumItemSlotString()), CraftItemStack.asNMSCopy(item)));
	}

	private final int RADIUS = Bukkit.getViewDistance() * 16;

	private final void broadcastLocalPacket(Packet<?> packet) {
		for (Player p : getBukkitEntity().getWorld().getPlayers()) {
			if (getBukkitEntity().getLocation().distanceSquared(p.getLocation()) <= RADIUS * RADIUS) {
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}

	/**
	 * Internal methods
	 */

	// @SuppressWarnings("deprecation")
	// @Override
	// public void i()
	// {
	// super.i();
	// this.getSpecatorTarget();
	//
	// if (target != null && path == null) {
	// if (target.isDead() || (target instanceof Player && !((Player)
	// target).isOnline())) {
	// this.target = null;
	// } else if
	// (getBukkitEntity().getLocation().getWorld().equals(target.getWorld())
	// && getBukkitEntity().getLocation().distanceSquared(target.getLocation()) <=
	// 32 * 32) {
	// lookAt(target.getLocation());
	// }
	// }
	// if (path != null) {
	// if (!path.update()) {
	// this.path = null;
	// }
	// }
	//
	// IBlockData data = world.getType(new BlockPosition(this));
	// Block block = data.getBlock();
	// if (block.q(data) == Material.FIRE) {
	// setOnFire(15);
	// }
	//
	// // Apply velocity etc.
	// this.motY = onGround ? Math.max(0.0, motY) : motY;
	// move(EnumMoveType.SELF, motX, motY, motZ);
	// this.motX *= 0.800000011920929;
	// this.motY *= 0.800000011920929;
	// this.motZ *= 0.800000011920929;
	// if (gravity && !this.onGround) {
	// this.motY -= 0.1; // Most random value, don't judge.
	// }
	// }

	@Override
	public boolean a(EntityHuman entity) {
		NPCInteractEvent event = new NPCInteractEvent(this, getBukkitEntity());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}

		return super.a(entity);
	}

	@Override
	public boolean damageEntity(DamageSource source, float damage) {
		if (godmode || noDamageTicks > 0) {
			return false;
		}

		DamageCause cause = null;
		org.bukkit.entity.Entity bEntity = null;
		if (source instanceof EntityDamageSource) {
			Entity damager = source.getEntity();
			cause = DamageCause.ENTITY_ATTACK;
			if (source instanceof EntityDamageSourceIndirect) {
				damager = ((EntityDamageSourceIndirect) source).getProximateDamageSource();
				if (damager.getBukkitEntity() instanceof ThrownPotion) {
					cause = DamageCause.MAGIC;
				} else if (damager.getBukkitEntity() instanceof Projectile) {
					cause = DamageCause.PROJECTILE;
				}
			}

			bEntity = damager.getBukkitEntity();
		} else if (source == DamageSource.FIRE)
			cause = DamageCause.FIRE;
		else if (source == DamageSource.STARVE)
			cause = DamageCause.STARVATION;
		else if (source == DamageSource.WITHER)
			cause = DamageCause.WITHER;
		else if (source == DamageSource.STUCK)
			cause = DamageCause.SUFFOCATION;
		else if (source == DamageSource.DROWN)
			cause = DamageCause.DROWNING;
		else if (source == DamageSource.BURN)
			cause = DamageCause.FIRE_TICK;
		else if (source == CraftEventFactory.MELTING)
			cause = DamageCause.MELTING;
		else if (source == CraftEventFactory.POISON)
			cause = DamageCause.POISON;
		else if (source == DamageSource.MAGIC) {
			cause = DamageCause.MAGIC;
		} else if (source == DamageSource.OUT_OF_WORLD) {
			cause = DamageCause.VOID;
		}

		if (cause != null) {
			NPCDamageEvent event = new NPCDamageEvent(this, bEntity, cause, damage);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				playAnimation(NPCAnimation.DAMAGE);
				return super.damageEntity(source, (float) event.getDamage());
			} else {
				return false;
			}
		}

		if (super.damageEntity(source, damage)) {
			if (bEntity != null) {
				Entity e = ((CraftEntity) bEntity).getHandle();
				double d0 = e.locX - this.locX;

				double d1;
				for (d1 = e.locZ - this.locZ; d0 * d0 + d1 * d1 < 0.0001D; d1 = (Math.random() - Math.random())
						* 0.01D) {
					d0 = (Math.random() - Math.random()) * 0.01D;
				}

				a(e, damage, d0, d1);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean getEntityCollision() {
		return this.entityCollision;
	}

	@Override
	public void setEntityCollision(boolean entityCollision) {
		this.entityCollision = entityCollision;
	}

	@Override
	public void f(double x, double y, double z) {
		if (getBukkitEntity() != null && getEntityCollision()) {
			super.f(x, y, z);
			return;
		}
	}

	@Override
	public AnniTeam getTeam() {
		return team;
	}
}
