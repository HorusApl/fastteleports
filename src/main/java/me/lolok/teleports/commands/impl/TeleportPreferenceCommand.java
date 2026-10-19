package me.lolok.teleports.commands.impl;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.commands.AbstractBaseCommand;
import me.lolok.teleports.configurations.files.SettingsConfiguration;
import me.lolok.teleports.configurations.messages.Message;
import me.lolok.teleports.database.managers.SQLTeleportsRequestsManager;
import me.lolok.teleports.teleports.enums.TeleportPreference;
import me.lolok.teleports.utils.EssentialsPlayersRetriever;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeleportPreferenceCommand extends AbstractBaseCommand {
    private final static SQLTeleportsRequestsManager TELEPORTS_REQUESTS_MANAGER = FastTeleportsPlugin.getInstance().getSqlManager().getTeleportsRequestsManager();
    private final TeleportPreference preference;

    public TeleportPreferenceCommand(JavaPlugin plugin, String command, TeleportPreference preference) {
        super(plugin, command, "fastteleports." + command.substring(4), "[target]", false);
        this.preference = preference;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            toggle(player);
            return;
        }
        if (!preference.hasTable())
            return;
        if (args[0].equalsIgnoreCase("list")) {
            FastTeleportsPlugin.getInstance().getSqlManager().getTeleportsRequestsManager().getPreferenceTargetUUIDs(preference, player.getUniqueId()).whenComplete(((uuids, throwable) -> {
                if (throwable != null || uuids == null) {
                    Message.UNEXPECTED_ERROR.send(player);
                    return;
                }
                if (uuids.isEmpty()) {
                    Message.PREFERENCES_LIST_EMPTY.send(player);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                uuids.stream().map(Bukkit::getOfflinePlayer).filter(OfflinePlayer::hasPlayedBefore).map(OfflinePlayer::getName).forEach(s -> sb.append(s).append(", "));
                sb.setLength(sb.length() - 2);
                Message.PREFERENCES_LIST_HEADER.send(player);
                player.sendMessage(Component.text(sb.toString()));
            }));
            return;
        }
        EssentialsPlayersRetriever.get(args[0]).ifPresentOrElse(target -> {
            if (player.getUniqueId().equals(target.getUniqueId()) && !FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("settings", SettingsConfiguration.class).isAllowYourself()) {
                Message.SENDER_SAME_TO_TARGET.send(sender);
                return;
            }
            TELEPORTS_REQUESTS_MANAGER.hasTeleportPreferenceTarget(preference, player.getUniqueId(), target.getUniqueId()).whenComplete((bool, throwable) -> {
                if (throwable != null) return;
                if (bool) {
                    TELEPORTS_REQUESTS_MANAGER.removeTeleportPreferenceTarget(preference, player.getUniqueId(), target.getUniqueId());
                    if (preference.getRemoved() != null)
                        preference.getRemoved().send(player, "target", target.getDisplayName());
                } else {
                    TELEPORTS_REQUESTS_MANAGER.addTeleportPreferenceTarget(preference, player.getUniqueId(), target.getUniqueId());
                    if (preference.getAdded() != null)
                        preference.getAdded().send(player, "target", target.getDisplayName());
                }
            });
        }, () -> Message.USER_NOT_FOUND.send(sender, "target", args[0]));
    }

    private void toggle(Player player) {
        TELEPORTS_REQUESTS_MANAGER.getRequestsPreference(preference, player.getUniqueId()).whenComplete((allowed, throwable) -> {
            if (throwable != null) return;
            boolean reverse = !allowed;
            TELEPORTS_REQUESTS_MANAGER.setRequestsPreference(preference, player.getUniqueId(), reverse);
            String status = (reverse ? Message.PREFERENCES_STATUS_YES : Message.PREFERENCES_STATUS_NO).getMessage(false);
            preference.getToggled().send(player, "status", status);
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!preference.hasTable()) return new ArrayList<>();
        List<String> list = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).toList());
        list.add("list");
        return list.stream().filter(s -> args.length == 0 || s.toLowerCase().startsWith(args[args.length - 1])).toList();
    }
}
