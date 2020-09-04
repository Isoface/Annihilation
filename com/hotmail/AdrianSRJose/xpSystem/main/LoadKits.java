package com.hotmail.AdrianSRJose.xpSystem.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.xpSystem.database.AsyncQuery;
import com.hotmail.AdrianSRJose.xpSystem.utils.Acceptor;

class LoadKits implements AsyncQuery {
	private final AnniPlayer player;
	private final Acceptor<AnniPlayer> postLoad;
	private List<String> kits;

	public LoadKits(AnniPlayer p, Acceptor<AnniPlayer> postLoad) {
		this.player = p;
		this.postLoad = postLoad;
	}

	@Override
	public void run() {
		player.setData("Kits", kits);
		if (postLoad != null) {
			postLoad.accept(player);
		}
	}

	@Override
	public String getQuerey() {
		return "SELECT * FROM tbl_player_kits WHERE ID='" + player.getID() + "';";
	}

	@Override
	public boolean isCallback() {
		return true;
	}

	@Override
	public void setResult(ResultSet result) {
		kits = new ArrayList<String>();
		try {
			while (result.next()) {
				kits.add(result.getString("Kit").toLowerCase());
			}
		} catch (SQLException e) {

		}
	}
}
