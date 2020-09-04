package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;

public final class EnderFurnace {
	private static FurnaceCreator creator;

	private EnderFurnace() {
	}

	public static FurnaceCreator getCreator() {
		if (creator == null) {
			String    version = ServerVersion.getVersion().name();
			String       name = "com.hotmail.AdrianSRJose.AnniPro.enderFurnace.versions." + version + ".FurnaceCreator";
			Class<?> rawClass = null;
			try {
				rawClass = Class.forName(name);
				Class<? extends FurnaceCreator> furnaceClass = rawClass.asSubclass(FurnaceCreator.class);
				Constructor<? extends FurnaceCreator> constructor = furnaceClass.getConstructor();
				creator = constructor.newInstance();
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
					| IllegalAccessException e) {
				creator = new TempCreator();
			}
		}
		return creator;
	}

	public static FurnaceData getFurnaceData(AnniPlayer player) {
		Object obj = player.getData("ED");
		if (obj == null)
			return null;
		return (FurnaceData) obj;
	}

	private static class TempCreator implements FurnaceCreator {
		@Override
		public IFurnace createFurnace(final AnniPlayer player) {
			return new TempFurnace(player);
		}

		private class TempFurnace implements IFurnace {
			
			private final String version;
			private final   AnniPlayer p;

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
						"Sorry, this server is using an unsupported version for Ender Furnaces. Version: " + version);
			}

			@Override
			public void clear() {

			}

			@Override
			public FurnaceData getFurnaceData() {
				return null;
			}

			@Override
			public void load(final FurnaceData data) {
			}
		}
	}
}
