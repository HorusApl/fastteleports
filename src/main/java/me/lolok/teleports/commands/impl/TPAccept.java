package me.lolok.teleports.commands.impl;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.commands.AbstractBaseCommand;
import me.lolok.teleports.configurations.messages.Message;
import me.lolok.teleports.teleports.ITeleportService;
import me.lolok.teleports.utils.EssentialsPlayersRetriever;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TPAccept extends AbstractBaseCommand {
    public TPAccept(JavaPlugin plugin) {
        super(plugin, "fasttpaccept", "fastteleports.tpaccept", "[target]", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ITeleportService service = FastTeleportsPlugin.getInstance().getTeleportService();
        Player player = (Player) sender;
        if (args.length == 0)
            service.getRecentTeleportRequestFromTarget(player.getUniqueId()).ifPresentOrElse(request -> {
                if (request.accept())
                    service.removeRequest(request.getSender(), request.getTarget());
            }, () -> Message.NOTHING_TO_ACCEPT_WITHOUT_SENDER.sendWithActionbar(player));
        else
            EssentialsPlayersRetriever.get(args[0]).ifPresentOrElse(other -> service.accept(other, player), () -> Message.USER_NOT_FOUND.sendWithActionbar(player, "target", args[0]));
    }
}
