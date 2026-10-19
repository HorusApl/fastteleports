package me.lolok.teleports.configurations.messages;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.configurations.files.MessagesConfiguration;
import me.lolok.teleports.teleports.enums.TeleportPreference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public enum Message {
    PREFERENCES_STATUS_YES("preferences.status.allow"),
    PREFERENCES_STATUS_NO("preferences.status.deny"),
    PREFERENCES_LIST_EMPTY("preferences.list.empty"),
    PREFERENCES_LIST_HEADER("preferences.list.header"),
    PREFERENCES_REQUESTS_TOGGLED("preferences.requests.toggled"),
    PREFERENCES_REQUESTS_ADDED("preferences.requests.added"),
    PREFERENCES_REQUESTS_REMOVED("preferences.requests.removed"),
    PREFERENCES_AUTO_ACCEPT_TOGGLED("preferences.auto-accept.toggled"),
    PREFERENCES_AUTO_ACCEPT_ADDED("preferences.auto-accept.added"),
    PREFERENCES_AUTO_ACCEPT_REMOVED("preferences.auto-accept.removed"),
    PREFERENCES_ACTION_BAR_TOGGLED("preferences.action-bar.toggled"),
    TELEPORT_WARN_MESSAGE("teleport.warn.message"),
    TELEPORT_WARN_HOVER("teleport.warn.hover"),
    TELEPORT_BUTTONS_ACCEPT("teleport.buttons.accept"),
    TELEPORT_BUTTONS_ACCEPT_HOVER("teleport.buttons.accept-hover"),
    TELEPORT_BUTTONS_DENY("teleport.buttons.deny"),
    TELEPORT_BUTTONS_DENY_HOVER("teleport.buttons.deny-hover"),
    TELEPORT_EXPIRED("teleport.expired"),
    TELEPORT_SENT("teleport.sent"),
    TELEPORT_CANCELLED_WITH_TARGET("teleport.cancelled.with-target"),
    TELEPORT_CANCELLED_WITHOUT_TARGET("teleport.cancelled.without-target"),
    TELEPORT_RECEIVED_TO("teleport.received.to"),
    TELEPORT_RECEIVED_HERE("teleport.received.here"),
    TELEPORT_ACCEPTED_YOU("teleport.accepted.you"),
    TELEPORT_ACCEPTED_OTHER("teleport.accepted.other"),
    TELEPORT_DENIED_YOU("teleport.denied.you"),
    TELEPORT_DENIED_OTHER("teleport.denied.other"),
    TELEPORT_MULTITUDE("teleport.multitude"),
    UNEXPECTED_ERROR("errors.unexpected-error"),
    NOT_TOGGLED("errors.not-toggled"),
    REQUEST_ALREADY_EXISTS("errors.request-already-exists"),
    NOTHING_TO_CANCEL("errors.nothing-to-cancel"),
    NOTHING_TO_CANCEL_WITHOUT_TARGET("errors.nothing-to-cancel-without-target"),
    NOTHING_TO_ACCEPT("errors.nothing-to-accept"),
    NOTHING_TO_ACCEPT_WITHOUT_SENDER("errors.nothing-to-accept-without-sender"),
    NOTHING_TO_DENY("errors.nothing-to-deny"),
    NOTHING_TO_DENY_WITHOUT_SENDER("errors.nothing-to-deny-without-sender"),
    EXECUTE_AS_PLAYER("errors.execute-as-player"),
    WRONG_USAGE("errors.wrong-usage"),
    PERMISSION_DENIED("errors.permission-denied"),
    INVALID_NUMBER("errors.invalid-number"),
    UNKNOWN_WORLD("errors.unknown-world"),
    UNKNOWN_REGION("errors.unknown-region"),
    USER_NOT_ONLINE_ANYMORE("errors.user-not-online-anymore"),
    SENDER_SAME_TO_TARGET("errors.sender-same-to-target"),
    USER_NOT_FOUND("errors.user-not-found");

    private final static MessagesConfiguration MESSAGES_CONFIGURATION = FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("messages", MessagesConfiguration.class);
    private final static MessagesConfiguration ACTION_BAR_MESSAGES_CONFIGURATION = FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("actionbar", MessagesConfiguration.class);
    private final static boolean PLACEHOLDERS_ENABLED = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

    private final String path;

    public String getMessage(boolean actionbar, String... placeholders) {
        String message = Objects.requireNonNull((actionbar ? ACTION_BAR_MESSAGES_CONFIGURATION : MESSAGES_CONFIGURATION).getConfiguration().getString(path));
        return parse(message, actionbar, placeholders);
    }

    public Component getComponent(boolean actionbar, String... placeholders) {
        String message = getMessage(actionbar, placeholders);
        return actionbar ? MiniMessage.miniMessage().deserialize(message) : LegacyComponentSerializer.legacySection().deserialize(message);
    }

    public void send(CommandSender target, String... placeholders) {
        String message = getMessage(false, placeholders);
        if (PLACEHOLDERS_ENABLED && target instanceof Player)
            message = PlaceholderAPI.setPlaceholders((Player) target, message);
        target.sendMessage(message);
    }

    public void sendWithActionbar(Player target, String... placeholders) {
        send(target, placeholders);
        FastTeleportsPlugin.getInstance().getSqlManager().getTeleportsRequestsManager().getRequestsPreference(TeleportPreference.ACTION_BAR, target.getUniqueId()).whenComplete((allowed, throwable) -> {
            if (throwable != null)
                throwable.printStackTrace();
            else if (allowed) {
                Component component = getComponent(true, placeholders);
                target.sendActionBar(component);
            }
        });
    }

    public void sendList(CommandSender target, String... placeholders) {
        List<String> messages = Objects.requireNonNull(MESSAGES_CONFIGURATION.getConfiguration().getStringList(path));
        for (String message : messages)
            target.sendMessage(parse(message, false, placeholders));
    }

    public static String parse(String message, boolean minimessage, CommandSender target, String... placeholders) {
        message = parse(message, minimessage, placeholders);
        return PLACEHOLDERS_ENABLED && target instanceof Player ? PlaceholderAPI.setPlaceholders((Player) target, message) : message;
    }

    public static String parse(String message, boolean minimessage, String... placeholders) {
        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i++) {
                if (i % 2 != 0) continue;
                String placeholder = "%" + placeholders[i] + "%", value = placeholders[i + 1];
                if (minimessage) {
                    value = value
                            .replaceAll("§l", "<b>").replaceAll("§o", "<i>").replaceAll("§n", "<u>")
                            .replaceAll("§m", "<st>").replaceAll("§k", "<obf>").replaceAll("§r", "");
                    int index;
                    do {
                        index = value.indexOf("§x");
                        if (index >= 0) {
                            String replacement = value.substring(index, index + 14);
                            String replace = String.format("<#%s>", replacement.substring(2).replace("§", ""));
                            value = value.replace(replacement, replace);
                        }
                    } while (index >= 0);
                    value = value
                            .replaceAll("§a", "<green>").replaceAll("§b", "<aqua>")
                            .replaceAll("§c", "<red>").replaceAll("§d", "<light_purple>")
                            .replaceAll("§e", "<yellow>").replaceAll("§f", "<white>")
                            .replaceAll("§0", "<black>").replaceAll("§1", "<dark_blue>")
                            .replaceAll("§2", "<dark_green>").replaceAll("§3", "<dark_aqua>")
                            .replaceAll("§4", "<dark_red>").replaceAll("§5", "<dark_purple>")
                            .replaceAll("§6", "<gold>").replaceAll("§7", "<gray>")
                            .replaceAll("§8", "<dark_gray>").replaceAll("§9", "<blue>");
                }
                message = message.replace(placeholder, value);
            }
        }
        return minimessage ? message : ChatColor.translateAlternateColorCodes('&', message);
    }
}
