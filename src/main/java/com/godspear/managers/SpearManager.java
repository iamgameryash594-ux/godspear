package com.godspear.managers;

import com.godspear.GodSpear;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpearManager {
    
    private final GodSpear plugin;
    private final File dataFile;
    private final File killsFile;
    private FileConfiguration dataConfig;
    private FileConfiguration killsConfig;
    
    @Getter
    private UUID currentSpearUUID;
    @Getter
    private UUID currentOwner;
    @Getter
    private Location lastKnownLocation;
    
    private final Map<UUID, Integer> killCounts = new HashMap<>();
    
    public SpearManager(GodSpear plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "spear_data.yml");
        this.killsFile = new File(plugin.getDataFolder(), "kills.yml");
        
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        loadData();
    }
    
    public void loadData() {
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            
            if (dataConfig.contains("spear_uuid")) {
                currentSpearUUID = UUID.fromString(dataConfig.getString("spear_uuid"));
            }
            
            if (dataConfig.contains("owner_uuid")) {
                currentOwner = UUID.fromString(dataConfig.getString("owner_uuid"));
            }
            
            if (dataConfig.contains("location")) {
                String worldName = dataConfig.getString("location.world");
                double x = dataConfig.getDouble("location.x");
                double y = dataConfig.getDouble("location.y");
                double z = dataConfig.getDouble("location.z");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    lastKnownLocation = new Location(world, x, y, z);
                }
            }
            
            if (!killsFile.exists()) {
                killsFile.createNewFile();
            }
            killsConfig = YamlConfiguration.loadConfiguration(killsFile);
            
            if (killsConfig.contains("kills")) {
                for (String key : killsConfig.getConfigurationSection("kills").getKeys(false)) {
                    UUID uuid = UUID.fromString(key);
                    int kills = killsConfig.getInt("kills." + key);
                    killCounts.put(uuid, kills);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load spear data: " + e.getMessage());
        }
    }
    
    public void saveData() {
        try {
            if (currentSpearUUID != null) {
                dataConfig.set("spear_uuid", currentSpearUUID.toString());
            } else {
                dataConfig.set("spear_uuid", null);
            }
            
            if (currentOwner != null) {
                dataConfig.set("owner_uuid", currentOwner.toString());
            } else {
                dataConfig.set("owner_uuid", null);
            }
            
            if (lastKnownLocation != null) {
                dataConfig.set("location.world", lastKnownLocation.getWorld().getName());
                dataConfig.set("location.x", lastKnownLocation.getX());
                dataConfig.set("location.y", lastKnownLocation.getY());
                dataConfig.set("location.z", lastKnownLocation.getZ());
            } else {
                dataConfig.set("location", null);
            }
            
            dataConfig.save(dataFile);
            
            killsConfig.set("kills", null);
            for (Map.Entry<UUID, Integer> entry : killCounts.entrySet()) {
                killsConfig.set("kills." + entry.getKey().toString(), entry.getValue());
            }
            killsConfig.save(killsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save spear data: " + e.getMessage());
        }
    }
    
    public ItemStack createNewSpear() {
        if (currentSpearUUID != null) {
            return null;
        }
        
        currentSpearUUID = UUID.randomUUID();
        ItemStack spear = plugin.getItemManager().createGodSpear(currentSpearUUID);
        saveData();
        return spear;
    }
    
    public void resetSpear() {
        currentSpearUUID = null;
        currentOwner = null;
        lastKnownLocation = null;
        saveData();
    }
    
    public void setOwner(UUID owner) {
        this.currentOwner = owner;
        saveData();
    }
    
    public void updateLocation(Location location) {
        this.lastKnownLocation = location;
        saveData();
    }
    
    public boolean isValidSpear(ItemStack item) {
        if (!plugin.getItemManager().isGodSpear(item)) {
            return false;
        }
        
        UUID itemUUID = plugin.getItemManager().getSpearUUID(item);
        return itemUUID != null && itemUUID.equals(currentSpearUUID);
    }
    
    public void invalidateSpear(ItemStack item) {
        plugin.getItemManager().removeGodSpearData(item);
    }
    
    public void addKill(UUID player) {
        killCounts.put(player, killCounts.getOrDefault(player, 0) + 1);
        saveData();
    }
    
    public int getKills(UUID player) {
        return killCounts.getOrDefault(player, 0);
    }
    
    public int getTotalKills() {
        return killCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public void respawnSpear() {
        if (currentSpearUUID == null) {
            return;
        }
        
        Location spawnLocation = getSpawnLocation();
        if (spawnLocation == null) {
            plugin.getLogger().warning("Could not find valid spawn location for God Spear!");
            return;
        }
        
        ItemStack spear = plugin.getItemManager().createGodSpear(currentSpearUUID);
        Item droppedItem = spawnLocation.getWorld().dropItem(spawnLocation, spear);
        droppedItem.setPickupDelay(0);
        droppedItem.setUnlimitedLifetime(true);
        
        updateLocation(spawnLocation);
        plugin.getLogger().info("God Spear respawned at " + formatLocation(spawnLocation));
    }
    
    private Location getSpawnLocation() {
        String mode = plugin.getConfigManager().getRespawnMode();
        
        if ("spawn".equalsIgnoreCase(mode)) {
            World world = Bukkit.getWorlds().get(0);
            return world.getSpawnLocation();
        } else {
            World world = Bukkit.getWorlds().get(0);
            Location spawn = world.getSpawnLocation();
            int radius = plugin.getConfigManager().getSafeLocationRadius();
            
            for (int i = 0; i < 10; i++) {
                int x = spawn.getBlockX() + (int) (Math.random() * radius * 2 - radius);
                int z = spawn.getBlockZ() + (int) (Math.random() * radius * 2 - radius);
                int y = world.getHighestBlockYAt(x, z);
                
                Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                if (loc.getBlock().getType() == Material.AIR) {
                    return loc;
                }
            }
            
            return spawn;
        }
    }
    
    public String formatLocation(Location loc) {
        if (loc == null) {
            return "Unknown";
        }
        return String.format("%s (%.1f, %.1f, %.1f)", 
            loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }
    
    public String getOwnerName() {
        if (currentOwner == null) {
            return "None";
        }
        
        Player player = Bukkit.getPlayer(currentOwner);
        if (player != null) {
            return player.getName();
        }
        
        return Bukkit.getOfflinePlayer(currentOwner).getName();
    }
}
