package com.godspear.managers;

import com.godspear.GodSpear;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityManager {
    
    private final GodSpear plugin;
    private final Map<UUID, Long> windChargeUsage = new HashMap<>();
    private final Map<UUID, Long> fallDamageImmunity = new HashMap<>();
    
    public AbilityManager(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    public void performLunge(Player player) {
        ConfigManager config = plugin.getConfigManager();
        
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "lunge")) {
            double remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "lunge");
            player.sendMessage(config.getMessage("cooldown_active", "time", String.format("%.1f", remaining)));
            return;
        }
        
        if (!canUseAbility(player)) {
            player.sendMessage(config.getMessage("worldguard_blocked"));
            return;
        }
        
        Vector direction = player.getLocation().getDirection();
        Vector velocity = player.getVelocity();
        
        double horizontalMultiplier = config.getLungeHorizontal();
        double verticalMultiplier = config.getLungeVertical();
        
        boolean hasWindCharge = hasRecentWindCharge(player.getUniqueId());
        
        velocity.add(new Vector(
            direction.getX() * horizontalMultiplier,
            direction.getY() * verticalMultiplier,
            direction.getZ() * horizontalMultiplier
        ));
        
        player.setVelocity(velocity);
        
        setFallDamageImmunity(player.getUniqueId());
        
        if (config.isParticles()) {
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3, 0.05);
        }
        
        if (config.isSounds()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);
        }
        
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "lunge", config.getLungeCooldown());
    }
    
    public void performDash(Player player) {
        ConfigManager config = plugin.getConfigManager();
        
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "dash")) {
            double remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "dash");
            player.sendMessage(config.getMessage("cooldown_active", "time", String.format("%.1f", remaining)));
            return;
        }
        
        if (!canUseAbility(player)) {
            player.sendMessage(config.getMessage("worldguard_blocked"));
            return;
        }
        
        Vector direction = player.getLocation().getDirection();
        player.setVelocity(direction.multiply(2.0));
        
        if (config.isParticles()) {
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 20 || !player.isOnline()) {
                        cancel();
                        return;
                    }
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 3, 0.2, 0.2, 0.2, 0.01);
                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }
        
        if (config.isSounds()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.2f);
        }
        
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "dash", config.getDashCooldown());
    }
    
    public void performTeleport(Player player) {
        ConfigManager config = plugin.getConfigManager();
        
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "teleport")) {
            double remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "teleport");
            player.sendMessage(config.getMessage("cooldown_active", "time", String.format("%.1f", remaining)));
            return;
        }
        
        if (!canUseAbility(player)) {
            player.sendMessage(config.getMessage("worldguard_blocked"));
            return;
        }
        
        Vector direction = player.getLocation().getDirection();
        player.setVelocity(direction.setY(1.5).multiply(1.5));
        
        if (config.isParticles()) {
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
        }
        
        if (config.isSounds()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "teleport", config.getTeleportCooldown());
    }
    
    public void applyKillBuffs(Player killer, Player victim) {
        ConfigManager config = plugin.getConfigManager();
        
        int strengthDuration = config.getStrengthDuration() * 20;
        int strengthAmplifier = config.getStrengthAmplifier();
        killer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, strengthDuration, strengthAmplifier));
        
        int speedDuration = config.getSpeedDuration() * 20;
        int speedAmplifier = config.getSpeedAmplifier();
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedDuration, speedAmplifier));
        
        int absorptionDuration = config.getAbsorptionDuration() * 20;
        int absorptionAmplifier = config.getAbsorptionAmplifier();
        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, absorptionDuration, absorptionAmplifier));
        
        String title = config.getMessage("kill_title");
        String subtitle = config.getMessage("kill_subtitle", "victim", victim.getName());
        killer.sendTitle(title, subtitle, 10, 40, 10);
        
        plugin.getSpearManager().addKill(killer.getUniqueId());
    }
    
    public void applyPassiveSpeed(Player player) {
        ConfigManager config = plugin.getConfigManager();
        
        if (!config.isPassiveSpeedEnabled()) return;
        
        int amplifier = config.getPassiveSpeedAmplifier();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier, false, false, false));
    }
    
    public void removePassiveSpeed(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }
    
    public void recordWindChargeUsage(UUID playerUUID) {
        windChargeUsage.put(playerUUID, System.currentTimeMillis());
    }
    
    public boolean hasRecentWindCharge(UUID playerUUID) {
        Long lastUsage = windChargeUsage.get(playerUUID);
        if (lastUsage == null) return false;
        
        double windowSeconds = plugin.getConfigManager().getWindChargeWindow();
        long windowMillis = (long) (windowSeconds * 1000);
        
        return System.currentTimeMillis() - lastUsage <= windowMillis;
    }
    
    public void setFallDamageImmunity(UUID playerUUID) {
        double duration = plugin.getConfigManager().getFallDamageImmunity();
        fallDamageImmunity.put(playerUUID, System.currentTimeMillis() + (long) (duration * 1000));
    }
    
    public boolean hasFallDamageImmunity(UUID playerUUID) {
        Long expiry = fallDamageImmunity.get(playerUUID);
        if (expiry == null) return false;
        
        if (System.currentTimeMillis() >= expiry) {
            fallDamageImmunity.remove(playerUUID);
            return false;
        }
        return true;
    }
    
    private boolean canUseAbility(Player player) {
        return !plugin.getWorldGuardManager().isInProtectedRegion(player);
    }
}
