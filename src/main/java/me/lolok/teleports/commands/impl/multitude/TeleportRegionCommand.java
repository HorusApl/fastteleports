package me.lolok.teleports.commands.impl.multitude;

import me.lolok.teleports.worldguard.WorldGuardProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TeleportRegionCommand extends TeleportMultitudeCommand {
    public TeleportRegionCommand(JavaPlugin plugin) {
        super(plugin, "fasttpregion", "<region> <world destination:x,y,z>");
    }

    @Override
    protected Collection<? extends Player> getSpecificPlayers(CommandSender sender, String input) {
        return Bukkit.getOnlinePlayers().stream().filter(player -> WorldGuardProvider.getProvider().isInside(player, input)).toList();
    }
}
