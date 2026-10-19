package me.lolok.teleports.commands;

import me.lolok.teleports.Service;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface BaseCommand extends CommandExecutor, TabCompleter, Service {
    ExecutorService COMMANDS_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    String getCommand();

    String getPermission();

    String getUsage();

    boolean isConsoleAllowed();

    void execute(CommandSender sender, String[] args);
}
