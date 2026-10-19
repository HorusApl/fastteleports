package me.lolok.teleports.configurations.files;

import lombok.Getter;
import me.lolok.teleports.FastTeleportsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SettingsConfiguration implements IConfig {
    @Getter
    private int timeout;
    @Getter
    private boolean allowYourself, alwaysOnGround;
    @Getter
    private List<Material> blacklistedGroundBlocks;

    @Override
    public void initialize(YamlConfiguration configuration) {
        this.blacklistedGroundBlocks = new ArrayList<>();
        this.timeout = configuration.getInt("requests.timeout", 30);
        this.allowYourself = configuration.getBoolean("requests.allow-yourself", false);
        this.alwaysOnGround = configuration.getBoolean("safety.always-on-ground", true);
        for (String s : configuration.getStringList("safety.blacklisted-ground-blocks"))
            try {
                Material material = Material.valueOf(s.toUpperCase());
                this.blacklistedGroundBlocks.add(material);
            } catch (IllegalArgumentException e) {
                FastTeleportsPlugin.getInstance().getLogger().warning("Unknown material type: " + s);
            }
    }
}
