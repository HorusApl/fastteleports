package me.lolok.teleports.teleports.requests;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.configurations.files.SettingsConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface TeleportRequest {
    SettingsConfiguration SETTINGS = FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("settings",SettingsConfiguration.class);

    void startExpirationTask();

    void stopExpirationTask();

    @Nullable Player getSenderPlayer();

    @Nullable Player getTargetPlayer();

    /**
     * Execute all the actions when this request is accepted
     * @return true if the request is completed
     */
    boolean accept();

    void deny();

    void sendNotification();

    TeleportRequestType getType();

    UUID getSender();

    UUID getTarget();

    long getCreatedAt();
}
