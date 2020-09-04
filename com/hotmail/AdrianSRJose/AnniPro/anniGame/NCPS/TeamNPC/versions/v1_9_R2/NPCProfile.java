package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS.TeamNPC.versions.v1_9_R2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_9_R2.MinecraftServer;

/**
 * Created by Lenny on 8/8/2014. You may use this code if permitted by the legal
 * owner only. Copyright 2014 Lennart ten Wolde
 */
public class NPCProfile {
	private static final LoadingCache<String, Property> TEXTURE_CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(30L, TimeUnit.MINUTES).build(new CacheLoader<String, Property>() {
				@Override
				public Property load(String key) throws Exception {
					return loadTextures(key);
				}
			});

	@SuppressWarnings("deprecation")
	private static final Property loadTextures(String name){
		GameProfile profile = new GameProfile(Bukkit.getOfflinePlayer(name).getUniqueId(), name);
		MinecraftServer.getServer().ay().fillProfileProperties(profile, true);
		return Iterables.getFirst(profile.getProperties().get("textures"), null);
	}

	/**
	 * Load a profile with a custom skin
	 *
	 * @param name
	 *            Display name of profile
	 * @param skinOwner
	 *            Owner of profile skin
	 * @return NPCProfile
	 */
	public static NPCProfile loadProfile(String name, String skinOwner) {
		try {
			final GameProfile profile = new GameProfile(UUID.randomUUID(), name);
			profile.getProperties().put("textures", TEXTURE_CACHE.get(skinOwner));
			return new NPCProfile(profile);
		} catch (Exception e) {
			// Make sure that we don't return any exceptions
			return new NPCProfile(name);
		}
	}

	public static NPCProfile loadProfile(final Player owner) {
		try {
			final GameProfile profile = new GameProfile(UUID.randomUUID(), owner.getName());
			String[] a = getFromName(owner.getName());// getTexturesFromPlayer(owner);
			// Util.print("Geting: " + owner.getName());
			Bukkit.getConsoleSender().sendMessage("Geting: " + owner.getName());
			profile.getProperties().put("textures", new Property("textures", a[0], a[1]));
			return new NPCProfile(profile);
		} catch (Exception e) {
			e.printStackTrace();
			// Make sure that we don't return any exceptions
			return new NPCProfile(owner.getName());
		}
	}

	public static NPCProfile loadProfile(final String playerName) {
		try {
			final GameProfile profile = new GameProfile(UUID.randomUUID(), playerName);
			String[] a = getFromName(playerName);// getTexturesFromPlayer(owner);
			// Util.print("Geting: " + owner.getName());
			Bukkit.getConsoleSender().sendMessage("Geting: " + playerName);
			profile.getProperties().put("textures", new Property("textures", a[0], a[1]));
			return new NPCProfile(profile);
		} catch (Exception e) {
			e.printStackTrace();
			// Make sure that we don't return any exceptions
			return new NPCProfile(playerName);
		}
	}

	// private static GameProfile getProfileFromPlayer(Player playerBukkit) {
	// EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
	// return playerNMS.getProfile();
	// }

	private static String[] getFromName(String name) {
		try {
			URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

			URL url_1 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
					.getAsJsonArray().get(0).getAsJsonObject();
			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();

			return new String[] { texture, signature };
		} catch (IOException e) {
			System.err.println("Could not get skin data from session servers!");
			e.printStackTrace();
			return null;
		}
	}

	// private static String[] getTexturesFromPlayer(Player playerBukkit)
	// {
	// EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
	// GameProfile profile = playerNMS.getProfile();
	// Property property =
	// profile.getProperties().get("textures").iterator().next();
	// String texture = property.getValue();
	// String signature = property.getSignature();
	// return new String[] { texture, signature };
	// }

	private final GameProfile handle;

	/**
	 * Return a profile with a steve skin.
	 *
	 * @param name
	 *            Display name of profile
	 */
	public NPCProfile(String name) {
		this(new GameProfile(UUID.randomUUID(), name));
	}

	public NPCProfile(GameProfile handle) {
		this.handle = handle;
	}

	/**
	 * Get the profile UUID
	 *
	 * @return Profile UUID
	 */
	public UUID getUUID() {
		return handle.getId();
	}

	/**
	 * Get the profile display name
	 *
	 * @return Display name
	 */
	public String getDisplayName() {
		return handle.getName();
	}

	/**
	 * Get the original game profile
	 *
	 * @return Original game profile
	 */
	public GameProfile getHandle() {
		return handle;
	}
}
