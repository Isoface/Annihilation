package com.hotmail.AdrianSRJose.AnniPro.mapBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public class FutureBlockReplace implements Runnable {
	
	public static final String COBBLESTONE_REGENERATING_BLOCKS_REPLACER_METADATA = "COBBLESTONE_REPLACER_METADATA";
	
	private final BlockState state;
	private final Block block;

	public FutureBlockReplace(Block b) {
		state = b.getState();
		block = b;
	}

	public FutureBlockReplace(Block b, boolean cobble) {
		state = b.getState();
		block = b;
		
		// set type
		b.setType(cobble ? Material.COBBLESTONE : Material.AIR);
		
		// add cobblestone metadata.
		if (cobble) {
			b.setMetadata(COBBLESTONE_REGENERATING_BLOCKS_REPLACER_METADATA,
					new FixedMetadataValue(AnnihilationMain.INSTANCE, COBBLESTONE_REGENERATING_BLOCKS_REPLACER_METADATA));
		}
	}

	@Override
	public void run() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(AnnihilationMain.INSTANCE, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				state.update(true);
//				Game.getGameMap().getWorld().spawnParticle(Particle.BLOCK_CRACK, paramLocation, paramInt);
				Game.getGameMap().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId());
			}
		});
	}
}