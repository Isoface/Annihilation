package com.hotmail.AdrianSRJose.AnniPro.anniGame;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;

import lombok.Getter;
import lombok.Setter;

public class AnniBoss {
	private Class<? extends LivingEntity> type;
	private String name;
	private Double vida;
	private Loc spawn;
	private Integer respawnTime;
	private TimeUnit respawnUnit;
	private @Getter @Setter ItemStack[]      rewards;
	private @Getter @Setter int rewardsInventorySize;
	
	public AnniBoss(Class<? extends LivingEntity> type, String name, Double vida, Loc spawn, Integer respawnTime,
			TimeUnit respawnUnit) {
		this.type        = type;
		this.name        = name;
		this.vida        = vida;
		this.spawn       = spawn;
		this.respawnTime = respawnTime;
		this.respawnUnit = respawnUnit;
	}

	public AnniBoss(ConfigurationSection section) {
		String t = section.getString("Type");
		if (t.equalsIgnoreCase("Wither"))
			type = Wither.class;
		else if (t.equalsIgnoreCase("IronGolem"))
			type = IronGolem.class;
		else
			type = null;
		//
		name = section.getString("Name", "");
		vida = section.getDouble("Health", 10.0);
		respawnTime = section.getInt("Respawn Time", 10);
		//
		String un = section.getString("Unit");
		un = un != null ? un.toUpperCase() : "";
		//
		respawnUnit = TimeUnit.valueOf(un);
		spawn = new Loc(section.getConfigurationSection("BossSpawnPoint"));
		
		// load boss shop reward
		ConfigurationSection rewar = section.getConfigurationSection("Rewards");
		if (rewar != null) {
			// load rewards inventory size
			rewardsInventorySize = rewar.getInt ( "InventorySize" );
			
			final Map < Integer , ItemStack > ITEMS = new HashMap < Integer , ItemStack > ( );
			int max_slot = 0;
			
			for ( String key : rewar.getKeys ( false ) ) {
				if ( !rewar.isItemStack ( key ) ) {
					continue;
				}
				
				try {
					int        slot = Integer.valueOf ( key );
					ItemStack stack = rewar.getItemStack ( key );
					if ( stack != null ) {
						ITEMS.put ( slot , stack );
						
						/* max slot */
						max_slot = Math.max ( max_slot , slot );
					}
				} catch ( NumberFormatException ex ) {
					/* this is not a valid reward item */
				}
			}
			
//			for ( String key : rewar.getKeys ( false ) ) {
//				try {
//					// get ConfigurationSection
//					ConfigurationSection ar = rewar.getConfigurationSection ( key );
//					
//					// get item and slot
//					Integer     slot = Integer.valueOf(key);
//					ItemStack loaded = GameBackup.loadItem(ar);
//					
//					// check and add
//					if (slot != null && loaded != null) {
//						if (slot.intValue() >= slotMayor) {
//							slotMayor = slot.intValue();
//						}
//						
//						// put
//						ITEMS.put(slot, loaded);
//					}
//				} catch ( Throwable throwable ) {
//					/* ignored */
//				}
//			}
			
			// set rewards
			if (!ITEMS.isEmpty()) {
				// set val
				rewards = new ItemStack[max_slot + 1];
				
				// set items
				for (Integer slot : ITEMS.keySet()) {
					// check slot
					if (slot == null) {
						continue;
					}
					
					// get and check item
					ItemStack item = ITEMS.get(slot);
					if (item == null) {
						continue;
					}
					
					// set
					rewards[slot.intValue()] = item;
				}
			}
		}
	}

	public Class<? extends LivingEntity> getType() {
		return type;
	}

	public void setType(Class<? extends LivingEntity> newType) {
		type = newType;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public Double getVida() {
		return vida;
	}

	public void setVida(Double newVida) {
		if (newVida != null) {
			vida = newVida;
		}
	}

	public Loc getSpawn() {
		return spawn;
	}

	public void setSpawn(Loc newLoc) {
		spawn = newLoc;
	}

	public Integer getRespawnTime() {
		return respawnTime;
	}

	public void setRespawnTime(Integer newTime) {
		respawnTime = newTime;
	}

	public TimeUnit getRespawnUnit() {
		return respawnUnit;
	}

	public void setRespawnUnit(TimeUnit unit) {
		respawnUnit = unit;
	}
}
