package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.GameStartEvent;
import com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.EnderBrewing;
import com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api.IBrewing;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.EnderFurnace;
import com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api.IFurnace;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

import lombok.Getter;
import lombok.Setter;

public final class AnniPlayer {
	private static final Map<UUID, AnniPlayer> players = new ConcurrentHashMap<UUID, AnniPlayer>();

	public static enum AnniGameMode {
		Spectator, Normal;
	}

	public static AnniPlayer getPlayer(UUID id) {
		if (id != null && players.get(id) == null) {
			PlayerLoader.loadPlayer(id);
		}
		return players.get(id);
	}

	public static AnniPlayer getPlayer(final Player p) {
		if (p != null && p.getUniqueId() != null) {
			return getPlayer(p.getUniqueId());
		}
		return null;
	}

	public static Collection<AnniPlayer> getPlayers() {
		return players.values();
	}

	public static void RegisterListener(final Plugin p) {
		players.clear();
		PlayerLoader l = new PlayerLoader();
		Bukkit.getPluginManager().registerEvents(l, p);
	}

	private static class PlayerLoader implements Listener {
		public PlayerLoader() {
			// If any players are online when this is called (hint: /reload), we load their
			// anniPlayer
			for (Player p : Bukkit.getOnlinePlayers()) {
				loadPlayer(p);
			}
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void playerCheck(PlayerJoinEvent event) {
			final Player p = event.getPlayer();
			if (!exists(p)) {
				loadPlayer(p);
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void enderFuraceManagement(PlayerQuitEvent event) {
			checkLeave(event.getPlayer());
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void enderFuraceManagement(PlayerKickEvent event) {
			checkLeave(event.getPlayer());
		}

		private void checkLeave(Player player) {
			if (Game.isGameRunning()) {
				final AnniPlayer p = getPlayer(player.getUniqueId());
				// Furnace
				if (ServerVersion.getVersion().isOlderThan(ServerVersion.v1_11_R1)) { // !VersionUtils.is1_11() && !VersionUtils.is1_12()
					if (p != null && p.enderfurnace != null) {
						p.setData("ED", p.enderfurnace.getFurnaceData());
						p.enderfurnace = null;
					}
				} else {
					p.setData("ED", null);
					p.enderfurnace = null;
				}

				// Brewing
				EnderBrewing.seveBrewingData(p);
				// p.setData("BD", null);
				// p.enderBrewing = null;
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void enderFuraceManagement(PlayerJoinEvent event) {
			if (Game.isGameRunning()) {
				final AnniPlayer p = getPlayer(event.getPlayer().getUniqueId());
				if (p == null) {
					return;
				}

				if (ServerVersion.getVersion().isOlderThan(ServerVersion.v1_11_R1)) {
					if (p.enderfurnace == null) {
						p.enderfurnace = EnderFurnace.getCreator().createFurnace(p);
					}
				} else {
					p.enderBrewing = EnderBrewing.getCreator().createBrewing(p);
					p.enderBrewing.load(EnderBrewing.getBrewingData(p));
				}
			}
		}

		private boolean exists(final Player p) {
			return players.containsKey(p.getUniqueId());
		}

		private static void loadPlayer(final Player p) {
			final AnniPlayer player = new AnniPlayer(p.getUniqueId(), p.getName());
			players.put(p.getUniqueId(), player);
		}

		private static void loadPlayer(final UUID id) {
			if (id != null) {
				final Player p = Bukkit.getPlayer(id);
				if (p != null) {
					final AnniPlayer player = new AnniPlayer(p.getUniqueId(), p.getName());
					players.put(p.getUniqueId(), player);
				}
			}
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void onGameStart(GameStartEvent event) {
			Iterator<AnniPlayer> ps = players.values().iterator();
			while (ps.hasNext()) {
				AnniPlayer player = ps.next();
				if (player.isOnline()) {
					player.enderfurnace = EnderFurnace.getCreator().createFurnace(player);
					player.enderBrewing = EnderBrewing.getCreator().createBrewing(player);
				} else {
					player.enderfurnace = null;
					player.enderBrewing = null;
				}
			}
			new FurnaceTick().runTaskTimer(AnnihilationMain.INSTANCE, 3L, 3L);
		}
	}

	private static class FurnaceTick extends BukkitRunnable {
		@Override
		public void run() {
			if (Game.isGameRunning()) {
				for (AnniPlayer player : AnniPlayer.getPlayers()) {
					if (player == null) {
						continue;
					}

					// Tick Furnace
					IFurnace f = player.enderfurnace;
					if (f != null) {
						f.tick();
					}

					// Tick Brewing
					if (player.hasEnderBrewing()) {
						player.getEnderBrewing().tick();
					}
				}
			} else
				this.cancel();
		}
	}

	private final UUID id;
	private final String name;

	private Map<Object, Object> data;
	private AnniTeam team;
	private Kit kit;
	private @Setter IFurnace enderfurnace;
	private @Setter IBrewing enderBrewing;
	private AnniGameMode mode;
	private @Getter @Setter boolean isResurrected = false;

	private AnniPlayer(UUID ID, String Name) {
		id = ID;
		name = Name;
		team = null;
		kit = Kit.CivilianInstance;
		enderfurnace = null;
		enderBrewing = null;
		mode = AnniGameMode.Normal;
	}

	public String getName() {
		return name;
	}

	public UUID getID() {
		return id;
	}

	public void openFurnace() {
		if (enderfurnace != null) {
			enderfurnace.open();
		}
	}

	public void openBrewing() {
		if (enderBrewing != null) {
			enderBrewing.open();
		}
	}

	public Object getData(Object key) {
		if (data == null)
			return null;
		return data.get(key);
	}

	public void setData(String key, Object value) {
		if (data == null) {
			data = new HashMap<Object, Object>();
		}
		data.put(key, value);
	}

	public void setTeam(AnniTeam t) {
		team = t;
		// String playerName = name;
		// String name = playerName.length() > 14 ? playerName.substring(0, 14) :
		// playerName;
		//
		/*
		 * if(t == null) { Player p = this.getPlayer(); if(p != null)
		 * p.setPlayerListName(ChatColor.WHITE+name); return; } // try { Player p =
		 * this.getPlayer(); if(p != null) { p.setPlayerListName(t.getFixPrefix() +
		 * name); //t.getColor() + name } } catch(IllegalArgumentException e1) { Random
		 * rnd = new Random(); name = (name.length() > 11 ? name.substring(0, 11) :
		 * name) + "" + rnd.nextInt(9); try { Player p = this.getPlayer(); if(p != null)
		 * { p.setPlayerListName(t.getColor() + name); } }
		 * catch(IllegalArgumentException e2) {
		 * Bukkit.getLogger().warning("[Annihilation] setPlayerListName error: " +
		 * e2.getMessage()); } }
		 */
	}

	public AnniTeam getTeam() {
		return team;
	}

	public void setKit(Kit kit) {
		if (kit != null) {
			this.kit = kit;
		}
	}

	public void sendMessage(String message) {
		final Player p = Bukkit.getPlayer(id);
		if (p != null && p.isOnline()) {
			p.sendMessage(message);
		}
	}

	public Kit getKit() {
		return kit;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(id);
	}

	public boolean isOnline() {
		return getPlayer() != null && getPlayer().isOnline();
	}

	public void setGameMode(AnniGameMode newmode) {
		mode = newmode;
	}

	public AnniGameMode getAnniGameMode() {
		return mode;
	}

	public boolean isSpectator() {
		if (mode == AnniGameMode.Spectator) {
			return true;
		}
		return false;
	}

	public boolean hasTeam() {
		return team != null;
	}

	public boolean hasKit() {
		return kit != null;
	}

	public boolean hasEnderFurnace() {
		return enderfurnace != null;
	}

	public IFurnace getEnderFurnace() {
		return enderfurnace;
	}

	public boolean hasEnderBrewing() {
		return enderBrewing != null;
	}

	public IBrewing getEnderBrewing() {
		return enderBrewing;
	}

	public boolean isOnDeathLobby() {
		return isOnline() && hasTeam() && team.isTeamDead() && KitUtils.isOnLobby(getPlayer());
	}

	public Map<Object, Object> getData() {
		return data;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnniPlayer) {
			AnniPlayer p = (AnniPlayer) obj;
			return id.equals(p.id);
		} else
			return false;
	}
}