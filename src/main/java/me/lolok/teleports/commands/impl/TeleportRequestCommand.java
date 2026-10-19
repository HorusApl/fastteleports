package me.lolok.teleports.commands.impl;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.commands.AbstractBaseCommand;
import me.lolok.teleports.configurations.files.SettingsConfiguration;
import me.lolok.teleports.configurations.messages.Message;
import me.lolok.teleports.database.managers.SQLTeleportsRequestsManager;
import me.lolok.teleports.teleports.ITeleportService;
import me.lolok.teleports.teleports.enums.TeleportPreference;
import me.lolok.teleports.teleports.requests.TeleportRequestType;
import me.lolok.teleports.utils.EssentialsPlayersRetriever;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportRequestCommand extends AbstractBaseCommand {
    private final TeleportRequestType type;

    public TeleportRequestCommand(JavaPlugin plugin, String command, TeleportRequestType type) {
        super(plugin, command, "fastteleports." + (command.substring(4)), "<target>", false);
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        EssentialsPlayersRetriever.get(args[0]).ifPresentOrElse(target -> {
            if (player.getUniqueId().equals(target.getUniqueId()) && !FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("settings", SettingsConfiguration.class).isAllowYourself()) {
                Message.SENDER_SAME_TO_TARGET.send(sender);
                return;
            }

            SQLTeleportsRequestsManager manager = FastTeleportsPlugin.getInstance().getSqlManager().getTeleportsRequestsManager();
            manager.getRequestsPreference(TeleportPreference.TOGGLE_REQUESTS, target.getUniqueId()).whenComplete((bool, throwable) -> {
               if (!bool)
                   Message.NOT_TOGGLED.sendWithActionbar(player, "target", target.getDisplayName());
               else {
                   manager.hasTeleportPreferenceTarget(TeleportPreference.TOGGLE_REQUESTS, target.getUniqueId(), player.getUniqueId()).whenComplete((active, throwable2) -> {
                       if (active) {
                           Message.NOT_TOGGLED.sendWithActionbar(player, "target", target.getDisplayName());
                           return;
                       }
                       ITeleportService service = FastTeleportsPlugin.getInstance().getTeleportService();
                       if (service.getTeleportRequest(player.getUniqueId(), target.getUniqueId()).isPresent()) {
                           Message.REQUEST_ALREADY_EXISTS.sendWithActionbar(player, "target", target.getDisplayName());
                           return;
                       }
                       service.addRequest(type, player.getUniqueId(), target.getUniqueId(), true);
                   });
               }
            });

        }, () -> Message.USER_NOT_FOUND.sendWithActionbar(player, "target", args[0]));
    }

}
