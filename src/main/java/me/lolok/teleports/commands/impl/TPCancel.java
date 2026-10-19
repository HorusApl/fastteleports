package me.lolok.teleports.commands.impl;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.commands.AbstractBaseCommand;
import me.lolok.teleports.configurations.messages.Message;
import me.lolok.teleports.teleports.ITeleportService;
import me.lolok.teleports.teleports.requests.TeleportRequest;
import me.lolok.teleports.utils.EssentialsPlayersRetriever;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TPCancel extends AbstractBaseCommand {
    public TPCancel(JavaPlugin plugin) {
        super(plugin, "fasttpcancel", "fastteleports.tpcancel", "[target]", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ITeleportService service = FastTeleportsPlugin.getInstance().getTeleportService();
        Player player = (Player) sender;
        if (args.length == 0)
            service.getRecentTeleportRequestFromSender(player.getUniqueId()).ifPresentOrElse(request -> cancel(service, player, request), () -> Message.NOTHING_TO_CANCEL_WITHOUT_TARGET.sendWithActionbar(player));
        else
            EssentialsPlayersRetriever.get(args[0]).ifPresentOrElse(target -> service.getTeleportRequest(player.getUniqueId(), target.getUniqueId()).ifPresentOrElse(request -> cancel(service, player, request), () -> Message.NOTHING_TO_CANCEL.sendWithActionbar(player, "target", target.getDisplayName())), () -> Message.USER_NOT_FOUND.send(sender, "target", args[0]));
    }

    private void cancel(ITeleportService service, Player sender, TeleportRequest request) {
        request.stopExpirationTask();
        service.removeRequest(request.getSender(), request.getTarget());
        Player target = request.getTargetPlayer();
        (target != null ? Message.TELEPORT_CANCELLED_WITH_TARGET : Message.TELEPORT_CANCELLED_WITHOUT_TARGET).sendWithActionbar(sender, "target", target.getDisplayName());
    }
}
