package com.godspear.listeners;

import com.godspear.GodSpear;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class MoveListener implements Listener {
    
    private final GodSpear plugin;
    
    public MoveListener(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !plugin.getSpearManager().isValidSpear(item)) {
            return;
        }
        
        Action action = event.getAction();
        
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (!player.isOnGround() || player.isSprinting()) {
                event.setCancelled(true);
                plugin.getAbilityManager().performLunge(player);
            }
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            if (player.isSneaking()) {
                plugin.getAbilityManager().performTeleport(player);
            } else {
                plugin.getAbilityManager().performDash(player);
            }
        }
    }
    
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (oldItem != null && plugin.getSpearManager().isValidSpear(oldItem)) {
            plugin.getAbilityManager().removePassiveSpeed(player);
        }
        
        if (newItem != null && plugin.getSpearManager().isValidSpear(newItem)) {
            plugin.getAbilityManager().applyPassiveSpeed(player);
        }
    }
}
