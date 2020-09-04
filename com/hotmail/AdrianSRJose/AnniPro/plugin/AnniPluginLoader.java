package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;
import org.yaml.snakeyaml.error.YAMLException;

import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;

public final class AnniPluginLoader {
	private final Map<String, AnniPluginClassLoader> loaders = new LinkedHashMap<String, AnniPluginClassLoader>();
	private final Map<String, Class<?>> classes = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>(); // Spigot
	
	public AnniPlugin loadPlugin(final File file) throws InvalidPluginException {
		Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final AnniPluginDescriptionFile description;
        try {
            description = getPluginDescription(file);
        } catch (InvalidDescriptionException ex) {
            throw new InvalidPluginException(ex);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getName());
        @SuppressWarnings("deprecation")
        final File oldDataFolder = new File(parentFile, description.getRawName());

        // Found old data folder
        if (dataFolder.equals(oldDataFolder)) {
            // They are equal -- nothing needs to be done!
        } else if (dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
            Bukkit.getServer().getLogger().warning(String.format(
                "While loading %s (%s) found old-data folder: `%s' next to the new one `%s'",
                description.getFullName(),
                file,
                oldDataFolder,
                dataFolder
            ));
        } else if (oldDataFolder.isDirectory() && !dataFolder.exists()) {
            if (!oldDataFolder.renameTo(dataFolder)) {
                throw new InvalidPluginException("Unable to rename old data folder: `" + oldDataFolder + "' to: `" + dataFolder + "'");
            }
            Bukkit.getServer().getLogger().log(Level.INFO, String.format(
                "While loading %s (%s) renamed data folder: `%s' to `%s'",
                description.getFullName(),
                file,
                oldDataFolder,
                dataFolder
            ));
        }

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(String.format(
                "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                dataFolder,
                description.getFullName(),
                file
            ));
        }

        for (final String pluginName : description.getDepend()) {
            if (loaders == null) {
                throw new UnknownDependencyException(pluginName);
            }
            AnniPluginClassLoader current = loaders.get(pluginName);

            if (current == null) {
                throw new UnknownDependencyException(pluginName);
            }
        }

        final AnniPluginClassLoader loader;
        try {
            loader = new AnniPluginClassLoader(this, getClass().getClassLoader(), description, dataFolder, file);
        } catch (InvalidPluginException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new InvalidPluginException(ex);
        }

        loaders.put(description.getName(), loader);

        return loader.plugin;
	}
	
    public AnniPluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("anni_plugin.yml");

            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain anni_plugin.yml"));
            }

            stream = jar.getInputStream(entry);

            return new AnniPluginDescriptionFile(stream);

        } catch (IOException ex) {
            throw new InvalidDescriptionException(ex);
        } catch (YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    private void removeClass(String name) {
        Class<?> clazz = classes.remove(name);

        try {
            if ((clazz != null) && (ConfigurationSerializable.class.isAssignableFrom(clazz))) {
                Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
                ConfigurationSerialization.unregisterClass(serializable);
            }
        } catch (NullPointerException ex) {
            // Boggle!
            // (Native methods throwing NPEs is not fun when you can't stop it before-hand)
        }
    }
    
    public void enablePlugin(final AnniPlugin plugin) {
        if (!plugin.isEnabled()) {
            plugin.getLogger().info("Enabling " + plugin.getDescription().getFullName());


            String pluginName = plugin.getDescription().getName();

            if (!loaders.containsKey(pluginName)) {
                loaders.put(pluginName, (AnniPluginClassLoader) plugin.getClassLoader());
            }

            try {
            	plugin.setEnabled(true);
            } catch (Throwable ex) {
                AnnihilationMain.INSTANCE.getServer().getLogger().log(Level.SEVERE, "Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            // Perhaps abort here, rather than continue going, but as it stands,
            // an abort is not possible the way it's currently written
            AnnihilationMain.getPluginManager().callEvent(new AnniPluginEnableEvent(plugin));
        }
    }
    
    public void disablePlugin(AnniPlugin plugin) {
        if (plugin.isEnabled()) {
            String message = String.format("Disabling %s", plugin.getDescription().getFullName());
            plugin.getLogger().info(message);

            AnnihilationMain.INSTANCE.getServer().getPluginManager().callEvent(new AnniPluginDisableEvent(plugin));

            ClassLoader cloader = plugin.getClassLoader();

            try {
            	plugin.setEnabled(false);
            } catch (Throwable ex) {
            	AnnihilationMain.INSTANCE.getServer().getLogger().log(Level.SEVERE, "Error occurred while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            loaders.remove(plugin.getDescription().getName());

            if (cloader instanceof AnniPluginClassLoader) {
            	AnniPluginClassLoader loader = (AnniPluginClassLoader) cloader;
                Set<String> names = loader.getClasses();

                for (String name : names) {
                    removeClass(name);
                }
                
                // close
                try {
					loader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
    }
}
