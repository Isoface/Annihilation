package com.hotmail.AdrianSRJose.AnniPro.plugin;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginAwareness;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import lombok.Getter;

public final class AnniPluginDescriptionFile {
	private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
		@Override
		protected Yaml initialValue() {
			return new Yaml(new SafeConstructor() {
				{
					yamlConstructors.put(null, new AbstractConstruct() {
						@Override
						public Object construct(final Node node) {
							if (!node.getTag().startsWith("!@")) {
								// Unknown tag - will fail
								return SafeConstructor.undefinedConstructor.construct(node);
							}
							// Unknown awareness - provide a graceful substitution
							return new PluginAwareness() {
								@Override
								public String toString() {
									return node.toString();
								}
							};
						}
					});
					for (final PluginAwareness.Flags flag : PluginAwareness.Flags.values()) {
						yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
							@Override
							public PluginAwareness.Flags construct(final Node node) {
								return flag;
							}
						});
					}
				}
			});
		}
	};

	String rawName = null;
	private String name = null;
	private String main = null;
	private String classLoaderOf = null;
	private List<String> depend = ImmutableList.of();
	private List<String> softDepend = ImmutableList.of();
	private List<String> loadBefore = ImmutableList.of();
	private String version = null;
	private Map<String, Map<String, Object>> arguments = null;
	private String description = null;
	private List<String> authors = null;
	private String website = null;
	private @Getter String configFile = "config.yml";
	private String prefix = null;
	private List<Permission> permissions = null;
	private Map<?, ?> lazyPermissions = null;
	private PermissionDefault defaultPerm = PermissionDefault.OP;
	private Set<PluginAwareness> awareness = ImmutableSet.of();

	public AnniPluginDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
		loadMap(asMap(YAML.get().load(stream)));
	}

	private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
		try {
			name = rawName = map.get("name").toString();

			if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
				throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
			}
			name = name.replace(' ', '_');
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "name is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "name is of wrong type");
		}

		try {
			version = map.get("version").toString();
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "version is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "version is of wrong type");
		}

		try {
			main = map.get("main").toString();
			if (main.startsWith("org.bukkit.") || main.startsWith("com.hotmail.AdrianSRJose.")) {
				throw new InvalidDescriptionException(
						"main may not be within the org.bukkit/com.hotmail.AdrianSRJose namespace");
			}
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "main is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "main is of wrong type");
		}

		// load command '/anni' arguments
		if (map.get("arguments") != null) {
			ImmutableMap.Builder<String, Map<String, Object>> commandsBuilder = ImmutableMap.<String, Map<String, Object>>builder();
			try {
				for (Map.Entry<?, ?> command : ((Map<?, ?>) map.get("arguments")).entrySet()) {
					ImmutableMap.Builder<String, Object> commandBuilder = ImmutableMap.<String, Object>builder();
					if (command.getValue() != null) {
						for (Map.Entry<?, ?> commandEntry : ((Map<?, ?>) command.getValue()).entrySet()) {
							if (commandEntry.getValue() instanceof Iterable) {
								// This prevents internal alias list changes
								ImmutableList.Builder<Object> commandSubList = ImmutableList.<Object>builder();
								for (Object commandSubListItem : (Iterable<?>) commandEntry.getValue()) {
									if (commandSubListItem != null) {
										commandSubList.add(commandSubListItem);
									}
								}
								commandBuilder.put(commandEntry.getKey().toString(), commandSubList.build());
							} else if (commandEntry.getValue() != null) {
								commandBuilder.put(commandEntry.getKey().toString(), commandEntry.getValue());
							}
						}
					}
					commandsBuilder.put(command.getKey().toString(), commandBuilder.build());
				}
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "arguments are of wrong type");
			}
			arguments = commandsBuilder.build();
		}

		if (map.get("class-loader-of") != null) {
			classLoaderOf = map.get("class-loader-of").toString();
		}

		depend     = makePluginNameList(map, "depend");
		softDepend = makePluginNameList(map, "softdepend");
		loadBefore = makePluginNameList(map, "loadbefore");

		if (map.get("website") != null) {
			website = map.get("website").toString();
		}
		
		if (map.get("config-file") != null) {
			// configFile
			String customConfigFile = map.get("config-file").toString();
			if (customConfigFile != null && !customConfigFile.isEmpty()) {
				configFile = customConfigFile.endsWith(".yml") ? customConfigFile : customConfigFile + ".yml";
			}
		}

		if (map.get("description") != null) {
			description = map.get("description").toString();
		}

		if (map.get("authors") != null) {
			ImmutableList.Builder<String> authorsBuilder = ImmutableList.<String>builder();
			if (map.get("author") != null) {
				authorsBuilder.add(map.get("author").toString());
			}
			try {
				for (Object o : (Iterable<?>) map.get("authors")) {
					authorsBuilder.add(o.toString());
				}
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "authors are of wrong type");
			} catch (NullPointerException ex) {
				throw new InvalidDescriptionException(ex, "authors are improperly defined");
			}
			authors = authorsBuilder.build();
		} else if (map.get("author") != null) {
			authors = ImmutableList.of(map.get("author").toString());
		} else {
			authors = ImmutableList.<String>of();
		}

		if (map.get("default-permission") != null) {
			try {
				defaultPerm = PermissionDefault.getByName(map.get("default-permission").toString());
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "default-permission is of wrong type");
			} catch (IllegalArgumentException ex) {
				throw new InvalidDescriptionException(ex, "default-permission is not a valid choice");
			}
		}

		if (map.get("awareness") instanceof Iterable) {
			Set<PluginAwareness> awareness = new HashSet<PluginAwareness>();
			try {
				for (Object o : (Iterable<?>) map.get("awareness")) {
					awareness.add((PluginAwareness) o);
				}
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "awareness has wrong type");
			}
			this.awareness = ImmutableSet.copyOf(awareness);
		}

		try {
			lazyPermissions = (Map<?, ?>) map.get("permissions");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "permissions are of the wrong type");
		}

		if (map.get("prefix") != null) {
			prefix = map.get("prefix").toString();
		}
	}

	private static List<String> makePluginNameList(final Map<?, ?> map, final String key)
			throws InvalidDescriptionException {
		final Object value = map.get(key);
		if (value == null) {
			return ImmutableList.of();
		}

		final ImmutableList.Builder<String> builder = ImmutableList.<String>builder();
		try {
			for (final Object entry : (Iterable<?>) value) {
				builder.add(entry.toString().replace(' ', '_'));
			}
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, key + " is of wrong type");
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "invalid " + key + " format");
		}
		return builder.build();
	}

	private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
		if (object instanceof Map) {
			return (Map<?, ?>) object;
		}
		throw new InvalidDescriptionException(object + " is not properly structured.");
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getMain() {
		return main;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public String getWebsite() {
		return website;
	}

	public List<String> getDepend() {
		return depend;
	}

	public List<String> getSoftDepend() {
		return softDepend;
	}

	public List<String> getLoadBefore() {
		return loadBefore;
	}

	public String getPrefix() {
		return prefix;
	}

	public Map<String, Map<String, Object>> getArguments() {
		return arguments;
	}

	public List<Permission> getPermissions() {
		if (permissions == null) {
			if (lazyPermissions == null) {
				permissions = ImmutableList.<Permission>of();
			} else {
				permissions = ImmutableList.copyOf(Permission.loadPermissions(lazyPermissions,
						"Permission node '%s' in plugin description file for " + getFullName() + " is invalid",
						defaultPerm));
				lazyPermissions = null;
			}
		}
		return permissions;
	}

	public PermissionDefault getPermissionDefault() {
		return defaultPerm;
	}
	
    public Set<PluginAwareness> getAwareness() {
        return awareness;
    }

	public String getFullName() {
		return name + " v" + version;
	}

	/**
	 * @return internal use
	 * @deprecated Internal use
	 */
	@Deprecated
	public String getRawName() {
		return rawName;
	}
}
