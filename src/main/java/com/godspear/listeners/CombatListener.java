package com.godspear.listeners;

import com.godspear.GodSpear;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class CombatListener implements Listener {
    
    private final GodSpear plugin;
    private final Random random = new Random();
    
    public CombatListener(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        
        if (!plugin.getSpearManager().isValidSpear(weapon)) {
            return;
        }
        
        LivingEntity victim = (LivingEntity) event.getEntity();
        
        boolean isCritical = plugin.getDamageManager().isCriticalHit(attacker);
        double totalDamage = plugin.getDamageManager().calculateDamage(attacker, victim, isCritical);
        
        event.setDamage(0);
        plugin.getDamageManager().applyDamage(victim, totalDamage);
        
        applyEffects(attacker, victim);
        
        if (victim instanceof Player) {
            Player victimPlayer = (Player) victim;
            if (victimPlayer.isBlocking()) {
                handleShieldCrush(victimPlayer);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer == null) {
            return;
        }
        
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (!plugin.getSpearManager().isValidSpear(weapon)) {
            return;
        }
        
        plugin.getAbilityManager().applyKillBuffs(killer, victim);
    }
    
    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        if (plugin.getAbilityManager().hasFallDamageImmunity(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    private void applyEffects(Player attacker, LivingEntity victim) {
        if (plugin.getConfigManager().isLightningOnHit()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    victim.getWorld().strikeLightningEffect(victim.getLocation());
                });
            });
        }
        
        if (plugin.getConfigManager().isParticles()) {
            victim.getWorld().spawnParticle(Particle.EXPLOSION, victim.getLocation(), 1);
        }
        
        if (plugin.getConfigManager().isSounds()) {
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
        }
        
        double knockbackMultiplier = plugin.getConfigManager().getKnockbackMultiplier();
        Vector knockback = attacker.getLocation().getDirection().multiply(knockbackMultiplier);
        knockback.setY(0.3);
        victim.setVelocity(victim.getVelocity().add(knockback));
    }
    
    private void handleShieldCrush(Player player) {
        int chance = plugin.getConfigManager().getShieldCrushChance();
        
        if (random.nextInt(100) < chance) {
            player.setCooldown(player.getInventory().getItemInOffHand().getType(), 100);
            
            if (plugin.getConfigManager().isSounds()) {
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
            }
        }
    }
}
