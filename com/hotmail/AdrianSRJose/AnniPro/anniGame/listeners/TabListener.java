package com.hotmail.AdrianSRJose.AnniPro.anniGame.listeners;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtils;
import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;

public final class TabListener implements Listener {

	public TabListener(final AnnihilationMain plugin) {
		// register events in this class.
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onQuit(final PlayerQuitEvent event) {
		try {
			// get player.
			final Player player = event.getPlayer();
			
			// get player handle.
			final Object handle = player.getClass().getMethod("getHandle").invoke(player);
			
			// server version
			final boolean v8r1 = ServerVersion.getVersion ( ).isOlderEqualsThan ( ServerVersion.v1_8_R1 );
			
			// make packet
			final Class<?>        packet_class = ReflectionUtils.getCraftClass("PacketPlayOutPlayerInfo");
			final Object                packet = packet_class.newInstance();
			final Class<?>        packet_data  = v8r1 ? ReflectionUtils.getCraftClass ( "PlayerInfoData" ) : packet_class.getClasses ( ) [ 0 ];
			final Class<?>        packet_enum  = v8r1 ? ReflectionUtils.getCraftClass ( "EnumPlayerInfoAction" ) : packet_class.getClasses ( ) [ 1 ];
			final Object               profile = handle.getClass().getMethod("getProfile").invoke(handle);
			final int                     ping = handle.getClass().getField("ping").getInt(handle);
			final Object           itc_manager = handle.getClass().getField("playerInteractManager").get(handle);
			final Object              gamemode = itc_manager.getClass().getMethod("getGameMode").invoke(itc_manager);
			final Object             list_name = handle.getClass().getMethod("getPlayerListName").invoke(handle);
			final Constructor<?> constructor_a = packet_data.getConstructors()[0];
			final Object                  data = constructor_a.newInstance(packet, profile, 0, gamemode, list_name);
			final Object            enum_value = packet_enum.getMethod("valueOf", String.class).invoke(packet_enum, "REMOVE_PLAYER");
			ReflectionUtils.getField(packet_class, true, "a").set(packet, enum_value);
			final List list = (List) ReflectionUtils.getField(packet_class, true, "b").get(packet);
			list.add(data);

			// send packet.
			Shedulers.scheduleSync(25, () -> {
				for (Player online : Bukkit.getOnlinePlayers()) {
					try {
						ReflectionUtils.sendPacket(online, packet);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			});
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
}


//package com.hotmail.AdrianSRJose.AnniPro.anniGame.listeners;
//
//import java.lang.reflect.Constructor;
//import java.util.List;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerQuitEvent;
//
//import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
//import com.hotmail.AdrianSRJose.AnniPro.utils.ReflectionUtils;
//import com.hotmail.AdrianSRJose.AnniPro.utils.Shedulers;
//
//public final class TabListener implements Listener {
//
//	public TabListener(final AnnihilationMain plugin) {
//		// register events in this class.
//		Bukkit.getPluginManager().registerEvents(this, plugin);
//	}
//
//	@EventHandler (priority = EventPriority.LOWEST)
//	public void onQuit(final PlayerQuitEvent event) {
//		try {
//			// get player.
//			final Player player = event.getPlayer();
//			
//			// get player handle.
//			final Object handle = player.getClass().getMethod("getHandle").invoke(player);
//			
//			// make packet
//			final Class<?>        packet_class = ReflectionUtils.getCraftClass("PacketPlayOutPlayerInfo");
//			final Object                packet = packet_class.newInstance();
//			final Class<?>        packet_data  = packet_class.getClasses()[0];
//			final Class<?>        packet_enum  = packet_class.getClasses()[1];
//			final Object               profile = handle.getClass().getMethod("getProfile").invoke(handle);
//			final int                     ping = handle.getClass().getField("ping").getInt(handle);
//			final Object           itc_manager = handle.getClass().getField("playerInteractManager").get(handle);
//			final Object              gamemode = itc_manager.getClass().getMethod("getGameMode").invoke(itc_manager);
//			final Object             list_name = handle.getClass().getMethod("getPlayerListName").invoke(handle);
//			final Constructor<?> constructor_a = packet_data.getConstructors()[0];
//			final Object                  data = constructor_a.newInstance(packet, profile, 0, gamemode, list_name);
//			final Object            enum_value = packet_enum.getMethod("valueOf", String.class).invoke(packet_enum, "REMOVE_PLAYER");
//			ReflectionUtils.getField(packet_class, true, "a").set(packet, enum_value);
//			final List list = (List) ReflectionUtils.getField(packet_class, true, "b").get(packet);
//			list.add(data);
//
//			// send packet.
//			Shedulers.scheduleSync(25, () -> {
//				for (Player online : Bukkit.getOnlinePlayers()) {
//					try {
//						ReflectionUtils.sendPacket(online, packet);
//					} catch (Throwable t) {
//						t.printStackTrace();
//					}
//				}
//			});
//		} catch(Throwable t) {
//			t.printStackTrace();
//		}
//	}
//}
