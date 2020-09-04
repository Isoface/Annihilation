package com.hotmail.AdrianSRJose.AnniPro.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.StandardPhaseHandler;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.AnnounceBar;
import com.hotmail.AdrianSRJose.AnniPro.announcementBar.Announcement;

public class NextPhaseCommand implements CommandExecutor {
	
	public NextPhaseCommand(JavaPlugin pl) {
		pl.getCommand("NextPhase").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// check permission.
		if (!sender.hasPermission("A.anni")) {
			sender.sendMessage(ChatColor.RED + "You dont have permission to use this command.");
			return true;
		}
		
		if (Game.isNotRunning()) {
			sender.sendMessage(ChatColor.RED + "The game is not Running!");
			return true;
		}

		if (Game.getGameMap() == null) {
			sender.sendMessage(ChatColor.RED + "Could not find the Game Map!");
			return true;
		}

		if (Game.getGameMap().getCurrentPhase() == 5) {
			return true;
		}

		// Get new Phase
		final StandardPhaseHandler handler = new StandardPhaseHandler(false);

		// New Announcement
		AnnounceBar.getInstance().countDown(
				new Announcement(Lang.PHASEBAR.toStringReplacement(Game.getGameMap().getCurrentPhase()) + " - {#}",
						Lang.BOSSBAR.toStringReplacement(Game.getGameMap().getCurrentPhase()) + " - {#}")
								.setTime(Game.getGameMap().getPhaseTime()).setCallback(handler));

		// Run Handler
		handler.run();
		return true;
	}
}
