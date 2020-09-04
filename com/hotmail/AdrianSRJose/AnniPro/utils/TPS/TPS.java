package com.hotmail.AdrianSRJose.AnniPro.utils.TPS;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;

public final class TPS {
	
	/**
	 * Global IPTS.
	 */
	private static ITPS ITPS;

	private TPS() {
	}
	
	/**
	 * @return current server TPS.
	 */
	public static Long getTPS() {
		String[] artps = TPS.getCreator().executeTPSQuery().split(",");
		return Long.valueOf((long) (Double.valueOf(artps[0].trim()).doubleValue()));
	}

	/**
	 * @return a ITPS
	 */
	private static ITPS getCreator() {
		if (ITPS == null) {
			String version = ServerVersion.getVersion().name();
			String name = "com.hotmail.AdrianSRJose.AnniPro.utils.TPS.versions." + version + ".TPSGetter";
			Class<?> rawClass = null;
			try {
				rawClass = Class.forName(name);
				Class<? extends ITPS> furnaceClass = rawClass.asSubclass(ITPS.class);
				Constructor<? extends ITPS> constructor = furnaceClass.getConstructor();
				ITPS = constructor.newInstance();
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
					| IllegalAccessException e) {
				ITPS = null;
			}
		}
		return ITPS;
	}

	/**
	 * @param tps getted tps from query.
	 * @return the tps with a format.
	 */
	public static String format(double tps) {
		return ((tps > 18.0) ? "" : (tps > 16.0) ? "" : "").toString() + ((tps > 20.0) ? "" : "")
				+ Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
	}
}
