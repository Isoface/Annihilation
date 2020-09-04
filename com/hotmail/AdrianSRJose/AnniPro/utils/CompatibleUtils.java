package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;

public class CompatibleUtils {

	public static void setData(Block block, byte data) {
		setData(block, data, true);
	}

	public static void setData(Block block, byte data, boolean applyPhysics) {
		try {
			block.getClass().getMethod("setData", byte.class, boolean.class).invoke(block, data, false);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static enum CompatibleParticles {
	    AMBIENT_ENTITY_EFFECT("SPELL_MOB_AMBIENT", "SPELL_MOB_AMBIENT"),
	    ANGRY_VILLAGER("VILLAGER_ANGRY", "VILLAGER_ANGRY"),
	    BARRIER("BARRIER", "BARRIER"),
	    BLOCK("BLOCK_CRACK", "BLOCK_CRACK"),
	    BUBBLE("WATER_BUBBLE", "WATER_BUBBLE"),
	    BUBBLE_COLUMN_UP("BUBBLE_COLUMN_UP", null),
	    BUBBLE_POP("BUBBLE_POP", null),
	    CLOUD("CLOUD", "CLOUD"),
	    CRIT("CRIT", "CRIT"),
	    CURRENT_DOWN("CURRENT_DOWN", null),
	    DAMAGE_INDICATOR("DAMAGE_INDICATOR", "DAMAGE_INDICATOR"),
	    DOLPHIN("DOLPHIN", null),
	    DRAGON_BREATH("DRAGON_BREATH", "DRAGON_BREATH"),
	    DRIPPING_LAVA("DRIP_LAVA", "DRIP_LAVA"),
	    DRIPPING_WATER("DRIP_WATER", "DRIP_WATER"),
	    DUST("REDSTONE", "REDSTONE"),
	    // ELDER_GUARDIAN("MOB_APPEARANCE", "MOB_APPEARANCE"), // No thank you
	    ENCHANT("ENCHANTMENT_TABLE", "ENCHANTMENT_TABLE"),
	    ENCHANTED_HIT("CRIT_MAGIC", "CRIT_MAGIC"),
	    END_ROD("END_ROD", "END_ROD"),
	    ENTITY_EFFECT("SPELL_MOB", "SPELL_MOB"),
	    EXPLOSION("EXPLOSION_LARGE", "EXPLOSION_LARGE"),
	    EXPLOSION_EMITTER("EXPLOSION_HUGE", "EXPLOSION_HUGE"),
	    FALLING_DUST("FALLING_DUST", "FALLING_DUST"),
	    FIREWORK("FIREWORKS_SPARK", "FIREWORKS_SPARK"),
	    FISHING("WATER_WAKE", "WATER_WAKE"),
	    FLAME("FLAME", "FLAME"),
	    FOOTSTEP(null, "FOOTSTEP"), // Removed in Minecraft 1.13 :(
	    HAPPY_VILLAGER("VILLAGER_HAPPY", "VILLAGER_HAPPY"),
	    HEART("HEART", "HEART"),
	    INSTANT_EFFECT("SPELL_INSTANT", "SPELL_INSTANT"),
	    ITEM("ITEM_CRACK", "ITEM_CRACK"),
	    ITEM_SLIME("SLIME", "SLIME"),
	    ITEM_SNOWBALL("SNOWBALL", "SNOWBALL"),
	    LARGE_SMOKE("SMOKE_LARGE", "SMOKE_LARGE"),
	    LAVA("LAVA", "LAVA"),
	    MYCELIUM("TOWN_AURA", "TOWN_AURA"),
	    NAUTILUS("NAUTILUS", null),
	    NOTE("NOTE", "NOTE"),
	    POOF("EXPLOSION_NORMAL", "EXPLOSION_NORMAL"), // The 1.13 combination of explode and showshovel
	    PORTAL("PORTAL", "PORTAL"),
	    RAIN("WATER_DROP", "WATER_DROP"),
	    SMOKE("SMOKE_NORMAL", "SMOKE_NORMAL"),
	    SPELL("SPELL", "SPELL"), // The Minecraft internal name for this is actually "effect", but that's the command name, so it's SPELL for the plugin instead
	    SPIT("SPIT", "SPIT"),
	    SPLASH("WATER_SPLASH", "WATER_SPLASH"),
	    SQUID_INK("SQUID_INK", null),
	    SWEEP_ATTACK("SWEEP_ATTACK", "SWEEP_ATTACK"),
	    TOTEM_OF_UNDYING("TOTEM", "TOTEM"),
	    UNDERWATER("SUSPENDED_DEPTH", "SUSPENDED_DEPTH"),
	    WITCH("SPELL_WITCH", "SPELL_WTICH");
		
		private final String enum_name;
		private final String enum_name_legacy;
		
		private CompatibleParticles(String enum_name, String enum_name_legacy) {
			// get enum names.
			this.enum_name        = enum_name;
			this.enum_name_legacy = enum_name_legacy;
		}
		
		public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) {
			// get enum displayer.
			Enum<?> enum_displayer = getCompatibleDisplayer();
			
			// check displayer.
			if (enum_displayer == null) {
				throw new UnsupportedOperationException("The particles name is not valid!");
			}
			
			// try to display.
			try {
				// get method.
				final Method method = enum_displayer.getClass().getMethod("display", float.class, float.class, float.class, float.class, int.class, Location.class, double.class);
				
				// invoke.
				method.invoke(enum_displayer, offsetX, offsetY, offsetZ, speed, amount, center, range);
			} catch(Throwable t) {
				t.printStackTrace();
			}
			
		}
		
		public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) {
			// get enum displayer.
			Enum<?> enum_displayer = getCompatibleDisplayer();
						
			// check displayer.
			if (enum_displayer == null) {
				throw new UnsupportedOperationException("The particles name is not valid!");
			}
			
			// try to display.
			try {
				// get method.
				final Method method = enum_displayer.getClass().getMethod("display", float.class, float.class, float.class, float.class, int.class, Location.class, List.class);
				
				// invoke.
				method.invoke(enum_displayer, offsetX, offsetY, offsetZ, speed, amount, center, players);
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
		
		public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) {
			display(offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
		}
		
		public void display(Vector direction, float speed, Location center, double range) {
			// get enum displayer.
			Enum<?> enum_displayer = getCompatibleDisplayer();
						
			// check displayer.
			if (enum_displayer == null) {
				throw new UnsupportedOperationException("The particles name is not valid!");
			}
			
			// try to display.
			try {
				// get method.
				final Method method = enum_displayer.getClass().getMethod("display", Vector.class, float.class, Location.class, double.class);
				
				// invoke.
				method.invoke(enum_displayer, direction, speed, center, range);
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
		
		public void display(Vector direction, float speed, Location center, List<Player> players) {
			// get enum displayer.
			Enum<?> enum_displayer = getCompatibleDisplayer();
						
			// check displayer.
			if (enum_displayer == null) {
				throw new UnsupportedOperationException("The particles name is not valid!");
			}

			// try to display.
			try {
				// get method.
				final Method method = enum_displayer.getClass().getMethod("display", Vector.class, float.class, Location.class, List.class);

				// invoke.
				method.invoke(enum_displayer, direction, speed, center, players);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		public void display(Vector direction, float speed, Location center, Player... players) {
			display(direction, speed, center, Arrays.asList(players));
		}
		
		public de.slikey.effectlib.util.ParticleEffect displayNewerVersions() {
			try {
				return de.slikey.effectlib.util.ParticleEffect.valueOf(enum_name);
			} catch(Throwable t) {
				return de.slikey.effectlib.util.ParticleEffect.valueOf(enum_name_legacy);
			}
		}
		
		public OldParticleEffect displayOlderVersions() {
			try {
				return OldParticleEffect.valueOf(enum_name);
			} catch(Throwable t) {
				return OldParticleEffect.valueOf(enum_name_legacy);
			}
		}
		
		private Enum<?> getCompatibleDisplayer() {
			return ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1) ? displayNewerVersions() : displayOlderVersions();
		}
	}
}