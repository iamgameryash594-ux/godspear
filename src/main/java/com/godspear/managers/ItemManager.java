package com.godspear.managers;

import com.godspear.GodSpear;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemManager {

    private final GodSpear plugin;
    private final NamespacedKey godSpearKey;

    public ItemManager(GodSpear plugin) {
        this.plugin = plugin;
        this.godSpearKey = new NamespacedKey(plugin, "godspear");
    }

    public ItemStack createGodSpear(UUID spearUUID) {
        ItemStack item = new ItemStack(Material.NETHERITE_SPEAR);
        ItemMeta meta = item.getItemMeta();

        String name = plugin.getConfigManager().getItemName().replace("&", "§");
        meta.setDisplayName(name);

        List<String> loreConfig = plugin.getConfig().getStringList("item.lore");
        List<String> lore = new ArrayList<>();
        for (String line : loreConfig) {
            lore.add(line.replace("&", "§"));
        }
        meta.setLore(lore);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES,
                         ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS,
                         ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        meta.setCustomModelData(plugin.getConfigManager().getCustomModelData());

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
            new AttributeModifier(NamespacedKey.minecraft("generic.attack_damage"),
                22.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
            new AttributeModifier(NamespacedKey.minecraft("generic.attack_speed"),
                1.6 - 4.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(godSpearKey, PersistentDataType.STRING, spearUUID.toString());

        item.setItemMeta(meta);
        return item;
    }

    public boolean isGodSpear(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_SPEAR) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(godSpearKey, PersistentDataType.STRING);
    }

    public UUID getSpearUUID(ItemStack item) {
        if (!isGodSpear(item)) return null;
        ItemMeta meta = item.getItemMeta();
        String uuidString = meta.getPersistentDataContainer().get(godSpearKey, PersistentDataType.STRING);
        try {
            return UUID.fromString(uuidString);
        } catch (Exception e) {
            return null;
        }
    }

    public void removeGodSpearData(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_SPEAR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().remove(godSpearKey);
        item.setItemMeta(meta);
    }
}
