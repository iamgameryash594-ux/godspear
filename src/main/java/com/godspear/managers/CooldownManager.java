package com.godspear.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    
    public void setCooldown(UUID player, String ability, double seconds) {
        cooldowns.computeIfAbsent(player, k -> new HashMap<>())
            .put(ability, System.currentTimeMillis() + (long) (seconds * 1000));
    }
    
    public boolean hasCooldown(UUID player, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(player);
        if (playerCooldowns == null) {
            return false;
        }
        
        Long expiry = playerCooldowns.get(ability);
        if (expiry == null) {
            return false;
        }
        
        if (System.currentTimeMillis() >= expiry) {
            playerCooldowns.remove(ability);
            return false;
        }
        
        return true;
    }
    
    public double getRemainingCooldown(UUID player, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(player);
        if (playerCooldowns == null) {
            return 0;
        }
        
        Long expiry = playerCooldowns.get(ability);
        if (expiry == null) {
            return 0;
        }
        
        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000.0 : 0;
    }
    
    public void clearCooldowns(UUID player) {
        cooldowns.remove(player);
    }
    
    public void clearCooldown(UUID player, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(player);
        if (playerCooldowns != null) {
            playerCooldowns.remove(ability);
        }
    }
}
