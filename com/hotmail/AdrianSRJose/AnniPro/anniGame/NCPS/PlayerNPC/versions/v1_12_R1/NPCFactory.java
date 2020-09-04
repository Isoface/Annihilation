package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.PlayerNPC.versions.v1_12_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.metadata.PlayerMetadataStore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import com.hotmail.AdrianSRJose.AnniPro.anniEvents.AnniPlayerRespawnEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.EquipmentSlot;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPC;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCDamageEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.NPCDeathEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.PlayerNPCManager;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.PlayerNPC.NPCContainer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_12_R1.NPCEntity;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_12_R1.NPCMetadataStore;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_12_R1.NPCNetworkManager;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_12_R1.NPCProfile;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.Config;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;

/**
 * NPCFactory main class, intializes and creates npcs.
 * 
 * @author lenis0012
 */
public class NPCFactory implements Listener {
	private static final Map<UUID, NPCContainer> CONTAINERS = new HashMap<UUID, NPCContainer>();
	private static final Map<UUID, Double> NPCS_HEALTH = new HashMap<UUID, Double>();
	private static final Map<UUID, UUID> NPC_KILLERS = new HashMap<UUID, UUID>();
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

	private NPC spawnHumanNPC(Location location, final Player owner, final AnniTeam team) {
		// Get World
		World world = location.getWorld();

		// Get Original Profile
		String finalName = Util.shortenString((team.getColor().toString() + Util.remC(owner.getName())), 16);
		GameProfile t = ((CraftPlayer) owner).getProfile();

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
		entity.getBukkitEntity().setMetadata("PLAYER_NPC", new FixedMetadataValue(plugin, true));
		entity.getBukkitEntity().setMetadata("OWNER_UUID",
				new FixedMetadataValue(plugin, owner.getUniqueId().toString()));
		entity.getBukkitEntity().setMetadata(team.getName(), new FixedMetadataValue(plugin, true));
		return entity;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKi(final NPCDeathEvent eve) {
		final LivingEntity npcEntity = eve.getNCP();
		final Player killer = eve.getKiller();
		if (npcEntity == null) {
			return;
		}

		final NPC npc = getNPC(npcEntity);
		if (npc == null) {
			return;
		}

		if (killer != null) {
			try {

				if (npcEntity.hasMetadata("OWNER_UUID")) {
					// Get And Check getted owner id
					final String ownerIDString = npcEntity.getMetadata("OWNER_UUID").get(0).asString();
					final UUID ownerID = UUID.fromString(ownerIDString);
					if (ownerID == null) {
						return;
					}

					// Get and Check Anni Players
					final AnniPlayer ap = AnniPlayer.getPlayer(ownerID);
					final AnniPlayer kp = AnniPlayer.getPlayer(killer);
					if (ap == null || kp == null) {
						return;
					}

					// Save Killer
					NPC_KILLERS.put(ap.getID(), kp.getID());

					// Restore Life
					NPCS_HEALTH.put(ap.getID(), 20.0);

					// Check Container
					NPCContainer cnt = CONTAINERS.get(ownerID);
					if (cnt == null) {
						cnt = new NPCContainer(ownerID);
						CONTAINERS.put(ownerID, cnt);
					}

					// Drop Items
					if (Config.NPC_DROP_ITEMS.toBoolean()) {
						eve.setDrops(Arrays.asList(cnt.getInventoryContents()));
					}

					final OfflinePlayer player = Bukkit.getOfflinePlayer(ownerID);
					if (player != null) {

						String message = "";
						if (player == null || ap == null || !ap.hasTeam()) {
							return;
						}

						// Check is game running
						if (Game.isNotRunning()) {
							return;
						}

						// Send Assert Death Message and call new PlayerKilledEvent
						final EntityDamageEvent lastDamageEvent = npcEntity.getLastDamageCause();
						if (lastDamageEvent != null) {
							// Get and check Cause
							final DamageCause cause = lastDamageEvent.getCause();
							if (cause == null || cause.name() == null) {
								return;
							}

							// Get Message
							final String ptk = ap.getTeam().getColor() + player.getName() + "(" + ap.getKit().getName()
									+ ")";
							if (killer != null) {
								if (cause != DamageCause.VOID) {
									final AnniPlayer k = AnniPlayer.getPlayer(killer.getUniqueId());
									if (k != null && k.isOnline() && k.hasTeam()) {
										// Get Format
										String ktk = k.getTeam().getColor() + killer.getName() + "("
												+ k.getKit().getName() + ")";
										String mesNormalKill = Lang.DEATHPHRASE.toString();
										String mesShotKill = Lang.DEATHPHRASESHOT.toString();

										// Get Assert Death Message
										if (cause.name().equals(DamageCause.ENTITY_ATTACK.name())) {
											Bukkit.broadcastMessage(ktk + " " + mesNormalKill + " " + ptk + message);
											ap.setData("LastDamagerUUID", null);
										}

										if (cause.name().equals(DamageCause.PROJECTILE.name())) {
											Bukkit.broadcastMessage(ktk + " " + mesShotKill + " " + ptk + message);
											ap.setData("LastDamagerUUID", null);
										}

										if (cause.name().equals(DamageCause.FALL.name())) {
											Bukkit.broadcastMessage(ktk + " " + mesNormalKill + " " + ptk + message);
											ap.setData("LastDamagerUUID", null);
										}
									}
								} else {
									if (ap.getData("LastDamagerUUID") != null) {
										final Player damager = Bukkit.getPlayer((UUID) ap.getData("LastDamagerUUID"));
										if (damager != null) {
											final AnniPlayer damagerAp = AnniPlayer.getPlayer(damager.getUniqueId());
											if (damagerAp != null && damagerAp.isOnline() && damagerAp.hasTeam()) {
												// Get Messages
												String mesTrewToVoid = damagerAp.getTeam().getColor()
														+ damager.getName() + "(" + damagerAp.getKit().getName() + ") "
														+ Lang.DEATHPHRASEVOID.toString() + " " + ptk + message;

												// broadcastMessage
												Bukkit.broadcastMessage(mesTrewToVoid);

												// Remove Last Damager Data
												ap.setData("LastDamagerUUID", null);
											}
										}
									} else {
										if (killer != null) {
											// Get And Check Killer
											final Player kp1 = killer;
											final AnniPlayer dk = AnniPlayer.getPlayer(kp1);
											if (kp1 == null || dk == null) {
												return;
											}

											// Set Death Message
											String mesTrewToVoid = dk.getTeam().getColor() + kp1.getName() + "("
													+ dk.getKit().getName() + ") " + Lang.DEATHPHRASEVOID.toString()
													+ " " + ptk + message;

											// broadcastMessage
											Bukkit.broadcastMessage(mesTrewToVoid);

											// Remove Last Damager Data
											ap.setData("LastDamagerUUID", null);
										}
									}
								}
							}
						}
					}
				}
			} catch (Throwable t) {
				// Ignore
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(final PlayerQuitEvent eve) {
		// Check is game Running
		if (Game.isNotRunning()) {
			return;
		}

		// Get and Check Player
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		if (ap != null) {
			// Check Combat
			if (Config.NPC_SPAWN_ONLY_IN_COMBAT.toBoolean()) {
				if (!PlayerNPCManager.isInCombat(p)) {
					return;
				}
			}

			// Save Life
			NPCS_HEALTH.put(ap.getID(), p.getHealth());

			NPCContainer cnt = CONTAINERS.get(p.getUniqueId());
			if (cnt == null) {
				cnt = new NPCContainer(p.getUniqueId());
				CONTAINERS.put(p.getUniqueId(), cnt);
			}

			// Save Armor
			cnt.setArmor(p.getInventory().getArmorContents());

			// Save Sword
			cnt.setItemInHand(p.getInventory().getItemInHand());

			// Save Inventory
			final ItemStack[] inv = p.getInventory().getContents();
			for (int x = 0; x < inv.length; x++) {
				if (KitUtils.isAnySoulbound(inv[x])) {
					inv[x] = new ItemStack(Material.AIR, 1);
				}
			}

			// Set
			cnt.setInventoryContents(inv);

			// Update cnt
			CONTAINERS.put(p.getUniqueId(), cnt);

			// Spawn NPC
			createNPC(ap, eve.getPlayer().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(AnniPlayerRespawnEvent eve) {
		final Player p = eve.getPlayer();
		final AnniPlayer ap = eve.getAnniPlayer();
		if (p == null || ap == null) {
			return;
		}

		// Check has npc
		if (!killedNPC(ap)) {
			return;
		}

		// Set not killed NPC
		setKilledNPC(ap, false);

		// Get and Check Killer
		final UUID killerID = NPC_KILLERS.get(ap.getID());
		if (killerID == null) {
			return;
		}

		// Get and Check Killer AnniPlayer
		final AnniPlayer kp = AnniPlayer.getPlayer(killerID);
		if (kp == null || !kp.hasTeam()) {
			return;
		}

		// Send Killed NPC Message
		p.sendMessage(Lang.NPC_KILLED.toStringPlayerNameReplacement(kp.getTeam().getColor().toString() + kp.getName()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDag(NPCDamageEvent eve) {
		final NPC npc = eve.getNpc();
		if (npc == null) {
			return;
		}

		final LivingEntity ent = npc.getBukkitEntity();
		if (!ent.hasMetadata("OWNER_UUID")) {
			return;
		}

		final UUID id = UUID.fromString(ent.getMetadata("OWNER_UUID").get(0).asString());
		if (id == null) {
			return;
		}

		// Set NPC Health
		NPCS_HEALTH.put(id, ent.getHealth());
	}

	private void setKilledNPC(final AnniPlayer ap, boolean b) {
		if (ap != null) {
			ap.setData("KEY_KILLEDNPC_KEY", Boolean.valueOf(b));
		}
	}

	private boolean killedNPC(final AnniPlayer ap) {
		if (ap != null && ap.getData("KEY_KILLEDNPC_KEY") instanceof Boolean) {
			return ((Boolean) ap.getData("KEY_KILLEDNPC_KEY")).booleanValue();
		}
		return false;
	}

	public void createNPC(final AnniPlayer ap, final Location spawnLoc) {
		// Check
		if (ap == null || !ap.hasTeam()) {
			return;
		}

		// Get Team
		final AnniTeam team = ap.getTeam();

		// Check not null Spawn
		if (spawnLoc == null) {
			return;
		}

		// Get NPC
		final NPC npc = spawnHumanNPC(spawnLoc, ap.getPlayer(), team);

		// Check Container
		NPCContainer cnt = CONTAINERS.get(ap.getID());
		if (cnt == null) {
			cnt = new NPCContainer(ap.getID());
			CONTAINERS.put(ap.getID(), cnt);
		}

		// Set has npc
		setKilledNPC(ap, false);

		// Set Vulnerable
		npc.setGodmode(false);

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
					if (cnt.getItemInHand() != null) {
						conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(), EnumItemSlot.valueOf(slot.toEnumItemSlotString()),
								CraftItemStack.asNMSCopy(cnt.getItemInHand())));
					}
					continue;
				}

				if (cnt.getArmor() != null && cnt.getArmor().length > (slot.getId() - 1)) {
					conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(), EnumItemSlot.valueOf(slot.toEnumItemSlotString()),
							CraftItemStack.asNMSCopy(cnt.getArmor()[slot.getId() - 1])));
				}
			}
		}
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

	public NPC getNPC(World w, final Player p) {
		for (NPC npc : getNPCs(w)) {
			if (npc == null || npc.getBukkitEntity() == null) {
				continue;
			}

			if (p.getUniqueId().toString().equals(npc.getBukkitEntity().getMetadata("OWNER_UUID").get(0).asString())) {
				return npc;
			}
		}
		return null;
	}

	public List<NPC> getNPCs() {
		List<NPC> npcList = new ArrayList<NPC>();
		if (Game.getGameMap() == null) {
			return npcList;
		}
		npcList.addAll(getNPCs(Game.getGameMap().getWorld()));
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

	public boolean isNPC(Entity entity) {
		return entity.hasMetadata("PLAYER_NPC");
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().equals(plugin)) {
			despawnAll();
		}
	}

	public void despawnAll() {
		for (World world : Bukkit.getWorlds()) {
			despawnAll(world);
		}
	}

	public void despawnAll(World world) {
		for (Entity entity : world.getEntities()) {
			if (entity.hasMetadata("PLAYER_NPC")) {
				entity.remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(final PlayerJoinEvent eve) {
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(eve.getPlayer());
		final NPC npc = getNPC(eve.getPlayer().getWorld(), eve.getPlayer());
		if (npc == null) {
			return;
		}

		if (!npc.getBukkitEntity().isDead()) {
			// Teleport to NPC
			p.teleport(npc.getBukkitEntity());

			// Despawn NPC
			despawnNPC(p);

			// Set NPC Health
			if (NPCS_HEALTH.get(p.getUniqueId()) != null) {
				if (Config.NPC_CHANGE_PLAYER_HEALTH_TO_NPC_HEALTH.toBoolean()) {
					p.setHealth(NPCS_HEALTH.get(p.getUniqueId()).doubleValue());
				}
			}
			return;
		}

		// Remove NPC
		despawnNPC(p);

		// WHEN HIS NPC IS KILLED
		setKilledNPC(ap, true);

		// Clear inventory
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.updateInventory();

		// Kill Player
		p.setHealth(0.0D);
	}

	public void despawnNPC(final Player p) {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity.hasMetadata("PLAYER_NPC")) {
					if (AnniPlayer.getPlayer(entity.getUniqueId()) == null) {
						if (entity.hasMetadata("OWNER_UUID")) {
							if (p.getUniqueId().toString().equals(entity.getMetadata("OWNER_UUID").get(0).asString())) {
								// Send Remove Packet
								for (Player pl : Bukkit.getOnlinePlayers()) {
									PlayerConnection conn = ((CraftPlayer) pl).getHandle().playerConnection;
									conn.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,
											(((NPCEntity) getNPC(p.getWorld(), p)))));
								}

								// Remove
								entity.remove();
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (!KitUtils.isValidPlayer(p)) {
			return;
		}

		final NPC pnc = getNPC(p.getWorld(), p);
		for (NPC npc : getNPCs()) {
			if (npc == null) {
				continue;
			}

			if (npc.getBukkitEntity().getMetadata("OWNER_UUID").get(0).asString()
					.equals(pnc.getBukkitEntity().getMetadata("OWNER_UUID").get(0).asString())) {
				continue;
			}

			// Spawn
			PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
			conn.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, (((NPCEntity) npc))));
			conn.sendPacket(new PacketPlayOutNamedEntitySpawn((((NPCEntity) npc))));

			// Head and body rotation
			(((NPCEntity) npc)).teleport(p, (((NPCEntity) npc)).otherLoc);
		}

		// Update Armor
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@Override
			public void run() {
				for (NPC npc : getNPCs()) {
					if (npc == null) {
						continue;
					}

					if (npc.getBukkitEntity().getMetadata("OWNER_UUID").get(0).asString()
							.equals(pnc.getBukkitEntity().getMetadata("OWNER_UUID").get(0).asString())) {
						continue;
					}

					Player other = Bukkit.getPlayer(p.getUniqueId());
					if (KitUtils.isValidPlayer(other)) {
						// Get Player Connection
						PlayerConnection conn = ((CraftPlayer) other).getHandle().playerConnection;

						// Get Container
						NPCContainer cnt = getContainer(
								UUID.fromString(npc.getBukkitEntity().getMetadata("OWNER_UUID").get(0).asString()));

						// Set Armor
						for (EquipmentSlot slot : EquipmentSlot.values()) {
							if (slot == EquipmentSlot.HAND) {
								if (cnt.getItemInHand() != null) {
									conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(),
											EnumItemSlot.valueOf(slot.toEnumItemSlotString()), CraftItemStack.asNMSCopy(cnt.getItemInHand())));
								}
								continue;
							}

							if (cnt.getArmor() != null && cnt.getArmor().length > (slot.getId() - 1)) {
								conn.sendPacket(new PacketPlayOutEntityEquipment((((NPCEntity) npc)).getId(),
										EnumItemSlot.valueOf(slot.toEnumItemSlotString()), CraftItemStack.asNMSCopy(cnt.getArmor()[slot.getId() - 1])));
							}
						}
					}
				}
			}
		}, 5L);
	}

	private NPCContainer getContainer(final UUID id) {
		NPCContainer cnt = CONTAINERS.get(id);
		if (cnt == null) {
			cnt = new NPCContainer(id);
			CONTAINERS.put(id, cnt);
		}

		return cnt;
	}
}
