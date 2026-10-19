package me.lolok.teleports.teleports;

import me.lolok.teleports.Service;
import me.lolok.teleports.teleports.requests.DefaultTeleportRequest;
import me.lolok.teleports.teleports.requests.TeleportRequest;
import me.lolok.teleports.teleports.requests.TeleportRequestType;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ITeleportService extends Service {
    Optional<TeleportRequest> getTeleportRequest(UUID sender, UUID target);

    Set<TeleportRequest> getTeleportRequestsFromSender(UUID sender);

    Set<TeleportRequest> getTeleportRequestsFromTarget(UUID target);

    Optional<TeleportRequest> getRecentTeleportRequestFromSender(UUID sender);

    Optional<TeleportRequest> getRecentTeleportRequestFromTarget(UUID target);

    void addRequest(TeleportRequestType type, UUID sender, UUID target, boolean notify);

    void removeRequest(UUID sender, UUID target);

    void accept(Player sender, Player target);

    void deny(Player sender, Player target);
}
