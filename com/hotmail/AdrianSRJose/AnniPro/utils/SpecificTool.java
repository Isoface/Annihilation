package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.main.Config;

public class SpecificTool implements Listener {

	public List<Material> hacha;
	public List<Material> pala;
	public List<Material> pico;
	public List<Material> espada;
	public List<Material> tijeras;
	public List<Material> Any;
	public List<Material> mano;
	public List<Material> hem;

	public SpecificTool(JavaPlugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
		hacha = new ArrayList<>();
		pala = new ArrayList<>();
		pico = new ArrayList<>();
		espada = new ArrayList<>();
		tijeras = new ArrayList<>();
		Any = new ArrayList<>();
		hem = new ArrayList<>();
		mano = new ArrayList<>();
		verify();
	}

	public void verify() {

		addToList(pala, Material.MYCEL, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL,
				Material.SOUL_SAND, Material.SNOW, Material.SNOW_BLOCK);

		//////////////////////// hacha

		{
			for (Material woods : Material.values()) {
				if (woods.name().contains("WOOD") || woods.name().contains("DOOR") || woods.name().contains("GATE")
						|| woods.name().contains("FENCE") || woods.name().contains("LOG")
						|| woods.name().contains("TORCH") || woods.name().contains("GLASS")) {
					hacha.add(woods);
					addToList(hacha, woods);
				}
			}

			addToList(hacha, Material.NOTE_BLOCK, Material.LADDER, Material.JUKEBOX, Material.CACTUS,
					Material.BOOKSHELF, Material.WOOD, Material.SIGN, Material.SIGN_POST, Material.WOOD_PLATE,
					Material.CHEST, Material.WORKBENCH, Material.FENCE_GATE, Material.CACTUS, Material.BED,
					Material.MELON_BLOCK, Material.JACK_O_LANTERN, Material.PUMPKIN, Material.PUMPKIN_PIE,
					Material.CACTUS, Material.BED_BLOCK);
		}

		//////////////////////// espada
		addToList(espada, Material.MELON_BLOCK, Material.TORCH, Material.LOG, Material.LOG_2, Material.PUMPKIN,
				Material.REDSTONE_TORCH_OFF, Material.CACTUS, Material.PUMPKIN_PIE, Material.REDSTONE_TORCH_ON,
				Material.WEB, Material.LEAVES, Material.LEAVES_2);

		for (Material cespada : Material.values()) {
			if (cespada.name().contains("WOOD")) {
				espada.add(cespada);
				addToList(espada, cespada);
			}
		}

		//////////////////////// tijeras
		{
			for (Material tijera : Material.values()) {
				if (tijera.name().contains("WOOL") || tijera.name().contains("LEAVES")
						|| tijera.name().contains("SAPLING")) {
					tijeras.add(tijera);
					addToList(tijeras, tijera);
				}
			}

			tijeras.add(Material.CARPET);
		}

		//////////////////////// pico
		{
			for (Material cpico : Material.values()) {
				if (cpico.name().contains("STONE") || cpico.name().contains("BRICK")
						|| cpico.name().contains("COBBLESTONE") || cpico.name().contains("ORE")
						|| cpico.name().contains("QUARTZ") || cpico.name().contains("OBSIDIAN")
						|| cpico.name().contains("PISTON") || cpico.name().contains("MUSHROOM")
						|| cpico.name().contains("RAIL") || cpico.name().contains("TORCH")
						|| cpico.name().contains("GLASS")) {
					pico.add(cpico);
					addToList(pico, cpico);
				}
			}

			addToList(pico, Material.STONE, Material.COBBLESTONE, Material.ANVIL, Material.BED_BLOCK,
					Material.NETHERRACK, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST, Material.COAL_BLOCK,
					Material.DIAMOND_BLOCK, Material.GOLD_BLOCK, Material.STAINED_CLAY, Material.EMERALD_BLOCK,
					Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.REDSTONE_BLOCK, Material.HAY_BLOCK,
					Material.SUGAR_CANE_BLOCK, Material.COMMAND, Material.IRON_DOOR, Material.IRON_DOOR_BLOCK,
					Material.BREWING_STAND, Material.IRON_PLATE, Material.IRON_TRAPDOOR, Material.STONE_PLATE,
					Material.GOLD_PLATE, Material.ICE, Material.PACKED_ICE, Material.DRAGON_EGG,
					Material.ENDER_PORTAL_FRAME, Material.TRIPWIRE_HOOK, Material.HOPPER_MINECART, Material.HOPPER,
					Material.CAULDRON, Material.MUSHROOM_SOUP, Material.RAILS, Material.LEVER, Material.ENDER_STONE,
					Util.getEnumFromString(Material.class, "NETHER_WART_BLOCK"),
					Util.getEnumFromString(Material.class, "BONE_BLOCK"),
					Util.getEnumFromString(Material.class, "BEETROOT_BLOCK"),
					Util.getEnumFromString(Material.class, "PURPUR_BLOCK"), Material.COAL_ORE, Material.DIAMOND_ORE,
					Material.GLOWING_REDSTONE_ORE, Material.GOLD_ORE, Material.IRON_ORE, Material.LAPIS_ORE,
					Material.QUARTZ_ORE, Material.REDSTONE_ORE);
		}

		///////////////////////// pala
		{
			for (Material cpala : Material.values()) {
				if (cpala.name().contains("SNOW") || cpala.name().contains("GRASS") || cpala.name().contains("DIRT")
						|| cpala.name().contains("GLASS")) {
					pala.add(cpala);
					addToList(pala, cpala);
				}
			}

			addToList(pala, Material.SAND);
		}

		///////////////////////// Any
		{
			for (Material cany : Material.values()) {
				if (cany.name().contains("SPRUCE") || cany.name().contains("TORCH") || cany.name().contains("MUSHROOM")
						|| cany.name().contains("BUTTON") || cany.name().contains("SEEDS")) {
					Any.add(cany);
					addToList(Any, cany);
				}
			}

			addToList(Any, Material.LONG_GRASS, Material.SAPLING, Material.RED_ROSE, Material.VINE, 
					Material.DEAD_BUSH, Material.TRIPWIRE_HOOK, Material.STRING,
					Material.HAY_BLOCK);
		}

		////////////////////// mano
		for (Material m : Material.values()) {
			if (conMano(m)) {
				mano.add(m);
			}
		}

		////////////////////// hem
		for (Material m : Material.values()) {
			if (hacha.contains(m) && espada.contains(m) && mano.contains(m)) {
				hem.add(m);
			}
		}
	}

