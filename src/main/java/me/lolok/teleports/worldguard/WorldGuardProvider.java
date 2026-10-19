package me.lolok.teleports.worldguard;


import org.bukkit.entity.Player;

public interface WorldGuardProvider {
    WorldGuardProvider DEFAULT_PROVIDER = new WorldGuardProviderImpl();

    static WorldGuardProvider getProvider() {
        return DEFAULT_PROVIDER;
    }

    boolean isInside(Player player, String regionName);
}
