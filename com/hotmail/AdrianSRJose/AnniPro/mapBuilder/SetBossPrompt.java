package com.hotmail.AdrianSRJose.AnniPro.mapBuilder;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniBoss;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameBoss;
import com.hotmail.AdrianSRJose.AnniPro.utils.Loc;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;

public class SetBossPrompt extends ValidatingPrompt {
	private int lvl;
	private final Player player;
	private boolean start;
	private final ChatColor purple = ChatColor.LIGHT_PURPLE;
	private final ChatColor gold = ChatColor.GOLD;
	private final ChatColor red = ChatColor.RED;
	private final ChatColor green = ChatColor.GREEN;

	public SetBossPrompt(Player player) {
		this.player = player;
		lvl = 0;
		start = true;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		String finalMessage = "";

		if (start) {
			context.getForWhom().sendRawMessage(gold + "Boss Spawn location Seted in This Positon");
			start = false;
		}

		switch (lvl) {
		case 0:
		default: {
			context.getForWhom().sendRawMessage(purple + "What name do you want to put the boss?");
			finalMessage = purple + "If you no want put a name to boss, type " + green + "Skip" + purple + ".";
			break;
		}

		case 1: {
			context.getForWhom().sendRawMessage(purple + "Please enter the regeneration time of the boss");
			finalMessage = purple + "Enter a value in the format: " + red + "[" + green + "Number" + red + "] [" + green
					+ "Unit" + red + "]" + purple + " (omit the brackets)" + purple
					+ " Units: (seconds, minutes, hours) -> (s, m, h)" + green + " Example: " + gold + "5 m";
			break;
		}

		case 2: {
			context.getForWhom().sendRawMessage(purple + "Please Enter How a number to set the health of the Boss");
			finalMessage = purple + "Enter a " + green + "Number" + purple + " greater than -1.";
			break;
		}

		case 3: {
			context.getForWhom().sendRawMessage(purple + "Please Select a Type to Boss --> Types: [Wither, IronGolem]");
			finalMessage = purple + "Enter a " + green + "Boss Type " + purple + "Types: [Wither, IronGolem].";
			break;
		}

		}
		return finalMessage;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		if (input.startsWith("/"))
			input = input.substring(1);

		input = input.toLowerCase().trim();

		if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
			context.getForWhom()
					.sendRawMessage(ChatColor.GOLD + "Boss Helper" + ChatColor.LIGHT_PURPLE + " has been closed.");
			return Prompt.END_OF_CONVERSATION;
		} else if (input.equals("back") || input.equals("previous")) {
			lvl--;
			return this;
		}

		switch (lvl) {
		case 0: {
			if (GameBoss.getBoss() == null) {
				GameBoss.setAnniBoss(new AnniBoss(null, "", null, null, 0, null));
			}

			if (input.equalsIgnoreCase("skip")) {
				GameBoss.getBoss().setName("");
				context.getForWhom().sendRawMessage(gold + "You haven't named the boss" + ".");
			} else {
				GameBoss.getBoss().setName(input);
				context.getForWhom()
						.sendRawMessage(green + "You Have Put Of Name: " + gold + Util.wc(input) + purple + " To Boss" + ".");
			}
			GameBoss.getBoss().setSpawn(new Loc(player.getLocation(), true));
			lvl = 1;
			break;
		}

		case 1: {
			String[] args = input.split(" ");

			if (args.length == 2) {
				try {
					TimeUnit unit = MapBuilder.getUnit(args[1]);
					if (unit != null) {
						int time = Integer.parseInt(args[0]);
						GameBoss.getBoss().setRespawnTime(time);
						GameBoss.getBoss().setRespawnUnit(unit);
						context.getForWhom().sendRawMessage(
								green + "Boss Respawn Time Now Is " + gold + time + " " + unit.name() + ".");
						lvl = 2;
					}
				} catch (IllegalArgumentException e) {
					context.getForWhom().sendRawMessage(red + "Invalid Unit" + ".");
				}
			}
			break;
		}

		case 2: {
			try {
				if (input != null) {
					Double r = Double.parseDouble(input);
					GameBoss.getBoss().setVida(r);

					context.getForWhom().sendRawMessage(ChatColor.LIGHT_PURPLE + "The Boss Health now is " + gold
							+ GameBoss.getBoss().getVida() + ".");
					lvl = 3;
				}
			} catch (NumberFormatException e) {
				context.getForWhom().sendRawMessage(red + "Invalid Number" + ".");
			}
			break;
		}

		case 3: {
			if (input.equalsIgnoreCase("Wither") || input.equalsIgnoreCase("IronGolem")) {
				if (input.equalsIgnoreCase("Wither")) {
					GameBoss.getBoss().setType(Wither.class);
					context.getForWhom().sendRawMessage(green + "Selected Type: " + purple + "Wither");
				} else if (input.equalsIgnoreCase("IronGolem")) {
					GameBoss.getBoss().setType(IronGolem.class);
					context.getForWhom().sendRawMessage(green + "Selected Type: " + purple + "IronGolem");
				}

				GameBoss.getBossMap().saveTheConfig();
				context.getForWhom().sendRawMessage(ChatColor.GOLD + "Real Boss " + ChatColor.GREEN + "Seted" + " :).");
				context.getForWhom()
						.sendRawMessage(ChatColor.GOLD + "Boss Helper" + ChatColor.LIGHT_PURPLE + " has been closed.");
				player.performCommand("anniboss save map");
				lvl = 0;
				start = true;
				return Prompt.END_OF_CONVERSATION;
			} else
				context.getForWhom().sendRawMessage(red + "Invalid Type.");
			break;
		}
		}
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext cc, String ss) {
		return true;
	}
}
