package me.lolok.teleports.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldGuardProviderImpl implements WorldGuardProvider {
    @Override
    public boolean isInside(Player player, String regionName) {
        if (!isEnabled())
            return false;
        World world = BukkitAdapter.adapt(player.getWorld());
        BlockVector3 vector = BukkitAdapter.asBlockVector(player.getLocation());
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (manager == null)
            return false;
        ApplicableRegionSet set = manager.getApplicableRegions(vector);
        for (ProtectedRegion region : set)
            if (region.getId().equalsIgnoreCase(regionName))
                return true;
        return false;
    }

    private boolean isEnabled() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard");
    }
}
