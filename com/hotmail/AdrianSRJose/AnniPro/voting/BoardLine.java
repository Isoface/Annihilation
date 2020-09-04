package com.hotmail.AdrianSRJose.AnniPro.voting;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;

public class BoardLine {
	private String text;
	private LineAttribute att;
	private AnniTeam team;

	public BoardLine(String text, LineAttribute att) {
		this.text = text;
		this.att = att;
	}

	public BoardLine(String text, LineAttribute att, AnniTeam team) {
		this.text = text;
		this.att = att;
		this.team = team;
	}

	public String getText() {
		return text != null ? text : "";
	}

	public void setText(String newText) {
		text = newText;
	}

	public LineAttribute getAttribute() {
		return att;
	}

	public void setLineAttribute(LineAttribute newAtt) {
		att = newAtt;
	}

	public AnniTeam getTeam() {
		return team;
	}

	public void setTeam(AnniTeam newTeam) {
		team = newTeam;
	}
}
