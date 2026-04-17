package com.godspear.listeners;

import com.godspear.GodSpear;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {
    
    private final GodSpear plugin;
    
    public WorldListener(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        
        if (plugin.getSpearManager().isValidSpear(itemStack)) {
            event.setCancelled(true);
            item.setTicksLived(1);
            item.setUnlimitedLifetime(true);
            
            plugin.getSpearManager().updateLocation(item.getLocation());
        }
    }
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (org.bukkit.entity.Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                ItemStack itemStack = item.getItemStack();
                
                if (plugin.getSpearManager().isValidSpear(itemStack)) {
                    plugin.getSpearManager().updateLocation(item.getLocation());
                    plugin.getSpearManager().saveData();
                }
            }
        }
    }
}
