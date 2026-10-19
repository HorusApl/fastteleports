package me.lolok.teleports.commands.impl.multitude;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TeleportAllCommand extends TeleportMultitudeCommand {
    public TeleportAllCommand(JavaPlugin plugin) {
        super(plugin, "fasttpall", "<world:x,y,z>");
    }

    @Override
    protected Collection<? extends Player> getSpecificPlayers(CommandSender sender, String input) {
        return Bukkit.getOnlinePlayers();
    }
}
