package com.godspear;

import com.godspear.commands.CommandManager;
import com.godspear.listeners.CombatListener;
import com.godspear.listeners.MoveListener;
import com.godspear.listeners.PlayerListener;
import com.godspear.listeners.WorldListener;
import com.godspear.managers.*;
import com.godspear.placeholders.GodSpearExpansion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class GodSpear extends JavaPlugin {

    private ItemManager itemManager;
    private AbilityManager abilityManager;
    private DamageManager damageManager;
    private SpearManager spearManager;
    private CooldownManager cooldownManager;
    private ConfigManager configManager;
    private WorldGuardManager worldGuardManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.cooldownManager = new CooldownManager();
        this.itemManager = new ItemManager(this);
        this.spearManager = new SpearManager(this);
        this.damageManager = new DamageManager(this);
        this.abilityManager = new AbilityManager(this);
        this.worldGuardManager = new WorldGuardManager();

        registerCommands();
        registerListeners();
        registerPlaceholders();

        spearManager.loadData();

        getLogger().info("§c§lGodSpear §fhas been enabled! Only one God Spear exists.");
    }

    @Override
    public void onDisable() {
        if (spearManager != null) {
            spearManager.saveData();
        }
        getLogger().info("GodSpear disabled.");
    }

    private void registerCommands() {
        CommandManager commandManager = new CommandManager(this);
        getCommand("godspear").setExecutor(commandManager);
        getCommand("godspear").setTabCompleter(commandManager);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new CombatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                new GodSpearExpansion(this).register();
                getLogger().info("PlaceholderAPI expansion registered!");
            } catch (Exception e) {
                getLogger().warning("Failed to register PlaceholderAPI: " + e.getMessage());
            }
        }
    }

    public void reload() {
        reloadConfig();
        configManager = new ConfigManager(this);
        spearManager.loadData();
    }
}
