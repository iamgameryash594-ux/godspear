package com.godspear.commands;

import com.godspear.GodSpear;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    
    private final GodSpear plugin;
    
    public CommandManager(GodSpear plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("godspear.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                return handleGive(sender, args);
            case "reset":
                return handleReset(sender);
            case "info":
                return handleInfo(sender);
            case "tp":
                return handleTeleport(sender);
            case "reload":
                return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /godspear give <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return true;
        }
        
        if (plugin.getSpearManager().getCurrentSpearUUID() != null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("already_exists"));
            return true;
        }
        
        ItemStack spear = plugin.getSpearManager().createNewSpear();
        if (spear == null) {
            sender.sendMessage("§cFailed to create God Spear!");
            return true;
        }
        
        target.getInventory().addItem(spear);
        plugin.getSpearManager().setOwner(target.getUniqueId());
        plugin.getSpearManager().updateLocation(target.getLocation());
        
        sender.sendMessage(plugin.getConfigManager().getMessage("spear_given", "player", target.getName()));
        return true;
    }
    
    private boolean handleReset(CommandSender sender) {
        plugin.getSpearManager().resetSpear();
        sender.sendMessage(plugin.getConfigManager().getMessage("spear_reset"));
        return true;
    }
    
    private boolean handleInfo(CommandSender sender) {
        if (plugin.getSpearManager().getCurrentSpearUUID() == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_spear_exists"));
            return true;
        }
        
        String owner = plugin.getSpearManager().getOwnerName();
        int kills = plugin.getSpearManager().getTotalKills();
        String location = plugin.getSpearManager().formatLocation(plugin.getSpearManager().getLastKnownLocation());
        
        String message = plugin.getConfigManager().getMessage("spear_info")
            .replace("{owner}", owner)
            .replace("{kills}", String.valueOf(kills))
            .replace("{location}", location);
        
        sender.sendMessage(message);
        return true;
    }
    
    private boolean handleTeleport(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        if (plugin.getSpearManager().getLastKnownLocation() == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_spear_exists"));
            return true;
        }
        
        Player player = (Player) sender;
        player.teleport(plugin.getSpearManager().getLastKnownLocation());
        sender.sendMessage(plugin.getConfigManager().getMessage("spear_teleported"));
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(plugin.getConfigManager().getMessage("config_reloaded"));
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== GodSpear Commands ===");
        sender.sendMessage("§e/godspear give <player> §7- Give the God Spear");
        sender.sendMessage("§e/godspear reset §7- Reset the God Spear");
        sender.sendMessage("§e/godspear info §7- Show spear information");
        sender.sendMessage("§e/godspear tp §7- Teleport to the spear");
        sender.sendMessage("§e/godspear reload §7- Reload configuration");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("give", "reset", "info", "tp", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        
        return completions;
    }
}
