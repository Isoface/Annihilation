package com.hotmail.AdrianSRJose.AnniPro.anniGame.NCPS;

public enum EquipmentSlot {
	
	HELMET(4, "HEAD"), 
	CHESTPLATE(3, "CHEST"), 
	LEGGINGS(2, "LEGS"), 
	BOOTS(1, "FEET"), 
	HAND(0, "MAINHAND");

	private final int id;
	private final String enumItemSlot;

	private EquipmentSlot(int id, String eis) {
		this.id = id;
		this.enumItemSlot = eis;
	}

	public int getId() {
		return id;
	}

	public String toEnumItemSlotString() {
		return enumItemSlot;
	}
}
