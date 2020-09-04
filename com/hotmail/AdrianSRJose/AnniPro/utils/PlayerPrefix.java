package com.hotmail.AdrianSRJose.AnniPro.utils;

import org.bukkit.entity.Player;

import com.github.gustav9797.PowerfulPerms.PowerfulPerms;
import com.github.gustav9797.PowerfulPermsAPI.PermissionPlayer;

import me.TechXcrafter.UltraPermissions.UltraPermissions;
import me.TechXcrafter.UltraPermissions.storage.User;
import net.luckperms.api.LuckPermsProvider;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Represents the players
 * prefix getter class.
 * <p>
 * @author AdrianSR
 */
public final class PlayerPrefix {

	/**
	 * player prefix.
	 */
	private String premissions_ex_prefix    = null;
	private String ultra_permissions_prefix = null;
	private String powerful_perms_prefix    = null;
	private String luck_perms_prefix        = null;

	/**
	 * Construct a new
	 * <em>
	 * player prefix getter.
	 * <blockquote>
	 * <b> Compatible plugins:
	 * <p>
	 * - PermissionsEx 
	 * <p>
	 * - UltraPermissions 
	 * <p>
	 * - PowerfulPerms
	 * </em>
	 * </blockquote>
	 * <p>
	 * @param player the player to get his prefix.
	 */
	public PlayerPrefix(final Player player) {
		// get PermissionsEx.
		{
			try {
				// check is enabled Ultra Permissisons
				Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");

				// check user.
				if (PermissionsEx.getUser(player) != null) {
					// get user prefix.
					premissions_ex_prefix = PermissionsEx.getUser(player).getPrefix();
				}
			} catch (Throwable t) {
				// ignore.
			}
		}

		// get UltraPermissions prefix.
		{
			try {
				// Check is enabled Ultra Permissisons
				Class.forName("me.TechXcrafter.UltraPermissions.UltraPermissions");

				// get and check user.
				final User user = UltraPermissions.getUserFromUUID(player.getUniqueId());
				if (user != null) {
					// get user prefix.
					ultra_permissions_prefix = user.getPrefix();
				}
			} catch (Throwable t) {
				// ignore.
			}
		}

		// get PowerfulPerms prefix.
		{
			try {
				// Check is enabled PowerfulPerms
				Class.forName("com.github.gustav9797.PowerfulPerms");

				// get and check user.
				final PermissionPlayer user = PowerfulPerms.getPlugin().getPermissionManager()
						.getPermissionPlayer(player.getUniqueId());
				if (user != null) {
					// get user prefix.
					powerful_perms_prefix = user.getPrefix();
				}
			} catch (Throwable t) {
				// ignore.
			}
		}
		
		/* get LuckPerms prefix */
		{
			try {
				luck_perms_prefix = LuckPermsProvider.get ( ).getUserManager ( ).getUser ( player.getUniqueId ( ) )
						.getCachedData ( ).getMetaData ( ).getPrefix ( );
			} catch ( Throwable ex ) {
				/* ignored */
			}
		}
	}
	
	/**
	 * Get player PermissionsEx
	 * prefix.
	 * <p>
	 * @return player prefix in PermissionsEx.
	 */
	public String getPermissionsExPrefix() {
		return premissions_ex_prefix;
	}
	
	/**
	 * Get player UltraPermissions
	 * prefix.
	 * <p>
	 * @return player prefix in UltraPermissions.
	 */
	public String getUltraPermissionsPrefix() {
		return ultra_permissions_prefix;
	}
	
	/**
	 * Get player PowerfulPerms
	 * prefix.
	 * <p>
	 * @return player prefix in PowerfulPerms.
	 */
	public String getPowerfulPermsPrefix() {
		return powerful_perms_prefix;
	}
	
	/**
	 * Gets the player LuckPerms prefix.
	 * <p>
	 * @return player LuckPerms prefix.
	 */
	public String getLuckPermsPrefix ( ) {
		return luck_perms_prefix;
	}
}