package me.lolok.teleports.commands.impl;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.commands.AbstractBaseCommand;
import me.lolok.teleports.database.ISQLManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ExecuteDatabaseUpdateCommand extends AbstractBaseCommand {
    public ExecuteDatabaseUpdateCommand(JavaPlugin plugin) {
        super(plugin, "fasttpdatabaseupdate", "fastteleports.databaseupdate", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ISQLManager manager = FastTeleportsPlugin.getInstance().getSqlManager();
        manager.executeUpdate("ALTER TABLE TeleportPreferences ADD action_bar BIT NOT NULL DEFAULT 1");
        manager.executeUpdate("ALTER TABLE TeleportPreferences RENAME COLUMN requests TO old_requests");
        manager.executeUpdate("ALTER TABLE TeleportPreferences ADD requests BIT NOT NULL DEFAULT 1");
        manager.executeUpdate("UPDATE TeleportPreferences SET requests = old_requests");
        manager.executeUpdate("ALTER TABLE TeleportPreferences DROP COLUMN old_requests");
        sender.sendMessage("Updated the database to version 1.1.0 of this plugin");
    }
}
