package com.hotmail.AdrianSRJose.xpSystem.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.main.Lang;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class GiveXpCommand implements CommandExecutor {
	private final XPSystem xpSystem;
	private final String gaveXpMessage;

	public GiveXpCommand(JavaPlugin pl, XPSystem xps, String mess) {
		pl.getCommand("AnniXp").setExecutor(this);
		xpSystem = xps;
		gaveXpMessage = mess;
	}

	private void sendXPMessage(AnniPlayer player, int XP) {
		player.sendMessage(XPMain.formatString(gaveXpMessage, XP));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p != null && p.isOnline() && p.hasPermission("A.Anni") || p.isOp()) {
				if (args.length > 0) {
					if (args[0] != null && args[0].equalsIgnoreCase("give")) {
						if (args.length > 1 && args[1] != null) {
							Player to = Bukkit.getPlayer(args[1]);
							if (to != null && to.isOnline()) {
								AnniPlayer toAp = AnniPlayer.getPlayer(to.getUniqueId());
								if (args.length > 2 && toAp != null && args[2] != null) {
									try {
										int i = Integer.parseInt(args[2]);
										int amount = XPMain.checkMultipliers(to, i);
										xpSystem.giveXP(to.getUniqueId(), amount);
										sendXPMessage(toAp, amount);
										if (!to.getUniqueId().equals(p.getUniqueId())) {
											if (gaveXpMessage != null) {
												String tor = gaveXpMessage;
												tor = Util.wc(tor);
												tor = tor.replace("%#", "§6" + amount);
												p.sendMessage("§a" + to.getName() + " has received " + tor);
											} else
												p.sendMessage("§a" + to.getName() + " has received " + "§6" + amount
														+ " §aof AnnihilationXP");
										}
									} catch (NumberFormatException e) {
										p.sendMessage("§cInvailid number");
									}
								}
							} else
								sender.sendMessage("§cPlayer not found");
						}
					} else
						sender.sendMessage("§aUsage: /AnniXp give (player) (quantity).");
				} else
					sender.sendMessage("§aUsage: /AnniXp give (player) (quantity).");
			} else
				sender.sendMessage(Lang.WITHOUT_PERMISSION_COMMAND.toString());
		} else
			sender.sendMessage("§cThis command needs to be used by a player.");
		return true;
	}
	//
}
