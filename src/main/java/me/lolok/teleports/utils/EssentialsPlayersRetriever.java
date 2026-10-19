package me.lolok.teleports.utils;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class EssentialsPlayersRetriever {
    private final static @Nullable Plugin ESSENTIALS = Bukkit.getPluginManager().getPlugin("Essentials");

    public static Optional<? extends Player> get(String name) {
        Player main = Bukkit.getPlayer(name);
        if (main != null)
            return Optional.of(main);
        else
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> ChatColor.stripColor(ESSENTIALS != null ? ((Essentials) ESSENTIALS).getUser(player).getNick(false, false) : player.getName()).equalsIgnoreCase(name))
                    .findFirst();
    }
}
