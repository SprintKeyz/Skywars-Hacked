package com.sprintkeyz.swhacks.inventories;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitSelect implements InventoryHolder {
    private Inventory inv;
    // kits: speed, jump boost, aimbot bows, criticals, fly, reach, velocity, nofall, scaffold

    ItemStack defaultkit;
    ItemStack close;
    ItemStack randomkit;
    ItemStack settings;

    ItemStack speed;
    ItemStack jump;
    ItemStack aimbot;
    ItemStack criticals;
    ItemStack fly;
    ItemStack reach;
    ItemStack velocity;
    ItemStack nofall;
    ItemStack scaffold;

    public KitSelect() {
        inv = Bukkit.createInventory(this, 54, "Kit Selector");
        init();
    }

    // movement=green, pvp=red, bridging=blue

    private void init() {
        List<String> defaultlore = new ArrayList<>();
        defaultlore.add("§7Diamond Pickaxe (Efficiency V)");
        defaultlore.add("§7Diamond Axe (Efficiency V)");
        defaultlore.add("§7Diamond Shovel (Efficiency V)");
        defaultlore.add("§7Diamond Sword (Sharpness I, Fire Aspect I)");
        defaultlore.add("§7Diamond Armor (Prot I)");
        defaultlore.add("§7Cobweb x16");
        defaultlore.add("§7Lava Bucket");
        defaultlore.add("§7Water Bucket");
        defaultlore.add(" ");
        defaultlore.add("§eClick to select!");

        List<String> randlore = new ArrayList<>();
        randlore.add("§7Chooses a random kit that you");
        randlore.add("§7own!");
        randlore.add("");
        randlore.add("§eClick to select!");
        defaultkit = CreateItem.createItemEnchanted("§aDefault", Material.DIAMOND_PICKAXE, defaultlore, 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        speed = CreateItem.createItemEnchanted("§aSpeed", Material.POTION, Collections.singletonList("§7Walk faster! (Movement)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        jump = CreateItem.createItemEnchanted("§aJump", Material.RABBIT_FOOT, Collections.singletonList("§7Jump higher! (Movement)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        aimbot = CreateItem.createItemEnchanted("§cAimbot", Material.BOW, Collections.singletonList("§7Arrows curve to target! (PVP)"), 1, true, true, Collections.singletonList(Enchantment.ARROW_DAMAGE), 0);
        criticals = CreateItem.createItemEnchanted("§cCriticals", Material.IRON_SWORD, Collections.singletonList("§7Do more damage! (PVP)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        fly = CreateItem.createItemEnchanted("§aFly", Material.FEATHER, Collections.singletonList("§7Fly (Has a Cooldown + Can't attack and fly)! (Movement)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        reach = CreateItem.createItemEnchanted("§cReach", Material.DIAMOND_SWORD, Collections.singletonList("§7Hit from farther away (1 block)! (PVP)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        velocity = CreateItem.createItemEnchanted("§aAntiKB", Material.WEB, Collections.singletonList("§7Take no Knockback! (Movement)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        nofall = CreateItem.createItemEnchanted("§aNofall", Material.WATER_BUCKET, Collections.singletonList("§7Can't take fall damage! (Movement)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        scaffold = CreateItem.createItemEnchanted("§9Scaffold", Material.WOOD, Collections.singletonList("§7Never fail a bridge + infinite blocks! (Bridging)"), 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);
        randomkit = CreateItem.createItemEnchanted("§eRandom Kit", Material.COMMAND, randlore, 1, true, true, Collections.singletonList(Enchantment.LUCK), 0);

        close = CreateItem.createitem("§cClose", Material.BARRIER, null, 1, true, true);

        settings = CreateItem.createitem("§9Settings", Material.REDSTONE_TORCH_ON, Collections.singletonList("§7Some QOL Settings (WIP)"), 1, true, true);

        //redstainedglass = CreateItem.createitem("§cKit doesn't exist!", Material.STAINED_GLASS_PANE, defaultlore, 1, true, true);
        //darkgraystainedglass = CreateItem.createitem(null, Material.BARRIER, defaultlore, 1, true, true);

        ItemStack redStainedGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        ItemStack grayStainedGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);

        redStainedGlass.getItemMeta().setDisplayName("§cKit not released!");
        grayStainedGlass.getItemMeta().setDisplayName(null);

        for (Player player : Bukkit.getOnlinePlayers()) {
            // player unlockable kits go here
        }

        inv.setItem(0, defaultkit);
        inv.setItem(1, speed);
        inv.setItem(2, jump);
        inv.setItem(3, aimbot);
        inv.setItem(4, criticals);
        inv.setItem(5, fly);
        inv.setItem(6, reach);
        inv.setItem(7, velocity);
        inv.setItem(8, nofall);
        inv.setItem(9, scaffold);

        for (int i=10; i<45; i++) {
            inv.setItem(i, redStainedGlass);
        }

        for (int i=45; i<49; i++) {
            inv.setItem(i, grayStainedGlass);
        }

        inv.setItem(49, close);
        inv.setItem(50, randomkit);

        for (int i=51; i<53; i++) {
            inv.setItem(i, grayStainedGlass);
        }

        inv.setItem(53, settings);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
