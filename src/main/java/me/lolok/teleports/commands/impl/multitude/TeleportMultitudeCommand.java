package me.lolok.teleports.commands.impl.multitude;

import me.lolok.teleports.commands.AbstractBaseCommand;
import me.lolok.teleports.configurations.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public abstract class TeleportMultitudeCommand extends AbstractBaseCommand {
    public TeleportMultitudeCommand(JavaPlugin plugin, String command, String usage) {
        super(plugin, command, "fastteleports." + command, usage, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<? extends Player> players = getSpecificPlayers(sender, args.length > 1 ? args[0] : null);
        if (players == null)
            return;
        Location location = getLocation(sender, args.length > 1 ? args[1] : args[0]);
        if (location != null) {
            players.forEach(player -> player.teleportAsync(location));
            Message.TELEPORT_MULTITUDE.send(sender, "amount", String.valueOf(players.size()));
        }
    }

    private Location getLocation(CommandSender sender, String input) {
        String worldName = input.substring(0, input.indexOf(":"));
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Message.WRONG_USAGE.send(sender, "<world:x,y,z>");
            return null;
        }
        String[] coords = input.substring(input.indexOf(":") + 1).split(",");
        if (coords.length < 3) {
            Message.INVALID_NUMBER.send(sender);
            return null;
        }
        double x, y, z;
        try {
            x = Double.parseDouble(coords[0]);
            y = Double.parseDouble(coords[1]);
            z = Double.parseDouble(coords[2]);
        } catch (NumberFormatException e) {
            Message.INVALID_NUMBER.send(sender);
            return null;
        }
        return new Location(world, x, y, z);
    }

    protected abstract Collection<? extends Player> getSpecificPlayers(CommandSender sender, String input);
}
