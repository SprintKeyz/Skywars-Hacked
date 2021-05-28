package com.sprintkeyz.swhacks.inventories;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Settings implements InventoryHolder {
    private Inventory inv;

    //close inventory on velocity...
    // need more setting ideas to create, so for now I leave this class here.

    public Settings() {
        inv = Bukkit.createInventory(this,9, "Settings");
    }

    private void init() {

    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
