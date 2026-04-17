package com.godspear.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardManager {
    
    private final boolean worldGuardEnabled;
    
    public WorldGuardManager() {
        this.worldGuardEnabled = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }
    
    public boolean isInProtectedRegion(Player player) {
        if (!worldGuardEnabled) {
            return false;
        }
        
        try {
            Location loc = player.getLocation();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
            
            return set.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
