package me.lolok.teleports.configurations.files;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesConfiguration implements IConfig {
    @Getter
    private YamlConfiguration configuration;

    @Override
    public void initialize(YamlConfiguration configuration) {
        this.configuration = configuration;
    }
}
