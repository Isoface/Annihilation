package com.hotmail.AdrianSRJose.AnniPro.utils.menu;

import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 16 July, 2020 / 10:58 AM
 */
public class MenuUtil {

	public static boolean isValidForMenus ( ItemStack stack ) {
		try {
			Class < ? > class0 = ReflectionUtils.getCraftBukkitClass ( "inventory" , "CraftItemStack" );
			Method     method0 = ReflectionUtils.getMethod ( class0 , "asNMSCopy" , ItemStack.class );
			
			Object nms_stack = method0.invoke ( class0 , stack );
			if ( nms_stack != null ) {
				Class < ? > class1 = ReflectionUtils.getCraftClass ( "ItemStack" );
				try {
					// it seems that we are in a server version >= 1.11
					return !( (boolean) ReflectionUtils.getMethod ( class1 , "isEmpty" ).invoke ( nms_stack ) );
				} catch ( NoSuchMethodException ex_b ) {
					// it seems that we are in a server version <= 1.10
					return true;
				}
			} else {
				return false;
			}
		} catch ( Throwable ex ) {
			ex.printStackTrace ( );
		}
		return true;
	}

	public static boolean isValidForMenus ( Material material ) {
		return isValidForMenus ( new ItemStack ( material , 1 ) );
	}
}
