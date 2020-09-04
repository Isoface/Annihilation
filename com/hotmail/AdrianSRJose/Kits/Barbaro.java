package com.hotmail.AdrianSRJose.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.Loadout;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.base.ConfigurableKit;

public class Barbaro extends ConfigurableKit {
	private int SwordLevel = 3;

	//
	@Override
	protected void setUp() {
	}

	@Override
	protected String getInternalName() {
		return "Babaro";
	}

	@Override
	protected ItemStack getDefaultIcon ( ) {
		return new ItemStack ( UniversalMaterial.IRON_SWORD.getMaterial ( ) );
	}

	@Override
	protected List<String> getDefaultDescription() {
		List<String> l = new ArrayList<String>();
		addToList(l, aqua + "You Are The Barbarian", "", aqua + "With your stone sword",
				aqua + "you can puch your Enemies", aqua + "you start with a stone sword", aqua + "with knocknack 3,",
				aqua + "and one potion of healt");
		return l;
	}

	@Override
	protected Loadout getFinalLoadout() {
		return new Loadout()
				.addSoulboundEnchantedItem(new ItemStack ( UniversalMaterial.STONE_SWORD.getMaterial ( ) ), Enchantment.KNOCKBACK, SwordLevel)
				.addWoodPick().addWoodAxe().addHealthPotion1();
	}

	@Override
	public void cleanup(Player arg0) {
	}

	@Override
	public boolean onItemClick(Inventory inv, AnniPlayer ap) {
		this.addLoadoutToInventory(inv);
		return true;
	}

	@Override
	protected void onPlayerRespawn(Player p, AnniPlayer ap) {
	}

	@Override
	protected int setDefaults(ConfigurationSection section) {
		return Util.setDefaultIfNotSet(section, "Sword-Punch-Level", 3);
	}

	@Override
	protected void loadFromConfig(ConfigurationSection section) {
		if (section.isInt("Sword-Punch-Level"))
			SwordLevel = Intt(section.getInt("Sword-Punch-Level"));
	}

	public int Intt(int i) {
		return i <= 0 ? 1 : i;
	}
}