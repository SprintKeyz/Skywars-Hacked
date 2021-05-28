package com.sprintkeyz.swhacks;

import com.sprintkeyz.swhacks.events.SWHacksEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.image.renderable.RenderableImageProducer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SWHacks extends JavaPlugin {

    public File userinf;
    public File path;

    @Override
    public void onEnable() {
        // check stuff
        checkYLevel();


        // update gamerules
        getServer().getWorld("world").setGameRuleValue("doDaylightCycle", "false");
        getServer().getWorld("world").setGameRuleValue("doWeatherCycle", "false");
        getServer().getWorld("world").setGameRuleValue("doMobSpawning", "false");
        getServer().getWorld("world").setGameRuleValue("doFireTick", "false");
        getLogger().info("GameRules set!");

        // set up listeners
        Bukkit.getPluginManager().registerEvents(new SWHacksEvents(this), this);

        // register commands
        getCommand("api").setExecutor(new SWHacksEvents(this));
        getCommand("resetmap").setExecutor(new SWHacksEvents(this));
        getCommand("resetcages").setExecutor(new SWHacksEvents(this));
        getCommand("resetblocks").setExecutor(new SWHacksEvents(this));

        // end of enable
        getLogger().info("---------------------------------------------------------------------------");
        getLogger().info("Plugin enabled!");
        getLogger().info("There's a good chance that you aren't subscribed to SprintKeyz!");
        getLogger().info("He worked hard on this plugin so consider subscribing to him at youtube.com/sprintkeyz! <3");
        getLogger().info("---------------------------------------------------------------------------");
    }

    public void checkYLevel() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().getY() < -53) {
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
                                Bukkit.broadcastMessage(SWHacksEvents.nametags.get(player) + " §ewas knocked into the void by " + SWHacksEvents.nametags.get(SWHacksEvents.lastAttacker.get(player)) + "§r§e.");
                            }

                            else {
                                Bukkit.broadcastMessage(SWHacksEvents.nametags.get(player) + "§e fell into the void.");
                            }
                        }

                        else {
                            player.teleport(new Location(player.getWorld(), 0, 100, 0));
                        }
                    }
                }
            }
        }, 0L, 5L);
    }
}
