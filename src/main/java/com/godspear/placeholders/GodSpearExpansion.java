package com.godspear.placeholders;

import com.godspear.GodSpear;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodSpearExpansion extends PlaceholderExpansion {
    
    private final GodSpear plugin;
    
    public GodSpearExpansion(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    @Override
    @NotNull
    public String getIdentifier() {
        return "godspear";
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return "GodSpear";
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.equals("owner")) {
            return plugin.getSpearManager().getOwnerName();
        }
        
        if (identifier.equals("kills")) {
            if (player == null) {
                return String.valueOf(plugin.getSpearManager().getTotalKills());
            }
            return String.valueOf(plugin.getSpearManager().getKills(player.getUniqueId()));
        }
        
        if (identifier.equals("status")) {
            if (plugin.getSpearManager().getCurrentSpearUUID() == null) {
                return "Not Created";
            }
            if (plugin.getSpearManager().getCurrentOwner() == null) {
                return "Dropped";
            }
            return "Owned";
        }
        
        return null;
    }
}
