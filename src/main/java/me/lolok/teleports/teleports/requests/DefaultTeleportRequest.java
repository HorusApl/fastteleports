package me.lolok.teleports.teleports.requests;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.configurations.files.SettingsConfiguration;
import me.lolok.teleports.configurations.messages.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultTeleportRequest implements TeleportRequest {
    @Getter
    private final TeleportRequestType type;
    @Getter
    private final UUID sender, target;
    @Getter
    private final long createdAt = System.currentTimeMillis();
    private boolean warned = false;
    private int expirationTaskId;

    @Override
    public void startExpirationTask() {
        expirationTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(FastTeleportsPlugin.getInstance(), () -> {
            FastTeleportsPlugin.getInstance().getTeleportService().removeRequest(sender, target);
            Player sender = getSenderPlayer();
            if (sender != null)
                Message.TELEPORT_EXPIRED.sendWithActionbar(sender);
        }, SETTINGS.getTimeout() * 20L).getTaskId();
    }

    @Override
    public void stopExpirationTask() {
        if (expirationTaskId > 0)
            Bukkit.getScheduler().cancelTask(expirationTaskId);
    }

    @Override
    public @Nullable Player getSenderPlayer() {
        return Bukkit.getPlayer(sender);
    }

    @Override
    public @Nullable Player getTargetPlayer() {
        return Bukkit.getPlayer(target);
    }

    @Override
    public boolean accept() {
        stopExpirationTask();
        Player sender = getSenderPlayer(), target = getTargetPlayer();
        Preconditions.checkNotNull(target, "Target null cannot accept a teleport request.");
        if (sender == null) {
            Message.USER_NOT_ONLINE_ANYMORE.sendWithActionbar(target);
            return true;
        }

        Location location = (type == TeleportRequestType.TO ? target.getLocation() : sender.getLocation()).clone();
        if (FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("settings", SettingsConfiguration.class).isAlwaysOnGround())
            while (location.clone().subtract(0, 1D, 0).getBlock().getType() == Material.AIR && location.getY() > location.getWorld().getMinHeight())
                location.subtract(0, 1D, 0);
        if (!location.getBlock().getType().isSolid())
            location.setY((int) location.getY());
        if (!warned && (SETTINGS.getBlacklistedGroundBlocks().contains(location.clone().subtract(0, 1D, 0).getBlock().getType()) || location.getBlockY() == location.getWorld().getMinHeight())) {
            startExpirationTask();
            warned = true;
            Component warn = Message.TELEPORT_WARN_MESSAGE.getComponent(false)
                    .hoverEvent(HoverEvent.showText(Message.TELEPORT_WARN_HOVER.getComponent(false)))
                    .clickEvent(ClickEvent.runCommand("/fasttpaccept " + sender.getName()));
            target.playSound(target.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);
            target.sendMessage(warn);
            target.sendActionBar(Message.TELEPORT_WARN_MESSAGE.getComponent(true));
            return false;
        }
        (type == TeleportRequestType.TO ? sender : target).teleportAsync(location);

        target.playSound(target.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1F, 1F);
        Message.TELEPORT_ACCEPTED_YOU.sendWithActionbar(target, "sender", sender.getDisplayName());
        Message.TELEPORT_ACCEPTED_OTHER.sendWithActionbar(sender, "target", target.getDisplayName());
        return true;
    }

    @Override
    public void deny() {
        stopExpirationTask();
        Player sender = getSenderPlayer(), target = getTargetPlayer();
        Preconditions.checkNotNull(target, "Target null cannot deny a teleport request.");
        if (sender == null) {
            Message.USER_NOT_ONLINE_ANYMORE.sendWithActionbar(target);
            return;
        }
        Message.TELEPORT_DENIED_YOU.sendWithActionbar(target, "sender", sender.getDisplayName());
        Message.TELEPORT_DENIED_OTHER.sendWithActionbar(sender, "target", target.getDisplayName());
    }

    @Override
    public void sendNotification() {
        Player sender = getSenderPlayer(), target = getTargetPlayer();
        if (sender == null || target == null) return;
        Message.TELEPORT_SENT.sendWithActionbar(sender, "target", target.getDisplayName());
        type.getMessage().sendWithActionbar(target, "sender", sender.getDisplayName());
        Component accept = Message.TELEPORT_BUTTONS_ACCEPT.getComponent(false)
                .hoverEvent(HoverEvent.showText(Message.TELEPORT_BUTTONS_ACCEPT_HOVER.getComponent(false)))
                .clickEvent(ClickEvent.runCommand("/fasttpaccept " + sender.getName()));
        Component deny = Message.TELEPORT_BUTTONS_DENY.getComponent(false)
                .hoverEvent(HoverEvent.showText(Message.TELEPORT_BUTTONS_DENY_HOVER.getComponent(false)))
                .clickEvent(ClickEvent.runCommand("/fasttpdeny " + sender.getName()));
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
        target.sendMessage(accept.append(Component.text(" ")).append(deny));
    }
}