	public void addToList(List<Material> l, Material... m) {
		for (Material toadd : m) {
			if (m != null && !l.contains(toadd)) {
				l.add(toadd);
			}
		}
	}

	public boolean conMano(Material m) {
		if (m == Material.MELON_BLOCK || m == Material.PUMPKIN || m == Material.PUMPKIN_PIE
				|| m == Material.JACK_O_LANTERN || m == Material.ICE || m == Material.PACKED_ICE)
			return true;
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onB(final BlockBreakEvent eve) {
		Player p = eve.getPlayer();

		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (!Config.USE_SPECIFIC_TOOL.toBoolean()) {
			return;
		}

		if (hacha.contains(eve.getBlock().getType())) {
			if (!hem.contains(eve.getBlock().getType()) || eve.getBlock().getType() == Material.CACTUS
					|| eve.getBlock().getType().name().contains("FENCE")) {
				if (p.getItemInHand().getType() == Material.AIR)
					eve.setCancelled(true);
			}
		}

		if (Any.contains(eve.getBlock().getType()))
			return;

		if (conMano(eve.getBlock().getType())) {
			if (p.getItemInHand().getType() != Material.AIR) {
				final String wit = p.getItemInHand().getType().name();
				if (wit.contains("PICKAXE") || wit.contains("SPADE") || wit.contains("SHEARS"))
					eve.setCancelled(true);
			}
		} else if (pico.contains(eve.getBlock().getType())) {
			if (!p.getItemInHand().getType().name().contains("PICKAXE"))
				eve.setCancelled(true);

		} else if (pala.contains(eve.getBlock().getType())) {
			if (!p.getItemInHand().getType().name().contains("SPADE"))
				eve.setCancelled(true);
		} else if (espada.contains(eve.getBlock().getType())) {
			if (!p.getItemInHand().getType().name().contains("SWORD")) {
				final String wit = p.getItemInHand().getType().name();
				if (wit.contains("PICKAXE") || wit.contains("SPADE") || wit.contains("SHEARS"))
					eve.setCancelled(true);
			}
		} else if (tijeras.contains(eve.getBlock().getType())) {
			if (p.getItemInHand().getType() != Material.SHEARS)
				eve.setCancelled(true);
		} else if (hacha.contains(eve.getBlock().getType())) {
			final Material wit = p.getItemInHand().getType();;
			if (!wit.name().contains("PICKAXE") && wit.name().contains("AXE"))
				return;
			eve.setCancelled(true);
		}
	}
}
