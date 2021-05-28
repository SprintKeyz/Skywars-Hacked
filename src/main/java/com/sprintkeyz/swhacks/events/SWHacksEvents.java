package com.sprintkeyz.swhacks.events;

import com.connorlinfoot.titleapi.TitleAPI;
import com.mojang.authlib.GameProfile;
import com.sprintkeyz.swhacks.SWHacks;
import com.sprintkeyz.swhacks.inventories.CreateItem;
import com.sprintkeyz.swhacks.inventories.KitSelect;
import com.sprintkeyz.swhacks.hitcalc.Knockback;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.hypixelapi.player.HypixelPlayer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class SWHacksEvents implements Listener, CommandExecutor {

    public static boolean gameStarted = false;
    public static String KEY = "u thought i'd put my API key here LMAO";
    public static int countdown = 15;
    public static boolean noDamage = false;

    public static ArrayList<Chest> alreadyOpened = new ArrayList<>();

    public static ArrayList<Player> deadPlayers = new ArrayList<>();
    public static HashMap<Player, Integer> playerKills = new HashMap<>();
    public static ArrayList<Block> playerPlacedBlocks = new ArrayList<>();
    public static HashMap<Location, Tuple<Material, Byte>> mapBrokenBlocks = new HashMap<>();
    public static HashMap<Location, Tuple<Material, Byte>> removedBlocks = new HashMap<>();
    public static HashMap<Player, Integer> kit = new HashMap<>();
    public static HashMap<Player, Boolean> canFly = new HashMap<>();

    public static HashMap<Player, String> nametags = new HashMap<>();

    public static HashMap<Player, Player> lastAttacker = new HashMap<>();


    World world = Bukkit.getWorld("world");

    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");

    public Location spawn1 = new Location(world, 22.5, 65, -40.5);
    public Location spawn2 = new Location(world, 31.5, 65, -30.5);
    public Location spawn3 = new Location(world, 41.5, 65, -21.5);
    public Location spawn4 = new Location(world, 41.5, 65, 22.5);
    public Location spawn5 = new Location(world, 31.5, 65, 31.5);
    public Location spawn6 = new Location(world, 22.5, 65, 41.5);
    public Location spawn7 = new Location(world, -21.5, 65, 41.5);
    public Location spawn8 = new Location(world, -30.5, 65, 31.5);
    public Location spawn9 = new Location(world, -40.5, 65, 22.5);
    public Location spawn10 = new Location(world, -40.5, 65, -21.5);
    public Location spawn11 = new Location(world, -30.5, 65, -30.5);
    public Location spawn12 = new Location(world, -21.5, 65, -40.5);

    public boolean firstPlayer = false; // on first join it gens arraylist (was too lazy to use onEnable)
    public HashMap<Player, Integer> plSpawn = new HashMap<>(); // sets spawn location integers (unique and random)

    public HashMap<Player, HypixelPlayer> hyPlayers = new HashMap<>();
    public HashMap<Player, String> playerRanks = new HashMap<>();

    public static ArrayList<Player> disabledScaffold = new ArrayList<>();

    public ArrayList<Integer> ints = new ArrayList<>(); // list of raw ints for spawn

    private SWHacks swhacks;
    public SWHacksEvents(SWHacks instance) {
        swhacks = instance;
    }

    public void playerJoinSequence(Player player) throws APIException, IOException {
        noDamage = true;
        playerKills.put(player, 0);

        if (KEY.equals("defaultkey")) {
            player.sendMessage(ChatColor.RED + "You haven't set an API key for Hypixel!");
            player.sendMessage(ChatColor.RED + "To do so, go to hypixel and type '/api new'");
            player.sendMessage(ChatColor.RED + "Then, come back here and type '/api <paste api key here>'!");
            player.sendMessage(ChatColor.RED + "After that, just rejoin!");
            return;
        }

        else {
            // Hypixel API Support is very shady, as I didn't want to use a database
            // for that reason, I made it store players with hyPlayers in an arraylist
            // however, it breaks on reload which causes too many API requests
            // I tried to implement 'sustainability' (no need to reload, it resets everything on rejoin) but it's not perfect
            // I'll try to fix in the future, but for now everyone gets [HACKER] rank (except me, I get yt)
            // if you want to try it for yourself, just uncomment the below code and build it with maven! I warned you!

            /* TO TRY:
                Set 'KEY' to defaultkey (unless u wanna put it as your API key, but pls use defaultkey)
                uncomment code below and ONLY code below
                comment out this (below the code you uncomment):

                if (player.getDisplayName().equals("SprintKeyz")) {
                    changeName("§c" + player.getDisplayName(), player);
                    player.setPlayerListName("§c[§fYOUTUBE§c] " + player.getDisplayName());
                    player.setDisplayName("§c[§fYOUTUBE§c] " + player.getDisplayName() + "§f");
                    playerRanks.put(player, "yt");
                }

                else {
                    changeName("§9" + player.getDisplayName(), player);
                    player.setPlayerListName("§9[HACKER] " + player.getDisplayName());
                    player.setDisplayName("§9[HACKER] " + player.getDisplayName() + "§f");
                    playerRanks.put(player, "hacker");
                }

                done! (I think)
             */

            /*HypixelAPI api = new HypixelAPI(KEY);

            HypixelPlayer hyPlayer = null;
            if (hyPlayers.containsKey(player)) {
                hyPlayer = hyPlayers.get(player);
            }

            else {
                hyPlayer = api.getPlayer(player.getDisplayName());
                hyPlayers.put(player, hyPlayer);
            }

            playerRanks.put(player, hyPlayer.getCurrentRank());

            if (hyPlayer.getCurrentRank().equals("DEFAULT")) {
                changeName("§7" + player.getDisplayName(), player);
                player.setPlayerListName("§7 " + player.getDisplayName());
                player.setDisplayName("§7 " + player.getDisplayName());
            }

            else if (hyPlayer.getCurrentRank().equals("VIP")) {
                changeName("§a" + player.getDisplayName(), player);
                player.setPlayerListName("§a[VIP] " + player.getDisplayName());
                player.setDisplayName("§a[VIP] " + player.getDisplayName() + "§f");
            }

            else if (hyPlayer.getCurrentRank().equals("VIP_PLUS")) {
                changeName("§a" + player.getDisplayName(), player);
                player.setPlayerListName("§a[VIP§6+§a] " + player.getDisplayName());
                player.setDisplayName("§a[VIP§6+§a] " + player.getDisplayName() + "§f");
            }

            else if (hyPlayer.getCurrentRank().equals("MVP")) {
                changeName("§b" + player.getDisplayName(), player);
                player.setPlayerListName("§b[MVP] " + player.getDisplayName());
                player.setDisplayName("§b[MVP] " + player.getDisplayName() + "§f");
            }

            else if (hyPlayer.getCurrentRank().equals("MVP_PLUS")) {
                changeName("§b" + player.getDisplayName(), player);
                player.setPlayerListName("§b[MVP§c+§b] " + player.getDisplayName());
                player.setDisplayName("§b[MVP§c+§b] " + player.getDisplayName() + "§f");
            }

            else if (hyPlayer.getCurrentRank().equals("MVP_PLUS_PLUS")) {
                changeName("§6" + player.getDisplayName(), player);
                player.setPlayerListName("§6[MVP§c++§6] " + player.getDisplayName());
                player.setDisplayName("§6[MVP§c++§6] " + player.getDisplayName() + "§f");
            }
            */

            if (player.getDisplayName().equals("SprintKeyz")) {
                changeName("§c" + player.getDisplayName(), player);
                nametags.put(player, "§c" + player.getDisplayName());
                player.setPlayerListName("§c[§fYOUTUBE§c] " + player.getDisplayName());
                player.setDisplayName("§c[§fYOUTUBE§c] " + player.getDisplayName() + "§f");
                playerRanks.put(player, "yt");
            }

            else {
                changeName("§9" + player.getDisplayName(), player);
                nametags.put(player, "§9" + player.getDisplayName());
                player.setPlayerListName("§9[HACKER] " + player.getDisplayName());
                player.setDisplayName("§9[HACKER] " + player.getDisplayName() + "§f");
                playerRanks.put(player, "hacker");
            }

            if (!firstPlayer) {
                genRandom();
                firstPlayer = true;
            }
            // generate a random integer for each player (spawns). numbers 1-12.
            plSpawn.put(player, retNew());
            Bukkit.getLogger().info("" + retNew());
            ints.remove(0);

            Bukkit.broadcastMessage(player.getDisplayName() + "§e has joined (§b" + Bukkit.getOnlinePlayers().size() + "§e/§b12§e)!");

            TitleAPI.sendTitle(player, 8, 20, 8, "§eSkyWars", "§9Hacked mode");

            switch (plSpawn.get(player)) {
                case 1:
                    player.teleport(spawn1);
                    break;

                case 2:
                    player.teleport(spawn2);
                    break;

                case 3:
                    player.teleport(spawn3);
                    break;

                case 4:
                    player.teleport(spawn4);
                    break;

                case 5:
                    player.teleport(spawn5);
                    break;

                case 6:
                    player.teleport(spawn6);
                    break;

                case 7:
                    player.teleport(spawn7);
                    break;

                case 8:
                    player.teleport(spawn8);
                    break;

                case 9:
                    player.teleport(spawn9);
                    break;

                case 10:
                    player.teleport(spawn10);
                    break;

                case 11:
                    player.teleport(spawn11);
                    break;

                case 12:
                    player.teleport(spawn12);
                    break;
            }
            if (countdown < 15) {
                if (countdown > 10) {
                    player.sendMessage("§eThe game starts in §a" + countdown + " §eseconds!");
                }

                else if (countdown > 5) {
                    player.sendMessage("§eThe game starts in §6" + countdown + " §eseconds!");
                }

                else if (countdown > 1) {
                    player.sendMessage("§eThe game starts in §c" + countdown + " §eseconds!");
                }
            }
            startSequence();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws APIException, IOException {
        e.setJoinMessage(null);
        playerJoinSequence(e.getPlayer());
        Player player = e.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        disabledScaffold.clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);

        ItemStack kitselector;
        kitselector = CreateItem.createitem("§aKit Selector §7(Right Click)", Material.BOW, null, 1, true, true);

        player.getInventory().setItem(0, kitselector);
    }

    public void kitActionbarChecker(Player player) {
        if (kit.getOrDefault(player, 0) == 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 0) {
                        sendActionText(player, "§eSelected Kit: §aDefault");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 1) {
                        sendActionText(player, "§eSelected Kit: §aSpeed");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 2) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 2) {
                        sendActionText(player, "§eSelected Kit: §aJump");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 3 && kit.getOrDefault(player, 0) == 3) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted) {
                        sendActionText(player, "§eSelected Kit: §cAimbot");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 4 && kit.getOrDefault(player, 0) == 4) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted) {
                        sendActionText(player, "§eSelected Kit: §cCriticals");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 5) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 5) {
                        sendActionText(player, "§eSelected Kit: §aFly");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 6) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 6) {
                        sendActionText(player, "§eSelected Kit: §cReach");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 7) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 7) {
                        sendActionText(player, "§eSelected Kit: §aVelocity");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 8) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 8) {
                        sendActionText(player, "§eSelected Kit: §aNofall");
                    }

                    else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }

        if (kit.getOrDefault(player, 0) == 9) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameStarted && kit.getOrDefault(player, 0) == 9) {
                        sendActionText(player, "§eSelected Kit: §9Scaffold");
                    }

                    else {
                        this.cancel();
                        return;
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 10);
        }
    }

    public void sendActionText(Player player, String message){
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getItem() != null) {
            if (!e.getItem().getType().isBlock()) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (e.getItem().getItemMeta() != null) {
                        if (e.getItem().getType() == Material.BOW && !gameStarted) {
                            player.openInventory(new KitSelect().getInventory());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlocksShiftClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getItem() != null) {
            if (e.getItem().getType().isBlock()) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (gameStarted && player.isSneaking()) {
                        if (kit.getOrDefault(player, 0) == 9) {
                            // scaffold
                            if (e.getItem().getType() == Material.WOOD || e.getItem().getType() == Material.STONE) {
                                if (!disabledScaffold.contains(player)) {
                                    disabledScaffold.add(player);
                                    player.sendMessage(ChatColor.RED + "Scaffold disabled!");
                                }

                                else {
                                    disabledScaffold.remove(player);
                                    player.sendMessage(ChatColor.GREEN + "Scaffold enabled!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void startSequence() {
        if (Bukkit.getOnlinePlayers().size() >= 2) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // events during countdown
                    /*8if (countdown == 11) {
                        Bukkit.broadcastMessage("§eThe game starts in §6" + 10 + " §eseconds!");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            TitleAPI.sendTitle(player, 10, 40, 10, "§e10 seconds", "§eRight-click the bow to pick a kit!");
                        }
                    }

                    if (countdown <= 5 && countdown > 1) {
                        Bukkit.broadcastMessage("§eThe game starts in §c" + countdown + " §eseconds!");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            TitleAPI.sendTitle(player, 0, 21, 0, "§c" + countdown, "§ePrepare to fight!");
                        }
                    }

                    if (countdown == 1) {
                        Bukkit.broadcastMessage("§eThe game starts in §c" + countdown + " §esecond!");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            TitleAPI.sendTitle(player, 0, 21, 0, "§c" + countdown, "§ePrepare to fight!");
                        }
                    }

                    if (countdown >= 1) {
                        countdown--;
                    }*/

                    if (Bukkit.getOnlinePlayers().size() == 1) {
                        this.cancel();
                    }

                    if (countdown >= 1) {
                        //Bukkit.broadcastMessage((countdown - 1) + "");
                        if (countdown == 11) {
                            Bukkit.broadcastMessage("§eThe game starts in §6" + 10 + " §eseconds!");
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                TitleAPI.sendTitle(player, 10, 40, 10, "§e10 seconds", "§eRight-click the bow to pick a kit!");
                                player.playSound(player.getLocation(), Sound.CLICK, 10f, 1.31f);
                            }
                        }

                        else if (countdown <= 6 && countdown > 2) {
                            Bukkit.broadcastMessage("§eThe game starts in §c" + (countdown-1) + " §eseconds!");
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                TitleAPI.sendTitle(player, 0, 21, 0, "§c" + (countdown-1), "§ePrepare to fight!");
                                player.playSound(player.getLocation(), Sound.CLICK, 10f, 1.31f);
                            }
                        }

                        else if (countdown == 2) {
                            Bukkit.broadcastMessage("§eThe game starts in §c" + 1 + " §esecond!");
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                TitleAPI.sendTitle(player, 0, 21, 0, "§c" + 1, "§ePrepare to fight!");
                                player.playSound(player.getLocation(), Sound.CLICK, 10f, 1.31f); // FIX SOUNDS
                            }
                        }
                        countdown--;
                    }

                    else {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.getInventory().clear();
                            player.closeInventory();
                            TitleAPI.sendTitle(player, 10, 40, 10, "§9§lHACKED MODE", "");
                            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 10f, 0.95f);
                        }
                        gameStarted = true;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            removeCage(p);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                                @Override
                                public void run() {
                                    giveKits(p);
                                }
                            }, 5L);
                        }

                        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                            @Override
                            public void run() {
                                noDamage = false;
                            }
                        }, 60L);
                        this.cancel();
                    }
                }
            }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 20);
        }
    }

    public void giveKits(Player player) {
        int kitNum = kit.getOrDefault(player, 0);
        switch (kitNum) {
            case 0:
                ItemStack helmet = CreateItem.createItemEnchanted("§bOP Helmet", Material.DIAMOND_HELMET, null, 1, false, false, Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL), 2);
                ItemStack chestplate = CreateItem.createItemEnchanted("§bOP Chestplate", Material.DIAMOND_CHESTPLATE, null, 1, false, false, Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL), 2);
                ItemStack leggings = CreateItem.createItemEnchanted("§bOP Leggings", Material.DIAMOND_LEGGINGS, null, 1, false, false, Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL), 2);
                ItemStack boots = CreateItem.createItemEnchanted("§bOP Boots", Material.DIAMOND_BOOTS, null, 1, false, false, Collections.singletonList(Enchantment.PROTECTION_ENVIRONMENTAL), 2);
                player.getInventory().setHelmet(helmet);
                player.getInventory().setChestplate(chestplate);
                player.getInventory().setLeggings(leggings);
                player.getInventory().setBoots(boots);

                ItemStack pick = CreateItem.createItemEnchanted("§bDiamond Pickaxe", Material.DIAMOND_PICKAXE, null, 1, false, false, Collections.singletonList(Enchantment.DIG_SPEED), 5);
                player.getInventory().setItem(1, pick);

                ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                ItemMeta swordMeta = sword.getItemMeta();
                swordMeta.setDisplayName("§bDiamond Sword");
                swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
                sword.setItemMeta(swordMeta);
                player.getInventory().setItem(0, sword);

                ItemStack axe = CreateItem.createItemEnchanted("§bDiamond Axe", Material.DIAMOND_AXE, null, 1, false, false, Collections.singletonList(Enchantment.DIG_SPEED), 5);
                player.getInventory().setItem(2, axe);

                ItemStack shovel = CreateItem.createItemEnchanted("§bDiamond Shovel", Material.DIAMOND_SPADE, null, 1, false, false, Collections.singletonList(Enchantment.DIG_SPEED), 5);
                player.getInventory().setItem(3, shovel);

                ItemStack cobweb = CreateItem.createitem("§fCobweb", Material.WEB, null, 16, false, false);
                player.getInventory().setItem(4, cobweb);

                ItemStack water = CreateItem.createitem("§9Water Bucket", Material.WATER_BUCKET, null, 1, false, false);
                player.getInventory().setItem(5, water);

                ItemStack lava = CreateItem.createitem("§6Lava Bucket", Material.LAVA_BUCKET, null, 1, false, false);
                player.getInventory().setItem(6, lava);

                break;

            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
                break;

            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3, false, false));
                break;

            case 3:
                List<Enchantment> bowEnchants = new ArrayList<>();
                bowEnchants.add(Enchantment.ARROW_DAMAGE);
                //bowEnchants.add(Enchantment.ARROW_KNOCKBACK);

                ItemStack aimbotbow = CreateItem.createItemEnchanted("§c§lBow of Aiming", Material.BOW, null, 1, false, false, bowEnchants, 5);
                player.getInventory().setItem(0, aimbotbow);
                player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                break;

            case 5:
                //fly
                player.sendMessage(ChatColor.RED + "Your flight will be ready in 10s!");
                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                    @Override
                    public void run() {
                        player.setAllowFlight(true);
                        player.sendMessage(ChatColor.GREEN + "Enabled flight!");
                    }
                }, 200L);
                break;

            case 8:
                //nofall
                break;

            case 9:
                // scaffold
                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.YELLOW + "To enable or disable scaffold, shift + right click with blocks!");
                        checkScaffold();
                        scaffoldBridge();
                    }
                }, 60L);
                break;
        }
    }

    public void checkScaffold() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (kit.getOrDefault(player, 0) == 9) {
                        // scaffold
                        if (!player.getInventory().contains(Material.WOOD, 64)) {
                            player.getInventory().addItem(new ItemStack(Material.WOOD, 64));
                        }
                    }
                }
            }
        }, 0L, 80L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            Player player = e.getPlayer();
            if (kit.getOrDefault(player, 0) == 6) {
                Entity lookingAtEntity = getNearestEntityInSight(player, 4); // 4 blocks of reach
                if (lookingAtEntity != null && lookingAtEntity instanceof LivingEntity) {
                    // is hit
                    hitSequence((LivingEntity) lookingAtEntity, player, 0);
                }
            }
        }
    }

    public void hitSequence(LivingEntity kbEntity, Player player, double resistance) {
        Knockback.applyKnockback(kbEntity, player, resistance);
        // check damage
        if (kbEntity.getHealth() > kbEntity.getHealth() - 5) {
            kbEntity.setHealth(kbEntity.getHealth() - 2);
            world.playSound(kbEntity.getLocation(), Sound.HURT_FLESH, 10, 1);
            world.playSound(player.getLocation(), Sound.HURT_FLESH, 10, 1);
        }

        else {
            kbEntity.setHealth(0);
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (kit.getOrDefault(player, 0) == 7) {
                Player damager = (Player) e.getDamager();
                player.setVelocity(player.getVelocity().setX(0).setY(0).setZ(0).normalize());
            }

            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                if (!lastAttacker.containsKey(player)) {

                    lastAttacker.put(player, damager);

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                        @Override
                        public void run() {
                            lastAttacker.remove(player);
                        }
                    }, 600L);
                }

                else {
                    lastAttacker.remove(player);
                    lastAttacker.put(player, damager);
                }
            }
        }
    }



    public static Entity getNearestEntityInSight(Player player, int range) {
        ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight( (Set<Material>) null, range);
        ArrayList<Location> sight = new ArrayList<>();
        for (int i = 0;i<sightBlock.size();i++)
            sight.add(sightBlock.get(i).getLocation());
        for (int i = 0;i<sight.size();i++) {
            for (int k = 0;k<entities.size();k++) {
                if (Math.abs(entities.get(k).getLocation().getX()-sight.get(i).getX())<1.3) {
                    if (Math.abs(entities.get(k).getLocation().getY()-sight.get(i).getY())<1.5) {
                        if (Math.abs(entities.get(k).getLocation().getZ()-sight.get(i).getZ())<1.3) {
                            return entities.get(k);
                        }
                    }
                }
            }
        }
        return null; //Return null/nothing if no entity was found
    }

    public void scaffoldBridge() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (kit.getOrDefault(player, 0) == 9) {
                        if (!disabledScaffold.contains(player)) {
                            // scaffold
                            Block blockUnderPlayer = player.getLocation().subtract(0, 1, 0).getBlock();
                            if (blockUnderPlayer.getType() == Material.AIR) {
                                boolean blocks = false;
                                List<Block> nearbyCheck = getNearbyBlocks(player.getLocation(), 2);
                                for (Block block : nearbyCheck) {
                                    if (block.getType() != Material.AIR && !block.isLiquid()) {
                                        blocks = true;
                                        break;
                                    }
                                }

                                if (blocks) {
                                    if (player.getInventory().contains(Material.WOOD)) {
                                        blockUnderPlayer.setType(Material.WOOD);
                                        playerPlacedBlocks.add(blockUnderPlayer);
                                    } else {
                                        player.sendMessage("§cImpossible! You ran out of wood!");
                                    }
                                    blocks=false;
                                }
                            }
                        }
                    }
                }
            }
        }, 0L, 1L);
    }

    int arrowTask;

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile arrow = e.getEntity();
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            if (kit.getOrDefault(player, 0) == 3) {
                arrowTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                    @Override
                    public void run() {
                        outerloop:
                        for (double i = 0.5; i < 12; i += 0.5) { //This will rapidly increase the range so you don't get so many mobs at one, and instead checks a few blocks at a time
                            for (Entity e : arrow.getNearbyEntities(i, 3, i)) { //Gets ALL nearby entities using the loop variable above it
                                if (e instanceof Player && e != player) { //Checks to make sure the entities isn't the shooter
                                    if (e.getType().isAlive()) { //Checks to make sure the entity is alive
                                        Location from = arrow.getLocation();
                                        Location to = e.getLocation(); //Gets the entities Location
                                        org.bukkit.util.Vector vFrom = from.toVector(); //Converts the from location to a vector
                                        org.bukkit.util.Vector vTo = to.toVector(); //Converts the to location to a vector
                                        Vector direction = vTo.subtract(vFrom).normalize(); //Subtracts the to variable to the from variable and normalizes it.
                                        arrow.setVelocity(direction); //Sets the arrows newfound direction
                                        break outerloop;
                                    }
                                }
                            }
                        }
                    }
                }, 0L, 1L);
            }
        }
    }

    @EventHandler
    public void onEntityAttackEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();

            if (deadPlayers.contains(damager)) {
                e.setCancelled(true);
            }

            else {
                if (kit.getOrDefault(damager, 0) == 4) {
                    // criticals
                    e.setDamage(e.getDamage() + (e.getDamage() / 3));
                }

                if (kit.getOrDefault(damager, 0) == 5) {
                    // flight
                    if (damager.isFlying()) {
                        e.setCancelled(true);
                        sendActionText(damager, ChatColor.RED + "You can't attack while flying!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            if (kit.getOrDefault(player, 0) == 3) {
                Bukkit.getScheduler().cancelTask(arrowTask);
            }
        }
    }

    @EventHandler
    public void onprojectileDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player damager = (Player) arrow.getShooter();
                    if (kit.getOrDefault(damager, 0) == 3) {
                        damager.playSound(damager.getLocation(), Sound.ORB_PICKUP, 10, 1);
                    }
                }
            }
        }
    }

    public static void changeName(String name, Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle", (Class<?>[]) null);
            Object entityPlayer = getHandle.invoke(player);
            Class<?> entityHuman = entityPlayer.getClass().getSuperclass();
            Field bH = entityHuman.getDeclaredField("bH");
            bH.setAccessible(true);
            bH.set(entityPlayer, new GameProfile(player.getUniqueId(), name));
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.hidePlayer(player);
                players.showPlayer(player);
            }
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType() == Material.CHEST) {
                    if (!alreadyOpened.contains(e.getClickedBlock().getState())) {
                        // is a chest
                        Chest chest = (Chest) e.getClickedBlock().getState();
                        chest.getBlockInventory().clear();

                        Potion regen1 = new Potion(PotionType.REGEN, 1);
                        regen1.setSplash(true);
                        ItemStack regen1pot = new ItemStack(regen1.toItemStack(1));
                        ItemMeta itemMeta = regen1pot.getItemMeta();
                        itemMeta.setLore(Collections.singletonList("§7Regeneration (0:33)"));
                        regen1pot.setItemMeta(itemMeta);

                        Potion poison1 = new Potion(PotionType.POISON, 1);
                        poison1.setSplash(true);
                        ItemStack poison1pot = new ItemStack(poison1.toItemStack(1));
                        ItemMeta poisonMeta = poison1pot.getItemMeta();
                        poisonMeta.setLore(Collections.singletonList("§7poison (0:16)"));
                        poison1pot.setItemMeta(poisonMeta);

                        ItemStack[] onePerChest = {new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_BOOTS)};
                        for (ItemStack i : onePerChest) {
                            ItemMeta im = i.getItemMeta();
                            im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
                            i.setItemMeta(im);
                        }
                        ItemStack[] swords_bows = {CreateItem.createItemEnchanted("§bStone Sword", Material.STONE_SWORD, null, 1, false, false, Collections.singletonList(Enchantment.DAMAGE_ALL), 1), new ItemStack(Material.DIAMOND_SWORD), CreateItem.createItemEnchanted("§bDiamond Sword", Material.DIAMOND_SWORD, null, 1, false, false, Collections.singletonList(Enchantment.DAMAGE_ALL), 1), CreateItem.createItemEnchanted("§bBow", Material.BOW, null, 1, false, false, Collections.singletonList(Enchantment.ARROW_DAMAGE), 1), CreateItem.createItemEnchanted("§bBow", Material.BOW, null, 1, false, false, Collections.singletonList(Enchantment.ARROW_DAMAGE), 3), new ItemStack(Material.ARROW, 15), new ItemStack(Material.ARROW, 20)};
                        ItemStack[] misc = {regen1pot, new Potion(PotionType.REGEN, 2, true).toItemStack(1), poison1pot, new Potion(PotionType.SPEED, 2, true).toItemStack(1), new ItemStack(Material.EGG, 16), new ItemStack(Material.SNOW_BALL, 16), new Potion(PotionType.FIRE_RESISTANCE, 1).toItemStack(1), new ItemStack(Material.FISHING_ROD), new ItemStack(Material.COOKED_BEEF, 16), new ItemStack(Material.EXP_BOTTLE, 32), new ItemStack(Material.EXP_BOTTLE, 64), new ItemStack(Material.DIAMOND_AXE), new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET)};

                        Collections.shuffle(Arrays.asList(onePerChest));
                        Collections.shuffle(Arrays.asList(swords_bows));
                        Collections.shuffle(Arrays.asList(misc));
                        Random random = new Random();
                        int[] slotsrand = new Random().ints(0, 26).distinct().limit(9).toArray();
                        ArrayList<ItemStack> finalitems = new ArrayList<>();
                        Collections.shuffle(Arrays.asList(onePerChest));
                        int onePerChestRand = random.nextInt(8);
                        finalitems.add(onePerChest[onePerChestRand]);
                        for (int i=0; i<5; i++) {
                            finalitems.add(swords_bows[i]);
                        }
                        Collections.shuffle(Arrays.asList(swords_bows));

                        for (int i=0; i<5; i++) {
                            finalitems.add(misc[i]);
                        }
                        Collections.shuffle(Arrays.asList(misc));

                        int iNum = 0;

                        Collections.shuffle(finalitems);

                        for (Integer i : slotsrand) {
                            if (iNum <= finalitems.size()) {
                                chest.getBlockInventory().setItem(i, finalitems.get(iNum));
                                iNum++;
                            }

                            else {
                                iNum = 0;
                                break;
                            }
                        }

                        chest.getBlockInventory().addItem(new ItemStack(Material.WOOD, 16));

                        alreadyOpened.add(chest);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        if (!gameStarted) {
            e.setCancelled(true);
        }
    }



    public void removeCage(Player player) {
        for (Block block : getNearbyBlocks(player.getLocation(), 3)) {
            if (block.getType() != Material.SMOOTH_BRICK ||
                    block.getType() != Material.SMOOTH_STAIRS ||
                    block.getType() != Material.IRON_BARDING ||
                    block.getType() != Material.STONE_SLAB2 ||
                    block.getType() != Material.DIODE ||
                    block.getType() != Material.LEAVES ||
                    block.getType() != Material.PRISMARINE) {
                removedBlocks.put(block.getLocation(), new Tuple(block.getType(), block.getData()));
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void commandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().contains("reload") || e.getMessage().contains("rl")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "resetmap");
        }
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public void genRandom() {
        for (int i=1; i<13; i++) {
            ints.add(i);
        }
        Collections.shuffle(ints); // shuffle raw spawn int list (1, 2, 3, 4 --> 4, 2, 1, 3...)
    }

    public int retNew() {
        return ints.get(0); // returns val 0 of ints (later removed so a new int can be picked)
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("api")) {
            if (args.length == 1) {
                KEY = args[0];
                sender.sendMessage(ChatColor.GREEN + "API Key set successfully!");
            }
        }

        if (cmd.getName().equalsIgnoreCase("resetmap")) {
            for (Block block : playerPlacedBlocks) {
                block.setType(Material.AIR);
            }
            playerPlacedBlocks.clear();

            for (Location loc : mapBrokenBlocks.keySet()) {
                world.getBlockAt(loc).setType(mapBrokenBlocks.get(loc).a());
                world.getBlockAt(loc).setData(mapBrokenBlocks.get(loc).b());
            }
            mapBrokenBlocks.clear();

            for (Location loc : removedBlocks.keySet()) {
                world.getBlockAt(loc).setType(removedBlocks.get(loc).a());
                world.getBlockAt(loc).setData(removedBlocks.get(loc).b());
            }
            removedBlocks.clear();
            sender.sendMessage(ChatColor.GREEN + "Successfully reset map!");
        }

        if (cmd.getName().equalsIgnoreCase("resetcages")) {
            for (Location loc : removedBlocks.keySet()) {
                world.getBlockAt(loc).setType(removedBlocks.get(loc).a());
                world.getBlockAt(loc).setData(removedBlocks.get(loc).b());
            }
            removedBlocks.clear();
            sender.sendMessage(ChatColor.GREEN + "Successfully reset cages!");
        }

        if (cmd.getName().equalsIgnoreCase("resetblocks")) {
            for (Block block : playerPlacedBlocks) {
                block.setType(Material.AIR);
            }
            playerPlacedBlocks.clear();

            for (Location loc : mapBrokenBlocks.keySet()) {
                world.getBlockAt(loc).setType(mapBrokenBlocks.get(loc).a());
                world.getBlockAt(loc).setData(mapBrokenBlocks.get(loc).b());
            }
            mapBrokenBlocks.clear();

            sender.sendMessage(ChatColor.GREEN + "Successfully reset all player-placed/broken blocks!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (playerRanks.containsKey(e.getPlayer())) {
            e.setFormat("§4[1✷] §r" + e.getPlayer().getDisplayName() + ": " + e.getMessage());
        }
    }

    @EventHandler
    public void setScoreboardOnJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameStarted == false) {
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard board = manager.getNewScoreboard();
                    Objective objective = board.registerNewObjective("test", "dummy");
                    objective.setDisplayName("§e§lSKYWARS");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score serverIP = objective.getScore("§ewww.hypixel.net");
                    serverIP.setScore(1);

                    Score space = objective.getScore(" ");
                    space.setScore(2);

                    Score mode = objective.getScore("§fMode: §9Hacked");
                    mode.setScore(3);

                    Score map = objective.getScore("§fMap: §aAquarius");
                    map.setScore(4);

                    Score space2 = objective.getScore("  ");
                    space2.setScore(5);

                    Score startCountdown = null;
                    if (Bukkit.getOnlinePlayers().size() < 2) {
                        startCountdown = objective.getScore("§fWaiting...");
                    } else {
                        startCountdown = objective.getScore("§fStarting in §a" + countdown + "s");
                    }
                    startCountdown.setScore(6);

                    Score space3 = objective.getScore("   ");
                    space3.setScore(7);

                    Score playerCount = objective.getScore("§fPlayers: §a" + Bukkit.getOnlinePlayers().size() + "/12");
                    playerCount.setScore(8);

                    Score space4 = objective.getScore("    ");
                    space4.setScore(9);

                    Date currentDate = new Date();

                    Score serverInfo = objective.getScore("§7" + format.format(currentDate) + "§8" + "  m820N");
                    serverInfo.setScore(10);

                    player.setScoreboard(board);
                }

                else {
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard gameboard = manager.getNewScoreboard();
                    Objective objective = gameboard.registerNewObjective("test", "dummy");
                    objective.setDisplayName("§e§lSKYWARS");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score serverIP = objective.getScore("§ewww.hypixel.net");
                    serverIP.setScore(1);

                    Score space = objective.getScore(" ");
                    space.setScore(2);

                    Score space2 = objective.getScore("  ");
                    space2.setScore(5);

                    Score space3 = objective.getScore("   ");
                    space3.setScore(7);

                    Score space4 = objective.getScore("    ");
                    space4.setScore(9);

                    Score space5 = objective.getScore("     ");
                    space5.setScore(12);

                    Date currentDate = new Date();

                    Score serverInfo = objective.getScore("§7" + format.format(currentDate) + "§8" + "  m820N");
                    serverInfo.setScore(13);

                    Score nEventText = objective.getScore("§fNext Event:");
                    nEventText.setScore(11);

                    Score nextEvent = objective.getScore("§aRefill 2:58");
                    nextEvent.setScore(10);

                    int remaining = Bukkit.getOnlinePlayers().size() - deadPlayers.size();

                    Score playersLeft = objective.getScore("§fPlayers left: §a" + remaining);
                    playersLeft.setScore(8);

                    Score kills = objective.getScore("§fKills: §a" + playerKills.get(player));
                    kills.setScore(6);

                    Score mode = objective.getScore("§fMode: §9Hacked");
                    mode.setScore(3);

                    Score map = objective.getScore("§fMap: §aAquarius");
                    map.setScore(4);

                    player.setScoreboard(gameboard);
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), 0, 3);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        deadPlayers.add(player);
        if (e.getEntity().getKiller() instanceof Player) {
            Player killer = e.getEntity().getKiller();
            playerKills.put(killer, playerKills.get(killer) + 1);
        }

        if (Bukkit.getOnlinePlayers().size() - deadPlayers.size() <= 1) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendMessage("Game over!");
                gameStarted = false;
                countdown = 15;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Bukkit.broadcastMessage(e.getPlayer().getDisplayName() + "§e has left (§b" + Bukkit.getOnlinePlayers().size() + "§e/§b12§e)!");

        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() <= 2) {
                    gameStarted = false;
                    countdown = 15;
                }

                if (Bukkit.getOnlinePlayers().size() == 0) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "resetmap");
                }
            }
        }, 5L);
    }

    @EventHandler
    public void onLoseHunger(FoodLevelChangeEvent e) {
        if (!gameStarted) {
            e.setCancelled(true);
        }

        if (deadPlayers.contains((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (noDamage) {
            e.setCancelled(true);
        }

        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getDamage() >= player.getHealth()) {
                e.setCancelled(true);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    if (lastAttacker.containsKey(player)) {
                        Player damager = lastAttacker.get(player);
                        Bukkit.broadcastMessage(nametags.get(player) + " §r§ewas killed by " + nametags.get(damager) + "§r§e.");
                        killSequence(player);
                    }
                }

                else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (lastAttacker.containsKey(player)) {
                        Player damager = lastAttacker.get(player);
                        Bukkit.broadcastMessage(nametags.get(player) + " §r§ewas knocked off a cliff by " + nametags.get(damager) + "§r§e.");
                        killSequence(player);
                    }

                    else {
                        Bukkit.broadcastMessage(nametags.get(player) + " §r§efell to their death!");
                        killSequence(player);
                    }
                }
            }
        }
    }

    public void killSequence(Player player) {
        if (!SWHacksEvents.deadPlayers.contains(player)) {
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0);
            player.setFireTicks(0);
            SWHacksEvents.deadPlayers.add(player);
            player.teleport(new Location(player.getWorld(), 0, 100, 0));
            for (PotionEffect e : player.getActivePotionEffects()) {
                player.removePotionEffect(e.getType());
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            for (Player pl1 : Bukkit.getOnlinePlayers()) {
                if (pl1 != player) {
                    pl1.hidePlayer(player);
                }
            }

            if (SWHacksEvents.lastAttacker.containsKey(player)) {
                SWHacksEvents.playerKills.put(SWHacksEvents.lastAttacker.get(player), SWHacksEvents.playerKills.getOrDefault(SWHacksEvents.lastAttacker.get(player), 0) + 1);
            }
        }

        else {
            player.teleport(new Location(player.getWorld(), 0, 100, 0));
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        playerPlacedBlocks.add(e.getBlock());
    }

    @EventHandler
    public void onPlayerRemoveBlock(BlockBreakEvent e) {
        if (playerPlacedBlocks.contains(e.getBlock())) {
            playerPlacedBlocks.remove(e.getBlock());
        }

        else {
            // is part of map
            mapBrokenBlocks.put(e.getBlock().getLocation(), new Tuple(e.getBlock().getType(), e.getBlock().getData()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) { return; }
        if (e.getClickedInventory().getHolder() instanceof KitSelect) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();

            if (e.getCurrentItem() == null) {
                return;
            }

            if (e.getCurrentItem().getType() == Material.DIAMOND_PICKAXE) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Default kit!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 0);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.BARRIER) {
                player.closeInventory();
            }

            if (e.getCurrentItem().getType() == Material.POTION) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Speed Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 1);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.RABBIT_FOOT) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Jump Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 2);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.BOW) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Aimbot Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 3);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.IRON_SWORD) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Criticals Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 4);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.FEATHER) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Fly Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 5);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Reach Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 6);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.WEB) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Velocity Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 7);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.WATER_BUCKET) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Nofall Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 8);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.WOOD) {
                player.sendMessage(ChatColor.YELLOW + "You've selected the Scaffold Hack!");
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                kit.put(player, 9);
                kitActionbarChecker(player);
            }

            if (e.getCurrentItem().getType() == Material.COMMAND) {
                Random random = new Random();
                int kitRand = random.nextInt(10);

                switch (kitRand) {
                    case 0:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Default kit!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 0);
                        kitActionbarChecker(player);
                        return;

                    case 1:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Speed Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 1);
                        kitActionbarChecker(player);
                        break;

                    case 2:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Jump Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 2);
                        kitActionbarChecker(player);
                        break;

                    case 3:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Aimbot Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 3);
                        kitActionbarChecker(player);
                        break;

                    case 4:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Criticals Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 4);
                        kitActionbarChecker(player);
                        break;

                    case 5:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Fly Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 5);
                        kitActionbarChecker(player);
                        break;

                    case 6:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Reach Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 6);
                        kitActionbarChecker(player);
                        break;

                    case 7:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Velocity Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 7);
                        kitActionbarChecker(player);
                        break;

                    case 8:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Nofall Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 8);
                        kitActionbarChecker(player);
                        break;

                    case 9:
                        player.sendMessage(ChatColor.YELLOW + "You've selected the Scaffold Hack!");
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10f, 0.89f);
                        kit.put(player, 9);
                        kitActionbarChecker(player);
                        break;
                }
            }
        }
    }

    public static ArrayList<Player> checkedFlight = new ArrayList<>();

    @EventHandler
    public void onPlayerFly(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.isFlying()) {
            if (!checkedFlight.contains(player)) {
                if (canFly.getOrDefault(player, true)) {
                    if (kit.getOrDefault(player, 0) == 5) {
                        checkedFlight.add(player);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                            @Override
                            public void run() {
                                canFly.put(player, false);
                                player.setAllowFlight(false);
                                flyDelay(player);
                            }
                        }, 400L);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
                            @Override
                            public void run() {
                                player.sendMessage(ChatColor.RED + "You have 10s left of flight!");
                            }
                        }, 300L);
                    }
                } else {
                    player.setAllowFlight(false);
                }
            }
        }
    }

    public void flyDelay(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Your flight re-enables in 30s!");
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SWHacksEvents.class), new Runnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.GREEN + "Flight re-enabled!");
                canFly.put(player, true);
                player.setAllowFlight(true);
                checkedFlight.remove(player);
            }
        }, 600L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (kit.getOrDefault(p, 0) == 8) {
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
