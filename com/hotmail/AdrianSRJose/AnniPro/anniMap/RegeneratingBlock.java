package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class RegeneratingBlock {
	public RegeneratingBlock(Material Type, int MaterialData, boolean Regenerate, boolean CobbleReplace,
			boolean NaturalBreak, int Time, TimeUnit Unit, int XP, Material Product, String Amount, int ProductData,
			String Effect, byte blockdata) {
		this.Type          = Type;
		this.Regenerate    = Regenerate;
		this.CobbleReplace = CobbleReplace;
		this.NaturalBreak  = NaturalBreak;
		this.Time          = Time;
		this.TimeUnit      = Unit;
		this.XP            = XP;
		this.Product       = Product;
		this.Amount        = Amount;
		this.MaterialData  = MaterialData;
		this.Effect        = Effect;
		this.ProductData   = ProductData;
		this.blockdata     = blockdata;
	}

	public final Material Type;
	public final int MaterialData;
	public final boolean Regenerate;
	public final boolean CobbleReplace;
	public final boolean NaturalBreak;
	public final int Time;
	public final TimeUnit TimeUnit;
	public final int XP;
	public final Material Product;
	public final String Amount;
	public final int ProductData;
	public final String Effect;
	public byte blockdata;
	
	public void setBlockData ( byte blockdata ) {
		this.blockdata = blockdata;
	}

	public int saveToConfig(ConfigurationSection configSection) {
		int save = 0;
		//
		if (configSection != null) {
			save += Util.setUpdatedIfNotEqual(configSection, "Type", Type.name());
			save += Util.setUpdatedIfNotEqual(configSection, "MaterialData", MaterialData);
			save += Util.setUpdatedIfNotEqual(configSection, "Regenerate", Regenerate);
			save += Util.setUpdatedIfNotEqual(configSection, "CobbleReplace", CobbleReplace);
			save += Util.setUpdatedIfNotEqual(configSection, "Regenerate", Regenerate);
			save += Util.setUpdatedIfNotEqual(configSection, "NaturalBreak", NaturalBreak);
			save += Util.setUpdatedIfNotEqual(configSection, "Time", Time);
			save += Util.setUpdatedIfNotEqual(configSection, "Unit", TimeUnit != null ? TimeUnit.name() : "");
			save += Util.setUpdatedIfNotEqual(configSection, "XP", XP);
			save += Util.setUpdatedIfNotEqual(configSection, "Product", Product != null ? Product.name() : "");
			save += Util.setUpdatedIfNotEqual(configSection, "Amount", Amount);
			save += Util.setUpdatedIfNotEqual(configSection, "ProductData", ProductData);
			save += Util.setUpdatedIfNotEqual(configSection, "Effect", Effect);
		}
		//
		return save;
	}
}
