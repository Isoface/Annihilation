package com.hotmail.AdrianSRJose.AnniPro.kits.Menu;

public enum GlassColor {
	WITHE((short) 0), ORANGE((short) 1), MAGENTA((short) 2), BLUE((short) 11), YELLOW((short) 4), GREEN(
			(short) 5), BLACK((short) 15), RED((short) 14), PINK((short) 6), CYAN((short) 9);

	private final short data;

	GlassColor(short data) {
		this.data = data;
	}

	public short shortValue() {
		return data;
	}

	public static GlassColor getGlassColor(String name, GlassColor def) {
		try {
			GlassColor o = GlassColor.valueOf(name.toUpperCase());
			if (o != null) {
				return o;
			}
		} catch (Exception e) {
		}
		return def;
	}
}
