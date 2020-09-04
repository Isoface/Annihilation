package com.hotmail.AdrianSRJose.AnniPro.main;

import org.bukkit.command.CommandSender;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;

public interface AnniArgument {
	String getHelp();

	boolean useByPlayerOnly();

	String getArgumentName();

	void executeCommand(CommandSender sender, String label, String[] args);

	String getPermission();

	MenuItem getMenuItem();
}
