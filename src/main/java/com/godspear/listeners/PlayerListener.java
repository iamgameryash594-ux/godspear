package com.godspear.listeners;

import com.godspear.GodSpear;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    
    private final GodSpear plugin;
    
    public PlayerListener(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && plugin.getItemManager().isGodSpear(item)) {
                if (!plugin.getSpearManager().isValidSpear(item)) {
                    plugin.getSpearManager().invalidateSpear(item);
                } else {
                    plugin.getSpearManager().setOwner(player.getUniqueId());
                    plugin.getSpearManager().updateLocation(player.getLocation());
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && plugin.getSpearManager().isValidSpear(item)) {
                plugin.getSpearManager().updateLocation(player.getLocation());
                plugin.getSpearManager().saveData();
            }
        }
        
        plugin.getCooldownManager().clearCooldowns(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        for (ItemStack item : event.getDrops()) {
            if (item != null && plugin.getSpearManager().isValidSpear(item)) {
                plugin.getSpearManager().updateLocation(player.getLocation());
                plugin.getSpearManager().setOwner(null);
            }
        }
    }
}
