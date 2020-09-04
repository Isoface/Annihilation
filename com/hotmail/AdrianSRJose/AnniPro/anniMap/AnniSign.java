package com.hotmail.AdrianSRJose.AnniPro.anniMap;

import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public final class AnniSign {
	private final FacingObject obj;
	private boolean signPost;
	private SignType type;

	public AnniSign(FacingObject obj, boolean signPost, SignType type) {
		this.obj = obj;
		this.signPost = signPost;
		this.type = type;
	}

	public AnniSign(ConfigurationSection configSection) {
		if (configSection == null)
			throw new NullPointerException();
		//
		boolean signpost = configSection.getBoolean("isSignPost");
		Loc loc = new Loc(configSection.getConfigurationSection("Location"));
		BlockFace facing = BlockFace.valueOf(configSection.getString("FacingDirection"));
		obj = new FacingObject(facing, loc);
		signPost = signpost;
		String data = configSection.getString("Data");
		if (data.equalsIgnoreCase("Brewing"))
			type = SignType.Brewing;
		else if (data.equalsIgnoreCase("Weapon"))
			type = SignType.Weapon;
		else
			type = SignType.newTeamSign(AnniTeam.getTeamByName(data.split("-")[1]));
	}

	public boolean isSignPost() {
		return signPost;
	}

	public Loc getLocation() {
		return obj.getLocation();
	}

	public BlockFace getFacingDirection() {
		return obj.getFacingDirection();
	}

	public SignType getType() {
		return type;
	}

	// save += Util.setUpdatedIfNotEqual(section, "patch", value);
	public int saveToConfig(ConfigurationSection configSection) {
		int save = 0;
		//
		if (configSection != null) {
			save += Util.setUpdatedIfNotEqual(configSection, "isSignPost", isSignPost());
			//
			save += Util.createSectionIfNoExitsInt(configSection, "Location");
			save += getLocation().saveToConfig(Util.createSectionIfNoExits(configSection, "Location"));
			//
			save += Util.setUpdatedIfNotEqual(configSection, "FacingDirection", getFacingDirection().name());
			//
			String data;
			if (this.getType().equals(SignType.Brewing))
				data = "Brewing";
			else if (this.getType().equals(SignType.Weapon))
				data = "Weapon";
			else
				data = "Team-" + this.getType().getTeam().getName();
			//
			save += Util.setUpdatedIfNotEqual(configSection, "Data", data);
		}
		//
		return save > 0 ? 1 : 0;
	}
}
