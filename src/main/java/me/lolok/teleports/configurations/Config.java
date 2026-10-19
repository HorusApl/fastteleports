package me.lolok.teleports.configurations;

import lombok.Getter;
import me.lolok.teleports.configurations.files.IConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

public class Config<T extends IConfig> {
    @Getter
    private final T config;

    public Config(T config) {
        this.config = config;
    }

    public static <T extends IConfig> Config<T> load(JavaPlugin plugin,  String fileName, Class<T> clazz) {
        String path = String.format("%s.yml", fileName);
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (InputStream in = Config.class.getClassLoader().getResourceAsStream(path)) {
                Files.write(file.toPath(), in.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy the default configuration. File: " + fileName, e);
            }
        }

        Config<T> config;
        try {
            config = new Config<>(clazz.getConstructor().newInstance());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to initialize config", e);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        config.getConfig().initialize(yaml);
        return config;
    }
}
