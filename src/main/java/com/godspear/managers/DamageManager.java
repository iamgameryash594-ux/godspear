package com.godspear.managers;

import com.godspear.GodSpear;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DamageManager {
    
    private final GodSpear plugin;
    
    public DamageManager(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    public double calculateDamage(Player attacker, LivingEntity victim, boolean isCritical) {
        ConfigManager config = plugin.getConfigManager();
        
        double baseDamage = config.getBaseDamage();
        
        Vector velocity = attacker.getVelocity();
        double velocityMagnitude = Math.sqrt(
            velocity.getX() * velocity.getX() + 
            velocity.getY() * velocity.getY() + 
            velocity.getZ() * velocity.getZ()
        );
        double velocityDamage = velocityMagnitude * config.getVelocityMultiplier();
        
        double fallDistance = attacker.getFallDistance();
        double fallDamage = fallDistance * config.getFallMultiplier();
        
        double totalDamage = baseDamage + velocityDamage + fallDamage;
        
        if (isCritical) {
            totalDamage *= config.getCriticalMultiplier();
        }
        
        if (totalDamage > config.getMaxDamage()) {
            totalDamage = config.getMaxDamage();
        }
        
        return totalDamage;
    }
    
    public void applyDamage(LivingEntity victim, double totalDamage) {
        ConfigManager config = plugin.getConfigManager();
        
        double trueDamagePercent = config.getTrueDamagePercent() / 100.0;
        double trueDamage = totalDamage * trueDamagePercent;
        double normalDamage = totalDamage - trueDamage;
        
        victim.damage(normalDamage);
        
        double newHealth = victim.getHealth() - trueDamage;
        if (newHealth < 0) newHealth = 0;
        victim.setHealth(newHealth);
    }
    
    public boolean isCriticalHit(Player attacker) {
        return attacker.getFallDistance() > 0 && 
               !attacker.isOnGround() && 
               attacker.getVelocity().getY() < 0;
    }
}
