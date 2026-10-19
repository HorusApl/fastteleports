package me.lolok.teleports.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.teleports.configurations.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public abstract class AbstractBaseCommand implements BaseCommand {
    private final JavaPlugin plugin;
    private final String command, permission, usage;
    private final boolean consoleAllowed;

    @Override
    public void enable() {
        Objects.requireNonNull(plugin.getServer().getPluginCommand(command)).setExecutor(this);
    }

    @Override
    public void disable() {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!consoleAllowed && !(sender instanceof Player)) {
            Message.EXECUTE_AS_PLAYER.send(sender);
            return true;
        }
        if (sender instanceof Player player && permission != null && !player.hasPermission(permission)) {
            Message.PERMISSION_DENIED.send(sender);
            return true;
        }
        if (this.usage != null && isCorrectUsage(sender, args))
            COMMANDS_EXECUTOR_SERVICE.execute(() -> execute(sender, args));
        return true;
    }

    public abstract void execute(CommandSender sender, String[] args);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        return Bukkit.getOnlinePlayers().stream().filter(player -> args.length == 0 || player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()) || player.getDisplayName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())).map(Player::getName).toList();
    }

    private boolean isCorrectUsage(CommandSender sender, String[] args) {
        int requirements = 0;
        for (String s : this.usage.split(" "))
            if (s.startsWith("<") && s.endsWith(">")) ++requirements;
        if (args.length < requirements) {
            Message.WRONG_USAGE.send(sender, "usage", this.usage);
            return false;
        }
        return true;
    }
}
