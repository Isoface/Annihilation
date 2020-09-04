package com.hotmail.AdrianSRJose.AnniPro.plugin;

import org.bukkit.command.CommandSender;

/**
 * Represents a class which contains a single method for executing commands
 */
public interface ArgumentExecutor {

    /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    public void onArgument(CommandSender sender, String label, String[] args);
}