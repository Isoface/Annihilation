package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.util.List;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.Setter;

public class AnniCommandArgument
{
    private final @Getter String                     name;
    private @Getter @Setter List<String>          aliases;
    protected @Getter @Setter String                 help;
    protected @Getter @Setter boolean     useByPlayerOnly;
    protected @Getter @Setter String           description = "";
    private @Getter @Setter ArgumentExecutor     executor;
    
    public AnniCommandArgument(final String name) {
    	this.name = name;
    }
    
    public void sendHelp(CommandSender sender) {
    	if (help != null) {
    		sender.sendMessage(help);
    	}
    }
    
    public void sendDescription(CommandSender sender) {
    	if (description != null && !description.isEmpty()) {
    		sender.sendMessage(description);
    	}
    }
}
