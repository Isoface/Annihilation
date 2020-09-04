package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_11_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.metadata.PlayerMetadataStore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJoinTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.EquipmentSlot;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPC;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.CompatibleUtils.CompatibleParticles;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_11_R1.ChatComponentText;
import net.minecraft.server.v1_11_R1.EnumItemSlot;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_11_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_11_R1.PlayerConnection;

/**
 * NPCFactory main class, intializes and creates npcs.
 *
 * @author lenis0012
 */
public class NPCFactory implements Listener {
	private final Plugin plugin;
	private final NPCNetworkManager networkManager;

	public NPCFactory(Plugin plugin) {
		this.plugin = plugin;
		this.networkManager = new NPCNetworkManager();

		// See #19
		CraftServer server = (CraftServer) Bukkit.getServer();
		try {
			Field field = server.getClass().getDeclaredField("playerMetadata");
			field.setAccessible(true);
			PlayerMetadataStore metadata = (PlayerMetadataStore) field.get(server);
			if (!(metadata instanceof NPCMetadataStore)) {
				field.set(server, new NPCMetadataStore());
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public NPC spawnHumanNPC(Location location, final Player owner, final AnniTeam team) {
		// Get World
		World world = location.getWorld();

		// Get Original Profile
		final String finalName = Util.shortenString((team.getColor().toString() + Util.remC(owner.getName())), 16);
		GameProfile t = ((CraftPlayer) owner).getProfile(); // NPCProfile.loadProfile("Thevampirion").getHandle();

		// Create new Profile
		GameProfile gp = new GameProfile(UUID.randomUUID(), finalName);

		// Get Original Profile skin
		for (String key : t.getProperties().asMap().keySet()) {
			if (!key.equals("textures")) {
				continue;
			}

			Collection<Property> p = t.getProperties().asMap().get(key);
			gp.getProperties().replaceValues(key, p);
		}

		// Spawn and modifies
		NPCEntity entity = new NPCEntity(world, location, new NPCProfile(gp), networkManager, team);
		entity.displayName = finalName;
		entity.listName = new ChatComponentText(team.getColor().toString() + "_");
		entity.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(plugin, true));
		entity.getBukkitEntity().setMetadata(team.getName(), new FixedMetadataValue(plugin, true));
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
			CompatibleParticles.CLOUD.displayNewerVersions().display(0.4F, 0.7F, 0.4F, 0.1F, 30, location.clone().add(0.0D, 0.5D, 0.0D),
					Integer.MAX_VALUE);
		} else {
			CompatibleParticles.CLOUD.displayOlderVersions().display(0.4F, 0.7F, 0.4F, 0.1F, 30, location.clone().add(0.0D, 0.5D, 0.0D),
					Integer.MAX_VALUE);
		}
		entity.getBukkitEntity().getWorld().playSound(location,
				ServerVersion.getVersion().isNewerEqualsThan(ServerVersion.v1_9_R1) 
				? Sound.ENTITY_ITEM_PICKUP : Sound.valueOf("ITEM_PICKUP"), 4f, 1f);
		return entity;
	}

	@EventHandler // test
	public void onJoinTeam(PlayerJoinTeamEvent eve) {
		if (Game.LobbyMap == null) {
			return;
		}

		// Check
		final AnniPlayer ap = eve.getPlayer();
		final AnniTeam team = eve.getTeam();
		if (!KitUtils.isValidPlayer(ap) || team == null) {
			return;
		}

		// Get Player and check is on lobby
		final Player p = ap.getPlayer();
		if (!KitUtils.isOnLobby(p)) {
			return;
		}

		// Remove Old
		final NPC old = getNPC(p.getWorld(), p.getName());
		if (old != null) {
			despawn(old);
		}

		// Check is not full Slots
		if (isFullSlots(p.getWorld(), team)) {
			return;
		}

		// Spawn NPC
		createNPC(p, team);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent eve) {
		despawn(getNPC(eve.getPlayer().getWorld(), eve.getPlayer().getName()));
	}

	@EventHandler
	public void onKick(PlayerKickEvent eve) {
		despawn(getNPC(eve.getPlayer().getWorld(), eve.getPlayer().getName()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLe(PlayerLeaveTeamEvent eve) {
		if (eve.getPlayer() != null && eve.getPlayer().isOnline()) {
			despawn(getNPC(eve.getPlayer().getPlayer().getWorld(), eve.getPlayer().getName()));
		}
	}

	private void createNPC(final Player p, final AnniTeam team) {
		// Get Spawn
		Loc spawnLoc = null;
		for (Loc l : Game.LobbyMap.getNPCsPositions(team)) {
			if (!isUsedLoc(team, l)) {
				spawnLoc = l;
			}
		}

		// Check not null Spawn
		if (spawnLoc == null || spawnLoc.toLocation() == null) {
			return;
		}

		// Get NPC
		final NPC npc = spawnHumanNPC(spawnLoc.toLocation(), p, team);

		// Spawn Packet
		for (Player pl : Bukkit.getOnlinePlayers()) {
			// Spawn
			PlayerConnection conn = ((CraftPlayer) pl).getHandle().playerConnection;
			conn.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, (((NPCEntity) npc))));
			conn.sendPacket(new PacketPlayOutNamedEntitySpawn((((NPCEntity) npc))));

			// Head and body rotation
			(((NPCEntity) npc)).teleport(pl, (((NPCEntity) npc)).otherLoc);

			// Set Armor
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot == EquipmentSlot.HAND) {
					continue;
				}

				conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(),
						EnumItemSlot.valueOf(slot.toEnumItemSlotString()),
						CraftItemStack.asNMSCopy(Loadout.coloredArmor(team)[slot.getId() - 1])));
			}
		}

		// Remove Packet
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					PlayerConnection conn = ((CraftPlayer) pl).getHandle().playerConnection;
					conn.sendPacket(
							new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, (((NPCEntity) npc))));
				}
			}
		}.runTaskLater(plugin, 60L);
	}

	private boolean isFullSlots(final World w, final AnniTeam team) {
		return getNPCs(team, w).size() >= Game.LobbyMap.getNPCsPositions(team).size();
	}

	private boolean isUsedLoc(final AnniTeam team, final Loc l) {
		for (NPC npc : getNPCs(team, l.getBukkitWorld())) {
			if (npc == null || npc.getBukkitEntity() == null || npc.getBukkitEntity().isDead()) {
				continue;
			}

			Loc loc = new Loc(npc.getBukkitEntity().getLocation(), true);
			if (loc.getBlockX() == l.getBlockX() && loc.getBlockY() == l.getBlockY()
					&& loc.getBlockZ() == l.getBlockZ()) {
				return true;
			}
		}
		return false;
	}

	public NPC getNPC(World w, String name) {
		for (NPC npc : getNPCs(w)) {
			if (npc == null || npc.getBukkitEntity() == null) {
				continue;
			}

			if (Util.remC(name).startsWith(Util.remC(npc.getBukkitEntity().getName()))) {
				return npc;
			}
		}
		return null;
	}

	public void despawn(NPC npc) {
		if (npc == null || npc.getBukkitEntity() == null) {
			return;
		}

		for (Entity ent : npc.getBukkitEntity().getWorld().getEntities()) {
			if (ent == null || !isNPC(ent)) {
				continue;
			}

			if (ent.getUniqueId().equals(npc.getBukkitEntity().getUniqueId())) {
				ent.remove();
			}
		}
	}

	public List<NPC> getNPCs(AnniTeam team, World world) {
		List<NPC> npcList = new ArrayList<NPC>();
		for (Entity entity : world.getEntities()) {
			if (entity == null) {
				continue;
			}

			if (isNPC(entity) && entity.hasMetadata(team.getName())) {
				npcList.add(getNPC(entity));
			}
		}

		return npcList;
	}

	public NPC getNPC(Entity entity) {
		if (!isNPC(entity)) {
			return null;
		}

		try {
			return (NPCEntity) ((CraftEntity) entity).getHandle();
		} catch (Exception ignored) {
			return null;
		}
	}

	public List<NPC> getNPCs() {
		List<NPC> npcList = new ArrayList<NPC>();
		for (World world : Bukkit.getWorlds()) {
			npcList.addAll(getNPCs(world));
		}

		return npcList;
	}

	public List<NPC> getNPCs(World world) {
		List<NPC> npcList = new ArrayList<NPC>();
		for (Entity entity : world.getEntities()) {
			if (isNPC(entity)) {
				npcList.add(getNPC(entity));
			}
		}

		return npcList;
	}

	public boolean isNPC(Entity entity) {
		return entity.hasMetadata("NPC");
	}

	public void despawnAll() {
		for (World world : Bukkit.getWorlds()) {
			despawnAll(world);
		}
	}

	public void despawnAll(World world) {
		for (Entity entity : world.getEntities()) {
			if (entity.hasMetadata("NPC")) {
				entity.remove();
			}
		}
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().equals(plugin)) {
			despawnAll();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}

		final UUID id = p.getUniqueId();

		for (NPC npc : getNPCs()) {
			// Spawn
			PlayerConnection conn = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;
			conn.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, (((NPCEntity) npc))));
			conn.sendPacket(new PacketPlayOutNamedEntitySpawn((((NPCEntity) npc))));

			// Head and body rotation
			(((NPCEntity) npc)).teleport(p, (((NPCEntity) npc)).otherLoc);

			// Set Armor
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot == EquipmentSlot.HAND) {
					continue;
				}

				conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(),
						EnumItemSlot.valueOf(slot.toEnumItemSlotString()),
						CraftItemStack.asNMSCopy(Loadout.coloredArmor(npc.getTeam())[slot.getId() - 1])));
			}
		}

		// Update Armor
		new BukkitRunnable() {
			@Override
			public void run() {
				for (NPC npc : getNPCs()) {
					Player other = Bukkit.getPlayer(id);
					if (KitUtils.isValidPlayer(other)) {
						PlayerConnection conn = ((CraftPlayer) other).getHandle().playerConnection;
						for (EquipmentSlot slot : EquipmentSlot.values()) {
							if (slot == EquipmentSlot.HAND) {
								continue;
							}

							conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(),
									EnumItemSlot.valueOf(slot.toEnumItemSlotString()),
									CraftItemStack.asNMSCopy(Loadout.coloredArmor(npc.getTeam())[slot.getId() - 1])));
						}
					}
				}
			}
		}.runTaskLater(plugin, 10L);

		// Remove
		new BukkitRunnable() {
			@Override
			public void run() {
				for (NPC npc : getNPCs()) {
					Player other = Bukkit.getPlayer(id);
					if (KitUtils.isValidPlayer(other)) {
						PlayerConnection conn = ((CraftPlayer) other).getHandle().playerConnection;
						conn.sendPacket(
								new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, (((NPCEntity) npc))));
					}
				}
			}
		}.runTaskLater(plugin, 60L);
	}
}
