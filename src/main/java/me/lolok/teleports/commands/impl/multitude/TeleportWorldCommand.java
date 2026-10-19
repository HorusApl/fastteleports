package me.lolok.teleports.commands.impl.multitude;

import me.lolok.teleports.configurations.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TeleportWorldCommand extends TeleportMultitudeCommand {
    public TeleportWorldCommand(JavaPlugin plugin) {
        super(plugin, "fasttpworld", "<world> <world destination:x,y,z>");
    }

    @Override
    protected Collection<? extends Player> getSpecificPlayers(CommandSender sender, String input) {
        World world = Bukkit.getWorld(input);
        if (world == null) {
            Message.UNKNOWN_WORLD.send(sender);
            return null;
        }
        return world.getPlayers();
    }
}
