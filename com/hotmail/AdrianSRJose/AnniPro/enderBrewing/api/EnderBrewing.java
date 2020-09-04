package com.hotmail.AdrianSRJose.AnniPro.enderBrewing.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public final class EnderBrewing {
	private static BrewingCreator creator;
	public static Map<UUID, BrewingData> data = new HashMap<UUID, BrewingData>();

	private EnderBrewing() {
	}

	public static BrewingCreator getCreator() {
		if (creator == null) {
			String name = "com.hotmail.AdrianSRJose.AnniPro.enderBrewing.versions." + ServerVersion.getVersion().name()
					+ ".Creator";
			Class<?> rawClass = null;
			try {
				rawClass = Class.forName(name);
				Class<? extends BrewingCreator> furnaceClass = rawClass.asSubclass(BrewingCreator.class);
				Constructor<? extends BrewingCreator> constructor = furnaceClass.getConstructor();
				creator = constructor.newInstance();
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
					| IllegalAccessException e) {
				creator = new TempCreator();
			}
		}
		return creator;
	}

	public static BrewingData getBrewingData(AnniPlayer player) {
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_11_R1)) { // VersionUtils.getIntVersion() >= 11
			return data.get(player.getID());
		}

		Object obj = player.getData("BD");
		if (obj == null) {
			return null;
		}
		return (BrewingData) obj;
	}

	public static void seveBrewingData(AnniPlayer player) {
		if (player == null || player.getEnderBrewing() == null || player.getEnderBrewing().getBrewingData() == null) {
			return;
		}

		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_11_R1)) { // VersionUtils.getIntVersion() >= 11
			data.put(player.getID(), player.getEnderBrewing().getBrewingData());
		}
	}

	private static class TempCreator implements BrewingCreator {
		@Override
		public IBrewing createBrewing(final AnniPlayer player) {
			return new TempFurnace(player);
		}

		private class TempFurnace implements IBrewing {
			
			private final String version;
			private final AnniPlayer   p;

			public TempFurnace(AnniPlayer p) {
				this.p       = p;
				this.version = ServerVersion.getVersion().name();
			}

			@Override
			public void tick() {
			}

			@Override
			public void open() {
				p.sendMessage(
						"Sorry, this server is using an unsupported version for Ender Brewing. Version: " + version);
			}

			@Override
			public void clear() {

			}

			@Override
			public BrewingData getBrewingData() {
				return null;
			}

			@Override
			public void load(final BrewingData data) {
			}
		}
	}
}
