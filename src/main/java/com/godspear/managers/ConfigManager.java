package com.godspear.managers;

import com.godspear.GodSpear;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class ConfigManager {
    
    private final GodSpear plugin;
    private final FileConfiguration config;
    
    private final double baseDamage;
    private final double velocityMultiplier;
    private final double fallMultiplier;
    private final double maxDamage;
    private final int trueDamagePercent;
    private final double criticalMultiplier;
    
    private final double lungeCooldown;
    private final double dashCooldown;
    private final double teleportCooldown;
    
    private final boolean lightningOnHit;
    private final boolean particles;
    private final boolean sounds;
    private final int shieldCrushChance;
    private final double knockbackMultiplier;
    private final boolean explosionVisual;
    
    private final double lungeHorizontal;
    private final double lungeVertical;
    private final double windChargeWindow;
    private final double fallDamageImmunity;
    
    private final String respawnMode;
    private final int safeLocationRadius;
    
    private final int strengthDuration;
    private final int strengthAmplifier;
    private final int speedDuration;
    private final int speedAmplifier;
    private final int absorptionDuration;
    private final int absorptionAmplifier;
    
    private final boolean passiveSpeedEnabled;
    private final int passiveSpeedAmplifier;
    
    private final String itemName;
    private final int customModelData;
    
    public ConfigManager(GodSpear plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        
        this.baseDamage = config.getDouble("damage.base", 22.0);
        this.velocityMultiplier = config.getDouble("damage.velocity_multiplier", 2.5);
        this.fallMultiplier = config.getDouble("damage.fall_multiplier", 1.8);
        this.maxDamage = config.getDouble("damage.max_damage", 40.0);
        this.trueDamagePercent = config.getInt("damage.true_damage_percent", 10);
        this.criticalMultiplier = config.getDouble("damage.critical_multiplier", 1.5);
        
        this.lungeCooldown = config.getDouble("cooldowns.lunge", 1.2);
        this.dashCooldown = config.getDouble("cooldowns.dash", 5.0);
        this.teleportCooldown = config.getDouble("cooldowns.teleport", 8.0);
        
        this.lightningOnHit = config.getBoolean("effects.lightning_on_hit", true);
        this.particles = config.getBoolean("effects.particles", true);
        this.sounds = config.getBoolean("effects.sounds", true);
        this.shieldCrushChance = config.getInt("effects.shield_crush_chance", 25);
        this.knockbackMultiplier = config.getDouble("effects.knockback_multiplier", 2.0);
        this.explosionVisual = config.getBoolean("effects.explosion_visual", true);
        
        this.lungeHorizontal = config.getDouble("lunge.horizontal_multiplier", 1.8);
        this.lungeVertical = config.getDouble("lunge.vertical_multiplier", 0.5);
        this.windChargeWindow = config.getDouble("lunge.wind_charge_window", 1.5);
        this.fallDamageImmunity = config.getDouble("lunge.fall_damage_immunity_duration", 3.0);
        
        this.respawnMode = config.getString("respawn.mode", "spawn");
        this.safeLocationRadius = config.getInt("respawn.safe_location_search_radius", 50);
        
        this.strengthDuration = config.getInt("buffs.strength_duration", 10);
        this.strengthAmplifier = config.getInt("buffs.strength_amplifier", 1);
        this.speedDuration = config.getInt("buffs.speed_duration", 10);
        this.speedAmplifier = config.getInt("buffs.speed_amplifier", 1);
        this.absorptionDuration = config.getInt("buffs.absorption_duration", 8);
        this.absorptionAmplifier = config.getInt("buffs.absorption_amplifier", 1);
        
        this.passiveSpeedEnabled = config.getBoolean("passive.speed_enabled", true);
        this.passiveSpeedAmplifier = config.getInt("passive.speed_amplifier", 0);
        
        this.itemName = config.getString("item.name", "&c&lGOD SPEAR");
        this.customModelData = config.getInt("item.custom_model_data", 1000);
    }
    
    public String getMessage(String path) {
        return colorize(config.getString("messages." + path, ""));
    }
    
    public String getMessage(String path, String placeholder, String value) {
        return getMessage(path).replace("{" + placeholder + "}", value);
    }
    
    private String colorize(String text) {
        return text.replace("&", "§");
    }
}
