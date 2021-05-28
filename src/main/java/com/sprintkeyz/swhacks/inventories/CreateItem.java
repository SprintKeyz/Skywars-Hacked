package com.sprintkeyz.swhacks.inventories;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;

public class CreateItem implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return null;
    }

    public static ItemStack createitem(String name, Material mat, List<String> lore, int amt, Boolean hideattribs, Boolean unbreakable) {

        ItemStack item = new ItemStack(mat, amt);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        if (hideattribs == true) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        }

        if (unbreakable == true) {
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemEnchanted(String name, Material mat, List<String> lore, int amt, Boolean hideattribs, Boolean unbreakable, List<Enchantment> enchant, int EnchantLevel) {

        ItemStack item = new ItemStack(mat, amt);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        if (hideattribs == true) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        }

        if (unbreakable == true) {
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        Iterator it = enchant.iterator();
        while (it.hasNext()) {
            Enchantment enchantment = (Enchantment) it.next();
            meta.addEnchant(enchantment, EnchantLevel, true);
            item.setItemMeta(meta);
            return item;
        }

        return item;
    }
}
