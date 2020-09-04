package com.hotmail.AdrianSRJose.xpSystem.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.xpSystem.utils.Acceptor;

public class GiftXpCommand implements CommandExecutor {
	private final XPSystem xpSystem;
	private final String giftXpMessage;

	public GiftXpCommand(JavaPlugin pl, XPSystem xps, String mess) {
		pl.getCommand("GiveXp").setExecutor(this);
		xpSystem = xps;
		giftXpMessage = mess;
	}

	private void sendXPMessage(Player from, Player to, int XP) {
		to.sendMessage(XPMain.formatString(giftXpMessage, XP, from));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p != null && p.isOnline()) {
				if (args.length > 0) {
					Player to = Bukkit.getPlayer(args[0]);
					if (to != null && to.isOnline()) {
						if (args.length > 1) {
							try {
								if (to.getUniqueId().equals(p.getUniqueId())) {
									return true;
								}

								// Get Ammount to gift
								int i = Integer.parseInt(args[1]);

								// Check and give
								xpSystem.getXP(p.getUniqueId(), new Acceptor<Integer>() {
									@Override
									public void accept(Integer xp) {
										if (xp >= i) {
											// Give and remove xp
											xpSystem.giveXP(to.getUniqueId(), i);
											xpSystem.removeXP(p.getUniqueId(), i);

											// Send message
											sendXPMessage(p, to, i);

											// Send gifted xp message
											if (giftXpMessage != null) {
												String tor = giftXpMessage;
												tor = Util.wc(tor);
												tor = tor.replace("%#", "§6" + i);
												p.sendMessage("§a" + to.getName() + " has received " + tor);
											} else
												p.sendMessage("§a" + to.getName() + " has received " + "§6" + i
														+ " §aof AnnihilationXP");
										} else
											p.sendMessage(ChatColor.RED + "No XP!");
									}
								});
							} catch (NumberFormatException e) {
								p.sendMessage("§cInvailid number");
							}
						}
					} else
						sender.sendMessage("§cPlayer not found");
				} else
					sender.sendMessage(ChatColor.RED + "Usage: /GiveXP [player] [ammount]");
			}
		} else
			sender.sendMessage("§cThis command needs to be used by a player.");
		return true;
	}
}
