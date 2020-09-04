package com.hotmail.AdrianSRJose.AnniPro.enderFurnace.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class UFData {
	private final AnniPlayer owner;
	private final Object furnace;

	public UFData(AnniPlayer ap, Object furnaceObject) {
		owner = ap;
		this.furnace = furnaceObject;
	}

	public void save() {
		if (owner != null) {
			try {
				Method m = Util.getMethod(Util.getCraftClass("TileEntityFurnace"), "getProperty",
						new Class<?>[] { int.class });
				Method its = Util.getMethod(Util.getCraftClass("TileEntityFurnace"), "getContents", new Class<?>[] {});
				//
				owner.setData("BurnTime", m.invoke(furnace, 0));
				owner.setData("CookTime", m.invoke(furnace, 2));
				owner.setData("CookTimeTotal", m.invoke(furnace, 3));
				//
				List<?> ls = (List<?>) its.invoke(furnace);
				owner.setData("Items", ls);
				//
				owner.setData("Player_FData", this);
			} catch (IllegalAccessException ParamException) {
				ParamException.printStackTrace();
			} catch (IllegalArgumentException ParamException) {
				ParamException.printStackTrace();
			} catch (InvocationTargetException ParamException) {
				ParamException.printStackTrace();
			}
		}
	}

	public void load(Object anotherFurnaceObject) {
		try {
			if (anotherFurnaceObject != null && owner != null) {
				Class<?> furClass = Util.getCraftClass("TileEntityFurnace");
				Method setProperty = Util.getMethod(furClass, "setProperty", new Class<?>[] { int.class, int.class });
				// ---
				Object burntime = owner.getData("BurnTime");
				if (burntime != null && burntime instanceof Integer)
					setProperty.invoke(anotherFurnaceObject, 0, ((Integer) burntime).intValue());
				// ---
				Object cooktime = owner.getData("CookTime");
				if (cooktime != null && cooktime instanceof Integer)
					setProperty.invoke(anotherFurnaceObject, 2, ((Integer) cooktime).intValue());
				// ---
				Object cooktimetotal = owner.getData("CookTimeTotal");
				if (cooktimetotal != null && cooktimetotal instanceof Integer)
					setProperty.invoke(anotherFurnaceObject, 3, ((Integer) cooktimetotal).intValue());
				// ---
				Object items = owner.getData("Items");
				if (items != null && items instanceof List<?>) {
					List<?> l = ((List<?>) items);
					if (l != null && l.size() > 0) {
						for (int x = 0; x < l.size(); x++) {
							Method setItem = Util.getMethod(furClass, "setItem",
									new Class<?>[] { int.class, Object.class });
							setItem.invoke(anotherFurnaceObject, l.get(x));
						}
					}
				}
				// ---
				destroy();
			}
		} catch (IllegalAccessException ParamException) {
			ParamException.printStackTrace();
		} catch (IllegalArgumentException ParamException) {
			ParamException.printStackTrace();
		} catch (InvocationTargetException ParamException) {
			ParamException.printStackTrace();
		} catch (NullPointerException ParamException) {
			// ----
		}
	}

	public void destroy() {
		if (owner != null) {
			owner.setData("BurnTime", null);
			owner.setData("CookTime", null);
			owner.setData("CookTimeTotal", null);
			owner.setData("items", null);
		}
	}
}
