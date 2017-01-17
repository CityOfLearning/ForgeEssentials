package com.forgeessentials.core.moduleLauncher.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.commons.output.LoggingHandler;

import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

	private static class ConfigFile {

		public Configuration config;

		public Set<ConfigLoader> loaders = new HashSet<>();

		public Set<ConfigLoader> loaded = new HashSet<>();

		public ConfigFile(File path) {
			config = new Configuration(path, true);
		}

	}

	private File rootDirectory;

	private Map<String, ConfigFile> configFiles = new HashMap<>();

	private boolean useCanonicalConfig = false;

	private String mainConfigName;

	public ConfigManager(File rootDirectory, String mainConfigName) {
		this.rootDirectory = rootDirectory;
		this.mainConfigName = mainConfigName;
		load(false);
	}

	public Configuration getConfig(String configName) {
		return getConfigFile(configName).config;
	}

	private ConfigFile getConfigFile(String configName) {
		ConfigFile loaders = configFiles.get(configName);
		if (loaders == null) {
			loaders = new ConfigFile(new File(rootDirectory, configName + ".cfg"));
			configFiles.put(configName, loaders);
		}
		return loaders;
	}

	public Configuration getMainConfig() {
		return getConfig(mainConfigName);
	}

	public String getMainConfigName() {
		return mainConfigName;
	}

	public boolean isUseCanonicalConfig() {
		return useCanonicalConfig;
	}

	public void load(boolean reload) {
		LoggingHandler.felog.debug("Loading configuration files");
		boolean changed = false;
		for (ConfigFile file : configFiles.values()) {
			if (reload) {
				file.config.load();
			}
			for (ConfigLoader loader : file.loaders) {
				if (!reload) {
					if (file.loaded.contains(loader)) {
						continue;
					}
					file.loaded.add(loader);
				}
				changed |= true;
				loader.load(file.config, reload);
			}
			if (changed) {
				file.config.save();
			}
		}
		LoggingHandler.felog.debug("Finished loading configuration files");
	}

	public void load(String configName) {
		ConfigFile file = configFiles.get(configName);
		if (file == null) {
			return;
		}
		for (ConfigLoader loader : file.loaders) {
			loader.load(file.config, true);
		}
		file.config.save();
	}

	public void registerLoader(String configName, ConfigLoader loader) {
		registerLoader(configName, loader, true);
	}

	public void registerLoader(String configName, ConfigLoader loader, boolean loadAfterRegistration) {
		if (useCanonicalConfig && loader.supportsCanonicalConfig()) {
			getConfigFile(mainConfigName).loaders.add(loader);
		} else {
			getConfigFile(configName).loaders.add(loader);
		}
		if (loadAfterRegistration) {
			load(false);
		}
	}

	public void save(String configName) {
		ConfigFile file = getConfigFile(configName);
		for (ConfigLoader loader : file.loaders) {
			if (loader instanceof ConfigSaver) {
				((ConfigSaver) loader).save(file.config);
			}
		}
		file.config.save();
	}

	public void saveAll() {
		LoggingHandler.felog.debug("Saving configuration files");
		for (ConfigFile file : configFiles.values()) {
			file.config.load();
			for (ConfigLoader loader : file.loaders) {
				if (loader instanceof ConfigSaver) {
					((ConfigSaver) loader).save(file.config);
				}
			}
			file.config.save();
		}
	}

	public void setUseCanonicalConfig(boolean useCanonicalConfig) {
		this.useCanonicalConfig = useCanonicalConfig;
	}

}
