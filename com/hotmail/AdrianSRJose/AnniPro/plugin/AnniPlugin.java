package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginAwareness;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

import lombok.Getter;
import lombok.Setter;

public abstract class AnniPlugin implements ArgumentExecutor {
	private @Getter boolean                       isEnabled = false;
	private @Getter AnniPluginLoader                 loader = null;
	private @Getter Server                           server = null;
	protected @Getter File                             file = null;
	private @Getter AnniPluginDescriptionFile   description = null;
	private @Getter AnniPluginLogger                 logger = null;
	private YamlConfiguration                     newConfig = null;
	private @Getter File                         dataFolder = null;
	private @Getter File                         configFile = null;
	private ClassLoader                         classLoader = null;
	private List<AnniCommandArgument>             arguments;
	private @Getter @Setter PluginYamlManager configManager;
	private static @Getter AnniPluginManager pluginManager = AnnihilationMain.getPluginManager();
	
	final void init(AnniPluginLoader loader, Server server, AnniPluginDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
        // load values
		this.loader      = loader;
        this.server      = server;
        this.file        = file;
        this.description = description;
        this.dataFolder  = dataFolder;
        this.classLoader = classLoader;
        this.configFile  = new File(dataFolder, (description.getConfigFile() != null ? description.getConfigFile() : "config.yml"));
        this.logger      = new AnniPluginLogger(this);
        this.arguments   = AnniPluginArgumentYamlParser.parse(this);
        this.isEnabled   = true;
        
        // check datafolder
        if (dataFolder != null && !dataFolder.exists() || !dataFolder.isDirectory()) {
        	dataFolder.mkdir();
        }
        
		// register arguments
		if (arguments != null && !arguments.isEmpty()) {
			for (AnniCommandArgument arg : arguments) {
				if (arg != null) {
					PluginArgumentManager.registerArgument(this, arg);
				}
			}
		}
	}
    
    public abstract void onEnable();
    public void onDisable() {}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onArgument(CommandSender sender, String label, String[] args) {
    	
    }
    
    public YamlConfiguration getConfig() {
        if (newConfig == null) {
            reloadConfig(false);
        }
        return newConfig;
    }
    
    protected final Reader getTextResource(String file, boolean UFT_8) {
        final InputStream in = getResource(file);

        return in == null ? null : new InputStreamReader(in, (isStrictlyUTF8() && UFT_8) ? Charsets.UTF_8 : Charset.defaultCharset());
    }
    
    public void reloadConfig(boolean UFT_8) {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource((description.getConfigFile() != null ? description.getConfigFile() : "config.yml"));
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;
        if (isStrictlyUTF8() || UFT_8) {
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        } else {
            final byte[] contents;
            defConfig = new YamlConfiguration();
            try {
                contents = ByteStreams.toByteArray(defConfigStream);
            } catch (final IOException e) {
                getLogger().log(Level.SEVERE, "Unexpected failure reading " + (description.getConfigFile() != null ? description.getConfigFile() : "config.yml"), e);
                return;
            }

            final String text = new String(contents, Charset.defaultCharset());
            if (!text.equals(new String(contents, Charsets.UTF_8))) {
                getLogger().warning("Default system encoding may have misread " + (description.getConfigFile() != null ? description.getConfigFile() : "config.yml") + " from plugin jar");
            }

            try {
                defConfig.loadFromString(text);
            } catch (final InvalidConfigurationException e) {
                getLogger().log(Level.SEVERE, "Cannot load configuration from jar", e);
            }
        }

        newConfig.setDefaults(defConfig);
    }
    
    private boolean isStrictlyUTF8() {
        return getDescription().getAwareness().contains(PluginAwareness.Flags.UTF8);
    }
    
    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }
    
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource((description.getConfigFile() != null ? description.getConfigFile() : "config.yml"), false);
        }
    }
    
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }
    
    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
    
    public AnniCommandArgument getArgument(String argumentName) {
    	if (argumentName == null) {
    		return null;
    	}
    	
    	for (AnniCommandArgument arg : arguments) {
    		if (arg != null) {
    			if (argumentName.equalsIgnoreCase(arg.getName())) {
    				return arg;
    			}
    		}
    	}
    	return null;
    }
    
    protected final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }
    
    public List<AnniCommandArgument> getArguments() {
    	return Collections.unmodifiableList(arguments);
    }
    
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public String getName() {
    	return description.getName();
    }
    
    @Override
    public String toString() {
        return description.getFullName();
    }
}
