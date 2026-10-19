package me.lolok.teleports.configurations;

import me.lolok.teleports.configurations.files.DatabaseConfiguration;
import me.lolok.teleports.configurations.files.IConfig;
import me.lolok.teleports.configurations.files.MessagesConfiguration;
import me.lolok.teleports.configurations.files.SettingsConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationService implements IConfigurationService {
    private final JavaPlugin plugin;
    private final Map<String, Config<?>> configurations = new HashMap<>();

    public ConfigurationService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        register("messages", Config.load(plugin, "messages", MessagesConfiguration.class), true);
        register("actionbar", Config.load(plugin, "actionbar", MessagesConfiguration.class), true);
        register("database", Config.load(plugin, "database", DatabaseConfiguration.class), true);
        register("settings", Config.load(plugin, "settings", SettingsConfiguration.class), true);
    }

    @Override
    public void disable() {
        configurations.clear();
    }

    @Override
    public <T extends IConfig> T getConfiguration(String name, Class<T> clazz) {
        return clazz.cast(configurations.get(name).getConfig());
    }

    @Override
    public void register(String name, Config<?> configuration, boolean overwrite) {
        if (configurations.containsKey(name) && !overwrite)
            throw new IllegalStateException(String.format("The configuration %s is already registered", name));
        configurations.put(name, configuration);
    }
}
