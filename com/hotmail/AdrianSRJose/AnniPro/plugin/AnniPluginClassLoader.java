package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidPluginException;

public class AnniPluginClassLoader extends URLClassLoader
{	
    private final AnniPluginLoader loader;
    private final Map<String, Class<?>> classes = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>(); // Spigot
    private final AnniPluginDescriptionFile description;
    private final File dataFolder;
    private final File file;
    final AnniPlugin plugin;
    private AnniPlugin pluginInit;
    private IllegalStateException pluginState;

	AnniPluginClassLoader(final AnniPluginLoader loader, final ClassLoader parent, final AnniPluginDescriptionFile description, final File dataFolder, final File file) throws MalformedURLException, InvalidPluginException {
		super(new URL[] {file.toURI().toURL()}, parent);
		Validate.notNull(loader, "Loader cannot be null");

        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", ex);
            }

            Class<? extends AnniPlugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(AnniPlugin.class);
            } catch (ClassCastException ex) {
                throw new InvalidPluginException("main class `" + description.getMain() + "' does not extend AnniPlugin", ex);
            }

            // set instance
            plugin = pluginClass.newInstance();
            
            // initialize
            if (plugin != null) {
            	initialize(plugin);
            }
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
        }
	}

    synchronized void initialize(AnniPlugin javaPlugin) {
        Validate.notNull(javaPlugin, "Initializing plugin cannot be null");
        Validate.isTrue(javaPlugin.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
//        if (this.plugin != null || this.pluginInit != null) {
//            throw new IllegalArgumentException("Plugin already initialized!", pluginState);
//        }
//
//        pluginState = new IllegalStateException("Initial initialization");
//        this.pluginInit = javaPlugin;

        javaPlugin.init(loader, Bukkit.getServer(), description, dataFolder, file, this);
    }
    
    Set<String> getClasses() {
        return classes.keySet();
    }
}
