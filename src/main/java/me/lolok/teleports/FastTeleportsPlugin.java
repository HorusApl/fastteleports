package me.lolok.teleports;

import lombok.Getter;
import me.lolok.teleports.commands.impl.*;
import me.lolok.teleports.commands.impl.multitude.TeleportAllCommand;
import me.lolok.teleports.commands.impl.multitude.TeleportRegionCommand;
import me.lolok.teleports.commands.impl.multitude.TeleportWorldCommand;
import me.lolok.teleports.configurations.ConfigurationService;
import me.lolok.teleports.configurations.IConfigurationService;
import me.lolok.teleports.database.ISQLManager;
import me.lolok.teleports.database.SQLManager;
import me.lolok.teleports.database.driver.Driver;
import me.lolok.teleports.teleports.ITeleportService;
import me.lolok.teleports.teleports.TeleportService;
import me.lolok.teleports.teleports.enums.TeleportPreference;
import me.lolok.teleports.teleports.requests.TeleportRequestType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class FastTeleportsPlugin extends JavaPlugin {
    @Getter
    private static FastTeleportsPlugin instance;
    @Getter
    private final ISQLManager sqlManager = new SQLManager(this);
    @Getter
    private final IConfigurationService configurationService = new ConfigurationService(this);
    @Getter
    private final ITeleportService teleportService = new TeleportService();


    @Override
    public void onLoad() {
        instance = this;
        configurationService.enable();
        Arrays.asList(Driver.values()).forEach(Driver::download);
    }

    @Override
    public void onEnable() {
        sqlManager.enable();
        teleportService.enable();
        new ExecuteDatabaseUpdateCommand(this).enable();
        new TPAccept(this).enable();
        new TPDeny(this).enable();
        new TPCancel(this).enable();
        new TeleportAllCommand(this).enable();
        new TeleportWorldCommand(this).enable();
        new TeleportRegionCommand(this).enable();
        new TeleportPreferenceCommand(this, "fasttptoggle", TeleportPreference.TOGGLE_REQUESTS).enable();
        new TeleportPreferenceCommand(this, "fasttpauto", TeleportPreference.AUTO_ACCEPT).enable();
        new TeleportPreferenceCommand(this, "fasttpactionbar", TeleportPreference.ACTION_BAR).enable();
        new TeleportRequestCommand(this, "fasttpa", TeleportRequestType.TO).enable();
        new TeleportRequestCommand(this, "fasttpahere", TeleportRequestType.HERE).enable();
        // Send notification messages
        Bukkit.getConsoleSender().sendMessage("§a| Plugin: " + getDescription().getDisplayName());
        Bukkit.getConsoleSender().sendMessage("§a| Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        sqlManager.disable();
        teleportService.disable();
        configurationService.disable();
    }
}
