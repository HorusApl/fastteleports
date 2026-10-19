package me.lolok.teleports.configurations.files;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public class DatabaseConfiguration implements IConfig {
    @Getter
    private String driver;

    @Override
    public void initialize(YamlConfiguration configuration) {
        this.driver = configuration.getString("driver", "SQLITE");
    }
}
