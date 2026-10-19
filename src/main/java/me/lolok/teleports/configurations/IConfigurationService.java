package me.lolok.teleports.configurations;

import me.lolok.teleports.Service;
import me.lolok.teleports.configurations.files.IConfig;

public interface IConfigurationService extends Service {
    <T extends IConfig> T getConfiguration(String name, Class<T> clazz);

    void register(String name, Config<?> configuration, boolean overwrite);
}
