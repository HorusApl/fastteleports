package me.lolok.teleports.teleports;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.configurations.messages.Message;
import me.lolok.teleports.database.managers.SQLTeleportsRequestsManager;
import me.lolok.teleports.teleports.enums.TeleportPreference;
import me.lolok.teleports.teleports.requests.DefaultTeleportRequest;
import me.lolok.teleports.teleports.requests.TeleportRequest;
import me.lolok.teleports.teleports.requests.TeleportRequestType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TeleportService implements ITeleportService {
    private final Set<TeleportRequest> requests = new HashSet<>();

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        requests.clear();
    }

    @Override
    public Optional<TeleportRequest> getTeleportRequest(UUID sender, UUID target) {
        return requests.stream().filter(req -> req.getSender().equals(sender) && req.getTarget().equals(target)).findFirst();
    }

    @Override
    public Set<TeleportRequest> getTeleportRequestsFromSender(UUID sender) {
        return requests.stream().filter(req -> req.getSender().equals(sender)).collect(Collectors.toSet());
    }

    @Override
    public Set<TeleportRequest> getTeleportRequestsFromTarget(UUID target) {
        return requests.stream().filter(req -> req.getTarget().equals(target)).collect(Collectors.toSet());
    }

    @Override
    public Optional<TeleportRequest> getRecentTeleportRequestFromSender(UUID sender) {
        return requests.stream()
                .sorted(Comparator.comparing(TeleportRequest::getCreatedAt).reversed())
                .filter(req -> req.getSender().equals(sender))
                .findFirst();
    }

    @Override
    public Optional<TeleportRequest> getRecentTeleportRequestFromTarget(UUID target) {
        return requests.stream()
                .sorted(Comparator.comparing(TeleportRequest::getCreatedAt).reversed())
                .filter(req -> req.getTarget().equals(target))
                .findFirst();
    }

    @Override
    public void addRequest(TeleportRequestType type, UUID sender, UUID target, boolean notify) {
        boolean accept = false;
        if (type == TeleportRequestType.TO) {
            SQLTeleportsRequestsManager manager = FastTeleportsPlugin.getInstance().getSqlManager().getTeleportsRequestsManager();
            accept = manager.getRequestsPreference(TeleportPreference.AUTO_ACCEPT, target).join() || manager.hasTeleportPreferenceTarget(TeleportPreference.AUTO_ACCEPT, target, sender).join();
        }
        if (!accept && getTeleportRequest(sender, target).isPresent())
            return;
        TeleportRequest request = new DefaultTeleportRequest(type, sender, target);
        if (accept) {
            request.accept();
            return;
        }
        request.startExpirationTask();
        requests.add(request);
        if (notify)
            request.sendNotification();
    }

    @Override
    public void removeRequest(UUID sender, UUID target) {
        requests.removeIf(req -> req.getSender().equals(sender) && req.getTarget().equals(target));
    }

    @Override
    public void accept(Player sender, Player target) {
        getTeleportRequest(sender.getUniqueId(), target.getUniqueId()).ifPresentOrElse(request -> {
            if (request.accept())
                removeRequest(request.getSender(), request.getTarget());
        }, () -> Message.NOTHING_TO_ACCEPT.sendWithActionbar(target, "sender", sender.getDisplayName()));
    }

    @Override
    public void deny(Player sender, Player target) {
        getTeleportRequest(sender.getUniqueId(), target.getUniqueId()).ifPresentOrElse(request -> {
            removeRequest(request.getSender(), request.getTarget());
            request.deny();
        }, () -> Message.NOTHING_TO_DENY.sendWithActionbar(target, "sender", sender.getDisplayName()));
    }
}
